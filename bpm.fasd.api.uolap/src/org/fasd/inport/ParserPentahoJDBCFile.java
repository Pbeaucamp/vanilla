package org.fasd.inport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import org.eclipse.core.runtime.Platform;
import org.fasd.sql.SQLConnection;

import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;


public class ParserPentahoJDBCFile {
	private ArrayList<SQLConnection> listConnection = new ArrayList<SQLConnection>();
	private String path;
	private boolean parsed = false;
	
	public ParserPentahoJDBCFile(String path){
		this.path = path;
	}
	
	
	public ArrayList<SQLConnection> getListConnection() throws Exception{
		if (!parsed)
			parse();
		return listConnection;
	}
	
	private void parse() throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line, fileDriver= "";
		String dbname= "", type = "", driver = "", url ="", user ="", pwd ="";
//		DigesterDBXML dig = new DigesterDBXML(Platform.getInstallLocation().getURL().getPath() + IConstants.getJdbcDriverXmlFile());
//		ListDriver.getInstance() list = new ListDriver();
//		
//		dig.getListDriver();
		
		
		
		while((line = reader.readLine()) != null){
			if (!line.startsWith("#") || line.startsWith(" ")){
				type = line.substring(line.indexOf("/type=") + 6);
				line = reader.readLine();
				fileDriver = line.substring(line.indexOf("/driverFile=") + 12);
				line = reader.readLine();
				driver = line.substring(line.indexOf("/driver=") + 8);
				line = reader.readLine();
				url = line.substring(line.indexOf("/url=") + 5);
				line = reader.readLine();
				user = line.substring(line.indexOf("/user=") + 6);
				line = reader.readLine();
				pwd = line.substring(line.indexOf("/password=") + 10);
				dbname = line.substring(0, line.indexOf("/password=") );
				for(DriverInfo d : ListDriver.getInstance(bpm.studio.jdbc.management.config.IConstants.getJdbcDriverXmlFile() ).getDriversInfo()){
					if (driver.equals(d.getClassName())){
						fileDriver = d.getFile();
						listConnection.add(new SQLConnection(url, user, pwd, fileDriver, driver, dbname, type));
						break;
					}
						
				}
				
			}
		}
		
		reader.close();
	}
	
	public static void main(String[] arg) throws Exception{
		ParserPentahoJDBCFile parser = new ParserPentahoJDBCFile("c:\\jdbc.properties");
		
		for(SQLConnection c : parser.getListConnection()){
			System.out.println("driver " + c.getDriverName());
			System.out.println("driverFile " + c.getDriverFile());
			System.out.println("user " + c.getUser());
			System.out.println("pass " + c.getPass());
			System.out.println("url " + c.getUrl());
			System.out.println("========");
		}
	}
	

}
