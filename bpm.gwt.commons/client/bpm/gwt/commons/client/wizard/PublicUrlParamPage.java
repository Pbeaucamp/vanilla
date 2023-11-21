package bpm.gwt.commons.client.wizard;

import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.viewer.param.ParametersPanel;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PublicUrlParamPage extends Composite implements IGwtPage {

	private static AddTaskParamPageUiBinder uiBinder = GWT.create(AddTaskParamPageUiBinder.class);

	interface AddTaskParamPageUiBinder extends UiBinder<Widget, PublicUrlParamPage> {
	}
	
	@UiField
	SimplePanel panelParameters;
	
	private int index;

	private LaunchReportInformations itemInfo;

	public PublicUrlParamPage(IGwtWizard parent, int index) {
		initWidget(uiBinder.createAndBindUi(this));
		this.index = index;
	}
	
	public void setItem(LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;
		
		panelParameters.setWidget(new ParametersPanel(itemInfo, false));
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return true;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	public boolean hasParameters() {
		return itemInfo.getGroupParameters() != null && !itemInfo.getGroupParameters().isEmpty();
	}

	public HashMap<String, String> getParameters() {
		HashMap<String, String> params = new HashMap<String, String>();
		
		List<VanillaGroupParameter> groupParams = itemInfo.getGroupParameters();
		if (groupParams != null) {
			for (VanillaGroupParameter group : groupParams) {
				if (group.getParameters() != null) {
					for (VanillaParameter param : group.getParameters()) {
						String key = param.getName();
						String value = param.getSelectedValues() != null && !param.getSelectedValues().isEmpty() ? param.getSelectedValues().get(0) : "";
						
						params.put(key, value);
					}
				}
			}
		}
		return params;
	}

}
