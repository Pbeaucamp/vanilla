package bpm.fd.web.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.DashboardPage;
import bpm.fd.web.client.actions.ActionManager;
import bpm.fd.web.client.actions.WidgetAction;
import bpm.fd.web.client.utils.PageManager;
import bpm.fd.web.client.utils.WidgetDropHelper;
import bpm.fd.web.client.utils.WidgetManager;
import bpm.fd.web.client.widgets.WidgetComposite;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.panels.AbstractTabHeader;
import bpm.gwt.commons.client.panels.Tab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class PagePanel extends Tab implements DragOverHandler, DragLeaveHandler, DropHandler, IDropPanel {

	private static PagePanelUiBinder uiBinder = GWT.create(PagePanelUiBinder.class);

	interface PagePanelUiBinder extends UiBinder<Widget, PagePanel> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();
	}

	@UiField
	MyStyle style;

	@UiField
	FocusPanel focusPanel;

	@UiField
	HTMLPanel panelContent;

	private IWait waitPanel;
	private WorkspacePanel workspacePanel;
	
	private WidgetManager widgetManager;
	private ActionManager actionManager;
	private PageManager pageManager;

	private List<WidgetComposite> items;
	
	private String pageName;

	public PagePanel(IWait waitPanel, WorkspacePanel workspacePanel, ActionManager actionManager, WidgetManager widgetManager, PageManager pageManager, String pageName, String title) {
		super(workspacePanel, title, true);
		this.add(uiBinder.createAndBindUi(this));
		this.addStyleName(style.mainPanel());
		
		this.waitPanel = waitPanel;
		this.workspacePanel = workspacePanel;
		this.actionManager = actionManager;
		this.widgetManager = widgetManager;
		this.pageManager = pageManager;
		this.pageName = pageName;

		focusPanel.addDragOverHandler(this);
		focusPanel.addDragLeaveHandler(this);
		focusPanel.addDropHandler(this);
	}

	public DashboardPage buildPage(boolean isTemplate) {
		if (pageName == null) {
			pageName = new Object().hashCode() + "";
		}
		String label = getTabTitle();

		List<DashboardComponent> components = new ArrayList<>();
		if (items != null) {
			for (WidgetComposite widget : items) {
				DashboardComponent component = widget.getComponent();
				if (isTemplate) {
					component.clear();
				}
				components.add(component);
			}
		}

		DashboardPage page = new DashboardPage();
		page.setName(pageName);
		page.setLabel(label);
		page.setComponents(components);
		return page;
	}
	
	public void displayGrid(boolean displayGrid, String styleName) {
		if (displayGrid) {
			panelContent.addStyleName(styleName);
		}
		else {
			panelContent.removeStyleName(styleName);
		}
	}

	@Override
	public void addWidget(WidgetComposite widget, int positionLeft, int positionTop) {
		widget.setDropPanel(this);
		panelContent.add(widget);
		
		widget.getElement().getStyle().setTop(positionTop, Unit.PX);
		widget.getElement().getStyle().setLeft(positionLeft, Unit.PX);

		if (items == null) {
			items = new ArrayList<WidgetComposite>();
		}
		items.add(widget);
		
		widget.updatePosition(positionLeft, positionTop);
		workspacePanel.setModified(true);
	}

	@Override
	public void removeWidget(WidgetComposite widget, boolean addAction) {
		if (addAction) {
			WidgetAction action = new WidgetAction(widgetManager, this, widget, widget.getLeft(), widget.getTop());
			actionManager.launchAction(action, true);
		}
		else {
			widget.removeFromParent();
			items.remove(widget);
		}
		workspacePanel.setModified(true);
	}

	@Override
	public AbstractTabHeader buildTabHeader() {
		if (tabHeader == null) {
			tabHeader = new PageHeader(getTabTitle(), this, getTabManager(), pageManager, isCloseable(), false, true);
		}
		return tabHeader;
	}

	@Override
	public void onDrop(DropEvent event) {
		event.preventDefault();
		event.stopPropagation();
		
		WidgetDropHelper.dropWidget(event, panelContent, workspacePanel, this, actionManager, widgetManager, waitPanel, panelContent, false, false);
	}

	@Override
	public void onDragLeave(DragLeaveEvent event) {
	}

	@Override
	public void onDragOver(DragOverEvent event) {
	}

	@Override
	public ActionManager getActionManager() {
		return actionManager;
	}
}
