package bpm.vanilla.platform.core.beans.resources;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table (name = "d4c_item_visualization")
public class D4CItemVisualization extends D4CItem {

	private static final long serialVersionUID = 1L;

	@Column(name = "url", length = 10000000)
	private String url;

	public D4CItemVisualization() {
		super();
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
}
