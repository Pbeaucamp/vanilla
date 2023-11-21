package bpm.vanilla.platform.core.runtime.components;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IVanillaSystemManager;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLocale;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.beans.VanillaSetup;
import bpm.vanilla.platform.core.beans.VanillaVersion;
import bpm.vanilla.platform.core.beans.Variable;
import bpm.vanilla.platform.core.components.system.IMailConfig;
import bpm.vanilla.platform.core.exceptions.VanillaSessionExpiredException;
import bpm.vanilla.platform.core.runtime.dao.VanillaDaoComponent;
import bpm.vanilla.platform.core.runtime.dao.platform.RuntimeComponentDAO;
import bpm.vanilla.platform.core.runtime.dao.platform.SessionDAO;
import bpm.vanilla.platform.core.runtime.dao.platform.VanillaLocaleDAO;
import bpm.vanilla.platform.core.runtime.dao.platform.VanillaSetupDAO;
import bpm.vanilla.platform.core.runtime.dao.platform.VanillaVersionDAO;
import bpm.vanilla.platform.core.runtime.dao.platform.VariableDAO;
import bpm.vanilla.platform.core.runtime.tools.MailHelper;

public class VanillaSystemManager extends AbstractVanillaManager implements IVanillaSystemManager{
	private RuntimeComponentDAO serverDao;//not used anymore
	private SessionDAO sessionDao;
	private VanillaSetupDAO vanillaSetupDao;
	private VariableDAO variableDao;
	private VanillaVersionDAO vanillaVersionDao;
	private VanillaLocaleDAO vanillaLocaleDao;
	
	@Override
	public void updateServerNode(Server server) throws Exception{
		serverDao.update(server);
	}
	@Override
	public void unregisterServerNode(Server server) throws Exception{
		serverDao.delete(server);
	}
	@Override
	public void registerServerNode(Server server) throws Exception{
		serverDao.add(server);
	}

	@Override
	public List<Server> getServerNodes(boolean includeStoppedComponent) throws Exception{
		return serverDao.getAll(includeStoppedComponent);
	}
	
	@Override
	public List<Server> getServerNodesByType(String componentNature, boolean includeStoppedComponent) throws Exception{
		return serverDao.getByType(componentNature, includeStoppedComponent);
	}

	@Override
	public VanillaSession getSession(String ticket) throws Exception {
		VanillaSession session = sessionDao.findForTicket(ticket);
		
		if (session == null){
			return null;
		}
		
		if (!session.isActive()){
			sessionDao.delete(session);
			throw new VanillaSessionExpiredException(session);
		}
		return session;
	}

