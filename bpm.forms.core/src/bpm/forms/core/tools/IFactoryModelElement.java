package bpm.forms.core.tools;

import java.util.List;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.core.design.IFormUIProperty;
import bpm.forms.core.design.IFormUi;
import bpm.forms.core.design.ITargetTable;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IFormInstanceFieldState;

/**
 * A Factory to create all Core object requested for designing a IForm
 * @author ludo
 *
 */
public interface IFactoryModelElement {

	public IForm createForm();
	
	public IFormDefinition createFormDefinition();
	
	public IFormInstance createFormInstance();
	
	public IFormFieldMapping createFormFieldMapping();
	
	public IFormUIProperty createFormUiProperty();
	
	public IFormUi createFormUi();
	
	
	public List<Class<? extends IForm>> getFormImplementors();
	public List<Class<? extends IFormDefinition>> getFormDefinitionImplementors();
	public List<Class<? extends IFormInstance>> getFormInstanceImplementors();
	public List<Class<? extends IFormFieldMapping>> getFormFieldMappingImplementors();
	public List<Class<? extends IFormUIProperty>> getFormUIPropertyImplementors();
	
	public List<Class<? extends IFormUi>> getFormUiImplementors();

	public ITargetTable createTargetTable();

	public IFormInstanceFieldState createFormInstanceFieldState();
	
	
	
}
