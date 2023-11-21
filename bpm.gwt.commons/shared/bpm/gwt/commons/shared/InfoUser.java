package bpm.gwt.commons.shared;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InfoUser implements IsSerializable {

	private String sessionId;
	private String vanillaRuntimeUrl;
	private String vanillaRuntimeExternalUrl;

	private User user;
	private Group group;
	private Repository repository;
	
	private Date userLastConnectionDate;

	private List<Group> availableGroups;
	
	private boolean isConnectedToVanilla;

	private boolean canExportDashboard;
	private String urlDeconnect;

	private String updateManagerUrl;
	private boolean feedbackIsNotInited;
	private boolean canSendFeedback;

	private PortailRepositoryDirectory openOnStartup;
	private PortailRepositoryDirectory myWatchlist;
	private PortailRepositoryDirectory reportBackgrounds;
	private boolean waitReportBackground;

	private HashMap<String, InfoWebapp> infoWebapps;

	private List<ClassDefinition> classes;
	
	private String wopiServiceUrl;

	public InfoUser() {
		this.infoWebapps = new HashMap<String, InfoWebapp>();
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}
	
	public void setVanillaRuntimeUrl(String vanillaRuntimeUrl) {
		this.vanillaRuntimeUrl = vanillaRuntimeUrl;
	}
	
	public String getVanillaRuntimeUrl() {
		return vanillaRuntimeUrl;
	}
	
	public void setVanillaRuntimeExternalUrl(String vanillaRuntimeExternalUrl) {
		this.vanillaRuntimeExternalUrl = vanillaRuntimeExternalUrl;
	}
	
	public String getVanillaRuntimeExternalUrl() {
		return vanillaRuntimeExternalUrl != null && !vanillaRuntimeExternalUrl.isEmpty() ? vanillaRuntimeExternalUrl : vanillaRuntimeUrl;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public List<Group> getAvailableGroups() {
		return availableGroups;
	}

	public void setAvailableGroups(List<Group> availableGroups) {
		this.availableGroups = availableGroups;
	}
	
	public boolean isConnectedToVanilla() {
		return isConnectedToVanilla;
	}
	
	public void setConnectedToVanilla(boolean isConnectedToVanilla) {
		this.isConnectedToVanilla = isConnectedToVanilla;
	}
	
	public void registerWebapp(InfoWebapp webapp) {
		this.infoWebapps.put(webapp.getName(), webapp);
	}
	
	public boolean isConnected(String webappName) {
		return infoWebapps.get(webappName) != null ? infoWebapps.get(webappName).isConnected() : false;
	}
	
	public boolean canAccess(String webappName) {
		return infoWebapps.get(webappName) != null ? infoWebapps.get(webappName).canAccess() : false;
	}
	
	public String getUrl(String webappName) {
		return infoWebapps.get(webappName) != null ? infoWebapps.get(webappName).getUrl() : "";
	}

	public void clearWebapps() {
		this.infoWebapps = new HashMap<String, InfoWebapp>();
	}

	public boolean canExportDashboard() {
		return canExportDashboard;
	}

	public void setCanExportDashboard(boolean canExportDashboard) {
		this.canExportDashboard = canExportDashboard;
	}

	public String getUrlDeconnect() {
		return urlDeconnect;
	}

	public void setUrlDeconnect(String urlDeconnect) {
		this.urlDeconnect = urlDeconnect;
	}

	public boolean feedbackIsNotInited() {
		return feedbackIsNotInited;
	}

	public void setFeedbackIsNotInited(boolean feedbackIsNotInited) {
		this.feedbackIsNotInited = feedbackIsNotInited;
	}

	public boolean canSendFeedback() {
		return canSendFeedback;
	}

	public void setCanSendFeedback(boolean canSendFeedback) {
		this.canSendFeedback = canSendFeedback;
	}

	public String getUpdateManagerUrl() {
		return updateManagerUrl;
	}

	public void setUpdateManagerUrl(String updateManagerUrl) {
		this.updateManagerUrl = updateManagerUrl;
	}

	public void setOpenOnStartup(PortailRepositoryDirectory openOnStartup) {
		this.openOnStartup = openOnStartup;
	}

	public boolean isOpenOnStartup(RepositoryItem item) {
		if (openOnStartup != null && openOnStartup.getItems() != null) {
			for (IRepositoryObject open : openOnStartup.getItems()) {
				if (open instanceof PortailRepositoryItem && ((PortailRepositoryItem) open).getId() == item.getId()) {
					return true;
				}
			}
		}
		return false;
	}

	public void setMyWatchlist(PortailRepositoryDirectory myWatchlist) {
		this.myWatchlist = myWatchlist;
	}

	public boolean isOnWatchlist(RepositoryItem item) {
		if (myWatchlist != null && myWatchlist.getItems() != null) {
			for (IRepositoryObject watch : myWatchlist.getItems()) {
				if (watch instanceof PortailRepositoryItem && ((PortailRepositoryItem) watch).getId() == item.getId()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public PortailRepositoryDirectory getReportBackgrounds() {
		return reportBackgrounds;
	}
	
	public void setReportBackgrounds(PortailRepositoryDirectory reportBackgrounds) {
		this.reportBackgrounds = reportBackgrounds;
	}

	public boolean isWaitReportBackground() {
		return waitReportBackground;
	}

	public void setWaitReportBackground(boolean waitReportBackground) {
		this.waitReportBackground = waitReportBackground;
	}

	public Date getUserLastConnectionDate() {
		return userLastConnectionDate;
	}
	
	public void setUserLastConnectionDate(Date userLastConnectionDate) {
		this.userLastConnectionDate = userLastConnectionDate;
	}
	
	public void setClasses(List<ClassDefinition> classes) {
		this.classes = classes;
	}
	
	public List<ClassDefinition> getClasses() {
		return classes;
	}

	public String getWopiServiceUrl() {
		return wopiServiceUrl;
	}

	public void setWopiServiceUrl(String wopiServiceUrl) {
		this.wopiServiceUrl = wopiServiceUrl;
	}
}
