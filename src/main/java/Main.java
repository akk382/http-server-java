import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        // Uncomment this block to pass the first stage

        try {
            ServerSocket serverSocket = new ServerSocket(4221);

            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);

            Socket client = serverSocket.accept();// Wait for connection from client.
            System.out.println("accepted new connection");
            OutputStream outputStream = client.getOutputStream();


            InputStream inputStream = new BufferedInputStream(client.getInputStream());
            byte[] request_bytes = inputStream.readAllBytes();
            String request = new String(request_bytes, StandardCharsets.UTF_8);

            String[] splitRequest = request.split("\r\n");
            String requestLine = splitRequest[0];
            List<String> requestHeaders = new ArrayList<>();
            int i = 1;
            while (i < splitRequest.length && !Objects.equals(splitRequest[i], "")) {
                requestHeaders.add(splitRequest[i]);
                i++;
            }
            String requestBody;
            if (i < splitRequest.length) {
                requestBody = splitRequest[i];
            }

            String[] requestLineParts = requestLine.split(" ");
            String requestMethod = requestLineParts[0];
            String requestTarget = requestLineParts[1]; // URL / resource path
            String requestVersion = requestLineParts[2];

            if (requestTarget.isEmpty()) {
                outputStream.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
            } else {
                outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            }


            client.close();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
