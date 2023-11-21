package bpm.oda.driver.reader.model.dataset;


import java.util.ArrayList;
import java.util.List;


public class DataSetDescriptor {
	private DataSet dataSet;
	private List<ColumnDescriptor> columns = new ArrayList<ColumnDescriptor>();
	private List<ParameterDescriptor> parameters = new ArrayList<ParameterDescriptor>();

	
	protected DataSetDescriptor(DataSet owner){
		this.dataSet = owner;
	}
	
	protected DataSet getDataSet(){
		return dataSet;
	}
	
	public List<ColumnDescriptor> getColumnsDescriptors(){
		return new ArrayList<ColumnDescriptor>(columns);
	}
	public List<ParameterDescriptor> getParametersDescriptors(){
		return new ArrayList<ParameterDescriptor>(parameters);
	}

	protected void addColumn(ColumnDescriptor col) {
		columns.add(col);
		
	}

	protected void addParameter(ParameterDescriptor p) {
		parameters.add(p);
		
	}

}
