package bpm.vanilla.platform.core.wrapper.servlets.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.forms.Form;
import bpm.vanilla.platform.core.components.forms.IForm;
import bpm.vanilla.platform.core.remote.internal.ComponentComunicator;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

/**
 * This is not meant to be used by any client. Its purpose is the call
 * directly(without passing by the VanillaDispatcher a WorkflowComponent
 * and list all the Current Manual Tasks.
 * 
 *  
 * @author ludo
 *
 */
public class WorkflowSystemRemote {

	private ComponentComunicator httpCommunicator;
	private XStream xstream;
	private String url;
	
	public WorkflowSystemRemote(String workflowComponentUrl, String login, String password){
		this.httpCommunicator = new ComponentComunicator(VanillaComponentType.COMPONENT_WORKFLOW);
		this.httpCommunicator.init(workflowComponentUrl, login, password);
		this.url = workflowComponentUrl;
		while(this.url.endsWith("/")){
			this.url = this.url.substring(0, this.url.length() - 1);
		}
		init();
	}
	
	private void init() {
		xstream = new XStream();
	}
	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	/**
	 * call the Servlet WorkflowService.LIST_FORMS
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public List<IForm> listHTMLForms(Integer groupId) throws Exception{
		XmlAction op = new XmlAction(createArguments(groupId), WorkflowService.ActionType.LIST_FORMS);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		xstream.toXML(op, os);
		
		
		String fullUrl = url + WorkflowService.SERVLET_RUNTIME;
		
		InputStream xml = httpCommunicator.sendMessage(
				fullUrl, 
				VanillaComponentType.COMPONENT_WORKFLOW, 
				WorkflowService.LIST_FORMS,
				new ByteArrayInputStream(os.toByteArray())
				);
		
		List<IForm> l = (List)xstream.fromXML(xml);
		return l;

	}
}
