package bpm.fd.design.ui.properties.model.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.maps.MapDatas;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentMapWMS;
import bpm.fd.api.core.model.components.definition.maps.openlayers.MapWMSOptions;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGroup;
import bpm.fd.design.ui.properties.model.map.PropertyMapColor;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class MapWmsEditor extends ComponentEditor{

	private ComboBoxViewerCellEditor dataSourceCbo;
	private ComboBoxViewerCellEditor dataSetCbo;
	private ComboBoxViewerCellEditor fieldCbo;	
	private TreeViewer colorViewer;
	
	public MapWmsEditor(Composite parent) {
		super(parent);
		fillBar();
	}
	
	public void setInput(EditPart editPart, ComponentConfig configuration, IComponentDefinition component){
		super.setInput(editPart, configuration, component);
		dataSourceCbo.setInput(component.getDictionary().getDatasources());
		dataSetCbo.setInput(component.getDictionary().getDatasets());
		if(component.getDatas().getDataSet() != null){
			dataSourceCbo.setValue(component.getDictionary().getDatasource(component.getDatas().getDataSet().getDataSourceName()));
			fieldCbo.setInput(component.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors());
		}
		else{
			dataSetCbo.setInput(Collections.EMPTY_LIST);
		}
		
		List colors = new ArrayList();
		for(ColorRange r : ((ComponentMapWMS)component).getColorRanges()){
			colors.add(new PropertyMapColor(r, colorViewer.getTree()));
		}
		colorViewer.setInput(colors);
	}

	@Override
	protected void fillBar() {
		createMap(getControl());
		createDatas(getControl());
		createColors(getControl());
		createParameters();
	}
	
	private void createMap(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.MapWmsEditor_0);
		
		final Property width = new Property(Messages.MapWmsEditor_1, new TextCellEditor(viewer.getTree()));
		final Property height = new Property(Messages.MapWmsEditor_2, new TextCellEditor(viewer.getTree()));
			
		final Property baseLayerUrl = new Property(Messages.MapWmsEditor_3, new TextCellEditor(viewer.getTree()));
		final Property baseLayerName = new Property(Messages.MapWmsEditor_4, new TextCellEditor(viewer.getTree()));
		
		final Property vectorLayerUrl = new Property(Messages.MapWmsEditor_5, new TextCellEditor(viewer.getTree()));
		final Property vectorLayerName = new Property(Messages.MapWmsEditor_6, new TextCellEditor(viewer.getTree()));
		
		final Property bounds = new Property(Messages.MapWmsEditor_7, new TextCellEditor(viewer.getTree()));
		final Property tileOrigin = new Property(Messages.MapWmsEditor_10, new TextCellEditor(viewer.getTree()));
		final Property projection = new Property(Messages.MapWmsEditor_11, new TextCellEditor(viewer.getTree()));
		final Property opacity = new Property("Opacity", new TextCellEditor(viewer.getTree()));
		
		final Property baseLayerType = new Property("Base layer type", new TextCellEditor(viewer.getTree()));
		final Property vectorLayerType = new Property("Vector layer type", new TextCellEditor(viewer.getTree()));
		final Property vectorBaseFile = new Property("Vector base file", new TextCellEditor(viewer.getTree()));
		
		PropertyGroup size = new PropertyGroup(Messages.MapWmsEditor_8);
		size.add(width);
		size.add(height);
		
		PropertyGroup viewpoint = new PropertyGroup(Messages.MapWmsEditor_9);
		viewpoint.add(baseLayerType);
		viewpoint.add(baseLayerUrl);
		viewpoint.add(baseLayerName);
		viewpoint.add(vectorLayerType);
		viewpoint.add(vectorLayerUrl);
		viewpoint.add(vectorLayerName);
		viewpoint.add(vectorBaseFile);
		viewpoint.add(bounds);
		viewpoint.add(tileOrigin);
		viewpoint.add(projection);
		viewpoint.add(opacity);
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentMapWMS c = (ComponentMapWMS)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				MapWMSOptions opts = (MapWMSOptions)c.getOptions(MapWMSOptions.class); 
				if (element == width){return c.getWidth() + "";} //$NON-NLS-1$
				if (element == height){return c.getHeight() + "";} //$NON-NLS-1$
				if (element == baseLayerUrl){return opts.getBaseLayerUrl();} //$NON-NLS-1$
				if (element == baseLayerName){return opts.getBaseLayerName();} //$NON-NLS-1$
				if (element == vectorLayerUrl){return opts.getVectorLayerUrl();} //$NON-NLS-1$
				if (element == vectorLayerName){return opts.getVectorLayerName();} //$NON-NLS-1$
				if (element == bounds){return opts.getBounds();} //$NON-NLS-1$
				if (element == tileOrigin){return opts.getTileOrigin();} //$NON-NLS-1$
				if (element == projection){return opts.getProjection();} //$NON-NLS-1$
				if (element == opacity){return opts.getOpacity();} //$NON-NLS-1$
				if (element == baseLayerType){return opts.getBaseLayerType();} //$NON-NLS-1$
				if (element == vectorLayerType){return opts.getVectorLayerType();} //$NON-NLS-1$
				if (element == vectorBaseFile){return opts.getBaseVectorGeometry();} //$NON-NLS-1$
				return ""; //$NON-NLS-1$
			}
		});
		
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentMapWMS c = (ComponentMapWMS)getComponentDefinition();
				if (c == null){return ;}
				MapWMSOptions opts = (MapWMSOptions)c.getOptions(MapWMSOptions.class);
				try{
					if (element == width){c.setWidth(Integer.valueOf((String)value));}
					if (element == height){c.setHeight(Integer.valueOf((String)value));}
					if (element == baseLayerUrl){opts.setBaseLayerUrl((String) value);} //$NON-NLS-1$
					if (element == baseLayerName){opts.setBaseLayerName((String) value);} //$NON-NLS-1$
					if (element == vectorLayerUrl){opts.setVectorLayerUrl((String) value);} //$NON-NLS-1$
					if (element == vectorLayerName){opts.setVectorLayerName((String) value);} //$NON-NLS-1$
					if (element == bounds){opts.setBounds((String) value);} //$NON-NLS-1$
					if (element == tileOrigin){opts.setTileOrigin((String) value);} //$NON-NLS-1$
					if (element == projection){opts.setProjection((String) value);} //$NON-NLS-1$
					if (element == opacity){opts.setOpacity((String) value);} //$NON-NLS-1$
					if (element == baseLayerType){opts.setBaseLayerType((String) value);} //$NON-NLS-1$
					if (element == vectorLayerType){opts.setVectorLayerType((String) value);} //$NON-NLS-1$
					if (element == vectorBaseFile){opts.setBaseVectorGeometry((String) value);} //$NON-NLS-1$
				}catch(Exception ex){}
				
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentMapWMS c = (ComponentMapWMS)getComponentDefinition();
				if (c == null){return null;}
				MapWMSOptions opts = (MapWMSOptions)c.getOptions(MapWMSOptions.class); 
				if (element == width){return c.getWidth() ;}
				if (element == height){return c.getHeight();}
				if (element == baseLayerUrl){return opts.getBaseLayerUrl();} //$NON-NLS-1$
				if (element == baseLayerName){return opts.getBaseLayerName();} //$NON-NLS-1$
				if (element == vectorLayerUrl){return opts.getVectorLayerUrl();} //$NON-NLS-1$
				if (element == vectorLayerName){return opts.getVectorLayerName();} //$NON-NLS-1$
				if (element == bounds){return opts.getBounds();} //$NON-NLS-1$
				if (element == tileOrigin){return opts.getTileOrigin();} //$NON-NLS-1$
				if (element == projection){return opts.getProjection();} //$NON-NLS-1$
				if (element == opacity){return opts.getOpacity();} //$NON-NLS-1$
				if (element == baseLayerType){return opts.getBaseLayerType();} //$NON-NLS-1$
				if (element == vectorLayerType){return opts.getVectorLayerType();} //$NON-NLS-1$
				if (element == vectorBaseFile){return opts.getBaseVectorGeometry();} //$NON-NLS-1$
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
		input.add(size);
		viewer.setInput(input);
	}
	
	private void createDatas(ExpandBar parent){
		TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
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
		

		fieldCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		fieldCbo.setContenProvider(new ArrayContentProvider());
		fieldCbo.setLabelProvider(new DatasLabelProvider());
		
		final Property datasource = new Property(Messages.MapEditor_17, dataSourceCbo);
		final Property dataset = new Property(Messages.MapEditor_18, dataSetCbo);
		final Property zoneId = new Property(Messages.MapEditor_19, fieldCbo);
		final Property valueFile = new Property(Messages.MapEditor_20, fieldCbo);
		final Property latitudeField = new Property("Latitude field", fieldCbo);
		final Property longitudeField = new Property("Longitude field", fieldCbo);
		final Property imgPathField = new Property("Image url field", fieldCbo);
		final Property imgSizeField = new Property("Image size field", fieldCbo);
		
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentMapWMS c = (ComponentMapWMS)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				MapDatas datas = (MapDatas)c.getDatas();
				if (element == datasource){return dataSourceCbo.getViewer().getCCombo().getText();}
				if (element == dataset){if (getComponentDefinition().getDatas().getDataSet() == null){return "";}return getComponentDefinition().getDatas().getDataSet().getName();} //$NON-NLS-1$

				try{
					if (element == zoneId){return datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(datas.getZoneIdFieldIndex() - 1).getColumnLabel();}
					if (element == valueFile){return datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(datas.getValueFieldIndex() - 1).getColumnLabel();}
					if (element == latitudeField){return datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(datas.getLatitudeIndex() - 1).getColumnLabel();}
					if (element == longitudeField){return datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(datas.getLongitudeIndex() - 1).getColumnLabel();}
					if (element == imgPathField){return datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(datas.getImgPathIndex() - 1).getColumnLabel();}
					if (element == imgSizeField){return datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(datas.getImgSizeIndex() - 1).getColumnLabel();}
				}catch(Exception ex){
					
				}
				return ""; //$NON-NLS-1$
			}
		});
	
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentMapWMS c = (ComponentMapWMS)getComponentDefinition();
				if (c == null){return;}
				MapDatas datas = (MapDatas)c.getDatas();
				if (element == dataset){datas.setDataSet((DataSet)value); fieldCbo.setInput(((DataSet)value).getDataSetDescriptor().getColumnsDescriptors());
				try {
					refreshParameters();
				} catch (Exception e) {
					e.printStackTrace();
				}}
				if (element == datasource){dataSetCbo.setInput(getComponentDefinition().getDictionary().getDatasets());}

				if (element == zoneId){datas.setZoneIdFieldIndex(((ComboBoxViewerCellEditor)getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1 );}
				if (element == valueFile){datas.setValueFieldIndex(((ComboBoxViewerCellEditor)getCellEditor(element)).getViewer().getCCombo().getSelectionIndex()+ 1 );}
				if (element == latitudeField){datas.setLatitudeIndex(((ComboBoxViewerCellEditor)getCellEditor(element)).getViewer().getCCombo().getSelectionIndex()+ 1 );}
				if (element == longitudeField){datas.setLongitudeIndex(((ComboBoxViewerCellEditor)getCellEditor(element)).getViewer().getCCombo().getSelectionIndex()+ 1 );}
				if (element == imgPathField){datas.setImgPathIndex(((ComboBoxViewerCellEditor)getCellEditor(element)).getViewer().getCCombo().getSelectionIndex()+ 1 );}
				if (element == imgSizeField){datas.setImgSizeIndex(((ComboBoxViewerCellEditor)getCellEditor(element)).getViewer().getCCombo().getSelectionIndex()+ 1 );}
				notifyChangeOccured();
				resize();
			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentMapWMS c = (ComponentMapWMS)getComponentDefinition();
				if (c == null){return null;}
				MapDatas datas = (MapDatas)c.getDatas();
				try{
					if (element == dataset){return datas.getDataSet();}
					if (element == datasource){return c.getDictionary().getDatasource(datas.getDataSet().getDataSourceName());}
					if (element == zoneId){((List)fieldCbo.getViewer().getInput()).get(datas.getZoneIdFieldIndex());}
					if (element == valueFile){((List)fieldCbo.getViewer().getInput()).get(datas.getValueFieldIndex());}
					if (element == latitudeField){((List)fieldCbo.getViewer().getInput()).get(datas.getLatitudeIndex());}
					if (element == longitudeField){((List)fieldCbo.getViewer().getInput()).get(datas.getLongitudeIndex());}
					if (element == imgPathField){((List)fieldCbo.getViewer().getInput()).get(datas.getImgPathIndex());}
					if (element == imgSizeField){((List)fieldCbo.getViewer().getInput()).get(datas.getImgSizeIndex());}

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
		});
		
		List input = new ArrayList();
		input.add(datasource);input.add(dataset);
		input.add(zoneId);input.add(valueFile);
		input.add(latitudeField);
		input.add(longitudeField);
		input.add(imgPathField);
		input.add(imgSizeField);
		
		viewer.setInput(input);

	}
	
	protected void createColors(ExpandBar parent){
		colorViewer = createViewer(parent);
		TreeViewerColumn valueCol = createValueViewerColum(colorViewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(colorViewer.getTree());
		item.setText(Messages.MapEditor_24);

		
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof PropertyMapColor){
					return ((PropertyMapColor)element).getRange().getName();
				}
				PropertyMapColor parent = (PropertyMapColor)((Property)element).getParent();
				return parent.getValueString((Property)element);
			}
			@Override
			public org.eclipse.swt.graphics.Color getBackground(Object element) {
				if (element instanceof PropertyMapColor){
					return null;
				}
				PropertyMapColor parent = (PropertyMapColor)((Property)element).getParent();
				
				return parent.getColor((Property)element);
			}
		});
	
		valueCol.setEditingSupport(new EditingSupport(colorViewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (element instanceof PropertyMapColor){
					((PropertyMapColor)element).setRangeName((String)value);
				}
				else{
					PropertyMapColor parent = (PropertyMapColor)((Property)element).getParent();
					parent.setValue((Property)element, value);
				}
				notifyChangeOccured();
				colorViewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				if (element instanceof PropertyMapColor){
					return ((PropertyMapColor)element).getRange().getName();
				}
				else{
					PropertyMapColor parent = (PropertyMapColor)((Property)element).getParent();
					return parent.getValue((Property)element);
				}
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property)element).getCellEditor() ;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return ((Property)element).getCellEditor() != null;
			}
		});
	
	
		createColorMenu();
	}
	
	
	private void createColorMenu(){
		MenuManager mgr = new MenuManager();
		Action addRange = new Action(Messages.MapEditor_25){
			public void run(){
				ColorRange range = new ColorRange(Messages.MapEditor_26, "ffffff", 0, Integer.MAX_VALUE); //$NON-NLS-2$
				((ComponentMapWMS)getComponentDefinition()).addColorRange(range);
				((List)colorViewer.getInput()).add(new PropertyMapColor(range, colorViewer.getTree()));
				colorViewer.refresh();
			}
		};
		
		final Action removeRange = new Action(Messages.MapEditor_28){
			public void run(){
				PropertyMapColor range = (PropertyMapColor)((IStructuredSelection)colorViewer.getSelection()).getFirstElement();
				((ComponentMapWMS)getComponentDefinition()).removeColorRange(range.getRange());
				((List)colorViewer.getInput()).remove(range);
				colorViewer.refresh();
			}
		};
		mgr.add(addRange);mgr.add(removeRange);
		mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (colorViewer.getSelection().isEmpty()){
					removeRange.setEnabled(false);return;
				}
				if (! (((IStructuredSelection)colorViewer.getSelection()).getFirstElement() instanceof PropertyMapColor)){
					removeRange.setEnabled(false);return;
				}
				removeRange.setEnabled(true);
				
				
			}
		});
	
		colorViewer.getTree().setMenu(mgr.createContextMenu(colorViewer.getTree()));
	}

}
