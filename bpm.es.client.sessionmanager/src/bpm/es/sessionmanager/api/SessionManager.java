package bpm.es.sessionmanager.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adminbirep.Activator;
import bpm.es.sessionmanager.Messages;
import bpm.es.sessionmanager.api.server.VanillaServer;
import bpm.es.sessionmanager.tools.OurLogger;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.VanillaLogsProps;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.repository.Repository;

/**
 * Api entry class to process and access vanilla and repository logs
 * 
 * @author ereynard
 * 
 */
public class SessionManager {
	// Users
	private List<VanillaSession> activeSessions = null;
	private List<UserWrapper> users = null;
	private List<User> usersRaw = null;
	// Server
	private VanillaServer server;

	private Repository rep;
	private List<VanillaLogs> logs;

	public SessionManager(Repository rep) {
		OurLogger.info("Starting SessionManager's API"); //$NON-NLS-1$
		this.rep = rep;
	}

	public List<UserWrapper> getUsers() {
		return users;
	}

	public VanillaServer getVanillaServer() throws Exception {
		server = new VanillaServer();

		return server;
	}

	public List<VanillaLogsProps> getPropertiesForVanillaLog(VanillaLogs log) throws Exception {
		return Activator.getDefault().getVanillaApi().getVanillaLoggingManager().getVanillaLogsPropertiesForLogId(log.getId());
	}

	public void loadData() throws Exception {

		OurLogger.info("Loading users..."); //$NON-NLS-1$
		loadUsers();

		OurLogger.info("Loading items..."); //$NON-NLS-1$

		OurLogger.info("Loading repository logs..."); //$NON-NLS-1$

		OurLogger.info("Loading vanilla logs..."); //$NON-NLS-1$
		loadVanillaLogs();
	}

	private void loadVanillaLogs() throws Exception {

		try {
			logs = Activator.getDefault().getVanillaApi().getVanillaLoggingManager().getListVanillaLogs();
		} catch(Exception e) {
			throw new Exception(Messages.SessionManager_12 + e.getMessage(), e);
		}
	}

	private void loadUsers() throws Exception {
		try {
			activeSessions = Activator.getDefault().getVanillaApi().getVanillaSystemManager().getActiveSessions();
		} catch(Exception e) {
			throw new Exception(Messages.SessionManager_27 + e.getMessage(), e);
		}

		try {
			usersRaw = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsers();
			users = new ArrayList<UserWrapper>();

			for(User u : usersRaw) {
				UserWrapper wrappedUser = new UserWrapper(u);

				VanillaSession session = sessionsContains(u);

				if(session != null) {
					wrappedUser.setConnected(true);
					wrappedUser.setConnectedSince(session.getCreationDate());
					wrappedUser.setLastAction(new Date(session.getCreationDate().getTime() + session.getTimeOut()));
				}

				users.add(wrappedUser);
			}

		} catch(Exception e) {
			throw new Exception(Messages.SessionManager_28 + e.getMessage(), e);
		}
	}

	private VanillaSession sessionsContains(User user) {
		for(VanillaSession session : activeSessions) {
			if(session.getUser().getId().intValue() == user.getId().intValue())
				return session;
		}
		return null;
	}
	
	public List<VanillaLogs> getVanillaLogs() {
		return logs;
	}
	
	public IndexedDay getVanillaLogsByDay() throws Exception {
		IndexedDay indexDay = new IndexedDay();
		for(VanillaLogs log : logs) {
			int dirItemId = log.getDirectoryItemId();

			if(dirItemId > 0) {
				String dirItemName = rep.getItem(dirItemId) != null ? rep.getItem(dirItemId).getItemName() : null;
				if(dirItemName == null) {
					dirItemName = Messages.SessionManager_15;
				}

				log.setObjectName(dirItemName);
			}

			indexDay.indexSessionLog(log);
		}
		return indexDay;
	}
	
	public IndexedApplication getVanillaLogsByApplication() {
		IndexedApplication result = new IndexedApplication();
		
		for(VanillaLogs log : logs) {
			
			String app = log.getObjectType();

			if(app != null && log.getApplication().equals("bpm.vanilla.repository")) {

				result.indexSessionLog(log);
			}

		}
		
		return result;
	}
	
	public IndexedObjectId getVanillaLogsByItems() throws Exception {
		IndexedObjectId result = new IndexedObjectId();
		
		for(VanillaLogs log : logs) {
			
			int dirItemId = log.getDirectoryItemId();

			if(dirItemId > 0) {
				String dirItemName = rep.getItem(dirItemId) != null ? rep.getItem(dirItemId).getItemName() : null;
				if(dirItemName == null) {
					dirItemName = Messages.SessionManager_15;
				}

				log.setObjectName(dirItemName);
				result.indexSessionLog(log);
			}

		}
		
		return result;
	}

	public IndexedDay getFilteredVanillaLogs(User selectedUser, Level selectedLevel, Calendar startDateCalendar, Calendar endDateCalendar, String text) throws Exception {
		IndexedDay indexDay = new IndexedDay();
		for(VanillaLogs log : logs) {
			
			if(respectFilter(log, selectedUser, selectedLevel, startDateCalendar, endDateCalendar, text)) {
			
				int dirItemId = log.getDirectoryItemId();
	
				if(dirItemId > 0) {
					String dirItemName = rep.getItem(dirItemId) != null ? rep.getItem(dirItemId).getItemName() : null;
					if(dirItemName == null) {
						dirItemName = Messages.SessionManager_15;
					}
	
					log.setObjectName(dirItemName);
				}
	
				indexDay.indexSessionLog(log);
			}
		}
		return indexDay;
	}

	private boolean respectFilter(VanillaLogs log, User selectedUser, Level selectedLevel, Calendar startDateCalendar, Calendar endDateCalendar, String text) {
		
		if(selectedUser != null && log.getUserId() != selectedUser.getId().intValue()) {
			return false;
		}
		if(selectedLevel != null && selectedLevel.getLevelId() > Level.valueOf(log.getLevel()).getLevelId()) {
			return false;
		}
		if(startDateCalendar != null && log.getDate().before(startDateCalendar.getTime())) {
			return false;
		}
		if(endDateCalendar != null && log.getDate().after(endDateCalendar.getTime())) {
			return false;
		}
		if(text != null && !text.isEmpty() && !log.getMessage().contains(text)) {
			return false;
		}
		
		return true;
	}

	public IndexedComponent getVanillaLogsByComponent() throws Exception {
		IndexedComponent indexDay = new IndexedComponent();
		for(VanillaLogs log : logs) {
			
			int dirItemId = log.getDirectoryItemId();

			if(dirItemId > 0) {
				String dirItemName = rep.getItem(dirItemId) != null ? rep.getItem(dirItemId).getItemName() : null;
				if(dirItemName == null) {
					dirItemName = Messages.SessionManager_15;
				}

				log.setObjectName(dirItemName);
			}

			indexDay.indexSessionLog(log);
		}
		return indexDay;
	}
}
