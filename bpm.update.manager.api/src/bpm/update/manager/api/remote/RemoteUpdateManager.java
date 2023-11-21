package bpm.update.manager.api.remote;

import org.apache.log4j.Logger;

import bpm.update.manager.api.IUpdateManager;
import bpm.update.manager.api.beans.UpdateInformations;
import bpm.update.manager.api.xstream.HttpCommunicator;
import bpm.update.manager.api.xstream.XmlAction;
import bpm.update.manager.api.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteUpdateManager implements IUpdateManager {

	private Logger logger = Logger.getLogger(RemoteUpdateManager.class);

	private HttpCommunicator httpCommunicator;
	private XStream xstream;
	
	private boolean isInited;
	
	public RemoteUpdateManager(String managerUrl) {
		if (managerUrl == null || managerUrl.isEmpty()) {
			logger.warn("Update properties are not correctly set in the application.properties file. Auto update are disabled.");
			this.isInited = false;
		}
		else {
			this.httpCommunicator = new HttpCommunicator();
			httpCommunicator.init(managerUrl);
			
			this.xstream = new XStream();
			this.isInited = true;
		}
	}
	
	private XmlArgumentsHolder createArguments(Object...  arguments){
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		for(int i = 0; i < arguments.length; i++){
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public UpdateInformations hasUpdate() throws Exception {
		logger.info("Check updates...");

		if (!isInited) {
			return null;
		}
		
		XmlAction op = new XmlAction(createArguments(), IUpdateManager.ActionType.HAS_UPDATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (UpdateInformations) xstream.fromXML(xml);
	}

	@Override
	public void updateApplication(boolean goBackPreviousApp, UpdateInformations appsInfos) throws Exception {
		XmlAction op = new XmlAction(createArguments(goBackPreviousApp, appsInfos), IUpdateManager.ActionType.UPDATE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public boolean restartServer() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IUpdateManager.ActionType.RESTART_SERVER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Boolean) xstream.fromXML(xml);
	}
}
