package bpm.gwt.workflow.commons.client.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.workflow.commons.client.IWorkflowAppManager;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.actions.ActionManager;
import bpm.gwt.workflow.commons.client.actions.BoxAction;
import bpm.gwt.workflow.commons.client.dialog.WorkflowDialog;
import bpm.gwt.workflow.commons.client.utils.Splitter;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.shared.Constants;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.Link;
import bpm.workflow.commons.beans.TypeActivity;
import bpm.workflow.commons.beans.Workflow;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class WorkspacePanel extends Composite implements NameChecker {

	private static final int DEFAULT_BOTTOM = 260;
	private static final int DEFAULT_HEIGHT = 218;

	private static WorkspacePanelUiBinder uiBinder = GWT.create(WorkspacePanelUiBinder.class);

	interface WorkspacePanelUiBinder extends UiBinder<Widget, WorkspacePanel> {
	}

	interface MyStyle extends CssResource {
		String gridView();

		String move();

		String splitter();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelProperties, panelSplitter;

	@UiField
	FocusPanel panelDrop;

	@UiField
	HTMLPanel mainPanel, panelGrid, panelPropertiesParent;
	
	private IWorkflowAppManager appManager;
	private IWait waitPanel;

	private ActionManager actionManager;
	private CustomController controller;
	private PickupDragController dragController;

	private AbsolutePanel grid;

	private List<BoxItem> items;
	private Workflow currentWorkflow;

	private BoxItem focusItem;
	
	public WorkspacePanel(IWorkflowAppManager appManager, IWait waitPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.appManager = appManager;
		this.waitPanel = waitPanel;
		this.actionManager = new ActionManager();

		controller = new CustomController(actionManager, 2000, 2000);
		controller.getView().addStyleName(style.gridView());
		controller.showGrid(true);
		this.grid = controller.getView();
		this.dragController = new PickupDragController(controller.getView(), true);
		controller.registerDragController(dragController);

		mainPanel.add(new MovePanel(this));
		mainPanel.add(new ZoomPanel(this));
		panelGrid.add(grid);

		panelDrop.addDragOverHandler(dragOverHandler);
		panelDrop.addDragLeaveHandler(dragLeaveHandler);
		panelDrop.addDropHandler(dropHandler);

		panelGrid.getElement().getStyle().setBottom(DEFAULT_BOTTOM, Unit.PX);
		panelPropertiesParent.setHeight(DEFAULT_HEIGHT + "px");

		panelSplitter.setWidget(new Splitter(panelGrid, panelPropertiesParent, style.splitter()));
	}

	public void initNewCreation() {
		if (items == null) {
			items = new ArrayList<BoxItem>();
		}

		BoxItem start = new BoxItem(appManager, this, TypeActivity.START, appManager.getLabel(TypeActivity.START));
		controller.addWidget(start, 15, 50);
		dragController.makeDraggable(start);
		items.add(start);

		BoxItem stop = new BoxItem(appManager, this, TypeActivity.STOP, appManager.getLabel(TypeActivity.STOP));
		controller.addWidget(stop, 550, 50);
		dragController.makeDraggable(stop);
		items.add(stop);
	}

	public void openCreation(Workflow workflow) {
		this.currentWorkflow = workflow;

		List<Activity> activities = workflow.getWorkflowModel().getActivities();
		if (activities != null) {
			// We add the box on the workspace
			for (Activity activity : activities) {
				BoxItem item = new BoxItem(appManager, this, activity);
				addBox(item, activity.getLeft(), activity.getTop());
			}

			List<Link> links = workflow.getWorkflowModel().getLinks();
			for (Link link : links) {
				final BoxItem startItem = findItem(link, items, true);
				final BoxItem endItem = findItem(link, items, false);
				if (startItem != null && endItem != null) {
					ScheduledCommand cmd = new ScheduledCommand() {

						@Override
						public void execute() {
							controller.drawStraightArrowConnection(startItem, endItem);
						}
					};
					Scheduler.get().scheduleDeferred(cmd);
				}
			}
		}
	}

	private BoxItem findItem(Link link, List<BoxItem> items, boolean isStart) {
		if (items == null) {
			return null;
		}

		for (BoxItem item : items) {
			if ((isStart && link.getStartActivity().getName().equals(item.getActivity().getName())) || (!isStart && link.getEndActivity().getName().equals(item.getActivity().getName()))) {
				return item;
			}
		}
		return null;
	}

	@UiHandler("btnSave")
	public void onSave(ClickEvent event) {
		int userId = appManager.getInfoUser().getUser().getId();
		String userName = appManager.getInfoUser().getUser().getLogin();

		boolean modify = false;
		if (currentWorkflow == null || currentWorkflow.getId() <= 0) {
			Workflow workflow = new Workflow();
			workflow.setAuthor(userId, userName);

			this.currentWorkflow = workflow;
		}
		else {
			modify = true;
			this.currentWorkflow.setModificator(userId, userName);
		}

		if (items == null) {
			MessageHelper.openMessageDialog(LabelsCommon.lblCnst.Information(), LabelsCommon.lblCnst.WorkflowNotValid());
			return;
		}

		Workflow w = buildWorkflow(true);
		if (w == null) {
			return;
		}
		else {
			currentWorkflow = w;
		}

		if (!currentWorkflow.isValid()) {
			final boolean finalModify = modify;

			final InformationsDialog dial = new InformationsDialog(LabelsCommon.lblCnst.Information(), LabelsCommon.lblCnst.Confirmation(), LabelsCommon.lblCnst.Cancel(), LabelsCommon.lblCnst.WorkflowNotValidSureSave(), true);
			dial.center();
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {

				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						saveWorkflow(finalModify, currentWorkflow);
					}
				}
			});
		}
		else {
			saveWorkflow(modify, currentWorkflow);
		}
	}

	/**
	 * If checkValidity, the method return null when the workflow is not valid
	 * 
	 * @param checkValidity
	 * @return
	 */
	public Workflow buildWorkflow(boolean checkValidity) {
		if (currentWorkflow == null) {
			currentWorkflow = new Workflow();
		}
		currentWorkflow.getWorkflowModel().clearActivities();
		HashMap<String, BoxItem> itemsMap = new HashMap<String, BoxItem>();
		for (BoxItem item : items) {
			if (!checkValidity || item.isValid()) {
				if (checkValidity && itemsMap.get(item.getName()) != null) {
					MessageHelper.openMessageDialog(LabelsCommon.lblCnst.Information(), LabelsCommon.lblCnst.ItemsCannotHaveSameName());
					return null;
				}
				else {
					itemsMap.put(item.getName(), item);
				}

				try {
					currentWorkflow.getWorkflowModel().addActivity(item.getActivityWithPosition());
				} catch (Exception e) {
					if (!checkValidity) {
						break;
					}
					e.printStackTrace();
					throw e;
				}
				// return null;
			}
			else {
				MessageHelper.openMessageDialog(LabelsCommon.lblCnst.Information(), LabelsCommon.lblCnst.ItemsNotValid());
			}
		}

		currentWorkflow.getWorkflowModel().setLinks(controller.getLinks());

		return currentWorkflow;
	}

	private void saveWorkflow(boolean modify, Workflow workflow) {
		WorkflowDialog dial = new WorkflowDialog(this, workflow, modify);
		dial.center();
	}

	public void saveWorkflow(Workflow workflow, boolean modify) {
		waitPanel.showWaitPart(true);

		AsyncCallback<Workflow> callback = new GwtCallbackWrapper<Workflow>(waitPanel, true) {

			@Override
			public void onSuccess(Workflow result) {
				currentWorkflow = result;

				appManager.reloadConsult();
			}
		}.getAsyncCallback();
		appManager.manageWorkflow(workflow, modify, callback);
	}

	@Override
	public boolean checkIfNameTaken(int id, String value) {
		if (items != null) {
			for (BoxItem item : items) {
				if (item.getName().equals(value)) {
					return true;
				}
			}
		}
		return false;
	}

	@UiHandler("btnUndo")
	public void onUndo(ClickEvent event) {
		actionManager.undoAction();
	}

	@UiHandler("btnRedo")
	public void onRedo(ClickEvent event) {
		actionManager.redoAction();
	}

	public void addBox(BoxItem item, int positionLeft, int positionTop) {
		controller.addWidget(item, positionLeft, positionTop);
		dragController.makeDraggable(item);

		if (items == null) {
			items = new ArrayList<BoxItem>();
		}
		items.add(item);
	}

	public void removeBox(BoxItem item, boolean addAction) {
		if (addAction) {
			BoxAction action = new BoxAction(appManager, this, controller, item, item.getLeft(), item.getTop());
			actionManager.launchAction(action, true);
		}
		else {
			controller.deleteWidget(item);
			items.remove(item);
		}
	}

	public void setZoom(double zoomValue) {
		applyCss(grid.getElement(), "transformOrigin", "top left");
		applyCss(grid.getElement(), "transform", "scale(" + zoomValue + ")");
	}

	public void moveGrid(int xValue, int yValue) {
		int marginTop = getValueFromCss(grid.getElement().getStyle().getMarginTop()) + yValue;
		if (marginTop <= 0 && marginTop >= -2000) {
			grid.getElement().getStyle().setMarginTop(marginTop, Unit.PX);
		}

		int marginLeft = getValueFromCss(grid.getElement().getStyle().getMarginLeft()) + xValue;
		if (marginLeft <= 0 && marginLeft >= -2000) {
			grid.getElement().getStyle().setMarginLeft(marginLeft, Unit.PX);
		}
	}

	private int getValueFromCss(String value) {
		try {
			if (value.indexOf("px") > 0) {
				return Integer.parseInt(value.substring(0, value.indexOf("px")));
			}
			else if (value.indexOf("%") > 0) {
				return Integer.parseInt(value.substring(0, value.indexOf("%")));
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private void applyCss(Element element, String property, String value) {
		element.getStyle().setProperty(property, value);
		System.out.println("Property : " + property + " Value : " + value);
	}

	private DropHandler dropHandler = new DropHandler() {

		@Override
		public void onDrop(DropEvent event) {
			event.preventDefault();

			String data = event.getData(Constants.TYPE_TOOL);
			if (data != null && !data.isEmpty()) {
				Integer typeToolIndex = Integer.parseInt(data);
				TypeActivity typeTool = TypeActivity.valueOf(typeToolIndex);

				int x = event.getNativeEvent().getClientX() - grid.getAbsoluteLeft();
				int y = event.getNativeEvent().getClientY() - grid.getAbsoluteTop();

				BoxAction action = new BoxAction(appManager, WorkspacePanel.this, typeTool, x, y);
				actionManager.launchAction(action, true);
			}
		}
	};

	private DragOverHandler dragOverHandler = new DragOverHandler() {

		@Override
		public void onDragOver(DragOverEvent event) {
		}
	};

	private DragLeaveHandler dragLeaveHandler = new DragLeaveHandler() {

		@Override
		public void onDragLeave(DragLeaveEvent event) {
		}
	};

	public void displayProperties(BoxItem item, PropertiesPanel<?> properties) {
		if (focusItem != null) {
			focusItem.select(false);
		}
		this.focusItem = item;
		this.focusItem.select(true);

		if (properties == null) {
			panelProperties.clear();
			panelProperties.setVisible(false);
		}
		else {
			panelProperties.setWidget(properties);
			panelProperties.setVisible(true);
		}
	}

}
