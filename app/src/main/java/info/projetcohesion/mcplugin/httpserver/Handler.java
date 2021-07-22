package info.projetcohesion.mcplugin.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * The core of the integrated HTTP server.
 * The HTTP traffic is managed here.
 */
public class Handler implements HttpHandler {
    /**
     * Header used at the start of a file uploaded by a HTML form with <code>enctype="multipart/form-data"</code>
     */
    private static final byte[] UPLOAD_HEADER = "--".getBytes();

    /**
     * Byte representation of the character "\n"
     */
    private static final byte NEWLINE = 10; // "\n".getBytes() = byte[]{10}

    /**
     * Byte representation of the character "\r"
     */
    private static final byte CR = 13; // "\r".getBytes() = byte[]{13}

    /**
     * Handle the requests
     * @param exchange The HTTP exchange between the client and the server
     * @throws IOException If an I/O error occurs
     * @see HttpHandler#handle(HttpExchange) 
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("HTTP request from " + exchange.getRemoteAddress().toString()); // Log the IPs addresses
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            byte[] data = exchange.getRequestBody().readAllBytes();

            // Needed since a for loop will start at this index later
            if (data.length < 5) {
                badRequest(exchange);
                return;
            }

            // Check for the header
            for(int i = 0; i < UPLOAD_HEADER.length; i++) {
                if(UPLOAD_HEADER[i] != data[i]) {
                    badRequest(exchange);
                    return;
                }
            }

            int endHeadersIndex = searchForCRLF(data, true); // Search for the header
            int endFooterIndex = searchForCRLF(data, false); // Search for the footer

            if(endHeadersIndex == -1 || endFooterIndex == -1) {
                badRequest(exchange);
                return;
            }

            data = Arrays.copyOfRange(data, endHeadersIndex, endFooterIndex-1); // Remove the header and footer

            // TODO Process the data

            sendResponse(exchange, "Done", HttpCodes.OK);
        } else {
            sendResponse(exchange, "GET is not supported", HttpCodes.METHOD_NOT_ALLOWED);
        }
    }

    /**
     * Send responses to an HTTP client
     * @param exchange The HTTP exchange between the client and the server
     * @param text The response to send to the client
     * @param code The HTTP error code to send.
     * @throws IOException If an I/O error occurs
     * @see Handler#handle(HttpExchange)
     * @see HttpCodes
     */
    private void sendResponse(HttpExchange exchange, String text, int code) throws IOException {
        OutputStream os = exchange.getResponseBody();

        exchange.sendResponseHeaders(code, text.length());
        os.write(text.getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
    }

    /**
     * Search for Windows-style newlines in a byte array
     * @param data The byte array to process
     * @param isHeader <code>true</code> if the search target is an HTTP file upload, <code>false</code> for a footer
     * @return The index of the start of the newline, or <code>-1</code> if not found
     */
    private int searchForCRLF(byte[] data, boolean isHeader) {
        if (isHeader) {
            for(int i = 4; i < data.length; i++) {
                // Search for two new lines (Windows formatted)
                if(data[i-4] == CR && data[i-3] == NEWLINE && data[i-2] == CR && data[i-1] == NEWLINE) {
                    return i;
                }
            }
        } else {
            for(int i = data.length-1; i >= 2; i--) {
                // Search for new line (Windows formatted)
                if(data[i-2] == CR && data[i-1] == NEWLINE) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Logs bad requests and send an error code to the client
     * @param exchange The HTTP exchange between the client and the server
     * @throws IOException If an I/O error occurs
     */
    private void badRequest(HttpExchange exchange) throws IOException {
        System.err.println("Request from " + exchange.getRemoteAddress().toString() + " was malformed");
        sendResponse(exchange, "Bad request", HttpCodes.BAD_REQUEST);
    }
}