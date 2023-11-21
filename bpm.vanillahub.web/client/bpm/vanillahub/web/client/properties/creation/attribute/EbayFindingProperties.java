package bpm.vanillahub.web.client.properties.creation.attribute;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PanelWithVariables;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.activities.attributes.EbayFinding;
import bpm.vanillahub.core.beans.activities.attributes.EbayFinding.FormatOutput;
import bpm.vanillahub.core.beans.activities.attributes.EbayFinding.TypeProduct;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.properties.creation.DataServiceActivityProperties;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class EbayFindingProperties extends Composite implements ChangeHandler, PanelWithVariables {

	private static EbayFindingPropertiesUiBinder uiBinder = GWT.create(EbayFindingPropertiesUiBinder.class);

	interface EbayFindingPropertiesUiBinder extends UiBinder<Widget, EbayFindingProperties> {
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
	ListBox lstFormats;

	@UiField
	HTMLPanel panelTexts;

	private DataServiceActivityProperties parentProperties;
	private ResourceManager resourceManager;
	private EbayFinding ebayFinding;

	private List<VariableTextHolderBox> variableTexts = new ArrayList<VariableTextHolderBox>();

	public EbayFindingProperties(DataServiceActivityProperties parentProperties, ResourceManager resourceManager, EbayFinding ebayFinding) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parentProperties = parentProperties;
		this.resourceManager = resourceManager;
		this.ebayFinding = ebayFinding != null ? ebayFinding : new EbayFinding();

		int selectedIndex = 0;
		int i = 0;
		for (TypeProduct type : TypeProduct.values()) {
			lstType.addItem(type.getName(), String.valueOf(type.getType()));
			if (this.ebayFinding.getTypeProduct() != null && this.ebayFinding.getTypeProduct() == type) {
				selectedIndex = i;
			}
			i++;
		}
		lstType.setSelectedIndex(selectedIndex);
		lstType.addChangeHandler(this);

		VariableTextHolderBox txtProduct = new VariableTextHolderBox(this.ebayFinding.getProductIdVS(), Labels.lblCnst.ProductId(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtProduct.addChangeHandler(this);
		variableTexts.add(txtProduct);
		panelTexts.add(txtProduct);

		VariableTextHolderBox txtOutputName = new VariableTextHolderBox(this.ebayFinding.getOutputNameVS(), LabelsCommon.lblCnst.OutputFileName(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtOutputName.addChangeHandler(this);
		variableTexts.add(txtOutputName);
		panelTexts.add(txtOutputName);

		VariableTextHolderBox txtApiKey = new VariableTextHolderBox(this.ebayFinding.getApiKeyVS(), Labels.lblCnst.ApiKey(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtApiKey.addChangeHandler(this);
		variableTexts.add(txtApiKey);
		panelTexts.add(txtApiKey);

		VariableTextHolderBox txtResultPerPage = new VariableTextHolderBox(this.ebayFinding.getResultPerPageVS(), Labels.lblCnst.ResultPerPage(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtResultPerPage.addChangeHandler(this);
		variableTexts.add(txtResultPerPage);
		panelTexts.add(txtResultPerPage);

		VariableTextHolderBox txtPageNumber = new VariableTextHolderBox(this.ebayFinding.getPageNumberVS(), Labels.lblCnst.PageNumber(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtPageNumber.addChangeHandler(this);
		variableTexts.add(txtPageNumber);
		panelTexts.add(txtPageNumber);

		int selectedFormatIndex = 0;
		i = 0;
		for (FormatOutput type : FormatOutput.values()) {
			lstFormats.addItem(type.getFormat(), String.valueOf(type.getType()));
			if (this.ebayFinding.getFormatOutput() != null && this.ebayFinding.getFormatOutput() == type) {
				selectedFormatIndex = i;
			}
			i++;
		}
		lstFormats.setSelectedIndex(selectedFormatIndex);
		lstFormats.addChangeHandler(this);

		refreshGeneratedUrl();
	}

	public EbayFinding getEbayFinding() {
		int typeId = Integer.parseInt(lstType.getValue(lstType.getSelectedIndex()));
		int formatId = Integer.parseInt(lstFormats.getValue(lstFormats.getSelectedIndex()));
		TypeProduct typeProduct = TypeProduct.valueOf(typeId);
		FormatOutput output = FormatOutput.valueOf(formatId);

		ebayFinding.setTypeProduct(typeProduct);
		ebayFinding.setFormatOutput(output);
		
		return ebayFinding;
	}

	@Override
	public void onChange(ChangeEvent event) {
		refreshGeneratedUrl();
	}

	public void refreshGeneratedUrl() {
		parentProperties.refreshGeneratedUrl(generateurl());
	}

	private String generateurl() {
		EbayFinding ebayFinding = getEbayFinding();
		return ebayFinding.buildDataUrl(resourceManager.getParameters(), resourceManager.getVariables());
	}

	public boolean isValid() {
		EbayFinding ebayFinding = getEbayFinding();
		return ebayFinding.isValid();
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
