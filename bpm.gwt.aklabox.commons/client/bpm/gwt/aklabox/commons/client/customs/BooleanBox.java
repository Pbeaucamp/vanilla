package bpm.gwt.aklabox.commons.client.customs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class BooleanBox extends Composite {

	private static BooleanBoxUiBinder uiBinder = GWT.create(BooleanBoxUiBinder.class);

	interface BooleanBoxUiBinder extends UiBinder<Widget, BooleanBox> {
	}
	
	@UiField
	RadioButton btnTrue, btnFalse;

	public BooleanBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public BooleanBox(boolean value) {
		initWidget(uiBinder.createAndBindUi(this));
		
		setValue(value);
	}

	public BooleanBox(String labelTrue, String labelFalse) {
		initWidget(uiBinder.createAndBindUi(this));

		setLabel(labelTrue, labelFalse);
	}

	public BooleanBox(String labelTrue, String labelFalse, boolean value) {
		initWidget(uiBinder.createAndBindUi(this));
		
		setLabel(labelTrue, labelFalse);
		setValue(value);
	}
	
	private void setLabel(String labelTrue, String labelFalse) {
		btnTrue.setText(labelTrue);
		btnFalse.setText(labelFalse);
	}

	public void setValue(boolean value) {
		btnTrue.setValue(value);
		btnFalse.setValue(!value);
	}
	
	public boolean getValue() {
		return btnTrue.getValue();
	}

	public void setEnabled(boolean canEdit) {
		btnTrue.setEnabled(canEdit);
		btnFalse.setEnabled(canEdit);
	}

}
