package bpm.fd.web.client.panels.properties.widgets;

import bpm.fd.core.ComponentParameter;
import bpm.fd.core.DashboardComponent;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.handlers.HasComponentSelectionHandler;
import bpm.fd.web.client.handlers.IComponentSelectionHandler;
import bpm.fd.web.client.panels.properties.ParametersProperties;
import bpm.fd.web.client.widgets.DashboardWidget;
import bpm.gwt.commons.client.custom.LabelTextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ParameterPanel extends Composite implements IComponentSelectionHandler {

	private static DataAggregationPropertiesUiBinder uiBinder = GWT.create(DataAggregationPropertiesUiBinder.class);

	interface DataAggregationPropertiesUiBinder extends UiBinder<Widget, ParameterPanel> {
	}

	interface MyStyle extends CssResource {
		String focus();
	}

	@UiField
	MyStyle style;

	@UiField
	FocusPanel panelSelection;

	@UiField
	LabelTextBox txtParam;

	@UiField
	Label txtComponent;

	@UiField
	Image btnDel;

	private HasComponentSelectionHandler parent;
	private ParametersProperties parametersProperties;
	
	private ComponentParameter parameter;
	private DashboardComponent selectedComponent;

	private boolean canModify;
	private boolean selection = false;

	public ParameterPanel(HasComponentSelectionHandler parent, ParametersProperties parametersProperties, ComponentParameter parameter, boolean canModify) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.parametersProperties = parametersProperties;
		this.parameter = parameter;
		this.canModify = canModify;

		txtParam.setText(parameter != null ? parameter.getName() : Labels.lblCnst.Unknown());
		txtComponent.setText(parameter.getProvider() != null ? parameter.getProvider().getTitle() : Labels.lblCnst.NoSelection());

		if (parameter.getProvider() != null) {
			selectedComponent = parameter.getProvider();
		}
		
		if (canModify) {
			txtParam.setEnabled(true);
			btnDel.setVisible(true);
		}
	}

	@UiHandler("panelSelection")
	public void onSelectionClick(ClickEvent event) {
		updateUi(null);
	}

	@UiHandler("btnDel")
	public void onDelClick(ClickEvent event) {
		parametersProperties.removeParam(this);
	}

	private void updateUi(DashboardComponent selectedComponent) {
		this.selection = !selection;
		if (selection) {
			panelSelection.addStyleName(style.focus());
			txtComponent.setText(Labels.lblCnst.SelectComponent());

			parent.addComponentSelectionHandler(this);
		}
		else {
			panelSelection.removeStyleName(style.focus());

			if (selectedComponent != null) {
				this.selectedComponent = selectedComponent;
			}
			txtComponent.setText(this.selectedComponent != null ? this.selectedComponent.getTitle() : Labels.lblCnst.NoSelection());

			parent.removeComponentSelectionHandler(this);
		}
	}

	public ComponentParameter getParameter() {
		if (canModify) {
			String name = txtParam.getText();
			parameter.setName(name);
		}
		
		parameter.setProvider(selectedComponent);
		return parameter;
	}

	@Override
	public void selectComponent(DashboardWidget widget) {
		DashboardComponent component = widget.getComponent();
		updateUi(component);
	}
}
