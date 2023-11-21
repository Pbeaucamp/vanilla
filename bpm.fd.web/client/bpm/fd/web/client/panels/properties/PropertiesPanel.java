package bpm.fd.web.client.panels.properties;

import java.util.List;

import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.RChartNature;
import bpm.fd.core.ComponentParameter;
import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentData;
import bpm.fd.core.IComponentOption;
import bpm.fd.core.IComponentRepositoryItem;
import bpm.fd.core.IComponentUrl;
import bpm.fd.core.component.ChartComponent;
import bpm.fd.core.component.KpiComponent;
import bpm.fd.core.component.RChartComponent;
import bpm.fd.web.client.handlers.HasComponentSelectionHandler;
import bpm.fd.web.client.panels.properties.ChartTypeProperties.TypeChart;
import bpm.fd.web.client.panels.properties.ChartTypeProperties.TypeDisplay;
import bpm.fd.web.client.panels.properties.RChartTypeProperties.TypeRChart;
import bpm.fd.web.client.utils.ChartTypeHelper;
import bpm.fd.web.client.utils.RChartTypeHelper;
import bpm.gwt.commons.client.loading.IWait;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PropertiesPanel extends Composite {

	private static PropertiesPanelUiBinder uiBinder = GWT.create(PropertiesPanelUiBinder.class);

	interface PropertiesPanelUiBinder extends UiBinder<Widget, PropertiesPanel> {
	}

	interface MyStyle extends CssResource {
		String minSize();
		String btnSelected();
		String panelWithButtons();
		String panelWithoutButtons();
	}

	@UiField
	MyStyle style;

	@UiField
	FocusPanel btnGeneral, btnChartType, btnData, btnOptions, btnRepository, btnParameters;

	@UiField
	SimplePanel contentPanel;
	
	@UiField
	HTMLPanel mainPanel, panelButton;

	private PopupPanel parent;
	private HasComponentSelectionHandler componentSelectionHandler;

	private GeneralProperties generalProperties;
	private ChartTypeProperties chartTypeProperties;
	private RChartTypeProperties RchartTypeProperties;
	private DataProperties dataProperties;
	private RepositoryItemOptionProperties repositoryProperties;
	private OptionProperties optionsProperties;
	private ParametersProperties parametersProperties;

	private DashboardComponent component;

	private FocusPanel selectedBtn;

	public PropertiesPanel(IWait waitPanel, HasComponentSelectionHandler componentSelectionHandler, PopupPanel parent, DashboardComponent component, boolean compact) {
		initWidget(uiBinder.createAndBindUi(this));
		this.componentSelectionHandler = componentSelectionHandler;
		this.parent = parent;
		this.component = component;

		this.selectedBtn = btnGeneral;
		this.generalProperties = new GeneralProperties(component);
		contentPanel.setWidget(generalProperties);

		if (component instanceof ChartComponent) {
			btnChartType.setVisible(true);
			this.chartTypeProperties = new ChartTypeProperties((ChartComponent) component, this);
		}
		if (component instanceof RChartComponent) {
			btnChartType.setVisible(true);
			this.RchartTypeProperties = new RChartTypeProperties((RChartComponent) component, this);
		}
		//Plusieurs if a faire si rcharcomponent , si differentes interfaces implementees pour ajouter les onglets option donnees proprietes

		if (component instanceof IComponentData) {
			btnData.setVisible(true);
			this.dataProperties = new DataProperties(waitPanel, (IComponentData) component, this);
		}

		if (component instanceof IComponentRepositoryItem) {
			btnRepository.setVisible(true);
			this.repositoryProperties = new RepositoryItemOptionProperties((IComponentRepositoryItem) component, this);
		}

		if (component instanceof IComponentOption) {
			btnOptions.setVisible(true);
			this.optionsProperties = new OptionProperties(waitPanel, component);
		}

		if (component instanceof IComponentUrl || component instanceof KpiComponent) {
			btnParameters.setVisible(true);
			this.parametersProperties = new ParametersProperties(componentSelectionHandler, component.getParameters(), true);
		}
		else if (component.getParameters() != null && !component.getParameters().isEmpty()) {
			loadParameters(component, component.getParameters());
		}
		
		if (compact) {
			contentPanel.removeStyleName(style.panelWithButtons());
			contentPanel.addStyleName(style.panelWithoutButtons());
			
			panelButton.removeFromParent();
		}
	}

	public void loadParameters(DashboardComponent component, List<ComponentParameter> parameters) {
		if (parameters != null && !parameters.isEmpty()) {
			this.parametersProperties = new ParametersProperties(componentSelectionHandler, parameters, false);
			btnParameters.setVisible(true);
		}
		else if (component != null && component instanceof KpiComponent) {
			btnParameters.setVisible(true);
			this.parametersProperties = new ParametersProperties(componentSelectionHandler, component.getParameters(), true);
		}
		else {
			this.parametersProperties = null;
			btnParameters.setVisible(false);
		}
	}

	public void refreshOptions(DashboardComponent component) {
		if (optionsProperties != null && component instanceof IComponentOption) {
			optionsProperties.refreshOptions((IComponentOption) component);
		}
	}

	private void updateUi(FocusPanel btn) {
		selectedBtn.removeStyleName(style.btnSelected());

		this.selectedBtn = btn;
		btn.addStyleName(style.btnSelected());
	}

	@UiHandler("btnGeneral")
	public void onGeneral(ClickEvent event) {
		contentPanel.setWidget(generalProperties);
		updateUi(btnGeneral);
	}

	@UiHandler("btnChartType")
	public void onChart(ClickEvent event) {
		if( chartTypeProperties != null ){
			contentPanel.setWidget(chartTypeProperties);
		}
		else if (RchartTypeProperties != null ){
			contentPanel.setWidget(RchartTypeProperties);
		}
		updateUi(btnChartType);
	}

	@UiHandler("btnData")
	public void onData(ClickEvent event) {
		contentPanel.setWidget(dataProperties);
		updateUi(btnData);
	}

	@UiHandler("btnOptions")
	public void onOptions(ClickEvent event) {
		contentPanel.setWidget(optionsProperties);
		updateUi(btnOptions);
	}

	@UiHandler("btnRepository")
	public void onRepository(ClickEvent event) {
		contentPanel.setWidget(repositoryProperties);
		updateUi(btnRepository);
	}

	@UiHandler("btnParameters")
	public void onParameters(ClickEvent event) {
		contentPanel.setWidget(parametersProperties);
		updateUi(btnParameters);
	}

	@UiHandler("btnConfirm")
	public void onConfirm(ClickEvent event) {
		confirm();
		
		if (parent != null) {
			parent.hide();
		}
	}

	@UiHandler("btnCancel")
	public void onCancel(ClickEvent event) {
		if (parent != null) {
			parent.hide();
		}
	}
	
	public void confirm() {
		if (generalProperties != null) {
			this.generalProperties.buildProperties(component);
		}

		if (dataProperties != null) {
			this.dataProperties.buildProperties((IComponentData) component);
		}

		if (optionsProperties != null) {
			this.optionsProperties.buildProperties((IComponentOption) component);
		}

		if (component instanceof IComponentRepositoryItem) {
			this.repositoryProperties.buildProperties((IComponentRepositoryItem) component);
		}

		if (parametersProperties != null) {
			this.parametersProperties.buildProperties(component);
		}

		if (chartTypeProperties != null) {
			TypeChart typeChart = chartTypeProperties.getSelectedTypeChart();
			TypeDisplay typeDisplay = chartTypeProperties.getSelectedTypeDisplay();
			boolean is3D = chartTypeProperties.is3D();
			ChartNature nature = ChartTypeHelper.getChartNature((ChartComponent) component, typeChart, typeDisplay, is3D);
			((ChartComponent) component).setNature(nature);
		}
		if (RchartTypeProperties != null) {
			TypeRChart typeChart = RchartTypeProperties.getSelectedTypeChart();
			RChartNature nature = RChartTypeHelper.getRChartNature((RChartComponent) component, typeChart);
			((RChartComponent) component).setNature(nature);
		}

		componentSelectionHandler.setModified(true);
	}

	public void resize(boolean reduce) {
		if (reduce) {
			mainPanel.addStyleName(style.minSize());
		}
		else {
			mainPanel.removeStyleName(style.minSize());
			
			int clientHeight = Window.getClientHeight();
			int clientWidth = Window.getClientWidth();
			
			mainPanel.getElement().getStyle().setProperty("height", (clientHeight - 100) + "px");
			mainPanel.getElement().getStyle().setProperty("width", (clientWidth - 100) + "px");
		}
	}
}
