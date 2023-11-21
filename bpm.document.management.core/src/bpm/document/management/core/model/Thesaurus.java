package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Thesaurus implements Serializable {

	private int id;
	private String name;
	private List<ProposedTag> tags;

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

	public List<ProposedTag> getTags() {
		if(tags == null) {
			tags = new ArrayList<ProposedTag>();
		}
		return tags;
	}

	public void setTags(List<ProposedTag> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return name;
	}
}
