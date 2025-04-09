import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Test {

    // TODO
//    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException {
//        Main server = new Main();
//        Thread thread = new Thread(() -> server.start(null));
//        thread.start();
//        Thread.sleep(200);
//
//        Test test = new Test();
//        test.testBaseUrl();
//
//        server.stop();
//        thread.join();
//    }
//
//    public void testBaseUrl() throws IOException, InterruptedException, ExecutionException, TimeoutException {
//        String url = "http://localhost:4221/";
//        URI uri = URI.create(url);
//        HttpRequest httpRequest = HttpRequest.newBuilder()
//                .uri(uri)
//                .GET()
//                .build();
//        HttpClient client = HttpClient.newHttpClient();
//        CompletableFuture<HttpResponse<String>> future = client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
//        HttpResponse<String> response = future.get(10, TimeUnit.SECONDS);
//        System.out.println(response.body());
//    }
}