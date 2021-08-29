package info.projetcohesion.mcplugin.httpserver;

/**
 * A subset of HTTP error codes used by the server.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status">Mozilla Developer Network article on HTTP codes</a>
 */
public class HttpCodes {
    /**
     * When everything went OK
     */
    public static final int OK = 200;

    /**
     * When the resource sent by the client was created
     */
    public static final int CREATED = 201;

    /**
     * When the resource asked by the client has been moved permanently
     */
    public static final int MOVED_PERMANENTLY = 301;

    /**
     * When the client sent a malformed request to the server
     */
    public static final int BAD_REQUEST = 400;

    /**
     * When the client used a method (such as POST or GET) that is not supported by the server
     */
    public static final int METHOD_NOT_ALLOWED = 405;

    /**
     * When the client is sending too many requests in a short amount of time.
     */
    public static final int TOO_MANY_REQUESTS = 429;
}
