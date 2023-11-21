package bpm.fd.web.client;

import java.util.List;

import bpm.fm.api.model.Theme;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.platform.core.beans.VanillaImage;
import bpm.vanilla.platform.core.beans.data.Datasource;

public class ClientSession {

	private static ClientSession instance;
	
	private List<Datasource> datasources;
	private List<VanillaImage> images;
	
	private List<MapVanilla> maps;

	private List<Theme> themes;

	private ClientSession() {

	}

	public static ClientSession getInstance() {
		if (instance == null) {
			instance = new ClientSession();
		}
		return instance;
	}

	public List<Datasource> getDatasources() {
		return datasources;
	}

	public void setDatasources(List<Datasource> datasources) {
		this.datasources = datasources;
	}

	public void setImages(List<VanillaImage> images) {
		this.images = images;
	}
	
	public List<VanillaImage> getImages() {
		return images;
	}

	public List<MapVanilla> getMaps() {
		return maps;
	}

	public void setMaps(List<MapVanilla> maps) {
		this.maps = maps;
	}

	public void setThemes(List<Theme> result) {
		this.themes = result;
	}

	public List<Theme> getThemes() {
		return themes;
	}
	
}
