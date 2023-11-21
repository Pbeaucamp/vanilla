package bpm.fd.core.component;

import bpm.fd.core.DashboardComponent;

public class LabelComponent extends DashboardComponent {

	private static final long serialVersionUID = 1L;

	@Override
	public ComponentType getType() {
		return ComponentType.LABEL;
	}

	@Override
	protected void clearData() { }
}
