package bpm.gwt.commons.client.custom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ImageButtonText extends Composite implements HasText, HasClickHandlers {

	private static ImageButtonTextUiBinder uiBinder = GWT.create(ImageButtonTextUiBinder.class);

	interface ImageButtonTextUiBinder extends UiBinder<Widget, ImageButtonText> {
	}

	interface MyStyle extends CssResource {
		String button();

		String buttondisabled();

		String image();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel focus;

	@UiField
	Image img;

	@UiField
	Label text;

	private boolean enabled = true;

	public ImageButtonText() {
		initWidget(uiBinder.createAndBindUi(this));
		img.addStyleName(style.image());
		this.addStyleName(style.button());
	}

	@Override
	public String getText() {
		return text.getText();
	}

	public void setResource(ImageResource resource) {
		img.setResource(resource);
	}

	@Override
	public void setText(String texte) {
		text.setText(texte);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			focus.removeStyleName(style.buttondisabled());
		}
		else {
			focus.addStyleName(style.buttondisabled());
		}
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (!enabled) {
			event.stopPropagation();
		}
		else {
			super.onBrowserEvent(event);
		}
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return focus.addDomHandler(handler, ClickEvent.getType());
	}
}