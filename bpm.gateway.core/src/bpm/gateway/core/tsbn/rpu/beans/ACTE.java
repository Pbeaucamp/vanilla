package bpm.gateway.core.tsbn.rpu.beans;

public class ACTE {

	//Key
	private String charId;
	private String entId;
	
	private String value;

	public ACTE(String charId, String entId, String value) {
		this.charId = charId;
		this.entId = entId;
		this.value = value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setCharId(String charId) {
		this.charId = charId;
	}

	public String getCharId() {
		return charId;
	}

	public void setEntId(String entId) {
		this.entId = entId;
	}

	public String getEntId() {
		return entId;
	}
}
