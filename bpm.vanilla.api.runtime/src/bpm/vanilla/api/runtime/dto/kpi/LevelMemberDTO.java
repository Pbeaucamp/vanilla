package bpm.vanilla.api.runtime.dto.kpi;

import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.utils.LevelMember;

public class LevelMemberDTO {

	private String value;
	private String label;	
	private List<LevelMemberDTO> children;
	
	public LevelMemberDTO(LevelMember levelMember) {
		this.value = levelMember.getValue();
		this.label = levelMember.getLabel();
		this.children = new ArrayList<>();
		List<LevelMember> levels = levelMember.getChildren();
		for (LevelMember lvl : levels) {
			this.children.add(new LevelMemberDTO(lvl));
		}
	}

	
	
	@Override
	public String toString() {
		return "LevelMemberDTO [label=" + label + ", children=" + children + "]";
	}


	public String getLabel() {
		return label;
	}

	public List<LevelMemberDTO> getChildren() {
		return children;
	}
	
}
