package bpm.vanillahub.web.client.properties.parameters;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.gwt.workflow.commons.client.workflow.properties.parameters.HasParameterWidget;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceMethodDefinition;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceParameter;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class WebServiceParametersPanel extends Composite {

	private static WebServiceParametersPanelUiBinder uiBinder = GWT.create(WebServiceParametersPanelUiBinder.class);

	interface WebServiceParametersPanelUiBinder extends UiBinder<Widget, WebServiceParametersPanel> {
	}

	interface MyStyle extends CssResource {
		String panelParam();
		String txt();
		String checkbox();
		String lblHelp();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel mainPanel;

	private ResourceManager resourceManager;
	private List<WidgetParameter> widgetParameters;

	public WebServiceParametersPanel(ResourceManager resourceManager, WebServiceMethodDefinition webServiceDefinition) {
		initWidget(uiBinder.createAndBindUi(this));
		this.resourceManager = resourceManager;

		if (webServiceDefinition.getParameters() != null) {
			createParameters(webServiceDefinition.getParameters());
		}
	}

	private void createParameters(List<WebServiceParameter> parameters) {
		widgetParameters = new ArrayList<WidgetParameter>();

		for (WebServiceParameter param : parameters) {
			WidgetParameter widgetParam = new WidgetParameter(param);
			widgetParameters.add(widgetParam);

			mainPanel.add(widgetParam.getWidget());
		}
	}

	public List<WebServiceParameter> getParameters() {
		List<WebServiceParameter> params = new ArrayList<WebServiceParameter>();
		if (widgetParameters != null) {
			for(WidgetParameter widg : widgetParameters) {
				WebServiceParameter param = widg.getParameter();
				param.setValue(widg.getParameterValue());
				
				params.add(param);
			}
		}
		return params;
	}

	private class WidgetParameter {

		private WebServiceParameter parameter;
		private HasParameterWidget parameterWidget;

		public WidgetParameter(WebServiceParameter parameter) {
			this.parameter = parameter;
			this.parameterWidget = createParameterWidget(parameter);
		}
		
		public WebServiceParameter getParameter() {
			return parameter;
		}

		public Object getParameterValue() {
			return parameterWidget.getParameterValue();
		}

		public Widget getWidget() {
			return createWidget(parameter, parameterWidget);
		}

		private Widget createWidget(WebServiceParameter parameter, HasParameterWidget parameterWidget) {
			Label lblHelp = new Label(parameter.getDefinition());
			lblHelp.addStyleName(style.lblHelp());

			HTMLPanel panel = new HTMLPanel("");
			panel.addStyleName(style.panelParam());
			panel.add(parameterWidget);
			panel.add(lblHelp);
			return panel;
		}

		private HasParameterWidget createParameterWidget(WebServiceParameter parameter) {
			switch (parameter.getType()) {
			case TYPE_BOOLEAN:
				return createCheckbox(parameter);
			case TYPE_DATE:
				return createDate(parameter);
			case TYPE_DOUBLE:
			case TYPE_FLOAT:
			case TYPE_INT:
			case TYPE_LONG:
				return createTextDecimal(parameter);
			case TYPE_STRING:
			case TYPE_OBJECT:
				return createText(parameter);

			default:
				return null;
			}
		}

		private HasParameterWidget createCheckbox(WebServiceParameter param) {
			CheckBoxParameter check = new CheckBoxParameter(param);
			check.addStyleName(style.checkbox());
			return check;
		}

		private HasParameterWidget createDate(WebServiceParameter param) {
			return new DateParameterWidget(param, resourceManager);
		}

		private HasParameterWidget createTextDecimal(WebServiceParameter param) {
			VariableString paramValue = param.getParameterValue() != null && param.getParameterValue() instanceof VariableString ? (VariableString) param.getParameterValue() : null;
			return new VariableTextHolderBox(paramValue, param.getName(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		}

		private HasParameterWidget createText(WebServiceParameter param) {
			VariableString paramValue = param.getParameterValue() != null && param.getParameterValue() instanceof VariableString ? (VariableString) param.getParameterValue() : null;
			return new VariableTextHolderBox(paramValue, param.getName(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		}
	}
}
