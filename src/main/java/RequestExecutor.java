import java.io.OutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RequestExecutor implements Runnable {

    private Socket client;
    private File directory;

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
            List<String> request = new ArrayList<>();
            String message;
            InputStream inputStream = client.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            while (!Objects.equals(message = reader.readLine(), "")) {
                request.add(message);
            }

            if (!request.isEmpty()) {
                String requestLine = request.getFirst();

                String[] requestLineParts = requestLine.split(" ");
                String httpMethod = requestLineParts[0];
                String httpURI = requestLineParts[1]; // URL / resource path
                String httpVersion = requestLineParts[2];

                boolean responded = handleBaseUri(httpURI) || handleEcho(httpURI)
                        || handleUserAgent(httpURI, request) || handleFiles(httpURI);
                handleNotFound(responded);
            }

            reader.close();
            inputStream.close();
            client.close();
        } catch (IOException ex) {
            System.out.println("Error occurred\n");
        }
    }

    private void handleNotFound(boolean responded) throws IOException {
        if (!responded) {
            OutputStream outputStream = client.getOutputStream();
            outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            outputStream.close();
        }
    }

    private boolean handleUserAgent(String httpURI, List<String> request) throws IOException {
        if (!httpURI.startsWith("/user-agent")) {
            return false;
        }
        Optional<String> userAgentHeader = request.stream().filter(req -> req.startsWith("User-Agent: ")).findFirst();
        if (userAgentHeader.isPresent()) {
            String userAgent = userAgentHeader.get().substring("User-Agent: ".length());
            String response = String.format(
                    "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %d\r\n\r\n%s",
                    userAgent.length(), userAgent);
            OutputStream outputStream = client.getOutputStream();
            outputStream.write(response.getBytes());
            outputStream.close();
            return true;
        }
        return false;
    }

    private boolean handleEcho(String httpURI) throws IOException {
        if (httpURI.startsWith("/echo/")) {
            String restOfURI = httpURI.substring(6);
            String response = String.format(
                    "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %d\r\n\r\n%s",
                    restOfURI.length(), restOfURI);
            OutputStream outputStream = client.getOutputStream();
            outputStream.write(response.getBytes());
            outputStream.close();
            return true;
        }
        return false;
    }

    private boolean handleBaseUri(String httpURI) throws IOException {
        if (httpURI.isEmpty() || httpURI.equals("/")) {
            OutputStream outputStream = client.getOutputStream();
            outputStream.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
            outputStream.close();
            return true;
        }
        return false;
    }

    private void handleIfDirectoryNotFound(File directory, OutputStream outputStream) throws IOException {
        if (!directory.exists()) {
            outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            outputStream.close();
            throw new IOException("Directory not found.\n");
        }
    }

    private void handleIfFileNotFound(File file, OutputStream outputStream) throws IOException {
        if (!file.exists()) {
            outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            outputStream.close();
            throw new IOException("File not found.\n");
        }
    }

    private boolean handleFiles(String httpURI) throws IOException {
        if (httpURI.startsWith("/files/")) {
            OutputStream outputStream = client.getOutputStream();
            handleIfDirectoryNotFound(directory, outputStream);
            String restOfUri = httpURI.substring("/files/".length());
            File file = new File(directory.getPath() + File.separator + restOfUri);
            handleIfFileNotFound(file, outputStream);
            System.out.println("File: " + file.getPath());
            InputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] bytes = fileInputStream.readAllBytes();
            System.out.println("Response Body: " + new String(bytes, StandardCharsets.UTF_8));
            String response = String.format(
                    "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: %d\r\n\r\n%s",
                    bytes.length, new String(bytes, StandardCharsets.UTF_8));
            outputStream.write(response.getBytes());
            fileInputStream.close();
            outputStream.close();
            return true;
        }
        return false;
    }
}
