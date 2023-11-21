package bpm.forms.core.runtime;

import java.util.List;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;

/**
 * The base interface to manage the IFormInstance in the FormStorageSystem
 * 
 * @author ludo
 *
 */
public interface IInstanceService {

	public void configure(Object object);
	
	/**
	 * 
	 * @param form
	 * @return the existing IFormInstance for the given IForm
	 * @throws Exception
	 */
	public List<IFormInstance> getRunningInstances(IForm form) throws Exception;
	
	/**
	 * 
	 * @param formDefinition
	 * @return the existing IFormInstance for the given IFormDefinition
	 * @throws Exception
	 */
	public List<IFormInstance> getRunningInstances(IFormDefinition formDefinition) throws Exception;
	
	/**
	 * delete the given IFormInstance and all its temporaray vaues
	 * @param instance
	 * @throws Exception
	 */
	public void delete(IFormInstance instance) throws Exception;
	
	/**
	 * save the given IFormInstance
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	public IFormInstance save(IFormInstance instance) throws Exception;
	
	
	public IFormInstanceFieldState save(IFormInstanceFieldState fieldState) throws Exception;
	
	public void update(IFormInstanceFieldState fieldState) throws Exception;

	public IFormInstance getRunningInstance(long instanceId) throws Exception;
	
	public List<IFormInstanceFieldState> getFieldsState(long instanceId) throws Exception;
	
	public List<IFormInstance> getFormsToSubmit(int groupId) throws Exception;
	
	public List<IFormInstance> getFormsToValidate(int groupId) throws Exception;

	public void update(IFormInstance formInstance) throws Exception;

	public void deleteFor(IFormDefinition form) throws Exception;
}
