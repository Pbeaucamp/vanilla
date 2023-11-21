package bpm.gwt.commons.client.custom.v2;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class LabelDateBox extends Composite{

	private DateTimeFormat df = DateTimeFormat.getFormat("dd/MM/yyyy");

	private static LabelDateBoxUiBinder uiBinder = GWT.create(LabelDateBoxUiBinder.class);

	interface LabelDateBoxUiBinder extends UiBinder<Widget, LabelDateBox> {
	}

	interface MyStyle extends CssResource {
		String full();

		String half();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelLabel;

	@UiField
	Label label, required;

	@UiField
	HTMLPanel panelText, panelDates;

	@UiField
	DateBox firstDatebox, lastDatebox;

	public LabelDateBox() {
		initWidget(uiBinder.createAndBindUi(this));

		firstDatebox.setFormat(new DateBox.DefaultFormat(df));
		firstDatebox.getDatePicker().setYearAndMonthDropdownVisible(true);
		firstDatebox.getDatePicker().setVisibleYearCount(200);

		lastDatebox.setFormat(new DateBox.DefaultFormat(df));
		lastDatebox.getDatePicker().setYearAndMonthDropdownVisible(true);
		lastDatebox.getDatePicker().setVisibleYearCount(200);
	}

	public void setLabel(String label) {
		this.label.setText(label);
	}

	public void setLabelWidth(int width) {
		this.panelLabel.setWidth(width + "px");
	}

	public void setTextWidth(int width) {
		this.panelDates.setWidth(width + "px");
	}
	
	public void setLastDateVisible(boolean visible) {
		this.lastDatebox.setVisible(visible);
		
		if (visible) {
			firstDatebox.removeStyleName(style.full());
			firstDatebox.addStyleName(style.half());
		}
		else {
			firstDatebox.addStyleName(style.full());
			firstDatebox.removeStyleName(style.half());
		}
	}

	public void setValue(Date firstDate, Date lastDate) {
		this.firstDatebox.setValue(firstDate);
		this.lastDatebox.setValue(lastDate);
	}

	public void setValue(Date date) {
		if (date != null) {
			this.firstDatebox.setValue(date);
		}
	}
	
	public Date getFirstDate() {
		return firstDatebox != null ? firstDatebox.getValue() : null;
	}
	
	public Date getLastDate() {
		return lastDatebox != null ? lastDatebox.getValue() : null;
	}

	public void setFocus(boolean focused) {
		this.firstDatebox.setFocus(focused);
	}

	public void setEnabled(boolean enabled) {
		this.firstDatebox.setEnabled(enabled);
		this.lastDatebox.setEnabled(enabled);
	}

	public HandlerRegistration addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler<Date> handler) {
		return firstDatebox.addValueChangeHandler(handler);
	}
}
