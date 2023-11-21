package bpm.fmloader.client.dto;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ObservatoireDTO implements IsSerializable {
	
	private int id;
	private String name;
	private List<ThemeDTO> themes;
	
	public ObservatoireDTO() {
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
	public List<ThemeDTO> getThemes() {
		return themes;
	}
	public void setThemes(List<ThemeDTO> themes) {
		this.themes = themes;
	}
	
	
}
