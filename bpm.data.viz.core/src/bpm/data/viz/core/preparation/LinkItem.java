package bpm.data.viz.core.preparation;

import java.io.Serializable;
import java.util.List;

import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class LinkItem implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int itemId;
	private int repositoryId;
	private int groupId;
	
	private String customName;
	private String publicUrl;
	
	private List<LinkItemParam> parameters;
	
	@XStreamOmitField
	private RepositoryItem item;
	
	public LinkItem() {
	}
	
	public LinkItem(RepositoryItem item, String publicUrl, int repositoryId, int groupId) {
		this.item = item;
		this.itemId = item.getId();
		this.customName = item.getName();
		this.publicUrl = publicUrl;
		this.repositoryId = repositoryId;
		this.groupId = groupId;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public int getRepositoryId() {
		return repositoryId;
	}
	
	public int getGroupId() {
		return groupId;
	}
	
	public RepositoryItem getItem() {
		return item;
	}
	
	public void setItem(RepositoryItem item) {
		this.item = item;
	}
	
	public String getCustomName() {
		return customName;
	}
	
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	
	public String getPublicUrl() {
		return publicUrl;
	}
	
	public void setPublicUrl(String publicUrl) {
		this.publicUrl = publicUrl;
	}
	
	public List<LinkItemParam> getParameters() {
		return parameters;
	}
	
	public void setParameters(List<LinkItemParam> parameters) {
		this.parameters = parameters;
	}
}
