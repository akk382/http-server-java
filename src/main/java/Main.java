import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        try {
            ServerSocket serverSocket = new ServerSocket(4221);

            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);

            Socket client = serverSocket.accept();// Wait for connection from client.
            System.out.println("accepted new connection");
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
                }
                else if (httpURI.startsWith("/user-agent")) {
                    Optional<String> userAgentHeader = request.stream().filter(req -> req.startsWith("User-Agent: ")).findFirst();
                    if (userAgentHeader.isPresent()) {
                        String userAgent = userAgentHeader.get().substring("User-Agent: ".length());
                        String response = String.format(
                                "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %d\r\n\r\n%s",
                                userAgent.length(), userAgent);
                        outputStream.write(response.getBytes());
                    }
                }
                else {
                    outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                }
            }



            client.close();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
