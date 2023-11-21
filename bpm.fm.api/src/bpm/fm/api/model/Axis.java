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

@Entity
@Table (name = "fm_axis")
public class Axis implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "axis_name")
	private String name;
	
	@Column (name = "creation_date")
	private Date creationDate = new Date();
	
	@Transient
	private List<Level> children;

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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public List<Level> getChildren() {
		return children;
	}

	public void setChildren(List<Level> children) {
		for(Level level : children) {
			level.setParent(this);
		}
		this.children = children;
	}

	public void addChild(Level level) {
		if(children == null) {
			children = new ArrayList<Level>();
		}
		if(!children.contains(level)) {
			level.setParent(this);
			children.add(level);
		}
	}

	public void removeChild(Level ds) {
		this.children.remove(ds);
	}
	
	@Override
	public boolean equals(Object obj) {
		return id == ((Axis)obj).getId();
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	public boolean isOnOneTable() {
		if(children.size() > 1) {
			String previousTable = null;
			for(Level lvl : children) {
				if(previousTable == null) {
					previousTable = lvl.getTableName();
				}
				else if(!previousTable.equals(lvl.getTableName())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
