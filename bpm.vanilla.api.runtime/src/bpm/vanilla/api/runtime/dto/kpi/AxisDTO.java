package bpm.vanilla.api.runtime.dto.kpi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Level;



public class AxisDTO {
	
	private int id;
	
	private String name;

	private Date creationDate;
	
	private List<LevelDTO> children;
	
	public AxisDTO (Axis axis) {
		this.id = axis.getId();
		this.name = axis.getName();
		this.creationDate = axis.getCreationDate();
		this.children = new ArrayList<>();
		for ( Level lvl : axis.getChildren()) {
			this.children.add(new LevelDTO(lvl));
		}
	}
	
	@Override
	public String toString() {
		return "AxisDTO [id=" + id + ", name=" + name + ", creationDate=" + creationDate + ", children=" + children + "]";
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public List<LevelDTO> getChildren() {
		return children;
	}
	
	
}
