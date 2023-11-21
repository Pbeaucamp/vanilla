package bpm.forms.remote.services;

import java.util.List;

import com.thoughtworks.xstream.XStream;

import bpm.forms.core.communication.xml.XmlAction;
import bpm.forms.core.communication.xml.XmlArgumentsHolder;
import bpm.forms.core.communication.xml.XmlAction.ActionType;
import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IFormInstanceFieldState;
import bpm.forms.core.runtime.IInstanceService;
import bpm.forms.model.impl.Form;
import bpm.forms.model.impl.FormDefinition;
import bpm.forms.model.impl.FormDefinitionTableMapping;
import bpm.forms.model.impl.FormFieldMapping;
import bpm.forms.model.impl.FormInstance;
import bpm.forms.model.impl.FormInstanceFieldState;
import bpm.forms.model.impl.FormUIFd;
import bpm.forms.model.impl.FormUIProperty;
import bpm.forms.model.impl.InstanciationRule;
import bpm.forms.model.impl.TargetTable;
import bpm.forms.remote.intenal.HttpCommunicator;

public class RemoteInstanceService implements IInstanceService{
	private HttpCommunicator httpCommunicator;
	private XStream xstream;
	
	public RemoteInstanceService(){
		httpCommunicator = new HttpCommunicator("");
		init();
	}
	
	public void configure(Object config){
		setVanillaRuntimeUrl((String)config);
	}
	
	public void setVanillaRuntimeUrl(String vanillaRuntimeUrl){
		synchronized (httpCommunicator) {
			httpCommunicator.setUrl(vanillaRuntimeUrl);
		}
		
	}
	
	private void init(){
		xstream = new XStream();
	}

	@Override
	public void delete(IFormInstance instance) throws Exception{
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(instance);
		
		XmlAction op = new XmlAction(args, ActionType.INST_DELETE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));

		
	}

	@Override
	public void deleteFor(IFormDefinition formDef) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(formDef);
		
		XmlAction op = new XmlAction(args, ActionType.INST_DELETE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));

		
	}

	@Override
	public List<IFormInstanceFieldState> getFieldsState(long instanceId) throws Exception{
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(instanceId);
		
		XmlAction op = new XmlAction(args, ActionType.INST_FIELD_STATE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List)xstream.fromXML(xml);

	}

	@Override
	public List<IFormInstance> getFormsToSubmit(int groupId) throws Exception{
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(groupId);
		
		XmlAction op = new XmlAction(args, ActionType.INST_GETTOSUBMIT);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<IFormInstance> getFormsToValidate(int groupId) throws Exception{
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(groupId);
		
		XmlAction op = new XmlAction(args, ActionType.INST_GETTOVALIDATE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List)xstream.fromXML(xml);
	}

	@Override
	public IFormInstance getRunningInstance(long instanceId) throws Exception{
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(instanceId);
		
		XmlAction op = new XmlAction(args, ActionType.INST_RUNNING);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (IFormInstance)xstream.fromXML(xml);
	}

	@Override
	public List<IFormInstance> getRunningInstances(IForm form) throws Exception{
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(form);
		
		XmlAction op = new XmlAction(args, ActionType.INST_RUNNING);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List)xstream.fromXML(xml);

	}

	@Override
	public List<IFormInstance> getRunningInstances(	IFormDefinition formDefinition) throws Exception{
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(formDefinition);
		
		XmlAction op = new XmlAction(args, ActionType.INST_RUNNING);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List)xstream.fromXML(xml);

	}

	@Override
	public IFormInstance save(IFormInstance instance) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(instance);
		
		XmlAction op = new XmlAction(args, ActionType.INST_SAVE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (IFormInstance)xstream.fromXML(xml);

		
	}

	@Override
	public IFormInstanceFieldState save(IFormInstanceFieldState fieldState) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(fieldState);
		
		XmlAction op = new XmlAction(args, ActionType.INST_SAVE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (IFormInstanceFieldState)xstream.fromXML(xml);

		
	}

	@Override
	public void update(IFormInstanceFieldState fieldState) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(fieldState);
		
		XmlAction op = new XmlAction(args, ActionType.INST_UPDATE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));

		
	}

	@Override
	public void update(IFormInstance formInstance) throws Exception{
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(formInstance);
		
		XmlAction op = new XmlAction(args, ActionType.INST_UPDATE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));

		
	}
}
