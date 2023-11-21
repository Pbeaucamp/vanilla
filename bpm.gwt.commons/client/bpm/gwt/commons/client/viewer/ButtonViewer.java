package bpm.gwt.commons.client.viewer;

import bpm.gwt.commons.client.listeners.IClose;
import bpm.gwt.commons.client.listeners.ICloseHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ButtonViewer extends FocusPanel {

	private static ButtonViewerUiBinder uiBinder = GWT.create(ButtonViewerUiBinder.class);

	interface ButtonViewerUiBinder extends UiBinder<Widget, ButtonViewer> {
	}
	
	interface MyStyle extends CssResource {
		String selected();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel button;
	
	@UiField
	Label label;

	private ICloseHandler closeHandler;
	private IClose close;
	private Widget viewer;
	
	public ButtonViewer(ICloseHandler closeHandler, String name, IClose close, Widget viewer) {
		setWidget(uiBinder.createAndBindUi(this));
		this.closeHandler = closeHandler;
		this.close = close;
		this.viewer = viewer;
		
		label.setText(name);
	}

	public void select(boolean select) {
		if(select) {
			button.addStyleName(style.selected());
		}
		else {
			button.removeStyleName(style.selected());
		}
	}

	@UiHandler("close")
	public void onCloseClick(ClickEvent click) {
		closeHandler.closeViewer(this, close, viewer);
	}
}
