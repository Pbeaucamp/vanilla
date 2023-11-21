package bpm.fd.core.component;

import java.io.Serializable;

public class CubeElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum CubeElementType {
		UNKNOWN, DIMENSION, MEASURE
	}

	private CubeElementType type;
	private String name;
	private String caption;
	private String uniqueName;
	
	private boolean visible;
	
	public CubeElement() { }

	public CubeElement(CubeElementType type, String name, String caption, String uniqueName, boolean visible) {
		this.type = type;
		this.name = name;
		this.caption = caption;
		this.uniqueName = uniqueName;
		this.visible = visible;
	}

	public CubeElementType getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public String getCaption() {
		return caption;
	}
	
	public String getUniqueName() {
		return uniqueName;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
