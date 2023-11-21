package bpm.vanilla.server.client.commandline;

import java.text.SimpleDateFormat;
import java.util.Properties;

import bpm.vanilla.server.client.ServerClient;
import bpm.vanilla.server.client.communicators.TaskDatas;
import bpm.vanilla.server.client.communicators.gateway.GatewayServerClient;

public class RunSpecificTask {
	private static ServerClient server; 
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private static final String[] commands = {};
	
	
	private static final String HELP =
	
	"arguments : \n " +
	"\t-0 run or ? \n"+
	"\t-1 serverUrl\n"+
	"\t-2 vanillaRepositoryId\n" + 
	"\t-3 directoryItemId\n" +
	"\t-4 login\n" +
	"\t-5 password\n" +
	"\t-6 vanillaGroupName\n" +
	"\t-7 priority\n" + 
	"\t-8 to n overriden properties name with their values (optional)\n\n\n" + 
	
	"syntaxe sample:\n" + 
	"run http://localhost:800/FdDeployer 1 13 system system System -> \n" + 
	"\t run the gateway with id=13 from the Repository with id=1 for the user system/system for its group System\n\n" +
	"*** supported additional properties ***\n"+
	"DATABASE_CONNECTION.{dataSourceName}.database\n"+
	"DATABASE_CONNECTION.{dataSourceName}.port\n"+
	"DATABASE_CONNECTION.{dataSourceName}.login\n"+
	"DATABASE_CONNECTION.{dataSourceName}.password\n"+
	"DATABASE_CONNECTION.{dataSourceName}.driverName\n"+
	"\n with {dataSourceName} replaced by the name of an SQLDataSOurce within the Gateway model definition";
	
	public static void main(String[] args) {
		if (args.length < 1){
			System.err.println("No command specified in command, check syntax with the ? parameter");
			return;
		}
		String command = args[0];
		if ("?".equals(command)){
			System.out.println(HELP);
			return;
		}
		
		if (args.length < 8){
			System.err.println("Arguments are missing, check syntax with the ? parameter");
			return;
		}
		
		String serverUrl = args[1];
		String repositoryId = args[2];
		String directoryItemId = args[3];
		
		String login = args[4];
		String password = args[5];
		String groupName = args[6];
		String priority = args[7];
		
		GatewayServerClient server = new GatewayServerClient("", serverUrl,
				login, 
				password);
		
		try{
			if (!server.isServerRunning()){
				System.out.println("GatewayServer is not running. Please start the server first.");
				return;
			}
		}catch(Exception ex){
			System.err.println("Unable to contact the server");
			ex.printStackTrace();
			return;
		}
		//TODO : missing parameters
		
		/*
		 * 
		 */
		Properties prop = new Properties();
		prop.setProperty("repositoryId",repositoryId);
		prop.setProperty("login", login);
		prop.setProperty("password", password);
		
		if (password.matches("[0-9a-f]{32}")){
			prop.setProperty("encrypted", "true");
		}
		else{
			prop.setProperty("encrypted", "false");
		}
		prop.setProperty("directoryItemId", directoryItemId);
		
		prop.setProperty("groupName", groupName);
		prop.setProperty("taskPriority", priority);
		
		
		if (args.length >= 9){
			String[] overriden = args[8].split(",");
			for(String couple : overriden){
				String[] splits = couple.split("=");
				if (splits.length == 2 && splits[0].startsWith("DATABASE_CONNECTION.")){
					System.out.println("Additional Properties found : " + splits[0] + "=" + splits[1]);
					prop.setProperty(splits[0], splits[1]);
				}
			}
		}
		
			
			
		
		
		TaskDatas taskDatas = new TaskDatas("", "", prop, new Properties(), 14, -1);
		System.out.println(taskDatas.getTaskProperties());
		try {
			int id = server.launchTask(taskDatas);
			System.out.println("Server registered the task with an id = " + id);
		} catch (Exception e) {
			System.err.println("Unable to send the task to the server");
			e.printStackTrace();
		}
		
		
	}
	
}
