package bpm.vanilla.api.runtime.dto;

import java.util.List;

import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.OLAPCube;

public class CubeParameter {

	private String uname;
	private String name;
	private String level;
	private String value;
	private List<String> selectableMembers;

	public CubeParameter(OLAPCube cube, Parameter p) {
		uname = p.getUname();
		name = p.getName();
		level = p.getLevel();
		value = p.getValue();
		selectableMembers = cube.searchOnDimensions("[", level);
	}

	public String getUname() {
		return uname;
	}

	public String getName() {
		return name;
	}

	public String getLevel() {
		return level;
	}

	public String getValue() {
		return value;
	}

	public List<String> getSelectableMembers() {
		return selectableMembers;
	}

}
