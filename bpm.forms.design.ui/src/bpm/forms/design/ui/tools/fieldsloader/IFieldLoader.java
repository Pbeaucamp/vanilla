package bpm.forms.design.ui.tools.fieldsloader;

import bpm.forms.core.design.IFormDefinition;

public interface IFieldLoader {

	/**
	 * load the fields from the formDefinition form its IFormUI 
	 * @param formUi
	 * @throws Exception
	 */
	public void loadFields(IFormDefinition formDefinition) throws Exception;
}
