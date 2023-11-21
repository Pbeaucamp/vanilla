package bpm.fd.web.client.widgets;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.core.ComponentParameter;
import bpm.fd.core.DashboardComponent;
import bpm.fd.core.component.ComponentType;
import bpm.fd.core.component.ContainerComponent;
import bpm.fd.core.component.ContentComponent;
import bpm.fd.core.component.DivComponent;
import bpm.fd.core.component.DrillStackableCellComponent;
import bpm.fd.core.component.StackableCellComponent;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.actions.ActionManager;
import bpm.fd.web.client.actions.WidgetAction;
import bpm.fd.web.client.handlers.HasComponentSelectionHandler;
import bpm.fd.web.client.panels.IDropPanel;
import bpm.fd.web.client.popup.PropertiesPopup;
import bpm.fd.web.client.utils.ToolHelper;
import bpm.fd.web.client.utils.WidgetDropHelper;
import bpm.fd.web.client.utils.WidgetManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ContainerWidget extends WidgetComposite implements DragOverHandler, DragLeaveHandler, DropHandler, IDropPanel {

	private static DivWidgetUiBinder uiBinder = GWT.create(DivWidgetUiBinder.class);

	interface DivWidgetUiBinder extends UiBinder<Widget, ContainerWidget> {
	}
	
	interface MyStyle extends CssResource {
		String contentPanelOnly();
		String draggableFix();
	}
	
	@UiField
	MyStyle style;

	@UiField
	FocusPanel focus;

	@UiField
	FlowPanel toolbar;

	@UiField
	SimplePanel containerPanel;
	
	@UiField
	HTMLPanel contentPanel;

	private ActionManager actionManager;
	private WidgetManager widgetManager;

	private boolean propertiesOpen = false;

	private List<ContentButton> widgets = new ArrayList<>();
	private ContentButton selectedButton;
	private ContentWidget selectedWidget;

	public ContainerWidget(IWait waitPanel, HasComponentSelectionHandler componentHandler, ActionManager actionManager, WidgetManager widgetManager, ComponentType tool, int positionLeft, int positionTop) {
		super(waitPanel, componentHandler, positionLeft, positionTop);
		initWidget(uiBinder.createAndBindUi(this));

		buildContent(actionManager, widgetManager, tool);
	}

	public ContainerWidget(IWait waitPanel, HasComponentSelectionHandler componentHandler, ActionManager actionManager, WidgetManager widgetManager, DashboardComponent component) {
		super(waitPanel, componentHandler, component.getLeft(), component.getTop());
		initWidget(uiBinder.createAndBindUi(this));
		this.component = component;

		ComponentType tool = component.getType();
		buildContent(actionManager, widgetManager, tool);
	}

	private void buildContent(ActionManager actionManager, WidgetManager widgetManager, ComponentType tool) {
		this.actionManager = actionManager;
		this.widgetManager = widgetManager;

		buildTool(widgetManager, tool);

		focus.addDragOverHandler(this);
		focus.addDragLeaveHandler(this);
		focus.addDropHandler(this);

		focus.addDoubleClickHandler(this);

		focus.addStyleName(style.draggableFix());
		focus.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		focus.addDragStartHandler(dragStartHandler);
	}

	private void buildTool(WidgetManager widgetManager, ComponentType tool) {
		boolean generateId = true;
		int width = DEFAULT_WIDTH;
		int height = DEFAULT_HEIGHT;

		String name = "";
		if (component != null) {
			if (component.getName() != null) {
				name = component.getName();
				generateId = false;
			}
		}

		switch (tool) {
		case DIV:
			name = "Div";
			if (component == null) {
				component = new DivComponent();
			}
			toolbar.setVisible(false);
			contentPanel.setStyleName(style.contentPanelOnly());
			break;
		case STACKABLE_CELL:
			name = "StackableCell";
			if (component == null) {
				component = new StackableCellComponent();
			}
			break;
		case DRILL_STACKABLE_CELL:
			name = "DrillStackableCell";
			if (component == null) {
				component = new DrillStackableCellComponent();
			}

			break;

		default:
			break;
		}

		setGeneratedId(widgetManager.addWidget(name, ContainerWidget.this, generateId));

		if (component.getName() == null || component.getName().isEmpty()) {
			component.setName(getGeneratedId());
		}
		width = component.getWidth() > 0 ? component.getWidth() : DEFAULT_WIDTH;
		focus.getElement().getStyle().setWidth(width, Unit.PX);
		height = component.getHeight() > 0 ? component.getHeight() : DEFAULT_HEIGHT;
		focus.getElement().getStyle().setHeight(height, Unit.PX);

		loadWidgets(component);
	}

	private void loadWidgets(DashboardComponent component) {
		if (component != null) {
			List<ContainerComponent> containers = ((ContentComponent) component).getContainers();
			if (containers != null) {
				for (ContainerComponent container : containers) {
					ContentWidget contentWidget = addWidget(null);
					if (container.getComponents() != null) {
						for (DashboardComponent comp : container.getComponents()) {
							ComponentType typeTool = comp.getType();
							if (typeTool == null) {
								MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), comp.getClass() + " " + Labels.lblCnst.IsNotSupported());
								continue;
							}

							WidgetComposite widget = buildWidget(getWaitPanel(), getComponentHandler(), actionManager, widgetManager, typeTool, comp);
							contentWidget.addWidget(widget);
						}
					}
				}
			}
			else {
				addWidget(null);
			}
		}
		else {
			addWidget(null);
		}
	}	
	
	private WidgetComposite buildWidget(IWait waitPanel, HasComponentSelectionHandler componentSelectionHandler, ActionManager actionManager, WidgetManager widgetManager, ComponentType typeTool, DashboardComponent component) {
		if (ToolHelper.isContentWidget(typeTool)) {
			return new ContainerWidget(waitPanel, componentSelectionHandler, actionManager, widgetManager, component);
		}
		else {
			return new DashboardWidget(this, waitPanel, componentSelectionHandler, widgetManager, component);
		}
	}

	@UiHandler("btnAddPanel")
	public ContentWidget addWidget(ClickEvent event) {
		ContentWidget widget = new ContentWidget();
		ContentButton button = new ContentButton(this, widget);

		widgets.add(button);
		int index = widgets.size() - 1;
		toolbar.insert(button, index);

		select(button);

		addParameter(index);
		
		return widget;
	}

	private void addParameter(int index) {
		if (component instanceof DrillStackableCellComponent) {
			ComponentParameter parameter = new ComponentParameter();
			parameter.setName(Labels.lblCnst.Tab() + "_" + (index + 1));
			component.addParameter(parameter);
		}
	}

	public void removeWidget(ContentButton button) {
		if (widgets.size() <= 1) {
			return;
		}

		int index = widgets.indexOf(button);

		toolbar.remove(button);
		widgets.remove(button);

		removeParameter(index);

		if (button.getContentWidget().getParent() == containerPanel) {
			select(widgets.get(0));
		}
	}

	private void removeParameter(int index) {
		component.removeParameter(index);

		for (int i = 0; i < component.getParameters().size(); i++) {
			ComponentParameter parameter = component.getParameters().get(i);
			parameter.setName(Labels.lblCnst.Tab() + "_" + (i + 1));
		}
	}

	public void select(ContentButton button) {
		if (selectedButton != null) {
			selectedButton.setSelected(false);
		}
		this.selectedButton = button;
		this.selectedButton.setSelected(true);
		changeWidget(button.getContentWidget());
	}

	private void changeWidget(ContentWidget widget) {
		this.selectedWidget = widget;
		containerPanel.setWidget(widget);
	}

	private DragStartHandler dragStartHandler = new DragStartHandler() {

		@Override
		public void onDragStart(DragStartEvent event) {
			int absoluteLeft = focus.getAbsoluteLeft();
			int absoluteTop = focus.getAbsoluteTop();

			int x = event.getNativeEvent().getClientX() - absoluteLeft;
			int y = event.getNativeEvent().getClientY() - absoluteTop;

			event.setData(DashboardWidget.WIDGET_ID, getGeneratedId());
			event.setData(DashboardWidget.ABSOLUTE_LEFT, String.valueOf(x));
			event.setData(DashboardWidget.ABSOLUTE_TOP, String.valueOf(y));
			event.getDataTransfer().setDragImage(getElement(), x, y);
		}
	};

	@Override
	public void onDrop(DropEvent event) {
		event.preventDefault();
		event.stopPropagation();
		
		WidgetDropHelper.dropWidget(event, containerPanel, getComponentHandler(), this, actionManager, widgetManager, getWaitPanel(), selectedWidget, false, true);
	}

	public DashboardComponent getComponent() {
		int top = getTop();
		int left = getLeft();

		int width = getWidth();
		int height = getHeight();

		List<ContainerComponent> containers = new ArrayList<>();
		if (widgets != null) {
			for (ContentButton button : widgets) {
				ContentWidget widget = button.getContentWidget();
				containers.add(widget.getContainer());
			}
		}

		component.setLeft(left);
		component.setTop(top);
		component.setWidth(width);
		component.setHeight(height);

		if (component instanceof ContentComponent) {
			((ContentComponent) component).setContainers(containers);
		}

		return component;
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		event.stopPropagation();

		if (!propertiesOpen) {
			PropertiesPopup popup = new PropertiesPopup(getWaitPanel(), getComponentHandler(), component, false);
			popup.center();
			popup.addCloseHandler(this);

			propertiesOpen = true;
		}
	}
	
	@Override
	public void onClose(CloseEvent<PopupPanel> event) {
		this.propertiesOpen = false;
	}

	@Override
	public void onDragLeave(DragLeaveEvent event) {
	}

	@Override
	public void onDragOver(DragOverEvent event) {
	}

	@Override
	public void addWidget(WidgetComposite widget, int positionLeft, int positionTop) {
		widget.setDropPanel(this);

		widget.getElement().getStyle().setTop(positionTop, Unit.PX);
		widget.getElement().getStyle().setLeft(positionLeft, Unit.PX);

		selectedWidget.addWidget(widget);
		getComponentHandler().setModified(true);
	}

	@Override
	public void removeWidget(WidgetComposite widget, boolean addAction) {
		if (addAction) {
			WidgetAction action = new WidgetAction(widgetManager, this, widget, widget.getLeft(), widget.getTop());
			actionManager.launchAction(action, true);
		}
		else {
			widget.removeFromParent();
		}
		getComponentHandler().setModified(true);
	}

	@Override
	public int getWidth() {
		String width = focus.getElement().getStyle().getWidth();
		try {
			return Integer.parseInt(width.split("px")[0]);
		} catch (Exception e) {
		}

		return 100;
	}

	@Override
	public int getHeight() {
		String height = focus.getElement().getStyle().getHeight();
		try {
			return Integer.parseInt(height.split("px")[0]);
		} catch (Exception e) {
		}

		return 100;
	}

	@Override
	public WidgetManager getWidgetManager() {
		return widgetManager;
	}

	@Override
	public ActionManager getActionManager() {
		return actionManager;
	}
}
