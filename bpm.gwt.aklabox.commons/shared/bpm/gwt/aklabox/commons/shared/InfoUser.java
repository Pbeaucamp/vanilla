package bpm.gwt.aklabox.commons.shared;

import java.util.HashMap;

import bpm.aklabox.workflow.core.model.Platform;
import bpm.document.management.core.model.Customer;
import bpm.document.management.core.model.ItemRight;
import bpm.document.management.core.model.User;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.IsSerializable;

public class InfoUser implements IsSerializable {

	private String sessionId;

	private User user;
	private String runtimeUrl;
	private Platform platformInfo;

	private int chorusTarget;
	private Customer customer;
	
	private ItemRight maxUserRight;
	
	private HashMap<String, String> properties = new HashMap<String, String>();

	public InfoUser() {
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}
	
	public void setRuntimeUrl(String runtimeUrl) {
		this.runtimeUrl = runtimeUrl;
	}
	
	public String getRuntimeUrl() {
		return runtimeUrl;
	}

	public void setPlatformInfo(Platform platformInfo) {
		this.platformInfo = platformInfo;
	}

	public Platform getPlatformInfo() {
		return platformInfo;
	}

	public void setChorusTarget(int chorusTarget) {
		this.chorusTarget = chorusTarget;
	}

	public int getChorusTarget() {
		return chorusTarget;
	}
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public Customer getCustomer() {
		return customer;
	}

//	public void loadCookies() {
//		String target = Cookies.getCookie(AklaboxConstant.COOKIE_AKLADEMAT_CHORUS_TARGET);
//		if (target != null) {
//			setChorusTarget(Integer.parseInt(target));
//		}
//	}

	public void putProperty(String key, String value) {
		properties.put(key, value);
	}

	public String getProperty(String key) {
		return properties.get(key);
	}
	
	public String getWopiUrl() {
		return properties.get(VanillaConfiguration.P_WOPI_WS_URL);
	}
	
	public boolean isWopiAvailable() {
		String wopiUrl = getWopiUrl();
		return wopiUrl != null && !wopiUrl.isEmpty();
	}
	
	public ItemRight getMaxUserRight() {
		return maxUserRight;
	}
}
