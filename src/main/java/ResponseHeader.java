public enum ResponseHeader {
    CONTENT_ENCODING("Content-Encoding"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length");

    private final String header;
    ResponseHeader(String header) {
        this.header = header;
    }

    public String value() {
        return header;
    }

    @Override
    public String toString() {
        return this.header;
    }
}
