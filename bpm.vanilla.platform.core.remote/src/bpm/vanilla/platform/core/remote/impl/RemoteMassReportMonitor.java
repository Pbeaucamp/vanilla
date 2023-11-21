package bpm.vanilla.platform.core.remote.impl;

import java.util.List;

import bpm.vanilla.platform.core.IMassReportMonitor;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.MassReportState;
import bpm.vanilla.platform.core.beans.WorkflowRunInstance;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteMassReportMonitor implements IMassReportMonitor{
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	
	
	public RemoteMassReportMonitor(HttpCommunicator httpCommunicator){
		this.httpCommunicator = httpCommunicator;
	}
	
	static{
		xstream = new XStream();
	}
	
	private XmlArgumentsHolder createArguments(Object...  arguments){
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		for(int i = 0; i < arguments.length; i++){
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	@Override
	public MassReportState getMassReportState(IObjectIdentifier workflowItemId,
			IObjectIdentifier reportIdentifier,
			String workflowInstanceUuid) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowItemId,reportIdentifier, workflowInstanceUuid), 
				IMassReportMonitor.ActionType.GET_STATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (MassReportState)xstream.fromXML(xml);
	}

	@Override
	public void setReportGenerated(IObjectIdentifier workflowItemId,
			IObjectIdentifier launchedReportId, String workflowInstanceUuid, String activityInstanceUuid)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowItemId, launchedReportId, workflowInstanceUuid, activityInstanceUuid), 
				IMassReportMonitor.ActionType.SET_GENERATED);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
	}

	@Override
	public void setReportGenerationAsked(IObjectIdentifier workflowItemId,
			IObjectIdentifier launchedReportId, String workflowInstanceUuid, String activityInstanceUuid)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowItemId, launchedReportId, workflowInstanceUuid, activityInstanceUuid), 
				IMassReportMonitor.ActionType.SET_ASKED);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public List<WorkflowRunInstance> getWorklowsUsingMassReporting()
			throws Exception {
		XmlAction op = new XmlAction(createArguments(), 
				IMassReportMonitor.ActionType.LIST);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (List)xstream.fromXML(xml);
	}

	@Override
	public void delete(List<WorkflowRunInstance> instances) throws Exception {
		XmlAction op = new XmlAction(createArguments(instances), 
				IMassReportMonitor.ActionType.DELETE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

}
