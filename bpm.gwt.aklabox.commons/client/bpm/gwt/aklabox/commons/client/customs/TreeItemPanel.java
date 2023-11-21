package bpm.gwt.aklabox.commons.client.customs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TreeItemPanel extends Composite implements HasDoubleClickHandlers, HasContextMenuHandlers, HasClickHandlers {

	public enum ItemSize {
		SMALL,
		BIG
	}
	
	private static TreeItemPanelUiBinder uiBinder = GWT.create(TreeItemPanelUiBinder.class);

	interface TreeItemPanelUiBinder extends UiBinder<Widget, TreeItemPanel> {
	}
	
	interface MyStyle extends CssResource {
		String selected();
		String imgSmall();
		String imgBig();
		String lblSmall();
		String lblBig();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	FocusPanel focus;
	
	@UiField
	Image img;
	
	@UiField
	Label lbl;
	
	private HandlerManager manager = new HandlerManager(this);

	public TreeItemPanel(String label) {
		initWidget(uiBinder.createAndBindUi(this));
		
		img.removeFromParent();
		buildContent(label, ItemSize.BIG);
	}

	public TreeItemPanel(ImageResource resource, String label, ItemSize size) {
		initWidget(uiBinder.createAndBindUi(this));
		
		if (resource != null) {
			img.setResource(resource);
		}
		else {
			img.removeFromParent();
		}
		buildContent(label, size);
	}

	public TreeItemPanel(String imgUrl, String label, ItemSize size) {
		initWidget(uiBinder.createAndBindUi(this));
		
		if (imgUrl != null) {
			img.setUrl(imgUrl);
		}
		else {
			img.removeFromParent();
		}
		buildContent(label, size);
	}

	private void buildContent(String label, ItemSize size) {
		lbl.setText(label);
		setTitle(label);
		
		focus.sinkEvents(Event.ONCONTEXTMENU);
		
		updateUi(size);
	}

	private void updateUi(ItemSize size) {
		if (size == ItemSize.BIG) {
			img.addStyleName(style.imgBig());
			lbl.addStyleName(style.lblBig());
		}
		else if (size == ItemSize.SMALL) {
			img.addStyleName(style.imgSmall());
			lbl.addStyleName(style.lblSmall());
		}
	}

	@Override
	public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
		return focus.addDoubleClickHandler(handler);
	}

	@Override
	public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
		return focus.addHandler(handler, ContextMenuEvent.getType());
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return focus.addClickHandler(handler);
	}
	
	@Override
	public void fireEvent(GwtEvent<?> event) {
		manager.fireEvent(event);
		super.fireEvent(event);
	}

	public void setSelected(boolean selected) {
		if (selected) {
			addStyleName(style.selected());
		}
		else {
			removeStyleName(style.selected());
		}
	}
}
