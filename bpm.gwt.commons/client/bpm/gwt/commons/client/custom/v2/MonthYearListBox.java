package bpm.gwt.commons.client.custom.v2;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.utils.MonthYear;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MonthYearListBox extends CompositeData<MonthYear> {

	private static MonthYearListBoxUiBinder uiBinder = GWT.create(MonthYearListBoxUiBinder.class);

	interface MonthYearListBoxUiBinder extends UiBinder<Widget, MonthYearListBox> {
	}
	
	@UiField
	LabelListBox<MonthYear> lst;

	public MonthYearListBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void loadListMonth(MonthYear monthYear, boolean addEmptyItem) {
		loadListMonth(monthYear, addEmptyItem, true);
	}
	
	public void loadListMonth(MonthYear monthYear, boolean addEmptyItem, boolean selectDefaultValue) {
		int month = monthYear.getMonth();
		int year = monthYear.getYear();

		List<MonthYear> items = new ArrayList<MonthYear>();
		for (int i = -12; i < 12; i++) {
			int monthCalcul = month + i;

			int newMonth = monthCalcul < 0 ? (12 + monthCalcul) + 1 : (monthCalcul > 11 ? (monthCalcul - 12) + 1 : monthCalcul + 1);
			int newYear = monthCalcul < 0 ? year - 1 : (monthCalcul > 11 ? year + 1 : year);

			items.add(new MonthYear(newMonth, newYear));
		}
		lst.setList(items, addEmptyItem);
		if (selectDefaultValue) {
			lst.setValue(monthYear);
		}
	}
	
	public void setLabel(String label) {
		this.lst.setLabel(label);
	}
	
	public void setEnabled(boolean enabled) {
		this.lst.setEnabled(enabled);
	}

	public void setLabelWidth(int width) {
		this.lst.setLabelWidth(width);
	}
	
	public void setListWidth(int width) {
		this.lst.setListWidth(width);
	}

	@Override
	public MonthYear getValue() {
		return lst.getValue();
	}

	@Override
	public void setValue(MonthYear value) {
		this.lst.setValue(value);
	}

	@Override
	public Label getRequired() {
		return lst.getRequired();
	}

	public List<MonthYear> getList() {
		return lst.getList();
	}

	public HandlerRegistration addChangeHandler(ChangeHandler changeHandler) {
		return lst.addChangeHandler(changeHandler);
	}
}
