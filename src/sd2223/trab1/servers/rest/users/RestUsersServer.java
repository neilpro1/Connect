package sd2223.trab1.servers.rest.users;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import sd2223.trab1.api.discovery.Discovery;

public class RestUsersServer {

	private static Logger Log = Logger.getLogger(RestUsersServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public static final int PORT = 8080;
	public static final String SERVICE = "users";
	private static final String SERVER_URI_FMT = "http://%s:%s/rest";

	public static void main(String[] args) {
		try {

			String domain = args[0];
			//int id = Integer.parseInt(args[1]);
			
			ResourceConfig config = new ResourceConfig();
			config.register(RestUsersResource.class);
			// config.register(CustomLoggingFilter.class);

			String ip = InetAddress.getLocalHost().getHostAddress();
			//String ip = "feeds.ourorg-1";

			String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
			JdkHttpServerFactory.createHttpServer(URI.create(serverURI.replace(ip, "0.0.0.0")), config);

			Log.info(String.format("%s Server ready @ %s\n", SERVICE, serverURI));
			
			// More code can be executed here...
			Discovery discovery = Discovery.getInstance();
			String nameService = args[0] + ":" + SERVICE;
			discovery.announce(nameService, serverURI );

		} catch (Exception e) {
			Log.severe(e.getMessage());
		}
	}
}
