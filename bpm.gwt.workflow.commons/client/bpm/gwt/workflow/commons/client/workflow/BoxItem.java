package bpm.gwt.workflow.commons.client.workflow;

import bpm.gwt.workflow.commons.client.IWorkflowAppManager;
import bpm.gwt.workflow.commons.client.NameChanger;
import bpm.gwt.workflow.commons.client.popup.BoxItemContextMenuHandler;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class BoxItem extends FocusPanel implements FocusHandler, NameChanger, HasContextMenuHandlers {

	private static BoxItemUiBinder uiBinder = GWT.create(BoxItemUiBinder.class);

	interface BoxItemUiBinder extends UiBinder<Widget, BoxItem> {
	}

	interface MyStyle extends CssResource {
		String imgBox();

		String focus();

		String notValid();

		String loop();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel box;

	@UiField
	Image imgBox;

	@UiField
	Label lblName;

	private IWorkflowAppManager appManager;
	private WorkspacePanel creationPanel;
	private HandlerManager manager = new HandlerManager(this);

	private Activity activity;
	private PropertiesPanel<Activity> propertiesPanel;

	private boolean isValid = true;

	public BoxItem(IWorkflowAppManager appManager, WorkspacePanel creationPanel, TypeActivity type, String name) {
		setWidget(uiBinder.createAndBindUi(this));
		this.appManager = appManager;
		this.creationPanel = creationPanel;
		this.activity = buildActivity(type, name);

		buildContent(activity);
	}

	public BoxItem(IWorkflowAppManager appManager, WorkspacePanel creationPanel, Activity activity) {
		setWidget(uiBinder.createAndBindUi(this));
		this.appManager = appManager;
		this.creationPanel = creationPanel;
		this.activity = activity;

		buildContent(activity);
	}

	private void buildContent(Activity activity) {
		imgBox.setResource(appManager.getImage(activity.getType(), true));
		imgBox.addStyleName(style.imgBox());
		lblName.setText(activity.getName());

		updateStyle(activity.isLoop());

		propertiesPanel = buildProperties(activity.getType(), activity);

		sinkEvents(Event.ONCONTEXTMENU);

		addFocusHandler(this);
		addContextMenuHandler(new BoxItemContextMenuHandler(creationPanel, this));
	}

	private Activity buildActivity(TypeActivity type, String name) {
		return appManager.buildActivity(type, name);
	}

	private PropertiesPanel<Activity> buildProperties(TypeActivity type, Activity activity) {
		return appManager.buildActivityProperties(creationPanel, this, type, activity);
	}

	public Activity getActivityWithPosition() {
		Activity activity = this.activity;
		if (propertiesPanel != null) {
			activity = propertiesPanel.buildItem();
		}

		this.activity.setLeft(getLeft());
		this.activity.setTop(getTop());
		return activity;
	}

	public int getTop() {
		int top = 5;
		String topStr = getElement().getStyle().getTop();
		if (topStr.contains("px")) {
			top = Integer.parseInt(topStr.substring(0, topStr.length() - 2));
		}
		return top;
	}

	public int getLeft() {
		int left = 5;
		String leftStr = getElement().getStyle().getLeft();
		if (leftStr.contains("px")) {
			left = Integer.parseInt(leftStr.substring(0, leftStr.length() - 2));
		}
		return left;
	}

	public Activity getActivity() {
		return activity;
	}

	public boolean isValid() {
		return isValid;
	}

	public String getName() {
		return activity.getName() != null ? activity.getName() : "";
	}

	@Override
	public void changeName(String value, boolean isValid) {
		this.activity.setName(value);
		lblName.setText(value);

		this.isValid = isValid;

		updateBox();
	}

	private void updateBox() {
		if (isValid) {
			box.removeStyleName(style.notValid());
		}
		else {
			box.addStyleName(style.notValid());
		}
	}

	@Override
	public void onFocus(FocusEvent event) {
		creationPanel.displayProperties(this, propertiesPanel);
	}

	public void select(boolean select) {
		if (select) {
			addStyleName(style.focus());
		}
		else {
			removeStyleName(style.focus());
		}
	}

	@Override
	public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
		return manager.addHandler(ContextMenuEvent.getType(), handler);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		manager.fireEvent(event);
		super.fireEvent(event);
	}

	public void updateStyle(boolean loop) {
		if (loop) {
			box.addStyleName(style.loop());
		}
		else {
			box.removeStyleName(style.loop());
		}
	}
}
