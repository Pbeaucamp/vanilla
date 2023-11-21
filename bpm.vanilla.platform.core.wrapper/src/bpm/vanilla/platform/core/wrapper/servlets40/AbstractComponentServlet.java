package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.remote.impl.RemoteVanillaSystemManager;
import bpm.vanilla.platform.core.runtime.components.VanillaSystemManager;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.logging.IVanillaLogger;

import com.thoughtworks.xstream.XStream;

public class AbstractComponentServlet extends HttpServlet {

	protected IVanillaLogger logger;
	protected IVanillaComponentProvider component;
	protected XStream xstream;
	protected XStreamServletArgumentChecker argChecker = new XStreamServletArgumentChecker();

	@Override
	public void init() throws ServletException {
		xstream = new XStream();
		super.init();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	protected User extractUser(HttpServletRequest request) throws Exception{
		String sessionId = request.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		if (sessionId == null){
			sessionId = (String)request.getAttribute(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		}		
		VanillaSession session = component.getSystemManager().getSession(sessionId);
		return session.getUser();
	}
	
	public void log(IXmlActionType actionType, String componentId, HttpServletRequest request) throws Exception {
		User user = null;
		try {
			user = extractUser(request);
		} catch (Exception e) {
			return;
		}
		String ipAddress = request.getRemoteAddr();
		VanillaLogs log = new VanillaLogs(actionType.getLevel(), componentId, actionType.toString(), new Date(), user.getId(), ipAddress);
		component.getLoggingManager().addVanillaLog(log);
	}
	
}
