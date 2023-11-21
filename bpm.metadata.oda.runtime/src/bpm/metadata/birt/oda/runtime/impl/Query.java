/*
 *************************************************************************
 * Copyright (c) 2007 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.metadata.birt.oda.runtime.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.SortSpec;
import org.eclipse.datatools.connectivity.oda.spec.QuerySpecification;

import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.metadata.MetaData;
import bpm.metadata.MetaDataBuilder;
import bpm.metadata.birt.oda.runtime.Activator;
import bpm.metadata.digester.SqlQueryDigester;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.query.EffectiveQuery;
import bpm.metadata.query.FmdtQueryRuntimeException;
import bpm.metadata.query.QuerySql;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.FMDTQueryBean;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;



/**
 * Implementation class of IQuery for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that returns a pre-defined set
 * of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place. 
 */
public class Query implements IQuery
{
	private int m_maxRows;
	
	
	private ResultSetMetaData resultSetMetaData;
	
	protected IBusinessPackage fmdtPackage;
	private String connectionName;
	private Group group;
	private QuerySql fmdtQuery;
	private List<Object> parameters = new ArrayList<Object>();
	/*
	 * jdbc object must be closed
	 */
	private VanillaPreparedStatement jdbcStatement;
	private List<List<String>> promptValues;

	private Connection fmdtConnection;
	
	public Query(Connection fmdtConnection, VanillaPreparedStatement jdbcStatement, IBusinessPackage pack, String connectionName, Group group){
		this.fmdtConnection = fmdtConnection;
		this.fmdtPackage = pack;
		this.group = group;
		this.connectionName = connectionName;
		this.jdbcStatement = jdbcStatement;
	}
	
