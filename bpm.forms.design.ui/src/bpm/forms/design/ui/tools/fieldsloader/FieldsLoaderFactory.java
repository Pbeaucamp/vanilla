package bpm.forms.design.ui.tools.fieldsloader;

import bpm.forms.core.design.IFormUi;

public class FieldsLoaderFactory {

	
	public static IFieldLoader getFieldLoader(IFormUi formUi) throws Exception{
		
		if (formUi == null){
			throw new NullPointerException("Cannot find a IFieldLoader for null IFormUi"); //$NON-NLS-1$
		}
		
//		if (formUi instanceof FormUIFd){
			return new FormUiFdFieldLoader();
//		}
//		throw new Exception(formUi.getClass() + " not supported");
		
	}
}
