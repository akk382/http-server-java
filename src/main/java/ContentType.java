public enum ContentType {
    TEXT_PLAIN("text/plain");

    private final String contentType;
    ContentType(String contentType) {
        this.contentType = contentType;
    }

    public static ContentType fromString(String encodingType) {
        if (TEXT_PLAIN.name().equalsIgnoreCase(encodingType)) {
            return TEXT_PLAIN;
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
