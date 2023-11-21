package bpm.aklabox.workflow.core.model.resources;

public class StandardForm extends Resource {

	private static final long serialVersionUID = 1L;

	private String description;
	private String baseImage;
	
	private int selectedAklaboxServer=0;
	private int linkedAklaboxForm=0;

	public StandardForm(String name){
		this.name = name;
	}
	
	public StandardForm(String name, int userId, String description) {

		this.name = name;
		this.userId = userId;
		
	}

	public StandardForm() {
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBaseImage() {
		return baseImage;
	}

	public void setBaseImage(String baseImage) {
		this.baseImage = baseImage;
	}

	public int getSelectedAklaboxServer() {
		return selectedAklaboxServer;
	}

	public void setSelectedAklaboxServer(int selectedAklaboxServer) {
		this.selectedAklaboxServer = selectedAklaboxServer;
	}

	public int getLinkedAklaboxForm() {
		return linkedAklaboxForm;
	}

	public void setLinkedAklaboxForm(int linkedAklaboxForm) {
		this.linkedAklaboxForm = linkedAklaboxForm;
	}

}
