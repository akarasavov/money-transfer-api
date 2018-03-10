package akt.app.server.exception;

import akt.app.server.response.ErrorCode;
import org.jetbrains.annotations.NonNls;

/**
 * @author akt.
 */
public class TransferMoneyException extends RuntimeException {

	private static final long serialVersionUID = 7549081949504395944L;
	private final ErrorCode errorCode;

	public TransferMoneyException(@NonNls String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
