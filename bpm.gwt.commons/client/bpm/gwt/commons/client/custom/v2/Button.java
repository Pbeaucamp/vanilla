package bpm.gwt.commons.client.custom.v2;

import bpm.gwt.commons.client.utils.GlobalCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implementation of Button with an image and a text
 */
public class Button extends FocusPanel implements HasEnabled {

	private static ButtonUiBinder uiBinder = GWT.create(ButtonUiBinder.class);

	interface ButtonUiBinder extends UiBinder<Widget, Button> {
	}

	@UiField
	HTMLPanel button;

	@UiField
	Image image;

	@UiField
	Label label;

	private boolean enabled = true;

	private int style = 0;

	public Button() {
		setWidget(uiBinder.createAndBindUi(this));

		addStyleName(GlobalCSS.BTN);
	}

	public Button(String text) {
		this();
		setText(text);
	}

	public Button(String text, ImageResource resource) {
		this(text);
		setResource(resource);
	}

	public Button(String text, ImageResource resource, int height) {
		this(text, resource);
		setHeight(height);
	}

	public void setStyle(int style) {
		this.style = style;

		// Material design (Default Button)
		if (style == 0) {
			addStyleName(GlobalCSS.BTN);
			removeStyleName(GlobalCSS.BTN_FLAT);
		}
		// Material design (Flat Button)
		else if (style == 1) {
			addStyleName(GlobalCSS.BTN_FLAT);
			removeStyleName(GlobalCSS.BTN);
		}
		else {
			addStyleName(GlobalCSS.BTN);
			removeStyleName(GlobalCSS.BTN_FLAT);
		}
	}

	public void setText(String text) {
		label.setText(text);
	}

	public void setTitle(String title) {
		super.setTitle(title);
	}

	public void setHeight(int height) {
		button.setHeight(height + "px");
		button.getElement().getStyle().setLineHeight(height, Unit.PX);
	}
	
	public void setButtonWidth(int width) {
		button.setWidth(width + "px");
	}

	public void setImageSize(int size) {
		image.setWidth(size + "px");
		image.setHeight(size + "px");
	}

	@Override
	public void setHeight(String height) {
		try {
			int h = Integer.parseInt(height);
			setHeight(h);
			return;
		} catch (Exception e) {
		}

		// Don't use this method
		super.setHeight(height);
	}

	public void setResource(ImageResource resource) {
		image.setVisible(true);
		image.setResource(resource);
	}

	/**
	 * Gets whether this widget is enabled.
	 *
	 * @return <code>true</code> if the widget is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			removeStyleName(GlobalCSS.BTN_DISABLED);
			removeStyleName(GlobalCSS.BTN_FLAT_DISABLED);
		}
		else {
			// Material design (Default Button)
			if (style == 0) {
				addStyleName(GlobalCSS.BTN_DISABLED);
			}
			// Material design (Flat Button)
			else if (style == 1) {
				addStyleName(GlobalCSS.BTN_FLAT_DISABLED);
			}
			else {
				addStyleName(GlobalCSS.BTN_DISABLED);
			}
		}
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (enabled) {
			super.onBrowserEvent(event);
		}
		else {
			// event.preventDefault();
			event.stopPropagation();
		}
	}
}
