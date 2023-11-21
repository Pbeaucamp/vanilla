package bpm.faweb.shared;

import java.util.ArrayList;
import java.util.List;

public class IDirectoryItemDTO extends TreeParentDTO {

	private String subType = "";
	private int directoryId;
	
	private List<ItemCube> cubes = new ArrayList<ItemCube>();

	public IDirectoryItemDTO(String name) {
		super(name);
	}

	public IDirectoryItemDTO() {
		super();
	}

	public int getDirectoryId() {
		return directoryId;
	}

	public void setDirectoryId(int directoryId) {
		this.directoryId = directoryId;
	}

	public String getSubType() {
		return subType;
	}

	public List<ItemCube> getCubes() {
		return cubes;
	}

	public void addCube(ItemCube cube) {
		if (cubes == null) {
			this.cubes = new ArrayList<ItemCube>();
		}

		cube.setParent(this);

		this.cubes.add(cube);
	}

}
