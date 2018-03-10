package akt.app;

import akt.app.database.dao.UserDaoImpl;
import akt.app.server.HttpServer;
import akt.app.server.HttpServerFactory;
import akt.app.server.ServerAddress;
import akt.app.server.servlet.ServletMapPathFactory;
import akt.app.service.UserServiceImpl;
import redis.embedded.RedisServer;

/**
 * @author akt.
 */
public class RunApp {

	public static void main(String[] args) throws Exception {
		if (args.length == 3) {
			String httpServerAddress = args[0];
			String httpServerPort = args[1];
			String redisPortArg = args[2];

			try {
				int redisPort = Integer.parseInt(redisPortArg);
				ServletMapPathFactory servletMapPathFactory = new ServletMapPathFactory(
					new UserServiceImpl(new UserDaoImpl(ServerAddress.DEFAULT_REDIS_HOST,
						redisPort))
				);
				HttpServerFactory httpServerFactory = new HttpServerFactory(servletMapPathFactory.create());

				HttpServer httpServer = httpServerFactory
					.buildHttpServer(httpServerAddress, Integer.parseInt(httpServerPort), 100);
				httpServer.start();

				RedisServer redisServer = new RedisServer(redisPort);
				redisServer.start();

			} catch (NumberFormatException exception) {
				System.out.println("Port should be integer");
			}
		} else {
			System.out.println("Application will run with default configuration");
			HttpServer httpServer = HttpServerFactory.DEFAULT;
			httpServer.start();

			RedisServer redisServer = new RedisServer();
			redisServer.start();
		}
	}
}
