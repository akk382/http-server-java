import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {

    private HTTPMethod method;
    private HTTPVersion version;
    private Map<RequestHeader, String> requestHeaderMap;
    private RequestBody requestBody;
    private String uri;

    public HTTPMethod getMethod() {
        return method;
    }

    public void setMethod(HTTPMethod method) {
        this.method = method;
    }

    public HTTPVersion getVersion() {
        return version;
    }

    public void setVersion(HTTPVersion version) {
        this.version = version;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Map<RequestHeader, String> getRequestHeaderMap() {
        return requestHeaderMap;
    }

    public void setRequestHeaderMap(Map<RequestHeader, String> requestHeaderMap) {
        this.requestHeaderMap = requestHeaderMap;
    }

    public void addRequestHeader(RequestHeader requestHeader, String value) {
        if (this.requestHeaderMap == null) {
            requestHeaderMap = new HashMap<>();
        }
        this.requestHeaderMap.put(requestHeader, value);
    }

    public String getRequestHeader(RequestHeader requestHeader) {
        if (this.requestHeaderMap == null) {
            return null;
        }
        return this.requestHeaderMap.getOrDefault(requestHeader, null);
    }
}
