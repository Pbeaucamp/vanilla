package bpm.smart.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "r_script")
public class RScript implements Serializable{

	private static final long serialVersionUID = 1L;

	public enum ScriptType {
		R("R"), MARKDOWN("MARKDOWN");

	    private final String name;       

	    private ScriptType(String s) {
	        name = s;
	    }

	    public boolean equalsName(String otherName) {
	        return (otherName == null) ? false : name.equals(otherName);
	    }

	    public String toString() {
	       return this.name;
	    }
		
	}
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;

	@Column (name = "idProject")
	private int idProject;
	
	@Column (name = "name")
	private String name;
	
	@Column (name = "free")
	private boolean free;
	
	@Column (name = "holdingUsername")
	private String holdingUsername;
	
	@Column (name = "scriptType")
	private String scriptType;
	
	@Column (name = "comment")
	private String comment;
	
	public RScript() {
		super();
		this.holdingUsername = "";
		this.name = "";
		this.scriptType = ScriptType.R.name;
		this.comment = "";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIdProject() {
		return idProject;
	}

	public void setIdProject(int idProject) {
		this.idProject = idProject;
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	public String getHoldingUsername() {
		return holdingUsername;
	}

	public void setHoldingUsername(String holdingUsername) {
		this.holdingUsername = holdingUsername;
	}

	public String getScriptType() {
		return scriptType;
	}

	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
}
