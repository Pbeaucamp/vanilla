package bpm.gateway.core.samples;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseFactory;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.studio.jdbc.management.config.IConstants;

/**
 * This sample show how to create a simple Gateway Model from the api that read Datas from
 * a SQL Database et write it in a CSV File
 * @author LCA
 *
 */
public class LoadInsertSqlSample {

	/**
	 * 
	 * @return a newly created GatewayModel
	 */
	protected DocumentGateway createDocument(){
		
		DocumentGateway doc = new DocumentGateway();
		doc.setAuthor("LCA");
		doc.setCreationDate(Calendar.getInstance().getTime());
		doc.setName("LoadInsertSql Sample");
		return doc;
	}
	
	
	/**
	 * create a DataBase server and register it in the resource Manager
	 * @throws Exception
	 */
	protected void createResources() throws Exception{
		DataBaseConnection socket = new DataBaseConnection();
		socket.setName("SqlSampledataConnection");
		socket.setDataBaseName("sampledata");
		
		/*
		 * The driver Name come from the file driverJdbc.xml
		 */
		socket.setDriverName("MySql");
		socket.setHost("localhost");
		socket.setPort("3306");
		socket.setLogin("root");
		socket.setPassword("root");
		

		//create the DataBaseServer
		DataBaseServer sqlDatabase = DataBaseFactory.create("sqlDatabase", "", socket);
		
		//register the server in the manager, if the connection is not opened, it will be			
		ResourceManager.getInstance().addServer(sqlDatabase);
		
	}
	
	
	protected void createModelContent(DocumentGateway model){
		
		/*
		 *extract datas from SQL 
		 */
		DataBaseInputStream dbIs = new DataBaseInputStream();
		dbIs.setName("Extract Datas From Table customers");
		dbIs.setPositionX(50);
		dbIs.setPositionY(50);
		
		dbIs.setServer(ResourceManager.getInstance().getServer("sqlDatabase"));
		dbIs.setDefinition("SELECT customers.CUSTOMERNUMBER, customers.CUSTOMERNAME, customers.CONTACTLASTNAME, customers.CONTACTFIRSTNAME, customers.STATE, customers.CITY, customers.COUNTRY FROM customers");
		dbIs.setTemporaryFilename(ResourceManager.getInstance().getParameter(ResourceManager.VAR_GATEWAY_TEMP) + dbIs.getName());
		
		
		
		
		/*
		 * write datas into CSV file
		 */
		FileOutputCSV outputCsv = new FileOutputCSV();
		outputCsv.setName("insert datas into a file");
		outputCsv.setPositionX(100);
		outputCsv.setPositionY(50);
		outputCsv.setTemporaryFilename(ResourceManager.getInstance().getParameter(ResourceManager.VAR_GATEWAY_TEMP) + outputCsv.getName());
		outputCsv.setDefinition("samples/output-files/customers.txt");
		outputCsv.setDelete(true);
		outputCsv.setSeparator(';');
		outputCsv.setAppend(false);

		
		/*
		 * add Transformation to the model
		 */
		model.addTransformation(dbIs);
		model.addTransformation(outputCsv);
		
		/*
		 * link the transformations
		 */
		dbIs.addOutput(outputCsv);
	}
	
	
	protected void init(){
		//init
//		JdbcConnectionProvider.init(IConstants.getJdbcJarFolder(), IC);
//		
//		// same as the previous but needed by the FMDT API
//		// the ressources/jdbc , resources/driverjdbc.xml string are hardcoded for now in this api

		
		
		//init the GATEWAY_HOME 
		ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_BIGATEWAY_HOME).setValue("");
		
		//add the variable TEMP folder
		
		ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP);
		Variable GATEWAY_TEMP = ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP);
		GATEWAY_TEMP.setValue("temp");
		
	
		// create the temp folder
		File f = new File(GATEWAY_TEMP.getValueAsString());
		if (!f.exists()){
			
			try {
				f.createNewFile();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		
			
		LoadInsertSqlSample sample = new LoadInsertSqlSample();
		
		sample.init();
		
		try {
			sample.createResources();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		DocumentGateway doc = sample.createDocument();
		sample.createModelContent(doc);
		
		
		/*
		 * dump the XML of the model in the console
		 */
		System.out.println(doc.getElement().asXML());;
		
	}
}
