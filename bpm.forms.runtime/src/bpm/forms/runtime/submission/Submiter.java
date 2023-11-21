package bpm.forms.runtime.submission;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.core.design.ITargetTable;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IFormInstanceFieldState;
import bpm.forms.runtime.VanillaFormsRuntimeComponent;
import bpm.vanilla.platform.core.beans.User;

public class Submiter {
	
	private String clientIp;
	private User user;
	private VanillaFormsRuntimeComponent component;
	public Submiter(VanillaFormsRuntimeComponent component, String clientIp, User user){
		this.clientIp = clientIp;
		this.user = user;
		this.component = component;
	}

	
	public String getClientIp(){
		return clientIp;
	}
	public void submit(IFormInstance formInstance, Properties fieldValues/*, VanillaProfil profil*/) throws Exception{
		
		IFormDefinition formDefinition = component.getFormServiceProvider().getDefinitionService().getFormDefinition(formInstance.getFormDefinitionId());
		
		if (formDefinition == null){
			throw new Exception("Unable to find FormDefinition for the FormInstance with id " + formInstance.getId());
		}
		
		
		for(ITargetTable table : formDefinition.getITargetTables()){
			
			/*
			 * insert the values
			 */
			HashMap<IFormFieldMapping, String> values = new HashMap<IFormFieldMapping, String>();
			
			for(IFormFieldMapping fieldMap : formDefinition.getIFormFieldMappings()){
				if (fieldMap.getTargetTableId() != null && fieldMap.getTargetTableId() == table.getId()){
					
					Enumeration<String> en = (Enumeration)fieldValues.keys();
					
					while(en.hasMoreElements()){
						String key = en.nextElement();
						
						if (key.equalsIgnoreCase(fieldMap.getFormFieldId())){
							values.put(fieldMap, fieldValues.getProperty(key));
							break;
						}
					}
				}
			}
			
			insertValues(formInstance, values);
			
		}
		
		
		formInstance.setIsSubmited(true);
		formInstance.setSubmiterIp(getClientIp());
		formInstance.setLastSubmitionDate(new Date());
		if (user != null){
			formInstance.setSubmiterUserId(user.getId());
		}
		component.getFormServiceProvider().getInstanceService().update(formInstance);
	}
	
	
	private void insertValues(IFormInstance formInstance, HashMap<IFormFieldMapping, String> values /*, VanillaProfil profil*/) throws Exception{
		
		List<IFormInstanceFieldState> states = component.getFormServiceProvider().getInstanceService().getFieldsState(formInstance.getId());
		for(IFormFieldMapping key : values.keySet()){
			IFormInstanceFieldState state = null;
			
			for(IFormInstanceFieldState fs : states){
				if (fs.getFormFieldMappingId() == key.getId()){
					state = fs;
					break;
				}
			}
			
			if (state == null){
				state = component.getFormFactory().createFormInstanceFieldState();
			}
			
			state.setFormFieldMappingId(key.getId());
			state.setFormInstanceId(formInstance.getId());
			state.setValue(values.get(key));
			
			component.getFormServiceProvider().getInstanceService().save(state);
			
		}
		
	}

}
