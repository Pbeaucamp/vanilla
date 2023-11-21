package bpm.vanilla.server.client.commandline;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.beans.tasks.TaskList;
import bpm.vanilla.platform.core.beans.tasks.TaskListParser;
import bpm.vanilla.server.client.ServerClient;
import bpm.vanilla.server.client.communicators.TaskDatas;
import bpm.vanilla.server.client.communicators.TaskInfo;
import bpm.vanilla.server.client.communicators.TaskMeta;
import bpm.vanilla.server.client.communicators.freemetadata.FmdtServerClient;
import bpm.vanilla.server.client.communicators.gateway.GatewayServerClient;

public class ReportingServer {

	private static ServerClient server; 
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	private static final String[] commands = new String[]{"start", "stop", "run",  "?", "list", "detail"};
	
	private static final String HELP = " start SERVERTYPE http://ip:port/name login password<-> start the server at the url\n" +
	"stop SERVERTYPE http://ip:port/name  login password<-> start the server at the url : stop the server at the given url\n" +
	"run SERVERTYPE http://ip:port/name login password SERVERTYPE tasklist.xml groupName/groupName2.../groupeNamen <-> run the given taskList file for each given groups\n"+
	"list SERVERTYPE http://ip:port/name  login password<-> dump all tasks present on the server\n" + 
	"detail SERVERTYPE http://ip:port/name  login password taskId <-> dump task definition, state and parameters for thie given taskid\n\n";
	
	/**
	 * @param args : 
	 *  - server Url
	 *  - command : start, stop, run, state
	 *  - runFileLocation
	 *  - returnFileLocation
	 *  
	 */
	public static void main(String[] args) {
		String command = args[0];
		String servertype = args[1];
		String login = args[2];
		String password = args[3];
		ServerType type = ServerType.valueOf(servertype);
		
		if (commands[0].equals(command)){
			System.out.println("****** Starting Server ********");
			try {
				if (args.length < 4){
					System.err.println("Error : Missing ServerType argument");
					return;
				}
				if (args.length < 5){
					System.err.println("Error : Missing Url argument");
					return;
				}
				String serverUrl = args[4];
				
				server = createServer(type, serverUrl, login, password);
				System.out.println(startServer());
				
			} catch (Exception e) {
				
				e.printStackTrace();
				System.err.println("Error when starting Server :" + e.getMessage());
			}
		}
		else if (commands[1].equals(command)){
			System.out.println("****** Stopping Server ********");
			if (args.length < 4){
				System.err.println("Error : Missing ServerType argument");
				return;
			}
			if (args.length < 5){
				System.err.println("Error : Missing Url argument");
				return;
			}
			try {
				String serverUrl = args[2];
				server = createServer(type, serverUrl, login, password);
				System.out.println(stopServer());
			} catch (Exception e) {
				
				e.printStackTrace();
				System.err.println("Error when starting Server :" + e.getMessage());
			}
		}
		else if (commands[2].equals(command)){
			System.out.println("****** Launch TaskList ********");
			if (args.length < 4){
				System.err.println("Error : Missing ServerType argument");
				return;
			}
			if (args.length < 5){
				System.err.println("Error : Missing Url argument");
				return;
			}
			if (args.length < 6){
				System.err.println("Error : TaskList file path argument");
				return;
			}
			if (args.length < 7){
				System.err.println("Error : Missing Groups arguments");
				return;
			}
			
			String serverUrl = args[2];
			
			try {
				server = createServer(type, serverUrl, login, password);
			} catch (Exception e1) {
				e1.printStackTrace();
				System.err.println("Cannot create serverRemote");
				return;
			}
			String listTaskFile = args[5];
			String[] groups = args[6].split("\\/");
//			try {
//				
//				runTaskList(listTaskFile, groups);
//			} catch (Exception e) {
//				e.printStackTrace();
//				System.err.println("Problem when trying to launch task List");
//			}
			throw new RuntimeException("TaskList not supported");
		}
		else if (commands[3].equals(command)){
			System.out.println("****** Help ********");
			System.out.println(HELP);
		}
		else if (commands[4].equals(command)){
			System.out.println("****** List Server Content ********");
			if (args.length < 4){
				System.err.println("Error : Missing ServerType argument");
				return;
			}
			if (args.length < 5){
				System.err.println("Error : Missing Url argument");
				return;
			}
			String serverUrl = args[2];
			
			try {
				server = createServer(type, serverUrl, login, password);
			} catch (Exception e1) {
				e1.printStackTrace();
				System.err.println("Cannot create serverRemote");
				return;
			}
			try{
				for(TaskInfo inf : server.getTaskInfos()){
					dumpInfo(inf);
				}
			}catch(Exception ex){
				ex.printStackTrace();
				System.err.println("Error when dumping infos");
			}
				
		}
		else if (commands[5].equals(command)){
			System.out.println("****** Task Detail ********");
			String serverUrl = args[2];
			if (args.length < 4){
				System.err.println("Error : Missing ServerType argument");
				return;
			}
			if (args.length < 5){
				System.err.println("Error : Missing Url argument");
				return;
			}
			if (args.length < 6){
				System.err.println("Error : Missing Task Id argument");
				return;
			}
			Integer id = null;
			
			try{
				id = Integer.parseInt(args[5]);
			}catch(Exception ex){
				System.err.println("TaskId arg not a number : " +args[5]);
				return;
			}
			
			
			try{
				server = createServer(type, serverUrl, login, password);
				dumpInfo(server.getTaskInfo(id));
				dumpMeta(server.getTaskDefinition(id));
			}catch(Exception ex){
				ex.printStackTrace();
				System.err.println("Error when dumping infos");
			}
				
		}
		else{
			System.err.println("Command named " + command + " not supported");
		}

	}

