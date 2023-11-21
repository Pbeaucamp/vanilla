package bpm.vanilla.api.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import bpm.vanilla.api.controller.commons.APIResponse;
import bpm.vanilla.api.core.IVanillaAPIManager;
import bpm.vanilla.api.core.model.ItemRunInformations;
import bpm.vanilla.api.core.remote.RemoteVanillaApiManager;
import bpm.vanilla.api.core.remote.internal.HttpCommunicator;
import bpm.vanilla.api.dto.GenerateIntegrationParameter;
import bpm.vanilla.api.dto.GenerateKpiParameter;
import bpm.vanilla.api.dto.GenerateSimpleKpiParameter;
import bpm.vanilla.api.dto.RunVanillaHubParameter;
import bpm.vanilla.api.dto.RunVanillaParameter;
import bpm.vanilla.api.dto.UploadFile;
import bpm.vanilla.api.dto.ValidationDataParameter;
import bpm.vanilla.api.dto.VanillaHubParameter;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractType;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunIdentifier;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

@CrossOrigin
@RestController
public class VanillaAPIController {

	private IVanillaAPI vanillaApi;
	private IVanillaAPIManager manager;
	private IGedComponent gedManager;
	private GatewayComponent etlManager;
	private WorkflowService workflowService;

	public VanillaAPIController() {
		//TODO: Security
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
		String rootLogin = conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String rootPassword = conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		String vanillaUrl = conf.getProperty(VanillaConfiguration.P_VANILLA_URL);
		
		
		HttpCommunicator httpCommunicator = new HttpCommunicator();
		httpCommunicator.init(vanillaUrl, null);
		
		IVanillaContext ctx = new BaseVanillaContext(vanillaUrl, rootLogin, rootPassword);
		
		this.vanillaApi = new RemoteVanillaPlatform(ctx);
		this.manager = new RemoteVanillaApiManager(httpCommunicator);
		this.etlManager = new RemoteGatewayComponent(ctx);
		this.gedManager = new RemoteGedComponent(vanillaUrl, rootLogin, rootPassword);
		this.workflowService = new RemoteWorkflowComponent(ctx);
	}
	
