import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class HTTPOutputStream {

    private final OutputStream outputStream;

    public HTTPOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    // HTTPVersion Status_Code\r\nheader1\r\nheder2\r\n\r\nbody
    public void write(HTTPResponse response) throws IOException {
        StringBuilder builder = new StringBuilder()
                .append(response.getVersion())
                .append(HTTPConstants.SPACE)
                .append(response.getStatusCode())
                .append(HTTPConstants.LINE_SEPARATOR);
        response.getResponseHeaderMap().forEach((header, value) -> {
                    builder.append(HeaderPrefix.getHeaderPrefix(header));
                    builder.append(value);
                    builder.append(HTTPConstants.LINE_SEPARATOR);
                });
        builder.append(HTTPConstants.LINE_SEPARATOR);
        outputStream.write(builder.toString().getBytes());
        if (response.getResponseHeaderMap().containsKey(ResponseHeader.CONTENT_LENGTH)) {
            outputStream.write(response.getResponseBody());
        }
        outputStream.flush();
    }
}
