package akt.app.server.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author akt.
 */

@JsonRootName(value = "error")
public class ErrorResponse {

	@JsonProperty(value = "error_message")
	private final String message;

	@JsonProperty(value = "error_code")
	private final int code;

	public ErrorResponse(@JsonProperty(value = "error_message", required = true) String message,
		@JsonProperty(value = "error_code", required = true) int code) {
		this.message = message;
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public int getCode() {
		return code;
	}
}
