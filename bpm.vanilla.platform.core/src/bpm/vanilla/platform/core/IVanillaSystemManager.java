package bpm.vanilla.platform.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLocale;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.beans.VanillaSetup;
import bpm.vanilla.platform.core.beans.VanillaVersion;
import bpm.vanilla.platform.core.beans.Variable;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.components.system.IMailConfig;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

/**
 * VanillaSetupManager + ServerManager + VanillaVersionDAO + SessionManager
 * ere: added email here
 * 
 * @author ludo
 *
 */
public interface IVanillaSystemManager {
	
	
	public static enum ActionType implements IXmlActionType{
		NODE_REGISTRATION(Level.DEBUG), NODE_RELEASE(Level.DEBUG), NODE_UPDATE(Level.DEBUG), LIST_NODES(Level.DEBUG), LIST_NODES_BY_TYPE(Level.DEBUG),
		GET_VANILLA_SETUP(Level.DEBUG), UPDATE_VANILLA_SETUP(Level.DEBUG),
		GET_VANILLA_VERSION(Level.DEBUG),
		LOGIN(Level.INFO),LOGOUT(Level.INFO),LIST_SESSIONS(Level.DEBUG),FIND_SESSION(Level.DEBUG),
		ADD_VARIABLE(Level.INFO),DEL_VARIABLE(Level.INFO),UPDATE_VARIABLE(Level.INFO),GET_VARIABLE(Level.DEBUG),LIST_VARIABLES(Level.DEBUG),
		SEND_EMAIL(Level.INFO),
		STOP_NODE(Level.DEBUG), START_NODE(Level.DEBUG),
		GET_VANILLA_LOCALES(Level.DEBUG), UPDATE_SESSION(Level.DEBUG), FIND_SESSION_BY_USER_PASS(Level.DEBUG), PING(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
		
	}
	
	public void registerServerNode(Server server) throws Exception;
	public void unregisterServerNode(Server server) throws Exception;
	public void updateServerNode(Server server) throws Exception;
	
	public List<Server> getServerNodes(boolean includeStoppedComponent) throws Exception;
	public List<Server> getServerNodesByType(String componentNature, boolean includeStoppedComponent) throws Exception;
	
	public VanillaSetup getVanillaSetup() throws Exception;
	public void updateVanillaSetup(VanillaSetup vanillaSetup) throws Exception;
	
	public VanillaVersion findLastVersion() throws Exception;
	
	public VanillaSession createSession(User user) throws Exception;
	public void deleteSession(String sessionId) throws Exception;
	public List<VanillaSession> getActiveSessions() throws Exception;
	public VanillaSession getSession(String ticket) throws Exception;
	
	
	public int addVariable(Variable variable) throws Exception;
	public void deleteVariable(Variable variable) throws Exception;
	public void updateVariable(Variable variable) throws Exception;
	public Variable getVariable(String variableName) throws Exception;
	public List<Variable> getVariables() throws Exception;
	
	/**
	 * Send a email using the platform's mailer
	 * 
	 * @param mailConfig
	 * @param attachements
	 * @return
	 * @throws Exception 
	 */
	public String sendEmail(IMailConfig mailConfig, HashMap<String, InputStream> attachements) throws Exception;
	
	public void startNodeComponent(Server server) throws Exception;
	
	public void stopNodeComponent(Server server) throws Exception;
	
	/**
	 * Return the list of supported locale for Vanilla's Web Application
	 * 
	 * @param fromPortal if set to true, send the locale available for the portal if not for other webapp
	 * @return
	 * @throws Exception
	 */
	public List<VanillaLocale> getVanillaLocales(boolean fromPortal) throws Exception;
	
	/**
	 * Replace the session for this id
	 * Be careful, it doesn't check user or anything.
	 * @param session
	 * @param vanillaSessionId
	 * @throws Exception
	 */
	public void updateSession(VanillaSession session, String vanillaSessionId) throws Exception;
	public VanillaSession getSession(String login, String password) throws Exception;
	
	/**
	 * Return true if the server is available and ready to take request
	 * Used by webapps at startup
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean ping() throws Exception;
}
