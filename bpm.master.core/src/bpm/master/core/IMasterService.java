package bpm.master.core;

import java.util.List;

import bpm.document.management.core.model.User;
import bpm.document.management.core.xstream.IXmlActionType;
import bpm.master.core.model.InstanceIdentifier;
import bpm.master.core.model.InstanceLog;
import bpm.master.core.model.OzwilloRequest;

public interface IMasterService {

	public enum ActionType implements IXmlActionType {
		REGISTER, UNREGISTER, GET_INSTANCES, GET_INSTANCE_USERS, GET_OZWILLO_REQUESTS, LINK_REQUEST, GET_INSTANCE_LOG, GET_REQUEST_BY_INSTANCE
	}

	public static final String SERVLET = "/masterService";
	
	public void register(InstanceIdentifier identifier) throws Exception;
	
	public void unregister(InstanceIdentifier identifier) throws Exception;
	
	public List<InstanceIdentifier> getInstances() throws Exception;
	
	public List<User> getInstanceUsers(InstanceIdentifier identifier) throws Exception;
	
	public List<OzwilloRequest> getOzwilloRequests() throws Exception;
	
	public void linkOzwilloWithInstance(OzwilloRequest request, InstanceIdentifier instance) throws Exception;

	public List<InstanceLog> getInstanceLog(OzwilloRequest identifier) throws Exception;

	public OzwilloRequest getOzwilloRequestByInstance(InstanceIdentifier id) throws Exception;
}
