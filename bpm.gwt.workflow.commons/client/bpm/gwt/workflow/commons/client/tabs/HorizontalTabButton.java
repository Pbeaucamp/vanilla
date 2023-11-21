package bpm.gwt.workflow.commons.client.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class HorizontalTabButton extends Composite implements ClickHandler {

	private static TabHeaderUiBinder uiBinder = GWT.create(TabHeaderUiBinder.class);

	interface TabHeaderUiBinder extends UiBinder<Widget, HorizontalTabButton> {
	}

	interface MyStyle extends CssResource {
		String btnCollapse();
		String btnExpand();
		String btnExpandSelected();
		String btnCollapseSelected();
	}

	@UiField
	MyStyle style;

	@UiField
	FocusPanel focusPanel;

	@UiField
	Label lblTitle;

	@UiField
	Image icon;

	private HorizontalTab tab;
	private HorizontalTabManager tabManager;

	private boolean isSelected = false;
	private boolean isExpand = true;

	public HorizontalTabButton(String title, ImageResource icon, HorizontalTab tab, HorizontalTabManager tabManager) {
		initWidget(uiBinder.createAndBindUi(this));
		this.tab = tab;
		this.tabManager = tabManager;

		focusPanel.setTitle(title);
		lblTitle.setText(title);

		focusPanel.addClickHandler(this);

		this.icon.setVisible(false);
		this.icon.setResource(icon);
	}

	@Override
	public void onClick(ClickEvent event) {
		tabManager.selectTab(this);
	}

	public void setSelected(boolean selected) {
		this.isSelected = selected;
		if (selected) {
			if (isExpand) {
				this.focusPanel.removeStyleName(style.btnCollapseSelected());
				this.focusPanel.addStyleName(style.btnExpandSelected());
			}
			else {
				this.focusPanel.removeStyleName(style.btnExpandSelected());
				this.focusPanel.addStyleName(style.btnCollapseSelected());
			}
		}
		else {
			this.focusPanel.removeStyleName(style.btnCollapseSelected());
			this.focusPanel.removeStyleName(style.btnExpandSelected());
		}
	}

	public HorizontalTab getTab() {
		return tab;
	}

	public void collapse(boolean isExpand) {
		this.isExpand = isExpand;
		if (isExpand) {
			this.focusPanel.removeStyleName(style.btnCollapse());
			this.focusPanel.addStyleName(style.btnExpand());
		}
		else {
			this.focusPanel.removeStyleName(style.btnExpand());
			this.focusPanel.addStyleName(style.btnCollapse());
		}

		lblTitle.setVisible(isExpand);
		icon.setVisible(!isExpand);
		
		setSelected(isSelected);
	}
}
