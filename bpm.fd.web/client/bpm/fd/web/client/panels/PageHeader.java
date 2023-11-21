package bpm.fd.web.client.panels;

import bpm.fd.web.client.utils.PageManager;
import bpm.fd.web.client.utils.ToolHelper;
import bpm.fd.web.client.widgets.DashboardWidget;
import bpm.gwt.commons.client.panels.AbstractTabHeader;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PageHeader extends AbstractTabHeader implements DragOverHandler, DragLeaveHandler, DropHandler {

	private static final String TAB_ID = "TabId";

	private static TabHeaderUiBinder uiBinder = GWT.create(TabHeaderUiBinder.class);

	interface TabHeaderUiBinder extends UiBinder<Widget, PageHeader> {
	}

	interface MyStyle extends CssResource {
		String btnSmall();

		String btnSmallSelected();

		String btnBig();

		String btnBigSelected();

		String textSmall();

		String textBig();

		String labelSmall();

		String labelBig();

		String arrowLeft();

		String arrowRight();
		String draggableFix();
	}

	@UiField
	MyStyle style;

	@UiField
	FocusPanel focusPanel, panelLeft, panelRight;

	@UiField
	Label lblTitle;

	@UiField
	TextBox txtTitle;

	@UiField
	Image btnClose;

	private boolean big;
	private boolean isDraggable;

	private TabManager tabManager;
	private String generatedId;
	
	private boolean isModified = false;

	public PageHeader(String title, Tab tab, TabManager tabManager, PageManager pageManager, boolean isCloseable, boolean big, boolean isDraggable) {
		super(tab, tabManager);
		initWidget(uiBinder.createAndBindUi(this));
		this.big = big;
		this.isDraggable = isDraggable;
		this.tabManager = tabManager;

		focusPanel.setTitle(title);
		lblTitle.setText(title);
		txtTitle.setText(title);

		focusPanel.addClickHandler(this);
		focusPanel.setStyleName(big ? style.btnBig() : style.btnSmall());
		lblTitle.setStyleName(big ? style.labelBig() : style.labelSmall());
		txtTitle.setStyleName(big ? style.textBig() : style.textSmall());

		btnClose.setVisible(isCloseable);

		focusPanel.addDragOverHandler(this);
		focusPanel.addDragLeaveHandler(this);
		// focusPanel.addDropHandler(this);

		if (pageManager != null) {
			this.generatedId = pageManager.addWidget(this);
		}

		if (isDraggable) {
			panelLeft.addDragOverHandler(this);
			panelLeft.addDragLeaveHandler(this);
			panelLeft.addDropHandler(this);

			panelRight.addDragOverHandler(this);
			panelRight.addDragLeaveHandler(this);
			panelRight.addDropHandler(this);

			focusPanel.addStyleName(style.draggableFix());
			focusPanel.getElement().setDraggable(Element.DRAGGABLE_TRUE);
			focusPanel.addDragStartHandler(dragStartHandler);
		}
		else {
			panelLeft.setVisible(false);
			panelRight.setVisible(false);
		}
	}

	@UiHandler("btnClose")
	public void onCloseClick(ClickEvent event) {
		event.stopPropagation();
		
		close();
	}

	@Override
	public void setSelected(boolean selected) {
		if (selected) {
			this.focusPanel.addStyleName(big ? style.btnBigSelected() : style.btnSmallSelected());
			this.focusPanel.addStyleName(VanillaCSS.TAB_BTN_SELECTED);
		}
		else {
			this.focusPanel.removeStyleName(big ? style.btnBigSelected() : style.btnSmallSelected());
			this.focusPanel.removeStyleName(VanillaCSS.TAB_BTN_SELECTED);
		}
	}

	@Override
	public void applySize(double percentage) {
		focusPanel.getElement().getStyle().setWidth(percentage, Unit.PCT);
	}

	private int getIndex() {
		return getTabManager() != null ? getTabManager().getIndex(this) : 0;
	}

	@Override
	public void setTitle(String title) {
		focusPanel.setTitle(title);
		lblTitle.setText(title);
		txtTitle.setText(title);

		getTab().setTabHeaderTitle(title);
		setModified(isModified);
	}

	@UiHandler("focusPanel")
	public void onDoubleClick(DoubleClickEvent event) {
		updateUi(true);
	}

	@UiHandler("txtTitle")
	public void onDeselect(BlurEvent event) {
		updateUi(false);
	}

	@UiHandler("txtTitle")
	public void onEnter(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			updateUi(false);
		}
	}

	private void updateUi(boolean displayTextbox) {
		lblTitle.setVisible(!displayTextbox);
		txtTitle.setVisible(displayTextbox);
		txtTitle.setFocus(displayTextbox);

		if (!displayTextbox) {
			String pageTitle = txtTitle.getText();
			setTitle(pageTitle);
		}
		else {
			String title = txtTitle.getText();
			txtTitle.setCursorPos(title.length());
		}
	}

	@Override
	public void onDrop(DropEvent event) {
		event.preventDefault();

		String tabId = event.getData(PageHeader.TAB_ID);
		if (isDraggable && tabId != null) {
			int index = -1;
			if (event.getSource() == panelLeft) {
				index = getIndex();
			}
			else if (event.getSource() == panelRight) {
				index = getIndex() + 1;
			}
			tabManager.updatePosition(tabId, index);
		}
		panelLeft.removeStyleName(style.arrowLeft());
		panelRight.removeStyleName(style.arrowRight());
	}

	@Override
	public void onDragLeave(DragLeaveEvent event) {
		panelLeft.removeStyleName(style.arrowLeft());
		panelRight.removeStyleName(style.arrowRight());
	}

	@Override
	public void onDragOver(DragOverEvent event) {
		String data = event.getData(ToolHelper.TYPE_TOOL);
		if (data != null && !data.isEmpty() && getTabManager() != null) {
			getTabManager().selectTab(this);
		}

		String generatedId = event.getData(DashboardWidget.WIDGET_ID);
		if (generatedId != null && !generatedId.isEmpty() && getTabManager() != null) {
			getTabManager().selectTab(this);
		}

		String tabId = event.getData(PageHeader.TAB_ID);
		if (isDraggable && tabId != null) {
			if (event.getSource() == panelLeft) {
				panelLeft.addStyleName(style.arrowLeft());
			}
			else if (event.getSource() == panelRight) {
				panelRight.addStyleName(style.arrowRight());
			}
		}
	}

	private DragStartHandler dragStartHandler = new DragStartHandler() {

		@Override
		public void onDragStart(DragStartEvent event) {
			if (isDraggable) {
				int absoluteLeft = focusPanel.getAbsoluteLeft();
				int absoluteTop = focusPanel.getAbsoluteTop();

				int x = event.getNativeEvent().getClientX() - absoluteLeft;
				int y = event.getNativeEvent().getClientY() - absoluteTop;

				event.setData(PageHeader.TAB_ID, generatedId);
				event.getDataTransfer().setDragImage(getElement(), x, y);
			}
		}
	};

	@Override
	public void setModified(boolean isModified) {
		this.isModified = isModified;

		String title = lblTitle.getText();
		if (isModified) {
			lblTitle.setText(title + " *");
		}
		else if (title.contains("*")){
			title = title.substring(0, title.indexOf("*") - 2);
			lblTitle.setText(title);
		}
	}

	// private DropHandler tabIndexHandler = new DropHandler() {
	//
	// @Override
	// public void onDrop(DropEvent event) {
	//
	// }
	// };
}
