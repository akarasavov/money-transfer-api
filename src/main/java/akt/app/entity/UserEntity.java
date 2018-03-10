package akt.app.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author akt.
 */
public class UserEntity {

	@JsonProperty("id")
	private long id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("balance")
	private double balance;

	public UserEntity(@JsonProperty(value = "id", required = true) long id,
		@JsonProperty(value = "name", required = true) String name,
		@JsonProperty(value = "balance", required = true) double balance) {
		this.id = id;
		this.name = name;
		this.balance = balance;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		UserEntity that = (UserEntity) o;

		if (id != that.id) {
			return false;
		}
		return Double.compare(that.balance, balance) == 0 && (name != null ? name.equals(that.name)
			: that.name == null);

	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = (int) (id ^ (id >>> 32));
		result = 31 * result + (name != null ? name.hashCode() : 0);
		temp = Double.doubleToLongBits(balance);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
