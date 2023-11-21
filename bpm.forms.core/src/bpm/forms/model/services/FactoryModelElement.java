package bpm.forms.model.services;

import java.util.ArrayList;
import java.util.List;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.core.design.IFormUIProperty;
import bpm.forms.core.design.IFormUi;
import bpm.forms.core.design.ITargetTable;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IFormInstanceFieldState;
import bpm.forms.core.tools.IFactoryModelElement;
import bpm.forms.model.impl.Form;
import bpm.forms.model.impl.FormDefinition;
import bpm.forms.model.impl.FormFieldMapping;
import bpm.forms.model.impl.FormInstance;
import bpm.forms.model.impl.FormInstanceFieldState;
import bpm.forms.model.impl.FormUIFd;
import bpm.forms.model.impl.FormUIProperty;
import bpm.forms.model.impl.TargetTable;

public class FactoryModelElement implements IFactoryModelElement{

	public static final String PLUGIN_ID = "bpm.forms.model";
	
	
	private static List<Class<? extends IFormDefinition>> formDefClasses = new ArrayList<Class<? extends IFormDefinition>>();
	private static List<Class<? extends IForm>> formClasses = new ArrayList<Class<? extends IForm>>();
	private static List<Class<? extends IFormFieldMapping>> formFieldMappingClasses = new ArrayList<Class<? extends IFormFieldMapping>>();
	private static List<Class<? extends IFormInstance>> formInstanceClasses = new ArrayList<Class<? extends IFormInstance>>();
	private static List<Class<? extends IFormUi>> formUiClasses = new ArrayList<Class<? extends IFormUi>>();
	private static List<Class<? extends IFormUIProperty>> formUIPropertyClasses = new ArrayList<Class<? extends IFormUIProperty>>();
	
	static{
		formDefClasses.add(FormDefinition.class);
		formClasses.add(Form.class);
		formFieldMappingClasses.add(FormFieldMapping.class);
		formInstanceClasses.add(FormInstance.class);
		formUiClasses.add(FormUIFd.class);
		formUIPropertyClasses.add(FormUIProperty.class);
		
	}
	@Override
	public IForm createForm() {
		return new Form();
	}

	@Override
	public IFormDefinition createFormDefinition() {
		return new FormDefinition();
	}

	@Override
	public IFormFieldMapping createFormFieldMapping() {
		return new FormFieldMapping();
	}

	@Override
	public IFormInstance createFormInstance() {
		return new FormInstance();
	}

	@Override
	public IFormUi createFormUi() {
		return new FormUIFd();
	}

	@Override
	public IFormUIProperty createFormUiProperty() {
		return new FormUIProperty();
	}

	@Override
	public List<Class<? extends IFormDefinition>> getFormDefinitionImplementors() {
		return formDefClasses;
	}

	@Override
	public List<Class<? extends IFormFieldMapping>> getFormFieldMappingImplementors() {
		return formFieldMappingClasses;
	}

	@Override
	public List<Class<? extends IForm>> getFormImplementors() {
		return formClasses;
	}

	@Override
	public List<Class<? extends IFormInstance>> getFormInstanceImplementors() {
		return formInstanceClasses;
	}

	@Override
	public List<Class<? extends IFormUIProperty>> getFormUIPropertyImplementors() {
		return formUIPropertyClasses;
	}

	@Override
	public List<Class<? extends IFormUi>> getFormUiImplementors() {
		return formUiClasses;
	}

	@Override
	public ITargetTable createTargetTable() {
		return new TargetTable();
	}

	@Override
	public IFormInstanceFieldState createFormInstanceFieldState() {
		return new FormInstanceFieldState();
	}

	
	
}
