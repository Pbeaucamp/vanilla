package bpm.master.core.remote;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;

import bpm.document.management.core.model.User;
import bpm.master.core.IMasterService;
import bpm.master.core.model.InstanceIdentifier;
import bpm.master.core.model.InstanceLog;
import bpm.master.core.model.OzwilloRequest;

public class RemoteMasterService implements IMasterService {

	private HttpConnector httpConnector;

	public RemoteMasterService(HttpConnector httpConnector) {
		this.httpConnector = httpConnector;
	}
	
	private Serializable handleResponse(RemoteAction remoteAction) throws Exception {
		byte[] result = executeAction(remoteAction);
		
		if (result == null) {
			return null;
		}
		
		Serializable serializable = SerializationUtils.deserialize(result);
		if (serializable instanceof Exception) {
			throw (Exception) serializable;
		}
		return serializable;
	}
	
	private byte[] executeAction(RemoteAction remoteAction) throws Exception {
		if (httpConnector == null) {
			throw new Exception("Remote is not initialized.");
		}
		return httpConnector.executeAction(remoteAction);
	}
	
	@Override
	public void register(InstanceIdentifier identifier) throws Exception {
		RemoteAction remoteAction = new RemoteAction(ActionType.REGISTER, identifier);
		handleResponse(remoteAction);
	}

	@Override
	public void unregister(InstanceIdentifier identifier) throws Exception {
		RemoteAction remoteAction = new RemoteAction(ActionType.UNREGISTER, identifier);
		handleResponse(remoteAction);
	}

	@Override
	public List<InstanceIdentifier> getInstances() throws Exception {
		RemoteAction remoteAction = new RemoteAction(ActionType.GET_INSTANCES);
		return (List<InstanceIdentifier>)handleResponse(remoteAction);
	}

	@Override
	public List<User> getInstanceUsers(InstanceIdentifier identifier) throws Exception {
		RemoteAction remoteAction = new RemoteAction(ActionType.GET_INSTANCE_USERS, identifier);
		return (List<User>)handleResponse(remoteAction);
	}

	@Override
	public List<OzwilloRequest> getOzwilloRequests() throws Exception {
		RemoteAction remoteAction = new RemoteAction(ActionType.GET_OZWILLO_REQUESTS);
		return (List<OzwilloRequest>)handleResponse(remoteAction);
	}

	@Override
	public void linkOzwilloWithInstance(OzwilloRequest request, InstanceIdentifier instance) throws Exception {
		RemoteAction remoteAction = new RemoteAction(ActionType.LINK_REQUEST, request, instance);
		handleResponse(remoteAction);
	}

	@Override
	public List<InstanceLog> getInstanceLog(OzwilloRequest identifier) throws Exception {
		RemoteAction remoteAction = new RemoteAction(ActionType.GET_INSTANCE_LOG, identifier);
		return (List<InstanceLog>)handleResponse(remoteAction);
	}

	@Override
	public OzwilloRequest getOzwilloRequestByInstance(InstanceIdentifier id) throws Exception {
		RemoteAction remoteAction = new RemoteAction(ActionType.GET_REQUEST_BY_INSTANCE, id);
		return (OzwilloRequest)handleResponse(remoteAction);
	}

}
