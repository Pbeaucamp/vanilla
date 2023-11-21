package bpm.gwt.commons.client.custom;

import bpm.gwt.commons.client.utils.color.ColorPicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ColorPanel extends Composite implements HasChangeHandlers {

	private static ColorPanelUiBinder uiBinder = GWT.create(ColorPanelUiBinder.class);

	interface ColorPanelUiBinder extends UiBinder<Widget, ColorPanel> {
	}
	
	interface MyStyle extends CssResource {
		String alignRight();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Label lbl;

	@UiField
	FocusPanel panelColor;
	
	private String color;
	
	public ColorPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setText(String text) {
		this.lbl.setText(text);
	}
	
	public void setAlignRight(boolean alignRight) {
		if (alignRight) {
			panelColor.addStyleName(style.alignRight());
		}
		else {
			panelColor.removeStyleName(style.alignRight());
		}
	}
	
	public void setColor(String hexaColor) {
		setColor(hexaColor, true);
	}
	
	public void setColor(String hexaColor, boolean fireEvent) {
		this.color = hexaColor;
		if (hexaColor != null && !hexaColor.isEmpty()) {
			panelColor.getElement().getStyle().setBackgroundColor("#" + hexaColor);
		}
		
		if (fireEvent) {
			ChangeEvent.fireNativeEvent(Document.get().createChangeEvent(), this);
		}
	}
	
	public String getColor() {
		return color;
	}
	
	@UiHandler("panelColor")
	public void onColorClick(ClickEvent event) {
		int clientWidth = Window.getClientWidth();
		int x = event.getClientX();
		if (x + ColorPicker.COLOR_PICKER_WIDTH >= clientWidth) {
			x = event.getClientX() - ColorPicker.COLOR_PICKER_WIDTH;
		}
		int y = event.getClientY();
		
		final ColorPicker dial = new ColorPicker();
		dial.setColor(color);
		dial.setPopupPosition(x, y);
		dial.show();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				setColor(dial.getColor());
			}
		});
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}
}
