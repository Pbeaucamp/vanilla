package bpm.fmloader.client.table.value;

import bpm.fmloader.client.constante.Constantes;
import bpm.fmloader.client.dto.AssoMetricAppDTO;
import bpm.fmloader.client.dto.DTO;
import bpm.fmloader.client.dto.IndicatorValuesDTO;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class ValueCellIndicateur extends HTMLPanel implements ValueCell {

	private IndicatorValuesDTO value;
	private AssoMetricAppDTO assoc;
	
	private TextBox txtObjectif, txtSeuilMax, txtSeuilMin, txtValMax, txtValMin;

	public ValueCellIndicateur(IndicatorValuesDTO value, AssoMetricAppDTO assoc) {
		super("");
		this.value = value;
		this.assoc = assoc;
		
		txtObjectif = new TextBox();
		txtSeuilMax = new TextBox();
		txtSeuilMin = new TextBox();
		txtValMax = new TextBox();
		txtValMin = new TextBox();
		
		FlexTable table = new FlexTable();
		table.setWidget(0, 0, new Label(Constantes.LBL.valObj()));
		table.setWidget(1, 0, new Label(Constantes.LBL.valSeuilMax()));
		table.setWidget(2, 0, new Label(Constantes.LBL.valSeuilMin()));
		table.setWidget(3, 0, new Label(Constantes.LBL.valMax()));
		table.setWidget(4, 0, new Label(Constantes.LBL.valMin()));
		
		table.setWidget(0, 1, txtObjectif);
		table.setWidget(1, 1, txtSeuilMax);
		table.setWidget(2, 1, txtSeuilMin);
		table.setWidget(3, 1, txtValMax);
		table.setWidget(4, 1, txtValMin);
		
		if(value.getObjValue() != null && !value.getObjValue().equals("")) {
			txtObjectif.setText(value.getObjValue());
		}
		if(value.getSeuilMaxValue() != null && !value.getSeuilMaxValue().equals("")) {
			txtSeuilMax.setText(value.getSeuilMaxValue());
		}
		if(value.getSeuilMinValue() != null && !value.getSeuilMinValue().equals("")) {
			txtSeuilMin.setText(value.getSeuilMinValue());
		}
		if(value.getValMaxValue() != null && !value.getValMaxValue().equals("")) {
			txtValMax.setText(value.getValMaxValue());
		}
		if(value.getValMinValue() != null && !value.getValMinValue().equals("")) {
			txtValMin.setText(value.getValMinValue());
		}
		this.add(table);
	}

	@Override
	public AssoMetricAppDTO getAssoc() {
		return assoc;
	}

	@Override
	public DTO getValue() {
		value.setObjValue(txtObjectif.getText());
		value.setSeuilMaxValue(txtSeuilMax.getText());
		value.setSeuilMinValue(txtSeuilMin.getText());
		value.setValMaxValue(txtValMax.getText());
		value.setValMinValue(txtValMin.getText());
		return value;
	}
	
}
