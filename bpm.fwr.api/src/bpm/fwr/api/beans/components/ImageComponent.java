package bpm.fwr.api.beans.components;


public class ImageComponent extends ReportComponent {

	private String name;
	private String url;
	private String type;
	private String imageData;

	public ImageComponent() {
	}

	public ImageComponent(String name, String url, String type, String imageData) {
		this.name = name;
		this.url = url;
		this.setType(type);
		this.imageData = imageData;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setImageData(String imageData) {
		this.imageData = imageData;
	}

	public String getImageData() {
		return imageData;
	}
}
