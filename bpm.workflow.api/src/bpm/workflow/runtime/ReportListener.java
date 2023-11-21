package bpm.workflow.runtime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.listeners.event.impl.ReportExecutedEvent;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.utils.IOWriter;

/**
 * Will handle a report Generation
 * 
 * 
 * @author ludo
 *
 */
public class ReportListener {
	public class Handler{
		private String outputFileName;
		private IRunIdentifier taskId;	
		private String processInstanceUuid;
		private String activityInstanceUuid;
		private IObjectIdentifier workflowIdentifier;
		private IObjectIdentifier reportIdentifier;
		
		public Handler(String processInstanceUuid, String activityInstanceUuid, IObjectIdentifier workflowIdentifier, IObjectIdentifier reportIdentifier, String outputFileName, IRunIdentifier taskId){
			this.outputFileName = outputFileName;
			this.taskId = taskId;
			this.activityInstanceUuid = activityInstanceUuid;
			this.processInstanceUuid = processInstanceUuid;
			this.workflowIdentifier = workflowIdentifier;
			this.reportIdentifier = reportIdentifier;
		}
		
		/**
		 * @return the activityInstanceUuid
		 */
		protected String getActivityInstanceUuid() {
			return activityInstanceUuid;
		}

		private IVanillaContext getVanillaContext(String sessionId) throws Exception{
			VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
			RemoteVanillaPlatform api = new RemoteVanillaPlatform(
					conf.getVanillaServerUrl(),
					conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN),
					conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
			
			VanillaSession session = api.getVanillaSystemManager().getSession(sessionId);
			return new BaseVanillaContext(conf.getVanillaServerUrl(), session.getUser().getLogin(), session.getUser().getPassword());
			
		}
		
		public void handle(ReportExecutedEvent event){
			InputStream is = null;
			FileOutputStream os = null;
			File folderStorage = new File(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "/" + WorkflowService.WORKFLOW_MASS_HISTORIZATION_FOLDER);
			
			try{
				Logger.getLogger(getClass()).info("Executing handlder...");
				RemoteReportRuntime remote = new RemoteReportRuntime(getVanillaContext(event.getSessionId()));
				
				is = remote.loadGeneratedReport(taskId);
				
				File file = new File(folderStorage, outputFileName);
				
				os = new FileOutputStream(file);
				IOWriter.write(is, os, true, true);
				Logger.getLogger(getClass()).info("File " + file.getAbsolutePath() + " writed from report execution");

			}catch(Exception ex){
				Logger.getLogger(getClass()).error("ReportExecution Handling failed - " + ex.getMessage(), ex);
			}finally{
				if (is != null){
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (os != null){
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				Logger.getLogger(getClass()).info("Handler number = " + monitoringHandler.size());
			}
			
		}

		/**
		 * @return the processInstanceUuid
		 */
		protected String getProcessInstanceUuid() {
			return processInstanceUuid;
		}

		/**
		 * @return the workflowIdentifier
		 */
		protected IObjectIdentifier getWorkflowIdentifier() {
			return workflowIdentifier;
		}

		/**
		 * @return the reportIdentifier
		 */
		protected IObjectIdentifier getReportIdentifier() {
			return reportIdentifier;
		}
	
		
	
	}
	
	
	private List<Handler> monitoringHandler = new ArrayList<Handler>();
	
	
	public void addHandler(Handler handler){
		synchronized (monitoringHandler) {
			monitoringHandler.add(handler);
			Logger.getLogger(getClass()).info("Handling Report for task " + handler.taskId);
		}
		try{
			IVanillaAPI api = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration());
			
			api.getMassReportMonitor().setReportGenerationAsked(
					handler.getWorkflowIdentifier(), 
					handler.getReportIdentifier(), 
					handler.getProcessInstanceUuid(),
					handler.getActivityInstanceUuid());
		}catch(Exception ex){
			Logger.getLogger(getClass()).error("Failed to update MassReportMonitor from handler " + ex.getMessage(), ex);
		}
	}

	protected void removeHandler(Handler handler){
		synchronized (monitoringHandler) {
			monitoringHandler.remove(handler);
			Logger.getLogger(getClass()).info("handler removed");
		}
		try{			
			IVanillaAPI api = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration());
		
			api.getMassReportMonitor().setReportGenerated(
					handler.getWorkflowIdentifier(), 
					handler.getReportIdentifier(), 
					handler.getProcessInstanceUuid(),
					handler.getActivityInstanceUuid());
		}catch(Exception ex){
			Logger.getLogger(getClass()).error("Failed to update MassReportMonitor from handler " + ex.getMessage(), ex);
		}
		
	}

	public void handle(ReportExecutedEvent event){
		for(Handler h : monitoringHandler){
			if (event.getRunIdentifier().getKey().equals(h.taskId.getKey())){
				h.handle(event);
				removeHandler(h);
				return;
				
			}
		}
	}

}
