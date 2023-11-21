package bpm.document.management.core.model;

public class Sign implements IAdminDematObject {

	private static final long serialVersionUID = 1L;
	
	private AkladematAdminEntity<Sign> parent;
	
	private User signatory;

	public Sign() {
		super();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setParent(AkladematAdminEntity<? extends IAdminDematObject> parent) {
		this.parent = (AkladematAdminEntity<Sign>) parent;
	}
	
	public AkladematAdminEntity<Sign> getParent() {
		return parent;
	}

	public void setSignatory(User signatory) {
		this.signatory = signatory;
	}
	
	public User getSignatory() {
		return signatory;
	}
}
