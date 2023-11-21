package bpm.vanilla.portal.client.wizard;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.viewer.param.ParametersPanel;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;

public class AddTaskParamPage extends Composite implements IGwtPage {

	private static AddTaskParamPageUiBinder uiBinder = GWT.create(AddTaskParamPageUiBinder.class);

	interface AddTaskParamPageUiBinder extends UiBinder<Widget, AddTaskParamPage> {
	}
	
	@UiField
	SimplePanel panelParameters;
	
	private int index;

	private LaunchReportInformations itemInfo;

	public AddTaskParamPage(IGwtWizard parent, int index) {
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
		boolean hasParams = itemInfo.getGroupParameters() != null && !itemInfo.getGroupParameters().isEmpty();
		//We check that there are not all empty
		if (hasParams) {
			for (VanillaGroupParameter groupParam : itemInfo.getGroupParameters()) {
				if (groupParam.getParameters() == null || groupParam.getParameters().isEmpty()) {
					continue;
				}
				
				for (VanillaParameter param : groupParam.getParameters()) {
					if (!param.isHidden()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public List<VanillaGroupParameter> getParameters() {
		return itemInfo.getGroupParameters();
	}

}
