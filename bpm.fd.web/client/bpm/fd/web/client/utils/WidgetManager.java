package bpm.fd.web.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import bpm.fd.core.DashboardComponent;
import bpm.fd.web.client.widgets.DashboardWidget;
import bpm.fd.web.client.widgets.WidgetComposite;

public class WidgetManager {

	public static final String ID = "ID";

	private HashMap<String, WidgetComposite> widgets;

	private List<DashboardWidget> selectedWidgets = new ArrayList<DashboardWidget>();

	public WidgetManager() {
		this.widgets = new HashMap<>();
	}

	public String addWidget(String name, WidgetComposite widget, boolean generateId) {
		String generatedId = name;
		if (generateId) {
			generatedId = name + "_" + String.valueOf(new Object().hashCode());
		}
		widgets.put(generatedId, widget);
		return generatedId;
	}

	public WidgetComposite getWidget(String widgetId) {
		WidgetComposite widget = widgets.get(widgetId);
		return widget;
	}

	public WidgetComposite getWidget(DashboardComponent component) {
		if (widgets != null) {
			for (Entry<String, WidgetComposite> entry : widgets.entrySet()) {
				WidgetComposite composite = entry.getValue();
				if (component.equals(composite.getComponent())) {
					return composite;
				}
			}
		}
		return null;
	}

	public void removeWidget(String widgetId) {
		widgets.remove(widgetId);
	}

	public void selectWidget(DashboardWidget widget) {
		selectedWidgets.add(widget);
	}

	public void deselectWidget(DashboardWidget widget) {
		selectedWidgets.remove(widget);
	}

	public List<DashboardWidget> getSelectedWidgets() {
		return selectedWidgets;
	}

	public void clearWidgetSelection(DashboardWidget dashboardWidget) {
		if (selectedWidgets != null) {
			for (DashboardWidget widget : selectedWidgets) {
				if (dashboardWidget == null || !widget.equals(dashboardWidget)) {
					widget.deselect();
				}
			}
			selectedWidgets.clear();
		}
	}
}
