import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Arrays;
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
