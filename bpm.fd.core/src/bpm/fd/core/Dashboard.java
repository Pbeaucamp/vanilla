package bpm.fd.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fd.core.component.ComponentType;
import bpm.fd.core.component.EventType;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.repository.IDashboard;

public class Dashboard implements Serializable, IDashboard {

	private static final long serialVersionUID = 1L;
	private String name;
	private String description;

	private Integer itemId;
	private Integer dictionaryId;
	
	private String css;

	private List<DashboardPage> pages;
	private List<DashboardComponent> components;
	
	private List<Dataset> datasets;
	
	private HashMap<EventType, String> eventScript = new HashMap<EventType, String>();

	public Dashboard() {
		for (EventType e : EventType.getAvailablesFor(ComponentType.DASHBOARD)) {
			eventScript.put(e, "");
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public List<DashboardPage> getPages() {
		return pages;
	}

	public void setPages(List<DashboardPage> pages) {
		this.pages = pages;
	}

	public void addPage(DashboardPage page) {
		if (pages == null) {
			pages = new ArrayList<DashboardPage>();
		}
		pages.add(page);
	}

	public void removePage(DashboardPage page) {
		pages.remove(page);
	}

	public Integer getDictionaryId() {
		return dictionaryId;
	}

	public void setDictionaryId(Integer dictionaryId) {
		this.dictionaryId = dictionaryId;
	}

	public List<DashboardComponent> getComponents() {
		return components;
	}

	public void setComponents(List<DashboardComponent> components) {
		this.components = components;
	}
	
	public void addComponent(DashboardComponent component) {
		if(components == null) {
			components = new ArrayList<DashboardComponent>();
		}
		components.add(component);
	}
	
	public void removeComponent(DashboardComponent component) {
		components.remove(component);
	}

	public List<Dataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<Dataset> datasets) {
		this.datasets = datasets;
	}
	
	public void addDataset(Dataset dataset) {
		if(datasets == null) {
			datasets = new ArrayList<Dataset>();
		}
		datasets.add(dataset);
	}
	
	public void removeDataset(Dataset dataset) {
		datasets.remove(dataset);
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}
	
	public List<EventType> getEventsType() {
		return new ArrayList<EventType>(eventScript.keySet());
	}
	
	public HashMap<EventType, String> getEventScript() {
		return eventScript;
	}

	public String getJavaScript(EventType type) {
		return eventScript.get(type);
	}
	
	public void setEventScript(HashMap<EventType, String> eventScript) {
		this.eventScript = eventScript;
	}

	public void setJavaScript(EventType type, String script) {
		if (script != null && eventScript.keySet().contains(type)) {
			eventScript.put(type, script);
		}
	}

	public boolean hasEvents() {
		for (EventType e : eventScript.keySet()) {
			if (eventScript.get(e) != null && !eventScript.get(e).isEmpty()) {
				return true;
			}
		}
		return false;
	}
}
