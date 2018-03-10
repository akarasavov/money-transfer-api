package akt.app.server;

/**
 * @author akt.
 */
public interface ServletConstant {

	interface Path {
		String CREATE_ACCOUNT_PATH = "/account/create";
		String GET_ACCOUNT_PATH = "/account/get";
		String TRANSFER_MONEY_PATH = "/transfer";
	}

	String JSON_CONTENT_TYPE = "application/json";


}
