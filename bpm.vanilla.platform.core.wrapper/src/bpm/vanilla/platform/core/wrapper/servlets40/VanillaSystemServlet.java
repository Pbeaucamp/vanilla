package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.IVanillaSystemManager;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.beans.VanillaSetup;
import bpm.vanilla.platform.core.beans.Variable;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.components.system.MailConfig;
import bpm.vanilla.platform.core.runtime.components.VanillaSystemManager;
import bpm.vanilla.platform.core.wrapper.dispatcher.StartStopComponent;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class VanillaSystemServlet extends AbstractComponentServlet{
	
	public VanillaSystemServlet(IVanillaComponentProvider componentProvider, IVanillaLogger logger){
		this.logger = logger;
		this.component = componentProvider;
	}
	
	@Override
	public void init() throws ServletException {
		logger.info("Initializing VanillaSystemServlet...");
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
			
			if (!(action.getActionType() instanceof IVanillaSystemManager.ActionType)){
				throw new Exception("ActionType not a IVanillaSystemManager.ActionType");
			}
			
			IVanillaSystemManager.ActionType type = (IVanillaSystemManager.ActionType)action.getActionType();
			if(!(type == IVanillaSystemManager.ActionType.FIND_SESSION)) {
				log(type, ((VanillaSystemManager)component.getSystemManager()).getComponentName(), req);
			}
			
			try{
				switch (type) {
				case ADD_VARIABLE:
					actionResult = addVariable(args);
					break;
				case DEL_VARIABLE:
					delVariable(args);
					break;
				case GET_VARIABLE:
					actionResult = getVariable(args);
					break;
				case LIST_VARIABLES:
					actionResult = listVariables(args);
					break;
				case UPDATE_VARIABLE:
					updateVariable(args);
					break;
					
				case FIND_SESSION:
					actionResult = findSession(args);
					break;
				case FIND_SESSION_BY_USER_PASS:
					actionResult = findSession(args);
					break;
				case LIST_SESSIONS:
					actionResult = listSessions(args);
					break;
				case LOGIN:
					actionResult = login(args);
					break;
				case LOGOUT:				
					logout(args);
					break;
					
				case GET_VANILLA_SETUP:
					actionResult = getVanillaSetup(args);
					break;
				case UPDATE_VANILLA_SETUP:
					updateVanillaSetup(args);
					break;
					
				case GET_VANILLA_VERSION:
					actionResult = getVanillaVersion(args);
					break;
					
				case LIST_NODES:
					//listNodes();
//					throw new Exception(type + " is not supported anymore");
					actionResult = listNodes(args);
					break;
				case LIST_NODES_BY_TYPE:
//					throw new Exception(type + " is not supported anymore");
					actionResult = listNodesByType(args);
					break;
				case NODE_REGISTRATION:
//					throw new Exception(type + " is not supported anymore");
					registerNode(args);
					break;
				case NODE_RELEASE:
//					throw new Exception(type + " is not supported anymore");
					releaseNode(args);
					break;
				case NODE_UPDATE:
//					throw new Exception(type + " is not supported anymore");
					updateNode(args);
					break;
				case SEND_EMAIL:
					actionResult = sendEmail(args);
					break;
				case START_NODE:
					startNodeComponent(args);
					break;
				case STOP_NODE:
					stopNodeComponent(args);
					break;
					
				case GET_VANILLA_LOCALES:
					actionResult = getVanillaLocale(args);
					break;
					
				case UPDATE_SESSION:
					component.getSystemManager().updateSession((VanillaSession)args.getArguments().get(0), (String)args.getArguments().get(1));
					break;
				case PING:
					actionResult = ping(args);
					break;
				default :
					throw new Exception("Unsupported action with type : " + type);
				}
			}catch(Exception ex){
				throw new ActionException("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();	
			
		}catch(Throwable ex){
			ex.printStackTrace();
			logger.error(ex.getMessage(), ex);
			
			//TODO : send error
		}
	}

	private void updateNode(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSystemManager.class.getMethod("updateServerNode", Server.class), args);
		component.getSystemManager().updateServerNode((Server)args.getArguments().get(0));

	}

	private void releaseNode(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSystemManager.class.getMethod("unregisterServerNode", Server.class), args);
		component.getSystemManager().unregisterServerNode((Server)args.getArguments().get(0));

	}
	
	private void registerNode(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSystemManager.class.getMethod("registerServerNode", Server.class), args);
		component.getSystemManager().registerServerNode((Server)args.getArguments().get(0));

	}

	private Object listNodesByType(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSystemManager.class.getMethod("getServerNodesByType", VanillaSetup.class), args);
		return component.getSystemManager().getServerNodesByType((String)args.getArguments().get(0), (Boolean) args.getArguments().get(1));
	}

	private Object listNodes(XmlArgumentsHolder args)  throws Exception{
		return component.getSystemManager().getServerNodes((Boolean) args.getArguments().get(0));
	}

	private Object getVanillaVersion(XmlArgumentsHolder args) throws Exception{
		return component.getSystemManager().findLastVersion();
	}

	private void updateVanillaSetup(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSystemManager.class.getMethod("updateVanillaSetup", VanillaSetup.class), args);
		component.getSystemManager().updateVanillaSetup((VanillaSetup)args.getArguments().get(0));
	}

	private Object getVanillaSetup(XmlArgumentsHolder args) throws Exception{
		return component.getSystemManager().getVanillaSetup();
	}
	
	private Object getVanillaLocale(XmlArgumentsHolder args) throws Exception {
		return component.getSystemManager().getVanillaLocales((Boolean)args.getArguments().get(0));
	}

	private Object getVariable(XmlArgumentsHolder args)throws Exception {
		argChecker.checkArguments(IVanillaSystemManager.class.getMethod("getVariable", String.class), args);
		return component.getSystemManager().getVariable((String)args.getArguments().get(0));

	}

	private void updateVariable(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSystemManager.class.getMethod("updateVariable", Variable.class), args);
		component.getSystemManager().updateVariable((Variable)args.getArguments().get(0));
	}
	private Object addVariable(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSystemManager.class.getMethod("addVariable", Variable.class), args);
		return component.getSystemManager().addVariable((Variable)args.getArguments().get(0));
	}
	
	private void delVariable(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSystemManager.class.getMethod("deleteVariable", Variable.class), args);
		component.getSystemManager().deleteVariable((Variable)args.getArguments().get(0));
	}
	
	private Object listVariables(XmlArgumentsHolder args) throws Exception{
		return component.getSystemManager().getVariables();
	}
	
	private Object login(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSystemManager.class.getMethod("createSession", User.class), args);
		return component.getSystemManager().createSession((User)args.getArguments().get(0));

	}
	private void logout(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IVanillaSystemManager.class.getMethod("deleteSession", String.class), args);
		component.getSystemManager().deleteSession((String)args.getArguments().get(0));

	}

	private Object ping(XmlArgumentsHolder args) throws Exception{
		return component.getSystemManager().ping();
	}
	
	private Object listSessions(XmlArgumentsHolder args) throws Exception{
		return component.getSystemManager().getActiveSessions();

	}
	private Object findSession(XmlArgumentsHolder args) throws Exception{
		try {
			argChecker.checkArguments(IVanillaSystemManager.class.getMethod("getSession", String.class), args);
		} catch (Exception e) {
			argChecker.checkArguments(IVanillaSystemManager.class.getMethod("getSession", String.class, String.class), args);
			return component.getSystemManager().getSession((String)args.getArguments().get(0), (String)args.getArguments().get(1));
		}
		return component.getSystemManager().getSession((String)args.getArguments().get(0));

	}
	private Object sendEmail(XmlArgumentsHolder args) throws Exception {
		logger.info("Received a sendEmail request, deserializing...");
		
		HashMap<String, byte[]> serializedAttachements = (HashMap<String, byte[]>) args.getArguments().get(1);
		
		HashMap<String, InputStream> attachements = new HashMap<String, InputStream>();
		
		for (String attachementName : serializedAttachements.keySet()) {
			byte[] data = Base64.decodeBase64(serializedAttachements.get(attachementName));
			ByteArrayInputStream stream = new ByteArrayInputStream(data);
			attachements.put(attachementName, stream);
		}
		logger.info("Delegating work to SystemManager...");
		try {
			String res = component.getSystemManager().sendEmail((MailConfig)args.getArguments().get(0), attachements);
			return res;
		} catch (Exception e) {
			logger.error("Failed to send email : " + e.getMessage());
			return "<error>" + e.getMessage() + "<error>";
		}
	}
	
