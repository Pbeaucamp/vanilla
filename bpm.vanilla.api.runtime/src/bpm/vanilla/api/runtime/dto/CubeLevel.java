package bpm.vanilla.api.runtime.dto;

import bpm.fa.api.olap.Level;

public class CubeLevel {
	private int depth;
	private String uname;
	private String name;

	public CubeLevel(Level lvl) {
		depth = lvl.getLevelNumber();
		uname = lvl.getUniqueName();
		name = lvl.getName();
	}

	public int getDepth() {
		return depth;
	}

	public String getUname() {
		return uname;
	}

	public String getName() {
		return name;
	}

}
