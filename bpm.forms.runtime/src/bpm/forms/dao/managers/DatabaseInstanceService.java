package bpm.forms.dao.managers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IFormInstanceFieldState;
import bpm.forms.core.runtime.IInstanceService;
import bpm.forms.dao.VanillaFormsDaoComponent;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;


public class DatabaseInstanceService extends HibernateDaoSupport implements IInstanceService{

	private VanillaFormsDaoComponent component;
	
	@Override
	public void delete(IFormInstance instance) {
		
		for(IFormInstanceFieldState s : getFieldsState(instance.getId())){
			getHibernateTemplate().delete(s);
		}
		
		getHibernateTemplate().delete(instance);
		
	}

	@Override
	public List<IFormInstance> getRunningInstances(IForm form) {
		List l = getHibernateTemplate().find("from FormDefinition def, FormInstance inst where def.id=inst.formDefinitionId and def.formId=" + form.getId());
		
		List<IFormInstance> inst = new ArrayList<IFormInstance>();
		for(Object o : l){
			if (o instanceof Object[]){
				
				if (!inst.contains(((Object[])o)[1])){
					inst.add((IFormInstance)((Object[])o)[1]);
				}
			}
		}
		
		return inst;
	}

	@Override
	public List<IFormInstance> getRunningInstances(IFormDefinition formDefinition) {
		return getHibernateTemplate().find("from FormInstance where formDefinitionId=" + formDefinition.getId());
	}



	@Override
	public IFormInstance save(IFormInstance instance) throws Exception {
		
		Long id = (Long)getHibernateTemplate().save(instance);
		instance.setId(id);
		
		/*
		 * create FieldStaates
		 */
		for(IFormFieldMapping fMap : (List<IFormFieldMapping>)getHibernateTemplate().find("from FormFieldMapping where formDefinitionId=" + instance.getFormDefinitionId())){
			
			IFormInstanceFieldState state = component.getFactoryModelElement().createFormInstanceFieldState();
			
			
			
			state.setFormFieldMappingId(fMap.getId());
			state.setFormInstanceId(instance.getId());
			state.setValue(null);
			
			getHibernateTemplate().save(state);
		}
		
		return instance;
		
	}

	@Override
	public IFormInstance getRunningInstance(long instanceId) {
		
		IFormInstance instance = null;
		for (Object o : getHibernateTemplate().find("from FormInstance inst, FormInstanceFieldState field where inst.id=" + instanceId + " and field.formInstanceId=inst.id")){
			if (instance == null){
				Object _o = ((Object[])o)[0];
				instance = (IFormInstance)_o;
			}
			Object _o = ((Object[])o)[1];
			
			instance.addFieldState(((IFormInstanceFieldState)_o));
		}
		return instance;
	}

	@Override
	public IFormInstanceFieldState save(IFormInstanceFieldState fieldState) throws Exception {
		
		
				
		if (fieldState.getId() <= 0){
			Long id = (Long)getHibernateTemplate().save(fieldState);
			fieldState.setId(id);
		}
		else{
			getHibernateTemplate().update(fieldState);
		}
		return fieldState;
		
	}
	
	@Override
	public void update(IFormInstanceFieldState fieldState) throws Exception {
		getHibernateTemplate().update(fieldState);
				
	}

	@Override
	public List<IFormInstanceFieldState> getFieldsState(long instanceId) {
		return getHibernateTemplate().find("from FormInstanceFieldState where formInstanceId=" + instanceId);

	}

	@Override
	public List<IFormInstance> getFormsToSubmit(int groupId) {
		return  getHibernateTemplate().find("from FormInstance where groupId=" + groupId + " and formSubmited = 0");
	}

	@Override
	public List<IFormInstance> getFormsToValidate(int groupId) {
		return  getHibernateTemplate().find("from FormInstance where groupId=" + groupId + " and formSubmited > 0 and formValidated = 0");
	}

	@Override
	public void update(IFormInstance formInstance) {
		getHibernateTemplate().update(formInstance);
		
	}
	
	public void deleteFor(IFormDefinition form) throws Exception{
		for(IFormInstance i : getRunningInstances(form)){
			if (!i.isSubmited()){
				throw new Exception("Cannot delete a FormInstance not submited");
			}
			if (! i.isValidated()){
				throw new Exception("Cannot delete a FormInstance not validated");
			}
			
			
			for(IFormInstanceFieldState s : getFieldsState(i.getId())){
				getHibernateTemplate().delete(s);
			}
			
			getHibernateTemplate().delete(i);
		}
	}

	@Override
	public void configure(Object object) {
		if (object instanceof VanillaFormsDaoComponent){
			this.component = (VanillaFormsDaoComponent)object;
			Logger.getLogger(getClass()).info("DataBaseDefinitionService configured");
		}
		
	}
}
