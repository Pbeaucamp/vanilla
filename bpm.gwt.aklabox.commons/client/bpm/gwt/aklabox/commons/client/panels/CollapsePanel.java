package bpm.gwt.aklabox.commons.client.panels;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Helper composite to have a collapsible panel on the left (mainly for navigation)
 * Don't forget to call collapseNavigationPanel() in your class that extend CollapseWidget
 *
 */
public class CollapsePanel extends Composite {

	private static CollapsePanelUiBinder uiBinder = GWT.create(CollapsePanelUiBinder.class);

	interface CollapsePanelUiBinder extends UiBinder<Widget, CollapsePanel> {
	}
	
	@UiField
	SimplePanel leftPanel, leftContent, contentPanel;
	
	@UiField
	DockLayoutPanel dockPanel;
	
	private CollapseWidget leftWidget;
	private List<ICatchCollapsePanel> catchCollapsePanel = new ArrayList<ICatchCollapsePanel>();
	private int widthLeftPanel, widthCollapse;
	
	public CollapsePanel(int widthLeftPanel, int widthCollapse) {
		initWidget(uiBinder.createAndBindUi(this));
		this.widthLeftPanel = widthLeftPanel;
		this.widthCollapse = widthCollapse;
		
		dockPanel.setWidgetSize(leftPanel, widthLeftPanel);
		leftPanel.setWidth(widthLeftPanel + "px");
	}
	
	public void setLeftPanel(CollapseWidget leftWidget) {
		this.leftWidget = leftWidget;
		leftContent.setWidget(leftWidget);
	}
	
	public void setCenterPanel(Widget centerWidget) {
		contentPanel.setWidget(centerWidget);
	}
	
	public void collapseNavigationPanel(boolean isCollapse) {
		collapseNavigationPanel(isCollapse, 5);
	}
	
	public void collapseNavigationPanel(boolean isCollapse, int rightMargin) {
		if (isCollapse) {
			dockPanel.setWidgetSize(leftPanel, widthLeftPanel);
			leftWidget.managePanel(isCollapse);
			leftPanel.setWidth(widthLeftPanel + "px");
		}
		else {
			dockPanel.setWidgetSize(leftPanel, widthCollapse);
			leftWidget.managePanel(isCollapse);
			leftPanel.setWidth((widthCollapse - rightMargin) + "px");
		}
		for(ICatchCollapsePanel wid : catchCollapsePanel){
			wid.onCollapse(isCollapse);
		}
	}

	public List<ICatchCollapsePanel> getCatchCollapsePanel() {
		return catchCollapsePanel;
	}

	public void setCatchCollapsePanel(List<ICatchCollapsePanel> catchCollapsePanel) {
		this.catchCollapsePanel = catchCollapsePanel;
	}
	
	
}
