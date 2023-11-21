/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.fm.oda.driver.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.SortSpec;
import org.eclipse.datatools.connectivity.oda.spec.QuerySpecification;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.freemetrics.api.manager.client.FmClientAccessor;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * The Query String must be on the form <queryAssoc> <fmApplicationId>id</fmApplicationId> <fmMetricId>id</fmMetricId> </queryAssoc>
 * 
 * There must be a Association between the given Application id and Metric id
 * 
 */
public class Query implements IQuery {
	private int m_maxRows;
	private String m_preparedText;
	
//	private Axis application;
//	private Metric metric;
	
	private List<Metric> metrics;
	private List<Axis> axes;
	
	private boolean dateAsParameter = false;
//	private boolean applicationAsParameter = false;
//	private boolean metricAsParameter = false;
	private FmClientAccessor assessor;

	private String dateFormat = "MM/dd/yyyy";

	private java.util.Date startDate = null;
	private java.util.Date endDate = null;
	private ParameterMetaData parameterMetadata;

	private SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

	public Query(FmClientAccessor assessor) {
		this.assessor = assessor;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#prepare(java.lang.String)
	 */
	public void prepare(String queryText) throws OdaException {
		m_preparedText = queryText;
		
		QueryHelper helper = new QueryHelper(assessor);
		try {
			helper.parseQueryXml(queryText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		metrics = helper.getMetrics();
		axes = helper.getAxes();
		startDate = helper.getStartDate();
		endDate = helper.getEndDate();
		dateAsParameter = helper.isDateAsParameter();

	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setAppContext(java.lang.Object)
	 */
	public void setAppContext(Object context) throws OdaException {
	// do nothing; assumes no support for pass-through context
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#close()
	 */
	public void close() throws OdaException {
		m_preparedText = null;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getMetaData()
	 */
	public IResultSetMetaData getMetaData() throws OdaException {
		return new ResultSetMetaData(metrics, axes, startDate, endDate, dateAsParameter);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#executeQuery()
	 */
	public IResultSet executeQuery() throws OdaException {
		
		
		
		
		List<LoaderDataContainer> data = null;
		try {
			data = assessor.getRemoteFm().getLoaderValuesForAxes(metrics, axes, startDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ResultSet(data, metrics, axes, startDate, endDate);
		
		
//		try {
//			
//			if(endDate != null) {
//				resultSet = new ResultSet(assessor.getRemoteFm().getValuesByMetricAxisAndDateInterval(metric.getId(), application.getId(), startDate, endDate).values().iterator().next(), dateFormat);
//			}
//			else {
//				resultSet = new ResultSet(assessor.getRemoteFm().getValuesByDateAndAxisAndMetric(startDate, application.getId(), metric.getId()).values().iterator().next(), dateFormat);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		resultSet.setMaxRows(getMaxRows());
//		return resultSet;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setProperty(java.lang.String, java.lang.String)
	 */
	public void setProperty(String name, String value) throws OdaException {
	// do nothing; assumes no data set query property
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setMaxRows(int)
	 */
	public void setMaxRows(int max) throws OdaException {
		m_maxRows = max;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getMaxRows()
	 */
	public int getMaxRows() throws OdaException {
		return m_maxRows;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#clearInParameters()
	 */
	public void clearInParameters() throws OdaException {
		startDate = null;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setInt(java.lang.String, int)
	 */
	public void setInt(String parameterName, int value) throws OdaException {
		if(parameterName.equals(ParameterMetaData.P_START_DATE)) {
			startDate = new Date(value);
		}
		else if(parameterName.equals(ParameterMetaData.P_END_DATE)) {
			endDate = new Date(value);
		}
//		else if(parameterName.equals(ParameterMetaData.P_METRIC_ID)) {
//			metric = Activator.getDefault().getFmManager().getMetricById(value);
//		}
//		else if(parameterName.equals(ParameterMetaData.P_AXE_ID)) {
//			application = Activator.getDefault().getFmManager().getApplicationById(value);
//		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setInt(int, int)
	 */
	public void setInt(int parameterId, int value) throws OdaException {
		if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_START_DATE)) {
			startDate = new Date(value);
		}
		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_END_DATE)) {
			endDate = new Date(value);
		}
//		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_METRIC_ID)) {
//			metric = Activator.getDefault().getFmManager().getMetricById(value);
//		}
//		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_AXE_ID)) {
//			application = Activator.getDefault().getFmManager().getApplicationById(value);
//		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDouble(java.lang.String, double)
	 */
	public void setDouble(String parameterName, double value) throws OdaException {
		if(parameterName.equals(ParameterMetaData.P_START_DATE)) {
			startDate = new Date((long) value);
		}
		else if(parameterName.equals(ParameterMetaData.P_END_DATE)) {
			endDate = new Date((long) value);
		}
//		else if(parameterName.equals(ParameterMetaData.P_METRIC_ID)) {
//			metric = Activator.getDefault().getFmManager().getMetricById((int) value);
//		}
//		else if(parameterName.equals(ParameterMetaData.P_AXE_ID)) {
//			application = Activator.getDefault().getFmManager().getApplicationById((int) value);
//		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDouble(int, double)
	 */
	public void setDouble(int parameterId, double value) throws OdaException {
		if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_START_DATE)) {
			startDate = new Date((long) value);
		}
		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_END_DATE)) {
			endDate = new Date((long) value);
		}
//		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_METRIC_ID)) {
//			metric = Activator.getDefault().getFmManager().getMetricById((int) value);
//		}
//		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_AXE_ID)) {
//			application = Activator.getDefault().getFmManager().getApplicationById((int) value);
//		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void setBigDecimal(String parameterName, BigDecimal value) throws OdaException {
		if(parameterName.equals(ParameterMetaData.P_START_DATE)) {
			startDate = new Date(value.longValue());
		}
		else if(parameterName.equals(ParameterMetaData.P_END_DATE)) {
			endDate = new Date(value.longValue());
		}
//		else if(parameterName.equals(ParameterMetaData.P_METRIC_ID)) {
//			metric = Activator.getDefault().getFmManager().getMetricById(value.intValue());
//		}
//		else if(parameterName.equals(ParameterMetaData.P_AXE_ID)) {
//			application = Activator.getDefault().getFmManager().getApplicationById(value.intValue());
//		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setBigDecimal(int, java.math.BigDecimal)
	 */
	public void setBigDecimal(int parameterId, BigDecimal value) throws OdaException {
		if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_START_DATE)) {
			startDate = new Date(value.longValue());
		}
		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_END_DATE)) {
			endDate = new Date(value.longValue());
		}
//		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_METRIC_ID)) {
//			metric = Activator.getDefault().getFmManager().getMetricById(value.intValue());
//		}
//		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_AXE_ID)) {
//			application = Activator.getDefault().getFmManager().getApplicationById(value.intValue());
//		}

	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setString(java.lang.String, java.lang.String)
	 */
	public void setString(String parameterName, String value) throws OdaException {

		if(parameterName.equals(ParameterMetaData.P_START_DATE)) {
			try {
				startDate = sdf.parse(value);
			} catch(ParseException e) {
				startDate = null;
				e.printStackTrace();
				throw new OdaException("Error while parsing date : value " + e.getMessage());
			}
		}
		else if(parameterName.equals(ParameterMetaData.P_END_DATE)) {
			try {
				endDate = sdf.parse(value);
			} catch(ParseException e) {
				endDate = null;
				e.printStackTrace();
				throw new OdaException("Error while parsing date : value " + e.getMessage());
			}
		}
//		else if(parameterName.equals(ParameterMetaData.P_METRIC_ID)) {
//			metric = Activator.getDefault().getFmManager().getMetricById(Integer.parseInt(value));
//		}
//		else if(parameterName.equals(ParameterMetaData.P_AXE_ID)) {
//			application = Activator.getDefault().getFmManager().getApplicationById(Integer.parseInt(value));
//		}

	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setString(int, java.lang.String)
	 */
	public void setString(int parameterId, String value) throws OdaException {
		if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_START_DATE)) {
			try {
				startDate = sdf.parse(value);
			} catch(ParseException e) {
				startDate = null;
				e.printStackTrace();
				throw new OdaException("Error while parsing date : value " + e.getMessage());
			}
		}
		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_END_DATE)) {
			try {
				endDate = sdf.parse(value);
			} catch(ParseException e) {
				endDate = null;
				e.printStackTrace();
				throw new OdaException("Error while parsing date : value " + e.getMessage());
			}
		}
//		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_METRIC_ID)) {
//			metric = Activator.getDefault().getFmManager().getMetricById(Integer.parseInt(value));
//		}
//		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_AXE_ID)) {
//			application = Activator.getDefault().getFmManager().getApplicationById(Integer.parseInt(value));
//		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDate(java.lang.String, java.sql.Date)
	 */
	public void setDate(String parameterName, Date value) throws OdaException {
		if(parameterName.equals(ParameterMetaData.P_START_DATE)) {
			startDate = value;
		}
		else if(parameterName.equals(ParameterMetaData.P_END_DATE)) {
			endDate = value;
		}
		else {
			throw new OdaException("You can't use a date to fill a parameter of type int.");
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDate(int, java.sql.Date)
	 */
	public void setDate(int parameterId, Date value) throws OdaException {
		if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_START_DATE)) {
			startDate = value;
		}
		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_END_DATE)) {
			endDate = value;
		}
		else {
			throw new OdaException("You can't use a date to fill a parameter of type int.");
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTime(java.lang.String, java.sql.Time)
	 */
	public void setTime(String parameterName, Time value) throws OdaException {
		if(parameterName.equals(ParameterMetaData.P_START_DATE)) {
			startDate = new Date(value.getTime());
		}
		else if(parameterName.equals(ParameterMetaData.P_END_DATE)) {
			endDate = new Date(value.getTime());
		}
		else {
			throw new OdaException("You can't use a date to fill a parameter of type int.");
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTime(int, java.sql.Time)
	 */
	public void setTime(int parameterId, Time value) throws OdaException {
		if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_START_DATE)) {
			startDate = new Date(value.getTime());
		}
		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_END_DATE)) {
			endDate = new Date(value.getTime());
		}
		else {
			throw new OdaException("You can't use a date to fill a parameter of type int.");
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void setTimestamp(String parameterName, Timestamp value) throws OdaException {
		if(parameterName.equals(ParameterMetaData.P_START_DATE)) {
			startDate = new Date(value.getTime());
		}
		else if(parameterName.equals(ParameterMetaData.P_END_DATE)) {
			endDate = new Date(value.getTime());
		}
		else {
			throw new OdaException("You can't use a date to fill a parameter of type int.");
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTimestamp(int, java.sql.Timestamp)
	 */
	public void setTimestamp(int parameterId, Timestamp value) throws OdaException {
		if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_START_DATE)) {
			startDate = new Date(value.getTime());
		}
		else if(getFmParameterMetadata().getParameterName(parameterId).equals(ParameterMetaData.P_END_DATE)) {
			endDate = new Date(value.getTime());
		}
		else {
			throw new OdaException("You can't use a date to fill a parameter of type int.");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setBoolean(java.lang.String, boolean)
	 */
	public void setBoolean(String parameterName, boolean value) throws OdaException {
		throw new OdaException("This Query cannot contain a Boolean parameter");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setBoolean(int, boolean)
	 */
	public void setBoolean(int parameterId, boolean value) throws OdaException {
		throw new OdaException("This Query cannot contain a Boolean parameter");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setObject(java.lang.String, java.lang.Object)
	 */
	public void setObject(String parameterName, Object value) throws OdaException {
		if(value instanceof Date) {
			setDate(parameterName, (Date) value);
		}
		else if(value instanceof Integer) {
			setInt(parameterName, (Integer) value);
		}
		else if(value instanceof BigDecimal) {
			setBigDecimal(parameterName, (BigDecimal) value);
		}
		else if(value instanceof Long) {
			setDouble(parameterName, new Double((Long) value));
		}
		else if(value instanceof Float) {
			setDouble(parameterName, new Double((Float) value));
		}
		else if(value instanceof Double) {
			setDouble(parameterName, new Double((Double) value));
		}
		else if(value instanceof String) {
			setString(parameterName, (String) value);
		}
		else if(value instanceof Time) {
			setTime(parameterName, (Time) value);
		}
		else if(value instanceof Timestamp) {
			setTimestamp(parameterName, (Timestamp) value);
		}

		throw new OdaException("Unable to use setObject for the type " + value.getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setObject(int, java.lang.Object)
	 */
	public void setObject(int parameterId, Object value) throws OdaException {
		setObject(getParameterMetaData().getParameterName(parameterId), value);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setNull(java.lang.String)
	 */
	public void setNull(String parameterName) throws OdaException {
		if(parameterName.equals(ParameterMetaData.P_START_DATE)) {
			startDate = null;
		}
		else if(parameterName.equals(ParameterMetaData.P_END_DATE)) {
			endDate = null;
		}
//		else if(parameterName.equals(ParameterMetaData.P_METRIC_ID)) {
//			metric = null;
//		}
//		else if(parameterName.equals(ParameterMetaData.P_AXE_ID)) {
//			application = null;
//		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setNull(int)
	 */
	public void setNull(int parameterId) throws OdaException {
		if(parameterId == 1) {
			startDate = null;
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#findInParameter(java.lang.String)
	 */
	public int findInParameter(String parameterName) throws OdaException {
		return getFmParameterMetadata().getParameterPosition(parameterName);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getParameterMetaData()
	 */
	public IParameterMetaData getParameterMetaData() throws OdaException {
		if(parameterMetadata == null) {
			parameterMetadata = new ParameterMetaData(dateAsParameter, false, false);
		}
		return parameterMetadata;
	}
	
	public ParameterMetaData getFmParameterMetadata() throws OdaException {
		return (ParameterMetaData) getParameterMetaData();
	}
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setSortSpec(org.eclipse.datatools.connectivity.oda.SortSpec)
	 */
	public void setSortSpec(SortSpec sortBy) throws OdaException {
		throw new UnsupportedOperationException();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getSortSpec()
	 */
	public SortSpec getSortSpec() throws OdaException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setSpecification(org.eclipse.datatools.connectivity.oda.spec.QuerySpecification)
	 */
	@SuppressWarnings("restriction")
	public void setSpecification(QuerySpecification querySpec) throws OdaException, UnsupportedOperationException {
		// assumes no support
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getSpecification()
	 */
	@SuppressWarnings("restriction")
	public QuerySpecification getSpecification() {
		// assumes no support
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getEffectiveQueryText()
	 */
	public String getEffectiveQueryText() {
		return m_preparedText;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#cancel()
	 */
	public void cancel() throws OdaException, UnsupportedOperationException {
		// assumes unable to cancel while executing a query
		throw new UnsupportedOperationException();
	}

}
