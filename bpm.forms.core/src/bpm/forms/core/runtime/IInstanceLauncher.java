package bpm.forms.core.runtime;

import java.util.List;

import bpm.forms.core.design.IForm;
/**
 * a Simpe interface that will be responsibe for creating the IFormInstance of a IForm
 * @author ludo
 *
 */
public interface IInstanceLauncher {
	
	/**
	 * to perform some internal configuration
	 * @param object
	 */
	public void configure(Object object);
	
	/**
	 * create all the possible IFormInstance for the given IForm and groupId
	 * 
	 * each IFormDefinition (activated, designed, within the time range if one specified) will generate an IFormInstance
	 * 
	 * The implementor should store those IFormInstance throuht the IInstanceService.save(IFormInstance) method 
	 * 
	 * @param form
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public List<IFormInstance> launchForm(IForm form, int groupId) throws Exception;
}
