package bpm.vanilla.server.reporting.server.tasks;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;

import bpm.fwr.api.FwrReportManager;
import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.birt.HTMLEmbeddedImageHandler;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.tasks.ITaskState;
import bpm.vanilla.platform.core.beans.tasks.TaskPriority;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.tasks.ITask;
import bpm.vanilla.server.commons.server.tasks.ReportGenerationState;
import bpm.vanilla.server.commons.server.tasks.TaskPriorityComparator;
import bpm.vanilla.server.reporting.pool.FwrPoolableModel;
import bpm.vanilla.server.reporting.server.ReportingServerConfig;

/**
 * This class Generate A FWR report in the given OutputSTream if teh
 * OutputStream is null, a File is created with the name :
 * ((ReportingServerConfig)server.getConfig()).getGenerationFolder() +
 * "/report_" + id
 * 
 * if the server is stopped, all generated files will be deleted
 * 
 * @author ludo
 * 
 */
public class TaskGenerateFWRReport extends TaskGenerateReport {
	
	private static final long serialVersionUID = -843904062921151187L;

	private TaskPriority taskPriority;
	private transient FwrPoolableModel fwrModel;
	private IReportRuntimeConfig reportConfig;
	
	private long taskId;

	private boolean on = false;
	private transient Thread thread;
	private transient OutputStream outputStream;

	private ReportGenerationState state = new ReportGenerationState();

	public TaskGenerateFWRReport(IReportRuntimeConfig reportConfig, long taskId, Server server, FwrPoolableModel fwrModel, OutputStream outputStream, String sessionId) {
		super(server, sessionId);
		this.taskPriority = TaskPriority.NORMAL_PRIORITY;
		this.reportConfig = reportConfig;
		this.taskId = taskId;
		this.fwrModel = fwrModel;
		this.outputStream = outputStream;
	}

	public class Runner extends Thread {
		public void run() {
			state.setStarted();
			on = true;
			boolean shouldCloseOutputStream = false;
			
			FWRReport model = null;
			try {
				model = fwrModel.getModel();
			} catch (Exception ex) {
				ex.printStackTrace();
				state.setFailed("Error when getting Repository connection datas:\n\t" + ex.getCause().getMessage());
				on = false;
				state.setStopped();
				return;
			}

			IRepositoryApi sock = null;
			try {
				sock = new RemoteRepositoryApi(getPoolableModel().getItemKey().getRepositoryContext());
			} catch (Exception e) {
				e.printStackTrace();

				state.setFailed("Error when generating Report :\n\t-" + e.getMessage());
				if (shouldCloseOutputStream) {
					try {
						outputStream.close();
					} catch (IOException ex) {
						e.printStackTrace();
					}
				}
				return;
			}

			applyProperties(model);

			try {
				FwrReportManager manager = new FwrReportManager(fwrModel.getBirtEngine(), model);
				HashMap<String, String> alternates = reportConfig.getAlternateConnectionsConfiguration() != null ? reportConfig.getAlternateConnectionsConfiguration().getAlternateConnections() : new HashMap<String, String>();
				IReportRunnable design = manager.createReport(sock, alternates, reportConfig.getOutputFormat());
				IRunAndRenderTask task = fwrModel.getBirtEngine().createRunAndRenderTask(design);

				String outputFileName = ((ReportingServerConfig) server.getConfig()).getGenerationFolder() + "/" + taskId;
				if (outputStream == null) {
					outputStream = new FileOutputStream(outputFileName);
					shouldCloseOutputStream = true;
				}
				
				// set output options
				if (model.getOutput().equalsIgnoreCase("PDF")) {
					PDFRenderOption renderOption = new PDFRenderOption();
					renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
					renderOption.setOutputFormat(PDFRenderOption.OUTPUT_FORMAT_PDF);
					renderOption.setOutputStream(outputStream);
	
					HashMap<String, IRenderOption> contextMap = new HashMap<String, IRenderOption>();
					contextMap.put(EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT, renderOption);
	
					task.setAppContext(contextMap);
					task.setRenderOption(renderOption);
				}
				else if (model.getOutput().equalsIgnoreCase("EXCEL") || model.getOutput().equalsIgnoreCase("EXCEL_PLAIN")) {
					HTMLRenderOption renderOption = new HTMLRenderOption();
					renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
					renderOption.setOutputFormat("xls");
					renderOption.setOutputStream(outputStream);
	
					HashMap<String, IRenderOption> contextMap = new HashMap<String, IRenderOption>();
					contextMap.put(EngineConstants.APPCONTEXT_HTML_RENDER_CONTEXT, renderOption);
	
					task.setAppContext(contextMap);
					task.setRenderOption(renderOption);
				}
				else if (model.getOutput().equalsIgnoreCase("DOC") || model.getOutput().equalsIgnoreCase("RTF")) {
					PDFRenderOption renderOption = new PDFRenderOption();
					renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
					renderOption.setOutputFormat("doc");
					renderOption.setOutputStream(outputStream);
	
					HashMap<String, IRenderOption> contextMap = new HashMap<String, IRenderOption>();
					contextMap.put(EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT, renderOption);
	
					task.setAppContext(contextMap);
					task.setRenderOption(renderOption);
				}
				else {
					IRenderOption renderOption = new HTMLRenderOption();
					renderOption.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_HTML);
					renderOption.setOutputStream(outputStream);
					renderOption.setImageHandler(new HTMLEmbeddedImageHandler());
					((HTMLRenderOption) renderOption).setEnableAgentStyleEngine(true);
	
					task.setRenderOption(renderOption);
				}
				

				setParametersValuesForTask(task);
	
				// run report
				// designreport.saveAs("C:/Users/manu/Desktop/test.rptdesign");
				task.run();
				task.close();
			
				outputStream.close();
				state.setSucceed();
				setOutputFileName(outputFileName);
			} catch (Exception e) {
				e.printStackTrace();

				state.setFailed("Error when generating Report :\n\t-" + e.getMessage());
				if (shouldCloseOutputStream) {
					try {
						outputStream.close();
					} catch (IOException ex) {
						e.printStackTrace();
					}
				}
			}

