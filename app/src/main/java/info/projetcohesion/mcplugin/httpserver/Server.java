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
    private static HttpServer s_server;
    private static final FileManager s_config = new FileManager("http");

    /**
     * Start the integrated HTTP server
     * @throws IOException If an I/O error occurs
     * @see HttpServer
     */
    public static void start() throws IOException {
        if(s_server == null) { // If the server is already running, do nothing
            checkConfig();

            s_server = HttpServer.create(new InetSocketAddress(Objects.requireNonNull(s_config.get().getString("server.hostname")), s_config.get().getInt("server.port")), 0);
            s_server.createContext("/", new Handler());
            s_server.setExecutor(Executors.newFixedThreadPool(s_config.get().getInt("perfs.threads")));
            s_server.start();

            System.out.println("HTTP server started on " + s_server.getAddress().toString());
        }
    }

    /**
     * Stop the integrated HTTP server
     * @see HttpServer#stop(int) 
     */
    public static void stop() {
        if(s_server != null) { // Don't stop an already stopped server
            s_server.stop(0); // Stop now
            s_server = null; // Stopped HttpServer objects can't be reused
            System.out.println("HTTP server stopped");
        }
    }

    /**
     * Basics sanity checks for the HTTP configuration file.
     * If the file does not exist, create it with the following default values :
     * <ul>
     *     <li>Hostname : <code>localhost</code></li>
     *     <li>Port : <code>8080</code></li>
     *     <li>Thread count : Current number of processors</li>
     * </ul>
     *
     * @see Runtime#availableProcessors()
     */
    private static void checkConfig() {
        if(s_config.get().getConfigurationSection("server") == null) {
            s_config.get().set("server.hostname", "localhost");
            s_config.get().set("server.port", 8080);
        } if (s_config.get().getConfigurationSection("perfs") == null) {
            s_config.get().set("perfs.threads", Runtime.getRuntime().availableProcessors());

            System.out.println("Using all " + Runtime.getRuntime().availableProcessors() + " detected CPUs for HTTP traffic handling. You can change this in the config file.");
        }

        s_config.save();
    }
}
