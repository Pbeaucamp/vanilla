package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.IGlobalManager;
import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

public class GlobalManagerServlet extends AbstractComponentServlet {

	private static final long serialVersionUID = 1L;
	
	private IGlobalManager component;
	private XStream xstream = new XStream();
	
	public GlobalManagerServlet(IVanillaComponentProvider component) {
		this.component = component.getGlobalManager();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try{
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if (action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}

			if (!(action.getActionType() instanceof IGlobalManager.ActionType)) {
				throw new Exception("ActionType not a Archive manager action");
			}
			
			IGlobalManager.ActionType type = (IGlobalManager.ActionType) action.getActionType();
			
			switch(type){
			case MANAGE_ITEM:
				actionResult = component.manageItem((Serializable) args.getArguments().get(0), (ManageAction) args.getArguments().get(1));
				break;
			default:
				throw new Exception("Unknown action " + type.name());
			}
				
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			
			resp.getWriter().close();
		}catch(Exception ex){
			ex.printStackTrace();
			resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, ex.getMessage());
		}
	}

}
