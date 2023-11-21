package bpm.faweb.shared;


public class ItemCubeView extends IDirectoryItemDTO {

	private ItemCube parent;
	
	public ItemCubeView() { }
	
	public ItemCubeView(String name) {
		super(name);
	}

	public ItemCube getCubeDto() {
		return parent;
	}
	
	public void setParent(ItemCube parent) {
		this.parent = parent;
	}
}
