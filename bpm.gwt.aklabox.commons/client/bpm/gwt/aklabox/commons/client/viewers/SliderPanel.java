package bpm.gwt.aklabox.commons.client.viewers;

import bpm.document.management.core.utils.TreatmentImageObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
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

public class SliderPanel extends Composite {

	private static SliderPanelUiBinder uiBinder = GWT.create(SliderPanelUiBinder.class);

	interface SliderPanelUiBinder extends UiBinder<Widget, SliderPanel> {
	}
	

	@UiField
	HTMLPanel sliderPanel;
	@UiField Image imgPanel;

	private SliderBar slider;
	private Image imgPreview;
	private TreatmentImageObject.Type type;
	private TreatmentImageObject imageTreatment;

	public SliderPanel(Image imgPreview, ImageResource img, int pctInitValue, TreatmentImageObject.Type type, TreatmentImageObject imageTreatment) {
		initWidget(uiBinder.createAndBindUi(this));
		this.imageTreatment = imageTreatment;
		slider = new SliderBar(0.0, 100.0);
		slider.setStepSize(5.0);
		slider.sinkEvents(Event.MOUSEEVENTS);
		sliderPanel.add(slider);
		this.imgPreview = imgPreview;
		this.type = type;

		slider.setCurrentValue(pctInitValue);

		//setImgCenter();
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void onChange(Widget sender) {
				modifyImage();
			}
		});
		
		imgPanel.setResource(img);
	}

	private void modifyImage() {
		switch (type) {
		case CONTRAST:
			imageTreatment.addTreatment(type, slider.getCurrentValue()); 
			imgPreview.getElement().getStyle().setProperty("filter", imageTreatment.renderCssFilterProperty());
			imgPreview.getElement().getStyle().setProperty("WebkitFilter", imageTreatment.renderCssFilterProperty());
			break;
		case BRIGHTNESS:
			imageTreatment.addTreatment(type, slider.getCurrentValue()); 
			imgPreview.getElement().getStyle().setProperty("filter", imageTreatment.renderCssFilterProperty());
			imgPreview.getElement().getStyle().setProperty("WebkitFilter", imageTreatment.renderCssFilterProperty());
			break;
		default:
			break;
		}
		//imgPreview.setWidth((100 + (slider.getCurrentValue() - 50)) + "%");
	}

	@UiHandler("imgMinus")
	void onMinus(ClickEvent e) {
		if (slider.getCurrentValue() > 0.0) {
			slider.setCurrentValue(slider.getCurrentValue() - 10.0);
			modifyImage();
		}
	}

	@UiHandler("imgPlus")
	void onPlus(ClickEvent e) {
		if (slider.getCurrentValue() < 100.0) {
			slider.setCurrentValue(slider.getCurrentValue() + 10.0);
			modifyImage();
		}
	}

}
