package bpm.faweb.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GroupChart implements IsSerializable{
	
	private String uname;

	private String measure=null;
	private List<SerieChart> series = new ArrayList<SerieChart>();

	public GroupChart(){ }
	
	public GroupChart(String uname){
		this.setUname(uname);
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getUname() {
		return uname;
	}
	
	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}

	public void setSeries(List<SerieChart> series) {
		this.series = series;
	}

	public List<SerieChart> getSeries() {
		return series;
	}
	
	public void addSeries(SerieChart serie){
		this.series.add(serie);
	}
}
