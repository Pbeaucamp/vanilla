package bpm.faweb.shared.infoscube;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ChartInfos implements IsSerializable {
	private List<String> chartGroups;
	private List<String> chartDatas;
	private List<String> chartFilters;
	private String measure;
	private String type;
	private String title;
	private String renderer;

	private String chartSVG;

	public ChartInfos() {
		super();
		chartGroups = new ArrayList<String>();
		chartDatas = new ArrayList<String>();
		chartFilters = new ArrayList<String>();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRenderer() {
		return renderer;
	}

	public void setRenderer(String renderer) {
		this.renderer = renderer;
	}

	public List<String> getChartGroups() {
		return chartGroups;
	}

	public void setChartGroups(List<String> chartGroups) {
		this.chartGroups = chartGroups;
	}

	public void addGroup(String group) {
		chartGroups.add(group);
	}

	public void addData(String data) {
		chartDatas.add(data);
	}

	public List<String> getChartDatas() {
		return chartDatas;
	}

	public void setChartDatas(List<String> chartDatas) {
		this.chartDatas = chartDatas;
	}

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setChartFilters(List<String> chartFilters) {
		this.chartFilters = chartFilters;
	}

	public List<String> getChartFilters() {
		return chartFilters;
	}

	public void addChartFilter(String filter) {
		chartFilters.add(filter);
	}

	public String getChartSVG() {
		return chartSVG;
	}

	public void setChartSVG(String chartSVG) {
		this.chartSVG = chartSVG;
	}

}
