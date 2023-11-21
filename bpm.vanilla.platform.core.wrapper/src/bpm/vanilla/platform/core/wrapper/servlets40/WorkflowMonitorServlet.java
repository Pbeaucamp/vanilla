package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState.StepInfos;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunIdentifier;
import bpm.vanilla.platform.core.utils.IOWriter;

/**
 * this servlet provide a simple HTML view that show a running gateway its content
 * 
 * It must have a Parameter Name runIdentifier to be able to retrieve it.
 * @author ludo
 *
 */
public class WorkflowMonitorServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		
		String workflowId = req.getParameter(WorkflowService.P_WORKFLOW_RUN_IDENTIFIER);
		Boolean includeTitle = true;
		
		if (req.getParameter(WorkflowService.P_WORKFLOW_MONITOR_INCLUDE_TITLE) != null){
			try{
				includeTitle = Boolean.parseBoolean(req.getParameter(WorkflowService.P_WORKFLOW_MONITOR_INCLUDE_TITLE));
			}catch(Exception ex){
				includeTitle = true;
			}
		}
		
		
		String result = null;
		try{
			boolean misArgs = false;
			if (workflowId == null){
				misArgs = true;
			}
			if (includeTitle == null || includeTitle){
				result = generateHTML(workflowId, true, misArgs);
			}
			else{
				result = generateHTML(workflowId, includeTitle, misArgs);
			}
			
			resp.setContentType("text/html");
			
			ByteArrayInputStream bis = new ByteArrayInputStream(result.getBytes());
			IOWriter.write(bis, resp.getOutputStream(), true, true);
		}catch(Exception ex){
			Logger.getLogger(getClass()).error("Unable to generate an HTML response - " + ex.getMessage(), ex);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to generate an HTML response - " + ex.getMessage());
		}

	}
	
	private String getHTML(WorkflowInstanceState state){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuilder buf = new StringBuilder();
		
		if (state == null){
			buf.append("Could not find this execution of Gateway.");
		}
		else{
			buf.append("<table border=\"1\" id=\"round_corners_table\">\n");
			buf.append("<caption class=\"component-title\">Process State</caption>\n");
			buf.append("<tr id=\"top-row\"><td id=\"tl\"></td><td class=\"empty\" colspan=\"5\"></td><td id=\"tr\"></td></tr>\n");
			buf.append("<tr>\n");
			buf.append("<td class=\"empty\" ></td>\n");
			buf.append("<th class=\"st\" >Step name</th>\n");
			buf.append("<th class=\"st\" >Type</th>\n");
			buf.append("<th class=\"st\" >State</th>\n");
			buf.append("<th class=\"st\" >Start</th>\n");
			buf.append("<th class=\"st\" >Stop</th>\n");
			buf.append("<td class=\"empty\"></td>\n");
			buf.append("</tr>\n");
			
			for(StepInfos s : state.getStepInfos()){
				
				String stateColor = "#ffffff";
				
				switch(s.getState()){
				case ENDED:
					stateColor = "#ACFA58";
					break;
				case STARTED:
					stateColor = "#c3daf9";
					break;
				case SUSPENDED:
					stateColor = "#ff7d00";
					break;
				case FAILED:
					stateColor = "#ff0000";
					break;
				}
				
				
				
				buf.append("<tr >\n");
				buf.append("<td class=\"empty\" ></td>\n");
				
				if (s.getManualStepUrl() == null){
					buf.append("<td bgcolor=\"" + stateColor + "\">" + s.getStepName() + "</td>\n");
				}
				else{
					buf.append("<td bgcolor=\"" + stateColor + "\"><a href=\"" + s.getManualStepUrl() + "\" target=\"_blank\">" + s.getStepName() + "</a></td>\n");
				}
				
				
				buf.append("<td bgcolor=\"" + stateColor + "\">" + s.getNature() + "</td>\n");
				buf.append("<td bgcolor=\"" + stateColor + "\">" + s.getState() + "</td>\n");
				if (s.getStartDate() != null){
					buf.append("<td bgcolor=\"" + stateColor + "\">" + sdf.format(s.getStartDate()) + "</td>\n");
				}
				else{
					buf.append("<td bgcolor=\"" + stateColor + "\"></td>\n");
				}
				if (s.getStopedDate() != null){
					buf.append("<td bgcolor=\"" + stateColor + "\">" + sdf.format(s.getStopedDate()) + "</td>\n");
				}
				else{
					buf.append("<td bgcolor=\"" + stateColor + "\"></td>\n");
				}
				
				buf.append("<td class=\"empty\" ></td>\n");
				buf.append("</tr>\n");
			}
			buf.append("<tr>\n");
			buf.append("<td class=\"empty\"></td>\n");
			
			
			String stateColor = "#c3daf9";
			switch(state.getState()){
			case ENDED:
				stateColor = "#ACFA58";
				break;
			case STARTED:
				stateColor = "#c3daf9";
				break;
			case SUSPENDED:
				stateColor = "#ff7d00";
				break;
			case FAILED:
				stateColor = "#ff0000";
				break;
			}
			buf.append("<td bgcolor=\""+ stateColor + "\" >Transformation State</td>\n");

			buf.append("<td colspan=\"4\" bgcolor=\""+ stateColor + "\">" + state.getState() + "</td>");
			buf.append("<td class=\"empty\"></td>\n");
			buf.append("</tr>");
		}
		buf.append(" <tr id=\"bottom-row\"><td id=\"bl\"></td><td class=\"empty\" colspan=\"5\"></td><td id=\"br\"></td></tr>");
		buf.append("</table>");
		
		return buf.toString();
	}
	/**
	 * 
	 * @param identifier
	 * @param includeTitle : if true the VanillaLog will be present
	 * @return
	 * @throws Exception
	 */
	private String generateHTML(String identifier, boolean includeTitle, boolean missingArgs) throws Exception{
		
		WorkflowInstanceState state = null;
		Throwable stateException = null;
		try{
			state = getState(identifier);;
		}catch(Throwable ex){
			stateException = ex;
			Logger.getLogger(getClass()).error("Could not find WorkflowProcess Instance state " + identifier + " - " + ex.getMessage(), ex);
		}
		StringBuffer buf = new StringBuffer();
		buf.append("<html>\n");
		buf.append("    <head>\n");
		if (state != null && state.getState() != ActivityState.ENDED){
			buf.append("        <META HTTP-EQUIV=\"Refresh\" CONTENT=\"1\">\n");
		}
		
		buf.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"../images/vanillaExternalCss.css\"/>\n");
		
		
		buf.append("         <style>\n");
		buf.append("             #round_corners_table { color:black;clear:both; border-color:black; border: 2px; border-collapse: collapse;}\n");
		buf.append("             #top-row {height: 20px; border: none; }\n");
		buf.append("             .empty {border: none; background-color: #E0E0F8; }\n");
		
		buf.append("             .st {background-color: #E0E0F8; }\n");
