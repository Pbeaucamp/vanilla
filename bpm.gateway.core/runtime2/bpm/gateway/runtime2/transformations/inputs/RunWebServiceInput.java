package bpm.gateway.runtime2.transformations.inputs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.webservice.WebServiceHelper;
import bpm.gateway.core.transformations.webservice.WebServiceInput;
import bpm.gateway.core.transformations.webservice.WebServiceRow;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.vanilla.platform.core.beans.Variable;
import bpm.vanilla.platform.core.beans.service.IService;

public class RunWebServiceInput extends RuntimeStep {

	private WebServiceInput transfo;

	private HashMap<String, Integer> columnNames;
	private Iterator<WebServiceRow> result;

	public RunWebServiceInput(WebServiceInput transfo, int bufferSize) {
		super(null, transfo, bufferSize);
		this.transfo = transfo;
	}

	@Override
	public void init(Object adapter) throws Exception {
		isInited = true;

		// String targetNamespace = "http://litwinconsulting.com/webservices/";
		// String targetNamespace = "http://footballpool.dataaccess.eu";

		String webServiceUrl = transfo.getWebServiceUrl();
		String methodName = transfo.getMethodName();
		String targetNamespace = WebServiceHelper.getTargetNamespace(webServiceUrl);

		List<IService> parameters = transfo.getParameters();

		createResources();
		List<WebServiceRow> response = WebServiceHelper.callWebService(webServiceUrl, targetNamespace, methodName, parameters, columnNames);
		result = response.iterator();
	}

	private void createResources() throws Exception {
		// We init the list of columns we have to get
		columnNames = new LinkedHashMap<String, Integer>();
		StreamDescriptor descriptor = this.getTransformation().getDescriptor(getTransformation());
		for (StreamElement el : descriptor.getStreamElements()) {
			columnNames.put(el.name, el.type);
		}
	}

	@Override
	public void performRow() throws Exception {
		if (result == null) {
			throw new Exception("No Response defined");
		}

		if(result.hasNext()){
			Row row = RowFactory.createRow(this);
			
			WebServiceRow d =  result.next();
	
			int i = 0;
			for (String colName : columnNames.keySet()) {
	
				boolean found = false;
				for (Entry<String, String> entryRow : d.getColumnValues().entrySet()) {
					if (colName.equals(entryRow.getKey())) {
						insertData(colName, entryRow.getValue(), row, i);
						found = true;
						break;
					}
				}
	
				if (!found) {
					row.set(i, null);
				}
	
				i++;
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
		if (result != null){
			result = null;
			info(" Closed resultSet");
		}
		
		if (columnNames != null){
			columnNames = null;
			info(" Closed Connection");
		}
	}

	
	private void insertData(String colName, String value, Row row, int i) {
		int type = columnNames.get(colName);
		if(type == Variable.TYPE_BOOLEAN){
			row.set(i, value != null && !value.isEmpty() ? Boolean.parseBoolean(value) : null);
		}
		else if(type == Variable.TYPE_DATE){
			row.set(i, value != null && !value.isEmpty() ? value : null);
		}
		else if(type == Variable.TYPE_DOUBLE){
			row.set(i, value != null && !value.isEmpty() ? Double.parseDouble(value) : null);
		}
		else if(type == Variable.TYPE_FLOAT){
			row.set(i, value != null && !value.isEmpty() ? Float.parseFloat(value) : null);
		}
		else if(type == Variable.TYPE_INT){
			row.set(i, value != null && !value.isEmpty() ? Integer.parseInt(value) : null);
		}
		else if(type == Variable.TYPE_LONG){
			row.set(i, value != null && !value.isEmpty() ? Long.parseLong(value) : null);
		}
		else if(type == Variable.TYPE_STRING){
			row.set(i, value);
		}
		else {
			row.set(i, value);
		}
	}
}
