package bpm.gwt.aklabox.commons.client.viewers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.SliderBar;

public class SliderZoom extends Composite {

	private static SliderZoomUiBinder uiBinder = GWT.create(SliderZoomUiBinder.class);

	interface SliderZoomUiBinder extends UiBinder<Widget, SliderZoom> {
	}

	@UiField
	HTMLPanel sliderPanel;

	private SliderBar slider;
	private Image imgPreview;

	public SliderZoom(Image imgPreview, boolean isImage) {
		initWidget(uiBinder.createAndBindUi(this));
		slider = new SliderBar(0.0, 100.0);
		slider.setStepSize(10.0);
		slider.sinkEvents(Event.MOUSEEVENTS);
		sliderPanel.add(slider);
		this.imgPreview = imgPreview;

		slider.setCurrentValue(40);

		setImgCenter();
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void onChange(Widget sender) {
				setImgCenter();
			}
		});
	}

	private void setImgCenter() {
		imgPreview.setWidth((100 + (slider.getCurrentValue() - 50)) + "%");
	}

	@UiHandler("zoomOut")
	void onZoomOut(ClickEvent e) {
		if (slider.getCurrentValue() > 0.0) {
			slider.setCurrentValue(slider.getCurrentValue() - 10.0);
			setImgCenter();
		}
	}

	@UiHandler("zoomIn")
	void onZoomIn(ClickEvent e) {
		if (slider.getCurrentValue() < 100.0) {
			slider.setCurrentValue(slider.getCurrentValue() + 10.0);
			setImgCenter();
		}
	}

}
