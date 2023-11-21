package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.IExternalManager;
import bpm.vanilla.platform.core.IResourceManager;
import bpm.vanilla.platform.core.beans.resources.D4CItem.TypeD4CItem;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

public class ExternalManagerServlet extends AbstractComponentServlet {

	private static final long serialVersionUID = 1L;
	
	private IExternalManager component;
	private XStream xstream = new XStream();
	
	public ExternalManagerServlet(IVanillaComponentProvider component) {
		this.component = component.getExternalManager();
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

			if (!(action.getActionType() instanceof IExternalManager.ActionType)) {
				throw new Exception("ActionType not a Archive manager action");
			}
			
			IExternalManager.ActionType type = (IExternalManager.ActionType) action.getActionType();
			
			switch(type){
			case GET_D4C_DEFINITIONS:
				actionResult = component.getD4CDefinitions();
				break;
			case GET_D4C_ITEMS:
				actionResult = component.getD4cItems((Integer) args.getArguments().get(0), (TypeD4CItem) args.getArguments().get(1));
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
