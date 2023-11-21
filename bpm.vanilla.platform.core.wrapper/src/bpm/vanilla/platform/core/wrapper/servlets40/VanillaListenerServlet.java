package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IVanillaComponentListenerService;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class VanillaListenerServlet  extends AbstractComponentServlet{
	
	public VanillaListenerServlet(IVanillaComponentProvider componentProvider, IVanillaLogger logger){
		this.logger = logger;
		this.component = componentProvider;
	}
	
	@Override
	public void init() throws ServletException {
		logger.info("Initializing VanillaListenerServlet...");
		super.init();
		

	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try{
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof IVanillaComponentListenerService.ActionType)){
				throw new Exception("ActionType not a IRepositoryManager");
			}
			
			IVanillaComponentListenerService.ActionType type = (IVanillaComponentListenerService.ActionType)action.getActionType();
			
			log(type, "bpm.vanilla.platform.core.runtime.components.VanillaListenerService", req);
			
			try{
				switch (type) {
				case ADD_LISTENER:
					addListener(args);
					break;
				case REMOVE_LISTENER:
					removeListener(args);
					break;
				case MOD_LISTENER:
					modifyListener(args);
					break;
				case FIRE_EVENT:
					fireEvent(args);
					break;
				case LIST_COMPONENTS:
					actionResult = listComponentIdentifiers(args);
					break;
				default :
					throw new Exception("Unsupported action type : " + type);
				}
			}catch(Exception ex){
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
				throw new ActionException("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();	
			
		}catch(Exception ex){
			logger.error(ex.getMessage(), ex);
			
			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}

	private void addListener(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaComponentListenerService.class.getMethod("registerVanillaComponent", IVanillaComponentIdentifier.class), args);
		component.getVanillaListenerComponent().registerVanillaComponent((IVanillaComponentIdentifier)args.getArguments().get(0));

	}
	
	private void removeListener(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaComponentListenerService.class.getMethod("unregisterVanillaComponent", IVanillaComponentIdentifier.class), args);
		component.getVanillaListenerComponent().unregisterVanillaComponent((IVanillaComponentIdentifier)args.getArguments().get(0));

	}
	
	private void modifyListener(XmlArgumentsHolder args) throws Exception{
		//argChecker.checkArguments(IVanillaComponentListenerService.class.getMethod("unregisterVanillaComponent", IVanillaComponentIdentifier.class), args);
		
		component.getVanillaListenerComponent().updateVanillaComponent(
				(IVanillaComponentIdentifier)args.getArguments().get(0));

	}
	
	private void fireEvent(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaComponentListenerService.class.getMethod("fireEvent", IVanillaEvent.class), args);
		component.getVanillaListenerComponent().fireEvent((IVanillaEvent)args.getArguments().get(0));

	}

	private Object listComponentIdentifiers(XmlArgumentsHolder args) throws Exception{
		
		return component.getVanillaListenerComponent().getRegisteredComponents((String)args.getArguments().get(0), false);

	}	
}
