package bpm.faweb.client.openlayer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LegendItem extends Composite{

	private static LegendItemUiBinder uiBinder = GWT
			.create(LegendItemUiBinder.class);

	interface LegendItemUiBinder extends UiBinder<Widget, LegendItem> {
	}

	@UiField Label lblName, lblMinMax;
	@UiField HTMLPanel legendColor;
	
	private String name;
	private String color;
	double min;
	double max;
	
	public LegendItem(String name, String color, double min, double max) {
		initWidget(uiBinder.createAndBindUi(this));
		this.name = name;
		this.color = color;
		this.min = min;
		this.max = max;
		lblName.setText(name);
		lblMinMax.setText("Range: " + String.valueOf(min) + " - " + String.valueOf(max));
		legendColor.getElement().setAttribute("style", "background: " + "#"+color + ";");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	@UiHandler("btnRemove")
	void onRemove(ClickEvent e){
		this.removeFromParent();
	}

}
