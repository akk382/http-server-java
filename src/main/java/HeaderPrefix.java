public class HeaderPrefix {
    public static String getHeaderPrefix(RequestHeader header) {
        return header.value() + ": ";
    }

    public static String getHeaderPrefix(ResponseHeader header) {
        return header.value() + ": ";
    }
}
