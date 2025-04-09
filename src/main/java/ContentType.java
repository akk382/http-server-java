public enum ContentType {
    TEXT_PLAIN("text/plain"),
    OCTET_STREAM("application/octet-stream");

    private final String contentType;
    ContentType(String contentType) {
        this.contentType = contentType;
    }

    public static ContentType fromString(String encodingType) {
        if (TEXT_PLAIN.contentType.equalsIgnoreCase(encodingType)) {
            return TEXT_PLAIN;
        } else if (OCTET_STREAM.contentType.equalsIgnoreCase(encodingType)) {
            return OCTET_STREAM;
        }
        return null;
    }

    public String value() {
        return contentType;
    }

    @Override
    public String toString() {
        return this.contentType;
    }
}
