package bpm.vanilla.platform.core.beans.data;

import java.io.Serializable;

public class DatasetParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private int index;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
