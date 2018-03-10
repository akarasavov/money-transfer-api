package akt.app.server.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author akt.
 */
@JsonRootName("addUser")
public class AddUserRequest {

	@JsonProperty("name")
	private final String name;
	@JsonProperty("balance")
	private final double balance;

	public AddUserRequest(@JsonProperty(value = "name", required = true) String name,
		@JsonProperty(value = "balance", required = true) double balance) {
		this.name = name;
		this.balance = balance;
	}

	public String getName() {
		return name;
	}

	public double getBalance() {
		return balance;
	}
}
