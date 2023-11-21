package bpm.gwt.aklabox.commons.server.security;

import bpm.document.management.core.xstream.SessionExpiredException;
import bpm.gwt.aklabox.commons.client.services.exceptions.SecurityException;
import bpm.gwt.aklabox.commons.client.services.exceptions.ServiceException;

public class ExceptionHelper {

	public static ServiceException getClientException(Exception exception, String message, boolean includeException) {
		if (exception instanceof SessionExpiredException
				|| exception instanceof bpm.aklabox.workflow.core.xstream.SessionExpiredException
				|| exception instanceof bpm.document.management.manager.core.xstream.SessionExpiredException) {
			return new SecurityException(SecurityException.ERROR_TYPE_SESSION_EXPIRED, "Session is expired.");
		}
		return includeException ? new ServiceException(message, exception) : new ServiceException(message + exception.getMessage());
	}
}
