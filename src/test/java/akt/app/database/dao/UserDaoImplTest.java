package akt.app.database.dao;

import akt.app.entity.UserEntity;
import akt.app.server.ServerAddress;
import java.io.IOException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.embedded.RedisServer;

/**
 * @author akt.
 */
public class UserDaoImplTest {

	private UserDaoImpl userDao;
	private RedisServer redisServer;

	@Before
	public void setUp() throws Exception {
		this.redisServer = new RedisServer();
		redisServer.start();

		this.userDao = new UserDaoImpl(ServerAddress.DEFAULT_REDIS_HOST, ServerAddress.DEFAULT_REDIS_PORT);
	}

	@Test
	public void insertEntity() throws IOException {
		String name = "test";
		double balance = 2.3;
		long id = userDao.insertUserEntity(name, balance);
		UserEntity userAccount = userDao.getUserEntity(id);
		Assert.assertEquals(userAccount.getName(), name);
		Assert.assertTrue(userAccount.getBalance() == balance);
	}



	@After
	public void tearDown() throws Exception {
		redisServer.stop();

	}

}