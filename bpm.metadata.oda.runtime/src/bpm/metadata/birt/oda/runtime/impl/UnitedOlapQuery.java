package bpm.metadata.birt.oda.runtime.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.SortSpec;
import org.eclipse.datatools.connectivity.oda.spec.QuerySpecification;

import bpm.metadata.MetaData;
import bpm.metadata.MetaDataBuilder;
import bpm.metadata.birt.oda.runtime.Activator;
import bpm.metadata.digester.SqlQueryDigester;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.query.EffectiveQuery;
import bpm.metadata.query.FmdtQueryRuntimeException;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.FMDTQueryBean;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class UnitedOlapQuery implements IQuery {

	private int m_maxRows;
	
	protected IBusinessPackage fmdtPackage;
//	private String connectionName;
	private Group group;
	private bpm.metadata.query.UnitedOlapQuery fmdtQuery;
	private List<List<String>> promptValues;

	private UnitedOlapConnection fmdtConnection;
	
	public UnitedOlapQuery(UnitedOlapConnection connection, IBusinessPackage pack/*, String connectionName*/, Group group) {
		this.fmdtConnection = connection;
		this.fmdtPackage = pack;
//		this.connectionName = connectionName;
		this.group = group;
	}
	
	@Override
	public void cancel() throws OdaException, UnsupportedOperationException {
		
	}

	@Override
	public void clearInParameters() throws OdaException {
		promptValues.clear();
        for(Prompt p : fmdtQuery.getPrompts()){
			promptValues.add(new ArrayList<String>());
		}
	}

	@Override
	public void close() throws OdaException {
		
		
	}

	@Override
	public IResultSet executeQuery() throws OdaException {
		FMDTQueryBean logBean = null;
		try{
			
			/**
			 * ere added
			 */
			synchronized (fmdtPackage.getBusinessModel().getModel()) {
				MetaData model = fmdtPackage.getBusinessModel().getModel();
				if (!model.isBuilt()){
					MetaDataBuilder builder = new MetaDataBuilder(fmdtConnection.getRepositoryConnection());
					builder.build(fmdtPackage.getBusinessModel().getModel(), fmdtConnection.getRepositoryConnection(), group.getName());
					if (!"".equals(builder.getErrorsBuffer())){
						Activator.getLogger().error(builder.getErrorsBuffer(), null);
					}
				}
			}
			Activator.getLogger().info("building query : ");
			logBean = new FMDTQueryBean();
			logBean.setFmdtQuery(fmdtQuery.getXml());
			logBean.setVanillaGroupId(group.getId());
			logBean.setRepositoryId(fmdtConnection.getVanillaRepository().getId());
			logBean.setDirectoryItemId(fmdtConnection.directoryItemId);
			java.util.Date start = new java.util.Date();
			logBean.setDate(start);
			EffectiveQuery effQuery = fmdtPackage.evaluateQuery(fmdtConnection.getVanillaContext(), fmdtQuery, promptValues);
			String sqlQuery = effQuery.getGeneratedQuery().replace("`", "\"");
			
			logBean.setEffectiveQuery(sqlQuery);
			logBean.setWeight(effQuery.getWeight());
			
			if (group.getMaxSupportedWeightFmdt() < effQuery.getWeight()){
				throw new FmdtQueryRuntimeException("The Group " + group.getName() + " cannot support the weight of the query", sqlQuery, effQuery.getWeight());
			}
			
			logBean.setDate(start);
			
			Activator.getLogger().info("executing query : " + sqlQuery);
			
//			List<List<String>> values = fmdtPackage.executeQuery(
//					group.getMaxSupportedWeightFmdt(), 
//					fmdtConnection.getVanillaContext(), 
//					connectionName, 
//					fmdtQuery, 
//					promptValues);
			List<List<Object>>values = fmdtPackage.getQueryExecutor().execute(
					fmdtConnection.getVanillaContext(), 
					fmdtQuery, 
					fmdtConnection.getConnectionName(), promptValues);
			
			Activator.getLogger().info("Query executed");
			
			return new UnitedOlapResultSet(fmdtQuery, values);
			
		}catch(SQLException ex){
			String errMsg = "SQLException : ";
			while (ex != null) {
                
                errMsg += "\n\tErrorCode = "+ ex.getErrorCode();
                errMsg += "\n\tSQLState = "+ ex.getSQLState();
                errMsg += "\n\tMessage = "+ ex.getMessage();
            
                ex = ex.getNextException();
            }
			logBean.setFailureCause(errMsg);
			Activator.getLogger().error("Error when executing query : " , ex);
            throw new OdaException(new Exception(errMsg, ex)); 
		
		}catch(FmdtQueryRuntimeException e){
			logBean.setFailureCause(e.getMessage());
			logBean.setEffectiveQuery(e.getEffectiveQuery());
			throw new RuntimeException(e.getMessage());
		}catch(Exception e){
			e.printStackTrace();
			Activator.getLogger().error("Error when executing query : " , e);
			throw new OdaException(e);
		}finally{
			if (logBean != null){
				IVanillaAPI api = new RemoteVanillaPlatform(fmdtConnection.getVanillaContext());
				
				try{
					api.getVanillaLoggingManager().addFmdtQuery(logBean);
				}catch(Exception ex){
					Logger.getLogger(getClass()).warn("Failed to save FMDTLogBean in Vanilla " + ex.getMessage(), ex);
				}
			}
		}
	}

	@Override
	public int findInParameter(String parameterName) throws OdaException {
		for(int i = 0; i < fmdtQuery.getPrompts().size(); i++){
			if (fmdtQuery.getPrompts().get(i).getName().equals(parameterName)){
				return i;
			}
		}
		
		throw new OdaException("Cannot find parameter named " + parameterName + " because there is no prompt in the FMDT Query");
	}

	@Override
	public String getEffectiveQueryText() {
		try {
			if (!fmdtPackage.getBusinessModel().getModel().isBuilt()){

				MetaDataBuilder builder = new MetaDataBuilder(fmdtConnection.getRepositoryConnection());
				builder.build(fmdtPackage.getBusinessModel().getModel(), fmdtConnection.getRepositoryConnection(), group.getName());
				if (!"".equals(builder.getErrorsBuffer())){
					Activator.getLogger().error(builder.getErrorsBuffer(), null);
				}
			}
			String sqlQuery = fmdtPackage.getQuery(group.getMaxSupportedWeightFmdt(), fmdtConnection.getVanillaContext(), fmdtQuery, promptValues).replace("`", "\"");
			
			return sqlQuery;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(getClass()).error("Unable to generate SQL Query - " + e.getMessage(), e);
		}
		
		return null;
	}

	@Override
	public int getMaxRows() throws OdaException {
		return m_maxRows;
	}

	@Override
	public IResultSetMetaData getMetaData() throws OdaException {
		return new UnitedOlapResultSetMetadata(fmdtQuery);
	}

	@Override
	public IParameterMetaData getParameterMetaData() throws OdaException {
		return new ParameterMetaData(this.fmdtQuery.getSelect(), this.fmdtQuery.getPrompts());
	}

	@Override
	public SortSpec getSortSpec() throws OdaException {
		return null;
	}

	@Override
	public QuerySpecification getSpecification() {
		return null;
	}

	@Override
	public void prepare(String queryText) throws OdaException {
		try{
			SqlQueryDigester dig = new SqlQueryDigester(IOUtils.toInputStream(queryText, "UTF-8"),
					group.getName(),
					fmdtPackage);
			
			fmdtQuery = dig.getUOlapModel();
			promptValues = new ArrayList<List<String>>();
			for(Prompt p : fmdtQuery.getPrompts()){
				promptValues.add(new ArrayList<String>());
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new OdaException("Unable to parse FmdtQuery :\n" + e.getMessage());
		}
	}

	@Override
	public void setAppContext(Object arg0) throws OdaException {
		
	}

	@Override
	public void setBigDecimal( String parameterName, BigDecimal value ) throws OdaException
	{
		int i = 0;
		for(Prompt p : fmdtQuery.getPrompts()){
        	if (p.getName().equals(parameterName)){
        		setBigDecimal(i + 1, value);
        		return;
        	}
        	i++;
        }
	}

	@Override
	public void setBigDecimal(int parameterId, BigDecimal value) throws OdaException {
		List<String> vals = promptValues.get(parameterId - 1);
		if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("in")){
			
			if (vals == null){
				vals = new ArrayList<String>();
			}
		}
		else if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("between")){
			if (vals == null){
				vals = new ArrayList<String>();
			}
			if (vals.size() > 1){
				vals.remove(0);
			}
		}
		else{
			vals = new ArrayList<String>();
		}
		vals.add(value + "");
		
	
		promptValues.set(parameterId - 1, vals);
	}

	@Override
	public void setBoolean(String parameterName, boolean value) throws OdaException {
		int i = 0;
		for(Prompt p : fmdtQuery.getPrompts()){
        	if (p.getName().equals(parameterName)){
        		setBoolean(i + 1, value);
        		return;
        	}
        	i++;
        }
	}

	@Override
    public void setBoolean( int parameterId, boolean value ) throws OdaException
	{
		List<String> vals = promptValues.get(parameterId - 1);
		if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("in")){
			
			if (vals == null){
				vals = new ArrayList<String>();
			}
		}
		else if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("between")){
			if (vals == null){
				vals = new ArrayList<String>();
			}
			if (vals.size() > 1){
				vals.remove(0);
			}
		}
		else{
			vals = new ArrayList<String>();
		}
		vals.add(value + "");
		
		
		promptValues.set(parameterId - 1, vals);
	}

	@Override
	public void setDate( String parameterName, Date value ) throws OdaException
	{
		int i = 0;
		for(Prompt p : fmdtQuery.getPrompts()){
        	if (p.getName().equals(parameterName)){
        		setDate(i + 1, value);
        		return;
        	}
        	i++;
        }
	}

	@Override
	public void setDate( int parameterId, Date value ) throws OdaException
	{
		List<String> vals = promptValues.get(parameterId - 1);
		if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("in")){
			
			if (vals == null){
				vals = new ArrayList<String>();
			}
		}
		else if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("between")){
			if (vals == null){
				vals = new ArrayList<String>();
			}
			if (vals.size() > 1){
				vals.remove(0);
			}
		}
		else{
			vals = new ArrayList<String>();
		}
		vals.add(value + "");
		
	
		promptValues.set(parameterId - 1, vals);
	}

	@Override
	public void setDouble( String parameterName, double value ) throws OdaException
	{
		int i = 0;
		for(Prompt p : fmdtQuery.getPrompts()){
        	if (p.getName().equals(parameterName)){
        		setDouble(i + 1, value);
        		return;
        	}
        	i++;
        }
	}

	@Override
	public void setDouble( int parameterId, double value ) throws OdaException
	{
		List<String> vals = new ArrayList<String>();
		vals.add(value + "");
		promptValues.set(parameterId - 1, vals);
	}

	@Override
	public void setInt( String parameterName, int value ) throws OdaException
	{
		int pos = 0;
		for(Prompt p : fmdtQuery.getPrompts()){
        	if (p.getName().equals(parameterName)){
        		setInt(pos + 1, value);
        		return;
        	}
        	pos++;
        }
	}

	@Override
	public void setInt( int parameterId, int value ) throws OdaException
	{
		List<String> vals = new ArrayList<String>();
		vals.add(value + "");
		promptValues.set(parameterId - 1, vals);
        
	}

	@Override
	public void setMaxRows(int max) throws OdaException {
		 m_maxRows = max;
	}

	@Override
	public void setNull(String parameterName) throws OdaException {
		int i = 0;
		for(Prompt p : fmdtQuery.getPrompts()){
        	if (p.getName().equals(parameterName)){
        		List<String> vals = new ArrayList<String>();
        		vals.add("null");
        		promptValues.set(i, vals);
        		return;
        	}
        	i++;
        }
	}

	@Override
	public void setNull(int parameterId) throws OdaException {
		promptValues.set(parameterId - 1, null);
	}

	@Override
	public void setObject(String arg0, Object value) throws OdaException {
		int pos = 0;
		for(Prompt p : fmdtQuery.getPrompts()){
        	if (p.getName().equals(arg0)){
        		setObject(pos + 1, value);
        		return;
        	}
        	pos++;
        }
		
	}

	@Override
	public void setObject(int parameterId, Object value) throws OdaException {
		List<String> vals = promptValues.get(parameterId - 1);
		if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("in")){
			
			if (vals == null){
				vals = new ArrayList<String>();
			}
		}
		else if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("between")){
			if (vals == null){
				vals = new ArrayList<String>();
			}
			if (vals.size() > 1){
				vals.remove(0);
			}
		}
		else{
			vals = new ArrayList<String>();
		}
		vals.add(value + "");
		
	
		promptValues.set(parameterId - 1, vals);
		
	}

	@Override
	public void setProperty(String arg0, String arg1) throws OdaException {
		
	}

	@Override
	public void setSortSpec(SortSpec arg0) throws OdaException {
		
	}

	@Override
	public void setSpecification(QuerySpecification arg0) throws OdaException, UnsupportedOperationException {
		
	}

	@Override
	public void setString( String parameterName, String value ) throws OdaException
	{
		int i = 0;
		for(Prompt p : fmdtQuery.getPrompts()){
        	if (p.getName().equals(parameterName)){
        		setString(i + 1, value);
        		return;
        	}
        	i++;
        }
	}

	@Override
	public void setString( int parameterId, String value ) throws OdaException
	{
		List<String> vals = promptValues.get(parameterId - 1);
		if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("in")){
			
			if (vals == null){
				vals = new ArrayList<String>();
			}
			vals.add(value + "");
		}
		else if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("between")){
			if (vals == null){
				vals = new ArrayList<String>();
			}
			if (vals.size() > 1){
				vals.remove(0);
			}
			vals.add(value + "");
		}
		else{
			if (vals.size() == 1){
				vals.set(0, value + "");
			}
			else{
				vals.add(value + "");
			}
		}
		
		
		
	
		promptValues.set(parameterId - 1, vals);
	}

	@Override
	public void setTime( String parameterName, Time value ) throws OdaException
	{
		int i = 0;
		for(Prompt p : fmdtQuery.getPrompts()){
        	if (p.getName().equals(parameterName)){
        		setTime(i + 1, value);
        		return;
        	}
        	i++;
        }
	}

	@Override
	public void setTime( int parameterId, Time value ) throws OdaException
	{
		List<String> vals = promptValues.get(parameterId - 1);
		if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("in")){
			
			if (vals == null){
				vals = new ArrayList<String>();
			}
		}
		else if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("between")){
			if (vals == null){
				vals = new ArrayList<String>();
			}
			if (vals.size() > 1){
				vals.remove(0);
			}
		}
		else{
			vals = new ArrayList<String>();
		}
		vals.add(value + "");
		
	
		promptValues.set(parameterId - 1, vals);
	}

	@Override
	public void setTimestamp( String parameterName, Timestamp value ) throws OdaException
	{
		int i = 0;
		for(Prompt p : fmdtQuery.getPrompts()){
        	if (p.getName().equals(parameterName)){
        		setTimestamp(i + 1, value);
        		return;
        	}
        	i++;
        }
	}

	@Override
	public void setTimestamp( int parameterId, Timestamp value ) throws OdaException
	{
		List<String> vals = promptValues.get(parameterId - 1);
		if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("in")){
			
			if (vals == null){
				vals = new ArrayList<String>();
			}
		}
		else if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("between")){
			if (vals == null){
				vals = new ArrayList<String>();
			}
			if (vals.size() > 1){
				vals.remove(0);
			}
		}
		else{
			vals = new ArrayList<String>();
		}
		vals.add(value + "");
		
	
		promptValues.set(parameterId - 1, vals);
	}


}
