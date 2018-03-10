package akt.app.server.servlet;

import akt.app.server.HttpServer;
import akt.app.server.HttpServerFactory;
import akt.app.server.response.ErrorCode;
import akt.app.server.response.ErrorResponse;
import akt.app.util.ObjectMapperUtil;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import redis.embedded.RedisServer;

/**
 * @author akt.
 */
class AbstractServletTest {

	private RedisServer redisServer;
	private HttpServer server;

	@Before
	public void setUp() throws Exception {
		this.server = HttpServerFactory.DEFAULT;
		server.start();

		this.redisServer = new RedisServer();
		redisServer.start();
	}

	@After
	public void tearDown() throws Exception {
		redisServer.stop();
		server.stop();
	}

	boolean isErrorCodeEqual(ErrorCode errorCode, HttpResponse response) throws IOException {
		String content = EntityUtils.toString(response.getEntity());
		ErrorResponse errorResponse = ObjectMapperUtil.toObject(content, ErrorResponse.class);
		return errorResponse.getCode() == errorCode.getCode();
	}
}
