package bpm.vanilla.platform.core.beans.resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Transient;

public class ValidationSchemaResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private int validationId;
	private Date validationDate;
	private String schema;

	private int nbLinesCheck;
	private int nbLinesError;
	
	private List<String> columnsWithError;
	private List<String> rulesWithError;
	private List<ValidationRuleResult> ruleResults;
	
	@Transient
	private HashMap<String, Integer> columnsWithNbOfErrors;

	public ValidationSchemaResult() {
	}

	public ValidationSchemaResult(int validationId, String schema, int nbLinesCheck, int nbLinesError) {
		this.validationDate = new Date();
		this.validationId = validationId;
		this.schema = schema;
		this.nbLinesCheck = nbLinesCheck;
		this.nbLinesError = nbLinesError;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getValidationId() {
		return validationId;
	}

	public void setValidationId(int validationId) {
		this.validationId = validationId;
	}
	
	public Date getValidationDate() {
		return validationDate;
	}
	
	public void setValidationDate(Date validationDate) {
		this.validationDate = validationDate;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public int getNbLinesCheck() {
		return nbLinesCheck;
	}

	public void setNbLinesCheck(int nbLinesCheck) {
		this.nbLinesCheck = nbLinesCheck;
	}

	public int getNbLinesError() {
		return nbLinesError;
	}

	public void setNbLinesError(int nbLinesError) {
		this.nbLinesError = nbLinesError;
	}
	
	public HashMap<String, Integer> getColumnsWithNbOfErrors() {
		return columnsWithNbOfErrors;
	}
	
	public void incrementColumnLine(String column) {
		if (columnsWithNbOfErrors == null) {
			columnsWithNbOfErrors = new HashMap<String, Integer>();
		}
		
		columnsWithNbOfErrors.merge(column, 1, Integer::sum);
	}
	
	public void incrementLine(boolean isError) {
		this.nbLinesCheck++;
		this.nbLinesError = isError ? nbLinesError + 1 : nbLinesError;
	}
	
	public void addRuleResult(ValidationRuleResult ruleResult) {
		if (ruleResults == null) {
			this.ruleResults = new ArrayList<ValidationRuleResult>();
		}
		
		this.ruleResults.add(ruleResult);
		addColumnWithError(ruleResult.getField());
		addRuleWithError(ruleResult.getRuleName());
	}
	
	public List<ValidationRuleResult> getRuleResults() {
		return ruleResults;
	}
	
	public void setRuleResults(List<ValidationRuleResult> ruleResults) {
		this.ruleResults = ruleResults;
	}
	
	public void addRuleWithError(String rule) {
		if (rulesWithError == null) {
			this.rulesWithError = new ArrayList<String>();
		}
		
		if (rule != null && !rule.isEmpty() && !rulesWithError.contains(rule)) {
			this.rulesWithError.add(rule);
		}
	}
	
	public List<String> getRulesWithError() {
		return rulesWithError;
	}
	
	public void addColumnWithError(String column) {
		if (columnsWithError == null) {
			this.columnsWithError = new ArrayList<String>();
		}
		
		if (!columnsWithError.contains(column)) {
			this.columnsWithError.add(column);
		}
	}
	
	public List<String> getColumnsWithError() {
		return columnsWithError;
	}
	
	public List<String> getDetails() {
		List<String> details = new ArrayList<String>();
		details.add("	Schema '" + schema + "\n");
		details.add("		Nb lines check : " + nbLinesCheck + "\n");
		details.add("		Nb lines error: " + nbLinesError + "\n");
		return details;
	}
}
