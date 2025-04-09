import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HTTPResponse {
    // status line
    private HTTPVersion version;
    private HTTPStatusCode statusCode;
    private List<ResponseHeader> responseHeaders;
    private Optional<ResponseBody> responseBody;

    public HTTPVersion getVersion() {
        return version;
    }

    public void setVersion(HTTPVersion version) {
        this.version = version;
    }

    public HTTPStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HTTPStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public List<ResponseHeader> getResponseHeaders() {
        if (this.responseHeaders == null) {
            return new ArrayList<>();
        }
        return responseHeaders;
    }

    public void setResponseHeaders(List<ResponseHeader> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public Optional<ResponseBody> getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(ResponseBody responseBody) {
        this.responseBody = Optional.ofNullable(responseBody);
    }
}
