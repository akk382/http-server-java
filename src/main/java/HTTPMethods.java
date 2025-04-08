enum HTTPMethods {
    GET("GET"),
    POST("POST");

    private final String methodString;

    HTTPMethods(String methodString) {
        this.methodString = methodString;
    }
}
