package bpm.document.management.core.model;

/**
 * 
 * Not used for now
 *
 */
public class Helios implements IAdminDematObject {

	private static final long serialVersionUID = 1L;
	
	private AkladematAdminEntity<Helios> parent;

	public Helios() {
		super();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setParent(AkladematAdminEntity<? extends IAdminDematObject> parent) {
		this.parent = (AkladematAdminEntity<Helios>) parent;
	}
	
	public AkladematAdminEntity<Helios> getParent() {
		return parent;
	}
}
