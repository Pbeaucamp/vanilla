package bpm.vanillahub.web.client.properties.creation.attribute;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PanelWithVariables;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.activities.attributes.URLProperties;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;

public class URLPropertiesPanel extends Composite implements ChangeHandler, PanelWithVariables {

	private static URLPropertiesPanelUiBinder uiBinder = GWT.create(URLPropertiesPanelUiBinder.class);

	interface URLPropertiesPanelUiBinder extends UiBinder<Widget, URLPropertiesPanel> {
	}
	
	interface MyStyle extends CssResource {
		String txt();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelTexts;
	
	@UiField
	CustomCheckbox chkUnzip;

	private URLProperties urlProperties;

	private List<VariableTextHolderBox> variableTexts = new ArrayList<VariableTextHolderBox>();

	public URLPropertiesPanel(ResourceManager resourceManager, URLProperties urlProperties) {
		initWidget(uiBinder.createAndBindUi(this));
		this.urlProperties = urlProperties != null ? urlProperties : new URLProperties();

		VariableTextHolderBox txtUrl = new VariableTextHolderBox(this.urlProperties.getUrlVS(), LabelsCommon.lblCnst.URL(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtUrl.addChangeHandler(this);
		variableTexts.add(txtUrl);
		panelTexts.add(txtUrl);

		VariableTextHolderBox txtOutputName = new VariableTextHolderBox(this.urlProperties.getOutputNameVS(), LabelsCommon.lblCnst.OutputFileName(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtOutputName.addChangeHandler(this);
		variableTexts.add(txtOutputName);
		panelTexts.add(txtOutputName);
		
		chkUnzip.setValue(this.urlProperties.isUnzip());
	}

	public URLProperties getUrlProperties() {
		boolean isUnzip = chkUnzip.getValue();
		urlProperties.setUnzip(isUnzip);
		
		return urlProperties;
	}

	@Override
	public void onChange(ChangeEvent event) {
		
	}
	
	public boolean isValid() {
		URLProperties urlProperties = getUrlProperties();
		return urlProperties.isValid();
	}

	@Override
	public void setVariables(List<Variable> variables, List<Parameter> parameters) {
		if (variableTexts != null) {
			for(VariableTextHolderBox variableText : variableTexts) {
				variableText.setVariables(variables);
				variableText.setParameters(parameters);
			}
		}
	}
}
