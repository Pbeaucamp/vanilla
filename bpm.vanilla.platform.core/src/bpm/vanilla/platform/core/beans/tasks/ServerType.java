package bpm.vanilla.platform.core.beans.tasks;

import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.WorkflowService;

public enum ServerType {
	REPORTING, GATEWAY, GED, WORKFLOW;

	
	public String getTypeName(){
		switch(this){
		case REPORTING:
			return SERVER_TYPE_NAME[0];
		case GATEWAY:
			return SERVER_TYPE_NAME[1];
		case GED:
			return SERVER_TYPE_NAME[2];
		case WORKFLOW:
			return SERVER_TYPE_NAME[3];
		}
		return null;
	}
	
	public String getServletName(){
		switch(this){
		case REPORTING:
			return SERVER_SERVLET_NAME[0];
		case GATEWAY:
			return SERVER_SERVLET_NAME[1];
		case GED:
			return SERVER_SERVLET_NAME[2];
		case WORKFLOW:
			return SERVER_SERVLET_NAME[3];
		}
		return null;
	}
	private static final String[] SERVER_SERVLET_NAME = new String[]{
		ReportingComponent.REPORTING_SERVLET, GatewayComponent.GATEWAY_XTSREAM_SERVLET, "GedServlet", WorkflowService.SERVLET_RUNTIME
	};
	private static final String[] SERVER_TYPE_NAME = new String[]{
		"Reporting", "Gateway", "Document Indexer", "Workflow"
	};
	
	private static final int TYPE_REPORTING = 0;
	private static final int TYPE_GATEWAY = 1;
	private static final int TYPE_GED = 2;
	
	public static ServerType getByName(String name){
		if (name.equals(SERVER_TYPE_NAME[TYPE_REPORTING])){
			return REPORTING;
		}
		else if (name.equals(SERVER_TYPE_NAME[TYPE_GATEWAY])){
			return GATEWAY;
		}
		else if (name.equals(SERVER_TYPE_NAME[TYPE_GED])){
			return GED;
		}
		return null;
	}
}
