public enum HTTPStatusCode {
    OK("200 OK"),
    NOT_FOUND("404 Not Found"),
    CREATED("201 Created");

    private final String status;
    HTTPStatusCode(String status) {
        this.status = status;
    }

    public String value() {
        return status;
    }

    @Override
    public String toString() {
        return this.status;
    }
}
