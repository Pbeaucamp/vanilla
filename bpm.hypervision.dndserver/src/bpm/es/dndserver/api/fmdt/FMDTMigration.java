package bpm.es.dndserver.api.fmdt;

public class FMDTMigration {

	private int dirItemId;
	private FMDTDataSource source;
	private FMDTDataSource target;
	
	public FMDTMigration(int dirItemId, FMDTDataSource source) {
		this.dirItemId = dirItemId;
		this.source = source;
		
	}
	
	public void setSource(FMDTDataSource source) {
		this.source = source;
	}
	
	public boolean isMappingComplete() {
		return source != null && target != null;
	}
	
	public int getDirItemId() {
		return dirItemId;
	}
	
	public FMDTDataSource getSource() {
		return source;
	}
	
	public void setTarget(FMDTDataSource target) {
		this.target = target;
	}

	public FMDTDataSource getTarget() {
		return target;
	}
}
