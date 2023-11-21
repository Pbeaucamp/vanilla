package bpm.gwt.aklabox.commons.shared;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceConfiguration;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceHolder;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LaunchReportInformations implements IsSerializable {
	
	private RepositoryItem item;
	private Group selectedGroup;
	
	private String reportKey;
	private boolean hasBeenRun = false;
	private boolean toRun = true;
	
	private List<VanillaGroupParameter> groupParameters;
	private List<UserRunConfiguration> userConfigurations;

	private List<String> reportResources;
	private AlternateDataSourceHolder alternateDatasource;
	
	private List<String> outputs;
	private String locale;
	private Integer limitRows;
	private AlternateDataSourceConfiguration alternateDataSourceConfig = new AlternateDataSourceConfiguration();
	
	private List<Group> availableGroups;
	
	//Used by ReportsGroup item
	private List<LaunchReportInformations> reports;
	
	public LaunchReportInformations() { }
	
	public LaunchReportInformations(RepositoryItem item, Group selectedGroup, List<VanillaGroupParameter> groupParameters, List<UserRunConfiguration> userConfigurations, 
			List<String> reportResources, AlternateDataSourceHolder alternateDatasource, List<Group> availableGroups) {
		this.item = item;
		this.selectedGroup = selectedGroup;
		this.groupParameters = groupParameters;
		this.userConfigurations = userConfigurations;
		this.reportResources = reportResources;
		this.alternateDatasource = alternateDatasource;
		this.availableGroups = availableGroups;
	}
	
	public RepositoryItem getItem() {
		return item;
	}
	
	public Group getSelectedGroup() {
		return selectedGroup;
	}
	
	public void setSelectedGroup(Group selectedGroup) {
		this.selectedGroup = selectedGroup;
	}
	
	public AlternateDataSourceHolder getAlternateDatasource() {
		return alternateDatasource;
	}
	
	public List<VanillaGroupParameter> getGroupParameters() {
		return groupParameters;
	}
	
	public void setGroupParameters(List<VanillaGroupParameter> groupParameters) {
		this.groupParameters = groupParameters;
	}
	
	public List<String> getReportResources() {
		return reportResources;
	}
	
	public List<UserRunConfiguration> getUserConfigurations() {
		return userConfigurations;
	}

	public List<String> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<String> outputs) {
		this.outputs = outputs;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Integer getLimitRows() {
		return limitRows;
	}

	public void setLimitRows(Integer limitRows) {
		this.limitRows = limitRows;
	}

	public AlternateDataSourceConfiguration getAlternateDataSourceConfig() {
		return alternateDataSourceConfig;
	}

	public void setAlternateDataSourceConfig(AlternateDataSourceConfiguration alternateDataSourceConfig) {
		this.alternateDataSourceConfig = alternateDataSourceConfig;
	}

	public List<Group> getAvailableGroups() {
		return availableGroups;
	}

	public void setItem(RepositoryItem item) {
		this.item = item;
	}

	public String getReportKey() {
		return reportKey;
	}

	public void setReportKey(String reportKey) {
		this.reportKey = reportKey;
	}

	public boolean hasBeenRun() {
		return hasBeenRun;
	}

	public void setHasBeenRun(boolean hasBeenRun) {
		this.hasBeenRun = hasBeenRun;
	}

	public List<LaunchReportInformations> getReports() {
		return reports;
	}
	
	public boolean isGroupReport() {
		return reports != null && !reports.isEmpty();
	}

	public void addReport(LaunchReportInformations report) {
		if(reports == null) {
			reports = new ArrayList<LaunchReportInformations>();
		}
		this.reports.add(report);
	}

	public void setReports(List<LaunchReportInformations> reports) {
		this.reports = reports;
	}

	public boolean isToRun() {
		return toRun;
	}

	public void setToRun(boolean toRun) {
		this.toRun = toRun;
	}
}
