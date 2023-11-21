package bpm.gwt.commons.client.utils.color;

import com.google.gwt.canvas.client.Canvas;
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

public class HuePickerPanel extends Composite {

	private static HuePickerPanelUiBinder uiBinder = GWT.create(HuePickerPanelUiBinder.class);

	interface HuePickerPanelUiBinder extends UiBinder<Widget, HuePickerPanel> {
	}
	
	@UiField(provided=true)
	Canvas canvas;
	
	private int handleY = 90;
	
	private boolean mouseDown;

	public HuePickerPanel() {
		canvas = Canvas.createIfSupported();
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("canvas")
	public void onMouseDown(MouseDownEvent event) {
		handleY = event.getRelativeY(canvas.getElement());
		drawGradient();
		fireHueChanged(getHue());
		
		mouseDown = true;
	}

	@UiHandler("canvas")
	public void onMouseMove(MouseMoveEvent event) {
		if (mouseDown) {
			handleY = event.getRelativeY(canvas.getElement());
			drawGradient();
			fireHueChanged(getHue());
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
		drawGradient();
	}

	private void drawGradient() {
		Context2d ctx = canvas.getContext2d();

		// draw gradient
		ctx.setFillStyle("#ffffff"); //$NON-NLS-1$
		ctx.fillRect(0, 0, 26, 180);
		for (int y = 0; y <= 179; y++) {
			String hex = ColorUtils.hsl2hex(y * 2, 100, 100);
			ctx.setFillStyle("#" + hex); //$NON-NLS-1$
			ctx.fillRect(3, y, 20, 1);
		}

		// draw handle
		if (handleY >= 0) {
			ctx.setFillStyle("#000000"); //$NON-NLS-1$

			ctx.beginPath();
			ctx.moveTo(3, handleY);
			ctx.lineTo(0, handleY - 3);
			ctx.lineTo(0, handleY + 3);
			ctx.closePath();
			ctx.fill();
			
			ctx.moveTo(23, handleY);
			ctx.lineTo(26, handleY - 3);
			ctx.lineTo(26, handleY + 3);
			ctx.closePath();
			ctx.fill();
		}
	}
	
	public HandlerRegistration addHueChangedHandler(IHueChangedHandler handler) {
		return addHandler(handler, HueChangedEvent.getType());
	}
	
	private void fireHueChanged(int hue) {
		fireEvent(new HueChangedEvent(hue));
	}
	
	public int getHue() {
		return handleY * 2;
	}

	public void setHue(int hue) {
		handleY = (int) Math.min(Math.max(Math.round(hue / 2d), 0d), 179d);
		drawGradient();
		fireHueChanged(hue);
	}
}
