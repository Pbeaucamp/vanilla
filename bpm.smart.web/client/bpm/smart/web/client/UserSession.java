package bpm.smart.web.client;

import java.util.Date;

import bpm.gwt.commons.shared.InfoUser;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserSession implements IsSerializable {

	private InfoUser infoUser;
	private Date defaultDate;
	private String rlibs;

	private boolean copyPasteEnabled;
	
	public UserSession() { }

	public Date getDefaultDate() {
		return defaultDate;
	}

	public void setDefaultDate(Date defaultDate) {
		this.defaultDate = defaultDate;
	}

	public String getRlibs() {
		return rlibs;
	}

	public void setRlibs(String rlibs) {
		this.rlibs = rlibs;
	}

	public InfoUser getInfoUser() {
		return infoUser;
	}

	public void setInfoUser(InfoUser infoUser) {
		this.infoUser = infoUser;
	}

	public boolean isCopyPasteEnabled() {
		return copyPasteEnabled;
	}

	public void setCopyPasteEnabled(boolean copyPasteEnabled) {
		this.copyPasteEnabled = copyPasteEnabled;
	}
}
