package bpm.gwt.workflow.commons.client.tabs;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.CollapseWidget;
import bpm.gwt.commons.client.panels.ICatchCollapsePanel;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class MultipleTabPanel extends CollapseWidget implements HorizontalTabManager {

	private static MultipleTabPanelUiBinder uiBinder = GWT.create(MultipleTabPanelUiBinder.class);

	interface MultipleTabPanelUiBinder extends UiBinder<Widget, MultipleTabPanel> {
	}

	@UiField
	HTMLPanel panelToolbar, panelTabs;

	@UiField
	Image imgExpand, imgCollapse;

	private CollapsePanel collapsePanel;

	private HorizontalTabButton selectedBtn;
	private List<HorizontalTabButton> buttons;

	public MultipleTabPanel(CollapsePanel collapsePanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.collapsePanel = collapsePanel;
		this.buttons = new ArrayList<>();

		imgCollapse.addStyleName(VanillaCSS.RIGHT_TOOLBAR);
		panelToolbar.addStyleName(VanillaCSS.RIGHT_TOOLBAR);
		imgExpand.setVisible(false);
	}

	public void addTab(HorizontalTab tab, boolean select) {
		tab.setTabManager(this);

		HorizontalTabButton button = tab.buildTabHeader();
		panelTabs.add(button);
		buttons.add(button);

		if (select) {
			selectTab(button);
		}
		
		if(tab instanceof ICatchCollapsePanel) {
			List<ICatchCollapsePanel> list = collapsePanel.getCatchCollapsePanel();
			list.add((ICatchCollapsePanel) tab);
			collapsePanel.setCatchCollapsePanel(list);
		}
	}

	@Override
	public void selectTab(HorizontalTabButton tabButton) {
		if (selectedBtn != null) {
			selectedBtn.setSelected(false);
		}

		this.selectedBtn = tabButton;
		tabButton.setSelected(true);

		collapsePanel.setCenterPanel(tabButton.getTab());
	}

	@Override
	@UiHandler("imgCollapse")
	public void onCollapseClick(ClickEvent event) {
		collapsePanel.collapseNavigationPanel(false);
	}

	@Override
	@UiHandler("imgExpand")
	public void onExpandClick(ClickEvent event) {
		collapsePanel.collapseNavigationPanel(true);
	}

	@Override
	public Image getImgExpand() {
		return imgExpand;
	}

	@Override
	public List<Widget> getCollapseWidgets() {
		List<Widget> widgets = new ArrayList<>();
		widgets.add(imgCollapse);
		widgets.add(panelToolbar);
		return widgets;
	}

	@Override
	protected void additionnalCollapseTreatment(boolean isCollapse) {
		if (buttons != null) {
			for (HorizontalTabButton btn : buttons) {
				btn.collapse(isCollapse);
			}
		}

		int padding = 0;
		if (isCollapse) {
			padding = 5;
		}
		panelTabs.getElement().getStyle().setPadding(padding, Unit.PX);
	}
}
