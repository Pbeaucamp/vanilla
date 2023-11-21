package bpm.fd.web.client.actions;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.component.ComponentType;
import bpm.fd.web.client.handlers.HasComponentSelectionHandler;
import bpm.fd.web.client.panels.IDropPanel;
import bpm.fd.web.client.utils.ToolHelper;
import bpm.fd.web.client.utils.WidgetManager;
import bpm.fd.web.client.widgets.ContainerWidget;
import bpm.fd.web.client.widgets.DashboardWidget;
import bpm.fd.web.client.widgets.WidgetComposite;
import bpm.gwt.commons.client.loading.IWait;

public class WidgetAction extends Action {

	private IDropPanel dropPanel;

	private WidgetComposite item;
	private int positionLeft;
	private int positionTop;

	private TypeAction type;

	public WidgetAction(IWait waitPanel, HasComponentSelectionHandler componentSelectionHandler, ActionManager actionManager, WidgetManager widgetManager, IDropPanel dropPanel, ComponentType typeTool, int positionLeft, int positionTop) {
		buildContent(dropPanel, buildWidget(dropPanel, waitPanel, componentSelectionHandler, actionManager, widgetManager, typeTool, positionLeft, positionTop), positionLeft, positionTop, TypeAction.ADD);
	}
	
	public WidgetAction(IWait waitPanel, HasComponentSelectionHandler componentSelectionHandler, WidgetManager appManager, IDropPanel dropPanel, DashboardComponent component, int positionLeft, int positionTop) {
		buildContent(dropPanel, new DashboardWidget(dropPanel, waitPanel, componentSelectionHandler, appManager, component), positionLeft, positionTop, TypeAction.ADD);
	}

	public WidgetAction(WidgetManager appManager, IDropPanel dropPanel, WidgetComposite item, int positionLeft, int positionTop) {
		buildContent(dropPanel, item, positionLeft, positionTop, TypeAction.REMOVE);
	}
	
	private void buildContent(IDropPanel dropPanel, WidgetComposite item, int positionLeft, int positionTop, TypeAction type) {
		this.dropPanel = dropPanel;	
		this.item = item;
		this.positionLeft = positionLeft;
		this.positionTop = positionTop;
		this.type = type;
	}
	
	private WidgetComposite buildWidget(IDropPanel dropPanel, IWait waitPanel, HasComponentSelectionHandler componentSelectionHandler, ActionManager actionManager, WidgetManager widgetManager, ComponentType typeTool, int positionLeft, int positionTop) {
		if (ToolHelper.isContentWidget(typeTool)) {
			return new ContainerWidget(waitPanel, componentSelectionHandler, actionManager, widgetManager, typeTool, positionLeft, positionTop);
		}
		else {
			return new DashboardWidget(dropPanel, waitPanel, componentSelectionHandler, widgetManager, typeTool, positionLeft, positionTop);
		}
	}

	@Override
	public void doAction() {
		if (type == TypeAction.ADD) {
			dropPanel.addWidget(item, positionLeft, positionTop);
		}
		else if (type == TypeAction.REMOVE) {
			item.removeFromWidget();
		}
	}

	@Override
	public void undoAction() {
		if (type == TypeAction.ADD) {
			item.removeFromWidget();
		}
		else if (type == TypeAction.REMOVE) {
			dropPanel.addWidget(item, positionLeft, positionTop);
		}
	}
}
