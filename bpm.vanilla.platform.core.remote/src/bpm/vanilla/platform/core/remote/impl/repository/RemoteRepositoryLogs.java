package bpm.vanilla.platform.core.remote.impl.repository;

import java.util.Collection;
import java.util.List;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.RepositoryLog;
import bpm.vanilla.platform.core.repository.services.IRepositoryLogService;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteRepositoryLogs implements IRepositoryLogService{
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static{
		xstream = new XStream();

	}
	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
	public RemoteRepositoryLogs(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}
	private Object handleError(String responseMessage) throws Exception{
		if (responseMessage.isEmpty()){
			return null;
		}
		Object o = xstream.fromXML(responseMessage);
		if (o != null && o instanceof VanillaException){
			throw (VanillaException)o;
		}
		return o;
	}
	@Override
	public int addLog(RepositoryLog d) throws Exception {
		XmlAction op = new XmlAction(createArguments(d), 
				IRepositoryLogService.ActionType.ADD_LOG);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer)handleError(result);
		
	}
	@Override
	public void delReportModel(RepositoryLog d) throws Exception {
		XmlAction op = new XmlAction(createArguments(d), 
				IRepositoryLogService.ActionType.DEL_LOG);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
		
	}
	@Override
	public RepositoryLog getById(int logId) throws Exception {
		XmlAction op = new XmlAction(createArguments(logId), 
				IRepositoryLogService.ActionType.FIND_LOG);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (RepositoryLog)handleError(result);
	}
	@Override
	public List<RepositoryLog> getLogs() throws Exception {
		XmlAction op = new XmlAction(createArguments(), 
				IRepositoryLogService.ActionType.LIST_LOG);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)handleError(result);
	}
	@Override
	public List<RepositoryLog> getLogsFor(RepositoryItem i) throws Exception {
		XmlAction op = new XmlAction(createArguments(i), 
				IRepositoryLogService.ActionType.LIST_LOG_FOR_ITEM);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)handleError(result);
	}

	
}
