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

import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PanelWithVariables;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.activities.attributes.ClientCredentialsProperties;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;

public class ClientCredentialsPropertiesPanel extends Composite implements ChangeHandler, PanelWithVariables {

	private static APIPropertiesPanelUiBinder uiBinder = GWT.create(APIPropertiesPanelUiBinder.class);

	interface APIPropertiesPanelUiBinder extends UiBinder<Widget, ClientCredentialsPropertiesPanel> {
	}
	
	interface MyStyle extends CssResource {
		String txt();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelTexts;

	private ClientCredentialsProperties clientCredentials;

	private List<VariableTextHolderBox> variableTexts = new ArrayList<VariableTextHolderBox>();

	public ClientCredentialsPropertiesPanel(ResourceManager resourceManager, ClientCredentialsProperties clientCredentials) {
		initWidget(uiBinder.createAndBindUi(this));
		this.clientCredentials = clientCredentials != null ? clientCredentials : new ClientCredentialsProperties();

		VariableTextHolderBox txtUrl = new VariableTextHolderBox(this.clientCredentials.getAccessTokenURLVS(), Labels.lblCnst.AccessTokenURL(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtUrl.addChangeHandler(this);
		variableTexts.add(txtUrl);
		panelTexts.add(txtUrl);

		VariableTextHolderBox txtClientId = new VariableTextHolderBox(this.clientCredentials.getClientIdVS(), Labels.lblCnst.ClientId(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtClientId.addChangeHandler(this);
		variableTexts.add(txtClientId);
		panelTexts.add(txtClientId);

		VariableTextHolderBox txtClientSecret = new VariableTextHolderBox(this.clientCredentials.getClientSecretVS(), Labels.lblCnst.ClientSecret(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtClientSecret.addChangeHandler(this);
		variableTexts.add(txtClientSecret);
		panelTexts.add(txtClientSecret);

		VariableTextHolderBox txtScope = new VariableTextHolderBox(this.clientCredentials.getScopeVS(), Labels.lblCnst.Scope(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtScope.addChangeHandler(this);
		variableTexts.add(txtScope);
		panelTexts.add(txtScope);
	}

	public ClientCredentialsProperties getSecurity() {
		return clientCredentials;
	}

	@Override
	public void onChange(ChangeEvent event) {
		
	}
	
	public boolean isValid() {
		ClientCredentialsProperties urlProperties = getSecurity();
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
