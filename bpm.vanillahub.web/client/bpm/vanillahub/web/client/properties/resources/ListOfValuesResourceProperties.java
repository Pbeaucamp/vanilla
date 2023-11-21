package bpm.vanillahub.web.client.properties.resources;

import java.util.List;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChanger;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanillahub.web.client.I18N.Labels;

import com.google.gwt.user.client.ui.SimplePanel;

public class ListOfValuesResourceProperties extends PropertiesPanel<Resource> implements NameChanger {

	private ListOfValues lov;
	
	private SimplePanel panelContent;
	private ListOfValuesGridPanel valuesPanel;

	private boolean isNameValid = true;

	public ListOfValuesResourceProperties(NameChecker dialog, IResourceManager resourceManager, ListOfValues myLov) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.SMALL_PX, myLov != null ? myLov.getId() : 0, myLov != null ? myLov.getName() : Labels.lblCnst.ListOfValues(), false, true);
		this.lov = myLov != null ? myLov : new ListOfValues(Labels.lblCnst.ListOfValues());

		setNameChecker(dialog);
		setNameChanger(this);
		
		valuesPanel = new ListOfValuesGridPanel(lov);
		
		panelContent = addSimplePanel(true);
		panelContent.add(valuesPanel);
		
		checkName(getTxtName(), lov.getName());
	}

	@Override
	public boolean isValid() {
		boolean isValid = true;
		List<String> values = valuesPanel.getValues();
		if (values == null || values.isEmpty()) {
			isValid = false;
			valuesPanel.setTxtError(Labels.lblCnst.AtLeastOneValueNeeded());
		}
		else {
			valuesPanel.setTxtError("");
		}
		return isNameValid && isValid;
	}

	@Override
	public void changeName(String value, boolean isValid) {
		this.isNameValid = isValid;
		lov.setName(value);
	}

	@Override
	public Resource buildItem() {
		List<String> values = valuesPanel.getValues();
		
		lov.setValues(values);
		
		return lov;
	}
}
