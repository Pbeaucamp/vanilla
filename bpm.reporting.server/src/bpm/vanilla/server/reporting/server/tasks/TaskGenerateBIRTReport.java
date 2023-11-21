package bpm.vanilla.server.reporting.server.tasks;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.engine.api.IParameterGroupDefn;
import org.eclipse.birt.report.engine.api.IParameterSelectionChoice;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;

import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.tasks.ITaskState;
import bpm.vanilla.platform.core.beans.tasks.TaskPriority;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.listeners.event.impl.ObjectExecutedEvent;
import bpm.vanilla.platform.core.listeners.event.impl.ReportExecutedEvent;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunIdentifier;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.tasks.ITask;
import bpm.vanilla.server.commons.server.tasks.ReportGenerationState;
import bpm.vanilla.server.commons.server.tasks.TaskPriorityComparator;
import bpm.vanilla.server.reporting.pool.BirtPoolableModel;
import bpm.vanilla.server.reporting.server.ReportingServerConfig;
import bpm.vanilla.server.reporting.server.tasks.birt.HTMLEmbeddedImageHandler;

public class TaskGenerateBIRTReport extends TaskGenerateReport implements ITask {

	private static final long serialVersionUID = -3431402162749382076L;

	private transient Logger logger = Logger.getLogger(this.getClass());

	public static final int TYPE_STRING = 1;
	public static final int TYPE_FLOAT = 2;
	public static final int TYPE_DECIMAL = 3;
	public static final int TYPE_DATE_TIME = 4;
	public static final int TYPE_BOOLEAN = 5;
	public static final int TYPE_INTEGER = 6;
	public static final int TYPE_DATE = 7;
	public static final int TYPE_TIME = 8;

	private transient IReportRuntimeConfig reportConfig;
	private transient BirtPoolableModel birtModel;

	private transient boolean on = false;
	private transient Thread thread;
	private transient OutputStream outputStream;
	
	private long taskId;
	private TaskPriority taskPriority;
	private ReportGenerationState state = new ReportGenerationState();

	private String outputFormat;

	public TaskGenerateBIRTReport(IReportRuntimeConfig reportConfig, long taskId, Server server, 
			BirtPoolableModel poolableModel, OutputStream outputStream, String sessionId) throws Exception {
		super(server, sessionId);
		this.taskPriority = TaskPriority.NORMAL_PRIORITY;
		this.reportConfig = reportConfig;
		this.taskId = taskId;
		this.birtModel = poolableModel;
		this.server = server;

		this.outputStream = outputStream;
		this.outputFormat = reportConfig.getOutputFormat();
	}

	private class Runner extends Thread {