//		buf.append("             #logo {background-image: url(../images/Vanilla_Logo.gif); }\n");
		buf.append("             #bottom-row {height: 20px; border: none; }\n");
		buf.append("             #tl { border: none; width: 18px; background-image: url(../images/corner_tl.png); }\n");
		buf.append("             #tr { border: none;  width: 18px; background-image: url(../images/corner_tr.png); }\n");
		buf.append("             #bl {  border: none; background-image: url(../images/corner_bl.png); }\n");
		buf.append("             #br {  border: none; background-image: url(../images/corner_br.png); }\n");
		buf.append("         </style>\n");
		buf.append("    </head>\n");
		buf.append("    <body>\n");

		
		
		if (includeTitle){
			buf.append("<div class=\"centre\">\n");
			//buf.append("    <div style=\"float:left; margin-top=auto;\"><img src=\"../images/Vanilla_Logo.gif\" /></div>\n");
			buf.append("    <div class=\"entete\"><p>Workflow Monitor Console</p></div>\n");
		}
		
		
		buf.append("<br>\n");
		
		if (missingArgs){
			buf.append("<p>\n");
			buf.append("Missing Argument " + WorkflowService.P_WORKFLOW_RUN_IDENTIFIER);
			buf.append("</p>\n");
		}
		else{
			if (state != null){
				buf.append(getHTML(state));
			}
			else{
				StringBuilder b = new StringBuilder();
				b.append("<p class=\"error\">\n");
				Throwable t = stateException;
				b.append(stateException.getMessage() + "<br>");
				while( (t = t.getCause()) != null){
					b.append(t.getMessage() + "<br>");
				}
				b.append("</p>\n");
				buf.append(b.toString());
			}
		}
		
		
		
		
		
		if (includeTitle){
			buf.append("</div>\n");
		}
		
		
		
		buf.append("    </body>\n");
		buf.append("</html>\n");
		
		return buf.toString();
	}
	
	
	private WorkflowInstanceState getState(String identifier) throws Exception{
		
		int attempNumber = 0;
		
		RemoteWorkflowComponent remote = new RemoteWorkflowComponent(
				ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		
		WorkflowInstanceState state = null;
		
		while(attempNumber < 3 && state == null){
			try{
				state = remote.getInfos(new SimpleRunIdentifier(identifier), -1, -1); 
			}catch(Exception ex){
				ex.printStackTrace();
				Logger.getLogger(getClass()).warn("error when looking for WorkflowState " + ex.getMessage());
			}
			if (state == null){
				Thread.sleep(500);
			}
			attempNumber++;
			
		}
		return state;
	
	}
}
