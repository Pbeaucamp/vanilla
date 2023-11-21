package bpm.document.management.core.model;


public class AWS implements IAdminDematObject {

	private static final long serialVersionUID = 1L;

	public enum TypeAWS {
		AWS_MARKET, 
		AWS_LEGALITY
	}

	private TypeAWS typeAWS;
	
	private AkladematAdminEntity<AWS> parent;

	public AWS() {
		super();
	}

	public AWS(TypeAWS typeAWS) {
		super();
		this.typeAWS = typeAWS;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setParent(AkladematAdminEntity<? extends IAdminDematObject> parent) {
		this.parent = (AkladematAdminEntity<AWS>) parent;
	}
	
	public AkladematAdminEntity<AWS> getParent() {
		return parent;
	}
	
	public TypeAWS getTypeAWS() {
		return typeAWS;
	}
	
	public void setTypeAWS(TypeAWS typeAWS) {
		this.typeAWS = typeAWS;
	}
}
