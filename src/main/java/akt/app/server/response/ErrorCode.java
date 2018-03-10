package akt.app.server.response;

/**
 * @author akt.
 */
public enum ErrorCode {
	INVALID_HEADER(100),
	INVALID_INPUT_PARAMETER(101),
	NEGATIVE_BALANCE(102),
	TRANSFER_ON_SAME_ACCOUNT(103),
	CONCURRENT_ACCESS(104),
	INVALID_ACCOUNT(105),
	NO_MONEY_ON_ACCOUNT(106)
	;

	private final int code;

	ErrorCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
