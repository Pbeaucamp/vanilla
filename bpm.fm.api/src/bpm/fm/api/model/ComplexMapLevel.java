package bpm.fm.api.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "fm_complex_level")
public class ComplexMapLevel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "idMap")
	private int idMap;
	
	@Column (name = "idLevel")
	private int idLevel;
	
	@Column (name = "idAxis")
	private int idAxis;

	@Transient
	private Level level;
	
	@Column (name = "icon")
	private String iconUrl = "";
	
	@Column (name = "color")
	private String color = "";
	
	public ComplexMapLevel() {
		super();
	}
	
	public ComplexMapLevel(Level level) {
		super();
		this.level = level;
		this.idLevel = level.getId();
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
		this.idLevel = level.getId();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdMap() {
		return idMap;
	}

	public void setIdMap(int idMap) {
		this.idMap = idMap;
	}

	public int getIdLevel() {
		return idLevel;
	}

	public void setIdLevel(int idLevel) {
		this.idLevel = idLevel;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getIdAxis() {
		return idAxis;
	}

	public void setIdAxis(int idAxis) {
		this.idAxis = idAxis;
	}
	
	
}
