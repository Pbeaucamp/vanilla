package bpm.profiling.database.bean;

import java.util.Calendar;
import java.util.Date;

public class AnalysisConditionResult {
	private int id;
	private int conditionId;
	private Integer ruleSetId;
	private Integer validCount;
	private Double validCountPercent;
	private Integer distinctValidCount;
	private Double dictinctValidPercent;
	private Date date = Calendar.getInstance().getTime();
	

	

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getConditionId() {
		return conditionId;
	}
	public void setConditionId(int conditionId) {
		this.conditionId = conditionId;
	}
	public Integer getValidCount() {
		return validCount;
	}
	public void setValidCount(Integer validCount) {
		this.validCount = validCount;
	}
	public Double getValidCountPercent() {
		return validCountPercent;
	}
	public void setValidCountPercent(Double validCountPercent) {
		this.validCountPercent = validCountPercent;
	}
	public Integer getDistinctValidCount() {
		return distinctValidCount;
	}
	public void setDistinctValidCount(Integer distinctValidCount) {
		this.distinctValidCount = distinctValidCount;
	}
	public Double getDictinctValidPercent() {
		return dictinctValidPercent;
	}
	public void setDictinctValidPercent(Double dictinctValidPercent) {
		this.dictinctValidPercent = dictinctValidPercent;
	}
	public void setRuleSetId(Integer id2) {
		ruleSetId = id2;
		
	}
	
	public Integer getRuleSetId(){
		return ruleSetId;
	}
	
	
}
