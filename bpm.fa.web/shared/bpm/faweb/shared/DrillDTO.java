package bpm.faweb.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A class which represents the cube drill possibilities.
 * This can either be a cube drill or a report drill
 * @author Marc Lanquetin
 *
 */
public class DrillDTO implements IsSerializable {

	public static int CUBE = 0;
	public static int REPORT = 1;
	
	private String name;
	private int type;
	private int itemId;
	private String cubeName;
	
	private List<DrillParameterDTO> parameters;
	
	/**
	 * DON'T USE IT, JUST FOR SERIALIZABLE
	 */
	public DrillDTO(){}
	
	/**
	 * To create a cube drill
	 * @param name
	 * @param itemId
	 * @param cubeName
	 * @param dimension
	 */
	public DrillDTO(String name, int itemId, String cubeName) {
		this.name = name;
		this.itemId = itemId;
		this.cubeName = cubeName;
		type = CUBE;
	}
	
	/**
	 * To create a report drill
	 * @param name
	 * @param itemId
	 * @param parameters
	 */
	public DrillDTO(String name, int itemId, List<DrillParameterDTO> parameters) {
		this.name = name;
		this.itemId = itemId;
		this.parameters = parameters;
		type = REPORT;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public String getCubeName() {
		return cubeName;
	}
	
	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}

	public List<DrillParameterDTO> getParameters() {
		return parameters;
	}
	
	public void setParameters(List<DrillParameterDTO> parameters) {
		this.parameters = parameters;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
