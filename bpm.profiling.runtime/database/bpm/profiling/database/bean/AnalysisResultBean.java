package bpm.profiling.database.bean;

import java.util.Calendar;
import java.util.Date;

public class AnalysisResultBean {
	private int id;
	private int analysisContentId = -1;
	private Date creation =  Calendar.getInstance().getTime();
	private String dataType;
	private String lowValue;
	private String hightValue;
	private Integer lowValueCount;
	private Integer hightValueCount;
	private Double avgValue;
	private Integer distinctCount;
	private Integer nullCount;
	private Double nullPercent;
	private Integer zeroCount;
	private Double zeroPercent;
	private Integer blankCount;
	private Double blankPercent;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public int getAnalysisContentId() {
		return analysisContentId;
	}
	public void setAnalysisContentId(int analysisContentId) {
		this.analysisContentId = analysisContentId;
	}
	public Date getCreation() {
		return creation;
	}
	public void setCreation(Date creation) {
		this.creation = creation;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getLowValue() {
		return lowValue;
	}
	public void setLowValue(String lowValue) {
		this.lowValue = lowValue;
	}
	public String getHightValue() {
		return hightValue;
	}
	public void setHightValue(String hightValue) {
		this.hightValue = hightValue;
	}
	public Integer getLowValueCount() {
		return lowValueCount;
	}
	public void setLowValueCount(Integer lowValueCount) {
		this.lowValueCount = lowValueCount;
	}
	public Integer getHightValueCount() {
		return hightValueCount;
	}
	public void setHightValueCount(Integer hightValueCount) {
		this.hightValueCount = hightValueCount;
	}
	public Double getAvgValue() {
		return avgValue;
	}
	public void setAvgValue(Double avgValue) {
		this.avgValue = avgValue;
	}
	public Integer getDistinctCount() {
		return distinctCount;
	}
	public void setDistinctCount(Integer distinctCount) {
		this.distinctCount = distinctCount;
	}
	public Integer getNullCount() {
		return nullCount;
	}
	public void setNullCount(Integer nullCount) {
		this.nullCount = nullCount;
	}
	public Double getNullPercent() {
		return nullPercent;
	}
	public void setNullPercent(Double nullPercent) {
		this.nullPercent = nullPercent;
	}
	public Integer getZeroCount() {
		return zeroCount;
	}
	public void setZeroCount(Integer zeroCount) {
		this.zeroCount = zeroCount;
	}
	public Double getZeroPercent() {
		return zeroPercent;
	}
	public void setZeroPercent(Double zeroPercent) {
		this.zeroPercent = zeroPercent;
	}
	public Integer getBlankCount() {
		return blankCount;
	}
	public void setBlankCount(Integer blankCount) {
		this.blankCount = blankCount;
	}
	public Double getBlankPercent() {
		return blankPercent;
	}
	public void setBlankPercent(Double blankPercent) {
		this.blankPercent = blankPercent;
	}
	
	
	
	
}
