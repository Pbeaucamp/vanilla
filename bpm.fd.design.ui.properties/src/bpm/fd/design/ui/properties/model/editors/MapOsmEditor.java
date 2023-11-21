package bpm.fd.design.ui.properties.model.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentOsmMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapData;
import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapDataSerie;
import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapOption;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGroup;
import bpm.fd.design.ui.properties.model.PropertyOsmSerie;
import bpm.fd.design.ui.properties.model.chart.PropertyColor;
import bpm.fd.design.ui.properties.model.map.PropertyMapColor;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;
import bpm.fd.repository.ui.Activator;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.IVanillaContext;

public class MapOsmEditor extends ComponentEditor {
	
	private class ContentProvider extends PropertiesContentProvider {

		@Override
		public Object[] getChildren(Object parentElement) {
			if(def == null) {
				return null;
			}
			if(parentElement == series) {
				List l = new ArrayList();
				for(int i = 0 ; i < ((VanillaMapData) def.getDatas()).getSeries().size() ; i++) {
					VanillaMapDataSerie agg = ((VanillaMapData) def.getDatas()).getSeries().get(i);
//					System.out.println(i + " " + agg.getName());
					l.add(new PropertyOsmSerie(agg, fieldCbo, def));
				}

				return l.toArray(new Object[l.size()]);
			}
			return super.getChildren(parentElement);
		}

		@Override
		public boolean hasChildren(Object element) {
			if(def == null) {
				return false;
			}
			if(element == series) {
				VanillaMapData dt = (VanillaMapData) def.getDatas();
				if(dt == null || dt.getSeries() == null) {
					return false;
				}
				return ((VanillaMapData) def.getDatas()).getSeries().size() > 0;
			}
			return super.hasChildren(element);
		}
	}
	
	private ComboBoxViewerCellEditor dataSourceCbo;
	private ComboBoxViewerCellEditor dataSetCbo;
	private ComboBoxViewerCellEditor fieldCbo;	
	
	private PropertyGroup series;
	private TreeViewer viewer;
	private ComponentOsmMap def;
	private ComboBoxViewerCellEditor vanillaMapCombo;

	public MapOsmEditor(Composite parent) {
		super(parent);
		fillBar();
	}

	@Override
	protected void fillBar() {
		createMap(getControl());		
//		createParameters();
		createDatas(getControl());
		createSeries();
	}

	private void createMap(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText("Osm map options");

		final Property showLegend = new Property("Show legend", new CheckboxCellEditor(viewer.getTree()));
		final Property showBaseLayer = new Property("Show base layer", new CheckboxCellEditor(viewer.getTree()));
		ComboBoxViewerCellEditor orientationCombo = new ComboBoxViewerCellEditor(viewer.getTree());
		orientationCombo.setContentProvider(new ArrayContentProvider());
		orientationCombo.setLabelProvider(new LabelProvider());
		orientationCombo.setInput(VanillaMapOption.ORIENTATIONS);
		final Property legendOrientation = new Property("Legend orientation", orientationCombo);
		ComboBoxViewerCellEditor layoutCombo = new ComboBoxViewerCellEditor(viewer.getTree());
		layoutCombo.setContentProvider(new ArrayContentProvider());
		layoutCombo.setLabelProvider(new LabelProvider());
		layoutCombo.setInput(VanillaMapOption.LAYOUTS);
		final Property legendLayout = new Property("Legend layout", layoutCombo);
		final Property numberFormat = new Property("Number format", new TextCellEditor(viewer.getTree()));
		
		PropertyGroup viewpoint = new PropertyGroup(Messages.MapWmsEditor_9);
		viewpoint.add(numberFormat);
		viewpoint.add(showLegend);
		viewpoint.add(showBaseLayer);
		viewpoint.add(legendOrientation);
		viewpoint.add(legendLayout);
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentOsmMap c = (ComponentOsmMap)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				VanillaMapOption opts = (VanillaMapOption)c.getOptions(VanillaMapOption.class); 
				if (element == showLegend){return opts.isShowLegend() + "";} //$NON-NLS-1$
				if (element == showBaseLayer){return opts.isShowBaseLayer() + "";} //$NON-NLS-1$
				if (element == legendOrientation){return opts.getLegendOrientation() + "";} //$NON-NLS-1$
				if (element == legendLayout){return opts.getLegendLayout() + "";} //$NON-NLS-1$
				if (element == numberFormat){return opts.getNumberFormat() + "";} //$NON-NLS-1$
				
				return ""; //$NON-NLS-1$
			}
		});
		
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentOsmMap c = (ComponentOsmMap)getComponentDefinition();
				if (c == null){return;} //$NON-NLS-1$
				VanillaMapOption opts = (VanillaMapOption)c.getOptions(VanillaMapOption.class); 
				try{
					if (element == showLegend){opts.setShowLegend((boolean) value);} //$NON-NLS-1$
					if (element == showBaseLayer){opts.setShowBaseLayer((boolean) value);} //$NON-NLS-1$
					if (element == legendOrientation){opts.setLegendOrientation((String)value);} //$NON-NLS-1$
					if (element == legendLayout){opts.setLegendLayout((String)value);} //$NON-NLS-1$
					if (element == numberFormat){opts.setNumberFormat((String)value);} //$NON-NLS-1$
				}catch(Exception ex){}
				
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentOsmMap c = (ComponentOsmMap)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				VanillaMapOption opts = (VanillaMapOption)c.getOptions(VanillaMapOption.class); 
				if (element == showLegend){return opts.isShowLegend();} //$NON-NLS-1$
				if (element == showBaseLayer){return opts.isShowBaseLayer();} //$NON-NLS-1$
				if (element == legendOrientation){return opts.getLegendOrientation() + "";} //$NON-NLS-1$
				if (element == legendLayout){return opts.getLegendLayout() + "";} //$NON-NLS-1$
				if (element == numberFormat){return opts.getNumberFormat() + "";} //$NON-NLS-1$
				return null;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return ((Property)element).getCellEditor() != null;
			}
		});
		
		List input = new ArrayList();
		input.add(viewpoint);
		viewer.setInput(input);
	}

	private void createSeries() {
		series = new PropertyGroup(Messages.DatasChartEditor_21);
		((List)viewer.getInput()).add(series);

//		createMenu();
	}