		public void run() {
			// ere, so we dont touch vanilla, spare me
			if (outputFormat.equals("odt")) {
				logger.info("On the fly replacements of 'odt' by 'odt.xml'");
				outputFormat = "odt.xml";
			}
			state.setStarted();
			on = true;
			boolean shouldCloseOutputStream = false;

			String outputFileName = ((ReportingServerConfig) server.getConfig()).getGenerationFolder() + "/" + taskId;
			try {
				// locale stuff
				Locale locale = reportConfig.getLocale();

				if (outputStream == null) {
					outputStream = new FileOutputStream(outputFileName);
					shouldCloseOutputStream = true;
				}

				RenderOption renderOption = generateRenderOption();

				//

				IRunAndRenderTask task = birtModel.getBirtEngine().createRunAndRenderTask(birtModel.getModel());
				if (reportConfig.getMaxRowsPerQuery() != null && reportConfig.getMaxRowsPerQuery() != -1 && reportConfig.getMaxRowsPerQuery() != 0) {
					task.setMaxRowsPerQuery(reportConfig.getMaxRowsPerQuery());
				}

				if (locale != null/* && resources != null */) {
					logger.info("Setting task locale : " + locale.toString() + ", " + locale.getDisplayName());
					task.setLocale(locale);
				}

				HashMap<String, IScalarParameterDefn> defs = getParamatersDefinitions(birtModel.getBirtEngine(), birtModel.getModel());
				boolean b = setParametersValuesForTask(task, defs);
				// TODO : throw Exception ? log a warning?

				task.setRenderOption(renderOption);

				task.run();
				task.close();

				if (shouldCloseOutputStream) {
					outputStream.close();
				}

				setOutputFileName(outputFileName);
				state.setSucceed();
			} catch (Exception e) {
				e.printStackTrace();
				setOutputFileName(null);
				if (e.getCause() != null && e.getCause() instanceof RuntimeException) {
					state.setFailed("Error when generating Report :\n\t-" + e.getCause().getMessage());
				}
				else {
					state.setFailed("Error when generating Report :\n\t-" + e.getMessage());
				}

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
				TaskGenerateBIRTReport.this.stopTask();
			} catch (Exception e) {
				e.printStackTrace();
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
	 * @param taskPriority
	 *            the taskPriority to set
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

	public void startTask() {
		thread = new Runner();
		thread.start();
	}

	@Override
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

		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();

		RemoteVanillaPlatform api = new RemoteVanillaPlatform(conf);
//		IRunIdentifier runId = new SimpleRunIdentifier(getServer().getComponentIdentifier().getComponentUrl() + "-" + getServer().getComponentIdentifier().getComponentId() + "-" + getId());
		
		api.getListenerService().fireEvent(new ObjectExecutedEvent(getServer().getComponentIdentifier(), null, reportConfig.getObjectIdentifier(), reportConfig.getVanillaGroupId(), state));

		super.stopTask();
		
		Logger.getLogger(getClass()).info("##############################################################");
		Logger.getLogger(getClass()).info("End Report " + (birtModel.getDirectoryItem() != null ? birtModel.getDirectoryItem().getName() : "Unknown"));
		Logger.getLogger(getClass()).info("##############################################################");
	}

	private RenderOption generateRenderOption() throws Exception {
		logger.info("Generating Render Options...");
		logger.info("Found output format = " + outputFormat);

		// if (outputFormat )

		RenderOption options = null;
		if (outputFormat == null || "".equals(outputFormat) || outputFormat.trim().equalsIgnoreCase("pdf")) {
			options = new PDFRenderOption();
			options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
			options.setOutputFormat(PDFRenderOption.OUTPUT_FORMAT_PDF);
			options.setOutputStream(outputStream);
		}
		else if (outputFormat.trim().equalsIgnoreCase("html")) {
			options = new HTMLRenderOption();
			options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_HTML);
			options.setOutputStream(outputStream);
			options.setImageHandler(new HTMLEmbeddedImageHandler());
			((HTMLRenderOption) options).setEnableAgentStyleEngine(true);
			options.setSupportedImageFormats("");
		}
		else if (outputFormat.trim().equalsIgnoreCase("rtf")) {
			options = new RenderOption();
			options.setOutputFormat("rtf");
			options.setOutputStream(outputStream);
			options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
		}
		else if (outputFormat.trim().equalsIgnoreCase("xls")) {
			options = new EXCELRenderOption();
			// ((EXCELRenderOption) options).set
			// options = new RenderOption();
			options.setOutputFormat("xls");
			options.setOutputStream(outputStream);
			options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
		}
		else if (outputFormat.trim().equalsIgnoreCase("doc")) {
			options = new RenderOption();
			options.setOutputFormat("doc");
			options.setOutputStream(outputStream);
			options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
		}
		else if (outputFormat.trim().equalsIgnoreCase("odt.xml") || outputFormat.trim().equalsIgnoreCase("odt")) {
			options = new RenderOption();
			options.setOutputFormat("odt");
			options.setOutputStream(outputStream);
			options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
		}
		else if (outputFormat.trim().equalsIgnoreCase("ods")) {
			options = new RenderOption();
			options.setOutputFormat("xls");
			options.setOutputStream(outputStream);
			options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
		}
		else if (outputFormat.trim().equalsIgnoreCase("ppt")) {
			options = new RenderOption();
			options.setOutputFormat("ppt");
			options.setOutputStream(outputStream);
			options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
		}
		else if (outputFormat.trim().equalsIgnoreCase("ps") || outputFormat.trim().equalsIgnoreCase("postscript")) {
			options = new RenderOption();
			options.setOutputFormat("postscript");
			options.setOutputStream(outputStream);
			options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
		}
		else if (outputFormat.trim().equalsIgnoreCase("xlsx")) {
			options = new EXCELRenderOption();
			options.setOutputFormat("xlsx");
			options.setOutputStream(outputStream);
			options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
		}
		else if (outputFormat.trim().equalsIgnoreCase("docx")) {
			options = new RenderOption();
			options.setOutputFormat("docx");
			options.setOutputStream(outputStream);
			options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
		}
		else if (outputFormat.trim().equalsIgnoreCase("pptx")) {
			options = new RenderOption();
			options.setOutputFormat("pptx");
			options.setOutputStream(outputStream);
			options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
		}
		else if (outputFormat.trim().equalsIgnoreCase("csv")) {
			options = new RenderOption();
			options.setOutputFormat("csv");
			options.setOutputStream(outputStream);
			options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
		}
		else {
			logger.warn("No Valid options were found for the output format. Result might be an error.");
			throw new Exception("No Valid options were found for the output format. Requested = " + outputFormat);
		}

		return options;
	}

