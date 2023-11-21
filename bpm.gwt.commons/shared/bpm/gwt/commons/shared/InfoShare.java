package bpm.gwt.commons.shared;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InfoShare implements IsSerializable{
	
	private TypeShare typeShare;
	private TypeExport typeExport;
	private String itemName;
	private String format;
	private String emailText;
	
	private List<Group> selectedGroups;
	private List<Email> selectedEmails;
	
	private boolean isLandscape;
	
	public InfoShare() { }
	
	public InfoShare(TypeShare typeShare, TypeExport typeExport, String itemName, String format, List<Group> selectedGroups, List<Email> selectedEmails, String emailText, boolean isLandscape) {
		this.typeShare = typeShare;
		this.typeExport = typeExport;
		this.itemName = itemName;
		this.format = format;
		this.selectedGroups = selectedGroups;
		this.selectedEmails = selectedEmails;
		this.emailText = emailText;
		this.isLandscape = isLandscape;
	}
	
	public TypeExport getTypeExport() {
		return typeExport;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public TypeShare getTypeShare() {
		return typeShare;
	}
	
	public String getFormat() {
		return format;
	}
	
	public String getEmailText() {
		return emailText;
	}
	
	public List<Group> getSelectedGroups() {
		return selectedGroups != null ? selectedGroups : new ArrayList<Group>();
	}
	
	public List<Email> getSelectedEmails() {
		return selectedEmails != null ? selectedEmails : new ArrayList<Email>();
	}
	
	public boolean isLandscape() {
		return isLandscape;
	}

	public void setTypeShare(TypeShare typeShare) {
		this.typeShare = typeShare;
	}
	
	
}
