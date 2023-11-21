package bpm.forms.design.ui.tools.fieldsloader;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.core.design.IFormUi;
import bpm.forms.core.design.ui.VanillaFdProperties;
import bpm.forms.design.ui.Activator;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FormUiFdFieldLoader implements IFieldLoader{

	public static final String[] fieldProviderClasses = new String[]{
		"bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition" //$NON-NLS-1$
	};
	
	private IRepositoryApi repClient;
	
	
	@Override
	public void loadFields(IFormDefinition formDefinition) throws Exception {
		IFormUi ui = formDefinition.getFormUI();
		
		if (ui == null){
			throw new Exception("IFormUi is null."); //$NON-NLS-1$
		}
		
		initRepositoryClient(ui);
		
		/*
		 * load FdModel 
		 */
		RepositoryItem item = repClient.getRepositoryService().getDirectoryItem(Integer.parseInt(ui.getPropertyValue(VanillaFdProperties.PROP_FD_DIRECTORY_ITEM_ID)));
		String itemXml = repClient.getRepositoryService().loadModel(item);
		
		/*
		 * parse FdModel
		 */
		List<String> componentNames = new ArrayList<String>();
		
		Document fdModelDoc = DocumentHelper.parseText(itemXml);


		
		// getUsedComponentIds
		for(Node n : (List<Node>)fdModelDoc.getRootElement().selectNodes("//component-ref")){ //$NON-NLS-1$
			
			for (String s : fieldProviderClasses){
				if (((Element)n).attributeValue("class").equals(s)){ //$NON-NLS-1$
					componentNames.add(((Element)n).attributeValue("name")); //$NON-NLS-1$
				}
			}
		}
		
		/*
		 * update the FormDefinition
		 */
		
		//remove fields that are no longer available in the IFormUI
		List<IFormFieldMapping> toRemove = new ArrayList<IFormFieldMapping>();
		
		for(IFormFieldMapping m : formDefinition.getIFormFieldMappings()){
			
			boolean found = false;
			for(String s : componentNames){
				if (m.getFormFieldId().equals(s)){
					found = true;
					break;
				}
			}
			if (!found){
				toRemove.add(m);
			}
			
		}
		for(IFormFieldMapping f : toRemove){
			formDefinition.removeFormFieldMapping(f);
		}
		
		
		
		// add the missing fields
		for(String s : componentNames){
			
			boolean found = false;
			
			for(IFormFieldMapping m : formDefinition.getIFormFieldMappings()){
				if (m.getFormFieldId().equals(s)){
					found = true;
					break;
				}
			}
			if (!found){
				
				IFormFieldMapping field = Activator.getDefault().getFactoryModel().createFormFieldMapping();
				field.setFormFieldId(s);
				formDefinition.addFormFieldMapping(field);
			}
			
		}
		
		
		
		
	}
	
	
	private void initRepositoryClient(IFormUi ui) throws Exception{
		
		
		Repository repDef = null;
		
		try{
			repDef = Activator.getDefault().getVanillaContext().getVanillaApi().getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(ui.getPropertyValue(VanillaFdProperties.PROP_FD_REPOSITORY_ID)));
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Unable to load RepositoryDefinition from Vanilla Server :" + ex.getMessage(), ex); //$NON-NLS-1$
		}

		try{
			repClient = new RemoteRepositoryApi(new BaseRepositoryContext(
					Activator.getDefault().getVanillaContext().getVanillaContext()
					
					, Activator.getDefault().getVanillaContext().getGroup(), repDef));
		}catch(Exception ex){
			repClient = null;
			ex.printStackTrace();
			throw new Exception("Unable to create RepositoryClient :" + ex.getMessage(), ex); //$NON-NLS-1$
		}
		
		
		
		
	}

}
