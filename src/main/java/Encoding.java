public enum Encoding {
    GZIP("gzip");

    private final String encodingType;
    Encoding(String encodingType) {
        this.encodingType = encodingType;
    }

    public static Encoding fromString(String encodingType) {
        if (GZIP.name().equalsIgnoreCase(encodingType)) {
            return GZIP;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.encodingType;
    }
}
