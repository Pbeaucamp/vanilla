package bpm.vanilla.platform.core.beans.resources;

/*
 * This class is used to define a Simple KPI creation
 * It generate a query which can join 2 datasets, make calcul with two columns or with a mathematical operation
 * 
 */
public class SimpleKPIGenerationInformations extends AbstractD4CIntegrationInformations {

	private static final long serialVersionUID = 1L;

	private String sourceDatasetId;

	private String sqlQuery;

	private int folderTargetId;

	public String getSourceDatasetId() {
		return sourceDatasetId;
	}

	public void setSourceDatasetId(String sourceDatasetId) {
		this.sourceDatasetId = sourceDatasetId;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}
	
	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public int getFolderTargetId() {
		return folderTargetId;
	}

	public void setFolderTargetId(int folderTargetId) {
		this.folderTargetId = folderTargetId;
	}

}