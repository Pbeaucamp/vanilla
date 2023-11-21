package bpm.vanilla.platform.core.remote.impl.repository;

import java.util.List;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.services.IWatchListService;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteWatchList implements IWatchListService{
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
	public RemoteWatchList(HttpCommunicator httpCommunicator) {
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
	public void addToWatchList(RepositoryItem it)throws Exception {
		XmlAction op = new XmlAction(createArguments(it), 
				IWatchListService.ActionType.ADD_TO_WATCHLIST);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
		
	}
	@Override
	public List<RepositoryItem> getLastConsulted() throws Exception {
		XmlAction op = new XmlAction(createArguments(), 
				IWatchListService.ActionType.GET_LAST_CONSULTED);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)handleError(result);
	}
	@Override
	public List<RepositoryItem> getWatchList() throws Exception {
		XmlAction op = new XmlAction(createArguments(), 
				IWatchListService.ActionType.GET_WATCH_LIST);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)handleError(result);
	}
	@Override
	public void removeFromWatchList(RepositoryItem element) throws Exception {
		XmlAction op = new XmlAction(createArguments(element), 
				IWatchListService.ActionType.REMOVE_FROM_WATCHLIST);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
		
	}
}