	private static ServerClient createServer(ServerType type, String url, String login, String password) throws Exception{
		switch(type){
		case REPORTING:
			return new ServerClient(ServerType.REPORTING, "server", url, login, password);
		case GATEWAY:
			return new GatewayServerClient("", url, login, password);
		case FREEMETADATA:
			return new FmdtServerClient("", url, login, password);
		case DISCONNECTED:
			return new ServerClient(ServerType.DISCONNECTED, "server", url, login, password);			
		}
		throw new Exception("unknown server type");
	}
	
	private static void dumpMeta(TaskMeta meta) {
		Enumeration en = meta.getProperties().propertyNames();
		System.out.println("\t *** Properties ***");
		while(en.hasMoreElements()){
			String pname = (String)en.nextElement();
			System.out.println("\t-" + pname + "=" + meta.getProperties().getProperty(pname));
		}
		
		en = meta.getParameters().propertyNames();
		System.out.println("\t *** Parameters ***");
		while(en.hasMoreElements()){
			String pname = (String)en.nextElement();
			System.out.println("\t-" + pname + "=" + meta.getParameters().getProperty(pname));
		}
		
	}


	private static String startServer() throws Exception{
		server.startServer();
		return "Server Started";
	}

	private static String stopServer() throws Exception{
		server.stopServer();
		return "Server Stopped";
	}
	
//	private static void runTaskList(String fileName, String[] groups) throws Exception{
//		FileInputStream fis = new FileInputStream(fileName);
//		TaskList list = TaskListParser.parse(fis);
//		
//		for(TaskDatas td : list.getTasks()){
//			for(String s : groups){
//				td.getTaskProperties().setProperty("groupName", s);
//				
//				try{
//					server.launchTask(td);
//					System.out.println("Launched Object named " + td.getName() + " for Group " + s);
//				}catch(Exception ex){
//					ex.printStackTrace();
//					System.err.println("Error to launch Object named " + td.getName() + " for Group " + s);
//				}
//				
//			}
//		}
//		
//		for(TaskInfo inf : server.getTaskInfos()){
//			dumpInfo(inf);
//		}
//		
//	}
	
	
	
	
	private static void dumpInfo(TaskInfo inf){
		System.out.println("***** Task ******");
		System.out.println("\t-id : " + inf.getId());
		System.out.println("\t-priority : " + inf.getPriority());
		System.out.println("\t-Profil : " + inf.getGroupName());
		switch(inf.getState()){
		case TaskInfo.STATE_ENDED:
			System.out.println("\t-state : Finished");
			break;
		case TaskInfo.STATE_RUNNING:
			System.out.println("\t-state : Running");
			break;
		case TaskInfo.STATE_WAITING:
			System.out.println("\t-state : Waiting");
			break;
			
		}
		
		switch(inf.getResult()){
		case TaskInfo.RESULT_FAILED:
			System.out.println("\t-result : Failed");
			break;
		case TaskInfo.RESULT_SUCCEED:
			System.out.println("\t-result : Succeeded");
			break;
		case TaskInfo.RESULT_UNDEFINED:
			System.out.println("\t-result : Undefined");
			break;
			
		}
		System.out.println("\t-Created : " + sdf.format(inf.getCreationDate()));
		
		if (inf.getStartedDate() == null){
			System.out.println("\t-Started : " );
		}
		else{
			System.out.println("\t-Started : " + sdf.format(inf.getStartedDate()));
		}
		
		if (inf.getStoppedDate() == null){
			System.out.println("\t-Finished : " );
		}
		else{
			System.out.println("\t-Finished : " + sdf.format(inf.getStoppedDate()));
		}
	}
}
