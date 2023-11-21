package bpm.vanilla.platform.core.beans.alerts;



public class AlertKpi implements IAlertInformation {

	private static final long serialVersionUID = -3806440683920367931L;
	
	public enum KpiEvent {
		KPI("KPI");
		
		private String name;
		
		private KpiEvent(String name) {
			this.name = name;
		}
		
		public String getLabel() {
			return this.name;
		}
	}
	
	private KpiEvent subtypeEvent;

	private int metricId;

	private int axisId;

	private int levelIndex;

	private String levelValue;
	
	public void setSubtypeEvent(KpiEvent subtypeEvent) {
		this.subtypeEvent = subtypeEvent;
	}

	public KpiEvent getSubtypeEvent() {
		return subtypeEvent;
	}
	
	public int getMetricId() {
		return metricId;
	}

	public void setMetricId(int metricId) {
		this.metricId = metricId;
	}

	public int getAxisId() {
		return axisId;
	}

	public void setAxisId(int axisId) {
		this.axisId = axisId;
	}

	public int getLevelIndex() {
		return levelIndex;
	}

	public void setLevelIndex(int levelIndex) {
		this.levelIndex = levelIndex;
	}

	public String getLevelValue() {
		return levelValue;
	}

	public void setLevelValue(String levelValue) {
		this.levelValue = levelValue;
	}

	@Override
	public boolean equals(Object o) {
		return metricId == ((AlertKpi)o).getMetricId() && axisId == ((AlertKpi)o).getAxisId() 
				&& levelIndex == ((AlertKpi)o).getLevelIndex() && levelValue == ((AlertKpi)o).getLevelValue() 
				&& subtypeEvent == ((AlertKpi)o).getSubtypeEvent();
	}
	
	@Override
	public String getSubtypeEventName() {
		return subtypeEvent.toString();
	}
	
	@Override
	public String getSubtypeEventLabel() {
		return subtypeEvent.getLabel();
	}
}
