package bpm.android.vanilla.core.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class AndroidRepository implements Serializable {
	
	private int id;
	private String name;
	
	private List<AndroidObject> repositoryContent;
	
	public AndroidRepository() { }
	
	public AndroidRepository(int id, String name) {
		this.id = id;
		this.name = name;
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
	
	public String toString() {
		return name;
	}

	public List<AndroidObject> getRepositoryContent() {
		return repositoryContent;
	}

	public void setRepositoryContent(List<AndroidObject> repositoryContent) {
		this.repositoryContent = repositoryContent;
	}

	public void addRepositoryObject(AndroidObject androidObject) {
		if(repositoryContent == null){
			this.repositoryContent = new ArrayList<AndroidObject>();
		}
		this.repositoryContent.add(androidObject);
	}
}
