package bpm.workflow.commons.beans;

import java.io.Serializable;

public class ActivityOutput implements Serializable {

	public static enum OutputType {
		CHART, CSV
	}

	private static final long serialVersionUID = 1L;

	private String name = "";
	private String path = "";

	private OutputType type = OutputType.CHART;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public OutputType getType() {
		return type;
	}

	public void setType(OutputType type) {
		this.type = type;
	}

}
