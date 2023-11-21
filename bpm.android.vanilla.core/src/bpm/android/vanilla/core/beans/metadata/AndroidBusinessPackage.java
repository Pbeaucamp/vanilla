package bpm.android.vanilla.core.beans.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.dataset.IResource;

@SuppressWarnings("serial")
public class AndroidBusinessPackage implements Serializable {

	private String name;
	private AndroidBusinessModel parent;
	private List<AndroidBusinessTable> tables = new ArrayList<AndroidBusinessTable>();
	private List<IResource> resources = new ArrayList<IResource>();

	public AndroidBusinessPackage() {
	}

	public AndroidBusinessPackage(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addBusinessTable(AndroidBusinessTable table) {
		this.tables.add(table);
	}

	public void addResource(IResource resource) {
		this.resources.add(resource);
	}

	public List<AndroidBusinessTable> getBusinessTables() {
		return tables;
	}

	public List<IResource> getResources() {
		return resources;
	}

	public void setBusinessTables(List<AndroidBusinessTable> tables) {
		this.tables = tables;
	}

	public void setResources(List<IResource> resources) {
		this.resources = resources;
	}

	public AndroidBusinessModel getParent() {
		return parent;
	}

	public void setParent(AndroidBusinessModel parent) {
		this.parent = parent;
	}
}
