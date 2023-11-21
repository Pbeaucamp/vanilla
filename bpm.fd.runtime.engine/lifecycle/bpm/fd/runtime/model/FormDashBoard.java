package bpm.fd.runtime.model;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.runtime.model.ui.jsp.JSPCanvasGenerator;
import bpm.fd.runtime.model.ui.jsp.VanillaFormJspGenerator;
import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.core.design.internal.FormsUIInternalConstants;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IFormInstanceFieldState;
import bpm.forms.remote.services.RemoteServiceProvider;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;

public class FormDashBoard extends DashBoard{
	private class FormDashBoardMeta extends DashBoardMeta{
		public FormDashBoardMeta(Date loadingDate,
				IObjectIdentifier identifier, FdProject project) {
			super(loadingDate, identifier, project);
		}

		@Override
		public String getIdentifierString() {
			return super.getIdentifierString() + "-" + validationForm;
		}
	}

	private HashMap<String, String> hiddenParameterMap;
	private boolean validationForm;
	
	public FormDashBoard(boolean validationForm, IObjectIdentifier identifier, File jspFile,
			FdProject project, HashMap<String, String> hiddenParameterMap) throws Exception {
		super(identifier, jspFile, project, null);
		setMeta(new DashBoardMeta(new Date(), identifier, getProject()));
		this.hiddenParameterMap = hiddenParameterMap;
		this.validationForm = validationForm;
	}
	
	protected String generateJspCode() throws Exception{
		JSPCanvasGenerator canva = new VanillaFormJspGenerator(validationForm, getMeta(), getProject().getFdModel(), hiddenParameterMap);
		return canva.getJsp();
	}
	
	@Override
	public DashInstance createInstance(Group group, User user,
			String languageLocale) {
		DashInstance instance = super.createInstance(group, user, languageLocale);
		//if this is a validation form we have to set the DashState with the submited values
		DashState state = instance.getState();
		//TODO : set fields values
		
		RemoteServiceProvider formsRemote = new RemoteServiceProvider();
		formsRemote.setUrl(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl());
		
		
		
		try {
			
			Integer formInstanceId = hiddenParameterMap.get(FormsUIInternalConstants.VANILLA_FORM_INSTANCE_ID) != null ? Integer.valueOf(hiddenParameterMap.get(FormsUIInternalConstants.VANILLA_FORM_INSTANCE_ID)) : null;
			
			IFormInstance formInstance = formsRemote.getInstanceService().getRunningInstance(formInstanceId);
			
			List<IFormFieldMapping>  mapping = formsRemote.getDefinitionService().getFormDefinition(formInstance.getFormDefinitionId()).getIFormFieldMappings();

			List<IFormInstanceFieldState> l = formsRemote.getInstanceService().getFieldsState(formInstanceId);
			
			for(IFormInstanceFieldState s : l){
				
				for(IFormFieldMapping m : mapping){
					if (m.getId() == s.getFormFieldMappingId()){
						state.setComponentValue(m.getFormFieldId(), s.getValue());
						break;
					}
				}
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return instance;
	}
}
