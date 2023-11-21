package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MassImport implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int importId;
   
	private String name;
	private String source;
	private User userInCharge;
	private FileType defaultType=null; 
	private String frequency;
	private String week_day;
	private String hour;
	private List<ImportRule> rules = new ArrayList<ImportRule>();
	private Boolean saveAuto=false;
	private Date month_day = new Date();
	private Boolean run=false;
	
	public int getImportId() {
		return importId;
	}
	public void setImportId(int importId) {
		this.importId = importId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public User getUserInCharge() {
		return userInCharge;
	}
	public void setUserInCharge(User userInCharge) {
		this.userInCharge = userInCharge;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public Date getMonth_day() {
		return month_day;
	}
	public void setMonth_day(Date month_day) {
		this.month_day = month_day;
	}
	public String getWeek_day() {
		return week_day;
	}
	public void setWeek_day(String week_day) {
		this.week_day = week_day;
	}
	public String getHour() {
		return hour;
	}
	public void setHour(String hour) {
		this.hour = hour;
	}
	public FileType getDefaultType() {
		return defaultType;
	}
	public void setDefaultType(FileType defaultType) {
		this.defaultType = defaultType;
	}
	public List<ImportRule> getRules() {
		return rules;
	}
	public void setRules(List<ImportRule> rules) {
		this.rules = rules;
	}
	public void addRule(ImportRule rule){
		this.rules.add(rule);
	}
	
	public void removerule(ImportRule rule){
		this.rules.remove(rule);
	}
	
	public Boolean isSaveAuto() {
		return saveAuto;
	}
	public void setSaveAuto(Boolean saveAuto) {
		this.saveAuto = saveAuto;
	}
	
	public Boolean isRun() {
		return run;
	}
	public void setRun(Boolean run) {
		this.run = run;
	}
	@Override
	public boolean equals(Object obj) {
		try{
			if(importId==((MassImport)obj).getImportId()){
				return true;
			}
		}catch(Exception e){
				
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
	
}
