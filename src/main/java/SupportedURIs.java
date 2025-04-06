enum SupportedURIs {

    BASE_URL("/"),
    USER_AGENT("/user-agent"),
    ECHO("/echo/"),
    FILES("/files/");

    private final String uri;
    SupportedURIs(String uri) {
        this.uri = uri;
    }
}
