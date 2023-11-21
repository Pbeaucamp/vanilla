package bpm.gwt.workflow.commons.client.workflow;

import java.util.List;

import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.CollapseWidget;
import bpm.gwt.commons.client.panels.NavigationPanel;
import bpm.gwt.commons.client.panels.StackHeader;
import bpm.gwt.workflow.commons.client.IWorkflowAppManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ToolsPanel extends CollapseWidget {

	private static ToolsPanelOldUiBinder uiBinder = GWT.create(ToolsPanelOldUiBinder.class);

	interface ToolsPanelOldUiBinder extends UiBinder<Widget, ToolsPanel> {
	}

	@UiField
	NavigationPanel navigationPanel;

	private CollapseWidget collapseWidget;

	public ToolsPanel(IWorkflowAppManager appManager, CollapsePanel collapsePanel) {
		initWidget(uiBinder.createAndBindUi(this));

//		for (ToolBoxCategory category : appManager.getCategories(collapsePanel)) {
//			StackHeader header = category.buildHeader();
//			if (collapseWidget == null) {
//				collapseWidget = header;
//			}
//			navigationPanel.addMenu(header, category);
//		}
	}

	@Override
	public void onCollapseClick(ClickEvent event) { }

	@Override
	public void onExpandClick(ClickEvent event) { }

	@Override
	public Image getImgExpand() {
		return collapseWidget.getImgExpand();
	}

	@Override
	public List<Widget> getCollapseWidgets() {
		return collapseWidget.getCollapseWidgets();
	}

	@Override
	protected void additionnalCollapseTreatment(boolean isCollapse) { }
}
