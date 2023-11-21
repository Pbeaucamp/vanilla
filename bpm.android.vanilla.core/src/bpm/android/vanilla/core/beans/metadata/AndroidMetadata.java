package bpm.android.vanilla.core.beans.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class AndroidMetadata implements Serializable {

	private int id;
	private String name;
	private List<AndroidBusinessModel> models = new ArrayList<AndroidBusinessModel>();

	private boolean isLoad = false;

	public AndroidMetadata() {
	}

	public AndroidMetadata(int id, String name) {
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

	public void addBusinessModel(AndroidBusinessModel model) {
		if (models == null) {
			models = new ArrayList<AndroidBusinessModel>();
		}
		this.models.add(model);
	}

	public List<AndroidBusinessModel> getBusinessModels() {
		return models;
	}

	public void setBusinessModels(List<AndroidBusinessModel> models) {
		this.models = models;
	}

	public boolean isLoad() {
		return isLoad;
	}

	public void setLoad(boolean isLoad) {
		this.isLoad = isLoad;
	}

	public String toString() {
		return name;
	}
}
