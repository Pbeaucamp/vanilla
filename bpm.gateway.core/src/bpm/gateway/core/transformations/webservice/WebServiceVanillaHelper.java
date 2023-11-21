package bpm.gateway.core.transformations.webservice;

import java.util.List;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaWebServiceComponent;
import bpm.vanilla.platform.core.beans.Variable;
import bpm.vanilla.platform.core.beans.service.ServiceOutputData;
import bpm.vanilla.platform.core.beans.service.ServiceTransformationDefinition;
import bpm.vanilla.platform.core.remote.impl.RemoteVanillaWebServiceComponent;

public class WebServiceVanillaHelper {

	private IRepositoryContext repContext;
	
	public WebServiceVanillaHelper(IRepositoryContext repContext){
		this.repContext = repContext;
	}
	
	private IVanillaWebServiceComponent createSock() throws Exception{
		if (repContext == null){
			throw new Exception("Cannot use Vanilla Web Service without a IRepositoryContext");
		}
		
		RemoteVanillaWebServiceComponent remote = new RemoteVanillaWebServiceComponent(repContext.getVanillaContext());
		return remote;
	}
	
	public StreamDescriptor getDescriptor(WebServiceVanillaInput transfo) throws Exception {
		IVanillaWebServiceComponent sock =  createSock();

		DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
		
		if(transfo.getWebServiceDefinitionId() > 0){
			ServiceTransformationDefinition serviceDefinition = sock.getWebServiceDefinition(transfo.getWebServiceDefinitionId());
			List<ServiceOutputData> outputs = serviceDefinition.getOutputs();
	
			if (outputs != null && !outputs.isEmpty()) {
	
				for(ServiceOutputData output : outputs){
					StreamElement se = new StreamElement();
					se.name = output.getName();
					se.className = Variable.JAVA_CLASSES[output.getType()];
					se.isNullable = true;
					se.originTransfo = transfo.getName();
					se.transfoName = transfo.getName();
					se.tableName = "";
					se.typeName = Variable.TYPE_NAMES[output.getType()];
					se.type = output.getType();
	
					desc.addColumn(se);
				}
			}
		}
		
		return desc;	
	}
	
	public List<ServiceTransformationDefinition> getMethodNames() throws Exception {
		IVanillaWebServiceComponent sock =  createSock();
		return sock.getWebServiceDefinitions();
	}
	
	public void saveServiceDefinition(ServiceTransformationDefinition serviceDefinition) throws Exception{
		IVanillaWebServiceComponent sock = createSock();
		sock.addWebServiceDefinition(serviceDefinition);
	}

	public void deleteService(ServiceTransformationDefinition definition) throws Exception {
		IVanillaWebServiceComponent sock = createSock();
		sock.deleteWebServiceDefinition(definition.getId());
	}

	public void updateService(ServiceTransformationDefinition definition) throws Exception {
		IVanillaWebServiceComponent sock = createSock();
		sock.updateWebServiceDefinition(definition);
	}
}
