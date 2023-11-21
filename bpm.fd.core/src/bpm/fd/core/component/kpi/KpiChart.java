package bpm.fd.core.component.kpi;

import java.util.List;

import bpm.fd.core.component.ChartComponent;
import bpm.fm.api.model.Level;

public class KpiChart implements KpiElement {

	private static final long serialVersionUID = 1L;
	
	private List<KpiAggreg> aggregs;
	private Level level;
	
	private ChartComponent option = new ChartComponent();

	public ChartComponent getOption() {
		return option;
	}

	public void setOption(ChartComponent option) {
		this.option = option;
	}

	public List<KpiAggreg> getAggregs() {
		return aggregs;
	}

	public void setAggregs(List<KpiAggreg> aggregs) {
		this.aggregs = aggregs;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
}
