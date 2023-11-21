package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.IVanillaAccessRequestManager;
import bpm.vanilla.platform.core.beans.AccessRequest;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.runtime.components.VanillaAccessRequestManager;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class VanillaAccessRequestServlet extends AbstractComponentServlet{

	private static final long serialVersionUID = -2295221395480298601L;
	
	public VanillaAccessRequestServlet(IVanillaComponentProvider componentProvider, IVanillaLogger logger){
		this.logger = logger;
		this.component = componentProvider;
	}
	
	@Override
	public void init() throws ServletException {
		logger.info("Initializing VanillaAccessRequestServlet...");
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
			
			if (!(action.getActionType() instanceof IVanillaAccessRequestManager.ActionType)){
				throw new Exception("ActionType not a IVanillaAccessRequestManager.ActionType");
			}
			
			IVanillaAccessRequestManager.ActionType type = (IVanillaAccessRequestManager.ActionType)action.getActionType();
			
			log(type, ((VanillaAccessRequestManager)component.getVanillaAccessRequestComponent()).getComponentName(), req);
			
			try{
				switch (type) {
				case ADD_REQUEST:
					addAccessRequest(args);
					break;
				case UPDATE_REQUESTS:
					updateAccessRequest(args);
					break;
				case DELETE_REQUEST:
					deleteAccessRequest(args);
					break;
				case LIST_ADMIN_ALL_REQUESTS:
					actionResult = listAdminAllAccessRequest();
					break;
				case LIST_ADMIN_PENDING_REQUESTS:
					actionResult = listAdminPendingAccessRequest();
					break;
				case LIST_USER_ALL_REQUESTS:
					actionResult = listUserAllAccessRequest(args);
					break;
				case LIST_USER_PENDING_REQUESTS:
					actionResult = listUserPendingAccessRequest(args);
					break;
				case LIST_USER_ALL_DEMANDS:
					actionResult = listUserAllDemands(args);
					break;
				case LIST_USER_PENDING_DEMANDS:
					actionResult = listUserPendingDemands(args);
					break;
				case APPROVE_REQUEST:
					approveAccessRequest(args);
					break;
				case REFUSE_REQUEST:
					refuseAccessRequest(args);
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

	private void addAccessRequest(XmlArgumentsHolder args) throws Exception {
		component.getVanillaAccessRequestComponent().addAccessRequest((AccessRequest) args.getArguments().get(0));
	}
	
	@SuppressWarnings("unchecked")
	private void updateAccessRequest(XmlArgumentsHolder args) throws Exception {
		component.getVanillaAccessRequestComponent().updateAccessRequest((List<AccessRequest>) args.getArguments().get(0));
	}
	
	private void deleteAccessRequest(XmlArgumentsHolder args) throws Exception {
		component.getVanillaAccessRequestComponent().deleteAccessRequest((AccessRequest) args.getArguments().get(0));	
	}
	
	private List<AccessRequest> listAdminPendingAccessRequest() throws Exception {
		return component.getVanillaAccessRequestComponent().listAdminPendingAccessRequest();
	}
	
	
	private List<AccessRequest> listAdminAllAccessRequest() throws Exception {
		return component.getVanillaAccessRequestComponent().listAdminAllAccessRequest();
	}
	
	
	private List<AccessRequest> listUserPendingAccessRequest(XmlArgumentsHolder args) throws Exception {
		return component.getVanillaAccessRequestComponent()
			.listUserPendingAccessRequest((Integer) args.getArguments().get(0));
	}
	
	
	private List<AccessRequest> listUserAllAccessRequest(XmlArgumentsHolder args) throws Exception {
		return component.getVanillaAccessRequestComponent()
			.listUserAllAccessRequest((Integer) args.getArguments().get(0));
	}
	
	private List<AccessRequest> listUserPendingDemands(XmlArgumentsHolder args) throws Exception {
		return component.getVanillaAccessRequestComponent()
			.listUserPendingAccessDemands((Integer) args.getArguments().get(0));
	}
	
	private List<AccessRequest> listUserAllDemands(XmlArgumentsHolder args) throws Exception {
		return component.getVanillaAccessRequestComponent()
			.listUserAllAccessDemands((Integer) args.getArguments().get(0));
	}

	private void approveAccessRequest(XmlArgumentsHolder args) throws Exception {
		component.getVanillaAccessRequestComponent().approveAccessRequest(
				((AccessRequest) args.getArguments().get(0)));
	}
	
	private void refuseAccessRequest(XmlArgumentsHolder args) throws Exception {
		component.getVanillaAccessRequestComponent().refuseAccessRequest(
				((AccessRequest) args.getArguments().get(0)));
	}
}
