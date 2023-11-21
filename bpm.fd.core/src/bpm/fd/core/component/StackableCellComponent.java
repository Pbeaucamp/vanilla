package bpm.fd.core.component;


public class StackableCellComponent extends ContentComponent {

	private static final long serialVersionUID = 1L;

	@Override
	public ComponentType getType() {
		return ComponentType.STACKABLE_CELL;
	}

	@Override
	protected void clearData() { }
}
