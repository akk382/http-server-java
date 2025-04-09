import java.io.IOException;
import java.io.OutputStream;

public class HTTPOutputStream {

    private final OutputStream outputStream;

    public HTTPOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    // HTTPVersion Status_Code\r\nheader1\r\nheder2\r\n\r\nbody
    public void write(HTTPResponse response) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(response.getVersion());
        builder.append(HTTPConstants.SPACE);
        builder.append(response.getStatusCode());
        builder.append(HTTPConstants.LINE_SEPARATOR);
        response.getResponseHeaders().stream()
                .map(ResponseHeader::toString).forEach(header -> {
                    builder.append(header);
                    builder.append(HTTPConstants.LINE_SEPARATOR);
                });
        builder.append(HTTPConstants.LINE_SEPARATOR);
        // TODO
//        builder.append(response.getResponseBody().orElse(""));
        outputStream.write(builder.toString().getBytes());
        outputStream.flush();
    }
}
