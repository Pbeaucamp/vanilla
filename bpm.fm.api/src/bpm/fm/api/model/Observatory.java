package bpm.fm.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.Group;

@Entity
@Table (name = "fm_observatory")
public class Observatory implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;

	@Column (name = "observatory_name")
	private String name;
	
	@Column (name = "creation_date")
	private Date creationDate = new Date();
	
	@Transient
	private List<Theme> themes = new ArrayList<Theme>();
	
	@Transient	
	List<Group> groups = new ArrayList<Group>();

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

	public List<Theme> getThemes() {
		if(themes == null) {
			themes = new ArrayList<Theme>();
		}
		return themes;
	}

	public void setThemes(List<Theme> themes) {
		this.themes = themes;
	}
	
	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public void addTheme(Theme th) {
		if(!themes.contains(th)) {
			themes.add(th);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return id == ((Observatory)obj).getId();
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
}
