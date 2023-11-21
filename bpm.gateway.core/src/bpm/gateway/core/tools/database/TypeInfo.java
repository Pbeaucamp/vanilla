package bpm.gateway.core.tools.database;

public class TypeInfo {
	private String name;
	private String matching;
	private boolean hasPrecision = true;
	
	
	
	
	
	
	/**
	 * @return the hasPrecision
	 */
	public boolean isHasPrecision() {
		return hasPrecision;
	}
	/**
	 * @param hasPrecision the hasPrecision to set
	 */
	public void setHasPrecision(boolean hasPrecision) {
		this.hasPrecision = hasPrecision;
	}
	
	public void setHasPrecision(String hasPrecision) {
		this.hasPrecision = Boolean.parseBoolean(hasPrecision);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMatching() {
		return matching;
	}
	public void setMatching(String matching) {
		this.matching = matching;
	}
	
	
	
	
}
