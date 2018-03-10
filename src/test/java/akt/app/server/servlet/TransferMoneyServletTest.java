package akt.app.server.servlet;

import akt.app.database.dao.UserDaoImpl;
import akt.app.entity.UserEntity;
import akt.app.server.ServerAddress;
import akt.app.server.ServletConstant;
import akt.app.server.ServletConstant.Path;
import akt.app.server.TestConstrain;
import akt.app.server.request.TransferMoneyRequest;
import akt.app.server.response.ErrorCode;
import akt.app.server.response.ErrorResponse;
import akt.app.service.UserServiceImpl;
import akt.app.util.ObjectMapperUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author akt.
 */
public class TransferMoneyServletTest extends AbstractServletTest {

	private UserServiceImpl userService;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		this.userService = new UserServiceImpl(new UserDaoImpl(ServerAddress.DEFAULT_REDIS_HOST,
			ServerAddress.DEFAULT_REDIS_PORT));
	}

	@Test
	public void transferMoney() throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(TestConstrain.DEFAULT_HOST + Path.TRANSFER_MONEY_PATH);
		postRequest.setHeader("Content-type", ServletConstant.JSON_CONTENT_TYPE);

		UserEntity firstEntity = userService.addUser("test", 2000);
		UserEntity secondEntity = userService.addUser("test1", 2000);

		String addUserJson = ObjectMapperUtil.toString(new TransferMoneyRequest(firstEntity.getId(),
			secondEntity.getId(), 200));
		postRequest.setEntity(new StringEntity(addUserJson));
		HttpResponse response = httpClient.execute(postRequest);

		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpServletResponse.SC_CREATED);
	}

	@Test
	public void concurrentSuccessTransfer() throws IOException {
		for (int i = 0; i < 10; i++) {
			userService.addUser("test", 2000);
		}

		List<HttpThread> threadList = new ArrayList<>();

		for (int i = 1; i < 10; i += 2) {
			threadList.add(new HttpThread(i, i + 1));
		}

		threadList.forEach(Thread::start);
		threadList.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		Optional<HttpThread> result = threadList.stream()
			.filter(httpThread -> httpThread.errorCode != -1 && httpThread.errorCode == ErrorCode.CONCURRENT_ACCESS
				.getCode())
			.findAny();
		Assert.assertFalse(result.isPresent());


	}

	@Test
	public void concurrentTransfer() throws IOException {
		userService.addUser("test", 2000);
		userService.addUser("test1", 2000);

		List<HttpThread> threadList = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			threadList.add(new HttpThread(1, 2));
		}

		threadList.forEach(Thread::start);
		threadList.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		Optional<HttpThread> result = threadList.stream()
			.filter(httpThread -> httpThread.errorCode != -1 && httpThread.errorCode == ErrorCode.CONCURRENT_ACCESS
				.getCode())
			.findAny();
		Assert.assertTrue(result.isPresent());


	}

	@Test
	public void transferIllegalAmount() throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(TestConstrain.DEFAULT_HOST + Path.TRANSFER_MONEY_PATH);
		postRequest.setHeader("Content-type", ServletConstant.JSON_CONTENT_TYPE);

		UserEntity firstEntity = userService.addUser("test", 2000);
		UserEntity secondEntity = userService.addUser("test1", 2000);

		String addUserJson = ObjectMapperUtil.toString(new TransferMoneyRequest(firstEntity.getId(),
			secondEntity.getId(), 100000));
		postRequest.setEntity(new StringEntity(addUserJson));
		HttpResponse response = httpClient.execute(postRequest);

		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpServletResponse.SC_BAD_REQUEST);
		Assert.assertTrue(isErrorCodeEqual(ErrorCode.NO_MONEY_ON_ACCOUNT, response));
	}

	@Test
	public void transferSameAccount() throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(TestConstrain.DEFAULT_HOST + Path.TRANSFER_MONEY_PATH);
		postRequest.setHeader("Content-type", ServletConstant.JSON_CONTENT_TYPE);

		UserEntity firstEntity = userService.addUser("test", 2000);

		String addUserJson = ObjectMapperUtil.toString(new TransferMoneyRequest(firstEntity.getId(),
			firstEntity.getId(), 500));
		postRequest.setEntity(new StringEntity(addUserJson));
		HttpResponse response = httpClient.execute(postRequest);

		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpServletResponse.SC_BAD_REQUEST);
		Assert.assertTrue(isErrorCodeEqual(ErrorCode.TRANSFER_ON_SAME_ACCOUNT, response));
	}

	@Test
	public void transferNegativeBalance() throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(TestConstrain.DEFAULT_HOST + Path.TRANSFER_MONEY_PATH);
		postRequest.setHeader("Content-type", ServletConstant.JSON_CONTENT_TYPE);

		UserEntity firstEntity = userService.addUser("test", 2000);
		UserEntity secondEntity = userService.addUser("test1", 2000);

		String addUserJson = ObjectMapperUtil.toString(new TransferMoneyRequest(firstEntity.getId(),
			secondEntity.getId(), -500));
		postRequest.setEntity(new StringEntity(addUserJson));
		HttpResponse response = httpClient.execute(postRequest);

		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpServletResponse.SC_BAD_REQUEST);
		Assert.assertTrue(isErrorCodeEqual(ErrorCode.NEGATIVE_BALANCE, response));
	}

	private class HttpThread extends Thread {

		private final long from;
		private final long to;
		volatile int errorCode = -1;
		volatile int statusCode;

		HttpThread(long from, long to) {
			this.from = from;
			this.to = to;
		}

		public void run() {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost postRequest = new HttpPost(TestConstrain.DEFAULT_HOST + Path.TRANSFER_MONEY_PATH);
			postRequest.setHeader("Content-type", ServletConstant.JSON_CONTENT_TYPE);
			try {
				String addUserJson = ObjectMapperUtil.toString(new TransferMoneyRequest(from, to, 2));
				postRequest.setEntity(new StringEntity(addUserJson));
				HttpResponse response = httpClient.execute(postRequest);
				statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpServletResponse.SC_BAD_REQUEST) {
					this.errorCode = ObjectMapperUtil
						.toObject(EntityUtils.toString(response.getEntity()), ErrorResponse.class).getCode();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}


}