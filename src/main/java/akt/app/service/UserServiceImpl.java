package akt.app.service;

import akt.app.database.dao.UserDao;
import akt.app.entity.UserEntity;
import akt.app.server.exception.TransferMoneyException;
import akt.app.server.response.ErrorCode;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author akt.
 */
public class UserServiceImpl implements UserService {

	private final UserDao userDao;
	private Map<Long, Boolean> accountIdBlockedFlag = new ConcurrentHashMap<>();

	public UserServiceImpl(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public UserEntity addUser(String name, double balance) throws IOException {
		long id = userDao.insertUserEntity(name, balance);
		return userDao.getUserEntity(id);
	}

	@Override
	public UserEntity getUser(long id) throws IOException {
		return userDao.getUserEntity(id);
	}

	@Override
	public void transferMoney(long fromAccount, long toAccount, double amount) throws IOException {
		if (fromAccount == toAccount) {
			throw new TransferMoneyException("Account can't transfer money to itself",
				ErrorCode.TRANSFER_ON_SAME_ACCOUNT);
		}
		if (amount < 0) {
			throw new TransferMoneyException("Passed amount is negative", ErrorCode.NEGATIVE_BALANCE);
		}

		synchronized (this) {
			if (accountIdBlockedFlag.containsKey(fromAccount) ||
				accountIdBlockedFlag.containsKey(toAccount)) {
				throw new TransferMoneyException("Operation can't be executed, because "
					+ "previous transaction related with this account is not finished", ErrorCode.CONCURRENT_ACCESS);
			} else {
				accountIdBlockedFlag.put(fromAccount, true);
				accountIdBlockedFlag.put(toAccount, true);
			}
		}
		try {
			UserEntity fromEntity = userDao.getUserEntity(fromAccount);
			if (fromEntity == null) {
				throw new TransferMoneyException("Account with id" + fromAccount + " can't be found",
					ErrorCode.INVALID_ACCOUNT);
			}
			if (fromEntity.getBalance() < amount) {
				throw new TransferMoneyException(
					String.format("Account=%s don't have amount=%s", fromEntity.getId(), fromEntity.getBalance()),
					ErrorCode.NO_MONEY_ON_ACCOUNT);
			}
			UserEntity toEntity = userDao.getUserEntity(toAccount);
			if (toEntity == null) {
				throw new TransferMoneyException("Account with id" + toAccount + " can't be found",
					ErrorCode.INVALID_ACCOUNT);
			}

			fromEntity.setBalance(fromEntity.getBalance() - amount);
			toEntity.setBalance(toEntity.getBalance() + amount);

			userDao.updateUserEntity(fromEntity);
			userDao.updateUserEntity(toEntity);
		} finally {
			synchronized (this) {
				accountIdBlockedFlag.remove(fromAccount);
				accountIdBlockedFlag.remove(toAccount);
			}
		}
	}


}
