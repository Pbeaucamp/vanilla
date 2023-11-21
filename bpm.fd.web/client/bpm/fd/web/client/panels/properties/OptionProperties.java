package bpm.fd.web.client.panels.properties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.ChartComponent;
import bpm.fd.core.component.CommentComponent;
import bpm.fd.core.component.ContentComponent;
import bpm.fd.core.component.CubeViewComponent;
import bpm.fd.core.component.D4CComponent;
import bpm.fd.core.component.DataVizComponent;
import bpm.fd.core.component.DatagridComponent;
import bpm.fd.core.component.DivComponent;
import bpm.fd.core.component.FilterComponent;
import bpm.fd.core.component.FlourishComponent;
import bpm.fd.core.component.GaugeComponent;
import bpm.fd.core.component.ImageComponent;
import bpm.fd.core.component.JspComponent;
import bpm.fd.core.component.KpiChartComponent;
import bpm.fd.core.component.MapComponent;
import bpm.fd.core.component.RChartComponent;
import bpm.fd.core.component.kpi.KpiChart;
import bpm.gwt.commons.client.loading.IWait;

public class OptionProperties extends CompositeProperties<IComponentOption> {

	private static DataPropertiesUiBinder uiBinder = GWT.create(DataPropertiesUiBinder.class);

	interface DataPropertiesUiBinder extends UiBinder<Widget, OptionProperties> {
	}

	@UiField
	SimplePanel panelOptions;

	private CompositeProperties<IComponentOption> compositeProperties;

	public OptionProperties(IWait waitPanel, DashboardComponent component) {
		initWidget(uiBinder.createAndBindUi(this));

		// Ajouter un if pour RChart option 
		if (component instanceof ChartComponent) {
			this.compositeProperties = new ChartOptionsProperties(((ChartComponent) component));
		}
		if (component instanceof RChartComponent) {
			this.compositeProperties = new RChartOptionsProperties(((RChartComponent) component));
		}
		else if (component instanceof FilterComponent) {
			this.compositeProperties = new FilterOptionProperties((FilterComponent) component);
		}
		else if (component instanceof ImageComponent) {
			this.compositeProperties = new ImageOptionsProperties(waitPanel, (ImageComponent) component);
		}
		else if (component instanceof GaugeComponent) {
			this.compositeProperties = new GaugeOptionsProperties((GaugeComponent) component);
		}
		else if (component instanceof JspComponent) {
			this.compositeProperties = new JspOptionsProperties((JspComponent) component);
		}
		else if (component instanceof D4CComponent) {
			this.compositeProperties = new D4COptionsProperties((D4CComponent) component);
		}
		else if (component instanceof CommentComponent) {
			this.compositeProperties = new CommentOptionsProperties((CommentComponent) component);
		}
		else if (component instanceof MapComponent) {
			this.compositeProperties = new MapOptionsProperties((MapComponent) component);
		}
		else if (component instanceof DatagridComponent) {
			this.compositeProperties = new DataGridOptionsProperties((DatagridComponent) component);
		}
		else if (component instanceof CubeViewComponent) {
			this.compositeProperties = new CubeViewOptionsProperties(waitPanel, (CubeViewComponent) component);
		}
		else if (component instanceof ContentComponent && !(component instanceof DivComponent)) {
			this.compositeProperties = new ContentComponentOptionsProperties((ContentComponent) component);
		}
		else if (component instanceof KpiChartComponent) {
			this.compositeProperties = new ChartOptionsProperties(((KpiChart)((KpiChartComponent)component).getElement()).getOption());
		}
		else if (component instanceof DataVizComponent) {
			this.compositeProperties = new DataVizProperties(((DataVizComponent)component));
		}
		else if (component instanceof FlourishComponent) {
			this.compositeProperties = new FlourishOptionsProperties((FlourishComponent) component);
		}

		if (compositeProperties != null) {
			panelOptions.setWidget(compositeProperties);
		}
	}

	@Override
	public void buildProperties(IComponentOption component) {
		compositeProperties.buildProperties(component);
	}

	@Override
	public void refreshOptions(IComponentOption component) {
		compositeProperties.refreshOptions(component);
	}
}
