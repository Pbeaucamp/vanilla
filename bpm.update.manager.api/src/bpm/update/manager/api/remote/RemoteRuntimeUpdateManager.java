package bpm.update.manager.api.remote;

import java.util.List;

import bpm.update.manager.api.IRuntimeUpdateManager;
import bpm.update.manager.api.xstream.HttpCommunicator;
import bpm.update.manager.api.xstream.XmlAction;
import bpm.update.manager.api.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteRuntimeUpdateManager implements IRuntimeUpdateManager {
	
	private HttpCommunicator httpCommunicator;
	private XStream xstream;
	
	public RemoteRuntimeUpdateManager(String vanillaRuntimeUrl, String login, String password) {
		this.httpCommunicator = new HttpCommunicator();
		httpCommunicator.init(vanillaRuntimeUrl, login, password);
		
		this.xstream = new XStream();
	}
	
	private XmlArgumentsHolder createArguments(Object...  arguments){
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		for(int i = 0; i < arguments.length; i++){
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public void undeploy(List<String> plugins) throws Exception {
		XmlAction op = new XmlAction(createArguments(plugins), IRuntimeUpdateManager.ActionType.UNDEPLOY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	public void shutdownOsgi() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IRuntimeUpdateManager.ActionType.SHUTDOWN);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
}