	@Override
	public List<VanillaSession> getActiveSessions() throws Exception{
		Date actu = Calendar.getInstance().getTime();
		int t = VanillaDaoComponent.getVanillaSessionTimeOut();
		Long l = actu.getTime() - new Long(t + ""); 
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(l));
		Date avant = c.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sessionDao.findActivesSessions(sdf.format(avant));
	}

	@Override
	public void deleteSession(String ticket) throws Exception{
		VanillaSession session = sessionDao.findForTicket(ticket);
		if (session != null){
			sessionDao.delete(session);
		}
		
		
	}
	@Override
	public void updateVanillaSetup(VanillaSetup d) throws Exception{
		vanillaSetupDao.update(d);
	}
	
	private List<VanillaSetup> getVanillaSetups() throws Exception{
		return vanillaSetupDao.findAll();
	}
	
	private VanillaSetup getVanillaSetupById(int id) throws Exception{
		return vanillaSetupDao.findByPrimaryKey(id);
	}
	@Override
	public VanillaSetup getVanillaSetup() throws Exception{
		return getVanillaSetups().get(0);
	}
	@Override
	public List<Variable> getVariables() throws Exception{
		return variableDao.findAll();
	}
	@Override
	public Variable getVariable(String variableName) throws Exception{
		return variableDao.findByName(variableName);
	}
	@Override
	public int addVariable(Variable variable) throws Exception {
		List<Variable> var = variableDao.findAll();
		boolean exist = false;
		for (Variable v : var) {
			if (v.getName().equalsIgnoreCase(variable.getName())) {
				exist = true;
				break;
			}
		}
		if (!exist)
			return variableDao.save(variable);
		else
			throw new Exception("This varaible name is already used");
	}
	@Override
	public void deleteVariable(Variable variable) throws Exception{
		variableDao.delete(variable);
	}
	@Override
	public void updateVariable(Variable variable) throws Exception {
		List<Variable> var = variableDao.findAll();
		boolean exist = false;
		for (Variable v : var) {
			if (v.getId().equals(variable.getId())) {
				exist = true;
				break;
			}
		}
		
		if (exist) {
			variableDao.update(variable); 
		}
		else {
			throw new Exception("The variable " + variable.getName() + " doesn't exist, check name and Id");
		}
		
	}
	@Override
	public String getComponentName() {
		return getClass().getName();
	}
	@Override
	protected void init() throws Exception {
		this.serverDao = getDao().getServerDao();
		if (this.serverDao == null){
			throw new Exception("Missing ServerDao!");
		}
		this.sessionDao = getDao().getSessionDao();
		if (this.sessionDao == null){
			throw new Exception("Missing SessionDao!");
		}
		this.vanillaSetupDao = getDao().getVanillaSetupDao();
		if (this.vanillaSetupDao == null){
			throw new Exception("Missing VanillaSetupDao!");
		}
		this.vanillaVersionDao = getDao().getVanillaVersionDao();
		if (this.vanillaVersionDao == null){
			throw new Exception("Missing VanillaVersionDao!");
		}
		this.variableDao = getDao().getVariableDao();
		if (this.variableDao == null){
			throw new Exception("Missing VariableDao!");
		}
		this.vanillaLocaleDao = getDao().getVanillaLocaleDao();
		if (this.vanillaLocaleDao == null){
			throw new Exception("Missing VanillaLocaleDao!");
		}
		getLogger().info("init done!");
	}
	@Override
	public VanillaVersion findLastVersion() throws Exception {
		return vanillaVersionDao.findLast();
	}

	@Override
	public VanillaSession createSession(User user) {
		return sessionDao.createSession(user);
	}
	
	@Override
	public String sendEmail(IMailConfig mailConfig,
			HashMap<String, InputStream> attachements) throws Exception {
		
		try {
			String res = MailHelper.sendEmail(mailConfig, attachements);

			return res;
		} catch (Throwable e) {
			Logger.getLogger(getClass()).error("Mail issue - " + e.getMessage(), e);
			throw new Exception("Failed to send mail : " + e.getMessage(), e);
		}
		
	}
	
	/**
	 * this is NOT supposed to be called, just implemented to respect the Interface
	 */
	@Override
	public void startNodeComponent(Server server) throws Exception {
		throw new Exception("Not supported");
	}
	
	/**
	 * this is NOT supposed to be called, just implemented to respect the Interface
	 */
	@Override
	public void stopNodeComponent(Server server) throws Exception {
		throw new Exception("Not supported");
	}
	@Override
	public List<VanillaLocale> getVanillaLocales(boolean fromPortal) throws Exception {
		List<VanillaLocale> locales = vanillaLocaleDao.findAll();
		if(!fromPortal){
			List<VanillaLocale> toRemove = new ArrayList<VanillaLocale>();
			for(VanillaLocale locale : locales){
				if(!(locale.getLocaleValue().equals("en_EN")
						|| locale.getLocaleValue().equals("fr_FR")
						/*|| locale.getLocaleValue().equals("es_ES")*/)){
					toRemove.add(locale);
				}
			}
			
			for(VanillaLocale locale : toRemove){
				locales.remove(locale);
			}
		}
		Collections.sort(locales, new Comparator<VanillaLocale>() {

			@Override
			public int compare(VanillaLocale locale1, VanillaLocale locale2) {
				return locale1.getLocaleOrder() < locale2.getLocaleOrder() ? -1 : 1;
			}
		});
		
		return locales;
	}
	@Override
	public void updateSession(VanillaSession session, String vanillaSessionId) throws Exception {
		sessionDao.updateSession(session, vanillaSessionId);
	}
	@Override
	public VanillaSession getSession(String login, String password) throws Exception {
		VanillaSession session = sessionDao.findForLoginPassword(login, password);
		
		if (session == null){
			return null;
		}
		
		if (!session.isActive()){
			sessionDao.delete(session);
			throw new VanillaSessionExpiredException(session);
		}
		return session;
	}
	
	@Override
	public boolean ping() throws Exception {
		//TODO: Check database
		return true;
	}
}
