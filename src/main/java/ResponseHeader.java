public enum ResponseHeader {
    CONTENT_ENCODING("Content-Encoding");

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
