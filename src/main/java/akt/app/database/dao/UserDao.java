package akt.app.database.dao;

import akt.app.entity.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.jetbrains.annotations.Nullable;

/**
 * @author akt.
 */
public interface UserDao {

	long insertUserEntity(String name, double balance) throws IOException;

	@Nullable
	UserEntity getUserEntity(long entityId) throws IOException;

	boolean updateUserEntity(UserEntity entity) throws JsonProcessingException;


}
