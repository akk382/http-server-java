import java.util.List;

public class HTTPResponseBuilder {

    private HTTPVersion version;
    private HTTPStatusCode statusCode;
    private List<ResponseHeader> responseHeaders;
    private ResponseBody responseBody;

    public static HTTPResponseBuilder builder() {
        return new HTTPResponseBuilder();
    }

    public HTTPResponse build() {
        HTTPResponse response = new HTTPResponse();
        response.setStatusCode(this.statusCode);
        response.setResponseBody(this.responseBody);
        response.setVersion(this.version);
        response.setResponseHeaders(responseHeaders);
        return response;
    }

    public HTTPResponseBuilder setVersion(HTTPVersion version) {
        this.version = version;
        return this;
    }

    public HTTPResponseBuilder setStatusCode(HTTPStatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HTTPResponseBuilder setResponseHeaders(List<ResponseHeader> responseHeaders) {
        this.responseHeaders = responseHeaders;
        return this;
    }

    public HTTPResponseBuilder setResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
        return this;
    }
}
