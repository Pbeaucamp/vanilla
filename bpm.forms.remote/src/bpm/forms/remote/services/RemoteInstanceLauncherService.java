package bpm.forms.remote.services;

import java.util.List;

import bpm.forms.core.communication.xml.XmlAction;
import bpm.forms.core.communication.xml.XmlArgumentsHolder;
import bpm.forms.core.communication.xml.XmlAction.ActionType;
import bpm.forms.core.design.IForm;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IInstanceLauncher;
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

import com.thoughtworks.xstream.XStream;

public class RemoteInstanceLauncherService implements IInstanceLauncher{

	private HttpCommunicator httpCommunicator;
	private XStream xstream;
	
	public RemoteInstanceLauncherService(){
		
		init();
	}
	/**
	 * config : String vanillaRuntimeUrl
	 */
	public void configure(Object config){
		httpCommunicator = new HttpCommunicator((String)config);	
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
	public List<IFormInstance> launchForm(IForm form, int groupId) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(form);
		args.addArgument(groupId);
		
		XmlAction op = new XmlAction(args, ActionType.LAU_INSTANCIATE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));

		return (List)xstream.fromXML(xml);
	}
}
