package akt.app.server.servlet;

import akt.app.server.ServletConstant;
import akt.app.server.exception.TransferMoneyException;
import akt.app.server.request.TransferMoneyRequest;
import akt.app.server.response.ErrorCode;
import akt.app.server.response.ErrorResponse;
import akt.app.service.UserService;
import akt.app.util.ObjectMapperUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

/**
 * @author akt.
 */
public class TransferMoneyServlet extends HttpServlet {

	private static final long serialVersionUID = -3982980987929967768L;
	private final UserService userService;

	public TransferMoneyServlet(UserService userService) {
		this.userService = userService;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		String header = request.getHeader("Content-type");
		if (header == null || !header.equals(ServletConstant.JSON_CONTENT_TYPE)) {
			response.getWriter().write(ObjectMapperUtil.toString(new ErrorResponse("Header is not " +
				ServletConstant.JSON_CONTENT_TYPE, ErrorCode.INVALID_HEADER.getCode())));
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		TransferMoneyRequest transferMoneyRequest = readTransferMoneyRequest(request);
		if (transferMoneyRequest == null) {
			response.getWriter().write(ObjectMapperUtil.toString(new ErrorResponse("Client didn't pass "
				+ "parameter for transferring money", ErrorCode.INVALID_INPUT_PARAMETER.getCode())));
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			try {
				userService.transferMoney(transferMoneyRequest.getFromAccount(), transferMoneyRequest.getToAccount(),
					transferMoneyRequest.getAmount());
				response.setContentType(ServletConstant.JSON_CONTENT_TYPE);
				response.setStatus(HttpServletResponse.SC_CREATED);
			} catch (TransferMoneyException exception) {
				response.getWriter().write(ObjectMapperUtil.toString(new ErrorResponse(exception.getMessage(),
					exception.getErrorCode().getCode())));
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
	}

	@Nullable
	private TransferMoneyRequest readTransferMoneyRequest(HttpServletRequest request) {
		try {
			String content = IOUtils.toString(request.getReader());
			return ObjectMapperUtil.toObject(content, TransferMoneyRequest.class);
		} catch (IOException ignored) {

		}
		return null;
	}

}
