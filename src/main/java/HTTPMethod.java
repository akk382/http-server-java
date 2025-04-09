enum HTTPMethod {
    GET("GET"),
    POST("POST");

    private final String methodString;

    HTTPMethod(String methodString) {
        this.methodString = methodString;
    }


    public static HTTPMethod fromString(String method) {
        if (GET.methodString.equalsIgnoreCase(method)) {
            return GET;
        } else if (POST.methodString.equalsIgnoreCase(method)) {
            return POST;
        } else {
            return null;
        }
    }

    public String value() {
        return methodString;
    }

    @Override
    public String toString() {
        return this.methodString;
    }
}
