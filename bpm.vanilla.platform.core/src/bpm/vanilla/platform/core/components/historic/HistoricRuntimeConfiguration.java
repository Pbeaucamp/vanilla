package bpm.vanilla.platform.core.components.historic;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.historic.HistorizationConfig.HistorizationTarget;

/**
 * The historic runtime configuration
 * If you want to historize a report use this runtimeConfiguration and not the ones from the ged package
 * 
 * @author Marc Lanquetin
 *
 */
public class HistoricRuntimeConfiguration implements IRuntimeConfig {

	private IObjectIdentifier identifier;
	private Integer vanillaGroupId;
	private Integer directoryTargetId;
	
	private HistorizationTarget targetType;
	private List<Integer> targetIds;
	private String entryName;
	private String format;
	
	private Integer userId;
	
	private Integer historyId;
	
	private Date peremptionDate;
	
	public HistoricRuntimeConfiguration(IObjectIdentifier identifier, Integer vanillaGroupId, HistorizationTarget targetType, 
			List<Integer> targetIds, String entryName, String format, Integer userId, Integer historyId) {
		super();
		this.identifier = identifier;
		this.vanillaGroupId = vanillaGroupId;
		this.targetType = targetType;
		this.targetIds = targetIds;
		this.entryName = entryName;
		this.format = format;
		this.userId = userId;
		this.setHistoryId(historyId);
	}

	@Override
	public IObjectIdentifier getObjectIdentifier() {
		return identifier;
	}

	@Override
	public List<VanillaGroupParameter> getParametersValues() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public Integer getVanillaGroupId() {
		return vanillaGroupId;
	}

	public void setIdentifier(IObjectIdentifier identifier) {
		this.identifier = identifier;
	}

	public HistorizationTarget getTargetType() {
		return targetType;
	}

	public void setTargetType(HistorizationTarget targetType) {
		this.targetType = targetType;
	}

	public List<Integer> getTargetIds() {
		return targetIds;
	}

	public void setTargetIds(List<Integer> groupIds) {
		this.targetIds = groupIds;
	}

	public String getEntryName() {
		return entryName;
	}

	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setVanillaGroupId(Integer vanillaGroupId) {
		this.vanillaGroupId = vanillaGroupId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
	}

	public Integer getHistoryId() {
		return historyId;
	}

	public void setHistoryId(Integer historyId) {
		this.historyId = historyId;
	}

	public void setPeremptionDate(Date peremptionDate) {
		this.peremptionDate = peremptionDate;
	}

	public Date getPeremptionDate() {
		return peremptionDate;
	}

	public void setDirectoryTargetId(Integer directoryTargetId) {
		this.directoryTargetId = directoryTargetId;
	}

	public Integer getDirectoryTargetId() {
		return directoryTargetId;
	}
}
