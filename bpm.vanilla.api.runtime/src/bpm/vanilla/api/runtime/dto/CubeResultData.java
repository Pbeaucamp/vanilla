package bpm.vanilla.api.runtime.dto;

public class CubeResultData {
	private String name;
	private float value;
	private String cellID;

	public CubeResultData(String measName, CubeValueCell valCell) {
		name = measName;
		value = valCell.getValue();
		cellID = valCell.getId();
	}

	public String getName() {
		return name;
	}

	public float getValue() {
		return value;
	}

	public String getCellID() {
		return cellID;
	}

}
