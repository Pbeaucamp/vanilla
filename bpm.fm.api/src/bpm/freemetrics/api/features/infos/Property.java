package bpm.freemetrics.api.features.infos;

public class Property {

	private int id = 0;

	private String name;
	private String propValue;
	private String propDesc; 

	public Property() {
    	super();
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the propValue
	 */
	public String getPropValue() {
		return propValue;
	}

	/**
	 * @param propValue the propValue to set
	 */
	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}

	/**
	 * @return the propDesc
	 */
	public String getPropDesc() {
		return propDesc;
	}

	/**
	 * @param propDesc the propDesc to set
	 */
	public void setPropDesc(String propDesc) {
		this.propDesc = propDesc;
	}
}
