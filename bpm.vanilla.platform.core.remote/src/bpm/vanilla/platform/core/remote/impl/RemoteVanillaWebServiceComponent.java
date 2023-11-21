package bpm.vanilla.platform.core.remote.impl;

import java.util.List;

import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaWebServiceComponent;
import bpm.vanilla.platform.core.beans.service.ServiceTransformationDefinition;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteVanillaWebServiceComponent implements IVanillaWebServiceComponent {

	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	
	
	public RemoteVanillaWebServiceComponent(HttpCommunicator httpCommunicator){
		this.httpCommunicator = httpCommunicator;
	}
	
	public RemoteVanillaWebServiceComponent(IVanillaContext context){
		this.httpCommunicator = new HttpCommunicator();
		this.httpCommunicator.init(context.getVanillaUrl(), context.getLogin(), context.getPassword());
	}
	
	static{
		xstream = new XStream();
	}
	
	private XmlArgumentsHolder createArguments(Object...  arguments){
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		for(int i = 0; i < arguments.length; i++){
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	@Override
	public void addWebServiceDefinition(ServiceTransformationDefinition definition) throws Exception {
		XmlAction op = new XmlAction(createArguments(definition), IVanillaWebServiceComponent.ActionType.ADD_WEBSERVICE_DEFINITION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteWebServiceDefinition(ServiceTransformationDefinition definition) throws Exception {
		XmlAction op = new XmlAction(createArguments(definition), IVanillaWebServiceComponent.ActionType.DELETE_WEBSERVICE_DEFINITION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteWebServiceDefinition(int definitionId) throws Exception {
		XmlAction op = new XmlAction(createArguments(definitionId), IVanillaWebServiceComponent.ActionType.DELETE_WEBSERVICE_DEFINITION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public ServiceTransformationDefinition getWebServiceDefinition(int definitionId) throws Exception {
		XmlAction op = new XmlAction(createArguments(definitionId), IVanillaWebServiceComponent.ActionType.GET_WEBSERVICE_DEFINITION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (ServiceTransformationDefinition) xstream.fromXML(xml);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ServiceTransformationDefinition> getWebServiceDefinitions() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaWebServiceComponent.ActionType.GET_WEBSERVICE_ALLDEFINTIONS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<ServiceTransformationDefinition>) xstream.fromXML(xml);
	}

	@Override
	public void updateWebServiceDefinition(ServiceTransformationDefinition definition) throws Exception {
		XmlAction op = new XmlAction(createArguments(definition), IVanillaWebServiceComponent.ActionType.UPDATE_WEBSERVICE_DEFINITION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

}
