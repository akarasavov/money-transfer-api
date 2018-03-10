package akt.app.server.servlet;

import akt.app.database.dao.UserDaoImpl;
import akt.app.entity.UserEntity;
import akt.app.server.ServerAddress;
import akt.app.server.ServletConstant;
import akt.app.server.TestConstrain;
import akt.app.service.UserService;
import akt.app.service.UserServiceImpl;
import akt.app.util.ObjectMapperUtil;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author akt.
 */
public class GetUserAccountServletTest extends AbstractServletTest {


	@Test
	public void getRealUser() throws IOException {
		UserService userService = new UserServiceImpl(new UserDaoImpl(ServerAddress.DEFAULT_REDIS_HOST,
			ServerAddress.DEFAULT_REDIS_PORT));
		UserEntity userEntity = userService.addUser("test", 200);

		HttpClient httpClient = HttpClientBuilder.create().build();
		String uri = buildUrl(userEntity.getId());
		HttpGet getRequest = new HttpGet(uri);
		HttpResponse response = httpClient.execute(getRequest);
		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpServletResponse.SC_OK);

		UserEntity responseEntity = ObjectMapperUtil
			.toObject(EntityUtils.toString(response.getEntity()), UserEntity.class);
		Assert.assertTrue(userEntity.equals(responseEntity));
	}

	@Test
	public void getWithoutIdParameter() throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet getRequest = new HttpGet(TestConstrain.DEFAULT_HOST + ServletConstant.Path.GET_ACCOUNT_PATH);
		HttpResponse response = httpClient.execute(getRequest);

		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void getOnNotRealAccount() throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		String uri = buildUrl(-1);
		HttpGet getRequest = new HttpGet(uri);
		HttpResponse response = httpClient.execute(getRequest);

		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpServletResponse.SC_NOT_FOUND);
	}

	@NotNull
	private String buildUrl(long id) {
		return TestConstrain.DEFAULT_HOST + ServletConstant.Path.GET_ACCOUNT_PATH + "?id=" + id;
	}
}