package bpm.fd.core.component;

import bpm.fd.core.DashboardComponent;

public class ButtonComponent extends DashboardComponent {

	private static final long serialVersionUID = 1L;

	@Override
	public ComponentType getType() {
		return ComponentType.BUTTON;
	}

	@Override
	protected void clearData() { }
}
