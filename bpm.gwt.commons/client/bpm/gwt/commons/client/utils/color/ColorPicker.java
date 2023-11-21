package bpm.gwt.commons.client.utils.color;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ColorPicker extends PopupPanel {
	
	public static final int COLOR_PICKER_WIDTH = 250;
	
	private static FormatUiBinder uiBinder = GWT.create(FormatUiBinder.class);

	interface FormatUiBinder extends UiBinder<Widget, ColorPicker> {
	}
	
	@UiField
	SaturationLightnessPickerPanel slPicker;

	@UiField
	HuePickerPanel huePicker;

	private boolean confirm = false;
	private String color;
	
	public ColorPicker() {
		setWidget(uiBinder.createAndBindUi(this));
		
		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
		
		setWidth(COLOR_PICKER_WIDTH + "px");
	}

	@UiHandler("huePicker")
	public void onHueChanged(HueChangedEvent event) {
		slPicker.setHue(event.getHue());
	}

	public void setColor(String color) {
		int[] rgb = ColorUtils.getRGB(color);
		int[] hsl = ColorUtils.rgb2hsl(rgb);
		huePicker.setHue(hsl[0]);
		slPicker.setColor(color);

		this.color = color;
	}

	public String getColor() {
		return color;
	}
	
	public boolean isConfirm() {
		return confirm;
	}
	
	@UiHandler("btnOk")
	public void onOkClick(ClickEvent event) {
		this.confirm = true;
		
		this.color = slPicker.getColor();
		hide();
	}
}
