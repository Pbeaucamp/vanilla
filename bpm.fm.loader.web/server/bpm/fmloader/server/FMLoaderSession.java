package bpm.fmloader.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.fm.api.IFreeMetricsManager;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.remote.MdmRemote;

public class FMLoaderSession extends CommonSession {

//	private static FMLoaderSession instance;
	private String username;
	private String password;
	private IFreeMetricsManager manager;
	private List<String> jsps = new ArrayList<String>();
	private boolean isEncrypted;
	private Date selectedDate;

	private IMdmProvider mdmRemote;

	public FMLoaderSession() {
		
	}
	
	public IFreeMetricsManager getManager() {
		return manager;
	}

	public void setManager(IFreeMetricsManager manager) {
		this.manager = manager;
	}

	public IMdmProvider getMdmRemote() {
		if (mdmRemote == null) {
			String login = getUser().getLogin();
			String password = getUser().getPassword();
			String vanillaUrl = getVanillaRuntimeUrl();
			this.mdmRemote = new MdmRemote(login, password, vanillaUrl);
		}
		return mdmRemote;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setJsps(List<String> jsps) {
		this.jsps = jsps;
	}

	public List<String> getJsps() {
		return jsps;
	}

	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

	public boolean isEncrypted() {
		return isEncrypted;
	}

	@Override
	public String getApplicationId() {
		return "bpm.fm.loader.web";
	}

	public void setSelectedDate(Date selectedDate) {
		this.selectedDate = selectedDate;
	}
	
	public String getSelectedDate() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(selectedDate);
	}
	
}
