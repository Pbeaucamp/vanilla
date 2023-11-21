package bpm.vanilla.server.reporting.server.tasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import org.apache.log4j.Logger;
import org.eclipse.birt.report.data.oda.jdbc.JDBCDriverManager;

import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.tasks.ITaskState;
import bpm.vanilla.platform.core.beans.tasks.TaskPriority;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.tasks.ITask;
import bpm.vanilla.server.commons.server.tasks.ReportGenerationState;
import bpm.vanilla.server.commons.server.tasks.TaskPriorityComparator;
import bpm.vanilla.server.reporting.pool.JasperPoolableModel;
import bpm.vanilla.server.reporting.server.ReportingServerConfig;

public class TaskGenerateJASPERReport extends TaskGenerateReport implements ITask {

	private static final long serialVersionUID = -7008570889690886057L;

	private long taskId;
	private IReportRuntimeConfig reportConfig;
	private transient JasperPoolableModel jasperModel;
	private TaskPriority taskPriority;

	private boolean on = false;
	private transient Thread thread;
	private transient OutputStream outputStream;

	private ReportGenerationState state = new ReportGenerationState();

	public TaskGenerateJASPERReport(IReportRuntimeConfig reportConfig, long taskId, Server server, JasperPoolableModel jasperModel, OutputStream outputStream, String sessionId) {
		super(server, sessionId);
		this.taskPriority = TaskPriority.NORMAL_PRIORITY;
		this.reportConfig = reportConfig;
		this.taskId = taskId;
		this.server = server;
		this.jasperModel = jasperModel;

		this.outputStream = outputStream;
	}

