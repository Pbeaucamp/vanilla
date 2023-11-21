package bpm.fmloader.client.table.value;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;

import bpm.fmloader.client.dto.AssoMetricAppDTO;
import bpm.fmloader.client.dto.DTO;
import bpm.fmloader.client.dto.ValuesDTO;

public class ValueCellCompteur extends HTMLPanel implements ValueCell {

	private ValuesDTO value;
	private AssoMetricAppDTO assoc;
	private TextBox txtValue;

	public ValueCellCompteur(ValuesDTO value, AssoMetricAppDTO assoc) {
		super("");
		this.value = value;
		this.assoc = assoc;
		
		txtValue = new TextBox();
		this.add(txtValue);
		
		if(value.getValue() != null && !value.getValue().equals("")) {
			txtValue.setText(value.getValue());
		}
	}

	@Override
	public AssoMetricAppDTO getAssoc() {
		return assoc;
	}

	@Override
	public DTO getValue() {
		value.setValue(txtValue.getText());
		
		return value;
	}
	
}
