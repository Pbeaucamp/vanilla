package bpm.android.vanilla.wrapper.dispatcher;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.android.vanilla.core.IAndroidVanillaManager;
import bpm.android.vanilla.core.IAndroidVanillaManager.ActionType;
import bpm.android.vanilla.core.beans.AndroidVanillaContext;
import bpm.android.vanilla.core.xstream.IXmlActionType;
import bpm.android.vanilla.core.xstream.XmlAction;
import bpm.android.vanilla.core.xstream.XmlArgumentsHolder;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.reporting.AndroidVanillaManager;

import com.thoughtworks.xstream.XStream;

public class ConnectionServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ComponentAndroidWrapper component;
	protected XStream xstream;
	
	public ConnectionServlet(ComponentAndroidWrapper component) {
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
			IAndroidVanillaManager manager = new AndroidVanillaManager(component);
			
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
				case CONNECT:
					actionResult = manager.connect((AndroidVanillaContext) args.getArguments().get(0));
					break;
				case GET_GROUP_REPOSITORY:
					actionResult = manager.getGroupsAndRepositories((AndroidVanillaContext) args.getArguments().get(0));
					break;
				case GET_CONTEXT_WITH_PUBLIC_GROUP_AND_REPOSITORY:
					actionResult = manager.getContextWithPublicGroupAndRepository((AndroidVanillaContext) args.getArguments().get(0));
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
			component.getLogger().error(e.getMessage(), e);
		
			resp.getWriter().write("<error>" + e.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}
}
