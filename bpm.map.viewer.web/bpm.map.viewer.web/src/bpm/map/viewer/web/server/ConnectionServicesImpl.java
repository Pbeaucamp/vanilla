package bpm.map.viewer.web.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;











import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.map.viewer.web.client.UserSession;
import bpm.map.viewer.web.client.services.ConnectionServices;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ConnectionServicesImpl extends RemoteServiceServlet implements ConnectionServices {
	
	private static final long serialVersionUID = 7630616301811834767L;

	private MapViewerSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), MapViewerSession.class);
	}

	@Override
	public void initSession() throws ServiceException {
		// We create an empty session
		try {
			String sessionId = CommonSessionManager.createSession(MapViewerSession.class);
			CommonSessionHelper.setCurrentSessionId(sessionId, getThreadLocalRequest());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create a session.", e);
		}
	}
	
	public UserSession initMapViewerSession() throws Exception {
		MapViewerSession session = getSession();
		
		try {	
			UserSession.clear();
			
			String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
			
			String login = session.getUser().getLogin();
			String password = session.getUser().getPassword();
			
			IFreeMetricsManager manager = new RemoteFreeMetricsManager(vanillaUrl, login, password);
			session.setManager(manager);
			
			List<Group> groups = session.getVanillaApi().getVanillaSecurityManager().getGroups(session.getUser());
			UserSession.getInstance().setGroups(groups);
			
			session.setCurrentGroup(groups.get(0));
			
			Set<Observatory> set = new HashSet<Observatory>();
			
			for(Group group : groups) {
				List<Observatory> obs = manager.getObservatoriesByGroup(group.getId());
				set.addAll(obs);
			}
			UserSession.getInstance().setObservatories(new ArrayList<Observatory>(set));
			
			//fetch the metric and find the shortest periodicity
			String shortestPeriod = FactTable.PERIODICITY_YEARLY;
			for(Metric m : manager.getMetrics()) {
				if(m.getFactTable() instanceof FactTable) {
					String period = ((FactTable)m.getFactTable()).getPeriodicity();
					
					if(FactTable.PERIODICITIES_LIST.indexOf(period) > FactTable.PERIODICITIES_LIST.indexOf(shortestPeriod)) {
						shortestPeriod = period;
					}
					
				}
			}
			
			Date date = new Date();
			
			if(shortestPeriod.equals(FactTable.PERIODICITY_YEARLY)) {
				date.setYear(date.getYear() - 1);
			}
			else if(shortestPeriod.equals(FactTable.PERIODICITY_MONTHLY)) {
				date.setMonth(date.getMonth() - 1);
			}
			else if(shortestPeriod.equals(FactTable.PERIODICITY_DAILY)) {
				date.setDate(date.getDate() - 1);
			}
			
			UserSession.getInstance().setDefaultDate(date);
			
			List<String> fileNames = new ArrayList<>();
			String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES);
	        try {
				DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(basePath +"/KpiMap_Icons/"));
				for (Path path : directoryStream) {
				    fileNames.add(path.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	       
	        
	        UserSession.getInstance().setIconSet(fileNames);
	        
	        String appurl = getThreadLocalRequest().getScheme() + "://" + getThreadLocalRequest().getServerName() + ":" + getThreadLocalRequest().getServerPort();
	        UserSession.getInstance().setWebappUrl(appurl);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
		return UserSession.getInstance();

	}


}
