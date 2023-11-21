package bpm.smart.runtime;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.smart.core.model.Constants;
import bpm.smart.runtime.security.AirSession;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.workflow.commons.remote.IAdminManager;
import bpm.workflow.commons.remote.IAdminManager.ActionType;

import com.thoughtworks.xstream.XStream;

public class AdminServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private SmartManagerComponent component;
	protected XStream xstream;
	
	public AdminServlet(SmartManagerComponent component) {
		this.component = component;
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		xstream = new XStream();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String locale = req.getHeader(Constants.HTTP_HEADER_LOCALE);
			Locale loc = locale != null ? new Locale(locale) : null;
			
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof IXmlActionType)){
				throw new Exception("ActionType not a IAndroidReportingManager");
			}
			
			ActionType type = (ActionType)action.getActionType();

			Object actionResult = null;
			try{
				switch (type) {
				case LOGIN:
					actionResult = login((String) args.getArguments().get(0), (String) args.getArguments().get(1), loc);
					break;
				case CONNECT:
					actionResult = connect((User) args.getArguments().get(0), req, resp, loc);
					break;
				case LOGOUT:
					logout(req);
					break;
//				case CHECK_UDPATES:
//					actionResult = checkUpdates(req);
//					break;
				}
			}catch(Exception ex){
				throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();
		} catch (Exception e) {
			e.printStackTrace();
			
			component.getLogger().error(e.getMessage(), e);
		
			resp.getWriter().write("<error>" + e.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}

	private User login(String login, String password, Locale loc) throws Exception {
		IAdminManager adminManager = new AdminManager(component);
		return adminManager.login(login, password, loc != null ? loc.getLanguage() : "");
	}

	private String connect(User user, HttpServletRequest req, HttpServletResponse resp, Locale loc) throws Exception {
		AirSession session = new AirSession(component, user, loc);
		session = component.getSessionHolder().createSession(session, user);
		req.setAttribute(Constants.HTTP_HEADER_SESSION_ID, session.getSessionId());
		req.getSession().setAttribute(Constants.HTTP_HEADER_SESSION_ID, session.getSessionId());
		
		resp.addHeader(Constants.HTTP_HEADER_SESSION_ID, session.getSessionId());
		
		return session.getSessionId();
	}

	private void logout(HttpServletRequest req) throws Exception {
		String sessionId = req.getHeader(Constants.HTTP_HEADER_SESSION_ID);
		AirSession session = component.getSessionHolder().getSession(sessionId);
		
		session.getAdminManager().logout();
	}
}
