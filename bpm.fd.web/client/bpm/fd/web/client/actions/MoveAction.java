package bpm.fd.web.client.actions;

import java.util.List;

import bpm.fd.web.client.panels.IDropPanel;
import bpm.fd.web.client.widgets.DashboardWidget;
import bpm.fd.web.client.widgets.WidgetComposite;

public class MoveAction extends Action {

	private IDropPanel dropPanel;
	private IDropPanel oldPanel;

	private WidgetComposite item;
	private List<DashboardWidget> widgets;
	
	private int positionLeft;
	private int positionTop;
	
	private int previousPositionLeft;
	private int previousPositionTop;
	
	private int offsetX, offsetY;

	private TypeAction type;
	
	public MoveAction(WidgetComposite item, int positionLeft, int positionTop, int previousPositionLeft, int previousPositionTop, List<DashboardWidget> widgets, int offsetX, int offsetY) {
		buildContent(item, positionLeft, positionTop, previousPositionLeft, previousPositionTop, TypeAction.MOVE, widgets, offsetX, offsetY);
	}
	
	public MoveAction(IDropPanel dropPanel, IDropPanel oldPanel, WidgetComposite item, int positionLeft, int positionTop, int previousPositionLeft, int previousPositionTop, List<DashboardWidget> widgets, int offsetX, int offsetY) {
		buildContent(item, positionLeft, positionTop, previousPositionLeft, previousPositionTop, TypeAction.MOVE_PANEL, widgets, offsetX, offsetY);
		this.dropPanel = dropPanel;
		this.oldPanel = oldPanel;
	}
	
	private void buildContent(WidgetComposite item, int positionLeft, int positionTop, int previousPositionLeft, int previousPositionTop, TypeAction type, List<DashboardWidget> widgets, int offsetX, int offsetY) {
		this.item = item;
		this.positionLeft = positionLeft;
		this.positionTop = positionTop;
		this.previousPositionLeft = previousPositionLeft;
		this.previousPositionTop = previousPositionTop;
		this.type = type;
		this.widgets = widgets;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	@Override
	public void doAction() {
		if (type == TypeAction.MOVE) {
			item.updatePosition(positionLeft, positionTop);
		}
		else if (type == TypeAction.MOVE_PANEL) {
			item.removeFromWidget();
			dropPanel.addWidget(item, positionLeft, positionTop);
		}
		
		if (widgets != null) {
			for (DashboardWidget w : widgets) {
				if (!w.equals(item)) {
					
					if (type == TypeAction.MOVE) {
						w.updatePosition(w.getLeft() + offsetX, w.getTop() + offsetY);
					}
					else if (type == TypeAction.MOVE_PANEL) {
						w.removeFromWidget();
						dropPanel.addWidget(w, w.getLeft() + offsetX, w.getTop() + offsetY);
					}
				}
			}
		}
	}

	@Override
	public void undoAction() {
		if (type == TypeAction.MOVE) {
			item.updatePosition(previousPositionLeft, previousPositionTop);
		}
		else if (type == TypeAction.MOVE_PANEL) {
			item.removeFromWidget();
			oldPanel.addWidget(item, previousPositionLeft, previousPositionTop);
		}
		
		if (widgets != null) {
			for (DashboardWidget w : widgets) {
				if (!w.equals(item)) {
					
					if (type == TypeAction.MOVE) {
						w.updatePosition(w.getLeft() - offsetX, w.getTop() - offsetY);
					}
					else if (type == TypeAction.MOVE_PANEL) {
						w.removeFromWidget();
						dropPanel.addWidget(w, w.getLeft() - offsetX, w.getTop() - offsetY);
					}
				}
			}
		}
	}
}
