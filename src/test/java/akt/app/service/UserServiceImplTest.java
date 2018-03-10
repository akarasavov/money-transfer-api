package akt.app.service;

import akt.app.database.dao.UserDaoImpl;
import akt.app.entity.UserEntity;
import akt.app.server.ServerAddress;
import akt.app.server.exception.TransferMoneyException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.embedded.RedisServer;

/**
 * @author akt.
 */
public class UserServiceImplTest {

	private RedisServer redisServer;

	@Before
	public void setUp() throws Exception {
		this.redisServer = new RedisServer();
		redisServer.start();
	}

	@After
	public void tearDown() throws Exception {
		redisServer.stop();
	}

	@Test
	public void transferMoney() throws IOException {
		UserService userService = new UserServiceImpl(new UserDaoImpl(ServerAddress.DEFAULT_REDIS_HOST,
			ServerAddress.DEFAULT_REDIS_PORT));
		UserEntity firstEntity = userService.addUser("first", 200);
		UserEntity secondEntity = userService.addUser("second", 100);
		userService.transferMoney(firstEntity.getId(), secondEntity.getId(), 100);

		UserEntity user = userService.getUser(firstEntity.getId());
		Assert.assertTrue(user.getBalance() == 100);
	}

	@Test
	public void concurrentTransferDifferentAccount() throws IOException, InterruptedException {
		UserService userService = new UserServiceImpl(new UserDaoImpl(ServerAddress.DEFAULT_REDIS_HOST,
			ServerAddress.DEFAULT_REDIS_PORT));
		userService.addUser("test", 200);
		userService.addUser("test2", 200);
		List<TransferMoneyThread> threads = new ArrayList<>();
		for (int i = 1; i < 100; i += 2) {
			threads.add(new TransferMoneyThread(1, userService));
		}

		threads.forEach(Thread::start);
		for (Thread thread : threads) {
			thread.join();
		}

		Assert.assertTrue(threads.stream().filter(TransferMoneyThread::isError).findFirst().isPresent());
	}

	@Test
	public void concurrentTransferSameAccount() throws IOException, InterruptedException {
		UserService userService = new UserServiceImpl(new UserDaoImpl(ServerAddress.DEFAULT_REDIS_HOST,
			ServerAddress.DEFAULT_REDIS_PORT));
		userService.addUser("test", 200);
		userService.addUser("test2", 200);
		List<TransferMoneyThread> threads = new ArrayList<>();
		for (int i = 0; i < 100; i += 2) {
			threads.add(new TransferMoneyThread(1, userService));
		}

		threads.forEach(Thread::start);
		for (Thread thread : threads) {
			thread.join();
		}

		Assert.assertTrue(threads.stream().filter(TransferMoneyThread::isError).findFirst().isPresent());
	}

	@Test(expected = TransferMoneyException.class)
	public void invalidAmount() throws IOException {
		UserService userService = new UserServiceImpl(new UserDaoImpl(ServerAddress.DEFAULT_REDIS_HOST,
			ServerAddress.DEFAULT_REDIS_PORT));
		UserEntity firstEntity = userService.addUser("first", 50);
		UserEntity secondEntity = userService.addUser("second", 100);
		userService.transferMoney(firstEntity.getId(), secondEntity.getId(), 100);
	}

	@Test(expected = TransferMoneyException.class)
	public void invalidAccount() throws IOException {
		UserService userService = new UserServiceImpl(new UserDaoImpl(ServerAddress.DEFAULT_REDIS_HOST,
			ServerAddress.DEFAULT_REDIS_PORT));
		UserEntity firstEntity = new UserEntity(20, "test", 200);
		UserEntity secondEntity = userService.addUser("second", 100);
		userService.transferMoney(firstEntity.getId(), secondEntity.getId(), 100);
	}

	@Test(expected = TransferMoneyException.class)
	public void sameAccount() throws IOException {
		UserService userService = new UserServiceImpl(new UserDaoImpl(ServerAddress.DEFAULT_REDIS_HOST,
			ServerAddress.DEFAULT_REDIS_PORT));
		UserEntity firstEntity = new UserEntity(1, "test", 200);
		UserEntity secondEntity = userService.addUser("second", 100);
		userService.transferMoney(firstEntity.getId(), secondEntity.getId(), 100);
	}

	private class TransferMoneyThread extends Thread {

		private int startIndex;
		private final UserService userService;
		private volatile boolean error;

		TransferMoneyThread(int startIndex, UserService userService) {
			this.startIndex = startIndex;
			this.userService = userService;
		}

		@Override
		public void run() {
			try {
				userService.transferMoney(startIndex, startIndex + 1, 100);
				error = true;
			} catch (IOException e) {
				error = true;
			} catch (IllegalArgumentException ignored) {

			}
		}

		boolean isError() {
			return error;
		}
	}

}