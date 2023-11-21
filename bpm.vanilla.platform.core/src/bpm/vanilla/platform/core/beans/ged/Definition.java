package bpm.vanilla.platform.core.beans.ged;

/**
 * Use to store the definition of a Field (Lucene)
 * A System field is required and provide by the system (api)
 * @author vanilla
 *
 */

public class Definition {
	public static final String DEFINITION_NAME = "definition.name";
	
	private int id;
	private String name;
	private String stored;
	private String analyzed;
	private float boostLvl = 1;
	private String system;
	private String required;
	private String multiple;
	private String custom;
	
	public Definition() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public void setId(String id) {
		this.id = new Integer(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStored() {
		return stored;
	}

	public void setStored(String stored) {
		this.stored = stored;
	}

	public String getAnalyzed() {
		return analyzed;
	}
	
	public void setAnalyzed(String analyzed) {
		this.analyzed = analyzed;
	}

	public float getBoostLvl() {
		return boostLvl;
	}

	public void setBoostLvl(float boostLvl) {
		this.boostLvl = boostLvl;
	}
	public void setBoostLvl(String boostLvl) {
		this.boostLvl = new Float(boostLvl);
	}
	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}
	
	
	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}

	public boolean system() {
		return "1".equals(system);
	}
	
	public boolean stored() {
		return "1".equals(this.stored);
	}
	
	public boolean analized() {
		return "1".equals(this.analyzed);
	}
	
	public boolean required() {
		return "1".equals(this.required);
	}
	
	public boolean multiple() {
		return "1".equals(this.multiple);
	}
	
	public boolean custom() {
		return "1".equals(this.custom);
	}

}
