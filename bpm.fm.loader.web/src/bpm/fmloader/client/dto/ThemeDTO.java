package bpm.fmloader.client.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ThemeDTO implements IsSerializable {
	private int id;
	private String name;
	
	public ThemeDTO() {
		super();
	}
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
	
	
}
