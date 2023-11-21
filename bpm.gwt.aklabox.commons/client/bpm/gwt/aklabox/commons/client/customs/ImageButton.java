package bpm.gwt.aklabox.commons.client.customs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ImageButton extends Composite implements HasClickHandlers {

	private static ImageButtonUiBinder uiBinder = GWT.create(ImageButtonUiBinder.class);

	interface ImageButtonUiBinder extends UiBinder<Widget, ImageButton> {
	}
	
	interface MyStyle extends CssResource {
		String btnDisabled();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	FocusPanel focus;
	
	@UiField
	Image img;

	public ImageButton() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setResource(ImageResource resource) {
		img.setResource(resource);
	}

	public void setEnabled(boolean enabled) {
		if (enabled) {
			focus.removeStyleName(style.btnDisabled());
		}
		else {
			focus.addStyleName(style.btnDisabled());
		}
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return img.addClickHandler(handler);
	}
}
