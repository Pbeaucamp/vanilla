package bpm.gwt.commons.shared;

import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.vanilla.platform.core.beans.Group;

public class InfoShareMail extends InfoShare {

	private String key;

	//Dashboard informations
	private String dashboardUrl;
	private HashMap<String, String> selectedFolders;
	
	public InfoShareMail() { }
	
	public InfoShareMail(TypeExport typeExport, String key, String itemName, String format, List<Group> selectedGroups, List<Email> selectedEmails, String emailText, boolean isLandscape) {
		super(TypeShare.EMAIL, typeExport, itemName, format, selectedGroups, selectedEmails, emailText, isLandscape);
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

	public String getDashboardUrl() {
		return dashboardUrl;
	}

	public void setDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}

	public HashMap<String, String> getSelectedFolders() {
		return selectedFolders;
	}

	public void setSelectedFolders(HashMap<String, String> selectedFolders) {
		this.selectedFolders = selectedFolders;
	}
}
