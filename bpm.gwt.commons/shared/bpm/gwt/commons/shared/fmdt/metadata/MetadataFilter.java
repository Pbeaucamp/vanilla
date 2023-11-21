package bpm.gwt.commons.shared.fmdt.metadata;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import bpm.vanilla.platform.core.beans.data.DatabaseColumn;

public class MetadataFilter extends MetadataResource implements IsSerializable {

	private List<String> values;
	
	public MetadataFilter() { }
	
	public MetadataFilter(String name, DatabaseColumn column, List<String> values) {
		super(name, column);
		this.values = values;
	}
	
	public List<String> getValues() {
		return values;
	}
	
	public void setValues(List<String> values) {
		this.values = values;
	}
}
