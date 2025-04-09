import java.util.*;

public class HTTPResponse {
    // status line
    private HTTPVersion version;
    private HTTPStatusCode statusCode;
    private Map<ResponseHeader, String> responseHeaderMap;
    private byte[] responseBody;

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

    public Map<ResponseHeader, String> getResponseHeaderMap() {
        if (this.responseHeaderMap == null) {
            this.responseHeaderMap = new HashMap<>();
        }
        return responseHeaderMap;
    }

    public void setResponseHeaderMap(Map<ResponseHeader, String> responseHeaderMap) {
        this.responseHeaderMap = responseHeaderMap;
    }

    public void addResponseHeader(ResponseHeader responseHeader, String value) {
        if (this.responseHeaderMap == null) {
            this.responseHeaderMap = new HashMap<>();
        }
        this.responseHeaderMap.put(responseHeader, value);
    }

    public byte[] getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }
}
