package bpm.architect.web.client.panels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import bpm.data.viz.core.preparation.DataPreparationResult;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DataVizValueInfoPanel extends Composite {

	private static DataVizValueInfoPanelUiBinder uiBinder = GWT.create(DataVizValueInfoPanelUiBinder.class);

	interface DataVizValueInfoPanelUiBinder extends UiBinder<Widget, DataVizValueInfoPanel> {}

	@UiField
	Label lblCount, lblDistinct, lblMin, lblMax, lblAvg, lblLenghtMin, lblLenghtMax, lblLenghtAvg;
	
	private DataPreparationResult dataPreparationResult;

	public DataVizValueInfoPanel(DataPreparationResult dataPreparationResult, DataColumn dataColumn) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.dataPreparationResult = dataPreparationResult;
		refreshValueInfo(dataColumn);
	}

	public void refreshValueInfo(DataColumn dataColumn) {
		List<Serializable> colValues = new ArrayList<>();
		for(Map<DataColumn, Serializable> val : dataPreparationResult.getValues()) {
			colValues.add(val.get(dataColumn));
		}
		HashSet<Serializable> distinctVals = new HashSet<>(colValues);
		
		lblCount.setText(String.valueOf(colValues.size()));
		lblDistinct.setText(String.valueOf(distinctVals.size()));
		
		Double min = null;
		Double max = null;
		int countWithoutNull = 0;
		Double avg = 0D;
		
		Integer minLgt = null;
		Integer maxLgt = null;
		int countLenghtWithoutNull = 0;
		Double avgLgt = 0D;
		
		for(Serializable val : colValues) {
			if(val == null) {
				continue;
			}
			try {
				Double v = Double.parseDouble(val.toString());
				if(min == null) {
					min = v;
				}
				else {
					if(min > v) {
						min = v;
					}
				}
				if(max == null) {
					max = v;
				}
				else {
					if(max < v) {
						max = v;
					}
				}
				countWithoutNull++;
				avg += v;
			} catch(Exception e) {}

			if(minLgt == null) {
				minLgt = val.toString().length();
			}
			else {
				if(minLgt > val.toString().length()) {
					minLgt = val.toString().length();
				}
			}
			if(maxLgt == null) {
				maxLgt = val.toString().length();
			}
			else {
				if(maxLgt < val.toString().length()) {
					maxLgt = val.toString().length();
				}
			}
			countLenghtWithoutNull++;
			avgLgt += val.toString().length();
		}
		if(min != null) {
			lblMin.setText(String.valueOf(min));
		}
		else {
			lblMin.setText("");
		}
		if(max != null) {
			lblMax.setText(String.valueOf(max));
		}
		else {
			lblMax.setText("");
		}
		if(minLgt != null) {
			lblLenghtMin.setText(String.valueOf(minLgt));
		}
		else {
			lblLenghtMin.setText("");
		}
		if(maxLgt != null) {
			lblLenghtMax.setText(String.valueOf(maxLgt));
		}
		else {
			lblLenghtMax.setText("");
		}
		if(countWithoutNull > 0) {
			lblAvg.setText(NumberFormat.getFormat("##.00").format(Math.round(avg/(double)countWithoutNull * 100.0) / 100.0));
		}
		else {
			lblAvg.setText("");
		}
		if(countLenghtWithoutNull > 0) {
			lblLenghtAvg.setText(NumberFormat.getFormat("##.00").format(Math.round(avgLgt/(double)countLenghtWithoutNull * 100.0) / 100.0));
		}
		else {
			lblLenghtAvg.setText("");
		}
	}

}
