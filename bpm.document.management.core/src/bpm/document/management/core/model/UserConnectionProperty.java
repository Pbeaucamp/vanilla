package bpm.document.management.core.model;

import java.io.Serializable;

public class UserConnectionProperty implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private boolean aklaFlow;
	private boolean aklaSync;
	private boolean mobileApps;
	private boolean msAddIn;
	private int userId;
	
	public UserConnectionProperty() {
		// TODO Auto-generated constructor stub
	}

	public UserConnectionProperty(boolean aklaFlow, boolean aklaSync, boolean mobileApps, boolean msAddIn, int userId) {
		super();
		this.aklaFlow = aklaFlow;
		this.aklaSync = aklaSync;
		this.mobileApps = mobileApps;
		this.msAddIn = msAddIn;
		this.userId = userId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isAklaFlow() {
		return aklaFlow;
	}

	public void setAklaFlow(boolean aklaFlow) {
		this.aklaFlow = aklaFlow;
	}

	public boolean isAklaSync() {
		return aklaSync;
	}

	public void setAklaSync(boolean aklaSync) {
		this.aklaSync = aklaSync;
	}

	public boolean isMobileApps() {
		return mobileApps;
	}

	public void setMobileApps(boolean mobileApps) {
		this.mobileApps = mobileApps;
	}

	public boolean isMsAddIn() {
		return msAddIn;
	}

	public void setMsAddIn(boolean msAddIn) {
		this.msAddIn = msAddIn;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	
	
}
