package bpm.forms.remote.services;

import java.util.ArrayList;
import java.util.List;

import bpm.forms.core.communication.xml.XmlAction;
import bpm.forms.core.communication.xml.XmlArgumentsHolder;
import bpm.forms.core.communication.xml.XmlAction.ActionType;
import bpm.forms.core.design.IDefinitionService;
import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.ITargetTable;
import bpm.forms.model.impl.Form;
import bpm.forms.model.impl.FormDefinition;
import bpm.forms.model.impl.FormDefinitionTableMapping;
import bpm.forms.model.impl.FormFieldMapping;
import bpm.forms.model.impl.FormUIFd;
import bpm.forms.model.impl.FormUIProperty;
import bpm.forms.model.impl.InstanciationRule;
import bpm.forms.model.impl.TargetTable;
import bpm.forms.remote.intenal.HttpCommunicator;

import com.thoughtworks.xstream.XStream;

public class RemoteDefinitionService implements IDefinitionService{

	
	private HttpCommunicator httpCommunicator;
	private XStream xstream;
	
	public RemoteDefinitionService(){
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
	public void delete(IFormDefinition formdef) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(formdef);
		
		XmlAction op = new XmlAction(args, ActionType.DEF_DELETE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
		
		
	}

	@Override
	public void delete(IForm form) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(form);
		
		XmlAction op = new XmlAction(args, ActionType.DEF_DELETE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));

		
	}

	@Override
	public IFormDefinition getActiveFormDefinition(IForm form) {
		
		
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(form);
		
		XmlAction op = new XmlAction(args, ActionType.DEF_ACTIVE_FORM);
		
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IFormDefinition)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public List getColumnsForTargetTable(Long targetTableId) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(targetTableId);
		
		XmlAction op = new XmlAction(args, ActionType.DEF_COLUMNS_FOR_TT);
		
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList();
		}
	}

	@Override
	public IForm getForm(long formId) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(formId);
		
		XmlAction op = new XmlAction(args, ActionType.DEF_FORMS);
		
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IForm)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

	@Override
	public IFormDefinition getFormDefinition(long id) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(id);
		
		XmlAction op = new XmlAction(args, ActionType.DEF_FORM_DEF);
		
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IFormDefinition)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

	@Override
	public List<IFormDefinition> getFormDefinitionVersions(long formId) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(formId);
		
		XmlAction op = new XmlAction(args, ActionType.DEF_FORM_VERS);
		
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return null;
	}

	@Override
	public List<IForm> getForms() {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		
		XmlAction op = new XmlAction(args, ActionType.DEF_FORMS);
		
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

	@Override
	public ITargetTable getTargetTable(Long targetTableId) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(targetTableId);
		XmlAction op = new XmlAction(args, ActionType.DEF_TARGET_TABLE);
		
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (ITargetTable)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

	@Override
	public List<ITargetTable> getTargetTables() {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		
		XmlAction op = new XmlAction(args, ActionType.DEF_TARGET_TABLE);
		
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IForm saveForm(IForm form) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(form);
		XmlAction op = new XmlAction(args, ActionType.DEF_SAVE);
		
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (IForm)xstream.fromXML(xml);
		
		
	}

	@Override
	public IFormDefinition saveFormDefinition(IFormDefinition form) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(form);
		
		XmlAction op = new XmlAction(args, ActionType.DEF_SAVE);
		
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (IFormDefinition)xstream.fromXML(xml);
		
	}

	@Override
	public ITargetTable saveTargetTable(ITargetTable table) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(table);
		
		XmlAction op = new XmlAction(args, ActionType.DEF_SAVE);
		
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ITargetTable)xstream.fromXML(xml);
		
	}

	@Override
	public void update(IFormDefinition form) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(form);
		
		XmlAction op = new XmlAction(args, ActionType.DEF_UPDATE);
		
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
	}

	@Override
	public void update(IForm form) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(form);
		
		XmlAction op = new XmlAction(args, ActionType.DEF_UPDATE);
		
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));

		
	}

}
