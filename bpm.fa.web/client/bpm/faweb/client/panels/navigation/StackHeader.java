package bpm.faweb.client.panels.navigation;

import bpm.faweb.client.MainPanel;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class StackHeader extends Composite {

	private static StackHeaderUiBinder uiBinder = GWT.create(StackHeaderUiBinder.class);

	interface StackHeaderUiBinder extends UiBinder<Widget, StackHeader> {
	}
	
	interface MyStyle extends CssResource {
		String toolbar();
	}
	
	@UiField
	MyStyle style;

	@UiField
	Image imgTypeViewer, imgCollapse;

	@UiField
	Label lblTypeViewer;

	@UiField
	HTMLPanel toolbarNavigation;

	private MainPanel mainPanel;
	
	public StackHeader(MainPanel mainPanel, String headerName, boolean showCollapse) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		
		lblTypeViewer.setText(headerName);
		lblTypeViewer.setTitle(headerName);

		toolbarNavigation.setStyleName(style.toolbar());
		toolbarNavigation.addStyleName(VanillaCSS.NAVIGATION_TOOLBAR);
		
		if(!showCollapse) {
			imgCollapse.setVisible(false);
		}
	}
	
	public void updateHeader(String headerName) {
		lblTypeViewer.setText(headerName);
		lblTypeViewer.setTitle(headerName);
	}

	@UiHandler("imgCollapse")
	public void onCollapseClick(ClickEvent event) {
		mainPanel.collapseNavigationPanel(false);
	}

}
