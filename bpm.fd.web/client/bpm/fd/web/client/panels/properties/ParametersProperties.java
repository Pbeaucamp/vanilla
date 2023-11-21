package bpm.fd.web.client.panels.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.core.ComponentParameter;
import bpm.fd.core.DashboardComponent;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.handlers.HasComponentSelectionHandler;
import bpm.fd.web.client.panels.properties.widgets.ParameterPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ParametersProperties extends CompositeProperties<DashboardComponent> {

	private static OptionsPropertiesUiBinder uiBinder = GWT.create(OptionsPropertiesUiBinder.class);

	interface OptionsPropertiesUiBinder extends UiBinder<Widget, ParametersProperties> {
	}

	@UiField
	HTMLPanel toolbar, mainPanel;

	private HasComponentSelectionHandler parent;
	private List<ParameterPanel> paramPanels;

	public ParametersProperties(HasComponentSelectionHandler parent, List<ComponentParameter> parameters, boolean canCreateNew) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		loadParameters(parameters, canCreateNew);
		
		if (canCreateNew) {
			toolbar.setVisible(true);
		}
	}

	public void loadParameters(List<ComponentParameter> parameters, boolean canCreateNew) {
		this.paramPanels = new ArrayList<>();

		if (parameters != null) {
			for (ComponentParameter param : parameters) {
				addParam(param, canCreateNew);
			}
		}
	}
	
	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		ComponentParameter parameter = new ComponentParameter();
		parameter.setName(Labels.lblCnst.MyParameter() + "_" + (paramPanels.size() + 1));
		addParam(parameter, true);
	}

	@Override
	public void buildProperties(DashboardComponent component) {
		List<ComponentParameter> parameters = new ArrayList<>();
		for (ParameterPanel paramPanel : paramPanels) {
			parameters.add(paramPanel.getParameter());
		}

		component.setParameters(parameters);
	}
	
	private void addParam(ComponentParameter param, boolean canDelete) {
		ParameterPanel parameterPanel = new ParameterPanel(parent, this, param, canDelete);
		paramPanels.add(parameterPanel);
		mainPanel.add(parameterPanel);
	}

	public void removeParam(ParameterPanel parameterPanel) {
		paramPanels.remove(parameterPanel);
		mainPanel.remove(parameterPanel);
	}
}
