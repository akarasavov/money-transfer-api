package akt.app.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.jetbrains.annotations.NotNull;

/**
 * @author akt.
 */
public class HttpServerImpl implements HttpServer {

	private final Server server;

	private HttpServerImpl(Builder builder) {
		this.server = new Server(new QueuedThreadPool(builder.maxThreadSize));
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(builder.port);
		connector.setHost(builder.host);
		server.setConnectors(new Connector[]{connector});

		ServletHandler servletHandler = new ServletHandler();
		server.setHandler(servletHandler);

		builder.servletPathMap.forEach(servletHandler::addServletWithMapping);
	}

	@Override
	public void start() throws Exception {
		server.start();
	}

	@Override
	public void stop() throws Exception {
		server.stop();
	}


	static class Builder {

		private final int port;
		private final int maxThreadSize;
		private Map<ServletHolder, String> servletPathMap;
		String host;

		Builder(@NotNull String host, int port, int maxThreadSize) {
			Objects.requireNonNull(host);
			if (maxThreadSize <= 0) {
				throw new IllegalArgumentException("MaxThreadSize should be more than 0");
			}

			this.port = port;
			this.maxThreadSize = maxThreadSize;
			servletPathMap = new HashMap<>();
		}

		public Builder addServletWithMapping(@NotNull ServletHolder servlet, String urlPath) {
			servletPathMap.put(servlet, urlPath);
			return this;
		}

		public HttpServer build() {
			return new HttpServerImpl(this);
		}
	}
}