//	private void createMenu() {
//		MenuManager mgr = new MenuManager();
//
//		final Action addSeriePolygon = new Action("Add polygon serie") {
//			public void run() {
//				OsmDataSerie da = new OsmDataSeriePolygon();
//				((VanillaMapData) def.getDatas()).addSerie(da);
//				notifyChangeOccured();
//				resize();
//			}
//		};
//		
//		final Action addSerieLine = new Action("Add line serie") {
//			public void run() {
//				OsmDataSerie da = new OsmDataSerieLine();
//				((VanillaMapData) def.getDatas()).addSerie(da);
//				notifyChangeOccured();
//				resize();
//			}
//		};
//		
//		final Action addSerieMarker = new Action("Add marker serie") {
//			public void run() {
//				OsmDataSerie da = new OsmDataSerieMarker();
//				((VanillaMapData) def.getDatas()).addSerie(da);
//				notifyChangeOccured();
//				resize();
//			}
//		};
//
//		final Action deleteSerie = new Action(Messages.DatasChartEditor_30) {
//			public void run() {
//				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
//				PropertyOsmSerie p = (PropertyOsmSerie) ss.getFirstElement();
//				((OsmData) def.getDatas()).removeSerie(p.getSerie());
//				notifyChangeOccured();
//				resize();
//				expandTree(p);
//			}
//
//		};
//		
//		mgr.add(addSeriePolygon);
//		mgr.add(addSerieLine);
//		mgr.add(addSerieMarker);
//		mgr.add(deleteSerie);
//		
//		mgr.addMenuListener(new IMenuListener() {
//
//			@Override
//			public void menuAboutToShow(IMenuManager manager) {
//				if(def == null) {
//					addSeriePolygon.setEnabled(false);
//					addSerieLine.setEnabled(false);
//					addSerieMarker.setEnabled(false);
//					deleteSerie.setEnabled(false);
//					// return;
//				}
//				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
//				if(ss.isEmpty()) {
//					addSeriePolygon.setEnabled(false);
//					addSerieLine.setEnabled(false);
//					addSerieMarker.setEnabled(false);
//					deleteSerie.setEnabled(false);
//					return;
//				}
//				deleteSerie.setEnabled(ss.getFirstElement() instanceof PropertyOsmSerie);
//				addSeriePolygon.setEnabled(!(ss.getFirstElement() instanceof PropertyOsmSerie));
//				addSerieLine.setEnabled(!(ss.getFirstElement() instanceof PropertyOsmSerie));
//				addSerieMarker.setEnabled(!(ss.getFirstElement() instanceof PropertyOsmSerie));
//			}
//		});
//
//		viewer.getTree().setMenu(mgr.createContextMenu(viewer.getTree()));
//		
//	}

	private void createDatas(ExpandBar parent) {
		viewer = createViewer(parent);
		viewer.setContentProvider(new ContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.MapEditor_16);
		
		dataSourceCbo = new ComboBoxViewerCellEditor(viewer.getTree());
		dataSourceCbo.setContenProvider(new ArrayContentProvider());
		dataSourceCbo.setLabelProvider(new DatasLabelProvider());
		
		dataSetCbo = new ComboBoxViewerCellEditor(viewer.getTree());
		dataSetCbo.setContenProvider(new ArrayContentProvider());
		dataSetCbo.setLabelProvider(new DatasLabelProvider());
		dataSetCbo.getViewer().addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				IStructuredSelection ss = (IStructuredSelection)dataSourceCbo.getViewer().getSelection();
				if (ss.isEmpty()){
					return true;
				}
				else{
					String dsName = ((DataSource)ss.getFirstElement()).getName();
					return ((DataSet)element).getDataSourceName().equals(dsName);
				}
			}
		});
		
		vanillaMapCombo = new ComboBoxViewerCellEditor(viewer.getTree());
		vanillaMapCombo.setContenProvider(new ArrayContentProvider());
		vanillaMapCombo.setLabelProvider(new DatasLabelProvider());
		

		fieldCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		fieldCbo.setContenProvider(new ArrayContentProvider());
		fieldCbo.setLabelProvider(new DatasLabelProvider());
		
		final Property datasource = new Property(Messages.MapEditor_17, dataSourceCbo);
		final Property dataset = new Property(Messages.MapEditor_18, dataSetCbo);
		final Property zoneId = new Property(Messages.MapEditor_19, fieldCbo);
		final Property valueFile = new Property(Messages.MapEditor_20, fieldCbo);
		
		final Property mapVanilla = new Property("Vanilla Map", vanillaMapCombo);
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentOsmMap c = (ComponentOsmMap)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				if(element instanceof Property && ((Property) element).getParent() instanceof PropertyOsmSerie) {
					return ((PropertyOsmSerie) ((Property) element).getParent()).getPropertyValueString(element);
				}
				if(element instanceof Property && ((Property) element).getParent() instanceof PropertyMapColor) {
					return ((PropertyMapColor) ((Property) element).getParent()).getValueString((Property) element);
				}
				VanillaMapData datas = (VanillaMapData)c.getDatas();
				if (element == datasource){return dataSourceCbo.getViewer().getCCombo().getText();}
				if (element == dataset){if (getComponentDefinition().getDatas().getDataSet() == null){return "";}return getComponentDefinition().getDatas().getDataSet().getName();} //$NON-NLS-1$
				if (element == mapVanilla){if (datas.getMap() == null){return "";}return datas.getMap().getName();} //$NON-NLS-1$
				try{
					if (element == zoneId){return datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(datas.getZoneFieldIndex() - 1).getColumnLabel();}
					if (element == valueFile){return datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(datas.getValueFieldIndex() - 1).getColumnLabel();}
				}catch(Exception ex){
					
				}
				return ""; //$NON-NLS-1$
			}
		});
	
		
		
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentOsmMap c = (ComponentOsmMap)getComponentDefinition();
				if (c == null){return;} //$NON-NLS-1$
				
				if(element instanceof Property && ((Property) element).getParent() instanceof PropertyOsmSerie) {
					((PropertyOsmSerie) ((Property) element).getParent()).setPropertyValue(element, value);
				}
				
				if(element instanceof Property && ((Property) element).getParent() instanceof PropertyMapColor) {
					((PropertyMapColor) ((Property) element).getParent()).setValue((Property) element, value);
				}
				
				VanillaMapData datas = (VanillaMapData)c.getDatas();
				if (element == dataset){datas.setDataset((DataSet)value); fieldCbo.setInput(((DataSet)value).getDataSetDescriptor().getColumnsDescriptors());
				try {
					refreshParameters();
				} catch (Exception e) {
					e.printStackTrace();
				}}
				if (element == datasource){dataSetCbo.setInput(getComponentDefinition().getDictionary().getDatasets());}
				if (element == mapVanilla){
					MapVanilla map = (MapVanilla)((IStructuredSelection)((ComboBoxViewerCellEditor)getCellEditor(element)).getViewer().getSelection()).getFirstElement();			
					if(!(datas.getMapId() != null && datas.getMapId().intValue() == map.getId())) {
						datas.getSeries().clear();
					}
					else {
						datas.setMap(map);
						datas.getSeries().clear();
						//create the series
						for(MapDataSet ds : map.getDataSetList()) {
							VanillaMapDataSerie serie = new VanillaMapDataSerie();
							serie.setType(ds.getType());
							serie.setName(ds.getName());
							serie.setMapDataset(ds);
							serie.setMaxMarkerSize(ds.getMarkerSizeMax());
							serie.setMinMarkerSize(ds.getMarkerSizeMin());
							datas.addSerie(serie);
						}
					}
				}
				if (element == zoneId){datas.setZoneFieldIndex(((ComboBoxViewerCellEditor)getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1 );}
				if (element == valueFile){datas.setValueFieldIndex(((ComboBoxViewerCellEditor)getCellEditor(element)).getViewer().getCCombo().getSelectionIndex()+ 1 );}
				notifyChangeOccured();
				resize();
			}
			
			
			
			@Override
			protected Object getValue(Object element) {
				ComponentOsmMap c = (ComponentOsmMap)getComponentDefinition();
				if (c == null){return null;}
				
				if(element instanceof Property && ((Property) element).getParent() instanceof PropertyOsmSerie) {
					return ((PropertyOsmSerie) ((Property) element).getParent()).getPropertyValue(element);
				}
				
				if(element instanceof Property && ((Property) element).getParent() instanceof PropertyMapColor) {
					return ((PropertyMapColor) ((Property) element).getParent()).getValue((Property) element);
				}
				
				VanillaMapData datas = (VanillaMapData)c.getDatas();
				try{
					if (element == dataset){return datas.getDataSet();}
					if (element == datasource){return c.getDictionary().getDatasource(datas.getDataSet().getDataSourceName());}
					if (element == zoneId){((List)fieldCbo.getViewer().getInput()).get(datas.getZoneFieldIndex());}
					if (element == valueFile){((List)fieldCbo.getViewer().getInput()).get(datas.getValueFieldIndex());}
					if (element == mapVanilla){return datas.getMap();}

				}catch(Exception ex){}
				
				return null;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return element instanceof Property && ((Property)element).getCellEditor() != null;
			}
			@Override
			protected void initializeCellEditorValue(CellEditor cellEditor, ViewerCell cell) {
			
				super.initializeCellEditorValue(cellEditor, cell);
			}
		});
		
		List input = new ArrayList();
		input.add(datasource);
		input.add(dataset);
		input.add(zoneId);
		input.add(valueFile);
		input.add(mapVanilla);
		
		viewer.setInput(input);
		createColorMenu();
	}
	
	private void createColorMenu(){
		MenuManager mgr = new MenuManager();
		final Action addRange = new Action(Messages.MapEditor_25){
			public void run(){
				ColorRange range = new ColorRange(Messages.MapEditor_26, "ffffff", 0, Integer.MAX_VALUE); //$NON-NLS-2$
				
				Object sel = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				((PropertyOsmSerie)sel).getSerie().addMarkerColor(range);
				
				
				((PropertyOsmSerie)sel).addColor(range);
				
				viewer.refresh();
			}
		};
		
		final Action removeRange = new Action(Messages.MapEditor_28){
			public void run(){
				PropertyMapColor range = (PropertyMapColor)((IStructuredSelection)viewer.getSelection()).getFirstElement();
				Object sel = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				((PropertyOsmSerie)sel).getSerie().removeMarkerColor(range.getRange());
				((PropertyOsmSerie)sel).removeColor(range);
				viewer.refresh();
			}
		};
		mgr.add(addRange);mgr.add(removeRange);
		mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				
				Object sel = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				if(!(sel instanceof PropertyMapColor)) {
					removeRange.setEnabled(false);
				}
				else {
					removeRange.setEnabled(true);
				}
				if(!(sel instanceof PropertyOsmSerie)) {
					addRange.setEnabled(false);
				}
				else if(((PropertyOsmSerie)sel).getSerie().getType().equals(VanillaMapDataSerie.MARKER)){
					addRange.setEnabled(true);
				}
				
				
				
			}
		});
	
		viewer.getTree().setMenu(mgr.createContextMenu(viewer.getTree()));
	}

	@Override
	public void setInput(EditPart editPart, ComponentConfig configuration, IComponentDefinition component) {
		//Trick to remove the images from the colorEditor
		try {
			for(CellEditor editor : PropertyColor.colorPickers) {
				editor.dispose();
			}
			PropertyColor.colorPickers.clear();
		} catch(Exception e) {

		}
		dataSourceCbo.setInput(component.getDictionary().getDatasources());
		dataSetCbo.setInput(component.getDictionary().getDatasets());

		if(component.getDatas() != null && component.getDatas().getDataSet() != null) {
			dataSourceCbo.setValue(component.getDictionary().getDatasource(component.getDatas().getDataSet().getDataSourceName()));
			fieldCbo.setInput(component.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors());
		}
		else {
			dataSetCbo.setInput(Collections.EMPTY_LIST);
		}
		
		IVanillaContext ctx = Activator.getDefault().getRepositoryContext().getVanillaContext();
		RemoteMapDefinitionService remoteMap = new RemoteMapDefinitionService();
		remoteMap.configure(ctx.getVanillaUrl());
		
		try {
			List<MapVanilla> maps = remoteMap.getAllMapsVanilla();
			vanillaMapCombo.setInput(maps);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		this.def = (ComponentOsmMap)component;
		super.setInput(editPart, configuration, component);
	}
	
	private void expandTree(Property p) {
		viewer.expandToLevel(p, AbstractTreeViewer.ALL_LEVELS);
		if(p instanceof PropertyGroup) {
			for(Property c : ((PropertyGroup) p).getProperties()) {
				expandTree(c);
			}
		}

	}
}
