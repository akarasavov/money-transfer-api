package akt.app.server.servlet;

import akt.app.database.dao.UserDaoImpl;
import akt.app.server.ServerAddress;
import akt.app.server.ServletConstant;
import akt.app.service.UserService;
import akt.app.service.UserServiceImpl;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author akt.
 */
public class ServletMapPathFactory {

	public static Map<ServletHolder, String> DEFAULT_SERVLET_PATH_MAP = init(
		new UserServiceImpl(new UserDaoImpl(ServerAddress.DEFAULT_REDIS_HOST, ServerAddress.DEFAULT_REDIS_PORT)));
	private final UserService userService;


	private static Map<ServletHolder, String> init(UserService userService) {

		Map<ServletHolder, String> servletPathMap = new HashMap<>();
		servletPathMap
			.put(new ServletHolder(new CreateAccountServlet(userService)), ServletConstant.Path.CREATE_ACCOUNT_PATH);
		servletPathMap
			.put(new ServletHolder(new GetUserAccountServlet(userService)), ServletConstant.Path.GET_ACCOUNT_PATH);
		servletPathMap
			.put(new ServletHolder(new TransferMoneyServlet(userService)), ServletConstant.Path.TRANSFER_MONEY_PATH);
		return Collections.unmodifiableMap(servletPathMap);
	}

	public ServletMapPathFactory(UserService userService) {
		this.userService = userService;
	}

	public Map<ServletHolder, String> create() {
		return init(userService);
	}

}