	private User getUser() throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return vanillaApi.getVanillaSecurityManager().getUserByLogin(username);
        } else {
            // Handle the case when no user is authenticated
//            return null;
    		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
    		String rootLogin = conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
            return vanillaApi.getVanillaSecurityManager().getUserByLogin(rootLogin);
        }
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handle(HttpMessageNotReadableException e) {
		System.out.println("Returning HTTP 400 Bad Request");
		e.printStackTrace();
//	    logger.warn("Returning HTTP 400 Bad Request", e);
	}

	@PostMapping("/integration/generate")
	public APIResponse generateIntegration(@RequestBody GenerateIntegrationParameter parameter) {
		try {
			ContractIntegrationInformations integrationInfos = manager.generateIntegration(parameter.getRepositoryId(), parameter.getGroupId(), parameter.getIntegrationInfos(), parameter.isModifyMetadata(), parameter.isModifyIntegration());
			return new APIResponse(integrationInfos);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@GetMapping("/integration-by-limesurvey")
	public APIResponse getIntegrationByLimeSurvey(@RequestParam String limesurveyId) {
		try {
			ContractIntegrationInformations integrationInfos = manager.getIntegrationProcessByLimesurvey(limesurveyId);
			return new APIResponse(integrationInfos);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@GetMapping("/integration-by-contract")
	public APIResponse getIntegrationByContractId(@RequestParam int contractId) {
		try {
			ContractIntegrationInformations integrationInfos = manager.getIntegrationProcessByContract(contractId);
			return new APIResponse(integrationInfos);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@GetMapping("/vanillahubs")
	public APIResponse getVanillaHubs() {
		try {
			List<Workflow> workflows = manager.getVanillaHubs();
			return new APIResponse(workflows);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@PostMapping("/vanillahub/run")
	public APIResponse runVanillaHub(@RequestBody RunVanillaHubParameter parameter) {
		try {
			List<Parameter> parameters = convertParameters(parameter.getParameters());
			String uuid = manager.runVanillaHub(parameter.getWorkflowId(), parameters);
			return new APIResponse((Object) uuid);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@GetMapping("/vanillahub/progress")
	public APIResponse getVanillaHubProgress(@RequestParam int workflowId, @RequestParam String uuid) {
		try {
			WorkflowInstance instance = manager.getVanillaHubProgress(workflowId, uuid);
			return new APIResponse(instance);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@GetMapping("/item/informations")
	public APIResponse getItemInformations(@RequestParam int repositoryId, @RequestParam int groupId, @RequestParam int itemId) {
		try {
			User user = getUser();
			
			ItemRunInformations itemInformation = manager.getItemInformations(user, repositoryId, groupId, itemId);
			return new APIResponse(itemInformation);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@PostMapping("/etl/run")
	public APIResponse runETL(@RequestBody RunVanillaParameter parameter) {
		try {
			User user = getUser();
			
			int repositoryId = parameter.getRepositoryId();
			int groupId = parameter.getGroupId();
			int itemId = parameter.getItemId();
			
//			List<VanillaGroupParameter> parameters = convertParameters(parameter.getParameters());
			List<VanillaGroupParameter> parameters = parameter.getGroupParams();
			String uuid = manager.runETL(user, repositoryId, groupId, itemId, parameters);
			return new APIResponse((Object) uuid);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@GetMapping("/etl/progress")
	public APIResponse getETLProgress(@RequestParam String uuid) {
		try {
			int attempNumber = 0;
			
			GatewayRuntimeState state = null;
			
			while(attempNumber < 3 && state == null){
				try{
					state = etlManager.getRunState(new SimpleRunIdentifier(uuid)); 
				}catch(Exception ex){
					ex.printStackTrace();
				}
				if (state == null){
					Thread.sleep(500);
				}
				attempNumber++;
			}
			return new APIResponse(state);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@PostMapping("/workflow/run")
	public APIResponse runWorkflow(@RequestBody RunVanillaParameter parameter) {
		try {
			User user = getUser();
			
			int repositoryId = parameter.getRepositoryId();
			int groupId = parameter.getGroupId();
			int itemId = parameter.getItemId();
			
//			List<VanillaGroupParameter> parameters = convertParameters(parameter.getParameters());
			List<VanillaGroupParameter> parameters = parameter.getGroupParams();
			String uuid = manager.runWorkflow(user, repositoryId, groupId, itemId, parameters);
			return new APIResponse((Object) uuid);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@GetMapping("/workflow/progress")
	public APIResponse getWorkflowProgress(@RequestParam String uuid) {
		try {
			int attempNumber = 0;

			WorkflowInstanceState state = null;
			while (attempNumber < 3 && state == null) {
				try {
					state = workflowService.getInfos(new SimpleRunIdentifier(uuid), -1, -1);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if (state == null) {
					Thread.sleep(500);
				}
				attempNumber++;

			}
			return new APIResponse(state);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@PostMapping("/report/run")
	public void download(@RequestBody RunVanillaParameter parameter, HttpServletResponse response) throws Exception {
		try {
			
			String outputName = parameter.getOutputName() != null && !parameter.getOutputName().isEmpty() ? parameter.getOutputName() : String.valueOf(parameter.getItemId());
			String format = parameter.getFormat() != null && !parameter.getFormat().isEmpty() ? parameter.getFormat() : "html";
			
			// InputStream inputStream = new FileInputStream(new
			// File("C:/Users/svi/Desktop/FichePaie/FeuillePayeTese-Sept-2019-SVI.pdf"));
			// //load the file
			response.setHeader("Content-disposition", "filename=" + outputName + "." + format);
			if (format != null) {
				String mime = "";
				for (Formats f : Formats.values()) {
					if (f.getExtension().equals(format)) {
						mime = f.getMime();
						break;
					}
				}
				if (!mime.equals("")) {
					response.setContentType(mime);
				}
			}

			User user = getUser();
			
			int repositoryId = parameter.getRepositoryId();
			int groupId = parameter.getGroupId();
			int itemId = parameter.getItemId();
			
			InputStream inputStream = manager.runReport(user, repositoryId, groupId, itemId, outputName, format, parameter.getGroupParams(), parameter.getMails());
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@PostMapping("/kpi/generate")
	public APIResponse generateIntegration(@RequestBody GenerateKpiParameter parameter) {
		try {
			ContractIntegrationInformations integrationInfos = manager.generateKpi(parameter.getRepositoryId(), parameter.getGroupId(), parameter.getIntegrationInfos());
			return new APIResponse(integrationInfos);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@GetMapping("/kpi")
	public APIResponse getKpiByDatasetId(@RequestParam(required = false) String datasetId, @RequestParam(required = false) String organisation) {
		try {
			List<ContractIntegrationInformations> integrationInfos = new ArrayList<ContractIntegrationInformations>();
			if (datasetId != null) {
				integrationInfos = manager.getIntegrationKPIByDatasetId(datasetId);
			}
			else if (organisation != null) {
				integrationInfos = manager.getIntegrationByOrganisation(organisation, ContractType.KPI);
			}
			return new APIResponse(integrationInfos);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@PostMapping("/kpi/simple/generate")
	public APIResponse generateSimpleIntegration(@RequestBody GenerateSimpleKpiParameter parameter) {
		try {
			ContractIntegrationInformations integrationInfos = manager.generateIntegration(parameter.getRepositoryId(), parameter.getGroupId(), parameter.getIntegrationInfos(), parameter.isModifyMetadata(), parameter.isModifyIntegration());
			return new APIResponse(integrationInfos);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@GetMapping("/validation/schemas")
	public APIResponse getValidationSchemas() {
		try {
			List<String> schemas = manager.getValidationSchemas();
			return new APIResponse(schemas);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@PostMapping("/integration/remove")
	public APIResponse removeIntegration(@RequestBody GenerateIntegrationParameter parameter) {
		try {
			manager.deleteIntegration(parameter.getRepositoryId(), parameter.getGroupId(), parameter.getIntegrationInfos());
			return new APIResponse(null);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

//	@GetMapping("/testdate")
//	public APIResponse testDate() {
//		try {
//			KPIGenerationInformations infos = new KPIGenerationInformations();
//			infos.setSelectedDate(new Date());
//			return new APIResponse(infos);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new APIResponse("error", e.getMessage());
//		}
//	}

	@PostMapping("/validation/validate")
	public APIResponse generateSimpleIntegration(@RequestBody ValidationDataParameter parameter) {
		try {
			List<String> schemas = parameter.getSchemas() != null ? Arrays.asList(parameter.getSchemas()) : null;
			ValidationDataResult result = manager.validateData(parameter.getD4cUrl(), parameter.getD4cOrg(), parameter.getDatasetId(), parameter.getResourceId(), parameter.getContractId(), schemas);
			return new APIResponse(result);
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	@PostMapping("/integration/upload")
	public APIResponse handleFileUpload(@ModelAttribute UploadFile uploadFile) {
		try {
			if (uploadFile.getFile().isEmpty()) {
				throw new Exception("Failed to store empty file.");
			}
			
			if (uploadFile.getType() == 0) {
				String format = uploadFile.getFormat();

//				if (uploadFile.getDocumentId() == 0) {
//					List<Integer> groupIds = session.getMdmRemote().getSupplierSecurity();
//
//					GedDocument doc = gedManager.createDocumentThroughServlet(documentName, format, userId, groupIds, repositoryId, newVersion);
//					return new APIResponse(doc);
//				}
//				else {
					DocumentVersion version = gedManager.addVersionToDocumentThroughServlet(uploadFile.getDocumentId(), format, uploadFile.getFile().getInputStream());
					return new APIResponse(version);
//				}
			}
			else {
				return new APIResponse("error", "The upload type is not supported.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new APIResponse("error", e.getMessage());
		}
	}

	private List<Parameter> convertParameters(VanillaHubParameter[] parameters) {
		List<Parameter> hubParameters = new ArrayList<Parameter>();
		if (parameters != null) {
			for (VanillaHubParameter parameter : parameters) {
				Parameter hubParameter = new Parameter(parameter.getName());
				hubParameter.setId(parameter.getId());
				hubParameter.setValue(parameter.getValue());
				
				hubParameters.add(hubParameter);
			}
		}
		return hubParameters;
	}
	
	
}
