public class HeaderPrefix {
    public static String getHeaderPrefix(RequestHeader header) {
        return header.value() + HTTPConstants.HEADER_SEPARATOR;
    }

    public static String getHeaderPrefix(ResponseHeader header) {
        return header.value() + HTTPConstants.HEADER_SEPARATOR;
    }
}
