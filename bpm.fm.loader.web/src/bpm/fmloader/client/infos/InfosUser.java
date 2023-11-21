package bpm.fmloader.client.infos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.fmloader.client.dto.ObservatoireDTO;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InfosUser implements IsSerializable {

	private static InfosUser instance;
	private User user = new User();
	private boolean isUserAllowed;
	private List<Group> groups = new ArrayList<Group>();
	private List<Observatory> observatories = new ArrayList<Observatory>();
	private List<Axis> applications = new ArrayList<Axis>();
	private List<Metric> metrics = new ArrayList<Metric>();

	private Group selectedGroup;
	private String selectedPeriode;
	private List<LoaderDataContainer> values;

	private Date selectedDate;
	private String dateString;

	private boolean fromPortal;

	private int commentLimit;

	public InfosUser() {
		;
	}

	public static InfosUser getInstance() {
		if (instance == null) {
			instance = new InfosUser();
		}
		return instance;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public Group getSelectedGroup() {
		return selectedGroup;
	}

	public void setSelectedGroup(Group selectedGroup) {
		this.selectedGroup = selectedGroup;
	}

	public List<Axis> getApplications() {
		return applications;
	}

	public void setApplications(List<Axis> applications) {
		this.applications = applications;
	}
	
	public boolean isUserAllowed() {
		return isUserAllowed;
	}

	public void setUserAllowed(boolean isUserAllowed) {
		this.isUserAllowed = isUserAllowed;
	}

	public String getSelectedPeriode() {
//		return "MONTH";
		return selectedPeriode;
	}

	public void setSelectedPeriode(String selectedPeriode) {
		this.selectedPeriode = selectedPeriode;
	}

	// public List<MetricDTO> getSelectedMetrics() {
	// return selectedMetrics;
	// }
	//
	// public void setSelectedMetrics(List<MetricDTO> selectedMetrics) {
	// this.selectedMetrics = selectedMetrics;
	// }

	public List<LoaderDataContainer> getValues() {
		return values;
	}

	public void setValues(List<LoaderDataContainer> values) {
		this.values = values;
	}

	public List<Metric> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}

	public Date getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(Date selectedDate) {
		this.selectedDate = selectedDate;
	}

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	public void setFromPortal(boolean fromPortal) {
		this.fromPortal = fromPortal;
	}

	public boolean isFromPortal() {
		return fromPortal;
	}

	public void setCommentLimit(int commentLimit) {
		this.commentLimit = commentLimit;
	}

	public int getCommentLimit() {
		return commentLimit;
	}

	public List<Observatory> getObservatories() {
		return observatories;
	}

	public void setObservatories(List<Observatory> observatories) {
		this.observatories = observatories;
	}

}