	private boolean setParametersValuesForTask(IRunAndRenderTask task, HashMap<String, IScalarParameterDefn> defs) throws ParseException {
		if (reportConfig.getParametersValues() != null) {
			for (VanillaGroupParameter grpParam : reportConfig.getParametersValues()) {
				if (grpParam.getParameters() != null) {
					for (VanillaParameter param : grpParam.getParameters()) {
						String key = param.getName();
						List<String> valuesParam = param.getSelectedValues();
						IScalarParameterDefn def = defs.get(key);
						if (def == null) {
							continue;
						}

						if (def.getScalarParameterType().equalsIgnoreCase(VanillaParameter.PARAM_TYPE_MULTI)) {
							logger.info("Setting multivalued parameter");
							List<Object> values = new ArrayList<Object>();
							for (String value : valuesParam) {
								switch (def.getDataType()) {
									case TYPE_STRING:
										values.add(value);
										break;

									case TYPE_BOOLEAN:
										try {
											boolean val = Boolean.parseBoolean(value);
											values.add(val);
										} catch (Exception e) {
											values.add(null);
										}
										break;

									case TYPE_DECIMAL:
										values.add(new BigDecimal(value));
										break;

									case TYPE_FLOAT:
										try {
											float val = Float.parseFloat(value);
											values.add(val);
										} catch (Exception e) {
											values.add(null);
										}
										break;

									case TYPE_INTEGER:
										try {
											int val = Integer.parseInt(value);
											values.add(val);
										} catch (Exception e) {
											values.add(null);
										}
										break;

									case TYPE_DATE:
										String format = "yyyy-MM-dd";
										if (def.getDisplayFormat() != null && !def.getDisplayFormat().isEmpty()) {
											format = def.getDisplayFormat();
										}
										SimpleDateFormat sdf = new SimpleDateFormat(format);
										Date d = sdf.parse(value);
										java.sql.Date sql = new java.sql.Date(d.getTime());
										values.add(sql);
										break;

									case TYPE_DATE_TIME:
										String format2 = "yyyy-MM-dd hh:mm:ss";
										if (def.getDisplayFormat() != null && !def.getDisplayFormat().isEmpty()) {
											format2 = def.getDisplayFormat();
										}
										SimpleDateFormat sdf2 = new SimpleDateFormat(format2);
										Date _d = sdf2.parse(value);
										java.sql.Date _sql = new java.sql.Date(_d.getTime());
										values.add(_sql);
										break;

									default:
										break;
								}
							}
							task.setParameterValue(key, values.toArray(new Object[values.size()]));
						}
						else if ((valuesParam != null && !valuesParam.isEmpty()) || (param.getDefaultValue() != null && !param.getDefaultValue().isEmpty())) {
							String value = !valuesParam.isEmpty() && valuesParam.get(0) != null ? valuesParam.get(0) : param.getDefaultValue();
							String displayText = value;
							for(Entry<String, String> val : param.getValues().entrySet()) {
								if(val.getValue().equals(value)) {
									displayText = val.getKey();
									break;
								}
							}
							
							switch (def.getDataType()) {
								case TYPE_STRING:
									task.setParameterValue(key, value);
									break;

								case TYPE_BOOLEAN:
									try {
										boolean val = Boolean.parseBoolean(value);
										task.setParameterValue(key, val);
									} catch (Exception e) {
										task.setParameterValue(key, null);
									}
									break;

								case TYPE_DECIMAL:
									task.setParameterValue(key, new BigDecimal(value));
									break;

								case TYPE_FLOAT:
									try {
										float val = Float.parseFloat(value);
										task.setParameterValue(key, val);
									} catch (Exception e) {
										task.setParameterValue(key, null);
									}
									break;

								case TYPE_INTEGER:
									try {
										int val = Integer.parseInt(value);
										task.setParameterValue(key, val);
									} catch (Exception e) {
										task.setParameterValue(key, null);
									}
									break;

								case TYPE_DATE:
									String format = "yyyy-MM-dd";
									if (def.getDisplayFormat() != null && !def.getDisplayFormat().isEmpty()) {
										format = def.getDisplayFormat();
									}
									SimpleDateFormat sdf = new SimpleDateFormat(format);
									Date d = sdf.parse(value);
									java.sql.Date sql = new java.sql.Date(d.getTime());
									task.setParameterValue(key, sql);
									break;

								case TYPE_DATE_TIME:
									String format2 = "yyyy-MM-dd hh:mm:ss";
									if (def.getDisplayFormat() != null && !def.getDisplayFormat().isEmpty()) {
										format2 = def.getDisplayFormat();
									}
									SimpleDateFormat sdf2 = new SimpleDateFormat(format2);
									Date _d = sdf2.parse(value);
									java.sql.Date _sql = new java.sql.Date(_d.getTime());
									task.setParameterValue(key, _sql);
									break;

								default:
									break;
							}
							task.setParameterDisplayText(key, displayText);
						}
						else {
							task.setParameterValue(key, null);
						}
					}
				}
			}
		}

		try {
			task.setParameterValue("_ReportComment_ItemId_", birtModel.getDirectoryItem().getId() + "");
			task.setParameterDisplayText("_ReportComment_ItemId_", birtModel.getDirectoryItem().getId() + "");
			
			task.setParameterValue("_ReportComment_RepId_", birtModel.getItemKey().getRepositoryContext().getRepository().getId() + "");
			task.setParameterDisplayText("_ReportComment_RepId_", birtModel.getItemKey().getRepositoryContext().getRepository().getId() + "");
			
			task.setParameterValue("_ReportComment_GroupeId_", birtModel.getItemKey().getRepositoryContext().getGroup().getId() + "");
			task.setParameterDisplayText("_ReportComment_GroupeId_", birtModel.getItemKey().getRepositoryContext().getGroup().getId() + "");

			task.setParameterValue(CommentDefinition.DISPLAY_COMMENTS_PARAMETER, reportConfig.displayComments() ? "1" : "0");
			task.setParameterDisplayText(CommentDefinition.DISPLAY_COMMENTS_PARAMETER, reportConfig.displayComments() ? "1" : "0");
		} catch (Exception e) {

		}
		return task.validateParameters();
	}

