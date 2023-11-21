package bpm.gwt.aklabox.commons.client.panels;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class NavigationPanel extends CollapseWidget {

	private static NavigationPanelUiBinder uiBinder = GWT.create(NavigationPanelUiBinder.class);

	interface NavigationPanelUiBinder extends UiBinder<Widget, NavigationPanel> {
	}

	interface MyStyle extends CssResource {
		String contentPanel();

		String navigationPanelViewer();
	}

	@UiField
	MyStyle style;

	@UiField
	Image imgExpand;

	@UiField
	SimplePanel contentPanel;

	private StackLayoutPanel stackPanel;
	private CollapseWidget collapseWidget;

	public NavigationPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		this.stackPanel = new StackLayoutPanel(Unit.PX);
		stackPanel.addStyleName(style.contentPanel());

		contentPanel.setWidget(stackPanel);

		imgExpand.setVisible(false);
	}

	public void setPanels(List<StackNavigationPanel> panels) {
		this.collapseWidget = null;
		if (panels != null) {
			for (StackNavigationPanel panel : panels) {
				StackHeader header = panel.buildHeader();
				addMenu(header, panel);

				if (collapseWidget == null) {
					this.collapseWidget = header;
				}
			}
		}
	}

	private void addMenu(StackHeader menu, Widget panel) {
		stackPanel.add(panel, menu, 50);
	}

	@Override
	public void onCollapseClick(ClickEvent event) {
		if (collapseWidget != null) {
			collapseWidget.onCollapseClick(event);
		}
	}

	@Override
	@UiHandler("imgExpand")
	public void onExpandClick(ClickEvent event) {
		if (collapseWidget != null) {
			collapseWidget.onExpandClick(event);
		}
	}

	@Override
	public Image getImgExpand() {
		return imgExpand;
	}

	@Override
	public List<Widget> getCollapseWidgets() {
		List<Widget> widgets = new ArrayList<>();
		widgets.add(contentPanel);
		return widgets;
	}

	@Override
	protected void additionnalCollapseTreatment(boolean isCollapse) { }
}
