import validator.HTTPRequestValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestParser {
    public static HTTPRequest parse(InputStream inputStream) throws IOException {
        List<String> requestWithoutBody = new ArrayList<>();
        String message;
        InputStreamReader inReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inReader);

        while (!Objects.equals(message = bufferedReader.readLine(), "")) {
            requestWithoutBody.add(message);
        }

        if (requestWithoutBody.isEmpty()) {
            throw new IOException("Empty HTTP request received");
        }

        // TODO: validate the http request
        try {
            HTTPRequestValidator.validate(requestWithoutBody);
        } catch (RuntimeException ex) {
            throw ex;
        }

        HTTPRequest httpRequest = new HTTPRequest();
        String requestLine = requestWithoutBody.getFirst();
        String[] requestLineParts = requestLine.split(" ");
        httpRequest.setMethod(HTTPMethod.fromString(requestLineParts[0]));
        httpRequest.setUri(requestLineParts[1]); // URL / resource path
        httpRequest.setVersion(HTTPVersion.fromString(requestLineParts[2]));

        // Parse http headers
        requestWithoutBody.stream().skip(1).forEach(header -> {
            String[] headerKeyValue = header.split(":");    // TODO: Host header is not parsed correctly since it contained multiple ':'
            RequestHeader headerKey = RequestHeader.fromString(headerKeyValue[0]);
            if (headerKey != null) {
                String headerValue = headerKeyValue[1].strip();
                httpRequest.addRequestHeader(headerKey, headerValue);
            }
        });

        // extract the request body from the POST request.
        if (httpRequest.getMethod().equals(HTTPMethod.POST)) {
           String headerValue = httpRequest.getRequestHeader(RequestHeader.CONTENT_LENGTH);
            if (headerValue != null) {
                int contentLength = Integer.parseInt(headerValue);
                char[] buf = new char[contentLength];
                int read;
                try {
                    read = bufferedReader.read(buf, 0, contentLength);
                    if (read == -1 || (read == 0 && contentLength != 0)) {
                        throw new IOException("Failed to read request body.\n");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                RequestBody body = new RequestBody();
                body.setBody(buf);
                httpRequest.setRequestBody(body);
            }
        }

        return httpRequest;
    }
}
