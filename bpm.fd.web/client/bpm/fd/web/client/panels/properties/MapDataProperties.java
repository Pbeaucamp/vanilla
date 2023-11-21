package bpm.fd.web.client.panels.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapDataSerie;
import bpm.fd.core.IComponentData;
import bpm.fd.core.component.MapComponent;
import bpm.fd.web.client.ClientSession;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.panels.properties.widgets.MapSeriePanel;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class MapDataProperties extends CompositeProperties<IComponentData> implements IComponentDataProperties {

	private static MapDataPropertiesUiBinder uiBinder = GWT.create(MapDataPropertiesUiBinder.class);

	interface MapDataPropertiesUiBinder extends UiBinder<Widget, MapDataProperties> {
	}
	
	@UiField
	ListBoxWithButton valueField, zoneField, mapList;
	
	@UiField
	HTMLPanel panelSeries;
	
	@UiField
	SimplePanel panelDatagrid, panelSerieContent;

	private Dataset selectedDataset;

	private MapComponent component;
	
	private DataGrid<VanillaMapDataSerie> datagrid;
	private ListDataProvider<VanillaMapDataSerie> dataprovider;
	private SingleSelectionModel<VanillaMapDataSerie> selectionModel;
	private MapSeriePanel panelSerie;

	public MapDataProperties(MapComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.component = component;
		
		datagrid = new DataGrid<VanillaMapDataSerie>();

		this.datagrid = buildGrid();
		panelDatagrid.add(datagrid);

		this.panelSerie = new MapSeriePanel();
		panelSerieContent.add(panelSerie);
		
		mapList.setList(ClientSession.getInstance().getMaps(), true);
		
		if(component.getMapId() != null) {
			int i = 0;
			for(MapVanilla m : ClientSession.getInstance().getMaps()) {
				if(m.getId() == component.getMapId().intValue()) {
					mapList.setSelectedIndex(i + 1);
					MapVanilla map = (MapVanilla) mapList.getSelectedObject();
					List<VanillaMapDataSerie> series = createSeries(map);
					dataprovider.setList(series);
				}
				i++;
			}
		}
		
		mapList.addChangeHandler(new ChangeHandler() {	
			@Override
			public void onChange(ChangeEvent event) {
				MapVanilla map = (MapVanilla) mapList.getSelectedObject();
				if(map != null) {
					List<VanillaMapDataSerie> series = createSeries(map);
					dataprovider.setList(series);
				}
			}
		});
	}

	private List<VanillaMapDataSerie> createSeries(MapVanilla map) {
		List<VanillaMapDataSerie> series = new ArrayList<VanillaMapDataSerie>();
		for(MapDataSet dataset : map.getDataSetList()) {
			VanillaMapDataSerie serie = new VanillaMapDataSerie();
			serie.setName(dataset.getName());
			serie.setMapDataset(dataset);
			serie.setType(dataset.getType());
			serie.setMaxColor("8BBA00");
			serie.setMinColor("FF654F");
			serie.setMinMarkerSize(dataset.getMarkerSizeMin());
			serie.setMaxMarkerSize(dataset.getMarkerSizeMax());
			series.add(serie);
		}
		return series;
	}

	private DataGrid<VanillaMapDataSerie> buildGrid() {
		TextCell cell = new TextCell();

		Column<VanillaMapDataSerie, String> colName = new Column<VanillaMapDataSerie, String>(cell) {
			@Override
			public String getValue(VanillaMapDataSerie object) {
				return object.getName();
			}
		};

		dataprovider = new ListDataProvider<VanillaMapDataSerie>();

		DataGrid<VanillaMapDataSerie> datagrid = new DataGrid<VanillaMapDataSerie>();
		datagrid.setSize("100%", "100%");
		datagrid.addColumn(colName, LabelsConstants.lblCnst.Name());
		datagrid.setEmptyTableWidget(new Label(Labels.lblCnst.NoMeasures()));
		
		dataprovider.addDataDisplay(datagrid);

		selectionModel = new SingleSelectionModel<VanillaMapDataSerie>();
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				panelSerie.setSerie(selectionModel.getSelectedObject());
			}
		});
		datagrid.setSelectionModel(selectionModel);	
	    
	    if(component.getSeries() != null) {
	    	dataprovider.setList(new ArrayList<VanillaMapDataSerie>(component.getSeries()));
	    }
	    
	    return datagrid;
	}
	
	@Override
	public void setDataset(Dataset dataset, boolean refresh) {
		selectedDataset = dataset;
		
		valueField.setList(dataset.getMetacolumns());
		zoneField.setList(dataset.getMetacolumns());
		
		if(component.getDataset() != null && component.getDataset().getId() == dataset.getId()) {
			if(component.getValueFieldIndex() != null) {
				valueField.setSelectedIndex(component.getValueFieldIndex());
			}
			if(component.getZoneFieldIndex() != null) {
				zoneField.setSelectedIndex(component.getZoneFieldIndex());
			}
		}
	}

	@Override
	public void setDataset(Dataset dataset) {
		setDataset(dataset, true);
	}

	@Override
	public void buildProperties(IComponentData component) {
		MapComponent comp = (MapComponent) component;
		
		comp.setValueFieldIndex(valueField.getSelectedIndex());
		comp.setZoneFieldIndex(zoneField.getSelectedIndex());
		
		comp.setSeries(new ArrayList<VanillaMapDataSerie>(dataprovider.getList()));
		
		comp.setMap((MapVanilla) mapList.getSelectedObject());
	}

}
