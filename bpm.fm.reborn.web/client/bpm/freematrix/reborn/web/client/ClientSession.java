package bpm.freematrix.reborn.web.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ClientSession implements IsSerializable {

	private static ClientSession instance;
	
	private String login;
	private String password;
	private List<Metric> metrics;
	
	private List<String> drivers;
	
	private List<Group> groups;
	private List<Observatory> observatories;
	
	private Date defaultDate;
	
	private boolean isConnectedToCkan;
	
	private ClientSession() {
		
	}
	
	public static ClientSession getInstance() {
		if(instance == null) {
			instance = new ClientSession();
		}
		return instance;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public List<String> getDrivers() {
		return drivers;
	}

	public void setDrivers(List<String> drivers) {
		this.drivers = drivers;
	}
	
	public void addDriver(String driver) {
		if(this.drivers == null) {
			this.drivers = new ArrayList<String>();
		}
		this.drivers.add(driver);
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
		instance = new ClientSession();
	}

	public Date getDefaultDate() {
		return defaultDate;
	}

	public void setDefaultDate(Date defaultDate) {
		this.defaultDate = defaultDate;
	}
	
	public boolean isConnectedToCkan() {
		return isConnectedToCkan;
	}

	public void setConnectedToCkan(boolean isConnectedToCkan) {
		this.isConnectedToCkan = isConnectedToCkan;
	}
	
	
}
