package bpm.vanilla.repository.services.parsers;

import java.util.List;

import bpm.vanilla.platform.core.repository.Parameter;


/**
 * Interface to implement to extract some information from a
 * Model xml representation
 * @author ludo
 *
 */
public interface IModelParser {
	
	/**
	 * @return the ModelParameter by parsing the model
	 */
	public List<Parameter> getParameters();
	
	
	/**
	 * @return the requested DirectoryItem id's for this object 
	 */
	public List<Integer> getDependanciesDirectoryItemId();
	
	/**
	 * allow to modify the Xml definition by adding/changing informations
	 * this method is always called when adding a ModelXml in the repository
	 * @return overriden modelXml
	 * @throws Exception 
	 */
	public String overrideXml(Object object) throws Exception;

	/**
	 * 
	 * @return the ids of Used DataSource within the Model
	 */
	public List<Integer> getDataSourcesReferences();
}
