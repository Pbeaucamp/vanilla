package bpm.vanilla.server.client.commandline.profiing;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.server.client.ServerClient;
import bpm.vanilla.server.client.communicators.ServerConfigInfo;
import bpm.vanilla.server.client.communicators.TaskDatas;

public class ReportProfilingTool {
	
	/*
	 * context
	 */
	private static final String P_REPORT_LOGIN = "vanilla.context.login";
	private static final String P_REPORT_PASSWORD = "vanilla.context.password";
	private static final String P_REPORT_GROUP_ID = "vanilla.context.groupId";
	private static final String P_REPORT_GROUP_NAME = "vanilla.context.groupName";
	private static final String P_VANILLA_RUNTIME_URL = "vanilla.context.vanillaRuntimeUrl";
	
	
	/*
	 * object identifier
	 */
	private static final String P_REPORT_REPOSITORY_ID = "report.repositoryId";
	private static final String P_REPORT_DIRECTORY_ITEM_ID = "report.directoryItemId";
	
	/*
	 * object options
	 */
	private static final String P_REPORT_OUTPUT_FORMAT = "report.outputFormat"; 
	//private static final String P_REPORT_OUTPUT_NAME = "outputName"; // optional means that the object should be stored in GED
	private static final String P_REPORT_PRIORITY = "report.taskPriority";
	
	
	/*
	 * profiling options
	 */
	private static final String P_PROFIING_NUMBER_OF_RUN = "profiling.runNumber";
	private static final String P_PROFILING_RESULT_FILE = "profiling.outputFile";
	private static final String P_PROFILING_CONFIGURATION_FILE = "profiling.configurationFile";
	
