package akt.app.server.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author akt.
 */
@JsonRootName("transferMoney")
public class TransferMoneyRequest {

	@JsonProperty("fromAccount")
	private final long fromAccount;
	@JsonProperty("toAccount")
	private final long toAccount;
	@JsonProperty("amount")
	private final double amount;

	public TransferMoneyRequest(@JsonProperty(value = "fromAccount", required = true) long fromAccount,
		@JsonProperty(value = "toAccount", required = true) long toAccount,
		@JsonProperty(value = "amount", required = true) double amount) {
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.amount = amount;
	}

	public long getFromAccount() {
		return fromAccount;
	}

	public long getToAccount() {
		return toAccount;
	}

	public double getAmount() {
		return amount;
	}
}
