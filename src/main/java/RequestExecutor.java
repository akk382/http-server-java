import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPOutputStream;

public class RequestExecutor implements Runnable {

    private Socket client;
    private File directory;
    private HTTPRequest httpRequest;

    private RequestExecutor() {
    }

    public RequestExecutor(Socket clientSocket) {
        this.client = clientSocket;
    }

    public RequestExecutor(Socket clientSocket, File directory) {
        this.client = clientSocket;
        this.directory = directory;
    }

    @Override
    public void run() {
        try {

            InputStream inputStream = client.getInputStream();
            httpRequest = RequestParser.parse(inputStream);

            boolean responded = handleBaseUri() || handleEcho();
//            || handleUserAgent() || handleFiles();
            handleNotFound(responded);

            inputStream.close();
            client.close();
        } catch (IOException ex) {
            System.out.println("Error occurred: \n" + ex.getMessage());
        }
    }

    private void handleNotFound(boolean responded) throws IOException {
        if (!responded) {
            HTTPOutputStream outputStream = new HTTPOutputStream(client.getOutputStream());
            HTTPResponse response = HTTPResponseBuilder.builder()
                    .setVersion(HTTPVersion.HTTP1_1)
                    .setStatusCode(HTTPStatusCode.NOT_FOUND)
                    .build();
            outputStream.write(response);
        }
    }

    private boolean handleUserAgent() throws IOException {
        if (!httpRequest.getUri().startsWith("/user-agent")) {
            return false;
        }
        String userAgentHeader = httpRequest.getRequestHeader(RequestHeader.USER_AGENT);
        String userAgent = userAgentHeader.substring("User-Agent: ".length());
        Encoding acceptedEncoding = Encoding.fromString(
                httpRequest.getRequestHeader(RequestHeader.ACCEPT_ENCODING));
        byte[] encodedResponseBody = encodeResponseBody(userAgent, acceptedEncoding);
        String response = "HTTP/1.1 200 OK" + "\r\n";
        if (acceptedEncoding != null) {
            response += HeaderPrefix.getHeaderPrefix(ResponseHeader.CONTENT_ENCODING) + acceptedEncoding + HTTPConstants.LINE_SEPARATOR;
        }
        response += "Content-Type: text/plain\r\nContent-Length: %d\r\n\r\n";
        response = String.format(response, encodedResponseBody.length);
        OutputStream outputStream = client.getOutputStream();
        outputStream.write(response.getBytes());
        outputStream.write(encodedResponseBody);
        outputStream.close();
        return true;
    }

    private boolean handleEcho() throws IOException {
        if (httpRequest.getUri().startsWith(SupportedURIs.ECHO.value())) {
            String restOfURI = httpRequest.getUri().substring(SupportedURIs.ECHO.value().length());
            Encoding acceptedEncoding = Encoding.fromString(httpRequest.getRequestHeader(RequestHeader.ACCEPT_ENCODING));
            byte[] encodedResponseBody = encodeResponseBody(restOfURI, acceptedEncoding);
            HTTPResponseBuilder builder = HTTPResponseBuilder.builder()
                    .setVersion(HTTPVersion.HTTP1_1)
                    .setStatusCode(HTTPStatusCode.OK)
                    .addResponseHeader(ResponseHeader.CONTENT_TYPE, ContentType.TEXT_PLAIN.value())
                    .addResponseHeader(ResponseHeader.CONTENT_LENGTH, String.valueOf(encodedResponseBody.length))
                    .setResponseBody(encodedResponseBody);
            if (acceptedEncoding != null) {
                builder.addResponseHeader(ResponseHeader.CONTENT_ENCODING, acceptedEncoding.value());
            }
            HTTPResponse response = builder.build();
            HTTPOutputStream outputStream = new HTTPOutputStream(client.getOutputStream());
            outputStream.write(response);
            return true;
        }
        return false;
    }

    private boolean handleBaseUri() throws IOException {
        if (httpRequest.getUri().isEmpty() || httpRequest.getUri().equals(SupportedURIs.BASE_URL.value())) {
            HTTPResponse response = HTTPResponseBuilder.builder()
                    .setVersion(HTTPVersion.HTTP1_1)
                    .setStatusCode(HTTPStatusCode.OK)
                    .build();
            HTTPOutputStream outputStream = new HTTPOutputStream(client.getOutputStream());
            outputStream.write(response);
            return true;
        }
        return false;
    }

    private void handleIfDirectoryNotFound(File directory, OutputStream outputStream) throws IOException {
        if (!directory.exists()) {
            outputStream.write(("HTTP/1.1 404 Not Found" + HTTPConstants.LINE_SEPARATOR + HTTPConstants.LINE_SEPARATOR).getBytes());
            outputStream.close();
            throw new IOException("Directory not found.\n");
        }
    }

