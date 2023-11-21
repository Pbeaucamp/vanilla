package bpm.vanilla.portal.server;

import java.io.ByteArrayInputStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.report.ObjectInputStream;
import bpm.gwt.commons.server.security.CommonConfiguration;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.fmdt.FmdtModel;
import bpm.gwt.commons.shared.repository.PortailItemFmdtChart;
import bpm.gwt.commons.shared.repository.PortailItemFmdtDriller;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.query.SavedQuery;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapDataSource;
import bpm.vanilla.map.core.design.MapHelper;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.core.design.MapZone;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.IPreferencesManager;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.OpenPreference;
import bpm.vanilla.platform.core.beans.Widgets;
import bpm.vanilla.platform.core.beans.alerts.Action;
import bpm.vanilla.platform.core.beans.alerts.Action.TypeAction;
import bpm.vanilla.platform.core.beans.alerts.ActionMail;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;
import bpm.vanilla.platform.core.beans.alerts.AlertConstants;
import bpm.vanilla.platform.core.beans.alerts.AlertRepository;
import bpm.vanilla.platform.core.beans.alerts.AlertRepository.ObjectEvent;
import bpm.vanilla.platform.core.beans.alerts.Subscriber;
import bpm.vanilla.platform.core.beans.chart.SavedChart;
import bpm.vanilla.platform.core.beans.scheduler.Job;
import bpm.vanilla.platform.core.beans.scheduler.JobInstance;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.server.apiImpl.BIRTImpl;
import bpm.vanilla.portal.server.apiImpl.PreferencesImpl;
import bpm.vanilla.portal.server.apiImpl.RepositoryImpl;
import bpm.vanilla.portal.server.security.PortalSession;
import bpm.vanilla.portal.shared.MapColumns;
import bpm.vanilla.portal.shared.MapFeatures;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class BiPortalServiceImpl extends RemoteServiceServlet implements BiPortalService {

	private static final long serialVersionUID = -2644119746431385367L;
	public static Logger logger;
	private CommonConfiguration portalConfig;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		try {
			portalConfig = CommonConfiguration.getInstance();

			logger = Logger.getLogger(getClass());
			logger.info("Initing BiPortalServiceImpl");

			logger.info("Finished init of BiPortalServiceImpl");
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("Failed to init BiPortalService, reason : " + e.getMessage(), e);
			throw new ServletException("Failed to init BiPortalService, reason : " + e.getMessage(), e);
		}
	}

	private PortalSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), PortalSession.class);
	}

	@Override
	public void purgeLastUsed() throws ServiceException {
		logger.info("Start purge Last Used");

		PortalSession session = getSession();
		try {
			session.getRepositoryConnection().getAdminService().purgeHistoric();
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("The Last Used has not being purged. Please, try again or check logs: ", e);
		}
	}

	@Override
	public void addPersoDialog(int parentId, String name, String comment) throws Exception {
		PortalSession session = null;
		try {
			session = getSession();
		} catch(ServiceException e1) {
			e1.printStackTrace();
		}

		RepositoryDirectory dirParent = new RepositoryDirectory();
		dirParent.setId(parentId);

		session.getRepositoryConnection().getRepositoryService().addDirectory(name, comment, dirParent);
	}

	@Override
	public List<Widgets> getWidgets() throws Exception {
		PortalSession session = getSession();

		return session.getPreferencesManager().getWidgetsByUser(session.getUser().getId());
	}

	@Override
	public PortailRepositoryDirectory getLastConsulted(String group) throws ServiceException {
		logger.info("getLastConsulted()");

		PortalSession session = getSession();
		IRepositoryApi sock = session.getRepositoryConnection();

		PortailRepositoryDirectory res = new PortailRepositoryDirectory("LastUsed");
		try {
			RepositoryImpl impl = new RepositoryImpl(session);
			List<RepositoryItem> items = sock.getWatchListService().getLastConsulted();
			buildItems(impl, sock, res, items);

		} catch(Exception e) {
			String errMsg = "Failed to retrieve last consulted items : " + e.getMessage();
			logger.info(errMsg, e);
			throw new RuntimeException(errMsg, e);
		}

		if(res.getItems() != null) {
			logger.info("getLastConsulted returning with " + res.getItems().size() + " children");
		}

		return res;
	}

	private void buildItems(RepositoryImpl r, IRepositoryApi sock, PortailRepositoryDirectory res, List<RepositoryItem> items) {
		for(RepositoryItem di : items) {
			if(di.isDisplay()) {
				try {
					PortailRepositoryItem ti = r.buildItem(sock, res, di);
					if(ti != null) {
						res.addItem(ti);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean addToMyWatchList(int directoryItemId, int type) throws ServiceException {
		PortalSession session = getSession();

		IRepositoryApi sock = session.getRepositoryConnection();
		try {

			if(type > 0) {
				// IRepository currIRepository = new Repository(sock, type);
				sock.getWatchListService().addToWatchList(sock.getRepositoryService().getDirectoryItem(directoryItemId));

				return true;
			}
			else {
				return false;
			}

		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String removeToMyWatchList(int itemId) throws ServiceException {
		String res = "";
		PortalSession session = getSession();
		IRepositoryApi sock = session.getRepositoryConnection();

		try {
			sock.getWatchListService().removeFromWatchList(session.getRepository().getItem(itemId));
		} catch(Exception e) {
			e.printStackTrace();
			return "<error>" + e.getMessage() + "</error>";
		}

		return res;
	}

	/**
	 * Retrieves a list of the available locales for the given report
	 * 
	 * @param DirectoryItemId
	 *            , the dirItemId of the birt report
	 * @return
	 * @throws ServiceException
	 */
	public List<String> getReportLocales(int directoryItemId) throws ServiceException {
		logger.info("Fetching possible locales for item id " + directoryItemId);
		List<String> locales = new ArrayList<String>();

		PortalSession session = getSession();

		IRepositoryApi sock = session.getRepositoryConnection();
		// AdminAccess access = session.getAdminAccess();

		try {
			// LinkedDocumentAdministrator linkedManager = new
			// LinkedDocumentAdministrator(
			// sock, access);
			// List<LinkedDocument> docs =
			// linkedManager.getLinkedDocument((DirectoryItem)item);
			List<LinkedDocument> docs = sock.getRepositoryService().getLinkedDocumentsForGroup(directoryItemId, session.getCurrentGroup().getId());
			// report.rptdesign
			// - report.properties, this is the default
			// - report_fr_FR.properties, this is the fr_FR locale

			for(LinkedDocument doc : docs) {
				if(doc.getFormat().equals(".properties")) {
					String docName = doc.getName();
					locales.add(docName);
				}
			}

		} catch(Exception e) {
			logger.error("Failed to fetch Locales for dirItemId ( " + directoryItemId + "). " + "Reason :" + e.getMessage(), e);
		}

		return locales;
	}

	public void addOpenItem(int itemId) throws ServiceException {
		PortalSession session = getSession();

		RepositoryItem item;
		try {
			item = session.getRepositoryConnection().getRepositoryService().getDirectoryItem(itemId);
			PreferencesImpl.addOpenItem(item, session);
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	public void removeOpenItem(int itemId) throws ServiceException {
		PortalSession session = getSession();

		IRepositoryApi conn = session.getRepositoryConnection();

		RepositoryItem item;
		try {
			item = conn.getRepositoryService().getDirectoryItem(itemId);
			PreferencesImpl.removeOpenItem(item, session);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public PortailRepositoryDirectory getMyWatchList() throws ServiceException {
		PortalSession session = getSession();
		IRepositoryApi sock = session.getRepositoryConnection();

		PortailRepositoryDirectory res = new PortailRepositoryDirectory("WatchList");
		try {
			RepositoryImpl impl = new RepositoryImpl(session);

			List<RepositoryItem> items = sock.getWatchListService().getWatchList();
			buildItems(impl, sock, res, items);

		} catch(Exception e) {
			e.printStackTrace();
		}

		return res;

	}

	@Override
	public PortailRepositoryDirectory getOpenItems() throws ServiceException {
		PortalSession session = getSession();
		IRepositoryApi sock = session.getRepositoryConnection();
		IPreferencesManager manager = session.getPreferencesManager();

		PortailRepositoryDirectory res = new PortailRepositoryDirectory("OpenItems");
		try {
			RepositoryImpl impl = new RepositoryImpl(session);

			List<OpenPreference> l = manager.getOpenPreferencesForUserId(session.getUser().getId());
			List<RepositoryItem> items = new ArrayList<RepositoryItem>();
			for(OpenPreference o : l) {
				RepositoryItem item = session.getRepository().getItem(o.getItemId());
				items.add(item);
			}

			buildItems(impl, sock, res, items);

		} catch(Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	@Override
	public String runBIRTFromRep(int itemId) throws ServiceException {
		PortalSession session = getSession();

		String groupName = session.getCurrentGroup().getName();
		String url = portalConfig.getBirtViewerUrl() + "frameset?__report=" + BIRTImpl.writeBIRTFromRep(portalConfig.getBirtViewerPath(), session.getRepositoryConnection(), itemId, groupName, session.getRepository(), session);

		logger.info("Running birt from rep with id " + itemId + ", redirecting to " + url);

		return url;
	}

	@Override
	public PortailRepositoryItem getItemDto(int itemId) throws ServiceException {
		PortalSession session = getSession();
		IRepositoryApi sock = session.getRepositoryConnection();

		try {
			RepositoryItem item = session.getRepositoryConnection().getRepositoryService().getDirectoryItem(itemId);

			RepositoryImpl impl = new RepositoryImpl(session);
			return impl.buildItem(sock, null, item);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method return the first level of the repository instead of getting all items in the repository (Better performance)
	 */
	@Override
	public PortailRepositoryDirectory getRepositoryContents() throws ServiceException {
		PortalSession session = getSession();

		try {
			RepositoryImpl r = new RepositoryImpl(session);
			return r.getRepositoryDirectories(session.getCurrentRepository().getName(), session.getRepositoryConnection(), session.isShowAllRepository());
		} catch(Exception e) {
			logger.info("Error during content recuperation");
			logger.error("Error during content recuperation", e);
			e.printStackTrace();
			throw new ServiceException("Error during content recuperation: " + e.getMessage());
		}
	}

	@Override
	public PortailRepositoryDirectory getRepositoryContent(int typeRepository) throws ServiceException {
		PortalSession session = getSession();

		try {
			RepositoryImpl r = new RepositoryImpl(session);
			return r.getContentsByType(session.getRepositoryConnection(), typeRepository);
		} catch(Exception e) {
			logger.error("Error during content recuperation", e);
			e.printStackTrace();
			throw new ServiceException("Error during content recuperation: " + e.getMessage());
		}
	}

	@Override
	public List<IRepositoryObject> getDirectoryContent(PortailRepositoryDirectory dir) throws ServiceException {
		PortalSession session = getSession();

		try {
			RepositoryImpl r = new RepositoryImpl(session);
			return r.getDirectoryContent(session.getRepositoryConnection(), dir, session.isShowAllRepository());
		} catch(Exception e) {
			logger.error("Error during folder " + dir.getName() + " recuperation", e);
			e.printStackTrace();
			throw new ServiceException("Error during folder  " + dir.getName() + " recuperation: " + e.getMessage());
		}
	}

	@Override
	public List<Comment> getComments(int objectId, int type) throws ServiceException {
		PortalSession session = getSession();

		try {
			return session.getRepositoryConnection().getDocumentationService().getComments(session.getCurrentGroup().getId(), objectId, type);
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the comments for that item.", e);
		}
	}

	// @Override
	// public Date getNextExecution(String cron) throws ServiceException {
	// FixedPeriodCron cronManager = SchedulerUtils.getNextExecution(new Date(),
	// schedule);
	// Calendar cal = cronManager.getClosestDateAfter(Calendar.getInstance());
	// return cal.getTime();
	// }

	@Override
	public void addOrEditJob(Job job) throws ServiceException {
		PortalSession session = getSession();

		try {
			if(job.getId() > 0) {
				session.getVanillaApi().getSchedulerManager().editJob(job);
			}
			else {
				session.getVanillaApi().getSchedulerManager().addJob(job);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add or edit the Job.", e);
		}
	}

	@Override
	public void deleteJob(Job job) throws ServiceException {
		PortalSession session = getSession();

		try {
			session.getVanillaApi().getSchedulerManager().delJob(job);
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete the Job.", e);
		}
	}

	@Override
	public List<Job> getJobs() throws ServiceException {
		PortalSession session = getSession();

		try {
			List<Job> jobs = session.getVanillaApi().getSchedulerManager().getJobs(session.getCurrentRepository().getId());
			return jobs;
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the list of jobs.", e);
		}
	}

	@Override
	public void launchJob(Job job) throws ServiceException {
		PortalSession session = getSession();

		try {
			List<Job> jobs = session.getVanillaApi().getSchedulerManager().getJobs(job.getRepositoryId());
			if(jobs != null) {
				for(Job jobToLaunch : jobs) {
					if(job.getId() == jobToLaunch.getId()) {
						session.getVanillaApi().getSchedulerManager().runNowJob(jobToLaunch);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to run the job " + job.getDetail().getName());
		}
	}

	@Override
	public List<JobInstance> getJobHistoric(Job job) throws ServiceException {
		PortalSession session = getSession();

		try {
			return session.getVanillaApi().getSchedulerManager().getJobHistoric(job);
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the list of jobs.", e);
		}
	}

	@Override
	public void setShowAllRepository(boolean showAllRepository) throws ServiceException {
		PortalSession session = getSession();
		session.setShowAllRepository(showAllRepository);
	}

	@Override
	public boolean showAllRepository() throws ServiceException {
		PortalSession session = getSession();
		return session.isShowAllRepository();
	}

	@Override
	public boolean subscribeToItem(PortailRepositoryItem item) throws ServiceException {
		PortalSession session = getSession();

		boolean alreadySubscribe = isAlreadySubscribe(session, item);
		if(alreadySubscribe) {
			return true;
		}

		AlertRepository alertRep = new AlertRepository();
		// alertRep.setName("Subscribe to " + item.getName());
		alertRep.setRepositoryId(session.getCurrentRepository().getId());
		// alertRep.setTypeEvent(TypeEvent.OBJECT_TYPE);
		if(item.getType() == IRepositoryApi.CUST_TYPE && item.getSubType() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
			alertRep.setSubtypeEvent(ObjectEvent.BIRT_NEW_VERSION);
		}
		else if(item.getType() == IRepositoryApi.FWR_TYPE) {
			alertRep.setSubtypeEvent(ObjectEvent.FWR_NEW_VERSION);
		}
		else if(item.getType() == IRepositoryApi.GTW_TYPE) {
			alertRep.setSubtypeEvent(ObjectEvent.GTW);
		}
		else {
			throw new ServiceException("You cannot subscribe to this type of item.");
		}
		alertRep.setDirectoryItemId(item.getId());

		// int eventId;
		// try {
		// eventId = session.getRepositoryConnection().getAlertService().createEvent(event);
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw new ServiceException("Unable to save a new Event for this item.");
		// }

		Subscriber subscriber = new Subscriber();
		subscriber.setGroupId(session.getCurrentGroup().getId());
		subscriber.setUserId(session.getUser().getId());
		subscriber.setUserMail(session.getUser().getBusinessMail());
		subscriber.setUserName(session.getUser().getLogin());

		ActionMail mail = new ActionMail();
		mail.setSubject("Update on " + item.getName());
		mail.setContent("There was an update on the item " + item.getName());
		mail.addSubscriber(subscriber);

		Action action = new Action();
		action.setActionObject(mail);
		action.setActionType(TypeAction.MAIL);

		Alert alert = new Alert();
		alert.setAction(action);
		alert.setName("Alert on " + item.getName());
		alert.setState(AlertConstants.ACTIVE);
		alert.setTypeAction(TypeAction.MAIL);
		alert.setTypeEvent(TypeEvent.OBJECT_TYPE);

		try {
			session.getRepositoryConnection().getAlertService().createAlert(alert);
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save a new Alert for this item.");
		}

		return false;
	}

	private boolean isAlreadySubscribe(PortalSession session, PortailRepositoryItem item) {
		List<Alert> alerts = null;
		try {
			alerts = session.getRepositoryConnection().getAlertService().getAlertsByTypeAndDirectoryItemId(TypeEvent.OBJECT_TYPE, item.getId(), session.getCurrentRepository().getId());
		} catch(Exception e) {
			e.printStackTrace();
		}

		if(alerts != null) {
			boolean alreadySubscribe = false;
			for(Alert alert : alerts) {
				if(alert.getAction() != null && alert.getAction().getActionObject() instanceof ActionMail) {
					if(((ActionMail) alert.getAction().getActionObject()).getSubject() != null && !((ActionMail) alert.getAction().getActionObject()).getSubject().isEmpty() && ((ActionMail) alert.getAction().getActionObject()).getSubscribers() != null) {
						for(Subscriber su : ((ActionMail) alert.getAction().getActionObject()).getSubscribers()) {
							if(su.getUserId() == session.getUser().getId()) {
								alreadySubscribe = true;
								break;
							}
						}
					}
				}
				if(alreadySubscribe) {
					break;
				}
			}

			if(alreadySubscribe) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String createDisconnectedPackage(String packageName, int limitRows, List<RepositoryItem> items) throws ServiceException {
		PortalSession session = getSession();

		byte[] packageZip;
		try {
			packageZip = session.getRepositoryConnection().getRepositoryService().createDisconnectedPackage(packageName, limitRows, items);

			ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(packageZip);

			ObjectInputStream packageStream = new ObjectInputStream();
			packageStream.addStream("zip", byteArrayIs);

			session.addReport(packageName, packageStream);
			return packageName;
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to bundle this package.", e);
		}
	}

	@Override
	public void deleteItem(int itemId) throws ServiceException {
		PortalSession session = getSession();

		RepositoryItem item;
		try {
			item = session.getRepositoryConnection().getRepositoryService().getDirectoryItem(itemId);
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to retrieve the requested item : " + e.getMessage());
		}

		if(item != null) {
			try {
				session.getRepositoryConnection().getRepositoryService().delete(item);
			} catch(Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to delete the item : " + e.getMessage());
			}
		}
	}

	@Override
	public PortailRepositoryDirectory searchRepository(String search) throws ServiceException {
		PortalSession session = getSession();

		try {
			RepositoryImpl r = new RepositoryImpl(session);
			return r.searchRepository(session.getCurrentRepository().getName(), session.getRepositoryConnection(), search);
		} catch(Exception e) {
			logger.info("Error during search");
			logger.error("Error during search", e);
			e.printStackTrace();
			throw new ServiceException("Error during the search of '" + search + "': " + e.getMessage());
		}
	}

	@Override
	public void addOrEditMap(MapVanilla map) throws ServiceException {
		PortalSession session = getSession();

		try {
			if(map.getId() > 0) {
				session.getRemoteMap().updateMapVanilla(map);
			}
			else {
				session.getRemoteMap().saveMapVanilla(map);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add or edit the Map.", e);
		}

	}

	@Override
	public List<MapVanilla> getMaps() throws ServiceException {
		PortalSession session = getSession();
		List<MapVanilla> lesMaps;

		try {
			lesMaps = session.getRemoteMap().getAllMapsVanilla();
			return lesMaps;
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Error during the recuperation of maps : " + e.getMessage());
		}
	}

	@Override
	public void deleteMap(MapVanilla selectedMap) throws ServiceException {
		PortalSession session = getSession();

		try {
			// deleteMapDataSet(selectedMap.getDataSet());
			session.getRemoteMap().deleteMapVanilla(selectedMap);

		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete the Map.", e);
		}
	}

	@Override
	public List<String> getLesDrivers() throws Exception {
		List<String> drivers = new ArrayList<String>();
		String jdbcXmlFile = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_JDBC_XML_FILE);
		Collection<DriverInfo> infos;
		infos = ListDriver.getInstance(jdbcXmlFile).getDriversInfo();
		Iterator<DriverInfo> it = infos.iterator();
		while(it.hasNext()) {
			String className = it.next().getClassName();
			if(!drivers.contains(className)) {

				drivers.add(className);
			}
		}
		return drivers;
	}

	public List<MapDataSource> getMapsDataSource() throws ServiceException {
		PortalSession session = getSession();
		List<MapDataSource> lesDataSource;

		try {
			lesDataSource = session.getRemoteMap().getAllMapsDataSource();
			return lesDataSource;
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Error during the recuperation of datasource : " + e.getMessage());
		}
	}

	@Override
	public void saveDataSet(MapDataSet dtS) throws ServiceException {
		PortalSession session = getSession();
		boolean exist = false;
		List<MapDataSet> lesDataSet = getMapsDataSet();
		for(MapDataSet dataSet : lesDataSet) {
			if(dataSet.getId() == dtS.getId()) {
				exist = true;
			}
			else if(dataSet.getIdDataSource() == dtS.getIdDataSource()) {
				exist = true;
			}
		}

		if(!exist) {
			try {
				session.getRemoteMap().saveMapDataSet(dtS);
			} catch(Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to save data set.", e);
			}
		}
	}

	public List<MapDataSet> getMapsDataSet() throws ServiceException {
		PortalSession session = getSession();
		List<MapDataSet> lesDataSet;

		try {
			lesDataSet = session.getRemoteMap().getAllMapsDataSet();
			return lesDataSet;
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Error during the recuperation of dataset : " + e.getMessage());
		}
	}

	@Override
	public List<MapDataSource> getDataSourceByName(String name) throws ServiceException {
		PortalSession session = getSession();
		List<MapDataSource> dataSource;
		try {
			dataSource = session.getRemoteMap().getMapDataSourceByName(name);
			return dataSource;
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Error during the recuperation of datasource : " + e.getMessage());
		}

	}

	@Override
	public void deleteMapDataSet(MapDataSet mapDataSet) throws ServiceException {
		PortalSession session = getSession();

		try {
			session.getRemoteMap().deleteMapDataSet(mapDataSet);
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete the Map data set.", e);
		}
	}

	public ArrayList<MapColumns> getDataSetMetaData(String login, String password, String url, String driver, String query) throws ServiceException {

		ArrayList<MapColumns> columns = new ArrayList<MapColumns>();
		try {
			VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(url, login, password, driver);
			VanillaPreparedStatement rs = connection.prepareQuery(query);
			ResultSetMetaData rsM = rs.getQueryMetadata(query);

			MapColumns mapColumns;
			for(int i = 1; i < rsM.getColumnCount() + 1; i++) {
				mapColumns = new MapColumns(rsM.getColumnName(i), rsM.getColumnTypeName(i));
				columns.add(mapColumns);
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
		return columns;
	}

	@Override
	// ne sert plus
	public List<MapFeatures> getOsmValues(MapVanilla selectedMap) throws Exception {
		PortalSession session = getSession();

		// MapVanilla map = null;
		MapDataSet selectedDataSet = selectedMap.getDataSetList().get(0);

		RemoteMapDefinitionService mapDefinitionService = new RemoteMapDefinitionService();
		mapDefinitionService.configure(session.getVanillaRuntimeUrl());

		// for(MapVanilla m : mapDefinitionService.getAllMapsVanilla()) {
		// if(m.getName().equals(selectedMap.getName())) {
		// map = m;
		// break;
		// }
		// }

		/*
		 * modifie par kevin monnery
		 */
		// generate the values
		HashMap<String, MapFeatures> vals = new HashMap<String, MapFeatures>();

		VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(selectedDataSet.getDataSource().getUrl(), selectedDataSet.getDataSource().getLogin(), selectedDataSet.getDataSource().getMdp(), selectedDataSet.getDataSource().getDriver());

		VanillaPreparedStatement stmt = connection.prepareQuery(selectedDataSet.getQuery());

		// map the zones
		ResultSet rs = stmt.executeQuery();

		while(rs.next()) {
			String zoneId = rs.getString(selectedDataSet.getIdZone());
			String lat = rs.getString(selectedDataSet.getLatitude());
			String longi = rs.getString(selectedDataSet.getLongitude());

			if(vals.get(zoneId) == null) {
				MapFeatures v = new MapFeatures();
				v.setGeoId(zoneId);
				v.setMap(selectedMap);
				vals.put(zoneId, v);
			}
			vals.get(zoneId).addLatitude(lat);
			vals.get(zoneId).addLongitude(longi);

		}

		rs.close();
		stmt.close();
		ConnectionManager.getInstance().returnJdbcConnection(connection);

		return new ArrayList<MapFeatures>(vals.values());
	}

	@Override
	public List<MapFeatures> getOsmValuesbyDataSet(MapVanilla map, MapDataSet selectedDataSet) throws Exception {
		PortalSession session = getSession();

		RemoteMapDefinitionService mapDefinitionService = new RemoteMapDefinitionService();
		mapDefinitionService.configure(session.getVanillaRuntimeUrl());

		/*
		 * modifie par kevin monnery
		 */
		// generate the values
		HashMap<String, MapFeatures> vals = new HashMap<String, MapFeatures>();

		Map<String, MapZone> zones = MapHelper.getMapZone(selectedDataSet, getSession().getVanillaApi(), mapDefinitionService);
		for(String key : zones.keySet()) {
			MapFeatures f = new MapFeatures();
			f.setGeoId(key);
			f.setLatitudes(zones.get(key).getLatitudes());
			f.setLongitudes(zones.get(key).getLongitudes());
			f.setMap(map);
			vals.put(key, f);
		}
//		VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(selectedDataSet.getDataSource().getUrl(), selectedDataSet.getDataSource().getLogin(), selectedDataSet.getDataSource().getMdp(), selectedDataSet.getDataSource().getDriver());
//
//		VanillaPreparedStatement stmt = connection.prepareQuery(selectedDataSet.getQuery());
//
//		// map the zones
//		ResultSet rs = stmt.executeQuery();
//
//		while(rs.next()) {
//			String zoneId = rs.getString(selectedDataSet.getIdZone());
//			String lat = rs.getString(selectedDataSet.getLatitude());
//			String longi = rs.getString(selectedDataSet.getLongitude());
//
//			if(vals.get(zoneId) == null) {
//				MapFeatures v = new MapFeatures();
//				v.setGeoId(zoneId);
//				v.setMap(map);
//				vals.put(zoneId, v);
//			}
//			vals.get(zoneId).addLatitude(lat);
//			vals.get(zoneId).addLongitude(longi);
//
//		}
//
//		rs.close();
//		stmt.close();
//		ConnectionManager.getInstance().returnJdbcConnection(connection);

		return new ArrayList<MapFeatures>(vals.values());
	}

	@Override
	public void addOrEditAlert(Alert alert) throws ServiceException {
		PortalSession session = getSession();

		try {
			if(alert.getId() > 0) {
				session.getRepositoryConnection().getAlertService().updateAlert(alert);
			}
			else {
				session.getRepositoryConnection().getAlertService().createAlert(alert);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add or edit the Alert.", e);
		}
	}

	@Override
	public void deleteAlert(Alert alert) throws ServiceException {
		PortalSession session = getSession();

		try {
			session.getRepositoryConnection().getAlertService().removeAlert(alert);
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete the Alert.", e);
		}
	}

	@Override
	public void deleteSubscriber(Subscriber subscriber) throws ServiceException {
		PortalSession session = getSession();

		try {
			session.getRepositoryConnection().getAlertService().removeSubscriber(subscriber);
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete the Alert.", e);
		}
	}

	@Override
	public void saveWidgets(List<Widgets> widgets) throws Exception {
		PortalSession session = getSession();

		List<Widgets> olds = session.getPreferencesManager().getWidgetsByUser(session.getUser().getId());
		List<Widgets> toRm = new ArrayList<Widgets>();
		for(Widgets old : olds) {
			boolean found = false;
			for(Widgets n : widgets) {
				if(old.getWidgetId() == n.getWidgetId()) {
					found = true;
					break;
				}
			}
			if(!found) {
				toRm.add(old);
			}
		}

		for(Widgets w : toRm) {
			session.getPreferencesManager().delWidget(w);
		}

		for(Widgets w : widgets) {
			w.setUser(session.getUser().getId());
			session.getPreferencesManager().addWidget(w);
		}

	}

	@Override
	public Map<RepositoryItem, List<Comment>> getCommentsbyAllItems() throws ServiceException {
		PortalSession session = getSession();

		try {
			bpm.vanilla.platform.core.repository.Repository rep = new bpm.vanilla.platform.core.repository.Repository(session.getRepositoryConnection());
			List<RepositoryItem> items = rep.getItems(""); // get all items
			// Collections.sort(items, new Comparator<RepositoryItem>() {
			// @Override
			// public int compare(RepositoryItem arg0, RepositoryItem arg1) {
			// return arg0.
			// }
			// });
			Map<RepositoryItem, List<Comment>> unsortMap = new HashMap<RepositoryItem, List<Comment>>();
			for(RepositoryItem item : items) {
				if(item.getType() != IRepositoryApi.FD_DICO_TYPE) {
					List<Comment> comments = session.getRepositoryConnection().getDocumentationService().getComments(session.getCurrentGroup().getId(), item.getId(), Comment.ITEM);
					for(Comment com : comments) {
						com.setUser(session.getVanillaApi().getVanillaSecurityManager().getUserById(com.getCreatorId()));
					}
					unsortMap.put(item, comments);
				}

			}

			// Tri de la map par rapport aux commentaires les plus recents
			List<Map.Entry<RepositoryItem, List<Comment>>> list = new LinkedList<Map.Entry<RepositoryItem, List<Comment>>>(unsortMap.entrySet());

			// Sort list with comparator, to compare the Map values
			Collections.sort(list, new Comparator<Map.Entry<RepositoryItem, List<Comment>>>() {
				public int compare(Map.Entry<RepositoryItem, List<Comment>> o1, Map.Entry<RepositoryItem, List<Comment>> o2) {
					return -((o1.getValue().size() > 0) ? getLastComment(o1.getValue()).getCreationDate() : new Date(0)).compareTo((o2.getValue().size() > 0) ? getLastComment(o2.getValue()).getCreationDate() : new Date(0));
				}

				private Comment getLastComment(List<Comment> value) {
					Collections.sort(value, new Comparator<Comment>() {
						public int compare(Comment c1, Comment c2) {
							return (c1.getCreationDate()).compareTo(c2.getCreationDate());
						}
					});

					return value.get(value.size() - 1);

				}
			});

			// Convert sorted map back to a Map
			Map<RepositoryItem, List<Comment>> sortedMap = new LinkedHashMap<RepositoryItem, List<Comment>>();
			for(Map.Entry<RepositoryItem, List<Comment>> entry : list) {
				sortedMap.put(entry.getKey(), entry.getValue());
			}

			return sortedMap;
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the comments for that item.", e);
		}
	}

	@Override
	public List<String> getAllRepositoryTypes() throws Exception {
		return Arrays.asList(IRepositoryApi.REPOSITORY_ITEM_TYPES_NAMES);
	}

	@Override
	public List<PortailItemFmdtDriller> loadFmdtQueries(int id) throws ServiceException {
		List<PortailItemFmdtDriller> queriesFmdt = new ArrayList<PortailItemFmdtDriller>();
		try {
			RepositoryItem i = null;

			i = getSession().getRepositoryConnection().getRepositoryService().getDirectoryItem(id);

			String result = null;

			result = getSession().getRepositoryConnection().getRepositoryService().loadModel(i);

			List<IBusinessModel> bModels = null;
			bModels = MetaDataReader.read(getSession().getCurrentGroup().getName(), IOUtils.toInputStream(result, "UTF-8"), getSession().getRepositoryConnection(), false);

			if(bModels != null) {
				for(IBusinessModel bmod : bModels) {
					FmdtModel modelDTO = new FmdtModel();
					modelDTO.setName(bmod.getName());

					List<IBusinessPackage> packages = bmod.getBusinessPackages(getSession().getCurrentGroup().getName());
					for(IBusinessPackage pack : packages) {
						if(pack.isExplorable()) {
							for(SavedQuery query : pack.getSavedQueries()) {
								RepositoryItem item = new RepositoryItem();
								item.setId(id);
								item.setItemName(query.getName());
								item.setInternalVersion(pack.getName());
								item.setPublicVersion(bmod.getName());
								
								PortailItemFmdtDriller driller = new PortailItemFmdtDriller(item, IRepositoryApi.TYPES_NAMES[IRepositoryApi.FMDT_DRILLER_TYPE]);
								if (query.hasChart()) {
									List<SavedChart> charts = query.loadChart();
									
									for (SavedChart chart : charts) {
										PortailItemFmdtChart chartItem = new PortailItemFmdtChart(item, IRepositoryApi.TYPES_NAMES[IRepositoryApi.FMDT_CHART_TYPE], chart);
										driller.addChart(chartItem);
									}
								}
								
								queriesFmdt.add(driller);
							}
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return queriesFmdt;
	}

	@Override
	public Map<String, MapZone> getMapZone(MapVanilla map) throws ServiceException {
		PortalSession session = getSession();

		RemoteMapDefinitionService mapDefinitionService = new RemoteMapDefinitionService();
		mapDefinitionService.configure(session.getVanillaRuntimeUrl());

		try {
			return MapHelper.getMapZone(map.getDataSetList().get(0), getSession().getVanillaApi(), mapDefinitionService);
		} catch(Exception e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public void saveMapMetadataMappings(ArrayList<MapZone> zones, MapVanilla map) throws ServiceException {
		PortalSession session = getSession();

		RemoteMapDefinitionService mapDefinitionService = new RemoteMapDefinitionService();
		mapDefinitionService.configure(session.getVanillaRuntimeUrl());
		try {
			mapDefinitionService.saveMetadataMappingsFromZones(zones, map);
		} catch(Exception e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
}