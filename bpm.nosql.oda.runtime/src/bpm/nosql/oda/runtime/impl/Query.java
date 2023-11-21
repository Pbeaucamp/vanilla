package bpm.nosql.oda.runtime.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.SortSpec;
import org.eclipse.datatools.connectivity.oda.spec.QuerySpecification;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class Query implements IQuery {

	private String c_preparedText;
	private List<String> columnIds = new ArrayList<String>();
	private int c_maxRows = 0;
	private IResultSetMetaData metadata = null;
	private Connection connection;
	private DB db = null;
	private DBCollection collection = null;
	private DBObject metadataObject = null;
	private DBCursor cursor = null;
	private List<String> selectColumns;
	private String filterClause = null;
	private String sortClause = null;
	
	private String collectionName;

	public DBCursor getCursor() {
		return cursor;
	}

	public List<String> getColumnIds() {
		return columnIds;
	}

	public List<String> getSelectColumns() {
		return selectColumns;
	}

	public void setSelectColumns(List<String> selectColumns) {
		this.selectColumns = selectColumns;
	}

	public void setColumnIds(List<String> columnIds) {
		this.columnIds = columnIds;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Query(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void cancel() throws OdaException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearInParameters() throws OdaException {
		this.filterClause = "";
		this.sortClause = "";
	}

	@Override
	public void close() throws OdaException {
		this.db = null;
		this.collection = null;
	}
	
	public DBCollection getCollection() {
		return collection;
	}

	@Override
	public IResultSet executeQuery() throws OdaException {

		try {
			doQuery(this.db, this.collection, this.filterClause, this.sortClause);
			IResultSet resultSet = new ResultSet(this);
			resultSet.setMaxRows(getMaxRows());
			return resultSet;
		} catch (Exception e) {
			throw new OdaException("query_COULD_NOT_RETRIEVE_RESULTSET");
		}

	}

	private void doQuery(DB db2, DBCollection collection2, String whereClause, String sortClause) throws OdaException {
		try {
			BasicDBObject queryObject = (BasicDBObject) JSON.parse(whereClause);
			BasicDBObject sortObject = (BasicDBObject) JSON.parse(sortClause);
			cursor = collection.find(queryObject).sort(sortObject);
		} catch (Exception e) {
			throw new OdaException("query_INVALID_JSON_QUERY");
		}

	}

	@Override
	public int findInParameter(String parameterName) throws OdaException {
		return 0;
	}

	@Override
	public String getEffectiveQueryText() {
		return c_preparedText;
	}

	@Override
	public int getMaxRows() throws OdaException {

		return c_maxRows;
	}

	@Override
	public IResultSetMetaData getMetaData() throws OdaException {
		if (metadata == null) {
			try {
				metadata = new ResultSetMetaData(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

		try {
			this.c_preparedText = queryText;

			Set<String> collections = db.getCollectionNames();
			
			String database = "";
			try {
				database = queryText.substring(queryText.indexOf("from ") + 5);
				List<String> columns = Arrays.asList(queryText.substring(queryText.indexOf("select ") + 7, queryText.indexOf(" from ")).split(","));


				if (selectColumns == null || selectColumns.isEmpty()) {
					selectColumns = new ArrayList<String>();
					for (String col : columns) {
						selectColumns.add(col);
					}
				}
			} catch (Exception e) {
			}
			
			if(collectionName != null) {
				collection = db.getCollection(collectionName);
			}
			else {
				if (collections.contains(database)) {
					collection = db.getCollection(database);
				}
				else {
					throw new OdaException("query_INVALID_COLLECTION_NAME");
				}
			}
			
			metadataObject = collection.findOne();

			if (metadataObject == null) {
				throw new OdaException("query_INVALID_METADATA");
			}

		} catch (Exception e) {
			throw new OdaException("query_COULD_NOT_PREPARE_QUERY_MONGO" + " " + e.getMessage());
		}
	}

	@Override
	public void setAppContext(Object context) throws OdaException {

	}

	@Override
	public void setBigDecimal(String parameterName, BigDecimal value) throws OdaException {

	}

	@Override
	public void setBigDecimal(int parameterId, BigDecimal value) throws OdaException {

	}

	@Override
	public void setBoolean(String parameterName, boolean value) throws OdaException {

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
	public void setDouble(String parameterName, double value) throws OdaException {

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
	public void setObject(String parameterName, Object value) throws OdaException {

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
	public void setSpecification(QuerySpecification querySpec) throws OdaException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setString(String parameterName, String value) throws OdaException {

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
	public void setTimestamp(String parameterName, Timestamp value) throws OdaException {

	}

	@Override
	public void setTimestamp(int parameterId, Timestamp value) throws OdaException {

	}

	public String getEncoding() {
		return connection.getEncoding();
	}

	public Query(DB db, String collection) {
		this.db = db;
		this.collectionName = collection;
	}

	public DB getDb() {
		return db;
	}

}
