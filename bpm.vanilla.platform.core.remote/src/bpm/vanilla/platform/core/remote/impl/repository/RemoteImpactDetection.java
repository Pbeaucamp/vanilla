package bpm.vanilla.platform.core.remote.impl.repository;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.DataSourceImpact;
import bpm.vanilla.platform.core.repository.DirectoryItemDependance;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.services.IRepositoryImpactService;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteImpactDetection implements IRepositoryImpactService {
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static {
		xstream = new XStream();

	}

	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for(int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	public RemoteImpactDetection(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	private Object handleError(String responseMessage) throws Exception {
		if(responseMessage.isEmpty()) {
			return null;
		}
		Object o = xstream.fromXML(responseMessage);
		if(o != null && o instanceof VanillaException) {
			throw (VanillaException) o;
		}
		return o;
	}

	@Override
	public int add(DataSource d) throws Exception {
		XmlAction op = new XmlAction(createArguments(d), IRepositoryImpactService.ActionType.CREATE_DATASOURCE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer) handleError(result);
	}

	@Override
	public boolean del(DataSource d) throws Exception {
		XmlAction op = new XmlAction(createArguments(d), IRepositoryImpactService.ActionType.DELETE_DATASOURCE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean) handleError(result);

	}

	@Override
	public List<DataSource> getAllDatasources() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IRepositoryImpactService.ActionType.LIST_DATASOURCE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List) handleError(result);
	}

	@Override
	public DataSource getById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IRepositoryImpactService.ActionType.FIND_DATASOURCE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (DataSource) handleError(result);
	}

	@Override
	public List<DataSourceImpact> getForDataSourceId(int dataSourceId) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataSourceId), IRepositoryImpactService.ActionType.GET_DATASOURCE_IMPACTS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List) handleError(result);
	}

	@Override
	public void update(DataSource d) throws Exception {
		XmlAction op = new XmlAction(createArguments(d), IRepositoryImpactService.ActionType.UPDATE_DATASOURCE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);

	}

	@Override
	public List<DataSource> getDatasourcesByType(String extensionId) throws Exception {
		XmlAction op = new XmlAction(createArguments(extensionId), IRepositoryImpactService.ActionType.FIND_DATASOURCE_BY_TYPE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List) handleError(result);
	}

}
