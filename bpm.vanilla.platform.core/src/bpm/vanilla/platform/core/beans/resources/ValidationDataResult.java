package bpm.vanilla.platform.core.beans.resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

public class ValidationDataResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public enum DataValidationResultStatut {
		NO_VALIDATION,
		SUCCESS,
		ERROR
	}

	private int id;
	private Date validationDate;
	private DataValidationResultStatut statut;

	private int contractId;
	private int documentId;
	private int documentVersion;

	private String resourceId;
	
	private String message;

	private List<ValidationSchemaResult> schemaValidationResults;

	public ValidationDataResult() {
	}

	public ValidationDataResult(int contractId, int documentId, int documentVersion, DataValidationResultStatut statut, String message) {
		this.validationDate = new Date();
		this.contractId = contractId;
		this.documentId = documentId;
		this.documentVersion = documentVersion;
		this.statut = statut;
		this.message = message;
	}

	public ValidationDataResult(int contractId, String resourceId, DataValidationResultStatut statut, String message) {
		this.validationDate = new Date();
		this.contractId = contractId;
		this.resourceId = resourceId;
		this.statut = statut;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getValidationDate() {
		return validationDate;
	}

	public void setValidationDate(Date validationDate) {
		this.validationDate = validationDate;
	}

	public int getContractId() {
		return contractId;
	}

	public void setContractId(int contractId) {
		this.contractId = contractId;
	}

	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}

	public int getDocumentVersion() {
		return documentVersion;
	}

	public void setDocumentVersion(int documentVersion) {
		this.documentVersion = documentVersion;
	}
	
	public String getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	public DataValidationResultStatut getStatut() {
		return statut;
	}
	
	public void setStatut(DataValidationResultStatut statut) {
		this.statut = statut;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<ValidationSchemaResult> getSchemaValidationResults() {
		return schemaValidationResults;
	}

	public void setSchemaValidationResults(List<ValidationSchemaResult> schemaValidationResults) {
		this.schemaValidationResults = schemaValidationResults;
	}
	
	public void addSchemaValidationResult(ValidationSchemaResult schemaValidationResult) {
		if (schemaValidationResults == null) {
			this.schemaValidationResults = new ArrayList<ValidationSchemaResult>(); 
		}
		this.schemaValidationResults.add(schemaValidationResult);
	}

	public void resetDetails() {
		if (schemaValidationResults != null) {
			for (ValidationSchemaResult schemaResult : schemaValidationResults) {
				schemaResult.setRuleResults(null);
			}
		}
	}
	
	@Transient
	public List<String> getDetails() {
		List<String> details = new ArrayList<String>();
		details.add("Testing data from contract with ID '" + contractId + "' with document with ID '" + documentId + "' and version '" + documentVersion + "'\n");
		
		switch (statut) {
		case NO_VALIDATION:
			details.add("	Statut: No validation\\n");
			break;
		case ERROR:
			details.add("	Statut: Error\n");
			break;
		case SUCCESS:
			details.add("	Statut: Success\n");
			break;
		default:
			break;
		}
		details.add("	Message: " + message + "\n\n");
		if (getSchemaValidationResults() != null) {
			for (ValidationSchemaResult schemaResult : getSchemaValidationResults()) {
				details.addAll(schemaResult.getDetails());
			}
		}
		return details;
	}

}
