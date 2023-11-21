package bpm.vanillahub.web.client.properties.parameters;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.gwt.workflow.commons.client.workflow.properties.parameters.HasParameterWidget;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.activities.RunVanillaItemActivity;
import bpm.vanillahub.core.beans.activities.attributes.VanillaItemParameter;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class RunVanillaItemParameterPanel extends Composite {

	private static RunVanillaItemParameterPanelUiBinder uiBinder = GWT.create(RunVanillaItemParameterPanelUiBinder.class);

	interface RunVanillaItemParameterPanelUiBinder extends UiBinder<Widget, RunVanillaItemParameterPanel> {}

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
	
	public RunVanillaItemParameterPanel(ResourceManager resourceManager, RunVanillaItemActivity vanillaActivity) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.resourceManager = resourceManager;
		
		if (vanillaActivity.getParameters() != null) {
			createParameters(vanillaActivity.getParameters());
		}
	}

	private void createParameters(List<VanillaItemParameter> parameters) {
		widgetParameters = new ArrayList<WidgetParameter>();

		for (VanillaItemParameter param : parameters) {
			WidgetParameter widgetParam = new WidgetParameter(param);
			widgetParameters.add(widgetParam);

			mainPanel.add(widgetParam.getWidget());
		}
	}
	
	private class WidgetParameter {

		private VanillaItemParameter parameter;
		private HasParameterWidget parameterWidget;

		public WidgetParameter(VanillaItemParameter parameter) {
			this.parameter = parameter;
			this.parameterWidget = createParameterWidget(parameter);
		}

		public Widget getWidget() {
			return createWidget(parameter, parameterWidget);
		}

		private Widget createWidget(VanillaItemParameter parameter, HasParameterWidget parameterWidget) {
			Label lblHelp = new Label(parameter.getName());
			lblHelp.addStyleName(style.lblHelp());

			HTMLPanel panel = new HTMLPanel("");
			panel.addStyleName(style.panelParam());
			panel.add(parameterWidget);
			panel.add(lblHelp);
			return panel;
		}

		private HasParameterWidget createParameterWidget(VanillaItemParameter parameter) {
			return createText(parameter);
		}
		private HasParameterWidget createText(VanillaItemParameter param) {
			VariableString paramValue = param.getValue() != null && param.getValue() instanceof VariableString ? (VariableString) param.getValue() : null;
			return new VariableTextHolderBox(paramValue, param.getName(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		}
	}

}
