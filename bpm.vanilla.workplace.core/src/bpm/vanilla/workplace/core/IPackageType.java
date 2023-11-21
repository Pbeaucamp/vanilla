package bpm.vanilla.workplace.core;


public enum IPackageType {
	ONLY_METADATAS(0),
	ONLY_DOCUMENTS(1),
	METADATA_AND_DOCUMENTS(2);
	
	private int type;
	
	private IPackageType(int type) {
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
}