	/*
	 * server config options
	 */
	private static final String P_RUNTIME_SIMULTANOUS_TASKS = "vanilla.runtime.configuration.maxRunningTasks";
	private static final String P_RUNTIME_SIMULTANOUS_REPOSITORY_CONNECTION = "vanilla.runtime.configuration.maxRepositoryConnection";
	
	
	
	
	public static void main(String[] args) {
		String configFileName = System.getProperty(P_PROFILING_CONFIGURATION_FILE);
		if (configFileName == null){
			System.err.println("Error : missing system property " + P_PROFILING_CONFIGURATION_FILE);
			
			System.out.println("Using profiling_report.properties file");
			configFileName = "profiling_report.properties";
		}
		
		/*
		 * Load properties
		 */
		
		File f = new File(configFileName);
		
		if (!f.exists() || !f.isFile()){
			System.err.println("Error : " + configFileName + " is not a File or do  ot exists");
			System.exit(-1);
		}
		
		System.out.println("Loading for configuration file : " + configFileName);
		Properties properties = new Properties();
		try{
			properties.load(new FileInputStream(f));
		}catch(Exception ex){
			System.err.print("Error when loading the properties file content.");
			ex.printStackTrace();
			System.exit(-1);
		}
		
		try{
//			checkProperties(properties);
		}catch(Exception ex){
			System.err.print("Error when checking properties : " + ex.getMessage());
			ex.printStackTrace();
			System.exit(-1);
		}
		
		/*
		 * connect to server
		 */
		ServerClient remote = null;
		
		try{
			remote = createRemote(properties);
		}catch(Exception ex){
			System.err.print("Error when creating Runtime client : " + ex.getMessage());
			ex.printStackTrace();
			System.exit(-1);
		}
		
		/*
		 * create Tasks
		 */
		TaskDatas taskDatas = null;
		
		try{
			taskDatas = createTaskDatas(properties);
		}catch(Exception ex){
			System.err.print("Error when creating Task Datas : " + ex.getMessage());
			ex.printStackTrace();
			System.exit(-1);
		}
		
		XlsWriter xlsWriter = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
		File outputFile = new File(properties.getProperty(P_PROFILING_RESULT_FILE) + "_" + sdf.format(new Date()) +   ".xls");
		try{
			
			xlsWriter = new XlsWriter(outputFile);
		}catch(Exception ex){
			System.err.print("Error when creating XLS Writer : " + ex.getMessage());
			ex.printStackTrace();
			System.exit(-1);
		}
		
		List<ServerConfigurationProps> serverConfig = extractServerConfig(properties);
		
		
		ServerConfigInfo initialCOnfiguration = null;
		
		List<ProfilingResult> results = new ArrayList<ProfilingResult>();
		
		try {
			initialCOnfiguration = remote.getServerConfig();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		int numberOfRun = Integer.parseInt(properties.getProperty(P_PROFIING_NUMBER_OF_RUN));
		
		
		
		if (serverConfig.isEmpty()){
			
			ProfilingResult r = launchProfiling(xlsWriter, remote, taskDatas, numberOfRun, null);
			r.dump();
			results.add(r);
		}
		
		else{
			for(ServerConfigurationProps conf : serverConfig){
				/*
				 * configure Server
				 */
				
				
				try{
					System.out.println("Configuring Server for Profiling ");
					ServerConfigInfo cf = conf.update(remote.getServerConfig());
					
					System.out.println("Stopping Server ...");
					remote.stopServer();
					
					System.out.println("Updating Server Configuration with " + conf.toString());
					remote.resetServerConfig(cf);
					
					System.out.println("Restarting Server ...");
					remote.startServer();
					
					
					/*
					 * profiling
					 */
					System.out.println("Run Profiling");
					ProfilingResult r = launchProfiling(xlsWriter, remote, taskDatas, numberOfRun, conf);
					r.dump();
					results.add(r);
					
				}catch(Exception ex){
					System.err.println("Unable to run the profiling : " + ex.getMessage());
					ex.printStackTrace();
				}
				
			}
		}
		
		/*
		 * restore config
		 */
		System.out.println("Restoring initial Configuration");
		try{
			remote.stopServer();
			remote.resetServerConfig(initialCOnfiguration);
			remote.startServer();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		/*
		 * write Final Results
		 */
		xlsWriter.writeResume(results);
		try{
			xlsWriter.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		System.out.println("=========================");
		System.out.println("\n\nFile " + outputFile.getAbsolutePath() + " created!");
		System.out.println("Profiling Ended!!");
	}
	
	
	private static List<ServerConfigurationProps> extractServerConfig(Properties properties) {
		String buf = properties.getProperty(P_RUNTIME_SIMULTANOUS_TASKS);
		Integer[] taskNumber = null;
		Integer[] connectionNumber = null;
		
		if (buf != null){
			String[] ss = buf.split(",");
			taskNumber = new Integer[ss.length];
			for(int i = 0; i < ss.length; i++){
				taskNumber[i] = Integer.parseInt(ss[i].trim());
			}
			
			
		}
		
		buf = properties.getProperty(P_RUNTIME_SIMULTANOUS_REPOSITORY_CONNECTION);
		
		if (buf != null){
			String[] ss = buf.split(",");
			connectionNumber = new Integer[ss.length];
			for(int i = 0; i < ss.length; i++){
				connectionNumber[i] = Integer.parseInt(ss[i].trim());
			}
		}
		
		List<ServerConfigurationProps> confs = new ArrayList<ServerConfigurationProps>();
		
		if (taskNumber != null && connectionNumber != null){
			
			for(Integer t : taskNumber){
				for(Integer k : connectionNumber){
					confs.add(new ServerConfigurationProps(t, k));
				}
			}
		}
		else if (taskNumber != null){
			for(Integer k : taskNumber){
				confs.add(new ServerConfigurationProps(k, null));
			}
		}
		else if (connectionNumber != null){
			for(Integer k : connectionNumber){
				confs.add(new ServerConfigurationProps(null, k));
			}
		}
		
		return confs;
	}


	private static TaskDatas createTaskDatas(Properties properties) {
		Properties p = new Properties();
		
		p.setProperty("directoryItemId", properties.getProperty(P_REPORT_DIRECTORY_ITEM_ID));
		p.setProperty("groupId", properties.getProperty(P_REPORT_GROUP_ID));
		p.setProperty("groupName", properties.getProperty(P_REPORT_GROUP_NAME));
		p.setProperty("login", properties.getProperty(P_REPORT_LOGIN));
		p.setProperty("password", properties.getProperty(P_REPORT_PASSWORD));
		p.setProperty("repositoryId", properties.getProperty(P_REPORT_REPOSITORY_ID));
	
		p.setProperty("taskPriority", properties.getProperty(P_REPORT_PRIORITY));
		p.setProperty("outputFormat", properties.getProperty(P_REPORT_OUTPUT_FORMAT));
		
		
		TaskDatas datas = new TaskDatas("", "", p, new Properties(), 8, 2);
		return datas;
	}


	private static ServerClient createRemote(Properties properties) {
		ServerClient remote = new ServerClient(ServerType.REPORTING, "", 
				properties.getProperty(P_VANILLA_RUNTIME_URL),
				properties.getProperty(P_REPORT_LOGIN),
				properties.getProperty(P_REPORT_PASSWORD));
		return remote;
	}

	
	
	private static ProfilingResult launchProfiling(XlsWriter xlsWriter, ServerClient remote, TaskDatas taskDatas, int numberOfRun, ServerConfigurationProps conf){
		/*
		 * launchTasks
		 */
		final List<TaskRunner> taskProfilers = new ArrayList<TaskRunner>();
		
		
		
		
		for(int i = 0; i < numberOfRun; i++){
			taskProfilers.add(new TaskRunner(remote, taskDatas, i));
		}
		
		System.out.println("Start Profiling");
		
		for(TaskRunner r : taskProfilers){
			r.start();
		}
		
		
		/*
		 * wait end Tasks
		 */
		
		for(TaskRunner r : taskProfilers){
			try{
				r.join();
			}catch(Exception ex){
				System.out.println("Error when waiting for a task to end - " + ex.getMessage());
				ex.printStackTrace();
			}
			if (r.hasProfilingSucceed()){
				System.out.println(r.getIdentification() + " profiling succeed");
			}
			else{
				System.out.println(r.getIdentification() + " profiling failed");
			}
		}
		
		/*
		 * generate ResultFile
		 */
		File f = null;
		if (conf == null){
			f = new File("reporting.csv");
		}
		else{
			f = new File(conf.toString() + "reporting.csv");
		}
		return generateResultFile(xlsWriter, taskProfilers, conf);
	}
	

	private static ProfilingResult generateResultFile(XlsWriter xlsWriter, List<TaskRunner> taskProfilers, ServerConfigurationProps conf){
		
		Object[]  values = new Object[]{new ArrayList<Long>(), new ArrayList<Long>(), new ArrayList<Long>()};
		
		try{
			xlsWriter.writeSheet(conf.toString(), taskProfilers);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		for(TaskRunner t : taskProfilers){
			
			((List)values[0]).add(t.getSubmissionTime());
			try{
				long l = t.getTaskInfo().getStoppedDate().getTime() - t.getTaskInfo().getStartedDate().getTime();
				((List)values[1]).add(l);
			}catch(Exception ex){
				
			}
			
			try{
				long l = t.getTaskInfo().getStartedDate().getTime() - t.getTaskInfo().getCreationDate().getTime();
				((List)values[2]).add(l);
			}catch(Exception ex){

			}
			
			
		}
		/*
		 * compute results
		 *		 */
		double[] val = new double[]{0,0,0};
		for(int i = 0; i < 3; i++){
			
			for(Long l : (List<Long>)values[i]){
				val[i] += l;
			}
			
			val[i] = val[i] / ((List<Long>)values[i]).size();
		}
		
		return new ProfilingResult(val[0], val[1], val[2], conf.numberTasks, conf.numberRepConnection);
	}
}
