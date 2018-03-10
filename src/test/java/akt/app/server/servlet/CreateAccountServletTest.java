package akt.app.server.servlet;

import akt.app.entity.UserEntity;
import akt.app.server.ServletConstant;
import akt.app.server.TestConstrain;
import akt.app.server.request.AddUserRequest;
import akt.app.server.response.ErrorCode;
import akt.app.server.response.ErrorResponse;
import akt.app.util.ObjectMapperUtil;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author akt.
 */
public class CreateAccountServletTest extends AbstractServletTest {

	@Test
	public void createAccount() throws Exception {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(TestConstrain.DEFAULT_HOST + ServletConstant.Path.CREATE_ACCOUNT_PATH);
		postRequest.setHeader("Content-type", ServletConstant.JSON_CONTENT_TYPE);

		String accountName = "alex";
		int accountBalance = 200;
		String addUserJson = ObjectMapperUtil.toString(new AddUserRequest(accountName, accountBalance));
		postRequest.setEntity(new StringEntity(addUserJson));

		HttpResponse response = httpClient.execute(postRequest);
		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpServletResponse.SC_CREATED);

		UserEntity responseEntity = ObjectMapperUtil
			.toObject(EntityUtils.toString(response.getEntity()), UserEntity.class);

		Assert.assertTrue(responseEntity.getName().equals(accountName));
		Assert.assertTrue(responseEntity.getBalance() == accountBalance);
	}

	@Test
	public void invalidContentType() throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(TestConstrain.DEFAULT_HOST + ServletConstant.Path.CREATE_ACCOUNT_PATH);
		HttpResponse response = httpClient.execute(postRequest);
		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpServletResponse.SC_BAD_REQUEST);
		Assert.assertTrue(isErrorCodeEqual(ErrorCode.INVALID_HEADER, response));

	}

	@Test
	public void invalidContentEntity() throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(TestConstrain.DEFAULT_HOST + ServletConstant.Path.CREATE_ACCOUNT_PATH);
		postRequest.setHeader("Content-type", ServletConstant.JSON_CONTENT_TYPE);

		String addUserJson = ObjectMapperUtil.toString(new ErrorResponse("test", 12));
		postRequest.setEntity(new StringEntity(addUserJson));
		HttpResponse response = httpClient.execute(postRequest);
		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpServletResponse.SC_BAD_REQUEST);
		Assert.assertTrue(isErrorCodeEqual(ErrorCode.INVALID_INPUT_PARAMETER, response));
	}
}