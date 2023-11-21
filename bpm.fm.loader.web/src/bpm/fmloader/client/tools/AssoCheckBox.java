package bpm.fmloader.client.tools;

import com.google.gwt.user.client.ui.CheckBox;

import bpm.fmloader.client.dto.AssoMetricAppDTO;

public class AssoCheckBox extends CheckBox {

	private AssoMetricAppDTO asso;

	public AssoCheckBox(AssoMetricAppDTO asso) {
		super();
		this.asso = asso;
	}

	public AssoMetricAppDTO getAsso() {
		return asso;
	}

	public void setAsso(AssoMetricAppDTO asso) {
		this.asso = asso;
	}
	
	
	
}
