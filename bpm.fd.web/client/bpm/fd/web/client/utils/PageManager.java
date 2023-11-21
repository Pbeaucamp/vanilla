package bpm.fd.web.client.utils;

import java.util.HashMap;

import bpm.fd.web.client.panels.PageHeader;

public class PageManager {

	public static final String ID = "ID";

	private HashMap<String, PageHeader> widgets;

	public PageManager() {
		this.widgets = new HashMap<>();
	}

	public String addWidget(PageHeader widget) {
		String generatedId = String.valueOf(new Object().hashCode());
		widgets.put(generatedId, widget);
		return generatedId;
	}

	public PageHeader getWidget(String widgetId) {
		return widgets.get(widgetId);
	}

	public void removeWidget(String widgetId) {
		widgets.remove(widgetId);
	}
}
