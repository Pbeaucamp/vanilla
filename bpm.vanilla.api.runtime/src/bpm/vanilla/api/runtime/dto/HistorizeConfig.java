package bpm.vanilla.api.runtime.dto;

import java.util.List;

import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;

public class HistorizeConfig {
	private List<VanillaGroupParameter> groupParameters;
	private List<Integer> groupIds;
	private boolean contribution;
	private boolean histoForMe;
	private Integer histoId;
	private String gedName;
	private String peremptionDate;
	private String outputFormat;
	



	public List<VanillaGroupParameter> getGroupParameters() {
		return groupParameters;
	}

	public List<Integer> getGroupIds() {
		return groupIds;
	}

	public boolean isContribution() {
		return contribution;
	}

	public Integer getHistoId() {
		return histoId;
	}

	public void setHistoId(Integer histoId) {
		this.histoId = histoId;
	}

	public String getGedName() {
		return gedName;
	}

	public String getPeremptionDate() {
		return peremptionDate;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public boolean isHistoForMe() {
		return histoForMe;
	}

	public void setGroupParameters(List<VanillaGroupParameter> groupParameters) {
		this.groupParameters = groupParameters;
	}

	public void setGroupIds(List<Integer> groupIds) {
		this.groupIds = groupIds;
	}

	public void setContribution(boolean contribution) {
		this.contribution = contribution;
	}

	public void setHistoForMe(boolean histoForMe) {
		this.histoForMe = histoForMe;
	}

	public void setHistoId(int histoId) {
		this.histoId = histoId;
	}

	public void setGedName(String gedName) {
		this.gedName = gedName;
	}

	public void setPeremptionDate(String peremptionDate) {
		this.peremptionDate = peremptionDate;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}
	
	
	
	
}
