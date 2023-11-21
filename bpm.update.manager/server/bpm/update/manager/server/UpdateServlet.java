package bpm.update.manager.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.update.manager.api.IUpdateManager.ActionType;
import bpm.update.manager.api.beans.UpdateInformations;
import bpm.update.manager.api.xstream.IXmlActionType;
import bpm.update.manager.api.xstream.XmlAction;
import bpm.update.manager.api.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class UpdateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private XStream xstream;
	
	public UpdateServlet() throws ServletException {
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		xstream = new XStream();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof IXmlActionType)){
				throw new Exception("ActionType not a UpdateManager");
			}
			
			ActionType type = (ActionType) action.getActionType();
			UpdateManager manager = getManager();

			Object actionResult = null;
			try{
				switch (type) {
				case HAS_UPDATE:
					actionResult = manager.hasUpdate();
					break;
				case UPDATE:
					manager.updateApplication((Boolean) args.getArguments().get(0), (UpdateInformations) args.getArguments().get(1));
					break;
				case RESTART_SERVER:
					actionResult = manager.restartServer();
					break;
				}
			}catch(Exception ex){
				throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();
		} catch (Exception e) {
			resp.getWriter().write("<error>" + e.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}

	private UpdateManager getManager() throws ServiceException {
		return ApplicationManager.getInstance().getUpdateManager();
	}
}
