package bpm.vanilla.api.runtime.dto.kpi;

import java.util.Date;

import bpm.fm.api.model.Level;

public class LevelDTO {
	private int id;
	private String name;
	private Date creationDate;
	
	public LevelDTO(Level level) {
		this.id = level.getId();
		this.name = level.getName();
		this.creationDate = level.getCreationDate();
	}

	@Override
	public String toString() {
		return "LevelDTO [id=" + id + ", name=" + name + ", creationDate=" + creationDate + "]";
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
	
	
}
