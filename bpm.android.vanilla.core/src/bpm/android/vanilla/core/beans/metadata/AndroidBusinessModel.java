package bpm.android.vanilla.core.beans.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class AndroidBusinessModel implements Serializable {

	private String name;
	private AndroidMetadata parent;
	private List<AndroidBusinessPackage> packages = new ArrayList<AndroidBusinessPackage>();

	public AndroidBusinessModel() {
	}

	public AndroidBusinessModel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addBusinessPackage(AndroidBusinessPackage androidPackage) {
		this.packages.add(androidPackage);
	}

	public List<AndroidBusinessPackage> getBusinessPackages() {
		return packages;
	}

	public void setBusinessPackages(List<AndroidBusinessPackage> androidPackages) {
		this.packages = androidPackages;
	}

	public AndroidMetadata getParent() {
		return parent;
	}

	public void setParent(AndroidMetadata parent) {
		this.parent = parent;
	}
}
