package bpm.document.management.core.model;

import java.io.Serializable;

public class ImportRule implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int ruleId;

	private User user;
	private FileType docType; 
	private String rule;
	private int importId;
	
	
	public ImportRule() {
		super();
	}

	public ImportRule(User user, FileType docType, String rule, int importId) {
		super();
		this.user = user;
		this.docType = docType;
		this.rule = rule;
//		this.importId = importId;
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public FileType getDocType() {
		return docType;
	}

	public void setDocType(FileType docType) {
		this.docType = docType;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public int getImportId() {
		return importId;
	}

	public void setImportId(int importId) {
		this.importId = importId;
	}

	@Override
	public boolean equals(Object obj) {
		try{
			if(ruleId==((ImportRule)obj).getRuleId()){
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
