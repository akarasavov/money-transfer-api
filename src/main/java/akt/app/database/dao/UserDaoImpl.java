package akt.app.database.dao;

import akt.app.entity.UserEntity;
import akt.app.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

/**
 * @author akt.
 */
public class UserDaoImpl implements UserDao {

	private final static String ID = "id";
	private final String host;
	private final int port;

	public UserDaoImpl(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public long insertUserEntity(String name, double balance) throws JsonProcessingException {
		Jedis jedis = createJedis();
		Long id = jedis.incr(ID);
		UserEntity entity = new UserEntity(id, name, balance);
		String entityToStr = ObjectMapperUtil.toString(entity);
		jedis.set(String.valueOf(id), entityToStr);

		jedis.close();
		return id;
	}

	@NotNull
	private Jedis createJedis() {
		return new Jedis(host, port);
	}


	@Override
	public UserEntity getUserEntity(long entityId) throws IOException {
		Jedis jedis = createJedis();
		String entityStr = jedis.get(String.valueOf(entityId));
		jedis.close();
		if (entityStr != null) {
			return ObjectMapperUtil.toObject(entityStr, UserEntity.class);
		} else {
			return null;
		}
	}

	@Override
	public boolean updateUserEntity(UserEntity entity) throws JsonProcessingException {
		Jedis jedis = createJedis();
		String entityToStr = ObjectMapperUtil.toString(entity);
		jedis.set(String.valueOf(entity.getId()), entityToStr);
		jedis.close();
		return true;
	}

}
