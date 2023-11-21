package bpm.forms.core.design;

import java.util.List;

/**
 * Base interface for the VanillaForm design
 * @author ludo
 *
 */
public interface IDefinitionService {

	/**
	 * used to perform some customization on the implementors like setting an Url if the implementor 
	 * is a remote implementor
	 * 
	 * @param object
	 */
	public void configure(Object object);
	
	/**
	 * delete a IFormDefinition 
	 * @param form
	 * @throws Exception
	 */
	public void delete(IFormDefinition form) throws Exception;
	
	/**
	 * 
	 * @param id
	 * @return the IFormDefinition with the given id
	 * @throws Exception
	 */
	public IFormDefinition getFormDefinition(long id) throws Exception;
	
	/**
	 * 
	 * @return all the existing Vanilla Forms
	 */
	public List<IForm> getForms();
	
	/**
	 * 
	 * @param formId
	 * @return all the IFormDefinition for the given formId
	 */
	public List<IFormDefinition> getFormDefinitionVersions(long formId);
	
	/**
	 * 
	 * @param form
	 * @return store the given IFormDefinition in the form storage system and retrn it with its generated id
	 * @throws Exception
	 */
	public IFormDefinition saveFormDefinition(IFormDefinition form) throws Exception;
	
	/**
	 * 
	 * @param form
	 * @return  store the given IForm in the form storage system and return it with its generated id
	 * @throws Exception
	 */
	public IForm saveForm(IForm form) throws Exception;
	
	/**
	 * update the given IFormDefinition in the FormStorageSystem
	 * @param form
	 * @throws Exception
	 */
	public void update(IFormDefinition form) throws Exception;

	/**
	 * update the given IForm in the FormStorageSystem
	 * @param form
	 * @throws Exception
	 */
	public void update(IForm form) throws Exception;
	
	/**
	 * delete the given IForm from the FormStorageSystem.
	 * All its IFormDefinitions will be deleted too.
	 * 
	 * 
	 * @param form
	 * @throws Exception if an error occur or if some IformInstances attached to
	 * this form are still running
	 */
	public void delete(IForm o) throws Exception;

	/**
	 * save the given ITargetTable ad return it with its generated id
	 * @param table
	 * @return
	 * @throws Exception
	 */
	public ITargetTable saveTargetTable(ITargetTable table)throws Exception;

	/**
	 * 
	 * @return all registered ITargetTable
	 */
	public List<ITargetTable> getTargetTables();

	/**
	 * 
	 * @param targetTableId
	 * @return a List of STring of the registeredColumns for the ItargetTabe with the given id
	 */
	public List getColumnsForTargetTable(Long targetTableId);
	
	/**
	 * 
	 * @param form
	 * @return the 1st FormDefinition which may be instanciated for the Given IForm
	 */
	public IFormDefinition getActiveFormDefinition(IForm form);

	/**
	 * 
	 * @param targetTableId
	 * @return the ItargetTable with the given id
	 */
	public ITargetTable getTargetTable(Long targetTableId);

	/**
	 * 
	 * @param formId
	 * @return the IForm with the given id
	 */
	public IForm getForm(long formId);
}
