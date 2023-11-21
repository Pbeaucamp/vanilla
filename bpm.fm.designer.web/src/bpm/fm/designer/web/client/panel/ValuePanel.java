package bpm.fm.designer.web.client.panel;

import bpm.fm.api.model.Metric;

import com.google.gwt.user.client.ui.Composite;

public abstract class ValuePanel extends Composite {

	public abstract void fillData();

	public abstract Metric getMetric();
	
	public abstract void refresh(Metric metric);
}
