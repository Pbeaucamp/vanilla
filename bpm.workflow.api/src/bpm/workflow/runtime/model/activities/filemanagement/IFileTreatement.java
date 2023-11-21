package bpm.workflow.runtime.model.activities.filemanagement;

public interface IFileTreatement extends IFileServer {
	
	/**
	 * Use to specify the type of files which will be treated, for exemple: toto.txt; *.pdf, *.*
	 * @param filesName
	 */
	public void setFilesToTreat(String filesName);
	
	public String getFilesToTreat();

}
