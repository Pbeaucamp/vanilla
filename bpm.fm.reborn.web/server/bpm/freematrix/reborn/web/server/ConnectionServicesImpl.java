package bpm.freematrix.reborn.web.server;

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
import bpm.freematrix.reborn.web.client.ClientSession;
import bpm.freematrix.reborn.web.client.services.ConnectionServices;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ConnectionServicesImpl extends RemoteServiceServlet implements ConnectionServices {
	
	private static final long serialVersionUID = 7630616301811834767L;

	private FreeMetricsSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), FreeMetricsSession.class);
	}

	@Override
	public void initSession() throws ServiceException {
		// We create an empty session
		try {
			String sessionId = CommonSessionManager.createSession(FreeMetricsSession.class);
			CommonSessionHelper.setCurrentSessionId(sessionId, getThreadLocalRequest());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create a session.", e);
		}
	}
	
	@Override
	public ClientSession initFmSession() throws Exception {
		FreeMetricsSession session = getSession();
		
		try {	
			ClientSession.clear();
			
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			String vanillaUrl = config.getProperty(VanillaConfiguration.P_VANILLA_URL);

			String login = session.getUser().getLogin();
			String password = session.getUser().getPassword();
			
			RemoteFreeMetricsManager manager = new RemoteFreeMetricsManager(vanillaUrl, login, password);
			session.setManager(manager);
			
			List<Group> groups = session.getVanillaApi().getVanillaSecurityManager().getGroups(session.getUser());
			ClientSession.getInstance().setGroups(groups);
			
			session.setCurrentGroup(groups.get(0));
			
			Set<Observatory> set = new HashSet<Observatory>();
			
			for(Group group : groups) {
				List<Observatory> obs = manager.getObservatoriesByGroup(group.getId());
				set.addAll(obs);
			}
			ClientSession.getInstance().setObservatories(new ArrayList<Observatory>(set));
			
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
			
			ClientSession.getInstance().setDefaultDate(date);
			
			String ckanUrl = config.getProperty(VanillaConfiguration.P_CKAN_URL);
			boolean isConnectedToCkan = ckanUrl != null && !ckanUrl.isEmpty();
			ClientSession.getInstance().setConnectedToCkan(isConnectedToCkan);
			
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
		return ClientSession.getInstance();

	}


}
