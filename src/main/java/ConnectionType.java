public enum ConnectionType {
    KEEP_ALIVE("keep-alive"),
    CLOSE("close");

    private final String type;
    ConnectionType(String type) {
        this.type = type;
    }

    public static ConnectionType fromString(String type) {
        if (KEEP_ALIVE.type.equalsIgnoreCase(type)) {
            return KEEP_ALIVE;
        } else if (CLOSE.type.equalsIgnoreCase(type)) {
            return CLOSE;
        }
        return CLOSE;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
