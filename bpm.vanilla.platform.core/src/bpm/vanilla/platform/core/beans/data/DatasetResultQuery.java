package bpm.vanilla.platform.core.beans.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatasetResultQuery implements Serializable {

	private static final long serialVersionUID = 5647595186764951389L;
	
	private Dataset dataset;
	private Map<String, List<Serializable>> result;
	
	public DatasetResultQuery() {
		super();
	}

	public DatasetResultQuery(Dataset dataset, Map<String, List<Serializable>> result) {
		super();
		this.dataset = dataset;
		this.result = result;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public Map<String, List<Serializable>> getResult() {
		return result;
	}

	public void setResult(Map<String, List<Serializable>> result) {
		this.result = result;
	}
	
	public List<Serializable> asObjects(String column) {
		return result.get(column);
	}
	
	public List<String> asStrings(String column) throws ClassCastException {
		List<String> res = new ArrayList<String>();
		for(Object object : result.get(column)){
			res.add((String) object);
		}
		
		return res;
	}
	
	public List<Double> asDoubles(String column) throws ClassCastException {
		List<Double> res = new ArrayList<Double>();
		for(Object object : result.get(column)){
			res.add((Double) object);
		}
		
		return res;
	}
	
	public List<Integer> asIntegers(String column) throws ClassCastException {
		List<Integer> res = new ArrayList<Integer>();
		for(Object object : result.get(column)){
			res.add((Integer) object);
		}
		
		return res;
	}
}
