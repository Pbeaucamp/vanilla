package bpm.update.manager.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UpdateInformations implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String patchName;
	private String patchVersion;
	
	private List<Update> updateHistory;
	private List<Update> updates; 
	private List<ApplicationUpdateInformation> appInfos;

	public UpdateInformations() {
	}

	public UpdateInformations(String patchName, String patchVersion, List<Update> updateHistory) {
		this.patchName = patchName;
		this.patchVersion = patchVersion;
		this.updateHistory = updateHistory;
	}

	public void addAppInfos(ApplicationUpdateInformation appInfo) {
		if (appInfos == null) {
			this.appInfos = new ArrayList<ApplicationUpdateInformation>();
		}
		this.appInfos.add(appInfo);
	}

	public List<ApplicationUpdateInformation> getAppInfos() {
		return appInfos;
	}

	public void setAppInfos(List<ApplicationUpdateInformation> appInfos) {
		this.appInfos = appInfos;
	}
	
	public String getPatchName() {
		return patchName;
	}
	
	public String getPatchVersion() {
		return patchVersion;
	}
	
	public void setPatchVersion(String patchVersion) {
		this.patchVersion = patchVersion;
	}
	
	public List<Update> getUpdateHistory() {
		return updateHistory;
	}

	public void addUpdate(Update update) {
		if (updates == null) {
			this.updates = new ArrayList<Update>();
		}
		this.updates.add(update);
	}
	
	public List<Update> getUpdates() {
		return updates;
	}

	public boolean hasUpdate() {
		return updates != null && !updates.isEmpty();
	}

	public void clearUpdates() {
		this.updates = new ArrayList<Update>();
	}
}
