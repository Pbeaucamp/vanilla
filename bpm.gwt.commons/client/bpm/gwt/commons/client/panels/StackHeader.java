package bpm.gwt.commons.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.utils.VanillaCSS;

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

	public StackHeader(CollapsePanel collapsePanel, String title, boolean showCollapse, int rightMargin, String backgroundColor) {
		initWidget(uiBinder.createAndBindUi(this));
		this.collapsePanel = collapsePanel;
		this.rightMargin = rightMargin;

		txtTitle.setText(title);
		txtTitle.getElement();

		//toolbarPanel.addStyleName(VanillaCSS.TOOLBAR_LIGHT);
		//Pretty BULLSHIT
//		if(title == LabelsConstants.lblCnst.RuleCatText()) {
//			toolbarPanel.getElement().getStyle().setBackgroundColor("#F0F8FF");
//		}
//		else if(title == LabelsConstants.lblCnst.RuleCatNumber()) {
//			toolbarPanel.getElement().getStyle().setBackgroundColor("#98FB98");
//		}
//		else if(title == LabelsConstants.lblCnst.RuleCatDate()) {
//			toolbarPanel.getElement().getStyle().setBackgroundColor("#FFFFE0");
//		}
//		else if(title == LabelsConstants.lblCnst.RuleCatCalc()) {
//			toolbarPanel.getElement().getStyle().setBackgroundColor("#DCDCDC");
//		}
//		else if(title == LabelsConstants.lblCnst.RuleCatAdvanced()) {
//			toolbarPanel.getElement().getStyle().setBackgroundColor("#E6E6FA");
//		}
		if (backgroundColor != null && !backgroundColor.isEmpty()) {
			toolbarPanel.getElement().getStyle().setBackgroundColor(backgroundColor);
		}
		else {
			toolbarPanel.addStyleName(VanillaCSS.TOOLBAR_LIGHT);
		}


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
