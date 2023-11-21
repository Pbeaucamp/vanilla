package bpm.android.vanilla.core.beans;

import java.io.Serializable;

public class AndroidCubeView implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String image;
	
	public AndroidCubeView() { }
	
	public AndroidCubeView(int id, String name, String image) {
		this.id = id;
		this.name = name;
		this.image = image;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getImage() {
		return image;
	}
}
