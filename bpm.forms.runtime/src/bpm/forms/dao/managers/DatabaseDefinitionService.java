package bpm.forms.dao.managers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.forms.core.design.IDefinitionService;
import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.core.design.IFormUIProperty;
import bpm.forms.core.design.IFormUi;
import bpm.forms.core.design.IInstanciationRule;
import bpm.forms.core.design.ITargetTable;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.dao.VanillaFormsDaoComponent;
import bpm.forms.dao.internal.GroupInstanciationMapping;
import bpm.forms.dao.internal.GroupValidatorMapping;
import bpm.forms.model.impl.FormDefinitionTableMapping;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class DatabaseDefinitionService extends HibernateDaoSupport implements IDefinitionService{
	
	
	private VanillaFormsDaoComponent component;
	
	/**
	 * save the given FormDefinition in database
	 * @param form
	 * @throws Exception
	 */
	public IFormDefinition saveFormDefinition(IFormDefinition form) throws Exception{
		
		if (form.getCreationDate() == null){
			form.setCreationDate(Calendar.getInstance().getTime());
		}
		Long l = (Long)getHibernateTemplate().save(form);
		
		for(IFormFieldMapping f : form.getIFormFieldMappings()){
			f.setFormDefinitionId(form.getId());
			getHibernateTemplate().save(f);
			
		}
		for(ITargetTable t : form.getITargetTables()){
			FormDefinitionTableMapping m = new FormDefinitionTableMapping();
			m.setFormDefinitionId(form.getId());
			m.setTargetTableId(t.getId());
			
			getHibernateTemplate().save(m);
		}
		
		if (form.getFormUI() != null){
			
			for(IFormUIProperty p : form.getFormUI().getProperties()){
								
				p.setFormUiName(form.getFormUI().getName());
				p.setFormDefinitionId(l);
				
				Long pId = (Long)getHibernateTemplate().save(p);
				p.setId(pId);
				
			}
			
			
		}
		
		
		form.setId(l);
		
		return form;
	}
	
	
	/**
	 * load the ITargetTables and the FieldMappings for the given form
	 * @param form
	 */
	private void rebuildForm(IFormDefinition form){
		for(FormDefinitionTableMapping tMap : (List<FormDefinitionTableMapping>)getHibernateTemplate().find("from FormDefinitionTableMapping where formDefinitionId=" + form.getId())){
			ITargetTable t = null;
			try{
				t = (ITargetTable) getHibernateTemplate().find("from TargetTable where id=" + tMap.getTargetTableId()).get(0);
			}catch(Exception ex){
//				ex.printStackTrace();
				if (form.isActivated()){
					Logger.getLogger(getClass()).warn("The FormDefinition " + form.getId() + " is activated althought no target Table is defined");
				}
				if (form.isDesigned()){
					Logger.getLogger(getClass()).warn("The FormDefinition " + form.getId() + " is activated althought no target Table is defined");
				}
				
			}
			
			if (t != null){
				form.addTargetTable(t);
			}
			
		}
		
		for(IFormFieldMapping fMap : (List<IFormFieldMapping>)getHibernateTemplate().find("from FormFieldMapping where formDefinitionId=" + form.getId())){
			
			form.addFormFieldMapping(fMap);
		}
		
		/*
		 * UIForm
		 */
		List<IFormUIProperty> propsUi = (List<IFormUIProperty>) getHibernateTemplate().find("from FormUIProperty where formDefinitionId=" + form.getId());
		
		if (!propsUi.isEmpty()){
			
			IFormUi formUi = component.getFactoryModelElement().createFormUi();
			for(IFormUIProperty p : propsUi){
				formUi.setProperty(p.getPropertyName(), p.getPropertyValue());
			}
			
			form.setFormUI(formUi);
		}
		
	}
	
	
	/**
	 * 
	 * @return all the IFOrmDefinition in the Database
	 * the IFOrmDefinition are rebuilt(means that there ITargetTable and FieldMappings are availables
	 */
	public List<IForm> getForms(){
		List<IForm> forms = getHibernateTemplate().find("from Form");
		
		for(IForm f : forms){
			// load groupsVaidators
			for( GroupValidatorMapping m : (List<GroupValidatorMapping>)getHibernateTemplate().find("from GroupValidatorMapping where formId=" + f.getId())){
				f.addValidationGroup(m.getVanillaGroupId());
			}
			
			//load instanceRules
			
			for(IInstanciationRule r : (List<IInstanciationRule>)getHibernateTemplate().find("from InstanciationRule where formId=" + f.getId())){
				f.getInstanciationRule().setMode(r.getMode());
				f.getInstanciationRule().setScheduledType(r.getScheduledType());
				f.getInstanciationRule().setUniqueIp(r.isUniqueIp());
				
				//add InstanceGroups
				for(GroupInstanciationMapping m : (List<GroupInstanciationMapping>)getHibernateTemplate().find("from GroupInstanciationMapping where formId=" + f.getId())){
					f.getInstanciationRule().addGroupId(m.getVanillaGroupId());
				}
			}
			
		}
		
		return forms;
		
	}
	
	
	
	public List<IFormDefinition> getFormDefinitionVersions(long formId){
		List<IFormDefinition> forms = getHibernateTemplate().find("from FormDefinition where formId=" + formId);
		
		for(IFormDefinition f : forms){
			rebuildForm(f);
		}

		return forms;
	}
	
	public IFormDefinition getFormDefinition(long id)throws Exception{
		List l = getHibernateTemplate().find("from FormDefinition where id=" + id);
		if (l.isEmpty()){
			return null;
		}
		rebuildForm((IFormDefinition)l.get(0));
		return (IFormDefinition)l.get(0);
		
	}
	
	public void update(IFormDefinition form)throws Exception{
		
		for(IFormInstance inst : component.getInstanceService().getRunningInstances(form)){
			if (!inst.isSubmited()){
				throw new Exception("This FormDefinition has FormInstance which are not submited. It cannot be updated.");
			}
			if (!inst.isValidated()){
				throw new Exception("This FormDefinition has FormInstance which are not validated. It cannot be updated.");
			}
		}
		IFormDefinition storedDefinition = getFormDefinition(form.getId());
		
		/*
		 * remove TargetTables mappings
		 */
		for(ITargetTable t : storedDefinition.getITargetTables()){
			boolean found = false;
			for(ITargetTable tt : form.getITargetTables()){
				if (t.getId() == tt.getId()){
					found = true;
				}
			}
			if (!found){
				for(FormDefinitionTableMapping m : (List<FormDefinitionTableMapping>)getHibernateTemplate().find("from FormDefinitionTableMapping where formDefinitionId=" + form.getId() + " and targetTableId=" + t.getId())){
					getHibernateTemplate().delete(m);
				}
			}
		}
		
		/*
		 * add TargetTables mapping
		 */
		for(ITargetTable t : form.getITargetTables()){
			boolean found = false;
			for(ITargetTable tt : storedDefinition.getITargetTables()){
				if (t.getId() == tt.getId()){
					found = true;
					break;
				}
			}
			if (!found){
				FormDefinitionTableMapping m = new FormDefinitionTableMapping();
				m.setFormDefinitionId(form.getId());
				m.setTargetTableId(t.getId());
				getHibernateTemplate().save(m);
			}
		}
		
		/*
		 * remove FormFieldMapping
		 */
		for(IFormFieldMapping m : storedDefinition.getIFormFieldMappings()){
			boolean found = false;
			for(IFormFieldMapping mm : form.getIFormFieldMappings()){
				if (mm.getId() == m.getId()){
					found = true;
					break;
				}
			}
			if (!found){
				for(IFormFieldMapping mm : (List<IFormFieldMapping>)getHibernateTemplate().find("from FormFieldMapping where id=" + m.getId())){
					
					
					getHibernateTemplate().delete(mm);
				}
			}
		}
		
		/*
		 * add FormFieldMapping
		 */
		for(IFormFieldMapping m : form.getIFormFieldMappings()){
			getHibernateTemplate().saveOrUpdate(m);
			
		}
		
		/*
		 * FormUi
		 */
		if (form.getFormUI() != null){
			for(IFormUIProperty p : form.getFormUI().getProperties()){
				getHibernateTemplate().saveOrUpdate(p);
			}
		}
		else{
			for(IFormUIProperty p : (List<IFormUIProperty>)getHibernateTemplate().find("from FormUIProperty where formDefinitionId=" + form.getId())){
				getHibernateTemplate().delete(p);
			}
		}
		
		
		
		
		getHibernateTemplate().update(form);
		
	}
	
	public void delete(IFormDefinition form)throws Exception{
		for(IFormFieldMapping fMap : form.getIFormFieldMappings()){
			getHibernateTemplate().delete(fMap);
		}
		
		for(ITargetTable t : form.getITargetTables()){
			for(FormDefinitionTableMapping m : (List<FormDefinitionTableMapping>)getHibernateTemplate().find("from FormDefinitionTableMapping where targetTableId=" + t.getId())){
				getHibernateTemplate().delete(m);
				
			}
		}
		
		if (form.getFormUI() != null){
			for(IFormUIProperty p : form.getFormUI().getProperties()){
				getHibernateTemplate().delete(p);
			}
		}
		
		
		/*
		 *delete the object stored for the runtime 
		 */
		component.getInstanceService().deleteFor(form);
		
		
		
		
		getHibernateTemplate().delete(form);
		
		//TODO : delete instances
	}

	@Override
	public IForm saveForm(IForm form) throws Exception {
		if (form.getCreationDate() == null){
			form.setCreationDate(Calendar.getInstance().getTime());
		}
		Long l = (Long)getHibernateTemplate().save(form);
		form.setId(l);
		
		
		for(Integer i : form.getValidatorGroups()){
			GroupValidatorMapping m = new GroupValidatorMapping();
			m.setFormId(l);
			m.setVanillaGroupId(i);
			
			getHibernateTemplate().save(m);
		}
		
		return form;
	}
	
	public void update(IForm form) throws Exception{
		getHibernateTemplate().update(form);
		
		//instanceRules
		form.getInstanciationRule().setFormId(form.getId());
		getHibernateTemplate().saveOrUpdate(form.getInstanciationRule());
		
		//remove groupInstance
		for(GroupInstanciationMapping m : (List<GroupInstanciationMapping>)getHibernateTemplate().find("from GroupInstanciationMapping where formId=" + form.getId())){
			
			boolean found = false;
			
			for(Integer i : form.getInstanciationRule().getGroupId()){
				if (i.intValue() == m.getVanillaGroupId()){
					found = true;
					break;
				}
			}
			
			if (!found){
				getHibernateTemplate().delete(m);
			}
		}
		
		//add groupInstance
		List<GroupInstanciationMapping> lg = getHibernateTemplate().find("from GroupInstanciationMapping where formId=" + form.getId());
		for(Integer i : form.getInstanciationRule().getGroupId()){
			boolean found = false;
			for(GroupInstanciationMapping m : lg){
				if (i.intValue() == m.getVanillaGroupId()){
					found = true;
					break;
				}
			}
			
			if (!found){
				GroupInstanciationMapping m = new GroupInstanciationMapping();
				m.setFormId(form.getId());
				m.setVanillaGroupId(i);
				getHibernateTemplate().save(m);
			}
		}
		
		
//		for(InstanciationRule r : (List<InstanciationRule>)getHibernateTemplate().find("from InstanciationRule where formId=" + f.getId())){
//			f.getInstanciationRule().setMode(r.getMode());
//			f.getInstanciationRule().setScheduledType(r.getScheduledType());
//			f.getInstanciationRule().setUniqueIp(r.isUniqueIp());
//			
//			//add InstanceGroups
//			for(GroupInstanciationMapping m : (List<GroupInstanciationMapping>)getHibernateTemplate().find("from GroupInstanciationMapping where formId=" + f.getId())){
//				f.getInstanciationRule().addGroupId(m.getVanillaGroupId());
//			}
//		}
		
		
		//add GroupValidator
		for(Integer i : form.getValidatorGroups()){
			
			List<GroupValidatorMapping> maps = getHibernateTemplate().find("from GroupValidatorMapping where formId=" + form.getId() + " and vanillaGroupId=" + i);
			
			if(maps.isEmpty()){
				GroupValidatorMapping m = new GroupValidatorMapping();
				m.setFormId(form.getId());
				m.setVanillaGroupId(i);
				
				getHibernateTemplate().save(m);
			}
			
		}
		
		//remove GroupValidator
		List<GroupValidatorMapping> maps = getHibernateTemplate().find("from GroupValidatorMapping where formId=" + form.getId() );
		
		for(GroupValidatorMapping m : maps){
			boolean found = false;
			for(Integer i : form.getValidatorGroups()){
				if (m.getVanillaGroupId() == i){
					found = true;
					break;
				}
			}
			if (!found){
				getHibernateTemplate().delete(m);
			}
		}
	}

	@Override
	public void delete(IForm o) throws Exception {
		
		for(IFormDefinition def : getFormDefinitionVersions(o.getId())){
			delete(def);
		}
		
		getHibernateTemplate().delete(o);
		
	}

	@Override
	public List<ITargetTable> getTargetTables() {
		return getHibernateTemplate().find("from TargetTable");
	}

	@Override
	public ITargetTable saveTargetTable(ITargetTable table) throws Exception {
		if (table == null){
			throw new Exception("Cannot a null ITargetTable");
		}
		
		List l = getHibernateTemplate().find("from TargetTable where upper(name)=upper('" + table.getName() + "')");
		
		if (!l.isEmpty()){
			throw new Exception("A ITargetTable with the label " + table.getName() + " already exists");
		}
		
		
		Long id = (Long)getHibernateTemplate().save(table);
		table.setId(id);
		
		return table;
	}

	@Override
	public List getColumnsForTargetTable(Long targetTableId) {
		List<String> l = new ArrayList<String>();
		
		for(IFormFieldMapping f : (List<IFormFieldMapping>)getHibernateTemplate().find("from FormFieldMapping where targetTableId=" + targetTableId)){
			
			if (f.getDatabasePhysicalName() != null && !"".equals(f.getDatabasePhysicalName())){
				boolean found = false;
				for(String s : l){
					if (s.equals(f.getDatabasePhysicalName())){
						found = true;
						break;
					}
				}
				
				if (!found){
					l.add(f.getDatabasePhysicalName());
				}
			}
			
		}
		
		
		return l;
	}

	@Override
	public IFormDefinition getActiveFormDefinition(IForm form) {
		for(IFormDefinition fd : getFormDefinitionVersions(form.getId())){
			
			if (!fd.isDesigned() && !fd.isActivated()){
				continue;
			}
			if (fd.getStartDate() != null){
				if (!fd.getStopDate().after(Calendar.getInstance().getTime())){
					continue;	
				}
			}
			return fd;
			
		}
		return null;
	}

	@Override
	public ITargetTable getTargetTable(Long targetTableId) {
		List l = getHibernateTemplate().find("from TargetTable where id = " + targetTableId);
		
		if (l.isEmpty()){
			return null;
		}
		return (ITargetTable)l.get(0);
	}

	@Override
	public IForm getForm(long formId) {
		List<IForm> forms = getHibernateTemplate().find("from Form where id=" + formId);
		
		for(IForm f : forms){
			// load groupsVaidators
			for( GroupValidatorMapping m : (List<GroupValidatorMapping>)getHibernateTemplate().find("from GroupValidatorMapping where formId=" + f.getId())){
				f.addValidationGroup(m.getVanillaGroupId());
			}
			
			//load instanceRules
			
			for(IInstanciationRule r : (List<IInstanciationRule>)getHibernateTemplate().find("from InstanciationRule where formId=" + f.getId())){
				f.getInstanciationRule().setMode(r.getMode());
				f.getInstanciationRule().setScheduledType(r.getScheduledType());
				f.getInstanciationRule().setUniqueIp(r.isUniqueIp());
				
				//add InstanceGroups
				for(GroupInstanciationMapping m : (List<GroupInstanciationMapping>)getHibernateTemplate().find("from GroupInstanciationMapping where formId=" + f.getId())){
					f.getInstanciationRule().addGroupId(m.getVanillaGroupId());
				}
			}
		}
		
		if (forms.isEmpty()){
			return null;
		}
		return forms.get(0);
	}

	@Override
	public void configure(Object object) {
		if (object instanceof VanillaFormsDaoComponent){
			this.component = (VanillaFormsDaoComponent)object;
			Logger.getLogger(getClass()).info("DataBaseDefinitionService configured");
		}
		
	}
}
