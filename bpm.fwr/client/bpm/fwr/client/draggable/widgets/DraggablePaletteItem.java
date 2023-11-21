package bpm.fwr.client.draggable.widgets;

import bpm.fwr.client.utils.WidgetType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class DraggablePaletteItem extends AbsolutePanel implements HasMouseDownHandlers {

	private static DraggablePaletteItemUiBinder uiBinder = GWT.create(DraggablePaletteItemUiBinder.class);

	interface DraggablePaletteItemUiBinder extends UiBinder<Widget, DraggablePaletteItem> {
	}
	
	interface MyStyle extends CssResource {
		String imgItem();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Image imgItem;

	private WidgetType type;
	private String title;

	public DraggablePaletteItem(WidgetType type) {
		add(uiBinder.createAndBindUi(this));
		this.type = type;
	}

	public void setResource(ImageResource image) {
		imgItem.setResource(image);
		imgItem.addStyleName(style.imgItem());
		
		if(title != null) {
			imgItem.setTitle(title);
		}
	}

	public WidgetType getType() {
		return type;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}
}
