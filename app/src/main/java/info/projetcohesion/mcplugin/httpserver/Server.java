package info.projetcohesion.mcplugin.httpserver;

import com.sun.net.httpserver.HttpServer;
import info.projetcohesion.mcplugin.Plugin;
import info.projetcohesion.mcplugin.httpserver.handlers.FileHandler;
import info.projetcohesion.mcplugin.httpserver.handlers.RobotsHandler;
import info.projetcohesion.mcplugin.utils.FileUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * The integrated HTTP server
 */
public class Server {
	/**
     * The instance of the currently running Server. May be <code>null</code>.
     */
    private static HttpServer s_server;

	/**
	 * The configuration of the HTTP server, saved in the plugin data directory.
	 * @see FileUtils
	 */
    private static final FileUtils s_config = new FileUtils("http");

    /**
     * The plugin-wide logger
     * @see org.bukkit.plugin.PluginLogger
     */
    private static final Logger logger = Plugin.getPlugin().getLogger();

    /**
     * Start the integrated HTTP server
     * @throws IOException If an I/O error occurs
     * @see HttpServer
     */
    public static void start() throws IOException {
        if(s_server == null) { // If the server is already running, do nothing
            checkConfig();

            s_server = HttpServer.create(new InetSocketAddress(Objects.requireNonNull(s_config.get().getString("server.hostname")), s_config.get().getInt("server.port")), 0);
			s_server.createContext("/", new FileHandler(Objects.requireNonNull(s_config.get().getString("redirect.target")), s_config.get().getBoolean("messages.built-in")));
			s_server.createContext("/robots.txt", new RobotsHandler());;
            s_server.setExecutor(Executors.newFixedThreadPool(s_config.get().getInt("perfs.threads")));
            s_server.start();

            logger.info("HTTP server started on " + s_server.getAddress().toString());
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
            logger.info("HTTP server stopped");
        }
    }

    /**
     * Basics sanity checks for the HTTP configuration file.
     * If the file does not exist, create it with the following default values :
     * <ul>
     *     <li>Hostname : <code>localhost</code></li>
     *     <li>Port : <code>8080</code></li>
     *     <li>Thread count : Current number of processors</li>
     *     <li>Redirect URL : <code>https://example.org/</code></li>
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

            logger.info("Using all " + Runtime.getRuntime().availableProcessors() + " detected CPUs for HTTP traffic handling. You can change this in the config file.");
		} if(s_config.get().getConfigurationSection("redirect") == null) {
			s_config.get().set("redirect.target", "https://example.org/");

			logger.warning("The redirect.target configuration value has been created with https://example.org/ as the default. You may want to change that.");
		} if(s_config.get().getConfigurationSection("messages") == null) {
			s_config.get().set("messages.built-in", true);
        }

        s_config.save();
    }
}
