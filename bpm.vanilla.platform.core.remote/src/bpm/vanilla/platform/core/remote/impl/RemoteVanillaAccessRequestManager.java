package bpm.vanilla.platform.core.remote.impl;

import java.util.List;

import bpm.vanilla.platform.core.IVanillaAccessRequestManager;
import bpm.vanilla.platform.core.beans.AccessRequest;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;


public class RemoteVanillaAccessRequestManager implements IVanillaAccessRequestManager {
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	
	static{
		xstream = new XStream();

	}
	
	public RemoteVanillaAccessRequestManager(HttpCommunicator httpCommunicator){
		this.httpCommunicator = httpCommunicator;
	}
	
	
	private XmlArgumentsHolder createArguments(Object...  arguments){
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		for(int i = 0; i < arguments.length; i++){
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	@Override
	public void addAccessRequest(AccessRequest request) throws Exception {
		XmlAction op = new XmlAction(createArguments(request), IVanillaAccessRequestManager.ActionType.ADD_REQUEST);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}
	
	@Override
	public void deleteAccessRequest(AccessRequest request) throws Exception {
		XmlAction op = new XmlAction(createArguments(request), IVanillaAccessRequestManager.ActionType.DELETE_REQUEST);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}
	
	@Override
	public void updateAccessRequest(List<AccessRequest> requests)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(requests), IVanillaAccessRequestManager.ActionType.UPDATE_REQUESTS);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRequest> listAdminAllAccessRequest() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaAccessRequestManager.ActionType.LIST_ADMIN_ALL_REQUESTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<AccessRequest>) xstream.fromXML(xml);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRequest> listAdminPendingAccessRequest() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaAccessRequestManager.ActionType.LIST_ADMIN_PENDING_REQUESTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<AccessRequest>) xstream.fromXML(xml);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRequest> listUserAllAccessRequest(int userId)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVanillaAccessRequestManager.ActionType.LIST_USER_ALL_REQUESTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<AccessRequest>) xstream.fromXML(xml);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRequest> listUserPendingAccessRequest(int userId)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVanillaAccessRequestManager.ActionType.LIST_USER_PENDING_REQUESTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<AccessRequest>) xstream.fromXML(xml);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRequest> listUserAllAccessDemands(int userId)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVanillaAccessRequestManager.ActionType.LIST_USER_ALL_DEMANDS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<AccessRequest>) xstream.fromXML(xml);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRequest> listUserPendingAccessDemands(int userId)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVanillaAccessRequestManager.ActionType.LIST_USER_PENDING_DEMANDS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<AccessRequest>) xstream.fromXML(xml);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void approveAccessRequest(AccessRequest request) throws Exception {
		XmlAction op = new XmlAction(createArguments(request), IVanillaAccessRequestManager.ActionType.APPROVE_REQUEST);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}
	
	@Override
	public void refuseAccessRequest(AccessRequest request) throws Exception {
		XmlAction op = new XmlAction(createArguments(request), IVanillaAccessRequestManager.ActionType.REFUSE_REQUEST);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);	
	}
}
