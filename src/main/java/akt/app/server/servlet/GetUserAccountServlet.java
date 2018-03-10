package akt.app.server.servlet;

import akt.app.entity.UserEntity;
import akt.app.server.ServletConstant;
import akt.app.server.response.EmptyResponse;
import akt.app.server.response.ErrorCode;
import akt.app.server.response.ErrorResponse;
import akt.app.service.UserService;
import akt.app.util.ObjectMapperUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.Nullable;

/**
 * @author akt.
 */
public class GetUserAccountServlet extends HttpServlet {

	private static final long serialVersionUID = 3936317119242715787L;

	private final static String USER_ID_PARAMETER = "id";
	private final UserService userService;

	public GetUserAccountServlet(UserService userService) {
		this.userService = userService;
	}


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		Long entityId = getIdParameter(request);

		if (entityId != null) {
			UserEntity userEntity = userService.getUser(entityId);
			if (userEntity != null) {
				response.setContentType(ServletConstant.JSON_CONTENT_TYPE);
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().write(ObjectMapperUtil.toString(userEntity));
			} else {
				sendErrorMessage(ObjectMapperUtil.toString(new EmptyResponse()), response);
			}
		} else {
			ErrorResponse errorResponse = new ErrorResponse("id parameter should be long type and not empty",
				ErrorCode.INVALID_INPUT_PARAMETER.getCode());
			sendErrorMessage(ObjectMapperUtil.toString(errorResponse), response);
		}
	}

	@Nullable
	private Long getIdParameter(HttpServletRequest request) {
		String parameterStr = request.getParameter(USER_ID_PARAMETER);
		Long entityId = null;
		try {
			entityId = Long.parseLong(parameterStr);
		} catch (NumberFormatException ignored) {
		}
		return entityId;
	}

	private void sendErrorMessage(String message, HttpServletResponse response) throws IOException {
		response.setContentType(ServletConstant.JSON_CONTENT_TYPE);
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.getWriter().write(message);
	}


}