//	private List<Node> listNodes() throws VanillaException {
//		logger.info("Received request to list all available nodes");
//		
//		
//		try {
////			List<IVanillaComponentIdentifier> components = 
////				component.getVanillaListenerComponent().getComponents();
//			return ((VanillaSystemManager)component.getSystemManager()).getNodes();
//		} catch (Exception e) {
//			String errMsg = "Failed to retrieve available components : " + e.getMessage();
//			logger.error(errMsg, e);
//			throw new VanillaException(errMsg);
//		}
//		
//		//return new ArrayList<Node>();
//	}
	
	/**
	 * not going through to the vanilla component, addressing the the server component straight 
	 */
	private void startNodeComponent(XmlArgumentsHolder args) throws Exception {
		//component.get
//		IVanillaComponentIdentifier ident = new VanillaComponentIdentifier(server.getComponentNature(), 
//				server.getUrl(), port, contextPath, id, status)
		StartStopComponent.startComponent((Server) args.getArguments().get(0));
		//throw new Exception("Not supported");
	}
	
	/**
	 * not going through to the vanilla component, addressing the the server component straight
	 */
	private void stopNodeComponent(XmlArgumentsHolder args) throws Exception {
		StartStopComponent.stopComponent((Server) args.getArguments().get(0));
		//throw new Exception("Not supported");
	}
}
