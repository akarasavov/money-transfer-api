package akt.app.server;

import akt.app.server.HttpServerImpl.Builder;
import akt.app.server.servlet.ServletMapPathFactory;
import java.util.Map;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author akt.
 */
public class HttpServerFactory {

	private static int DEFAULT_THREAD_SIZE = 10;

	public static HttpServer DEFAULT = buildDefaultHttpServer();

	private final Map<ServletHolder, String> servletPathMap;

	private static HttpServer buildDefaultHttpServer() {
		HttpServerFactory httpServerFactory = new HttpServerFactory(ServletMapPathFactory.DEFAULT_SERVLET_PATH_MAP);
		return httpServerFactory
			.buildHttpServer(ServerAddress.DEFAULT_HTTP_SERVER_ADDRESS, ServerAddress.DEFAULT_HTTP_SERVER_PORT,
				DEFAULT_THREAD_SIZE);

	}

	public HttpServerFactory(Map<ServletHolder, String> servletPathMap) {
		this.servletPathMap = servletPathMap;
	}

	public HttpServer buildHttpServer(String serverAddress, int server, int maxThreadSize) {
		return buildHttpServer(serverAddress, server, maxThreadSize, servletPathMap);
	}

	private HttpServer buildHttpServer(String address, int port, int maxThreadSize,
		Map<ServletHolder, String> servletPathMap) {
		Builder builder = new HttpServerImpl.Builder(address, port, maxThreadSize);
		servletPathMap.forEach(builder::addServletWithMapping);
		return builder.build();
	}


}
