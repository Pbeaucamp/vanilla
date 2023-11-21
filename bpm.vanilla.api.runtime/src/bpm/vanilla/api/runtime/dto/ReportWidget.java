package bpm.vanilla.api.runtime.dto;

public abstract class ReportWidget {

	protected int id;
	protected String type;
	
	public ReportWidget() {
		
	}
	
	public ReportWidget(int id,String type) {
		this.id = id;
		this.type = type;
	}

	
	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void setId(int id) {
		this.id = id;
	}


	public void setType(String type) {
		this.type = type;
	}
	
	
}
