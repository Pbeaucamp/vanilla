package bpm.gateway.core.transformations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.internal.Row;

public class ConsistencyMapping implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Transformation input;
	private String inputName;
	private HashMap<String, String> mappings = new HashMap<String, String>();
	
	private List<Row> values;
	
	private Comparator<Row> sortComparator;
	private Comparator<Row> mappingComparator;
	
	private boolean keepInOutput = true;
	
	private ConsitencyTransformation parent;
	
	public ConsistencyMapping() {}

	public Transformation getInput() {
		return input;
	}

	public void setInput(Transformation input) {
		this.input = input;
		inputName = input.getName();
	}

	public HashMap<String, String> getMappings() {
		return mappings;
	}

	public void setMappings(HashMap<String, String> mappings) {
		this.mappings = mappings;
	}

	public List<Row> getValues() {
		return values;
	}

	public void setValues(List<Row> values) {
		this.values = values;
	}
	
	public void addValue(Row row) {
		if(values == null) {
			values = new ArrayList<Row>();
		}
		values.add(row);
	}

	public void clearValues() {
		this.values = null;
	}
	
	public Comparator<Row> getSortComparator() {
		if(sortComparator == null) {
			sortComparator = new Comparator<Row>() {
				@Override
				public int compare(Row o1, Row o2) {
					for(String map : mappings.values()) {
						try {
							List<StreamElement> elements = input.getDescriptor(null).getStreamElements();
							for(int i = 0 ; i < elements.size() ; i ++) {
								if(elements.get(i).equals(map)) {
									Comparable v1 = (Comparable) o1.get(i);
									Comparable v2 = (Comparable) o2.get(i);
									int res = v1.compareTo(v2);
									if(res != 0) {
										return res;
									}
								}
							}
						} catch (ServerException e) {
							e.printStackTrace();
						}
					}
					return 0;
				}
			};
		}
		return sortComparator;
	}
	
	public Comparator<Row> getMappingComparator() {
		if(mappingComparator == null) {
			mappingComparator = new Comparator<Row>() {
				@Override
				public int compare(Row o1, Row o2) {
					for(String map : mappings.keySet()) {
						try {
							Comparable v1 = null;
							List<StreamElement> elements = input.getDescriptor(null).getStreamElements();
							for(int i = 0 ; i < elements.size() ; i ++) {
								if(elements.get(i).name.equals(mappings.get(map))) {
									v1 = (Comparable) o1.get(i);
									break;
								}
							}
							Comparable v2 = null;
							List<StreamElement> masterElements = parent.getDescriptor(null).getStreamElements();
							for(int i = 0 ; i < masterElements.size() ; i ++) {
								if(masterElements.get(i).name.equals(map)) {
									v2 = (Comparable) o2.get(i);
									break;
								}
							}
							int res = 0;
							if(Number.class.isAssignableFrom(v1.getClass()) && Number.class.isAssignableFrom(v2.getClass())) {
								res = new Double(Double.parseDouble(v1.toString())).compareTo(new Double(Double.parseDouble(v2.toString())));
							}
							else {
								res = v1.compareTo(v2);
							}
							
							if(res != 0) {
								return res;
							}
							
						} catch (ServerException e) {
							e.printStackTrace();
						}
					}
					return 0;
				}
			};
		}
		return mappingComparator;
	}

	public void sortValues() {
		Collections.sort(values, getSortComparator());
	}

	public boolean isKeepInOutput() {
		return keepInOutput;
	}

	public void setKeepInOutput(boolean keepInOutput) {
		this.keepInOutput = keepInOutput;
	}
	
	public void addMapping(int master, int mapping) throws ServerException {
		String masterName = parent.getDescriptor(null).getStreamElements().get(master).name;
		String mappingName = this.getInput().getDescriptor(null).getStreamElements().get(mapping).name;
		
		addMapping(masterName, mappingName);
	}
	
	public void addMapping(String master, String mapping) {
		mappings.put(master, mapping);
	}
	
	public void removeMapping(int master, int mapping) throws ServerException {
		String masterName = parent.getDescriptor(null).getStreamElements().get(master).name;
		String mappingName = this.getInput().getDescriptor(null).getStreamElements().get(mapping).name;
		
		remove(masterName, mappingName);
	}
	
	public void remove(String master, String mapping) {
		mappings.remove(master);
	}

	public String getInputName() {
		return inputName;
	}

	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

	public ConsitencyTransformation getParent() {
		return parent;
	}

	public void setParent(ConsitencyTransformation parent) {
		this.parent = parent;
	}

}
