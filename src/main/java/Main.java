import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");
        File directory = getDirectory(args);


        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
             ServerSocket serverSocket = new ServerSocket(4221)) {
            serverSocket.setReuseAddress(true);

            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors

            while (true) {
                Socket client = serverSocket.accept();// Wait for connection from client.
                System.out.println("accepted new connection");

                if (directory != null) {
                    System.out.println("Directory received: " + directory.getPath());
                }
                executorService.submit(new RequestExecutor(client, directory));
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static File getDirectory(String[] args) {
        Iterator<String> iterator = Arrays.stream(args).iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals("--directory") && iterator.hasNext()) {
                String path = iterator.next();
                return new File(path);
            }
        }
        return null;
    }
}
