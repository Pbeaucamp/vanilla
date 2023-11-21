package bpm.gateway.runtime2.transformation.mapping;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.eclipse.datatools.modelbase.sql.query.QueryStatement;
import org.eclipse.datatools.modelbase.sql.query.TableInDatabase;
import org.eclipse.datatools.modelbase.sql.query.helper.StatementHelper;
import org.eclipse.datatools.sqltools.parsers.sql.query.SQLQueryParserManager;
import org.eclipse.datatools.sqltools.parsers.sql.query.SQLQueryParserManagerProvider;
import org.fasd.datasource.DataObjectOda;
import org.fasd.datasource.DatasourceOda;
import org.fasd.olap.FAModel;
import org.fasd.olap.ICube;
import org.fasd.olap.OLAPCube;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.transformations.olap.OlapOutput;
import bpm.gateway.core.transformations.outputs.IOutput;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.transformations.outputs.RunDataBaseOutput;
import bpm.studio.jdbc.management.model.DriverInfo;

public class RunOlapOutput extends RunDataBaseOutput{
	private String targetTableName;
	
	public RunOlapOutput(OlapOutput transformation, int bufferSize) {
		super(transformation, bufferSize);
		
	}
	@Override
	public void init(Object adapter) throws Exception {
		OlapOutput tr = (OlapOutput)getTransformation();
		FAModel model = null;
		try {
			model = tr.getDocument().getOlapHelper().getModel(tr);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
		
		OLAPCube cube = null;
		for(ICube c : model.getCubes()){
			if ( c instanceof OLAPCube && c.getName().equals(tr.getCubeName())){
				cube = (OLAPCube)c;
				break;
			}
		}
		
		if (cube == null){
			throw new Exception("Cube not found in model");
		}
		
		DataObjectOda factTable = (DataObjectOda)cube.getFactDataObject();
		if (!((DatasourceOda)factTable.getDataSource()).getOdaExtensionId().equals("org.eclipse.birt.report.data.oda.jdbc")){
			throw new Exception("Only Cube with FactTable using the OdaDriver org.eclipse.birt.report.data.oda.jdbc support this step");
		}
		DatasourceOda dataSource = (DatasourceOda)factTable.getDataSource();
		
		String jdbcDriverClass = dataSource.getPublicProperties().getProperty("odaDriverClass");
		String login = dataSource.getPublicProperties().getProperty("odaUser");
		String password = dataSource.getPublicProperties().getProperty("odaPassword");
		String url = dataSource.getPublicProperties().getProperty("odaURL");
		
		DriverInfo driverInfo = null;
				
	
		for(String s : JdbcConnectionProvider.getListDriver().getDriversName()){
			DriverInfo inf = JdbcConnectionProvider.getListDriver().getInfo(s);
			if (inf.getClassName().equals(jdbcDriverClass)){
				driverInfo = inf; 
				break;
			}
			
		}
		
		if (driverInfo == null){
			throw new Exception("Unable to find a registered driver using the class " + jdbcDriverClass + ".You shold add an entry in the jdbcDriver.xml file");
		}
		
		
		try{
			SQLQueryParserManager manager = SQLQueryParserManagerProvider.getInstance().getParserManager(null, null);
			QueryStatement factStmt = manager.parseQuery(factTable.getQueryText()).getQueryStatement();
			
			List tables = StatementHelper.getTablesForStatement(factStmt);
			if (tables.size() > 1 ){
				throw new Exception("The FactTable is defined by an SQL query using more than one table. Inserting datas within multiple tables is impossible");
			}
			
			targetTableName = ((TableInDatabase)tables.get(0)).getName();
		}catch (Throwable e) {
			e.printStackTrace();
			error("Failed to init - " + e.getMessage(), e);
			throw new Exception(e.getMessage(), e);
		}
		
		
		
		try{
			sqlConnection =JdbcConnectionProvider.createSqlConnection(getTransformation().getDocument(), driverInfo.getName(), url, login, password);
			sqlConnection.setAutoCommit(false);
		}catch(Exception ex){
			error("Unable to create the Sql connection - " + ex.getMessage(), ex);
		}
		
		
		
		
		if (sqlConnection == null){
			throw new Exception("Unable to create SQL Connection");
		}
		//sqlConnection.setAutoCommit(false);
		for(Transformation in : getTransformation().getInputs()){
			String query = prepareQuery(in);
			queries.put(in, query);
			PreparedStatement stmt = sqlConnection.prepareStatement(query);
			info(" created prepared Statement for " + in.getName());
			info( " statement query for  " + in.getName() + " : " + query);

			statements.put(in, stmt);
			datasMap.put(in, new ArrayBlockingQueue<Row>(100));
		
		}
		
		info(" inited");
		isInited = true;
		commitStmt = sqlConnection.createStatement();
		info(" created prepared Statement for commit");

	}
	
	@Override
	protected String prepareQuery(Transformation tr) throws Exception{
		/*
		 * insertion query creation
		 * 
		 * we will use some prepared statement JDBC features
		 */
		StringBuffer insertionBaseSql = new StringBuffer();
		insertionBaseSql.append("insert into ");
		insertionBaseSql.append(targetTableName);
		insertionBaseSql.append("(");
		
		IOutput db = (IOutput)getTransformation();
		
		boolean isFirst = true;
		StreamDescriptor desc = getTransformation().getDescriptor(getTransformation());
		for(StreamElement e : desc.getStreamElements()){
			HashMap<String, String> maps = db.getMappingsFor(tr);
			for(String key : maps.keySet()){
				if (key.equals(e.getFullName())){
					if (isFirst){
						isFirst = false;
					}
					else{
						insertionBaseSql.append(", ");
					}
					insertionBaseSql.append("\"" + e.name + "\"");
				}
				
			}
			
			
			
		}
		
		insertionBaseSql.append(") values(");
		isFirst = true;
		for(int i = 0; i < db.getMappingsFor(tr).size(); i++){
			if (isFirst){
				isFirst = false;
			}
			else{
				insertionBaseSql.append(", ");
			}
			
			insertionBaseSql.append("?");
		}
		insertionBaseSql.append(")");
		
		
		return insertionBaseSql.toString();
		
	}
}
