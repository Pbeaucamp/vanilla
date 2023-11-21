package bpm.vanilla.portal.server;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonConfiguration;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.Security;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.beans.ArchiveType;
import bpm.vanilla.platform.core.beans.ArchiveTypeItem;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.alerts.ActionMail;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Subscriber;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.services.AdminService;
import bpm.vanilla.portal.server.security.PortalSession;
//import bpm.vanilla.portal.shared.Action;
//import bpm.vanilla.portal.shared.Alert;
//import bpm.vanilla.portal.shared.Event;
//import bpm.vanilla.portal.shared.Subscriber;



import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AdminServiceImpl extends RemoteServiceServlet implements AdminService {

	public Logger logger = Logger.getLogger(getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = -4603408217460152647L;

	private CommonConfiguration portalConfig;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.info("Initing AdminServiceImpl ...");
		try {
			portalConfig = CommonConfiguration.getInstance();
		} catch (Exception e) {
			logger.error("Failed to init AdminServiceImpl, reason : " + e.getMessage(), e);
			throw new ServletException("Failed to init AdminServiceImpl, reason : " + e.getMessage(), e);
		}
		logger.info("AdminServiceImpl is ready");
	}

	private PortalSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), PortalSession.class);
	}

	public void close() {
		// logger.info("close session");
		//
		// try {
		// PortalSession session = getSession();
		//
		// if (session != null) {
		// logger.info("Portal Session not null");
		//
		// session.clearReports();
		// session.clearBackgroundReport();
		// }
		// } catch (ServiceException e) { }
	}

	// /**
	// * Actually for background run reports
	// */
	// @Override
	// public List<BackgroundItemDTO> getBackgroundReports() throws ServiceException {
	// PortalSession session = getSession();
	// ReportingComponent reportingComponent = session.getReportingComponent();
	//
	// List<BackgroundItemDTO> reports = new ArrayList<BackgroundItemDTO>();
	//
	// HashMap<Integer, BackgroundItemDTO> hashBackReports = session.getBackgroundReports();
	// if(hashBackReports != null){
	// for(Integer itemId : hashBackReports.keySet()){
	// BackgroundItemDTO report = hashBackReports.get(itemId);
	// if(report.getKeys() != null){
	// for(Key key : report.getKeys()){
	// if(!key.isReady()){
	// key.setReady(loadBackgroundReport(session, reportingComponent, report, key));
	// }
	// }
	// }
	// reports.add(report);
	// }
	// }
	// return reports;
	// }
	//
	// private boolean loadBackgroundReport(PortalSession session, ReportingComponent reportComp,
	// BackgroundItemDTO backItem, Key key){
	//
	// IRunIdentifier runId = new SimpleRunIdentifier(key.getKey());
	//
	// try {
	// if(reportComp.checkRunAsynchState(runId)){
	//
	// InputStream is = reportComp.loadGeneratedReport(runId);
	// byte currentXMLBytes[] = IOUtils.toByteArray(is);
	// ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
	//
	// if(session.getReport(backItem.getReportKey()) != null){
	// session.getReport(backItem.getReportKey()).addStream(key.getOutputFormat(), byteArrayIs);
	// }
	// else {
	// ObjectInputStream repIS = new ObjectInputStream();
	// repIS.addStream(key.getOutputFormat(), byteArrayIs);
	// session.addReport(backItem.getReportKey(), repIS);
	// }
	//
	// is.close();
	//
	// if(backItem.isHistorize()) {
	// //ged time mate!
	// logger.info("Report has historization/ged set, sending to ged...");
	// byteArrayIs.reset();
	// prepareGedConfig(session, backItem.getId(), key.getOutputFormat(),
	// backItem.getHistoName(), byteArrayIs, backItem.getGroupIds());
	// logger.info("Ged is finished");
	// }
	// else {
	// logger.info("Report had no historization/ged set, skipping sent to ged");
	// }
	//
	// logger.info("Adding report run in background with identifier = " + key.getKey()
	// + " and name = " + backItem.getName());
	//
	// return true;
	// }
	//
	// return false;
	// } catch (Exception e) {
	// e.printStackTrace();
	//
	// logger.error("Unable to retrieve report run in background with identifier = " + key.getKey(), e);
	//
	// return false;
	// }
	// }
	//
	// /**
	// * Adds the generated report in GED if needed
	// *
	// * @param session
	// * @param dirItemId
	// * @param format
	// * @param gedName
	// * @param is
	// * @throws Exception
	// */
	// private void prepareGedConfig(PortalSession session, int dirItemId, String format, String gedName,
	// InputStream is, List<Integer> groupIds) throws Exception {
	// //create comProps
	// IGedComponent gedComponent = session.getGedComponent();
	//
	// List<Definition> defs = gedComponent.getFieldDefinitions(false);
	//
	// ComProperties comProps = new ComProperties();
	//
	// Definition cat = findFieldByName(RuntimeFields.CATEGORY.getName(), defs);
	// comProps.setProperty(cat, "0");
	//
	// Definition author = findFieldByName(RuntimeFields.AUTHOR.getName(), defs);
	// comProps.setProperty(author, session.getUser().getLogin());
	//
	// Definition t = findFieldByName(RuntimeFields.TITLE.getName(), defs);
	// comProps.setProperty(t, gedName);
	//
	// Definition v = findFieldByName(RuntimeFields.VERSION.getName(), defs);
	// comProps.setProperty(v, "1");
	//
	// //onlyHistoric is NOT used apparently
	// GedIndexRuntimeConfig config = new GedIndexRuntimeConfig(comProps,
	// session.getUser().getId(),
	// session.getCurrentGroup().getId(),
	// groupIds, session.getCurrentRepository().getId(),
	// format, null, -1);
	//
	// gedComponent.index(config, is);
	// }
	//
	// private Definition findFieldByName(String fieldName, List<Definition> defs) {
	// for (Definition def : defs) {
	// if (def.getName().equals(fieldName)) {
	// return def;
	// }
	// }
	//
	// return null;
	// }

	@Override
	public void changeUserMail(String newMail, boolean privateMail) throws ServiceException {
		PortalSession session = getSession();

		User user = session.getUser();
		try {
			portalConfig.modifyMail(user, newMail, privateMail);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("An error happend during mail changed operation: ", e);
		}
	}

	public PortailRepositoryItem getDirectoryItemById(PortailRepositoryItem item) throws ServiceException {
		PortalSession session = getSession();

		try {
			if (item.getItem().getOwnerId() != 0) {
				item.setCreatedBy(session.getVanillaApi().getVanillaSecurityManager().getUserById(item.getItem().getOwnerId()).getLogin());
			}
		} catch (Exception e) {
		}

		try {
			if (item.getItem().getModifiedBy() != null) {
				User usMod = session.getVanillaApi().getVanillaSecurityManager().getUserById(item.getItem().getModifiedBy());
				if (usMod != null) {
					item.setModifiedBy(usMod.getLogin());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return item;
	}

	public void updateItemProperties(PortailRepositoryItem item) throws ServiceException {

		PortalSession session = getSession();

		RepositoryItem resItem = null;
		try {
			resItem = session.getRepositoryConnection().getRepositoryService().getDirectoryItem(item.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		resItem.setItemName(item.getName());
		resItem.setDisplay(item.getItem().isDisplay());
		resItem.setAvailableGed(item.getItem().isAvailableGed());
		resItem.setRealtimeGed(item.getItem().isRealtimeGed());

		resItem.setInternalVersion(item.getItem().getInternalVersion());
		resItem.setPublicVersion(item.getItem().getPublicVersion());

		try {
			session.getRepositoryConnection().getAdminService().update(resItem);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public HashMap<String, HashMap<String, HashMap<String, String>>> getVanillaDocumentationPaths() throws ServiceException {

		HashMap<String, HashMap<String, HashMap<String, String>>> documentPaths = null;

		String helpUrl = portalConfig.getHelpUrl();
		String documentationFile = helpUrl != null ? helpUrl + "help.properties" : "help/help.properties";

		logger.info("Loading documentation paths");
		Properties docProperties = null;
		try {
			FileInputStream fis = new FileInputStream(documentationFile);
			docProperties = new Properties();
			docProperties.load(fis);
			fis.close();
			logger.info("Documentation paths loaded.");

			if (docProperties != null) {

				documentPaths = new HashMap<String, HashMap<String, HashMap<String, String>>>();

				for (String docProp : docProperties.stringPropertyNames()) {
					String[] props = docProp.split("\\.");
					String type = props[0];
					String name = props[2];
					String locale = props[1];
					String path = docProperties.getProperty(docProp);

					List<String> namePath = new ArrayList<String>();
					namePath.add(locale);
					namePath.add(path);

					if (!documentPaths.containsKey(type)) {
						documentPaths.put(type, new HashMap<String, HashMap<String, String>>());
					}

					if (!documentPaths.get(type).containsKey(name)) {
						documentPaths.get(type).put(name, new HashMap<String, String>());
					}

					documentPaths.get(type).get(name).put(locale, path);
				}
			}
		} catch (Exception ex) {
			String msg = "Failed to load configuration file @" + documentationFile + ", reason : " + ex.getMessage();
			logger.error(msg, ex);
			documentPaths = null;
		}

		return documentPaths;
	}

	@Override
	public List<Alert> getAllAlerts() throws ServiceException {
		PortalSession session = getSession();
		List<Alert> alerts;
		try {
			alerts = session.getRepositoryConnection().getAlertService().getAlerts();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get alerts: " + e.getMessage());
		}
		return alerts;
	}

	// private List<Event> changeToPortalEvent(List<bpm.vanilla.platform.core.beans.alerts.Event> events) {
	// List<Event> portalEvents = new ArrayList<Event>();
	// if(events != null){
	// for(bpm.vanilla.platform.core.beans.alerts.Event event : events){
	// Event newEvent = new Event();
	// newEvent.setAlerts(changeToPortalAlert(event.getAlerts()));
	// newEvent.setDescription(event.getDescription());
	// newEvent.setDirectoryItemId(event.getDirectoryItemId());
	// newEvent.setId(event.getId());
	// newEvent.setName(event.getName());
	// newEvent.setRepositoryId(event.getRepositoryId());
	// newEvent.setSubtypeEvent(event.getSubtypeEvent());
	// newEvent.setTypeEvent(event.getTypeEvent());
	// portalEvents.add(newEvent);
	// }
	// }
	// return portalEvents;
	// }
	//
	// private List<Alert> changeToPortalAlert(List<bpm.vanilla.platform.core.beans.alerts.Alert> alerts) {
	// List<Alert> portalAlerts = new ArrayList<Alert>();
	// if(alerts != null){
	// for(bpm.vanilla.platform.core.beans.alerts.Alert alert : alerts){
	// Alert newAlert = new Alert();
	// newAlert.setAction(changeToPortalAction(alert.getAction()));
	// newAlert.setDescription(alert.getDescription());
	// newAlert.setEventId(alert.getEventId());
	// newAlert.setId(alert.getId());
	// newAlert.setName(alert.getName());
	// newAlert.setState(alert.getState());
	// newAlert.setSubscribers(changeToPortalSubscribers(alert.getSubscribers()));
	// newAlert.setTypeAction(alert.getTypeAction());
	// portalAlerts.add(newAlert);
	// }
	// }
	// return portalAlerts;
	// }
	//
	// private List<Subscriber> changeToPortalSubscribers(List<bpm.vanilla.platform.core.beans.alerts.Subscriber> subscribers) {
	// List<Subscriber> portalSubscribers = new ArrayList<Subscriber>();
	// if(subscribers != null){
	// for(bpm.vanilla.platform.core.beans.alerts.Subscriber subscriber : subscribers){
	// Subscriber newSubscriber = new Subscriber();
	// newSubscriber.setAlertId(subscriber.getAlertId());
	// newSubscriber.setId(subscriber.getId());
	// newSubscriber.setUserId(subscriber.getUserId());
	// newSubscriber.setUserMail(subscriber.getUserMail());
	// newSubscriber.setUserName(subscriber.getUserName());
	// portalSubscribers.add(newSubscriber);
	// }
	// }
	// return portalSubscribers;
	// }
	//
	// private Action changeToPortalAction(bpm.vanilla.platform.core.beans.alerts.Action action) {
	// if(action != null){
	// Action newAction = new Action();
	// newAction.setAlertId(action.getAlertId());
	// newAction.setContent(action.getContent());
	// newAction.setDirectoryItemId(action.getDirectoryItemId());
	// newAction.setId(action.getId());
	// newAction.setRepositoryId(action.getRepositoryId());
	// newAction.setSmtpHost(action.getSmtpHost());
	// newAction.setSmtpLogin(action.getSmtpLogin());
	// newAction.setSmtpPassword(action.getSmtpPassword());
	// newAction.setSubject(action.getSubject());
	// return newAction;
	// }
	// return null;
	// }

	@Override
	public void updateUser(User user, boolean changePassword, String oldPassword, String newPassword) throws ServiceException {
		PortalSession session = getSession();

		if (changePassword) {
			if (!user.getPassword().equals(Security.encodeMD5(oldPassword))) {
				throw new ServiceException("Wrong password for user " + user.getLogin());
			}

			user.setPassword(Security.encodeMD5(newPassword));
			user.setDatePasswordModification(new Date());
			user.setPasswordChange(0);
			user.setPasswordReset(0);

			CommonConfiguration config = CommonConfiguration.getInstance();
			String rootUser = config.getRootUser();
			if (user.getLogin().equals(rootUser)) {
				config.setRootPassword(newPassword);
			}
		}

		try {
			session.getVanillaApi().getVanillaSecurityManager().updateUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to update user : " + e.getMessage());
		}
	}

	@Override
	public ArchiveType saveArchiveType(ArchiveType archiveType) throws ServiceException {
		PortalSession session = getSession();
		try {
			if (archiveType.getId() > 0) {
				return session.getVanillaApi().getArchiveManager().updateArchiveType(archiveType);
			}
			else {
				return session.getVanillaApi().getArchiveManager().addArchiveType(archiveType);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public List<ArchiveType> getArchiveTypes() throws ServiceException {
		PortalSession session = getSession();
		try {
			return session.getVanillaApi().getArchiveManager().getArchiveTypes();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public ArchiveTypeItem getArchiveTypeForItem(int itemId, boolean isDirectory) throws ServiceException {
		PortalSession session = getSession();
		try {
			return session.getVanillaApi().getArchiveManager().getArchiveTypeByItem(itemId, session.getRepositoryConnection().getContext().getRepository().getId(), isDirectory);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public ArchiveTypeItem addArchiveTypeToItem(int itemId, int archiveTypeId, boolean isDirectory, boolean applyToChildren) throws ServiceException {
		PortalSession session = getSession();
		try {

			if (applyToChildren) {
				RepositoryDirectory dir = session.getRepository().getDirectory(itemId);
				List<IRepositoryObject> objects = session.getRepository().getDirectoryContent(dir);

				for (IRepositoryObject object : objects) {
					if (object instanceof RepositoryDirectory) {
						addArchiveTypeToItem(object.getId(), archiveTypeId, true, applyToChildren);
					}
					else {
						addArchiveTypeToItem(object.getId(), archiveTypeId, false, false);
					}
				}

			}

			return session.getVanillaApi().getArchiveManager().linkItemArchiveType(archiveTypeId, itemId, session.getRepositoryConnection().getContext().getRepository().getId(), isDirectory);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public List<Alert> getAlertsByTypeAndDirectoryItemId(TypeEvent typeEvent, int directoryItemId, int repositoryId) throws ServiceException {
		PortalSession session = getSession();
		List<Alert> alerts;
		try {
			alerts = session.getRepositoryConnection().getAlertService().getAlertsByTypeAndDirectoryItemId(typeEvent, directoryItemId, repositoryId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get alerts: " + e.getMessage(), e);
		}
		return alerts;
	}

	@Override
	public void addSubscriber(Alert alert) throws ServiceException {
		PortalSession session = getSession();
		User user = biPortal.get().getInfoUser().getUser();
		Subscriber sub = new Subscriber();
		sub.setUserId(user.getId());
		sub.setUserMail(user.getBusinessMail());
		sub.setUserName(user.getName());

		((ActionMail) alert.getAction().getActionObject()).addSubscriber(sub);
		try {
			session.getRepositoryConnection().getAlertService().updateAlert(alert);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to update alerts: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteArchiveType(ArchiveType selectedObject) throws ServiceException {
		PortalSession session = getSession();
		try {
			session.getVanillaApi().getArchiveManager().deleteArchiveType(selectedObject);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete archiveType : " + e.getMessage(), e);
		}
	}
}
