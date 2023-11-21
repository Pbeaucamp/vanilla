package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KpiMapBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private int mapId;
	private List<Integer> metricsIds = new ArrayList<Integer>();
	private List<Integer> levelIds = new ArrayList<Integer>();
	private Date startDate;
	private Date endDate;

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public List<Integer> getMetricsIds() {
		return metricsIds;
	}

	public void setMetricsIds(List<Integer> metricsIds) {
		this.metricsIds = metricsIds;
	}

	public List<Integer> getLevelIds() {
		return levelIds;
	}

	public void setLevelIds(List<Integer> levelIds) {
		this.levelIds = levelIds;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
