package bpm.gateway.runtime2.transformation.googleanalytics;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.googleanalytics.VanillaAnalyticsGoogle;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.vanilla.platform.core.IRepositoryContext;

import com.google.gdata.client.analytics.AnalyticsService;
import com.google.gdata.client.analytics.DataQuery;
import com.google.gdata.data.analytics.DataEntry;
import com.google.gdata.data.analytics.DataFeed;

public class GoogleAnalyticsExtractData extends RuntimeStep{

	private static final String APPLICATION_NAME = "gaExportAPI_acctSample_v2.0";
	private static final String GG_ANALYTICS_URL = "https://www.google.com/analytics/feeds/data";
	
	private String username;
	private String password;
	private String tableId;
	private String dateBegin, dateEnd;
	private List<String> dimensions;
	private List<String> metrics;
	private boolean groupByDate;
	
	private DataFeed currentFeed;
	private int currentFeedNumber;
	private int currentRow = 0;
	private int rowLength = 0;
	private int rowNumber = 0;
	
	private List<DataAndDate> data;
	
	public GoogleAnalyticsExtractData(IRepositoryContext repositoryCtx, 
			Transformation transformation, int bufferSize) {
		super(repositoryCtx, transformation, bufferSize);
	}
	
	@Override
	public void init(Object adapter) throws Exception {
		info("Init google analytics...");
		
		VanillaAnalyticsGoogle tr = (VanillaAnalyticsGoogle)getTransformation();
		
		this.username = tr.getUsername();
		this.password = tr.getPassword();
		this.tableId = tr.getTableId();
		this.dateBegin = tr.getBeginDate();
		this.dateEnd = tr.getEndDate();
		this.dimensions = tr.getDimensions();
		this.metrics = tr.getMetrics();
		this.groupByDate = tr.isGroupByDate();
		this.data = new ArrayList<DataAndDate>();
		this.currentFeedNumber = 0;
		this.currentRow = 0;
		this.rowLength = 0;
		this.rowNumber = 0;
		
		StringBuilder builderDim = new StringBuilder();
		if(dimensions != null){
			for(int i=0; i<dimensions.size(); i++){
				if(i != 0){
					builderDim.append("," + dimensions.get(i));
				}
				else {
					builderDim.append(dimensions.get(i));
				}
			}
		}
		
		StringBuilder builderMet = new StringBuilder();
		if(metrics != null){
			for(int i=0; i<metrics.size(); i++){
				if(i != 0){
					builderMet.append("," + metrics.get(i));
				}
				else {
					builderMet.append(metrics.get(i));
				}
			}
		}

		// Configure GA API.
	    AnalyticsService as = new AnalyticsService(APPLICATION_NAME);
	    // Client Login Authorization.
	    as.setUserCredentials(username, password);

    	int dimSize = dimensions != null ? dimensions.size() : 0;
    	int metricSize = metrics != null ? metrics.size() : 0;
		rowLength = dimSize + metricSize + 2;
		
		if(groupByDate){
		    // GA Data Feed query uri.
		    DataQuery query = new DataQuery(new URL(GG_ANALYTICS_URL));
		    query.setIds(tableId);
		    query.setDimensions(builderDim.toString());
		    query.setMetrics(builderMet.toString());
		    query.setStartDate(dateBegin);
		    query.setEndDate(dateEnd);
		    URL url = query.getUrl();
	
		    // Send our request to the Analytics API and wait for the results to
		    // come back.
		    try{
		    	DataFeed feed = as.getFeed(url, DataFeed.class);
		    	DataAndDate d = new DataAndDate(feed, dateBegin, dateEnd);
		    	data.add(d);
		    }
		    catch(Exception e){
		    	e.printStackTrace();
		    }
			
			if(data != null && !data.isEmpty()){
				currentFeed = data.get(currentFeedNumber).getFeed();
				rowNumber = currentFeed.getEntries().size();
			}
		}
		else {
			initForEachDate(as, builderDim.toString(), builderMet.toString());
		}
	}
	
