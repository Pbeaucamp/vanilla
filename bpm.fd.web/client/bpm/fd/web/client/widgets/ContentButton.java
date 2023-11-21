package bpm.fd.web.client.widgets;

import bpm.fd.web.client.popup.ContentWidgetMenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContentButton extends Composite implements HasContextMenuHandlers, ContextMenuHandler {

	private static ContentButtonUiBinder uiBinder = GWT.create(ContentButtonUiBinder.class);

	interface ContentButtonUiBinder extends UiBinder<Widget, ContentButton> {
	}
	
	interface MyStyle extends CssResource {
		String btnSelected();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	FocusPanel btn;
	
	private HandlerManager manager = new HandlerManager(this);
	
	private ContainerWidget parent;
	private ContentWidget widget;

	public ContentButton(ContainerWidget parent, ContentWidget widget) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.widget = widget;

		sinkEvents(Event.ONCONTEXTMENU);
		addContextMenuHandler(this);
	}

	@UiHandler("btn")
	public void onSelect(ClickEvent event) {
		parent.select(this);
	}

	public void setSelected(boolean selected) {
		if (selected) {
			addStyleName(style.btnSelected());
		}
		else {
			removeStyleName(style.btnSelected());
		}
	}
	
	public ContentWidget getContentWidget() {
		return widget;
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

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		ContentWidgetMenu itemMenu = new ContentWidgetMenu(parent, this);
		itemMenu.setAutoHideEnabled(true);
		itemMenu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		itemMenu.show();
		
		event.stopPropagation();
		event.preventDefault();
	}
}
