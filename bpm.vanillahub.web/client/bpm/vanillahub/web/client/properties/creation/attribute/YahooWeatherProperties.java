package bpm.vanillahub.web.client.properties.creation.attribute;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PanelWithVariables;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.activities.attributes.YahooWeather;
import bpm.vanillahub.core.beans.activities.attributes.YahooWeather.TypeWeather;
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

public class YahooWeatherProperties extends Composite implements ChangeHandler, PanelWithVariables {

	private static QuandlPropertiesUiBinder uiBinder = GWT.create(QuandlPropertiesUiBinder.class);

	interface QuandlPropertiesUiBinder extends UiBinder<Widget, YahooWeatherProperties> {
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

	private DataServiceActivityProperties parentProperties;
	private ResourceManager resourceManager;
	private YahooWeather yahooWeather;

	private List<VariableTextHolderBox> variableTexts = new ArrayList<VariableTextHolderBox>();

	public YahooWeatherProperties(DataServiceActivityProperties parentProperties, ResourceManager resourceManager, YahooWeather yahooWeather) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parentProperties = parentProperties;
		this.resourceManager = resourceManager;
		this.yahooWeather = yahooWeather != null ? yahooWeather : new YahooWeather();

		int selectedIndex = 0;
		int i = 0;
		for (TypeWeather type : TypeWeather.values()) {
			lstType.addItem(getWeatherTypeName(type), String.valueOf(type.getType()));
			if (this.yahooWeather.getTypeWeather() != null && this.yahooWeather.getTypeWeather() == type) {
				selectedIndex = i;
			}
			i++;
		}
		lstType.setSelectedIndex(selectedIndex);
		lstType.addChangeHandler(this);

		VariableTextHolderBox txtOutputName = new VariableTextHolderBox(this.yahooWeather.getOutputNameVS(), LabelsCommon.lblCnst.OutputFileName(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtOutputName.addChangeHandler(this);
		variableTexts.add(txtOutputName);
		panelTexts.add(txtOutputName);

		VariableTextHolderBox txtLocation = new VariableTextHolderBox(this.yahooWeather.getLocationVS(), Labels.lblCnst.Location(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtLocation.addChangeHandler(this);
		variableTexts.add(txtLocation);
		panelTexts.add(txtLocation);

		refreshGeneratedUrl();
	}

	private String getWeatherTypeName(TypeWeather type) {
		switch (type) {
		case FORECAST:
			return Labels.lblCnst.Forecast();
		case CURRENT_CONDITIONS:
			return Labels.lblCnst.CurrentConditions();
		case WIND:
			return Labels.lblCnst.Wind();

		default:
			break;
		};
		return LabelsCommon.lblCnst.Unknown();
	}

	public YahooWeather getYahooWeather() {
		int formatId = Integer.parseInt(lstType.getValue(lstType.getSelectedIndex()));
		TypeWeather typeWeather = TypeWeather.valueOf(formatId);

		yahooWeather.setTypeWeather(typeWeather);

		return yahooWeather;
	}

	@Override
	public void onChange(ChangeEvent event) {
		refreshGeneratedUrl();
	}

	public void refreshGeneratedUrl() {
		parentProperties.refreshGeneratedUrl(generateurl());
	}

	private String generateurl() {
		YahooWeather yahooWeather = getYahooWeather();
		return yahooWeather.buildDataUrl(resourceManager.getParameters(), resourceManager.getVariables());
	}

	public boolean isValid() {
		YahooWeather yahooWeather = getYahooWeather();
		return yahooWeather.isValid();
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
