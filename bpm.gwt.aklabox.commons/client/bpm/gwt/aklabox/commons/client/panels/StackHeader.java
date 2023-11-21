package bpm.gwt.aklabox.commons.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.aklabox.commons.client.utils.ThemeCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class StackHeader extends CollapseWidget {

	private static StackHeaderUiBinder uiBinder = GWT.create(StackHeaderUiBinder.class);

	interface StackHeaderUiBinder extends UiBinder<Widget, StackHeader> {
	}
	
	interface MyStyle extends CssResource {
		String toolbarPanel();
	}
	
	@UiField
	MyStyle style;

	@UiField
	Image imgCollapse;

	@UiField
	Label txtTitle;

	@UiField
	HTMLPanel toolbarPanel;
	
	private CollapsePanel collapsePanel;
	private int rightMargin;

	public StackHeader(CollapsePanel collapsePanel, String title, boolean showCollapse, int rightMargin) {
		initWidget(uiBinder.createAndBindUi(this));
		this.collapsePanel = collapsePanel;
		this.rightMargin = rightMargin;

		txtTitle.setText(title);
		txtTitle.setTitle(title);

		toolbarPanel.addStyleName(ThemeCSS.TOOLBAR_LIGHT);

		if (!showCollapse) {
			imgCollapse.setVisible(false);
		}
	}

	@Override
	@UiHandler("imgCollapse")
	public void onCollapseClick(ClickEvent event) {
		collapsePanel.collapseNavigationPanel(false, rightMargin);
	}

	@Override
	public void onExpandClick(ClickEvent event) {
		collapsePanel.collapseNavigationPanel(true);
	}

	@Override
	public Image getImgExpand() {
		return null;
	}

	@Override
	public List<Widget> getCollapseWidgets() {
		List<Widget> widgets = new ArrayList<>();
		widgets.add(toolbarPanel);
		return widgets;
	}

	@Override
	protected void additionnalCollapseTreatment(boolean isCollapse) { }
}
