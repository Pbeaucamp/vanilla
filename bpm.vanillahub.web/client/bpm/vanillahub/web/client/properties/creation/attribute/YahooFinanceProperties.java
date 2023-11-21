package bpm.vanillahub.web.client.properties.creation.attribute;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PanelWithVariables;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.activities.attributes.YahooFinance;
import bpm.vanillahub.core.beans.activities.attributes.YahooFinance.TypeFinance;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.properties.creation.DataServiceActivityProperties;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class YahooFinanceProperties extends Composite implements ChangeHandler, PanelWithVariables {

	private static QuandlPropertiesUiBinder uiBinder = GWT.create(QuandlPropertiesUiBinder.class);

	interface QuandlPropertiesUiBinder extends UiBinder<Widget, YahooFinanceProperties> {
	}

	interface MyStyle extends CssResource {
		String txt();

		String error();
	}

	@UiField
	MyStyle style;

	@UiField
	ListBox lstType;

	@UiField
	HTMLPanel panelTexts;
	
	@UiField
	HTML helpUrl;

	private DataServiceActivityProperties parentProperties;
	private ResourceManager resourceManager;
	private YahooFinance yahooFinance;

	private List<VariableTextHolderBox> variableTexts = new ArrayList<VariableTextHolderBox>();

	public YahooFinanceProperties(DataServiceActivityProperties parentProperties, ResourceManager resourceManager, YahooFinance yahooFinance) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parentProperties = parentProperties;
		this.resourceManager = resourceManager;
		this.yahooFinance = yahooFinance != null ? yahooFinance : new YahooFinance();

		int selectedIndex = 0;
		int i = 0;
		for (TypeFinance type : TypeFinance.values()) {
			lstType.addItem(type.getName(), String.valueOf(type.getType()));
			if (this.yahooFinance.getTypeFinance() != null && this.yahooFinance.getTypeFinance() == type) {
				selectedIndex = i;
			}
			i++;
		}
		lstType.setSelectedIndex(selectedIndex);
		lstType.addChangeHandler(this);

		VariableTextHolderBox txtOutputName = new VariableTextHolderBox(this.yahooFinance.getOutputNameVS(), LabelsCommon.lblCnst.OutputFileName(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtOutputName.addChangeHandler(this);
		variableTexts.add(txtOutputName);
		panelTexts.add(txtOutputName);

		VariableTextHolderBox txtIds = new VariableTextHolderBox(this.yahooFinance.getIdsVS(), Labels.lblCnst.Ids(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtIds.addChangeHandler(this);
		variableTexts.add(txtIds);
		panelTexts.add(txtIds);

		VariableTextHolderBox txtProperties = new VariableTextHolderBox(this.yahooFinance.getPropertiesVS(), LabelsCommon.lblCnst.Properties(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtProperties.addChangeHandler(this);
		variableTexts.add(txtProperties);
		panelTexts.add(txtProperties);

		refreshGeneratedUrl();

		helpUrl.setHTML(Labels.lblCnst.Help() + " : <u>" + YahooFinance.HELP_URL + "</u>");
	}

	@UiHandler("helpUrl")
	public void onHelpClick(ClickEvent event) {
		Window.open(YahooFinance.HELP_URL,"_blank","");
	}


	public YahooFinance getYahooFinance() {
		int formatId = Integer.parseInt(lstType.getValue(lstType.getSelectedIndex()));
		TypeFinance typeFinance = TypeFinance.valueOf(formatId);

		yahooFinance.setTypeFinance(typeFinance);

		return yahooFinance;
	}

	@Override
	public void onChange(ChangeEvent event) {
		refreshGeneratedUrl();
	}

	public void refreshGeneratedUrl() {
		parentProperties.refreshGeneratedUrl(generateurl());
	}

	private String generateurl() {
		YahooFinance yahooFinance = getYahooFinance();
		return yahooFinance.buildDataUrl(resourceManager.getParameters(), resourceManager.getVariables());
	}

	public boolean isValid() {
		YahooFinance yahooFinance = getYahooFinance();
		return yahooFinance.isValid();
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
