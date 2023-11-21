package bpm.gateway.runtime2.transformations.inputs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.gateway.core.transformations.inputs.FMDTInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.query.IQuery;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IRepositoryContext;

public class RunFMDTInput extends RuntimeStep{
	private VanillaJdbcConnection socket;
	private VanillaPreparedStatement stmt;
	private ResultSet resultSet;
	
	
	public RunFMDTInput(IRepositoryContext repCtx, FMDTInput transformation, int bufferSize) {
		super(repCtx, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		FMDTInput transfo = (FMDTInput)this.getTransformation();
		
		/*
		 * load FMDT Objects : model, package, connection, query
		 */
		Collection<IBusinessModel> models = transfo.getDocument().getFMDTHelper().getFmdtModel(transfo);
		
		info(" FMDT model loaded");
		
		IBusinessPackage pack = null;
		IBusinessModel model = null;
		for(IBusinessModel m : models){
			if (m.getName().equals(transfo.getBusinessModelName())){
//				error( " FMDT Businessmodel " + m.getName() + " not found");
				model = m;
				for(IBusinessPackage p : m.getBusinessPackages("none")){
					if (p.getName().equals(transfo.getBusinessPackageName())){
						pack = p;
						break;
					}
				}
				break;
			}
		}
		if (model == null){
			error(" FMDT BusinessModel " + transfo.getBusinessModelName() + " not found");
			throw new Exception("Unable to find BusinessModel");
		}
		if (pack == null){
			error(" FMDT BusinessPackage " + pack.getName() + " not found");
			throw new Exception("Unable to find BusinessPackage in BusinessModel");
		}
		else{
			info( " FMDT BusinessPackage " + pack.getName() + " found");
		}
		
		
		
		IQuery query = transfo.getQueryFmdt();
		List<List<String>> fmdtValues = new ArrayList<List<String>>();
		for(Prompt p : query.getPrompts()){
			List<String> pValues = new ArrayList<String>();
			for(String  k : transfo.getPromptValues(p.getName())){
				pValues.add(transfo.getDocument().getStringParser().getValue(transfo.getDocument(), k));
			}
			fmdtValues.add(pValues);
		}
		info(" query parameter defined");
		
		SQLConnection _con = (SQLConnection)pack.getConnection(null, transfo.getConnectioName());
		
		if (_con == null){
			error( " connection not found");
			throw new Exception("Unable to find SQLConnection for this package");
		}
		else{
			info(" connection found");
			socket = _con.getJdbcConnection();
		}
		
		/*
		 * load JDBC Objects
		 */
//		socket = _con.getJdbcConnection();
		
		try{
			
			info("creating statement in streamMode");
			stmt = socket.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			
			stmt.setFetchSize(Integer.MIN_VALUE);
			
			info(" DataBase connection created");
			
		}catch(SQLException e){
			
			stmt = socket.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			
			try{
				stmt.setFetchSize(1);
				info(" DataBase connection created");
			}catch(Exception ex){
				stmt.setFetchSize(0);
				info(" DataBase connection created");
			}
			
		}catch(Exception e){
			error(" DataBase connection failed", e);
			throw e;
		}
		
		/*
		 * runQuery
		 */
		
		String _sqlquery = null;
		try{
			_sqlquery = pack.getQuery(null, getRepositoryContext().getVanillaContext(), query, fmdtValues);
			_sqlquery = _sqlquery.replace("`", "\"");
			info(" SQLQuery = " + _sqlquery);
		}catch(Exception e){
			error(" unable to build SQL query from Metadata query :\n" + query.getXml());
			throw e;
		}
		 
		
		
		try{
			resultSet =  stmt.executeQuery(_sqlquery);
			info(" Run query : " + query);
		}catch(Exception ex){
			error(" Failed query : " + query, ex);
		}
		
		isInited = true;

	}

	@Override
	public void performRow() throws Exception {
		if (resultSet == null){
			throw new Exception("No ResultSet defined");
		}
		
		if (resultSet.next()){
			Row row = RowFactory.createRow(this);
			
			for(int i = 1; i <= row.getMeta().getSize(); i++){
				Object o = resultSet.getObject(i);
				row.set(i - 1, o);
			}
			
			writeRow(row);
		}
		else{
			if (!areInputsAlive()){
				if (areInputStepAllProcessed()){
					if (inputEmpty()){
						setEnd();
					}
				}
			}
			
		
			
		}
		
	}

	@Override
	public void releaseResources() {
		if (resultSet != null){
			
			try {
				resultSet.close();
				resultSet = null;
				info(" closed resultSet");
			} catch (SQLException e) {
				error(" error when closing resultSet", e);
			}
		}
		
		if (stmt != null){
			try {
				stmt.close();
				stmt = null;
				info(" closed Statement");
			} catch (Exception e) {
				error(" error when closing Statement", e);
			}
		}
		
		if (socket != null){
			try {
				ConnectionManager.getInstance().returnJdbcConnection(socket);
				socket = null;
				info(" closed Connection");
			} catch (Exception e) {
				error(" error when closing Connection", e);
			}
		}
		
	}
	
}
