package bpm.fmloader.server;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.fm.api.model.Observatory;
import bpm.fmloader.client.ConnectionServices;
import bpm.fmloader.client.exceptions.ServiceException;
import bpm.fmloader.client.infos.InfosUser;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ConnectionServicesImpl extends RemoteServiceServlet implements ConnectionServices {

	private static final long serialVersionUID = 8013269233376664156L;
	
	private String fmLoaderUrl;
	private int commentLimit;
//	private IManager man = null;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		
		initDefaultLogger();
		
		logger.info("Initing ConnectionServices servlet...");
		
		fmLoaderUrl = getServletContext().getInitParameter("fmloaderpath");
		if (fmLoaderUrl == null) {
			logger.fatal("Init parameter fmloaderpath is null");
			throw new ServletException("Init parameter fmloaderpath is null.");
		}
		
		logger.info("Using FM Loader Path : " + fmLoaderUrl);
		
		//recuperation des properties
		try {
			
			String p = System.getProperty("bpm.vanilla.configurationFile");
			
			Properties prop = new Properties();
			prop.load(new FileInputStream(p));
			
			commentLimit = Integer.parseInt(prop.getProperty("bpm.fm.loader.web.comment.size"));
		} catch (Exception e1) {
			commentLimit = 100;
			logger.warn("Failed to get comment limit property : " + e1.getMessage() + " using default values");
			//throw new ServletException("Failed to init configuration : " + e1.getMessage(), e1);
		} 
//		
//		FactoryManager.init(fmLoaderUrl, Tools.OS_TYPE_WINDOWS);
//		man = null;
//		try {
//			man = FactoryManager.getManager();
//		} catch (FactoryManagerException e) {
//			String msg = "Failed to create manager : " + e.getMessage();
//			logger.error(msg, e);
//			throw new ServletException(msg, e);
//		}
		
		logger.info("Created Manager");
	}

	@Override
	public void initSession() throws ServiceException {
		// We create an empty session
		try {
			String sessionId = CommonSessionManager.createSession(FMLoaderSession.class);
			CommonSessionHelper.setCurrentSessionId(sessionId, getThreadLocalRequest());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create a session.", e);
		}
	}
	
	/**
	 * Checks if logger has an appender, add one if needed
	 * CALL AFTER {@link ConfigurationManager}.initLogger
	 */
	private void initDefaultLogger() {
		if (!logger.getAllAppenders().hasMoreElements()) {
			logger.setLevel(Level.INFO);
			PatternLayout l = new PatternLayout("%p %C:%M %m%n");
			logger.addAppender(new ConsoleAppender(l, ConsoleAppender.SYSTEM_OUT));
		}
	}
	
	private FMLoaderSession getSession() throws Exception {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), FMLoaderSession.class);
	}
	
	@Override
	public InfosUser initFmSession(InfosUser infos) throws Exception {
		FMLoaderSession session = getSession();
		
		infos.setCommentLimit(commentLimit);

		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		
		String username = session.getUser().getLogin();
		String password = session.getUser().getPassword();
		
		IFreeMetricsManager manager = new RemoteFreeMetricsManager(vanillaUrl, username, password);
		getSession().setManager(manager);
		
		List<Group> groups = session.getVanillaApi().getVanillaSecurityManager().getGroups(session.getUser());
		infos.setGroups(groups);
		Set<Observatory> set = new HashSet<Observatory>();
		
		for(Group group : groups) {
			List<Observatory> obs = manager.getObservatoriesByGroup(group.getId());
			set.addAll(obs);
		}
		infos.setObservatories(new ArrayList<Observatory>(set));
		
		infos.setUser(session.getUser());
		
		Group group = null;
		try {
			if(session.getRepositoryConnection() == null) {
				Repository repository = new Repository();
				repository.setId(1);
				group = getSession().getVanillaApi().getVanillaSecurityManager().getGroups().get(0);
				session.initSession(group, repository);
			}
			else {
				group = session.getRepositoryConnection().getContext().getGroup();
			}
		} catch (Exception e) { }
		
		infos.setSelectedGroup(group);
		
		return infos;
	}
}
