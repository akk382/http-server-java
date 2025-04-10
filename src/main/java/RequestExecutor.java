import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
            boolean responded = handleBaseUri() || handleEcho() || handleUserAgent() || handleFiles();
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
        if (!httpRequest.getUri().startsWith(SupportedURIs.USER_AGENT.value())) {
            return false;
        }
        String userAgent = httpRequest.getRequestHeader(RequestHeader.USER_AGENT);
        Encoding acceptedEncoding = getAcceptedEncoding();
        byte[] encodedResponseBody = encodeResponseBody(userAgent, acceptedEncoding);
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

    private boolean handleEcho() throws IOException {
        if (httpRequest.getUri().startsWith(SupportedURIs.ECHO.value())) {
            String restOfURI = httpRequest.getUri().substring(SupportedURIs.ECHO.value().length());
            Encoding acceptedEncoding = getAcceptedEncoding();
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

    private void handleIfDirectoryNotFound(File directory) throws IOException {
        if (!directory.exists()) {
            handleNotFound(false);
            throw new IOException("Directory not found.\n");
        }
    }

    private void handleIfFileNotFound(File file) throws IOException {
        if (!file.exists()) {
            handleNotFound(false);
            throw new IOException("File not found.\n");
        }
    }

    private boolean handleFiles() throws IOException {
        if (httpRequest.getUri().startsWith(SupportedURIs.FILES.value())) {
            handleIfDirectoryNotFound(directory);
            String restOfUri = httpRequest.getUri().substring(SupportedURIs.FILES.value().length());
            File file = new File(directory.getPath() + File.separator + restOfUri);
            HTTPOutputStream outputStream = new HTTPOutputStream(client.getOutputStream());

            switch (httpRequest.getMethod()) {
                case GET -> {
                    Encoding acceptedEncoding = getAcceptedEncoding();
                    byte[] encodedContent = readFile(file, acceptedEncoding);
                    HTTPResponseBuilder builder = HTTPResponseBuilder.builder()
                            .setVersion(HTTPVersion.HTTP1_1)
                            .setStatusCode(HTTPStatusCode.OK)
                            .addResponseHeader(ResponseHeader.CONTENT_TYPE, ContentType.OCTET_STREAM.value())
                            .addResponseHeader(ResponseHeader.CONTENT_LENGTH, String.valueOf(encodedContent.length))
                            .setResponseBody(encodedContent);
                    if (acceptedEncoding != null) {
                        builder.addResponseHeader(ResponseHeader.CONTENT_ENCODING, acceptedEncoding.value());
                    }
                    HTTPResponse response = builder.build();
                    outputStream.write(response);
                    return true;
                }
                case POST -> {
                    writeToFile(file);
                    HTTPResponse response = HTTPResponseBuilder.builder()
                            .setVersion(HTTPVersion.HTTP1_1)
                            .setStatusCode(HTTPStatusCode.CREATED)
                            .build();
                    outputStream.write(response);
                    return true;
                }
                default -> handleNotFound(false);
            }
            throw new IOException("Failed request handling in handleFiles(String, String, List<String>).\n");
        }
        return false;
    }

    private void writeToFile(File file) throws IOException {
        handleIfFileAlreadyExists(file);
        if (!file.createNewFile()) {
            throw new IOException("Failed to create a new file.\n");
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(httpRequest.getRequestBody().getBody());
        writer.close();
    }

    private byte[] readFile(File file, Encoding encodeType) throws IOException {
        handleIfFileNotFound(file);
        InputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = fileInputStream.readAllBytes();
        fileInputStream.close();
        return encodeResponseBody(bytes, encodeType);
    }

    // Deletes the existing file for now.
    private void handleIfFileAlreadyExists(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    /* Utility Methods */
    private Encoding getAcceptedEncoding() {
        List<String> supportedEncodings = Arrays.stream(Encoding.values()).map(Encoding::value).toList();
        return Optional.ofNullable(httpRequest.getRequestHeader(RequestHeader.ACCEPT_ENCODING))
                .map(header -> header.split(","))
                .stream()
                .flatMap(Arrays::stream)
                .map(String::strip)
                .filter(supportedEncodings::contains)
                .findFirst()
                .map(Encoding::fromString)
                .orElse(null);
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

    // TODO: move to enum
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

}
