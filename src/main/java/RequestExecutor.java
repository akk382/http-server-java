import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RequestExecutor implements Runnable {

    private Socket client;
    private File directory;

    private RequestExecutor() {}

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
            OutputStream outputStream = client.getOutputStream();

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

                if (httpURI.isEmpty() || httpURI.equals("/")) {
                    outputStream.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                } else if (httpURI.startsWith("/echo/")) {
                    String restOfURI = httpURI.substring(6);
                    String response = String.format(
                            "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %d\r\n\r\n%s",
                            restOfURI.length(), restOfURI);
                    outputStream.write(response.getBytes());
                } else if (httpURI.startsWith("/user-agent")) {
                    Optional<String> userAgentHeader = request.stream().filter(req -> req.startsWith("User-Agent: ")).findFirst();
                    if (userAgentHeader.isPresent()) {
                        String userAgent = userAgentHeader.get().substring("User-Agent: ".length());
                        String response = String.format(
                                "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %d\r\n\r\n%s",
                                userAgent.length(), userAgent);
                        outputStream.write(response.getBytes());
                    }
                } else if (httpURI.startsWith("/files/")) {
                    if (this.directory == null) {
                        outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                    } else {
                        String restOfUri = httpURI.substring("/files/".length());
                        File file = new File(directory.getPath() + restOfUri);
                        InputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));
                        byte[] bytes = fileInputStream.readAllBytes();
                        String response = String.format(
                                "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: %d\r\n\r\n%s",
                                bytes.length, new String(bytes, StandardCharsets.UTF_8));
                        outputStream.write(response.getBytes());
                    }
                } else {
                    outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                }
            }

            client.close();
        } catch (IOException ex) {
            System.out.println("Error occurred\n");
        }



    }
}
