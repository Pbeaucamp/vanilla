package bpm.gwt.commons.client.viewer.widget;

import java.util.List;

import com.google.gwt.user.client.ui.ListBox;

import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;

public class PortalComboParameter {
	
	private List<VanillaParameter> allParams;
	private VanillaParameter param;
	private ListBox box;
	
	public PortalComboParameter(List<VanillaParameter> allParams, VanillaParameter param, ListBox box) {
		this.allParams = allParams;
		this.param = param;
		this.box = box;
	}

	public List<VanillaParameter> getAllParams() {
		return allParams;
	}

	public void setAllParams(List<VanillaParameter> allParams) {
		this.allParams = allParams;
	}

	public VanillaParameter getParam() {
		return param;
	}

	public void setParam(VanillaParameter param) {
		
		this.param = param;
	}

	public ListBox getBox() {
		return box;
	}

	public void setBox(ListBox box) {
		this.box = box;
	}
}
