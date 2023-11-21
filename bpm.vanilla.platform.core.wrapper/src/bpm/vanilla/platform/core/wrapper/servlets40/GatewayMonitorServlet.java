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
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState.StepInfos;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunIdentifier;
import bpm.vanilla.platform.core.utils.IOWriter;

/**
 * this servlet provide a simple HTML view that show a running gateway its content
 * 
 * It must have a Parameter Name runIdentifier to be able to retrieve it.
 * @author ludo
 *
 */
public class GatewayMonitorServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		
		String runIdentifier = req.getParameter(GatewayComponent.P_GATEWAY_RUN_IDENTIFIER);
		Boolean includeTitle = null;
		
		if (req.getParameter(GatewayComponent.P_GATEWAY_MONITOR_INCLUDE_TITLE) != null){
			try{
				includeTitle = Boolean.parseBoolean(req.getParameter(GatewayComponent.P_GATEWAY_MONITOR_INCLUDE_TITLE));
			}catch(Exception ex){
				includeTitle = true;
			}
		}
		
		
		String result = null;
		try{
			if (runIdentifier == null){
				result = generateHTMLMissingArguments();
			}
			else{
				if (includeTitle == null){
					result = generateHTML(runIdentifier, true);
				}
				else{
					result = generateHTML(runIdentifier, includeTitle);
				}
				
			}
			
			resp.setContentType("text/html");
			
			ByteArrayInputStream bis = new ByteArrayInputStream(result.getBytes());
			IOWriter.write(bis, resp.getOutputStream(), true, true);
		}catch(Exception ex){
			Logger.getLogger(getClass()).error("Unable to generate an HTML response - " + ex.getMessage(), ex);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to generate an HTML response - " + ex.getMessage());
		}
		
		
		
	}
	private String generateHTMLMissingArguments() {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>\n");
		buf.append("    <head>\n");
		buf.append("    </head>\n");
		buf.append("    <body>\n");
		buf.append("    Bad Url, the argument " + GatewayComponent.P_GATEWAY_RUN_IDENTIFIER+ " is missing.<br>");
		buf.append("    </body>\n");
		buf.append("</html>\n");
		return buf.toString();
		
	}
	/**
	 * 
	 * @param identifier
	 * @param includeTitle : if true the VanillaLog will be present
	 * @return
	 * @throws Exception
	 */
	private String generateHTML(String identifier, boolean includeTitle) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		GatewayRuntimeState state = getState(identifier);
		StringBuffer buf = new StringBuffer();
		buf.append("<html>\n");
		buf.append("    <head>\n");
		if (state != null && state.getState() != ActivityState.ENDED && state.getState() != ActivityState.FAILED){
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
			buf.append("    <div class=\"entete\"><p>BiGateway Monitor Console</p></div>\n");
		}
		
		
		buf.append("<br>\n");
		
		if (state == null){
			buf.append("Could not find this execution of Gateway.");
		}
		else{
			buf.append("<table border=\"1\" id=\"round_corners_table\">\n");
			buf.append("<caption class=\"component-title\">Transformation State</caption>\n");
			buf.append("<tr id=\"top-row\"><td id=\"tl\"></td><td class=\"empty\" colspan=\"8\"></td><td id=\"tr\"></td></tr>\n");
			buf.append("<tr>\n");
			buf.append("<td class=\"empty\" ></td>\n");
			buf.append("<th class=\"st\" >Step name</th>\n");
			buf.append("<th class=\"st\" >Step Duration</th>\n");
			buf.append("<th class=\"st\" >Buffered Rows</th>\n");
			buf.append("<th class=\"st\" >Processed Rows</th>\n");
			buf.append("<th class=\"st\" >Readed Rows</th>\n");
			buf.append("<th class=\"st\" >State</th>\n");
			buf.append("<th class=\"st\" >Start time</th>\n");
			buf.append("<th class=\"st\" >Stop time</th>\n");
			buf.append("<td class=\"empty\"></td>\n");
			buf.append("</tr>\n");
			
			boolean hasError = false;
			boolean hasWarning = false;
			
			for(StepInfos stepInfo : state.getStepsInfo()){
				
				String stateColor = "#ffffff";
				if (stepInfo.getErrorNumber() > 0){
					stateColor = "#ff0000";
					hasError = true;
				}
				else if (stepInfo.getWarningNumber() > 0){
					stateColor = "#ff7d00";
					hasWarning = true;
				}
				else{
					if (stepInfo.getStopTime() == null){
						stateColor = "#c3daf9";
					}
					else{
						stateColor = "#ACFA58";
					}
					
						
				}
				
				buf.append("<tr >\n");
				buf.append("<td class=\"empty\" ></td>\n");
				buf.append("<td bgcolor=\"" + stateColor + "\">" + stepInfo.getStepName() + "</td>\n");
				buf.append("<td bgcolor=\"" + stateColor + "\">" + stepInfo.getDuration() + "</td>\n");
				buf.append("<td bgcolor=\"" + stateColor + "\">" + stepInfo.getBufferedRows() + "</td>\n");
				buf.append("<td bgcolor=\"" + stateColor + "\">" + stepInfo.getProcessedRows() + "</td>\n");
				buf.append("<td bgcolor=\"" + stateColor + "\">" + stepInfo.getReadRows() + "</td>\n");
				buf.append("<td bgcolor=\"" + stateColor + "\">" + stepInfo.getState() + "</td>\n");
				
				if (stepInfo.getStartTime() != null){
					buf.append("<td bgcolor=\"" + stateColor + "\">" + sdf.format(stepInfo.getStartTime()) + "</td>\n");
				}
				else{
					buf.append("<td bgcolor=\"" + stateColor + "\"></td>\n");
				}
				
				
				if (stepInfo.getStopTime() != null){
					buf.append("<td bgcolor=\"" + stateColor + "\">" + sdf.format(stepInfo.getStopTime()) + "</td>\n");
				}
				else{
					buf.append("<td bgcolor=\"" + stateColor + "\">Running</td>\n");
				}
				buf.append("<td class=\"empty\" ></td>\n");
				buf.append("</tr>\n");
			}
			buf.append("<tr>\n");
			buf.append("<td class=\"empty\"></td>\n");
			
			
			String stateColor = "#ffffff";
			ActivityState st = state.getState();
			if (st != null){
				switch(st){
				case ENDED:
					if (hasError){
						stateColor = "#ff0000";
					}
					else if (hasWarning){
						stateColor = "#ff7d00";
					}
					else{
						stateColor = "#ACFA58";	
					}
					
					break;
				case RUNNING:
					stateColor = "#c3daf9";
				case WAITING:
					stateColor = "#c3daf9";
				}
			}
			
			buf.append("<td bgcolor=\""+ stateColor + "\" >Transformation State</td>\n");

			buf.append("<td colspan=\"7\" bgcolor=\""+ stateColor + "\">" + state.getState() + "</td>");
			buf.append("<td class=\"empty\"></td>\n");
			buf.append("</tr>");
		}
		buf.append(" <tr id=\"bottom-row\"><td id=\"bl\"></td><td class=\"empty\" colspan=\"8\"></td><td id=\"br\"></td></tr>");
		buf.append("</table>");
		
		buf.append("</div>\n");
		
		
		buf.append("    </body>\n");
		buf.append("</html>\n");
		
		return buf.toString();
	}
	
	
	private GatewayRuntimeState getState(String runIdentifier) throws Exception{
		
		int attempNumber = 0;
		
		RemoteGatewayComponent remote = new RemoteGatewayComponent(
				ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		
		GatewayRuntimeState state = null;
		
		while(attempNumber < 3 && state == null){
			try{
				state = remote.getRunState(new SimpleRunIdentifier(runIdentifier)); 
			}catch(Exception ex){
				Logger.getLogger(getClass()).warn("error when looking for GatewayState " + ex.getMessage());
			}
			if (state == null){
				Thread.sleep(500);
			}
			attempNumber++;
			
		}
		return state;
	
	}
}