			on = false;
			state.setStopped();
			try {
				stopTask();
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			Logger.getLogger(getClass()).info("##############################################################");
			Logger.getLogger(getClass()).info("End Report " + (fwrModel.getDirectoryItem() != null ? fwrModel.getDirectoryItem().getName() : "Unknown"));
			Logger.getLogger(getClass()).info("##############################################################");
		}
	}

	private void setParametersValuesForTask(IRunAndRenderTask task) throws ParseException {
		HashMap<String, Object> parameterValues = getParameters();
		for (String s : parameterValues.keySet()) {

			if (parameterValues.get(s) instanceof List<?>) {
				StringBuffer buf = new StringBuffer();
				
				int i = 0;
				for (String value : (List<String>) parameterValues.get(s)) {
					if(i == 0) {
						buf.append(value);
					}
					else {
						buf.append("," + value);
					}
					i++;
				}
				task.setParameterValue(s, buf.toString());
			}
			else {
				Object o = parameterValues.get(s);
				if(o.toString().contains("[")) {
					o = o.toString().substring(o.toString().indexOf("[") + 1);
				}
				task.setParameterValue(s, o);
			}
		}
	}

	/**
	 * @return the taskPriority
	 */
	public TaskPriority getTaskPriority() {
		return taskPriority;
	}

	/**
	 * @param taskPriority the taskPriority to set
	 */
	public void setTaskPriority(TaskPriority taskPriority) {
		this.taskPriority = taskPriority;
	}

	public long getId() {
		return taskId;
	}

	public ITaskState getTaskState() {
		return state;
	}

	public boolean isRunning() {
		return thread != null && thread.isAlive();
	}

	public boolean isStopped() {
		return !on;
	}

	private void applyProperties(FWRReport model) {
		if (reportConfig.getOutputFormat() != null) {
			model.setOutput(reportConfig.getOutputFormat());
		}
	}

	public void startTask() {
		thread = new Runner();
		thread.start();
	}

	public void stopTask() throws Exception {
		if (on) {
			if (thread != null && thread.isAlive()) {
				try {
					thread.interrupt();
				} catch (Exception ex) {

				} finally {
					try {
						thread.interrupt();
					} catch (Exception e) {

					}
				}
				state.setFailed("Task interrupted");
				thread = null;
				state.setStopped();
				on = false;
			}
		}
		thread = null;
		server.getTaskManager().removeFinishedTasksFromRunningList(this);
		
		super.stopTask();
	}

	private HashMap<String, Object> getParameters() {
		HashMap<String, Object> params = new HashMap<String, Object>();

		if(reportConfig.getParametersValues() != null) {
			for(VanillaGroupParameter groupParam : reportConfig.getParametersValues()) {
				if(groupParam.getParameters() != null) {
					for(VanillaParameter param : groupParam.getParameters()) {
						params.put(param.getName(), param.getSelectedValues());
					}
				}
			}
		}

		try {
			params.put("_ReportComment_ItemId_", fwrModel.getDirectoryItem().getId() + "");
			params.put("_ReportComment_RepId_", fwrModel.getItemKey().getRepositoryContext().getRepository().getId() + "");
		} catch (Exception e1) { }

		return params;
	}

	@Override
	public PoolableModel<?> getPoolableModel() {
		return fwrModel;
	}

	public int compareTo(Object arg0) {
		return TaskPriorityComparator.instance.compare(this, (ITask) arg0);
	}

	@Override
	public void join() throws Exception {
		if (thread != null && thread.isAlive()) {
			thread.join();
		}
	}
}
