package bpm.vanilla.web.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jws.WebService;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaWebServiceComponent;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.service.ServiceConstants;
import bpm.vanilla.platform.core.beans.service.ServiceInputData;
import bpm.vanilla.platform.core.beans.service.ServiceOutputData;
import bpm.vanilla.platform.core.beans.service.ServiceTransformationDefinition;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.RemoteVanillaWebServiceComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteVanillaParameterComponent;

@WebService(endpointInterface = "bpm.vanilla.web.service.IVanillaWebService")
public class VanillaWebService implements IVanillaWebService {

	@Override
	public String loadData(int itemId, String user, String password, int groupId, int repId, String xml) {
		
		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		String serviceFilesPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GATEWAY_HOME_FOLDER) + "/serviceFiles";
		
		IVanillaContext vanillaContext = new BaseVanillaContext(vanillaUrl, user, password);
		IVanillaAPI api = new RemoteVanillaPlatform(vanillaContext);
		
		Group group = null;
		Repository repository = null;
		try {
			group = api.getVanillaSecurityManager().getGroupById(groupId);
			repository = api.getVanillaRepositoryManager().getRepositoryById(repId);
		
			//Get the BIG model back
			IRepositoryContext ctx = new BaseRepositoryContext(vanillaContext, group, repository);
			IRepositoryApi sock = new RemoteRepositoryApi(ctx);
			
			String model = sock.getRepositoryService().loadModel(sock.getRepositoryService().getDirectoryItem(itemId));
			
			//Find the serviceTransformation to use

			int serviceId = findServiceId(model);
			IVanillaWebServiceComponent serviceComponent = new RemoteVanillaWebServiceComponent(vanillaContext);
			ServiceTransformationDefinition serviceDefinition = serviceComponent.getWebServiceDefinition(serviceId);
			
			//parse the xml input
			List<HashMap<String, String>> inputs = parseInput(xml, serviceDefinition);
			
			//generate the xml which will ne used be the BIG
			String result = createXmlOutput(inputs, serviceDefinition);
			
			File file = new File(serviceFilesPath  + "/" + itemId + new Object().hashCode());
			FileWriter writer = new FileWriter(file);
			writer.write(result);
			writer.flush();
			writer.close();
			
			//Pass the parameter and call the BIG
			RemoteGatewayComponent bigRemote = new RemoteGatewayComponent(vanillaContext);
			RemoteVanillaParameterComponent parameterRemote = new RemoteVanillaParameterComponent(vanillaContext);
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaContext);

			User vanillaUser = vanillaApi.getVanillaSecurityManager().getUserByLogin(user);
			
			GatewayRuntimeConfiguration runtimeConfig = new GatewayRuntimeConfiguration(new ObjectIdentifier(repId, itemId), null, groupId);
			List<VanillaGroupParameter> parameters = parameterRemote.getParameters(runtimeConfig);
			for(VanillaGroupParameter param : parameters) {
				for(VanillaParameter p : param.getParameters() ) {
					if(p.getName().equals(ServiceConstants.PARAMETER_FILE)) {
						p.addSelectedValue(file.getPath());
					}
				}
			}
			runtimeConfig.setParameters(parameters);
			
			GatewayRuntimeState state = bigRemote.runGateway(runtimeConfig, vanillaUser);	
			if(state.getState() == ActivityState.FAILED) {
				throw new Exception("The Gateway execution failed.");
			}
			
			//generate the return if there is one
			if(serviceDefinition.getReturns() != null && !serviceDefinition.getReturns().isEmpty()) {
//				String serviceResult = createServiceResult(serviceDefinition);
//				
//				return serviceResult;
			}
			
			return "The Gateway execution ended without problems.";
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "An error occured while executing the treatement on data.";
		
	}

	private String createXmlOutput(List<HashMap<String, String>> inputs, ServiceTransformationDefinition serviceDefinition) throws Exception {
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(ServiceConstants.XML_ROOT);
		
		for(HashMap<String, String> row : inputs) {
			Element rowElement = root.addElement(ServiceConstants.XML_ROW);
			for(ServiceOutputData output : serviceDefinition.getOutputs()) {
			
				Pattern p = Pattern.compile("\\{\\$(\\w+)\\}");
				Matcher m = p.matcher(output.getValue().replaceAll(" ", "").trim());

				while(m.find()) {
				   String col = m.group(1);
				   
				   rowElement.addElement(output.getName()).setText(row.get(col));
				}
			}
			
		}
		
		Element xml = doc.getRootElement();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		
		OutputFormat form = OutputFormat.createPrettyPrint();
		form.setTrimText(false);
		XMLWriter writer = new XMLWriter(bos, form);
		writer.write(xml);
		writer.close();
		
		String result = bos.toString("UTF-8"); 
		
		return result;
	}

	@SuppressWarnings("unchecked")
	private List<HashMap<String, String>> parseInput(String xml, ServiceTransformationDefinition serviceDefinition) throws Exception {
		List<HashMap<String, String>> inputs = new ArrayList<HashMap<String, String>>();
		Document doc = DocumentHelper.parseText(xml);
		
		XPath xpath = new Dom4jXPath("//" + serviceDefinition.getXmlRow());
		List<Node> inputElements = xpath.selectNodes(doc);
		
		for(int i = 0 ; i < inputElements.size() ; i++) {
			Node node = inputElements.get(i);
			HashMap<String, String> input = new HashMap<String, String>();
			
			for(ServiceInputData field : serviceDefinition.getInputs()) {
//				XPath xpathField = new Dom4jXPath("//" + field.getName());
//				
//				String value = ((Node)xpathField.selectSingleNode(node)).getText();
				String value = node.selectSingleNode(field.getName()).getText();
				input.put(field.getName(), value);
			}
			
			inputs.add(input);
		}
		
		return inputs;
	}

	@SuppressWarnings("unchecked")
	private int findServiceId(String model) throws Exception {
		Document doc = DocumentHelper.parseText(model);
		
		XPath xpath = new Dom4jXPath("//webServiceDefinitionId");
		List<Node> definitionIds = xpath.selectNodes(doc);
		if(definitionIds != null && !definitionIds.isEmpty()) {
			return Integer.parseInt(definitionIds.get(0).getText());
		}
		
		throw new Exception("Cannot find a service definition for this gateway");
	}

}
