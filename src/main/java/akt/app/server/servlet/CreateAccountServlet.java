package akt.app.server.servlet;

import akt.app.entity.UserEntity;
import akt.app.server.ServletConstant;
import akt.app.server.request.AddUserRequest;
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
public class CreateAccountServlet extends HttpServlet {

	private static final long serialVersionUID = -714559582156319640L;
	private final UserService userService;

	public CreateAccountServlet(UserService userService) {
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
		AddUserRequest addUserRequest = readAddUserRequest(request);
		if (addUserRequest == null) {
			response.getWriter().write(ObjectMapperUtil.toString(new ErrorResponse("Client didn't pass "
				+ "parameter for creating new account", ErrorCode.INVALID_INPUT_PARAMETER.getCode())));
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else if (addUserRequest.getBalance() < 0) {
			response.getWriter().write(ObjectMapperUtil.toString(new ErrorResponse("Balance can't be negative",
				ErrorCode.NEGATIVE_BALANCE.getCode())));
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			UserEntity userEntity = userService.addUser(addUserRequest.getName(), addUserRequest.getBalance());
			response.setContentType(ServletConstant.JSON_CONTENT_TYPE);
			response.setStatus(HttpServletResponse.SC_CREATED);
			response.getWriter().write(ObjectMapperUtil.toString(userEntity));
		}
	}

	@Nullable
	private AddUserRequest readAddUserRequest(HttpServletRequest request) {
		try {
			String content = IOUtils.toString(request.getReader());
			return ObjectMapperUtil.toObject(content, AddUserRequest.class);
		} catch (IOException ignored) {

		}
		return null;
	}

}
