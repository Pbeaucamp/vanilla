package bpm.vanilla.platform.core.remote.impl.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.DataSourceImpact;
import bpm.vanilla.platform.core.repository.DatasProvider;
import bpm.vanilla.platform.core.repository.DirectoryItemDependance;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.services.IDataProviderService;

import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteDatasProvider implements IDataProviderService{
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
	public RemoteDatasProvider(HttpCommunicator httpCommunicator) {
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
	public void breakLink(int itemId, int datasProviderId) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId, datasProviderId), 
				IDataProviderService.ActionType.BREAK_LINK);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
		
	}
	@Override
	public int createDatasProvider(DatasProvider dp)throws Exception {
		
		XmlAction op = new XmlAction(createArguments(dp), 
			IDataProviderService.ActionType.CREATE_PROVIDER);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer)handleError(result);
	}
	@Override
	public void delete(DatasProvider dp) throws Exception {
		XmlAction op = new XmlAction(createArguments(dp), 
				IDataProviderService.ActionType.DELETE_PROVIDER);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
		
	}
	@Override
	public List<DatasProvider> getAll() throws Exception {
		XmlAction op = new XmlAction(createArguments(), 
				IDataProviderService.ActionType.LIST_PROVIDER);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)handleError(result);
		
	}
	@Override
	public DatasProvider getDatasProvider(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), 
				IDataProviderService.ActionType.FIND_PROVIDER);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (DatasProvider)handleError(result);
	}
	@Override
	public List<DatasProvider> getForItem(int itemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId), 
				IDataProviderService.ActionType.FIND_ITEM_PROVIDERS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)handleError(result);
	}
	@Override
	public String link(int datasProviderID, int itemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasProviderID, itemId), 
				IDataProviderService.ActionType.LINK);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String)handleError(result);

	}
	@Override
	public void update(DatasProvider element) throws Exception {
		XmlAction op = new XmlAction(createArguments(element), 
				IDataProviderService.ActionType.UPDATE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
		
	}
	
	
}
