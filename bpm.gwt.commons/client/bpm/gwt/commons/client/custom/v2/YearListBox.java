package bpm.gwt.commons.client.custom.v2;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class YearListBox extends CompositeData<Integer> {

	private static YearListBoxUiBinder uiBinder = GWT.create(YearListBoxUiBinder.class);

	interface YearListBoxUiBinder extends UiBinder<Widget, YearListBox> {
	}
	
	@UiField
	LabelListBox<Integer> lst;

	public YearListBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void loadListMonth(Integer year, boolean addEmptyItem) {
		loadListYear(year, addEmptyItem, true);
	}
	
	public void loadListYear(Integer year, boolean addEmptyItem, boolean selectDefaultValue) {
		List<Integer> items = new ArrayList<Integer>();
		for (int i = -12; i < 12; i++) {
			int newYear = year + i;
			items.add(newYear);
		}
		lst.setList(items, addEmptyItem);
		if (selectDefaultValue) {
			lst.setValue(year);
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
	public Integer getValue() {
		return lst.getValue();
	}

	@Override
	public void setValue(Integer value) {
		this.lst.setValue(value);
	}

	@Override
	public Label getRequired() {
		return lst.getRequired();
	}

	public List<Integer> getList() {
		return lst.getList();
	}

	public HandlerRegistration addChangeHandler(ChangeHandler changeHandler) {
		return lst.addChangeHandler(changeHandler);
	}
}