	@Override
	public PoolableModel<?> getPoolableModel() {
		return birtModel;
	}

	public int compareTo(Object arg0) {
		return TaskPriorityComparator.instance.compare(this, (ITask) arg0);
	}

	@SuppressWarnings("unchecked")
	private static HashMap<String, IScalarParameterDefn> getParamatersDefinitions(IReportEngine engine, IReportRunnable design) {
		HashMap<String, IScalarParameterDefn> res = new HashMap<String, IScalarParameterDefn>();

		IGetParameterDefinitionTask parametersTask = engine.createGetParameterDefinitionTask(design);
		Collection<IParameterDefnBase> params = parametersTask.getParameterDefns(true);

		// Iterate over all parameters
		for (IParameterDefnBase param : params) {
			// Group section found
			if (param instanceof IParameterGroupDefn) {
				// Get Group Name
				IParameterGroupDefn group = (IParameterGroupDefn) param;

				// Get the parameters within a group
				Iterator<IScalarParameterDefn> i2 = group.getContents().iterator();
				while (i2.hasNext()) {
					IScalarParameterDefn scalar = i2.next();

					res.put(scalar.getName(), scalar);
				}
			}
			else {
				// Parameters are not in a group
				IScalarParameterDefn scalar = (IScalarParameterDefn) param;
				res.put(scalar.getName(), scalar);
			}
		}

		parametersTask.close();
		return res;
	}

	@Override
	public void join() throws Exception {
		if (thread != null && thread.isAlive()) {
			thread.join();
		}
	}

}
