package bpm.gateway.core.server.file;

/**
 * interface to split the path and the fileName for a File.definition attribute
 * @author ludo
 *
 */
public interface IFile {
	/**
	 * 
	 * @return the folderName representation of the folder
	 */
	public String getFolderName();
	
	/**
	 * 
	 * @return the folder identifier
	 */
	public String getFolder();
	
	/**
	 * 
	 * @return the fileName representation of the file
	 */
	public String getFileName();
	
	
	/**
	 * set the folder identifier
	 * @param folder
	 */
	public void setFolder(String folder);
	
	/**
	 * set the folderName (string representation of the folder
	 * @param folderName
	 */
	public void setFolderName(String folderName);
	
	
	/**
	 * set the string representation of the File
	 * @param filerName
	 */
	public void setFileName(String filerName);
	
	/**
	 * set the file identifier of the file(must not contains its full path, only its id)
	 * @param fileId
	 */
	public void setFile(String fileId);
	
	/**
	 * 
	 * @return the unique identifier of the file (not its full path)
	 */
	public String getFile();

}
