package bpm.gateway.core.transformations.googleanalytics;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.googleanalytics.GoogleAnalyticsExtractData;
import bpm.vanilla.platform.core.IRepositoryContext;

public class VanillaAnalyticsGoogle extends AbstractTransformation implements IConnection {

	private String username;
	private String password;
	private String tableId;
	private String beginDate;
	private String endDate;
	private List<String> dimensions;
	private List<String> metrics;
	
	private boolean groupByDate;
	
	private DefaultStreamDescriptor descriptor;
	
	public VanillaAnalyticsGoogle(){

	}

	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().contains(stream)){
			return false;
		}
		if (getInputs().size() > 0){
			throw new Exception("Can only have one input");
		}
		return super.addInput(stream);
	}
	
	@Override
	public void refreshDescriptor() {
		descriptor = new DefaultStreamDescriptor();
		
		StreamElement element = null;
		
		if(dimensions != null){
			for(String dim : dimensions){
				element = new StreamElement();
				element.name = dim;
				element.tableName= tableId;
				element.originTransfo = this.getName();
				element.className = String.class.getName();
				element.transfoName = getName();
				descriptor.addColumn(element);
			}
		}
		
		if(metrics != null){
			for(String metric : metrics){
				element = new StreamElement();
				element.name = metric;
				element.tableName= tableId;
				element.originTransfo = this.getName();
				element.className = Double.class.getName();
				element.transfoName = getName();
				descriptor.addColumn(element);
			}
		}
		
		element = new StreamElement();
		element.name = "ga:beginDate";
		element.tableName= tableId;
		element.originTransfo = this.getName();
		element.className = String.class.getName();
		element.transfoName = getName();
		descriptor.addColumn(element);
		
		element = new StreamElement();
		element.name = "ga:endDate";
		element.tableName= tableId;
		element.originTransfo = this.getName();
		element.className = String.class.getName();
		element.transfoName = getName();
		descriptor.addColumn(element);
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		try{
			return new GoogleAnalyticsExtractData(repositoryCtx, this, bufferSize);
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("vanillaAnalyticsGoogle");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
	
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		e.addElement("username").setText(username != null ? username : "");
		e.addElement("password").setText(password != null ? password : "");
		e.addElement("tableId").setText(tableId != null ? tableId : "");
		e.addElement("beginDate").setText(beginDate != null ? beginDate : "");
		e.addElement("endDate").setText(endDate != null ? endDate : "");
		e.addElement("groupByDate").setText(groupByDate + "");
		
		Element dimE = e.addElement("dimensions");
		if(dimensions != null){
			for(String dim : dimensions){
				dimE.addElement("dimension").setText(dim);
			}
		}
		
		Element metE = e.addElement("metrics");
		if(metrics != null){
			for(String met : metrics){
				metE.addElement("metric").setText(met);
			}
		}
		
		return e;
	}
	
	public Transformation copy() {
		return null;
	}
	
	@Override
	public String getBeginDate() {
		return beginDate;
	}
	
	@Override
	public String getEndDate() {
		return endDate;
	}
	
	@Override
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	
	@Override
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String getTableId() {
		return tableId;
	}
	
	@Override
	public String getUsername() {
		return username;
	}
	
	@Override
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}
	
	@Override
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public List<String> getDimensions() {
		return dimensions;
	}
	
	@Override
	public List<String> getMetrics() {
		return metrics;
	}
	
	@Override
	public void setDimensions(List<String> dimensions) {
		this.dimensions = dimensions;
	}
	
	@Override
	public void setMetrics(List<String> metrics) {
		this.metrics = metrics;
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public String getAutoDocumentationDetails() {
		return null;
	}

	@Override
	public void addDimension(String dimension) {
		if(dimensions == null){
			dimensions = new ArrayList<String>();
		}
		this.dimensions.add(dimension);
		refreshDescriptor();
	}

	@Override
	public void addMetric(String metric) {
		if(metrics == null){
			metrics = new ArrayList<String>();
		}
		this.metrics.add(metric);
		refreshDescriptor();
	}

	@Override
	public void removeDimension(String dimension) {
		this.dimensions.remove(dimension);
		refreshDescriptor();
	}

	@Override
	public void removeMetric(String metric) {
		this.metrics.remove(metric);
		refreshDescriptor();
	}

	public void setGroupByDate(boolean groupByDate) {
		this.groupByDate = groupByDate;
	}
	
	public void setGroupByDate(String groupByDate){
		if(groupByDate.equalsIgnoreCase("true")){
			this.groupByDate = true;
		}
		else {
			this.groupByDate = false;
		}
	}

	public boolean isGroupByDate() {
		return groupByDate;
	}
}
