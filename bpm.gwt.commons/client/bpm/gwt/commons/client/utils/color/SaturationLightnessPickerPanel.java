package bpm.gwt.commons.client.utils.color;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class SaturationLightnessPickerPanel extends Composite {

	private static SaturationLightnessPickerPanelUiBinder uiBinder = GWT.create(SaturationLightnessPickerPanelUiBinder.class);

	interface SaturationLightnessPickerPanelUiBinder extends UiBinder<Widget, SaturationLightnessPickerPanel> {
	}
	
	@UiField(provided=true)
	Canvas canvas;

	private int hue = 180;
	private int handleX = 90;
	private int handleY = 90;
	
	private boolean mouseDown = false;
	
	public SaturationLightnessPickerPanel() {
		canvas = Canvas.createIfSupported();
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("canvas")
	public void onMouseDown(MouseDownEvent event) {
		handleX = event.getRelativeX(canvas.getElement());
		handleY = event.getRelativeY(canvas.getElement());
		drawGradient(false);
		String color = getColorAtPixel(handleX, handleY);
		drawGradient(true);
		fireColorChanged(color);
		
		mouseDown = true;
	}

	@UiHandler("canvas")
	public void onMouseMove(MouseMoveEvent event) {
		if (mouseDown) {
			handleX = event.getRelativeX(canvas.getElement());
			handleY = event.getRelativeY(canvas.getElement());
			drawGradient(false);
			String color = getColorAtPixel(handleX, handleY);
			drawGradient(true);
			fireColorChanged(color);
		}
	}

	@UiHandler("canvas")
	public void onMouseUp(MouseUpEvent event) {
		mouseDown = false;
	}

	@UiHandler("canvas")
	public void onMouseOut(MouseOutEvent event) {
		mouseDown = false;
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		drawGradient(true);
	}
	
	public HandlerRegistration addColorChangedHandler(IColorChangedHandler handler) {
		return addHandler(handler, ColorChangedEvent.getType());
	}
	
	private void fireColorChanged(String color) {
		fireEvent(new ColorChangedEvent(color));
	}
	
	private void drawGradient(boolean drawHandle) {
		Context2d ctx = canvas.getContext2d();
		
		// draw gradient
		for (int x = 0; x <= 179; x++) {
			CanvasGradient grad = ctx.createLinearGradient(x, 0, x, 179);
			int s = Math.round(x * 100 / 179);
			String hex = ColorUtils.hsl2hex(hue, s, 0);
			grad.addColorStop(0, "#" + hex); //$NON-NLS-1$
			hex = ColorUtils.hsl2hex(hue, s, 100);
			grad.addColorStop(1, "#" + hex); //$NON-NLS-1$
			ctx.setFillStyle(grad);
			ctx.fillRect(x, 0, 1, 180);
		}

		// draw handle
		if (drawHandle) {
			ctx.beginPath();
			ctx.arc(handleX, handleY, 3, 0, Math.PI * 2, false);
			ctx.closePath();
			ctx.setFillStyle("#ffffff"); //$NON-NLS-1$
			ctx.fill();
			
			ctx.beginPath();
			ctx.arc(handleX, handleY, 2, 0, Math.PI * 2, false);
			ctx.closePath();
			ctx.setFillStyle("#000000"); //$NON-NLS-1$
			ctx.fill();
		}
	}
	
	private String getColorAtPixel(int x, int y) {
		x = Math.max(Math.min(x, 179), 0);
		y = Math.max(Math.min(y, 179), 0);
		Context2d ctx = canvas.getContext2d();
		com.google.gwt.canvas.dom.client.ImageData imageData = ctx.getImageData(x, y, 1, 1);
		CanvasPixelArray data = imageData.getData();
		return ColorUtils.rgb2hex(data.get(0), data.get(1), data.get(2));
	}
	
	public void setHue(int hue) {
		this.hue = hue;
		drawGradient(false);
		String color = getColorAtPixel(handleX, handleY);
		drawGradient(true);
		fireColorChanged(color);
	}

	public String getColor() {
		drawGradient(false);
		String color = getColorAtPixel(handleX, handleY);
		drawGradient(true);
		return color;
	}

	public void setColor(String color) {
		int[] rgb = ColorUtils.getRGB(color);
		int[] hsl = ColorUtils.rgb2hsl(rgb);
		hue = hsl[0];
		handleX = (int) Math.min(Math.max(Math.round(hsl[1] * 180d / 100d), 0), 179);
		handleY = (int) Math.min(Math.max(Math.round(hsl[2] * 180d / 100d), 0), 179);
		drawGradient(true);
		fireColorChanged(color);
	}
}
