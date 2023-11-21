package bpm.gwt.commons.shared;
public class MapService {
	
	private String name;
	private String url;
	
	public MapService() {
	}
	
	public MapService(String name, String url) {
		this.name = name;
		this.url = url;
	}
	
	public String getName() {
		return name;
	}
	
	public String getUrl() {
		return url;
	}
}