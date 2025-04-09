public enum RequestHeader {
    CONTENT_LENGTH("Content-Length"),
    ACCEPT_ENCODING("Accept-Encoding"),
    USER_AGENT("User-Agent"),
    HOST("Host"),
    ACCEPT("Accept");

    private final String headerName;
    RequestHeader(String headerName) {
        this.headerName = headerName;
    }

    public String value() {
        return headerName;
    }

    public static RequestHeader fromString(String header) {
        if (CONTENT_LENGTH.headerName.equalsIgnoreCase(header)) {
            return CONTENT_LENGTH;
        } else if (ACCEPT_ENCODING.headerName.equalsIgnoreCase(header)) {
            return ACCEPT_ENCODING;
        } else if (USER_AGENT.headerName.equalsIgnoreCase(header)) {
            return USER_AGENT;
        } else if (HOST.headerName.equalsIgnoreCase(header)) {
            return HOST;
        } else if (ACCEPT.headerName.equalsIgnoreCase(header)) {
            return ACCEPT;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return this.headerName;
    }
}
