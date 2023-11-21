package bpm.vanilla.platform.core.runtime.dao.preferences;

/**
 * A bean class to store UserRunConfigurations
 * Use bpm.vanilla.platform.core.beans.UserRunConfiguration and 
 * bpm.vanilla.platform.core.beans.UserRunConfigurationParameter 
 * to return those values to the client
 * @author Marc Lanquetin
 *
 */
public class UserRunConfigurationBean {

	private int id;
	private String runName;
	private int runId = -1;
	private int userId;
	private int repId;
	private int itemId;
	private String paramName;
	private String paramValue;
	private String description;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getRunName() {
		return runName;
	}
	
	public void setRunName(String runName) {
		this.runName = runName;
	}
	
	public int getRunId() {
		return runId;
	}
	
	public void setRunId(int runId) {
		this.runId = runId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getRepId() {
		return repId;
	}
	
	public void setRepId(int repId) {
		this.repId = repId;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public String getParamName() {
		return paramName;
	}
	
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	public String getParamValue() {
		return paramValue;
	}
	
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
}
