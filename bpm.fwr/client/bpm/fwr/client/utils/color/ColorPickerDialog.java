package bpm.fwr.client.utils.color;

import bpm.fwr.client.Bpm_fwr;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ColorPickerDialog extends AbstractDialogBox {
	
	private static ColorPickerDialogUiBinder uiBinder = GWT.create(ColorPickerDialogUiBinder.class);

	interface ColorPickerDialogUiBinder extends UiBinder<Widget, ColorPickerDialog> {
	}
	
	interface MyStyle extends CssResource {
		String center();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel;
	
	private SaturationLightnessPicker slPicker;
	private HuePicker huePicker;
	private String color;
	
	public ColorPickerDialog() {
		super("Select Color", false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(Bpm_fwr.LBLW.Ok(), okHandler, Bpm_fwr.LBLW.Cancel(), cancelHandler);
		
		// the pickers
		slPicker = new SaturationLightnessPicker();
		
		huePicker = new HuePicker();
		huePicker.addHueChangedHandler(new IHueChangedHandler() {
			public void hueChanged(HueChangedEvent event) {
				slPicker.setHue(event.getHue());
			}
		});
		
		HorizontalPanel panel = new HorizontalPanel();
		panel.addStyleName(style.center());
		panel.add(slPicker);
		panel.add(huePicker);
		
		contentPanel.add(panel);
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
	
	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			color = slPicker.getColor();
			
			ColorPickerDialog.this.hide();
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			ColorPickerDialog.this.hide();
		}
	};
}
