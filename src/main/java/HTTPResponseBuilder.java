import java.util.HashMap;
import java.util.Map;

public class HTTPResponseBuilder {

    private HTTPVersion version;
    private HTTPStatusCode statusCode;
    private Map<ResponseHeader, String> responseHeaderMap;
    private byte[] responseBody;

    public static HTTPResponseBuilder builder() {
        return new HTTPResponseBuilder();
    }

    public HTTPResponse build() {
        HTTPResponse response = new HTTPResponse();
        response.setStatusCode(this.statusCode);
        response.setResponseBody(this.responseBody);
        response.setVersion(this.version);
        response.setResponseHeaderMap(responseHeaderMap);
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

    public HTTPResponseBuilder setResponseHeaderMap(Map<ResponseHeader, String> responseHeaderMap) {
        this.responseHeaderMap = responseHeaderMap;
        return this;
    }

    public HTTPResponseBuilder addResponseHeader(ResponseHeader responseHeader, String value) {
        if (this.responseHeaderMap == null) {
            this.responseHeaderMap = new HashMap<>();
        }
        this.responseHeaderMap.put(responseHeader, value);
        return this;
    }

    public HTTPResponseBuilder setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
        return this;
    }
}
