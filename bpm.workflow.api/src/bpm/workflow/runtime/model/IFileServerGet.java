package bpm.workflow.runtime.model;

import bpm.workflow.runtime.resources.IResource;

/**
 * Interface of the activities which are of the "get.." type among the filemanagement activities
 * @author Charles MARTIN
 *
 */
//TODO delete this class and replace it by IFileServer and IFileTreatment
public interface IFileServerGet extends IServer{
	
	/**
	 * 
	 * @return if the file searched is specific : "yes", if not "no"
	 */
	public String getIsSpecific();
	/**
	 * Set if the file searched is specific
	 * @param specificPath : only "yes" or "no"
	 */
	public void setIsSpecific(String specificPath);
	
	/**
	 * 
	 * @return the specific path if the file searched is specific
	 */
	public String getPathSpecific();
	/**
	 * Set the specific path of the file if is specific
	 * @param path
	 */
	public void setPathSpecific(String _pathSpecific);
	
	/**
	 * 
	 * @return if the files searched hold a specific extension : "yes" or not "no"
	 */
	public String getIsSpecificEx();
	/**
	 * Set if the files searched hold a specific extension "yes" or not "no"
	 * @param specificEx
	 */
	public void setIsSpecificEx(String specificEx);
	
	/**
	 * 
	 * @return the extension if the files searched hold a specific extension
	 */
	public String getExtension();
	/**
	 * Set the extension if the files searched hold a specific extension
	 * @param extension
	 */
	public void setExtension(String _extension);
	
}
