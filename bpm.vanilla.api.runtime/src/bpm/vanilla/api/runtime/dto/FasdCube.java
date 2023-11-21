package bpm.vanilla.api.runtime.dto;

public class FasdCube {
	private String id;
	private int fasdID;
	private String name;

	public FasdCube(int _fasdID, String _name) {
		fasdID = _fasdID;
		name = _name;
		id = Integer.toString(fasdID) + name;
	}

	public String getId() {
		return id;
	}

	public int getFasdID() {
		return fasdID;
	}

	public String getName() {
		return name;
	}

}