	private class Runner extends Thread {
		public void run() {
			state.setStarted();
			on = true;

			JasperPrint jasperPrint = null;

			// get the repository to be able to load datasources
			IRepositoryApi sock = null;
			try {
				sock = new RemoteRepositoryApi(getPoolableModel().getItemKey().getRepositoryContext());
			} catch (Exception e) {
				e.printStackTrace();
				state.setFailed("Error when creating report outputStream :" + e.getMessage());
				on = false;
				state.setStopped();
				try {
					stopTask();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				return;
			}

			String outputFileName = ((ReportingServerConfig) server.getConfig()).getGenerationFolder() + "/" + taskId;

			boolean shouldCloseOutputStream = false;
			if (outputStream == null) {
				try {
					shouldCloseOutputStream = true;

					outputStream = new FileOutputStream(outputFileName);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					state.setFailed("Error when creating report outputStream :" + e.getMessage());
					on = false;
					state.setStopped();
					try {
						stopTask();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					return;
				}
			}

			// deal with the different DataSOurce types
			switch (jasperModel.getJasperModelType()) {

				case JasperPoolableModel.DATASOURCE_DB_REP:

					// load DataSource
					List<DataSource> ds = new ArrayList<DataSource>();
					try {
						ds = sock.getImpactDetectionService().getAllDatasources();
					} catch (Exception e) {
						e.printStackTrace();
					}
					DataSource look = null;
					for (DataSource s : ds) {
						if (s.getId() == jasperModel.getDataSourceId()) {
							look = s;
							break;
						}

					}

					if (look == null) {
						state.setFailed("Unable to find sql dataSource with id =" + jasperModel.getDataSourceId() + " on repository with id = ");
						on = false;
						state.setStopped();
						if (shouldCloseOutputStream) {
							try {
								outputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						try {
							stopTask();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						return;
					}
					
					if(!look.getOdaExtensionId().equals(DataSource.DATASOURCE_JDBC)) {
						state.setFailed("This type of datasource is not suppported for a Jasper Report : " + look.getOdaExtensionId());
						on = false;
						state.setStopped();
						try {
							stopTask();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						return;
					}
					
					String driverName = look.getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_DRIVER);
					String datasourceUrl = look.getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_URL);
					String user = look.getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_USER);
					String password = look.getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_PASS);

					try {

						// init JDBC stuff
						List<String> jarFilesLocation = new ArrayList<String>();
						jarFilesLocation.add(IConstants.getJdbcJarFolder());
						for (DriverInfo inf : ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo()) {
							if (inf.getClassName().equals(driverName)) {
								JDBCDriverManager.getInstance().loadAndRegisterDriver(inf.getClassName(), jarFilesLocation);
							}
							else if (inf.getName().equals(driverName)) {
								JDBCDriverManager.getInstance().loadAndRegisterDriver(inf.getClassName(), jarFilesLocation);
							}
						}

					} catch (Exception ex) {
						ex.printStackTrace();

					}

					Connection conn = null;
					try {
						conn = DriverManager.getConnection(datasourceUrl, user, password);
					} catch (Exception ex) {
						ex.printStackTrace();
						state.setFailed("Error when connecting to DataBase at: " + datasourceUrl + "\n" + ex.getMessage());
						on = false;
						if (shouldCloseOutputStream) {
							try {
								outputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						try {
							stopTask();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						return;
					}

					HashMap<String, Object> reportParameters = null;
					try {
						reportParameters = getReportParameters();
					} catch (Exception e2) {
						e2.printStackTrace();
					}

					// fill the report
					try {
						jasperPrint = JasperFillManager.fillReport(jasperModel.getModel(), reportParameters, conn);
					} catch (Exception ex) {
						ex.printStackTrace();
						state.setFailed("Error when filling the JasperReport :" + ex.getCause().getMessage());
						on = false;
						if (shouldCloseOutputStream) {
							try {
								outputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						try {
							stopTask();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						return;
					}

					break;
				case JasperPoolableModel.NO_DATASOURCE_DEFINED:
					break;

			}

			if (jasperPrint != null) {
				try {
					if (reportConfig.getOutputFormat().trim().equalsIgnoreCase("html")) {
						OutputProcessed ouputProcessed = processOutput();
						JRAbstractExporter exporter = ouputProcessed.exporter;
						byte[] reportAsBytes;
						ByteArrayOutputStream baos = new ByteArrayOutputStream();

						exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
						exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
						exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, ((ReportingServerConfig) server.getConfig()).getJasperImagesUri() + "/images/");

						exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
						exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, ((ReportingServerConfig) server.getConfig()).getGenerationFolder() + File.separator + "images");
						exporter.exportReport();

						reportAsBytes = baos.toByteArray();

						outputStream.write(reportAsBytes, 0, reportAsBytes.length);
						outputStream.flush();
						outputStream.close();
						setOutputFileName(outputFileName);
					}
					else {
						JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
						setOutputFileName(outputFileName);
					}

					state.setSucceed();
				} catch (Exception ex) {
					ex.printStackTrace();
					state.setFailed("Error when exporting JasperReport : " + ex.getCause().getMessage());
				}
			}
			else {
				state.setFailed("No JasperPrint created");
			}

			on = false;
			state.setStopped();
			if (shouldCloseOutputStream) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				stopTask();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			Logger.getLogger(getClass()).info("##############################################################");
			Logger.getLogger(getClass()).info("End Report " + (jasperModel.getDirectoryItem() != null ? jasperModel.getDirectoryItem().getName() : "Unknown"));
			Logger.getLogger(getClass()).info("##############################################################");
		}
	}

	private HashMap<String, Object> getReportParameters() throws Exception {
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		IRepositoryApi sock = new RemoteRepositoryApi(jasperModel.getItemKey().getRepositoryContext());
		List<Parameter> params = sock.getRepositoryService().getParameters(jasperModel.getDirectoryItem());
		
		if(params != null && !params.isEmpty()) {
			LOOK:for(Parameter p : params) {
				
				for(VanillaGroupParameter gp : reportConfig.getParametersValues()) {
					for(VanillaParameter vp : gp.getParameters()) {
						if(vp.getName().equals(p.getName())) {
							result.put(p.getName(), vp.getSelectedValues().get(0));
							continue LOOK;
						}
					}
				}
				
			}
		}
		
		return result;
	}

	public long getId() {
		return taskId;
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

	public ITaskState getTaskState() {
		return state;
	}

	public boolean isRunning() {

		return thread != null && thread.isAlive();
	}

	public boolean isStopped() {
		return !on;
	}

	public void stopTask() throws Exception {
		try {
			thread.interrupt();
		} catch (Exception ex) {

		} finally {
			try {
				thread.interrupt();
			} catch (Exception e) {

			}
		}
		thread = null;
		if (state.getFailingCause() != null && !state.getFailingCause().isEmpty()) {
			state.setFailed("Task interrupted");
		}
		state.setStopped();
		on = false;
		server.getTaskManager().removeFinishedTasksFromRunningList(this);
		
		super.stopTask();
	}

	public void startTask() {
		thread = new Runner();
		thread.start();
	}

	@Override
	public PoolableModel<?> getPoolableModel() {
		return jasperModel;
	}

	public int compareTo(Object arg0) {
		return TaskPriorityComparator.instance.compare(this, (ITask) arg0);
	}

	private class OutputProcessed {
		public JRAbstractExporter exporter;
	}

	private OutputProcessed processOutput() {
		OutputProcessed result = new OutputProcessed();

		result.exporter = new JRHtmlExporter();
		result.exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);

		return result;
	}

	@Override
	public void join() throws Exception {
		if (thread != null && thread.isAlive()) {
			thread.join();
		}

	}
}
