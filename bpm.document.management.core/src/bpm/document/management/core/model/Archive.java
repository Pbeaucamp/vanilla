package bpm.document.management.core.model;

/**
 * 
 * Not used for now
 *
 */
public class Archive implements IAdminDematObject {

	private static final long serialVersionUID = 1L;
	
	private AkladematAdminEntity<Archive> parent;

	public Archive() {
		super();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setParent(AkladematAdminEntity<? extends IAdminDematObject> parent) {
		this.parent = (AkladematAdminEntity<Archive>) parent;
	}
	
	public AkladematAdminEntity<Archive> getParent() {
		return parent;
	}
}
