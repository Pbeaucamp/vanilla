package bpm.gwt.commons.shared.analysis;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ChartData implements IsSerializable {

	private String groupValue;
	private List<String> serieValues;
	
	public ChartData() {
	}
	
	public ChartData(String groupValue, List<String> serieValues) {
		this.groupValue = groupValue;
		this.serieValues = serieValues;
	}
	
	public String getGroupValue() {
		return groupValue;
	}

	public String getSerieValues(int index) {
		return serieValues.get(index);
	}
}
