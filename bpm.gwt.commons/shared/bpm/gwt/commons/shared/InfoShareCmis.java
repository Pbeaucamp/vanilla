package bpm.gwt.commons.shared;

import java.util.ArrayList;
import java.util.HashMap;

import bpm.gwt.commons.shared.cmis.CmisFolder;
import bpm.gwt.commons.shared.cmis.CmisInformations;
import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.vanilla.platform.core.beans.Group;

public class InfoShareCmis extends InfoShare {
	
	private CmisInformations cmisInfos;
	private CmisFolder selectedFolder;
	
	private String key;

	//Dashboard informations
	private String dashboardUrl;
	private HashMap<String, String> selectedFolders;
	
	public InfoShareCmis() { }
	
	public InfoShareCmis(TypeExport typeExport, CmisInformations cmisInfos, CmisFolder selectedFolder, String key, String itemName, String format, boolean isLandscape) {
		super(TypeShare.CMIS, typeExport, itemName, format, new ArrayList<Group>(), new ArrayList<Email>(), "", isLandscape);
		this.cmisInfos = cmisInfos;
		this.selectedFolder = selectedFolder;
		this.key = key;
	}
	
	public CmisInformations getCmisInfos() {
		return cmisInfos;
	}
	
	public CmisFolder getSelectedFolder() {
		return selectedFolder;
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
