package info.projetcohesion.mcplugin.httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import info.projetcohesion.mcplugin.Plugin;
import info.projetcohesion.mcplugin.httpserver.HttpCodes;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Serves a <code>robots.txt</code> file used to disable indexing on this server.
 *
 * @see HttpHandler
 */
public class RobotsHandler implements HttpHandler {
    /**
     * This constant contains the following string in a byte array format :
     * <pre>
     * {@code
     * User-agent: *
     * Disallow: /
     * }
     * </pre>
     */
    private static final byte[] GUIDELINES = "User-agent: *\nDisallow: /".getBytes(StandardCharsets.UTF_8);

    /**
     * Handle the HTTP request
     * @param exchange The HTTP exchange between the server and the client
     * @throws IOException If an I/O error occurs
     * @see HttpHandler#handle(HttpExchange) 
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Plugin.getPlugin().getLogger().info("HTTP request from " + exchange.getRemoteAddress().toString() + " (robots.txt)");
        OutputStream os = exchange.getResponseBody();

        exchange.sendResponseHeaders(HttpCodes.OK, GUIDELINES.length);
        os.write(GUIDELINES);
        os.flush();
        os.close();
    }
}
