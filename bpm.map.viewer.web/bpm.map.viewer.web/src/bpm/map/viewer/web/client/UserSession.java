package bpm.map.viewer.web.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserSession implements IsSerializable {

	private static UserSession instance;
	
	private User user;
	private List<Metric> metrics;
	
	
	private List<Group> groups;
	private List<Observatory> observatories;
	
	private Date defaultDate;
	
	private List<String> iconSet;
	
	private String webappUrl;
	
	private UserSession() {
		
	}
	
	public static UserSession getInstance() {
		if(instance == null) {
			instance = new UserSession();
		}
		return instance;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Metric> getMetrics() {
		if(metrics == null) {
			metrics = new ArrayList<Metric>();
		}
		return metrics;
	}

	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Observatory> getObservatories() {
		return observatories;
	}

	public void setObservatories(List<Observatory> observatories) {
		this.observatories = observatories;
	}

	public static void clear() {
		instance = new UserSession();
	}

	public Date getDefaultDate() {
		return defaultDate;
	}

	public void setDefaultDate(Date defaultDate) {
		this.defaultDate = defaultDate;
	}

	public List<String> getIconSet() {
		return iconSet;
	}

	public void setIconSet(List<String> iconSet) {
		this.iconSet = iconSet;
	}

	public String getWebappUrl() {
		return webappUrl;
	}

	public void setWebappUrl(String webappUrl) {
		this.webappUrl = webappUrl;
	}
	
}
