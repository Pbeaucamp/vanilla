package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IUnitedOlapPreloadManager;
import bpm.vanilla.platform.core.beans.UOlapPreloadBean;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.runtime.components.UnitedOlapPreloadManager;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class VanillaUnitedOlapPreloadServlet  extends AbstractComponentServlet{
	
	
	public VanillaUnitedOlapPreloadServlet(IVanillaComponentProvider componentProvider, IVanillaLogger logger){
		this.logger = logger;
		this.component = componentProvider;
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("Initializing UnitedOlapPreloadServlet...");

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
			
			if (!(action.getActionType() instanceof IUnitedOlapPreloadManager.ActionType)){
				throw new Exception("ActionType not a IRepositoryManager");
			}
			
			IUnitedOlapPreloadManager.ActionType type = (IUnitedOlapPreloadManager.ActionType)action.getActionType();
			
			log(type, ((UnitedOlapPreloadManager)component.getUnitedOlapPreloadManager()).getComponentName(), req);
			
			try{
				switch (type) {
				case ADD_PRELOAD:
					addPreload(args);
					break;
				case DEL_PRELOAD:
					delPreload(args);
					break;
				case LIST_PRELOAD:
					actionResult = listPreload(args);
					break;
				
				
				}
			}catch(Exception ex){
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

	private void addPreload(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IUnitedOlapPreloadManager.class.getMethod("addPreload", UOlapPreloadBean.class), args);
		component.getUnitedOlapPreloadManager().addPreload((UOlapPreloadBean)args.getArguments().get(0));

	}
	
	private void delPreload(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IUnitedOlapPreloadManager.class.getMethod("removePreload", UOlapPreloadBean.class), args);
		component.getUnitedOlapPreloadManager().removePreload((UOlapPreloadBean)args.getArguments().get(0));

	}
	
	private Object listPreload(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IUnitedOlapPreloadManager.class.getMethod("getPreloadForIdentifier", IObjectIdentifier.class), args);
		return component.getUnitedOlapPreloadManager().getPreloadForIdentifier((IObjectIdentifier)args.getArguments().get(0));

	}

		
}
