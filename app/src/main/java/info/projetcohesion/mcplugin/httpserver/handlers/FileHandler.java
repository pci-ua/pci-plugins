package info.projetcohesion.mcplugin.httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import info.projetcohesion.mcplugin.Plugin;
import info.projetcohesion.mcplugin.commands.MapArtCommand;
import info.projetcohesion.mcplugin.httpserver.HttpCodes;
import info.projetcohesion.mcplugin.utils.ImageMagick;
import info.projetcohesion.mcplugin.utils.JarRessourceLoader;
import info.projetcohesion.mcplugin.utils.TextFileLoader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * The core of the integrated HTTP server.
 * The HTTP traffic for file upload is managed here.
 *
 * @see HttpHandler
 */
public class FileHandler implements HttpHandler {
    // ---- CONSTANTS VALUES ----

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

    // ---- END CONSTANTS ----

    // ---- HTML FILES ----

    /**
     * The HTML to send to the client when rate limited
     */
    private String _rateLimitHTML = "You are issuing too many requests. Try again later.";

    /**
     * The HTML to send to the client when a non POST request is sent
     */
    private String _notPostHTML = "This server only support POST requests.";

    /**
     * The HTML to send to the client when a bad request is received
     */
    private String _badRequestHTML = "Bad request";

    /**
     * The HTML to send to the client when ImageMagick fails
     */
    private String _magickFailHTML = "Something went wrong. Is your file an image ?";

    // ---- END HTML ----

    /**
     * The plugin-wide logger
     * @see org.bukkit.plugin.PluginLogger
     */
    private final Logger logger = Plugin.getPlugin().getLogger();

    /**
     * The URL that is used to redirect the client after the image has been processed
     */
    private String _redirectUrl;

    /**
     * Save the last time an IP has sent a request.
     */
    private HashMap<String, Long> ipTime = new HashMap<>();

    /**
     * Create a <code>FileHandler</code> object.
     * @param redirectUrl The URL to redirect to after a successful image upload.
     */
    public FileHandler(String redirectUrl, boolean useBuiltInMessages) {
        assert redirectUrl != null;
        assert !redirectUrl.isBlank();

        _redirectUrl = redirectUrl;

        // HTML files
        if(useBuiltInMessages) {
            try {
                _rateLimitHTML = JarRessourceLoader.load("/html/RateLimit.html");
            } catch (IOException e) {
                logger.severe("Failed to load /html/RateLimit.html from the JAR archive !");
            }

            try {
                _notPostHTML = JarRessourceLoader.load("/html/NotPostError.html");
            } catch (IOException e) {
                logger.severe("Failed to load /html/NotPostError.html from the JAR archive !");
            }

            try {
                _badRequestHTML = JarRessourceLoader.load("/html/BadRequest.html");
            } catch (IOException e) {
                logger.severe("Failed to load /html/BadRequest.html from the JAR archive !");
            }

            try {
                _magickFailHTML = JarRessourceLoader.load("/html/MagickFail.html");
            } catch (IOException e) {
                logger.severe("Failed to load /html/MagickFail.html from the JAR archive !");
            }
        } else {
            try {
                _rateLimitHTML = TextFileLoader.load(new File(Plugin.getPlugin().getDataFolder(), "html/RateLimit.html"));
            } catch (IOException e) {
                logger.severe("Failed to load RateLimit.html from the config directory !");
                e.printStackTrace();
            }

            try {
                _notPostHTML = TextFileLoader.load(new File(Plugin.getPlugin().getDataFolder(), "html/NotPostError.html"));
            } catch (IOException e) {
                logger.severe("Failed to load NotPostError.html from the config directory !");
                e.printStackTrace();
            }

            try {
                _badRequestHTML = TextFileLoader.load(new File(Plugin.getPlugin().getDataFolder(), "html/BadRequest.html"));
            } catch (IOException e) {
                logger.severe("Failed to load BadRequest.html from the config directory !");
                e.printStackTrace();
            }

            try {
                _magickFailHTML = TextFileLoader.load(new File(Plugin.getPlugin().getDataFolder(), "html/MagickFail.html"));
            } catch (IOException e) {
                logger.severe("Failed to load MagickFail.html from the config directory !");
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle the requests
     * @param exchange The HTTP exchange between the client and the server
     * @throws IOException If an I/O error occurs
     * @see HttpHandler#handle(HttpExchange) 
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String ip = exchange.getRemoteAddress().getAddress().toString();
        logger.info("HTTP request from " + ip); // Log the IPs addresses

        // Rate-limit check
        if (ipTime.containsKey(ip) && System.nanoTime() - ipTime.get(ip) < 10000000000L) { // 10 secs
            logger.warning("Remote " + ip + " has been rate-limited !");
            ipTime.put(ip, System.nanoTime());
            sendResponse(exchange, _rateLimitHTML, HttpCodes.TOO_MANY_REQUESTS);
            return;
        } else {
            ipTime.put(ip, System.nanoTime());
        }

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

            String imageID;

            try {
                imageID = MapArtCommand.getImageManager().convertAndAdd(data);
            } catch (ImageMagick.ImageMagickException e) {
                logger.severe("Failed to convert the image !");
                e.printStackTrace();

                sendResponse(exchange, _magickFailHTML, HttpCodes.SERVER_INTERNAL_ERROR);
                return;
            }

            sendResponse(exchange, imageID, HttpCodes.MOVED_PERMANENTLY, _redirectUrl + "?id=" + imageID);
        } else {
            sendResponse(exchange, _notPostHTML, HttpCodes.METHOD_NOT_ALLOWED);
        }
    }

    /**
     * Send a response to an HTTP client. This is the same as using <code>sendResponse(HttpExchange, String, int, null)</code>.
     * @param exchange The HTTP exchange between the client and the server
     * @param text The response to send to the client
     * @param code The HTTP error code to send.
     * @throws IOException If an I/O error occurs
     * @see FileHandler#sendResponse(HttpExchange, String, int, String)
     * @see FileHandler#handle(HttpExchange)
     * @see HttpCodes
     */
    private void sendResponse(HttpExchange exchange, String text, int code) throws IOException {
        this.sendResponse(exchange, text, code, null);
    }

    /**
     * Send a response to an HTTP client, with a <code>Location</code> header for redirection purposes.
     * @param exchange The HTTP exchange between the client and the server
     * @param text The response to send to the client
     * @param code The HTTP error code to send.
     * @param redirect The URL to redirect to. May be <code>null</code> if you don't want to redirect.
     * @throws IOException If an I/O error occurs
     * @see FileHandler#sendResponse(HttpExchange, String, int)
     * @see FileHandler#handle(HttpExchange)
     * @see HttpCodes
     */
    private void sendResponse(HttpExchange exchange, String text, int code, String redirect) throws IOException {
        OutputStream os = exchange.getResponseBody();

        if (redirect != null) {
            exchange.getResponseHeaders().add("Location", redirect);
        }

        exchange.sendResponseHeaders(code, text.length());
        os.write(text.getBytes(StandardCharsets.UTF_8));
        os.flush();

        exchange.getRequestBody().readAllBytes(); // Clear the request, to avoid connection reset errors

        exchange.close();
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
        logger.warning("Request from " + exchange.getRemoteAddress().toString() + " was malformed");
        sendResponse(exchange, _badRequestHTML, HttpCodes.BAD_REQUEST);
    }
}
