package bpm.fm.designer.web.client;

import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ClientSession implements IsSerializable {

	private static ClientSession instance;
	
	private String login;
	private String password;
	private String vanillaUrl;
	
	//TODO: Change with the real group
	private Group group;
	
	private List<Axis> axis;
	private List<Metric> metrics;
	
	private List<Observatory> observatories;
	
	private List<MapDataSet> datasets;
	private List<MapVanilla> maps;
	
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
	
	public String getVanillaUrl() {
		return vanillaUrl;
	}
	
	public void setVanillaUrl(String vanillaUrl) {
		this.vanillaUrl = vanillaUrl;
	}

	public List<Axis> getAxis() {
		if(axis == null) {
			axis = new ArrayList<Axis>();
		}
		return axis;
	}

	public void setAxis(List<Axis> axis) {
		this.axis = axis;
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

	public List<Observatory> getObservatories() {
		return observatories;
	}

	public void setObservatories(List<Observatory> observatories) {
		this.observatories = observatories;
	}

	public List<MapDataSet> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<MapDataSet> datasets) {
		this.datasets = datasets;
	}

	public List<MapVanilla> getMaps() {
		return maps;
	}

	public void setMaps(List<MapVanilla> maps) {
		this.maps = maps;
	}
	
	public Group getGroup() {
		return group;
	}
	
	public void setGroup(Group group) {
		this.group = group;
	}
	
}