	private void initForEachDate(AnalyticsService as, String dim, String met) throws Exception{
		List<String> dates = new ArrayList<String>();
		dates.add(dateBegin);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date dateB = df.parse(dateBegin);
		Date dateE = df.parse(dateEnd);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateB);
		while(!cal.getTime().equals(dateE)){
			cal.add(Calendar.DAY_OF_MONTH, 1);
			dates.add(df.format(cal.getTime()));
		}
		
		for(String date : dates){
			// GA Data Feed query uri.
		    DataQuery query = new DataQuery(new URL(GG_ANALYTICS_URL));
		    query.setIds(tableId);
		    query.setDimensions(dim);
		    query.setMetrics(met);
		    query.setStartDate(date);
		    query.setEndDate(date);
		    URL url = query.getUrl();
	
		    // Send our request to the Analytics API and wait for the results to
		    // come back.
		    try{
		    	DataFeed feed = as.getFeed(url, DataFeed.class);
		    	DataAndDate d = new DataAndDate(feed, date, date);
		    	data.add(d);
		    }
		    catch(Exception e){
		    	e.printStackTrace();
		    }
		}
		
		if(data != null && !data.isEmpty()){
			currentFeed = data.get(currentFeedNumber).getFeed();
			rowNumber = currentFeed.getEntries().size();
		}
	}

	@Override
	public void performRow() throws Exception {
		if(currentFeedNumber == data.size()-1 && currentRow == rowNumber){
			setEnd();
			return;
		}
		else if(currentRow == rowNumber){
			currentFeedNumber++;
			currentFeed = data.get(currentFeedNumber).getFeed();
			rowNumber = currentFeed.getEntries().size();
	    	
	    	currentRow = 0;
		}
		
		DataEntry entry = currentFeed.getEntries().get(currentRow);
		currentRow++;

		Row row = RowFactory.createRow(this);
		
		int i = 0;
		
		try {
			if(dimensions != null){
				for(String dim : dimensions){
					String dimEntry = entry.stringValueOf(dim);
					if (dimEntry != null){
						row.set(i, dimEntry);
						i++;
					}
					else {
						row.set(i, "");
						i++;
					}
				}
			}
		}catch(Exception ex){
			error(" problem when creating row for line {" + i + "}", ex);
			throw ex;
		}

    
		try{
			if(metrics != null){
				for(String metric : metrics){
					String metricEntry = entry.stringValueOf(metric);
					if (metricEntry != null && !metricEntry.isEmpty()){
						row.set(i, metricEntry);
					}
					i++;
				}
			}
		}catch(Exception ex){
			error(" problem when creating row for line {" + i + "}", ex);
			throw ex;
		}

		row.set(i, data.get(currentFeedNumber).getBeginDate());
		i++;
		row.set(i, data.get(currentFeedNumber).getEndDate());
		i++;
		
		if (i != rowLength){
			warn(" readed row size = " + i + " and StreamRowSize = " + rowLength);
			warn(" Problematic row kept in all standards OutputStreams");
			writeRow(row);
		}
		else{
			writeRow(row);
		}
	}

	@Override
	public void releaseResources() {
		if(data != null){
			data.clear();
		}
		this.data = null;
		this.currentFeedNumber = 0;
		this.currentRow = 0;
		this.rowLength = 0;
		
		info("Releasing Google Analytics data ... ");
	}
	
	public class DataAndDate {
		
		private DataFeed feed;
		private String beginDate;
		private String endDate;
		
		public DataAndDate(DataFeed feed, String beginDate, String endDate) {
			this.feed = feed;
			this.beginDate = beginDate;
			this.endDate = endDate;
		}
		
		public DataFeed getFeed(){
			return feed;
		}
		
		public String getBeginDate(){
			return beginDate;
		}
		
		public String getEndDate(){
			return endDate;
		}
	}
}
