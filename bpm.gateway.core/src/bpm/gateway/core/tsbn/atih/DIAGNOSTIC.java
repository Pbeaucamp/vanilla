package bpm.gateway.core.tsbn.atih;

public class DIAGNOSTIC {

	public enum Type {
		PRINCIPAL, ASSOCIEE
	}

	//Key
	private String charId;
	private String entId;
	
	private Type type;
	private String value;

	public DIAGNOSTIC(String charId, String entId, Type type, String value) {
		this.charId = charId;
		this.entId = entId;
		this.type = type;
		this.value = value;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
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
