package bpm.fd.api.core.model.datas;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;

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

	public List<ParameterDescriptor> getRealParameters() {
		return parameters;
	}
	
	protected void addColumn(ColumnDescriptor col) {
		columns.add(col);
		
	}

	protected void addParameter(ParameterDescriptor p) {
		parameters.add(p);
		
	}

	public void addDummyParameter(String pname, int pos, String plabel) {
		ParameterDescriptor p = new ParameterDescriptor(dataSet, pos, IParameterMetaData.parameterModeIn, pname, 1, "String");
		parameters.add(p);
		
	}

}