    private void handleIfFileNotFound(File file, OutputStream outputStream) throws IOException {
        if (!file.exists()) {
            outputStream.write(("HTTP/1.1 404 Not Found" + HTTPConstants.LINE_SEPARATOR + HTTPConstants.LINE_SEPARATOR).getBytes());
            outputStream.close();
            throw new IOException("File not found.\n");
        }
    }

    private boolean handleFiles() throws IOException, ExecutionException, InterruptedException {
        if (httpRequest.getUri().startsWith("/files/")) {
            OutputStream outputStream = client.getOutputStream();
            handleIfDirectoryNotFound(directory, outputStream);
            String restOfUri = httpRequest.getUri().substring("/files/".length());
            File file = new File(directory.getPath() + File.separator + restOfUri);

            switch (httpRequest.getMethod()) {
                case GET -> {
                    handleIfFileNotFound(file, outputStream);
                    InputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));
                    byte[] bytes = fileInputStream.readAllBytes();
                    Encoding acceptedEncoding = Encoding.fromString(httpRequest.getRequestHeader(RequestHeader.ACCEPT_ENCODING));
                    byte[] encodedResponseBody = encodeResponseBody(bytes, acceptedEncoding);
                    String response = "HTTP/1.1 200 OK" + HTTPConstants.LINE_SEPARATOR;
                    if (acceptedEncoding != null) {
                        response += "Content-Encoding: " + acceptedEncoding + HTTPConstants.LINE_SEPARATOR;
                    }
                    response += "Content-Type: application/octet-stream" + HTTPConstants.LINE_SEPARATOR + "Content-Length: %d" + HTTPConstants.LINE_SEPARATOR + HTTPConstants.LINE_SEPARATOR;
                    response = String.format(response, encodedResponseBody.length);
                    outputStream.write(response.getBytes());
                    outputStream.write(encodedResponseBody);
                    fileInputStream.close();
                    outputStream.close();
                    return true;
                }
                case POST -> {
                    handleIfFileAlreadyExists(file, outputStream);
                    if (!file.createNewFile()) {
                        throw new IOException("Failed to create a new file.\n");
                    }
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    writer.write(httpRequest.getRequestBody().getBody());
                    writer.close();
                    outputStream.write(("HTTP/1.1 201 Created" + HTTPConstants.LINE_SEPARATOR + HTTPConstants.LINE_SEPARATOR).getBytes());
                    outputStream.close();
                    return true;
                }
                default -> handleNotFound(false);
            }
            throw new IOException("Failed request handling in handleFiles(String, String, List<String>).\n");
        }
        return false;
    }

    // Deletes the existing file for now.
    private void handleIfFileAlreadyExists(File file, OutputStream outputStream) {
        if (file.exists()) {
            file.delete();
        }
    }

    /* Utility Methods */
    private Encoding getAcceptedEncoding(List<String> request) {
        return request.stream()
                .filter(req -> req.startsWith("Accept-Encoding")).findFirst()
                .map(acceptEncoding -> {
                    String[] encodingTypes = acceptEncoding.substring("Accept-Encoding: ".length()).split(",");
                    List<String> receivedEncodes = Arrays.stream(encodingTypes).map(String::strip).toList();
                    for (Encoding encoding : Encoding.values()) {
                        if (receivedEncodes.contains(encoding.toString())) {
                            return encoding;
                        }
                    }
                    return null;
                }).orElse(null);
    }

    private byte[] encodeResponseBody(String unEncodedResponseBody, Encoding encodeType) throws IOException {
        if (encodeType == null) {
            return unEncodedResponseBody.getBytes();
        }
        return switch (encodeType) {
            case GZIP -> encodeToGzip(unEncodedResponseBody);
        };
    }

    private byte[] encodeResponseBody(byte[] unEncodedResponseBody, Encoding encodeType) throws IOException {
        String unEncodedResponse = new String(unEncodedResponseBody, StandardCharsets.UTF_8);
        return encodeResponseBody(unEncodedResponse, encodeType);
    }

    private byte[] encodeToGzip(String value) throws IOException {
        byte[] buf = value.getBytes();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            gzos.write(buf, 0, buf.length);
        } catch (IOException ex) {
            throw ex;
        }
        return baos.toByteArray();
    }

    private int getContentLength(List<String> request) throws IOException {
        Optional<String> contentLengthHeader = request.stream().filter(req -> req.startsWith("Content-Length: ")).findFirst();
        if (contentLengthHeader.isPresent()) {
            String contentLength = contentLengthHeader.get();
            return Integer.parseInt(contentLength.substring("Content-Length: ".length()));
        }
        throw new IOException("Content-Length header is missing.\n");
    }
}
