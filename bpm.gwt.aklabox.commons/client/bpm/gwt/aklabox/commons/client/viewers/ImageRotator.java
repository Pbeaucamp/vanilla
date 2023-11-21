package bpm.gwt.aklabox.commons.client.viewers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ImageRotator extends Composite {

	private static ImageRotatorUiBinder uiBinder = GWT.create(ImageRotatorUiBinder.class);

	interface ImageRotatorUiBinder extends UiBinder<Widget, ImageRotator> {
	}

	@UiField
	SimplePanel sliderPanel;

	private Image imgPreview;
	private int currentRotation = 0;

	public ImageRotator(Image imgPreview, boolean isImage) {
		initWidget(uiBinder.createAndBindUi(this));
		this.imgPreview = imgPreview;
		sliderPanel.setWidget(new SliderZoom(imgPreview, isImage));
//		sliderLuminosityPanel.setWidget(new SliderLuminosity(imgPreview, isImage));
	}

	@UiHandler("rotateLeft")
	void onRotateLeft(ClickEvent e) {
		setRotation("left");
	}

	@UiHandler("rotateRight")
	void onRotateRight(ClickEvent e) {
		setRotation("right");
	}

	private void setRotation(String direction) {
		if (direction.equals("left")) {
			currentRotation -= 90;
		}
		else if (direction.equals("right")) {
			currentRotation += 90;
		}
		String angle = currentRotation + "deg";

		imgPreview.getElement().setAttribute("style", "-webkit-transform: rotate(" + angle + "); transform: rotate(" + angle + ");");
		imgPreview.setWidth(imgPreview.getWidth() + "px");
	}
}
