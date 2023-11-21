package bpm.vanilla.platform.core.remote.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.IVanillaSystemManager;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLocale;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.beans.VanillaSetup;
import bpm.vanilla.platform.core.beans.VanillaVersion;
import bpm.vanilla.platform.core.beans.Variable;
import bpm.vanilla.platform.core.components.system.IMailConfig;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteVanillaSystemManager implements IVanillaSystemManager{

	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static{
		xstream = new XStream();
	}
	public RemoteVanillaSystemManager(HttpCommunicator httpCommunicator){
		this.httpCommunicator = httpCommunicator;
	}
	
	
	
	private XmlArgumentsHolder createArguments(Object...  arguments){
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		for(int i = 0; i < arguments.length; i++){
			args.addArgument(arguments[i]);
		}
		return args;
	}

	/**
	 * Not really a remote operation, but hey, s**k it.
	 * not override on purpose
	 */
	public String getCurrentSessionId() {
		return httpCommunicator.getCurrentSessionId();
	}
	
	@Override
	public int addVariable(Variable variable) throws Exception {
		XmlAction op = new XmlAction(createArguments(variable), IVanillaSystemManager.ActionType.ADD_VARIABLE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer)xstream.fromXML(xml);
	}

	@Override
	public VanillaSession createSession(User user) throws Exception{
		XmlAction op = new XmlAction(createArguments(user), IVanillaSystemManager.ActionType.LOGIN);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (VanillaSession)xstream.fromXML(xml);
	}

	@Override
	public void deleteSession(String sessionId) throws Exception {
		XmlAction op = new XmlAction(createArguments(sessionId), IVanillaSystemManager.ActionType.LOGOUT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
		
	}

	@Override
	public void deleteVariable(Variable variable) throws Exception {
		XmlAction op = new XmlAction(createArguments(variable), IVanillaSystemManager.ActionType.DEL_VARIABLE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public VanillaVersion findLastVersion() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSystemManager.ActionType.GET_VANILLA_VERSION);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (VanillaVersion)xstream.fromXML(xml);
	}

	@Override
	public List<VanillaSession> getActiveSessions() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSystemManager.ActionType.LIST_SESSIONS);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
		public List<Server> getServerNodes(boolean includeStoppedComponent) throws Exception {
		XmlAction op = new XmlAction(createArguments(includeStoppedComponent), IVanillaSystemManager.ActionType.LIST_NODES);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<Server> getServerNodesByType(String componentNature, boolean includeStoppedComponent) throws Exception{
		XmlAction op = new XmlAction(createArguments(componentNature, includeStoppedComponent), IVanillaSystemManager.ActionType.LIST_NODES_BY_TYPE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public VanillaSession getSession(String ticket) throws Exception {
		XmlAction op = new XmlAction(createArguments(ticket), IVanillaSystemManager.ActionType.FIND_SESSION);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (VanillaSession)xstream.fromXML(xml);
	}

	@Override
	public VanillaSetup getVanillaSetup() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSystemManager.ActionType.GET_VANILLA_SETUP);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (VanillaSetup)xstream.fromXML(xml);
	}

	@Override
	public Variable getVariable(String variableName) throws Exception {
		XmlAction op = new XmlAction(createArguments(variableName), IVanillaSystemManager.ActionType.GET_VARIABLE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Variable)xstream.fromXML(xml);
	}

	@Override
	public List<Variable> getVariables() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSystemManager.ActionType.LIST_VARIABLES);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public void registerServerNode(Server server) throws Exception {
		XmlAction op = new XmlAction(createArguments(server), IVanillaSystemManager.ActionType.NODE_REGISTRATION);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
	}

	@Override
	public void unregisterServerNode(Server server) throws Exception {
		XmlAction op = new XmlAction(createArguments(server), IVanillaSystemManager.ActionType.NODE_RELEASE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
	}

	@Override
	public void updateServerNode(Server server) throws Exception {
		XmlAction op = new XmlAction(createArguments(server), IVanillaSystemManager.ActionType.NODE_UPDATE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
	}

	@Override
	public void updateVanillaSetup(VanillaSetup vanillaSetup) throws Exception {
		XmlAction op = new XmlAction(createArguments(vanillaSetup), IVanillaSystemManager.ActionType.UPDATE_VANILLA_SETUP);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
	}

	@Override
	public void updateVariable(Variable variable) throws Exception {
		XmlAction op = new XmlAction(createArguments(variable), IVanillaSystemManager.ActionType.UPDATE_VARIABLE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public String sendEmail(IMailConfig mailConfig, HashMap<String, InputStream> attachements) throws Exception {
		HashMap<String, byte[]> serializedAttachements = new HashMap<String, byte[]>();
		try {
			for (String attachementName : attachements.keySet()) {
				InputStream stream = attachements.get(attachementName);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int sz = 0;
				byte[] buf = new byte[1024];
				while((sz = stream.read(buf)) >= 0){
					bos.write(buf, 0, sz);
				}
				stream.close();
				
				byte[] streamDatas = Base64.encodeBase64(bos.toByteArray());
				
				serializedAttachements.put(attachementName, streamDatas);
			}
		} catch (IOException ex) {
			throw new IOException("Failed to serialize email attachements : " + ex.getMessage());
		}
		XmlAction op = new XmlAction(createArguments(mailConfig, serializedAttachements), IVanillaSystemManager.ActionType.SEND_EMAIL);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
		return (String) xstream.fromXML(result);
	}
	
	@Override
	public void startNodeComponent(Server server) throws Exception {
		XmlAction op = new XmlAction(createArguments(server), IVanillaSystemManager.ActionType.START_NODE);
		
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}
	
	@Override
	public void stopNodeComponent(Server server) throws Exception {
		XmlAction op = new XmlAction(createArguments(server), IVanillaSystemManager.ActionType.STOP_NODE);
		
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<VanillaLocale> getVanillaLocales(boolean fromPortal) throws Exception {
		XmlAction op = new XmlAction(createArguments(fromPortal), IVanillaSystemManager.ActionType.GET_VANILLA_LOCALES);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<VanillaLocale>)xstream.fromXML(xml);
	}



	@Override
	public void updateSession(VanillaSession session, String vanillaSessionId) throws Exception {
		XmlAction op = new XmlAction(createArguments(session, vanillaSessionId), IVanillaSystemManager.ActionType.UPDATE_SESSION);
		
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}



	@Override
	public VanillaSession getSession(String login, String password) throws Exception {
		XmlAction op = new XmlAction(createArguments(login, password), IVanillaSystemManager.ActionType.FIND_SESSION_BY_USER_PASS);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		try {
			return (VanillaSession)xstream.fromXML(xml);
		} catch(Exception e) {
			return null;
		}
	}
	
	@Override
	public boolean ping() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaSystemManager.ActionType.PING);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
			return (Boolean) xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
