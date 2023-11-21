package bpm.fd.core.component;


public class DivComponent extends ContentComponent {

	private static final long serialVersionUID = 1L;

	@Override
	public ComponentType getType() {
		return ComponentType.DIV;
	}

	@Override
	protected void clearData() { }
}
