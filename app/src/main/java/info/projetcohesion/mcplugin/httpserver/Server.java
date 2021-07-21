package info.projetcohesion.mcplugin.httpserver;

import com.sun.net.httpserver.HttpServer;
import info.projetcohesion.mcplugin.managers.FileManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.Executors;

/**
 * The integrated HTTP server
 */
public class Server {
    private static HttpServer _server;
    private static FileManager _config = new FileManager("http");

    /**
     * Start the integrated HTTP server
     * @throws IOException If an I/O error occurs
     * @see HttpServer
     */
    public static void start() throws IOException {
        if(_server == null) { // If the server is already running, do nothing
            checkConfig();

            _server = HttpServer.create(new InetSocketAddress(Objects.requireNonNull(_config.get().getString("server.hostname")), _config.get().getInt("server.port")), 0);
            _server.createContext("/", new Handler());
            _server.setExecutor(Executors.newFixedThreadPool(_config.get().getInt("perfs.threads")));
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

    private static void checkConfig() {
        if(_config.get().getConfigurationSection("server") == null) {
            _config.get().set("server.hostname", "localhost");
            _config.get().set("server.port", 8080);
        } if (_config.get().getConfigurationSection("perfs") == null) {
            _config.get().set("perfs.threads", Runtime.getRuntime().availableProcessors());

            System.out.println("Using all " + Runtime.getRuntime().availableProcessors() + " detected CPUs for HTTP traffic handling. You can change this in the config file.");
        }

        _config.save();
    }
}
