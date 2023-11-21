package bpm.nosql.oda.runtime.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTable;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.SortSpec;
import org.eclipse.datatools.connectivity.oda.spec.QuerySpecification;

public class HbaseQuery implements IQuery{

	private String m_preparedText;
	private HbaseConnection connection = null;
	private int c_maxRows = 0;
	private IResultSetMetaData metadata = null;
	private HTable table = null;
	private List<String> selectColumns = new ArrayList<String>();
	private String selectedTable = null;
	private String selectedFamilie = null;
	
	public HbaseQuery(HbaseConnection connection){
		this.connection = connection;
	}
	
	public HbaseQuery(HTable table) {
		this.table = table;
	}

	public void setConnection(HbaseConnection connection){
		this.connection = connection;	
	}
	
	public HbaseConnection getConnection(){
		return this.connection;
	}
	
	@Override
	public void cancel() throws OdaException, UnsupportedOperationException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void clearInParameters() throws OdaException {
		
	}

	@Override
	public void close() throws OdaException {
		this.m_preparedText = null;		
	}

	@Override
	public IResultSet executeQuery() throws OdaException {
		//TODO
		IResultSet resultSet;
		String tb = this.m_preparedText;
		selectedFamilie = tb.substring(tb.indexOf(".")+1);
		selectedTable = tb.substring(tb.indexOf(" from ")+6, tb.indexOf("."));
		try {
			doQuery(this.connection.getConfig(), selectedTable);
			resultSet = new HbaseResultSet(this);
			resultSet.setMaxRows(getMaxRows());
			
			} catch (Exception e) {
				throw new OdaException("query_COULD_NOT_RETRIEVE_RESULTSET");
			}

		return resultSet;
	}
	
	private void doQuery(Configuration c, String tableName) throws Exception{
		table = new HTable(c, tableName);
	}
	
	@Override
	public int findInParameter(String parameterName) throws OdaException {
		return 0;
	}

	@Override
	public String getEffectiveQueryText() {
		return m_preparedText;
	}

	@Override
	public int getMaxRows() throws OdaException {
		return c_maxRows;
	}

	@Override
	public IResultSetMetaData getMetaData() throws OdaException {
		if (metadata == null) {
			metadata = new HbaseResultSetMetaData(this);
		}
		return metadata;
	}

	@Override
	public IParameterMetaData getParameterMetaData() throws OdaException {
		return new ParameterMetaData();
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
		//TODO
		this.m_preparedText = queryText;
	}

	@Override
	public void setAppContext(Object context) throws OdaException {
		
	}

	@Override
	public void setBigDecimal(String parameterName, BigDecimal value)
			throws OdaException {
		
	}

	@Override
	public void setBigDecimal(int parameterId, BigDecimal value)
			throws OdaException {
		
	}

	@Override
	public void setBoolean(String parameterName, boolean value)
			throws OdaException {
		
	}

	@Override
	public void setBoolean(int parameterId, boolean value) throws OdaException {
		
	}

	@Override
	public void setDate(String parameterName, Date value) throws OdaException {
		
	}

	@Override
	public void setDate(int parameterId, Date value) throws OdaException {
		
	}

	@Override
	public void setDouble(String parameterName, double value)
			throws OdaException {
		
	}

	@Override
	public void setDouble(int parameterId, double value) throws OdaException {
		
	}

	@Override
	public void setInt(String parameterName, int value) throws OdaException {
		
	}

	@Override
	public void setInt(int parameterId, int value) throws OdaException {
		
	}

	@Override
	public void setMaxRows(int max) throws OdaException {
		c_maxRows = max;		
	}

	@Override
	public void setNull(String parameterName) throws OdaException {
		
	}

	@Override
	public void setNull(int parameterId) throws OdaException {
		
	}

	@Override
	public void setObject(String parameterName, Object value)
			throws OdaException {
		
	}

	@Override
	public void setObject(int parameterId, Object value) throws OdaException {
		
	}

	@Override
	public void setProperty(String name, String value) throws OdaException {
		
	}

	@Override
	public void setSortSpec(SortSpec sortBy) throws OdaException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void setSpecification(QuerySpecification querySpec)
			throws OdaException, UnsupportedOperationException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void setString(String parameterName, String value)
			throws OdaException {
		
	}

	@Override
	public void setString(int parameterId, String value) throws OdaException {
		
	}

	@Override
	public void setTime(String parameterName, Time value) throws OdaException {
		
	}

	@Override
	public void setTime(int parameterId, Time value) throws OdaException {
		
	}

	@Override
	public void setTimestamp(String parameterName, Timestamp value)
			throws OdaException {
		
	}

	@Override
	public void setTimestamp(int parameterId, Timestamp value)
			throws OdaException {
		
	}

	public List<String> getSelectColumns() {
		return selectColumns;
	}
	
	public void setSelectColumns(List<String> selectColumns) {
		this.selectColumns = selectColumns;
	}

	public void setSelectedTable(String selectedTable) {
		this.selectedTable = selectedTable;		
	}
	
	public String getSelectedTable(){
		return this.selectedTable;
	}

	public void setSelectedFamilies(String selectedFamily) {
		this.selectedFamilie = selectedFamily;	
	}
	
	public String getSelectedFamilie(){
		return this.selectedFamilie;
	}

	public HTable getTable() {
		return table;
	}

	public void setTable(HTable table) {
		this.table = table;
	}
	
	
}
