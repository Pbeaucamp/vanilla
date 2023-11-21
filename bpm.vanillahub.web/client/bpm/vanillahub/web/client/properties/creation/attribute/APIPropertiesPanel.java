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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PanelWithVariables;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.activities.attributes.APIProperties;
import bpm.vanillahub.core.beans.activities.attributes.APIProperties.TypeGrant;
import bpm.vanillahub.core.beans.activities.attributes.APIProperties.TypeSecurity;
import bpm.vanillahub.core.beans.activities.attributes.ClientCredentialsProperties;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;

public class APIPropertiesPanel extends Composite implements ChangeHandler, PanelWithVariables {

	private static APIPropertiesPanelUiBinder uiBinder = GWT.create(APIPropertiesPanelUiBinder.class);

	interface APIPropertiesPanelUiBinder extends UiBinder<Widget, APIPropertiesPanel> {
	}
	
	interface MyStyle extends CssResource {
		String txt();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelTexts;
	
	@UiField
	ListBox lstTypeSecurity;
	
	@UiField
	ListBox lstTypeGrant;
	
	@UiField
	SimplePanel panelSecurity;
	
	@UiField
	CustomCheckbox chkUnzip;

	private ResourceManager resourceManager;
	private APIProperties apiProperties;

	private List<VariableTextHolderBox> variableTexts = new ArrayList<VariableTextHolderBox>();
	private ClientCredentialsPropertiesPanel panelClientCredentials;

	public APIPropertiesPanel(ResourceManager resourceManager, APIProperties apiProperties) {
		initWidget(uiBinder.createAndBindUi(this));
		this.resourceManager = resourceManager;
		this.apiProperties = apiProperties != null ? apiProperties : new APIProperties();

		VariableTextHolderBox txtUrl = new VariableTextHolderBox(this.apiProperties.getUrlVS(), LabelsCommon.lblCnst.URL(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtUrl.addChangeHandler(this);
		variableTexts.add(txtUrl);
		panelTexts.add(txtUrl);

		VariableTextHolderBox txtOutputName = new VariableTextHolderBox(this.apiProperties.getOutputNameVS(), LabelsCommon.lblCnst.OutputFileName(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtOutputName.addChangeHandler(this);
		variableTexts.add(txtOutputName);
		panelTexts.add(txtOutputName);
		
		chkUnzip.setValue(this.apiProperties.isUnzip());
		
		int selectedIndex = 0;
		int i = 0;
		for (TypeSecurity type : TypeSecurity.values()) {
			lstTypeSecurity.addItem(getLabel(type), String.valueOf(type.getType()));
			if (this.apiProperties.getTypeSecurity() != null && this.apiProperties.getTypeSecurity() == type) {
				selectedIndex = i;
			}
			i++;
		}
		lstTypeSecurity.setSelectedIndex(selectedIndex);
		lstTypeSecurity.addChangeHandler(changeTypeSecurity);
		
		selectedIndex = 0;
		i = 0;
		for (TypeGrant type : TypeGrant.values()) {
			lstTypeGrant.addItem(getLabel(type), String.valueOf(type.getType()));
			if (this.apiProperties.getTypeGrant() != null && this.apiProperties.getTypeGrant() == type) {
				selectedIndex = i;
			}
			i++;
		}
		lstTypeGrant.setSelectedIndex(selectedIndex);
		lstTypeGrant.addChangeHandler(changeTypeGrant);
		
		updateUi(this.apiProperties.getTypeSecurity(), this.apiProperties.getTypeGrant());
	}
	
	private String getLabel(TypeSecurity type) {
		switch (type) {
		case NO_AUTH:
			return Labels.lblCnst.NoAuth();
		case OAUTH20:
			return Labels.lblCnst.OAuth20();
		default:
			break;
		}
		return LabelsConstants.lblCnst.Unknown();
	}
	
	private String getLabel(TypeGrant type) {
		switch (type) {
		case CLIENT_CREDENTIALS:
			return Labels.lblCnst.ClientCredentials();
		default:
			break;
		}
		return LabelsConstants.lblCnst.Unknown();
	}

	private void updateUi(TypeSecurity typeSecurity, TypeGrant typeGrant) {
		if (typeSecurity != null) {
			switch (typeSecurity) {
			case NO_AUTH:
				panelSecurity.clear();
				
				lstTypeGrant.setVisible(false);
				break;
			case OAUTH20:
				
				lstTypeGrant.setVisible(true);
				
				switch (typeGrant) {
				case CLIENT_CREDENTIALS:
					if (panelClientCredentials == null) {
						ClientCredentialsProperties clientCredentials = apiProperties.getSecurity() != null && apiProperties.getSecurity() instanceof ClientCredentialsProperties ? (ClientCredentialsProperties) apiProperties.getSecurity() : null;
						panelClientCredentials = new ClientCredentialsPropertiesPanel(resourceManager, clientCredentials);
					}
//					registerPanelWithVariables(panelClientCredentials);
					panelSecurity.setWidget(panelClientCredentials);
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
	}

	private ChangeHandler changeTypeSecurity = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			int type = Integer.parseInt(lst.getValue(lst.getSelectedIndex()));

			apiProperties.setTypeSecurity(TypeSecurity.valueOf(type));
			updateUi(apiProperties.getTypeSecurity(), apiProperties.getTypeGrant());
		}
	};

	private ChangeHandler changeTypeGrant = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			int type = Integer.parseInt(lst.getValue(lst.getSelectedIndex()));

			apiProperties.setTypeGrant(TypeGrant.valueOf(type));
			updateUi(apiProperties.getTypeSecurity(), apiProperties.getTypeGrant());
		}
	};

	public APIProperties getUrlProperties() {
		boolean isUnzip = chkUnzip.getValue();
		apiProperties.setUnzip(isUnzip);
		
		switch (apiProperties.getTypeSecurity()) {
		case NO_AUTH:
			break;
		case OAUTH20:
			switch (apiProperties.getTypeGrant()) {
			case CLIENT_CREDENTIALS:
				apiProperties.setSecurity(panelClientCredentials.getSecurity());
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		
		return apiProperties;
	}

	@Override
	public void onChange(ChangeEvent event) {
		
	}
	
	public boolean isValid() {
		APIProperties urlProperties = getUrlProperties();
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