	protected Connection getConnection(){
		return fmdtConnection;
	}
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#prepare(java.lang.String)
	 */
	public void prepare( String queryText ) throws OdaException{
		try{
			SqlQueryDigester dig = new SqlQueryDigester(IOUtils.toInputStream(queryText, "UTF-8"),
					group.getName(),
					fmdtPackage);
			
			fmdtQuery = dig.getModel();
			promptValues = new ArrayList<List<String>>();
			for(Prompt p : fmdtQuery.getPrompts()){
				promptValues.add(new ArrayList<String>());
			}
						
			parameters.clear();
			parameters.addAll(fmdtQuery.getPrompts());
			resultSetMetaData = new ResultSetMetaData(fmdtQuery, null);
			
		}catch(Exception e){
			e.printStackTrace();
			throw new OdaException("Unable to parse FmdtQuery :\n" + e.getMessage());
		}
		
		
		
	}
	
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setAppContext(java.lang.Object)
	 */
	public void setAppContext( Object context ) throws OdaException
	{
		if (context instanceof Group){
			this.group = (Group)context;
			Logger.getLogger(getClass()).debug("FMDT Query group set from AppContext with " + group.getName());
		}
	    // do nothing; assumes no support for pass-through context
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#close()
	 */
	public void close() throws OdaException
	{
		
		if (getJdbcStatement() != null){
			try {
				getJdbcStatement().close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new OdaException(e);
			}
		}
		
		      
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getMetaData()
	 */
	public IResultSetMetaData getMetaData() throws OdaException
	{
        return new ResultSetMetaData(fmdtQuery, null);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#executeQuery()
	 */
	public IResultSet executeQuery() throws OdaException
	{
		FMDTQueryBean logBean = null;
			
//		IResultSet resultSet = new ResultSet(fmdtQuery, QueryExecutor.execute(connectionName, fmdtPackage, fmdtQuery));
		
		String realQuery = "";
		
		try{
			
			/**
			 * ere added
			 */
			synchronized (fmdtPackage.getBusinessModel().getModel()) {
				MetaData model = fmdtPackage.getBusinessModel().getModel();
				if (!model.isBuilt()){
					MetaDataBuilder builder = new MetaDataBuilder(null);
					builder.build(fmdtPackage.getBusinessModel().getModel(), fmdtConnection.getRepositoryConnection(), group.getName());
					if (!"".equals(builder.getErrorsBuffer())){
						try {
							Activator.getLogger().error(builder.getErrorsBuffer(), null);
						} catch(Exception e) {

						}
					}
				}
			}
			try {
				Activator.getLogger().info("building query : ");
			} catch(Exception e) {

			}
			logBean = new FMDTQueryBean();
			logBean.setFmdtQuery(fmdtQuery.getXml());
			if (group == null || group.getId() == null){
				logBean.setVanillaGroupId(-1);//group.getId());
			}
			else{
				logBean.setVanillaGroupId(group.getId());
			}
			
			logBean.setRepositoryId(fmdtConnection.getVanillaRepository().getId());
			if (fmdtConnection.directoryItemId != null){
				logBean.setDirectoryItemId(fmdtConnection.directoryItemId);
			}
			
			java.util.Date start = new java.util.Date();
			logBean.setDate(start);
			
			
			EffectiveQuery sqlQuery = fmdtPackage.evaluateQuery(fmdtConnection.getVanillaContext(), fmdtQuery, promptValues);
			
			realQuery = sqlQuery.getGeneratedQuery();
			
			try {
				if(!(((SQLConnection)fmdtPackage.getConnection(group.getName(), connectionName)).getDriverName().toLowerCase().contains("drill") || ((SQLConnection)fmdtPackage.getConnection(group.getName(), connectionName)).getDriverName().toLowerCase().contains("mariadb"))) {
					realQuery = sqlQuery.getGeneratedQuery().replace("`", "\"");
				}
			} catch(Exception e2) {
				realQuery = sqlQuery.getGeneratedQuery().replace("`", "\"");
			}
			
			logBean.setEffectiveQuery(realQuery);
			logBean.setWeight(sqlQuery.getWeight());
			
			if (group.getMaxSupportedWeightFmdt() < sqlQuery.getWeight()){
				throw new FmdtQueryRuntimeException("The Group " + group.getName() + " cannot support the weight of the query", realQuery, sqlQuery.getWeight());
			}
			Logger.getLogger(getClass()).info("executing query : " + realQuery);
			
			getJdbcStatement().setMaxRows(fmdtQuery.getLimit());
			java.sql.ResultSet rs = null;
			try {
				rs = getJdbcStatement().executeQuery(realQuery);
			} catch(Exception e1) {
				//The jdbc connection might be in timeout		
				SQLConnection sqlConnection = (SQLConnection)fmdtPackage.getConnection(group.getName(), connectionName);
				VanillaJdbcConnection jdbcConnection = sqlConnection.getJdbcConnection();
				fmdtConnection.setJdbcConnection(jdbcConnection);
				jdbcStatement = fmdtConnection.createJdbcStatement(jdbcConnection);
				rs = jdbcStatement.executeQuery(realQuery);
			}
			java.util.Date end = new java.util.Date();
			logBean.setDuration(end.getTime() - start.getTime());
			
			try {
				Activator.getLogger().info("query executed " );
			} catch(Exception e) {
			}
			IResultSet resultSet = new StreamResultSet(rs, fmdtQuery);
			
			return resultSet;
		}catch(SQLException ex){
			ex.printStackTrace();
			String errMsg = "SQLException : ";
			while (ex != null) {
                
                errMsg += "\n\tErrorCode = "+ ex.getErrorCode();
                errMsg += "\n\tSQLState = "+ ex.getSQLState();
                errMsg += "\n\tMessage = "+ ex.getMessage();
            
                ex.printStackTrace();
                ex = ex.getNextException();
            }
			logBean.setFailureCause(errMsg);
			try {
				Activator.getLogger().error("Error when executing query : " + realQuery , ex);
			} catch(Exception e) {

			}
            throw new OdaException(new Exception(errMsg, ex)); 
		}catch(FmdtQueryRuntimeException e){
			logBean.setFailureCause(e.getMessage());
			logBean.setWeight(e.getWeight());
			logBean.setEffectiveQuery(e.getEffectiveQuery());
			throw new RuntimeException(e.getMessage());
		}catch(Exception e){
			e.printStackTrace();
			try {
				Activator.getLogger().error("Error when executing query : " , e);
			} catch(Exception e1) {

			}
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

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setProperty(java.lang.String, java.lang.String)
	 */
	public void setProperty( String name, String value ) throws OdaException
	{
		// do nothing; assumes no data set query property
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setMaxRows(int)
	 */
	public void setMaxRows( int max ) throws OdaException
	{
		m_maxRows = max > -1 ? max : 0;
	    if (getJdbcStatement() != null){

	    		
    		try {
    			getJdbcStatement().setMaxRows(m_maxRows);
			} catch (Exception e) {
				e.printStackTrace();
				throw new OdaException(e);
			}
    	
	    }
		
	}
	
	protected VanillaPreparedStatement getJdbcStatement(){
		return jdbcStatement;
	}
	
	protected void setJdbcStatement(VanillaPreparedStatement jdbcStat){
		this.jdbcStatement =  jdbcStat;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getMaxRows()
	 */
	public int getMaxRows() throws OdaException
	{
		return m_maxRows;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#clearInParameters()
	 */
	public void clearInParameters() throws OdaException
	{
        promptValues.clear();
        for(Prompt p : fmdtQuery.getPrompts()){
			promptValues.add(new ArrayList<String>());
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setInt(java.lang.String, int)
	 */
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

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setInt(int, int)
	 */
	public void setInt( int parameterId, int value ) throws OdaException
	{
		List<String> vals = new ArrayList<String>();
		vals.add(value + "");
		promptValues.set(parameterId - 1, vals);
        
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDouble(java.lang.String, double)
	 */
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

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDouble(int, double)
	 */
	public void setDouble( int parameterId, double value ) throws OdaException
	{
		List<String> vals = new ArrayList<String>();
		vals.add(value + "");
		promptValues.set(parameterId - 1, vals);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
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

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setBigDecimal(int, java.math.BigDecimal)
	 */
	public void setBigDecimal( int parameterId, BigDecimal value ) throws OdaException
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

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setString(java.lang.String, java.lang.String)
	 */
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

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setString(int, java.lang.String)
	 */
	public void setString( int parameterId, String value ) throws OdaException
	{
		
		try {
			
			if (StringUtils.containsIgnoreCase(value, "select") && StringUtils.containsIgnoreCase(value, "from"))
			{
				List<String> vals = new ArrayList<String>();
				vals.add(value);
				promptValues.set(parameterId - 1, vals);
			    return;
			}
			else {
				setNormalString(parameterId, value);
			}
		} catch (Exception e1) {
			setNormalString(parameterId, value);
		}
		
	}
	
	public void setNormalString( int parameterId, String value ) throws OdaException {
		if (value != null){
			//the prompt values are between simple quotes, so we need to eescape them
			value = value.replace("'", "''");
		}
		List<String> vals = promptValues.get(parameterId - 1);
		if (fmdtQuery.getPrompts().get(parameterId - 1).getOperator().equalsIgnoreCase("in")){
			
			if (vals == null){
				vals = new ArrayList<String>();
			}
			
			if(value.contains(",")) {
				String[] values = value.split(",");
				for(int i = 0; i < values.length ; i++) {
					vals.add(values[i] + "");
				}
			}
			else {
				vals.add(value + "");
			}
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

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDate(java.lang.String, java.sql.Date)
	 */
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

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDate(int, java.sql.Date)
	 */
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

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTime(java.lang.String, java.sql.Time)
	 */
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

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTime(int, java.sql.Time)
	 */
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

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTimestamp(java.lang.String, java.sql.Timestamp)
	 */
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

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTimestamp(int, java.sql.Timestamp)
	 */
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

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IQuery#setBoolean(java.lang.String, boolean)
     */
    public void setBoolean( String parameterName, boolean value )
            throws OdaException
    {
    	int i = 0;
		for(Prompt p : fmdtQuery.getPrompts()){
        	if (p.getName().equals(parameterName)){
        		setBoolean(i + 1, value);
        		return;
        	}
        	i++;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IQuery#setBoolean(int, boolean)
     */
    public void setBoolean( int parameterId, boolean value )
            throws OdaException
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
    
    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IQuery#setNull(java.lang.String)
     */
    public void setNull( String parameterName ) throws OdaException
    {
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

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IQuery#setNull(int)
     */
    public void setNull( int parameterId ) throws OdaException
    {
    	
    	promptValues.set(parameterId - 1, null);
    }

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#findInParameter(java.lang.String)
	 */
	public int findInParameter( String parameterName ) throws OdaException
	{
		
		for(int i = 0; i < fmdtQuery.getPrompts().size(); i++){
			if (fmdtQuery.getPrompts().get(i).getName().equals(parameterName)){
				return i;
			}
		}
		
		throw new OdaException("Cannot find parameter named " + parameterName + " because there is no prompt in the FMDT Query");
		
		
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getParameterMetaData()
	 */
	public IParameterMetaData getParameterMetaData() throws OdaException
	{
        
		return new ParameterMetaData(this.fmdtQuery.getSelect(), this.fmdtQuery.getPrompts());
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setSortSpec(org.eclipse.datatools.connectivity.oda.SortSpec)
	 */
	public void setSortSpec( SortSpec sortBy ) throws OdaException
	{
		// only applies to sorting, assumes not supported
        throw new UnsupportedOperationException();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getSortSpec()
	 */
	public SortSpec getSortSpec() throws OdaException
	{
		// only applies to sorting
		return null;
	}


	public void cancel() throws OdaException, UnsupportedOperationException {
		
	}


	public String getEffectiveQueryText() {
		try {
			if (!fmdtPackage.getBusinessModel().getModel().isBuilt()){

				MetaDataBuilder builder = new MetaDataBuilder(null);
				builder.build(fmdtPackage.getBusinessModel().getModel(), fmdtConnection.getRepositoryConnection(), group.getName());
				if (!"".equals(builder.getErrorsBuffer())){
					try {
						Activator.getLogger().error(builder.getErrorsBuffer(), null);
					} catch(Exception e) {

					}
				}
			}
			String sqlQuery = fmdtPackage.getQuery(null, fmdtConnection.getVanillaContext(), fmdtQuery, promptValues).replace("`", "\"");
			
			return sqlQuery;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(getClass()).error("Unable to generate SQL Query - " + e.getMessage(), e);
		}
		
		return null;
	}


	public QuerySpecification getSpecification() {
		return null;
	}


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


	public void setObject(int parameterId, Object value) throws OdaException {
		if(value instanceof String) {
			setString(parameterId, value.toString());
		}
		else {
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


	public void setSpecification(QuerySpecification arg0) throws OdaException,
			UnsupportedOperationException {
		
		
	}
    
}
