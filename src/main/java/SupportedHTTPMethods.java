enum SupportedHTTPMethods {
    GET("GET"), POST("POST");

    private final String methodString;

    SupportedHTTPMethods(String methodString) {
        this.methodString = methodString;
    }
}
