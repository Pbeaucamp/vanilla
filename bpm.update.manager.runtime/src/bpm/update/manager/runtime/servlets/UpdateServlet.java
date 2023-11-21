package bpm.update.manager.runtime.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.update.manager.api.IRuntimeUpdateManager.ActionType;
import bpm.update.manager.api.xstream.IXmlActionType;
import bpm.update.manager.api.xstream.XmlAction;
import bpm.update.manager.api.xstream.XmlArgumentsHolder;
import bpm.update.manager.runtime.ComponentUpdateRuntime;
import bpm.update.manager.runtime.RuntimeUpdateManager;

import com.thoughtworks.xstream.XStream;

public class UpdateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private ComponentUpdateRuntime component;
	private XStream xstream;
	
	private RuntimeUpdateManager updateManager;
	
	public UpdateServlet(ComponentUpdateRuntime component) throws ServletException {
		this.component = component;
		this.updateManager = new RuntimeUpdateManager(component);
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		xstream = new XStream();
	}
	
	@Override
	@SuppressWarnings("unchecked")
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
			
			ActionType type = (ActionType)action.getActionType();

			try{
				switch (type) {
				case UNDEPLOY:
					updateManager.undeploy((List<String>) args.getArguments().get(0));
					break;
				case SHUTDOWN:
					updateManager.shutdownOsgi();
					break;
				}
			}catch(Exception ex){
				throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			resp.getWriter().close();
		} catch (Exception e) {
			component.getLogger().error(e.getMessage(), e);
		
			resp.getWriter().write("<error>" + e.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}
}
