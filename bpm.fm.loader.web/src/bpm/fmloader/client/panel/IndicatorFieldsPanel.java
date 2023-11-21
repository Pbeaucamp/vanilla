package bpm.fmloader.client.panel;

import bpm.fmloader.client.constante.Constantes;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class IndicatorFieldsPanel extends VerticalPanel{

	public IndicatorFieldsPanel() {
		super();
		
		Label lblObj = new Label(Constantes.LBL.valObj());
		Label lblSMax = new Label(Constantes.LBL.valSeuilMax());
		Label lblSMin = new Label(Constantes.LBL.valSeuilMin());
		Label lblMax = new Label(Constantes.LBL.valMax());
		Label lblMin = new Label(Constantes.LBL.valMin());
		
		this.add(lblObj);
		this.add(lblSMax);
		this.add(lblSMin);
		this.add(lblMax);
		this.add(lblMin);
		
		this.addStyleName("indicatorFields");
	}

	
	
}
