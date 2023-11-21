package bpm.forms.instanciator.launcher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IServiceProvider;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IInstanceLauncher;
import bpm.forms.core.tools.IFactoryModelElement;


public abstract class InstanceLauncher implements IInstanceLauncher{
	
	
	
	public abstract IServiceProvider getServiceProvider() throws Exception;
	public abstract IFactoryModelElement getFactoryModel() throws Exception;
	
	public List<IFormInstance> launchForm(IForm form, int groupId) throws Exception {
		
		List<IFormInstance> instances = new ArrayList<IFormInstance>();
		List<IFormDefinition> defs = new ArrayList<IFormDefinition>();
		
		
		
		for(IFormDefinition _def : getServiceProvider().getDefinitionService().getFormDefinitionVersions(form.getId())){
			if (!_def.isDesigned() && !_def.isActivated()){
				continue;
			}
			if (_def.getStartDate() != null){
				if (!_def.getStopDate().after(Calendar.getInstance().getTime())){
					continue;	
				}
			}
			defs.add(_def);
		}
		
		if (defs.isEmpty()){
			throw new Exception("No FormDefinition is ready to be instanciated");
		}
		
		
		for(IFormDefinition def : defs){
			IFormInstance instance = getFactoryModel().createFormInstance();
			instance.setCreationDate(Calendar.getInstance().getTime());
			instance.setGroupId(groupId);
			instance.setFormDefinitionId(def.getId());
			
			/*
			 * compute expiration date if needed
			 */
			
			if (form.getLifeExpectancyHours() + form.getLifeExpectancyDays() + form.getLifeExpectancyMonths() + form.getLifeExpectancyYears() > 0){
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(Calendar.getInstance().getTime());
				calendar.add(Calendar.HOUR_OF_DAY, form.getLifeExpectancyHours());
				calendar.add(Calendar.DAY_OF_MONTH, form.getLifeExpectancyDays());
				calendar.add(Calendar.MONTH, form.getLifeExpectancyMonths());
				calendar.add(Calendar.YEAR, form.getLifeExpectancyYears());
				
				instance.setExpirationDate(calendar.getTime());
			}
			

			
			getServiceProvider().getInstanceService().save(instance);
			
			instances.add(instance);
		}
		
		
		
		return instances;
	}

	
	
	
}
