package bpm.gwt.commons.shared.fmdt.metadata;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Row implements IsSerializable {

	private List<String> values;

	public Row() { }
	
	public List<String> getValues() {
		return values;
	}

	public void addValue(String value) {
		if (values == null) {
			this.values = new ArrayList<String>();
		}
		this.values.add(value);
	}
}
