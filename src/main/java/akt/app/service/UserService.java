package akt.app.service;

import akt.app.entity.UserEntity;
import java.io.IOException;
import org.jetbrains.annotations.Nullable;

/**
 * @author akt.
 */
public interface UserService {

	UserEntity addUser(String name, double balance) throws IOException;

	@Nullable
	UserEntity getUser(long id) throws IOException;

	void transferMoney(long fromAccount, long toAccount, double amount) throws IOException;

}
