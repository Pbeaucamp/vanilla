package bpm.vanilla.portal.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.viewer.FormsDTO;
import bpm.gwt.commons.shared.viewer.FormsDTO.PortalFormType;
import bpm.vanilla.platform.core.components.forms.IForm;
import bpm.vanilla.platform.core.components.forms.IForm.IFormType;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHTMLForms;
import bpm.vanilla.portal.client.services.PerFormsService;
import bpm.vanilla.portal.server.security.PortalSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class PerFormsServiceImpl extends RemoteServiceServlet implements PerFormsService {
	
	private static final long serialVersionUID = 2547136924258859253L;
	
	private Logger logger = Logger.getLogger(getClass());

	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.info("Initing PerformsServiceImpl...");
	}

	private PortalSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), PortalSession.class);
	}
	
	/**
	 * Only for my groups
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public List<FormsDTO> getForms() throws ServiceException {
		
		PortalSession session = getSession();
		
		logger.info("Listing forms for group " + session.getCurrentGroup().getName());
		
		RemoteHTMLForms formComponent = session.getFormComponent();
		
		List<FormsDTO> list = new ArrayList<FormsDTO>();
		
		try {
			List<IForm> listForms = formComponent.getActiveForms(session.getCurrentGroup().getId());
			
			for (IForm form : listForms) {
				
				PortalFormType portalType = null;
				
				if (form.getType() == IFormType.Workflow) {
					portalType = PortalFormType.Workflow;
				}
				else if (form.getType() == IFormType.VanillaFormValidation) {
					portalType = PortalFormType.VanillaFormValidation;
				}
				else if (form.getType() == IFormType.VanillaFormSubmission) {
					portalType = PortalFormType.VanillaFormSubmission;
				}
				else {
					throw new ServiceException("Unsupported vanilla form type : " + form.getType().toString());
				}
				
				FormsDTO dto = new FormsDTO(portalType, form.getFormName(), form.getCreatedDate(), 
						form.getHtmlFormUrl(), form.getOriginName());

				logger.debug("Found a form with " + form.getFormName());
				
				list.add(dto);
			}
		} catch (Exception e) {
			logger.error("Failed to get forms to submit : " + e.getMessage() + 
					", for current group with name " + session.getCurrentGroup().getName(), e);
		}
		
		return list;
	}



	@Override
	public String submit(FormsDTO form) throws ServiceException {
		
		return "";
		
		//IOUtils.toString(is, "UTF-8");
//		List<IFormInstance> listInstances;
//		try {
//			listInstances = instanceService.getFormsToSubmit(groupId);
//		} catch (Exception e1) {
//			logger.error("could not find a valid instance :" + e1.getMessage());
//			return "error: could not find a valid instance";
//		}
//		
//		IFormInstance target = null;
//		
//		for (IFormInstance formInstance : listInstances) {
//			if (formInstance.getId() == form.getFormInstanceId() &&
//					formInstance.getFormDefinitionId() == form.getFormDefinitionId()) {
//				target = formInstance;
//				break;
//			}
//		}
//		
//		if (target == null) {
//			return "error: could not find a valid instance";
//		}
//		
//		String url = "";
//		
//		Context c = new Context(login, password, groupId, 
//				portalConfig.getVanillaUrl(), portalConfig.getServerSetup().getVanillaRuntimeServersUrl());
//		try {
//			url = InstanceAccessHelper.getFormUrl(target, c);
//		} catch (Exception e) {
//			url = "error: Failed to get form url :" + e.getMessage();
//			logger.error("Failed to get form url :" + e.getMessage());
//		}
//		
//		return url;
	}

	@Override
	public String validate(FormsDTO form) throws ServiceException {
		return "";
//		List<IFormInstance> listInstances;
//		try {
//			listInstances = instanceService.getFormsToValidate(groupId);
//		} catch (Exception e1) {
//			logger.error("could not find a valid instance :" + e1.getMessage());
//			return "error: could not find a valid instance";
//		}
//		
//		IFormInstance target = null;
//		
//		for (IFormInstance formInstance : listInstances) {
//			if (formInstance.getId() == form.getFormInstanceId() &&
//					formInstance.getFormDefinitionId() == form.getFormDefinitionId()) {
//				target = formInstance;
//				break;
//			}
//		}
//		
//		if (target == null) {
//			return "error: could not find a valid instance";
//		}
//		
//		String url = "";
//		
//		Context c = new Context(login, password, groupId, 
//				portalConfig.getVanillaUrl(), portalConfig.getServerSetup().getVanillaRuntimeServersUrl());
//		try {
//			url = InstanceAccessHelper.getValidationFormUrl(target, c);
//		} catch (Exception e) {
//			url = "error: Failed to get form url :" + e.getMessage();
//			logger.error("Failed to get form url :" + e.getMessage());
//		}
//		
//		return url;
	}
}
