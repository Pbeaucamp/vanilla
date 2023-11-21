package bpm.gwt.commons.shared.repository;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This holds the client-destined info relating to the history of a report 
 * @author manu
 *
 */
public class ReportHistoryDTO implements IsSerializable {

	private String 	modelName;
	private int 	modelId;
	
	private String 	historyName;
	private String  historyFormat;
	private String  historySummary;
	private int 	historyId;
	
	private Date 	creationDate;
	private Date	lastAccessDate;
	
	private boolean hasBeenAccessed;
	private String	lastUserName;
	
	private int accessCounter;
	
	private int documentId;
	
	private String key;
	
	public ReportHistoryDTO() {
	
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public String getHistoryName() {
		return historyName;
	}

	public void setHistoryName(String historyName) {
		this.historyName = historyName;
	}
	
	public String getHistoryFormat() {
		return historyFormat;
	}
	
	public void setHistoryFormat(String formatName) {
		this.historyFormat = formatName;
	}

	public int getHistoryId() {
		return historyId;
	}

	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastAccessDate() {
		return lastAccessDate;
	}

	public void setLastAccessDate(Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}

	public boolean isHasBeenAccessed() {
		return hasBeenAccessed;
	}

	public void setHasBeenAccessed(boolean hasBeenAccessed) {
		this.hasBeenAccessed = hasBeenAccessed;
	}

	public String getLastUserName() {
		return lastUserName;
	}

	public void setLastUserName(String lastUserName) {
		this.lastUserName = lastUserName;
	}
	
	public void setAccessCounter(int accessCounter) {
		this.accessCounter = accessCounter;
	}
	
	public int getAccessCounter() {
		return accessCounter;
	}

	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}

	public void setHistorySummary(String historySummary) {
		this.historySummary = historySummary;
	}

	public String getHistorySummary() {
		return historySummary;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
