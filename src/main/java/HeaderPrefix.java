public class HeaderPrefix {
    public static String getHeaderPrefix(RequestHeader header) {
        return header.name() + ": ";
    }

    public static String getHeaderPrefix(ResponseHeader header) {
        return header.name() + ": ";
    }
}
