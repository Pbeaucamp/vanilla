package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.UserRep;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.runtime.components.RepositoryManager;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class RepositoryServlet extends AbstractComponentServlet{
	
	
	public RepositoryServlet(IVanillaComponentProvider componentProvider, IVanillaLogger logger){
		this.logger = logger;
		this.component = componentProvider;
	}
	
	@Override
	public void init() throws ServletException {
		logger.info("Initializing VanillaRepositoryServlet...");
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
			
			if (!(action.getActionType() instanceof IRepositoryManager.ActionType)){
				throw new Exception("ActionType not a IRepositoryManager");
			}
			
			IRepositoryManager.ActionType type = (IRepositoryManager.ActionType)action.getActionType();
			
			log(type, ((RepositoryManager)component.getRepositoryManager()).getComponentName(), req);
			
			try{
				switch (type) {
				case ADD_REP:
					addRepository(args);
					break;
				case ADD_USER_ACCESS:
					addAccessToRepository(args);
					break;
				case DEL_REP:
					delRepository(args);
					break;
				case DEL_USER_ACCESS:
					delAccessToRepository(args);
					break;
				case FIND_REP_ID:
					actionResult = getRepositoryById(args);
					break;
				case FIND_REP_NAME:
					actionResult = getRepositoryByName(args);
					break;
				case FIND_REP_URL:
					actionResult = getRepositoryByUrl(args);
					break;
				case FIND_USER_ACCESS:
					actionResult = getAccess(args);
					break;
				case FIND_USER_ACCESS_4_REP:
					actionResult = getAccess4Rep(args);
					break;
				case FIND_USER_ACCESS_4_USER:
					actionResult = getAccess4User(args);
					break;
				case FIND_USER_ACCESS_4_USER_NAME:
					actionResult = getAccess4UserName(args);
					break;
				case HAS_ACCESS:
					actionResult = hasAccess(args);
					break;
				case LIST_REP:
					actionResult = listRepositories(args);
					break;
				case UPDATE_REP:
					updateRepository(args);
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

	private void addRepository(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IRepositoryManager.class.getMethod("addRepository", Repository.class), args);
		component.getRepositoryManager().addRepository((Repository)args.getArguments().get(0));
	}
	private void delRepository(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IRepositoryManager.class.getMethod("deleteRepository", Repository.class), args);
		component.getRepositoryManager().deleteRepository((Repository)args.getArguments().get(0));
	}
	private void updateRepository(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IRepositoryManager.class.getMethod("updateRepository", Repository.class), args);
		component.getRepositoryManager().updateRepository((Repository)args.getArguments().get(0));
	}
	private Object listRepositories(XmlArgumentsHolder args) throws Exception{
		return component.getRepositoryManager().getRepositories();
	}
	private Object getRepositoryById(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IRepositoryManager.class.getMethod("getRepositoryById", int.class), args);
		return component.getRepositoryManager().getRepositoryById((Integer)args.getArguments().get(0));
	}
	private Object getRepositoryByName(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IRepositoryManager.class.getMethod("getRepositoryByName", String.class), args);
		return component.getRepositoryManager().getRepositoryByName((String)args.getArguments().get(0));
	}
	private Object getRepositoryByUrl(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IRepositoryManager.class.getMethod("getRepositoryFromUrl", String.class), args);
		return component.getRepositoryManager().getRepositoryFromUrl((String)args.getArguments().get(0));
	}
	
	private void addAccessToRepository(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IRepositoryManager.class.getMethod("addUserRep", UserRep.class), args);
		component.getRepositoryManager().addUserRep((UserRep)args.getArguments().get(0));
	}
	private void delAccessToRepository(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IRepositoryManager.class.getMethod("delUserRep", UserRep.class), args);
		component.getRepositoryManager().delUserRep((UserRep)args.getArguments().get(0));
	}
	
	private Object getAccess(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IRepositoryManager.class.getMethod("getUserRepById", int.class), args);
		return component.getRepositoryManager().getUserRepById((Integer)args.getArguments().get(0));
	}
	private Object getAccess4User(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IRepositoryManager.class.getMethod("getUserRepByUserId", int.class), args);
		return 	component.getRepositoryManager().getUserRepByUserId((Integer)args.getArguments().get(0));
	}
	private Object getAccess4UserName(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IRepositoryManager.class.getMethod("getUserRepositories", String.class), args);
		return 	component.getRepositoryManager().getUserRepositories((String)args.getArguments().get(0));
	}
	private Object getAccess4Rep(XmlArgumentsHolder args) throws Exception{
		
		argChecker.checkArguments(IRepositoryManager.class.getMethod("getUserRepByRepositoryId", int.class), args);
		return component.getRepositoryManager().getUserRepByRepositoryId((Integer)args.getArguments().get(0));
	}
	private Object hasAccess(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IRepositoryManager.class.getMethod("hasUserHaveAccess", int.class, int.class), args);
		return component.getRepositoryManager().hasUserHaveAccess((Integer)args.getArguments().get(0), (Integer)args.getArguments().get(1));
	}
	
}
