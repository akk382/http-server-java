import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Test {

    public static void main(String[] args) {
        String request = "GET /index.html HTTP/1.1\r\nHost: localhost:4221\r\nUser-Agent: curl/7.64.1\r\nAccept: */*\r\n\r\n";
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
    }
}
