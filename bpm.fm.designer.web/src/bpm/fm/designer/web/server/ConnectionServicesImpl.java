package bpm.fm.designer.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.designer.web.client.ClientSession;
import bpm.fm.designer.web.client.services.ConnectionServices;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.map.core.design.IMapDefinitionService;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
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
	public ClientSession initFmSession() throws Throwable {
		FreeMetricsSession session = getSession();
		
		try {
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			String vanillaUrl = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
			
			String login = session.getUser().getLogin();
			String password = session.getUser().getPassword();
			
			IFreeMetricsManager manager = new RemoteFreeMetricsManager(vanillaUrl, login, password);
			getSession().setManager(manager);

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
			
			List<Observatory> observatories = session.getManager().getObservatories();
			Collections.sort(observatories, new Comparator<Observatory>() {
				@Override
				public int compare(Observatory o1, Observatory o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			
			HashSet<Metric> metrics = new HashSet<Metric>();
			HashSet<Axis> axis = new HashSet<Axis>();
			
			for(Observatory obs : observatories) {
				for(Theme th : obs.getThemes()) {
					metrics.addAll(th.getMetrics());
					axis.addAll(th.getAxis());
				}
			}
			/* kevin M */
			IMapDefinitionService mapservice = new RemoteMapDefinitionService();
			mapservice.configure(vanillaUrl);
			List<MapDataSet> listDts = new ArrayList<MapDataSet>();
			try {
				listDts = mapservice.getAllMapsDataSet();
			} catch (Exception e) {}
			List<MapVanilla> maps = new ArrayList<MapVanilla>();
			try {
				maps = mapservice.getAllMapsVanilla();
			} catch (Exception e) {}
			
			

			ClientSession.getInstance().setObservatories(observatories);
			ClientSession.getInstance().setAxis(new ArrayList<Axis>(axis));
			ClientSession.getInstance().setMetrics(new ArrayList<Metric>(metrics));
			ClientSession.getInstance().setDatasets(listDts);
			ClientSession.getInstance().setMaps(maps);
			ClientSession.getInstance().setLogin(login);
			ClientSession.getInstance().setPassword(password);
			ClientSession.getInstance().setVanillaUrl(vanillaUrl);
			
			//TODO: We don't have the goups, so we take the first group, to change - BULSHIT !!!
			try {
//				Integer groupId = Integer.parseInt(config.getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID));
//				Group dummy = new Group();
//				dummy.setId(groupId);
				ClientSession.getInstance().setGroup(group);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}

		return ClientSession.getInstance();

	}

	@Override
	public List<Group> getGroupForUser() throws Throwable {
		return getSession().getVanillaApi().getVanillaSecurityManager().getGroups(getSession().getUser());
	}

	@Override
	public List<Group> getAllGroups() throws Throwable {

		List<Group> groups = getSession().getVanillaApi().getVanillaSecurityManager().getGroups();

		return groups;
	}

	@Override
	public List<User> getUsers() throws Throwable {
		return getSession().getVanillaApi().getVanillaSecurityManager().getUsers();
	}

}
