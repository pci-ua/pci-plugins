package info.projetcohesion.mcplugin.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * The core of the integrated HTTP server.
 * The HTTP traffic is managed here.
 */
public class Handler implements HttpHandler {
    /**
     * Handle the requests
     * @param exchange The HTTP exchange between the client and the server
     * @throws IOException If an I/O error occurs
     * @see HttpHandler#handle(HttpExchange) 
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "This seems to work !"); // TODO Implement the server
    }

    /**
     * Send responses to an HTTP client
     * @param exchange The HTTP exchange between the client and the server
     * @param text The response to send to the client
     * @throws IOException If an I/O error occurs
     * @see Handler#handle(HttpExchange) 
     */
    private void sendResponse(HttpExchange exchange, String text) throws IOException {
        OutputStream os = exchange.getResponseBody();

        exchange.sendResponseHeaders(200, text.length());
        os.write(text.getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
    }
}
