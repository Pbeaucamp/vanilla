package bpm.gwt.commons.shared.viewer;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceConfiguration;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceHolder;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.repository.ItemMetadataTableLink;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LaunchReportInformations implements IsSerializable {

	public enum TypeRun {
		RUN,
		EXPORT,
		PRINT,
		BACKGROUND,
		DISCO
	}
	
	private PortailRepositoryItem item;
	private Group selectedGroup;
	
	private String reportKey;
	private boolean hasBeenRun = false;
	private boolean toRun = true;
	
	private List<VanillaGroupParameter> groupParameters;
	private List<UserRunConfiguration> userConfigurations;

	private List<String> reportResources;
	private AlternateDataSourceHolder alternateDatasource;
	private CommentsInformations commentsInformations;
	private Validation validation;
	
	private TypeRun typeRun;
	private List<String> outputs = new ArrayList<String>();
	private String locale;
	private Integer limitRows;
	private boolean displayComments = true;
	private AlternateDataSourceConfiguration alternateDataSourceConfig = new AlternateDataSourceConfiguration();
	
	private List<Group> availableGroups;
	
	//Used by ReportsGroup item
	private List<LaunchReportInformations> reports;
	
	private List<ItemMetadataTableLink> metadataLinks;
	
	public LaunchReportInformations() { }
	
	public LaunchReportInformations(PortailRepositoryItem item, Group selectedGroup, List<VanillaGroupParameter> groupParameters, 
			List<UserRunConfiguration> userConfigurations, CommentsInformations commentsInformations, Validation validation, 
			List<String> reportResources, AlternateDataSourceHolder alternateDatasource, List<Group> availableGroups, List<ItemMetadataTableLink> metadataLinks) {
		this.item = item;
		this.selectedGroup = selectedGroup;
		this.groupParameters = groupParameters;
		this.userConfigurations = userConfigurations;
		this.commentsInformations = commentsInformations;
		this.validation = validation;
		this.reportResources = reportResources;
		this.alternateDatasource = alternateDatasource;
		this.availableGroups = availableGroups;
		this.metadataLinks = metadataLinks;
	}
	
	public PortailRepositoryItem getItem() {
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
	
	public CommentsInformations getCommentsInformations() {
		return commentsInformations;
	}

	public void setCommentsInformations(CommentsInformations commentsInformations) {
		this.commentsInformations = commentsInformations;
	}
	
	public Validation getValidation() {
		return validation;
	}

	public TypeRun getTypeRun() {
		return typeRun;
	}

	public void setTypeRun(TypeRun typeRun) {
		this.typeRun = typeRun;
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
	
	public boolean displayComments() {
		return displayComments;
	}
	
	public void setDisplayComments(boolean displayComments) {
		this.displayComments = displayComments;
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

	public void setItem(PortailRepositoryItem item) {
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
	
	public List<ItemMetadataTableLink> getMetadataLinks() {
		return metadataLinks;
	}
}
