package bpm.vanillahub.web.client.properties.creation.attribute;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PanelWithVariables;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.activities.attributes.Quandl;
import bpm.vanillahub.core.beans.activities.attributes.Quandl.FormatOutput;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.properties.creation.DataServiceActivityProperties;
import bpm.vanillahub.web.client.properties.parameters.QuandlParametersPanel;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class QuandlProperties extends Composite implements ChangeHandler, PanelWithVariables {

	private static QuandlPropertiesUiBinder uiBinder = GWT.create(QuandlPropertiesUiBinder.class);

	interface QuandlPropertiesUiBinder extends UiBinder<Widget, QuandlProperties> {
	}

	interface MyStyle extends CssResource {
		String txt();

		String error();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelTexts;

	@UiField
	ListBox lstFormats;

	@UiField
	SimplePanel panelParameters;

	@UiField
	HTML helpUrl;

	private DataServiceActivityProperties parentProperties;
	private ResourceManager resourceManager;
	private Quandl quandl;

	private List<VariableTextHolderBox> variableTexts = new ArrayList<VariableTextHolderBox>();
	private QuandlParametersPanel gridParameters;

	public QuandlProperties(DataServiceActivityProperties parentProperties, ResourceManager resourceManager, Quandl quandl) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parentProperties = parentProperties;
		this.resourceManager = resourceManager;
		this.quandl = quandl != null ? quandl : new Quandl();

		VariableTextHolderBox txtOutputName = new VariableTextHolderBox(this.quandl.getOutputNameVS(), LabelsCommon.lblCnst.OutputFileName(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtOutputName.addChangeHandler(this);
		variableTexts.add(txtOutputName);
		panelTexts.add(txtOutputName);

		VariableTextHolderBox txtApiKey = new VariableTextHolderBox(this.quandl.getApiKeyVS(), Labels.lblCnst.ApiKey(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtApiKey.addChangeHandler(this);
		variableTexts.add(txtApiKey);
		panelTexts.add(txtApiKey);

		VariableTextHolderBox txtBase = new VariableTextHolderBox(this.quandl.getBaseVS(), Labels.lblCnst.QuandlBase(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtBase.addChangeHandler(this);
		variableTexts.add(txtBase);
		panelTexts.add(txtBase);

		VariableTextHolderBox txtIndicator = new VariableTextHolderBox(this.quandl.getIndicatorVS(), Labels.lblCnst.QuandlIndicator(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtIndicator.addChangeHandler(this);
		variableTexts.add(txtIndicator);
		panelTexts.add(txtIndicator);

		int selectedIndex = 0;
		int i = 0;
		for (FormatOutput output : FormatOutput.values()) {
			lstFormats.addItem(output.getFormat(), String.valueOf(output.getType()));
			if (this.quandl.getFormatOutput() != null && this.quandl.getFormatOutput() == output) {
				selectedIndex = i;
			}
			i++;
		}
		lstFormats.setSelectedIndex(selectedIndex);
		lstFormats.addChangeHandler(this);

		gridParameters = new QuandlParametersPanel(this, resourceManager, this.quandl);
		panelParameters.setWidget(gridParameters);

		refreshGeneratedUrl();

		helpUrl.setHTML(Labels.lblCnst.Help() + " : <u>" + Quandl.HELP_URL + "</u>");
	}

	@UiHandler("helpUrl")
	public void onHelpClick(ClickEvent event) {
		Window.open(Quandl.HELP_URL,"_blank","");
	}

	public Quandl getQuandl() {
		int formatId = Integer.parseInt(lstFormats.getValue(lstFormats.getSelectedIndex()));
		FormatOutput output = FormatOutput.valueOf(formatId);

		quandl.setFormatOutput(output);
		quandl.setParameters(gridParameters.getParameters());

		return quandl;
	}

	@Override
	public void onChange(ChangeEvent event) {
		refreshGeneratedUrl();
	}

	public void refreshGeneratedUrl() {
		parentProperties.refreshGeneratedUrl(generateurl());
	}

	private String generateurl() {
		Quandl quandl = getQuandl();
		return quandl.buildDataUrl(resourceManager.getParameters(), resourceManager.getVariables());
	}

	public boolean isValid() {
		Quandl quandl = getQuandl();
		return quandl.isValid();
	}

	@Override
	public void setVariables(List<Variable> variables, List<Parameter> parameters) {
		if (variableTexts != null) {
			for (VariableTextHolderBox variableText : variableTexts) {
				variableText.setVariables(variables);
				variableText.setParameters(parameters);
			}
		}
	}
}
