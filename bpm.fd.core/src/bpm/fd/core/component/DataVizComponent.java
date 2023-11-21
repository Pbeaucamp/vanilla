package bpm.fd.core.component;

import bpm.data.viz.core.preparation.DataPreparation;
import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentOption;

public class DataVizComponent extends DashboardComponent implements IComponentOption {

	private static final long serialVersionUID = 1L;

	private Integer datavizId;
	private DataPreparation dataviz;
	private DataVizOption option = new DataVizOption();

	@Override
	public ComponentType getType() {
		return ComponentType.DATAVIZ;
	}

	@Override
	protected void clearData() {
		// TODO Auto-generated method stub

	}

	public Integer getDatavizId() {
		return datavizId;
	}

	public void setDatavizId(Integer datavizId) {
		this.datavizId = datavizId;
	}

	public DataPreparation getDataviz() {
		return dataviz;
	}

	public void setDataviz(DataPreparation dataviz) {
		this.dataviz = dataviz;
		if(dataviz != null ) {
			datavizId = dataviz.getId();
		}
		else {
			datavizId = null;
		}
	}

	public DataVizOption getOption() {
		return option;
	}

}
