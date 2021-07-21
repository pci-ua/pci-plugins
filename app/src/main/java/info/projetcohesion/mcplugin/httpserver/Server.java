package info.projetcohesion.mcplugin.httpserver;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * The integrated HTTP server
 */
public class Server {
    private static HttpServer _server;

    /**
     * Start the integrated HTTP server
     * @throws IOException If an I/O error occurs
     * @see HttpServer
     */
    public static void start() throws IOException {
        if(_server == null) { // If the server is already running, do nothing
            _server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0); // TODO don't hardcode hostname and port
            _server.createContext("/", new Handler());
            _server.setExecutor(Executors.newFixedThreadPool(10)); // TODO don't hardcode the thread count for performance reasons
            _server.start();

            System.out.println("HTTP server started on " + _server.getAddress().toString());
        }
    }

    /**
     * Stop the integrated HTTP server
     * @see HttpServer#stop(int) 
     */
    public static void stop() {
        if(_server != null) { // Don't stop an already stopped server
            _server.stop(0); // Stop now
            _server = null; // Stopped HttpServer objects can't be reused
            System.out.println("HTTP server stopped");
        }
    }
}
