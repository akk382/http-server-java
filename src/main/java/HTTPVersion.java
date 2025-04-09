public enum HTTPVersion {
    HTTP1_1("HTTP/1.1");

    private final String version;
    HTTPVersion(String version) {
        this.version = version;
    }

    public static HTTPVersion fromString(String version) {
        if (HTTP1_1.version.equalsIgnoreCase(version)) {
            return HTTP1_1;
        } else {
            return null;
        }
    }

    public String value() {
        return version;
    }

    @Override
    public String toString() {
        return this.version;
    }
}
