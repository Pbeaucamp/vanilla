package bpm.fd.design.ui.properties.model.editors;

import java.awt.Color;
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
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.maps.ComponentMap;
import bpm.fd.api.core.model.components.definition.maps.MapDatas;
import bpm.fd.api.core.model.components.definition.maps.MapDrillInfo;
import bpm.fd.api.core.model.components.definition.maps.MapInfo;
import bpm.fd.api.core.model.components.definition.maps.MapDrillInfo.TargetType;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGroup;
import bpm.fd.design.ui.properties.model.map.PropertyMapColor;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;
import bpm.fd.design.ui.tools.ColorManager;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;

public class MapEditor extends ComponentEditor implements IComponentEditor{
	
	private ComboBoxViewerCellEditor dataSourceCbo;
	private ComboBoxViewerCellEditor dataSetCbo;
	private ComboBoxViewerCellEditor fieldCbo;	
	
	private ComboBoxViewerCellEditor modelPageCbo;
	private ComboBoxViewerCellEditor mapCbo;
	
	private TreeViewer colorViewer;
	
	protected void createDrillDown(ExpandBar parent){
		final TreeViewer viewer = createViewer(parent);
		TreeViewerColumn valueCol = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.MapEditor_0);
		
		final Property enable = new Property(Messages.MapEditor_1, new CheckboxCellEditor(viewer.getTree()));
		final Property zoneParam = new Property(Messages.MapEditor_2, new CheckboxCellEditor(viewer.getTree()));
		
		modelPageCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		modelPageCbo.setContenProvider(new ArrayContentProvider());
		modelPageCbo.setLabelProvider(new DatasLabelProvider());
		
		final PropertyGroup targetPage = new PropertyGroup(Messages.MapEditor_3, new ComboBoxCellEditor(viewer.getTree(), new String[]{TargetType.PopupPage.name(), TargetType.Parameter.name(), TargetType.FolderPage.name()}, SWT.READ_ONLY));
		final Property targetModel = new Property(Messages.MapEditor_4, modelPageCbo);
		targetPage.add(targetModel);
		
		PropertyGroup popup = new PropertyGroup(Messages.MapEditor_5);
		final Property width = new Property(Messages.MapEditor_6, new TextCellEditor(viewer.getTree()));
		final Property height = new Property(Messages.MapEditor_7, new TextCellEditor(viewer.getTree()));
		popup.add(width);
		popup.add(height);
		
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentMap c = (ComponentMap)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				MapDrillInfo drill = c.getDrillInfo();
				if (element == enable){	return drill.isDrillable() + "";} //$NON-NLS-1$
				if (element == zoneParam){	return drill.isSendCategory()+ "";} //$NON-NLS-1$
				if (element == targetPage){	return drill.getTarget().name();}
				if (element == targetModel){
					switch(drill.getTarget()){
					case PopupPage:
						if (drill.getModelPage() != null){return drill.getModelPage().getName();}
						break;
					case FolderPage:
						if (drill.getFolderPageName() != null){return drill.getFolderPageName();}
						break;
						
					}
					
				}
				if (element == width){ return drill.getWidth() + "";} //$NON-NLS-1$
				if (element == height){ return drill.getHeight() + "";} //$NON-NLS-1$
				return ""; //$NON-NLS-1$
			}
		});
		valueCol.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentMap c = (ComponentMap)getComponentDefinition();
				if (c == null){return;}
				MapDrillInfo drill = c.getDrillInfo();
				if (element == enable){	drill.setDrillable((Boolean)value);}
				if (element == zoneParam){	drill.setSendCategory((Boolean)value);}
				if (element == targetPage){	drill.setTarget(TargetType.values()[(Integer)value]);}
				if (element == targetModel){
					if (drill.getTarget() == TargetType.PopupPage){
						drill.setModelPage((FdModel)value);
					}
					else if (drill.getTarget() == TargetType.FolderPage){
						drill.setFolderPageName(((IStructureElement)value).getName());
					}
				}
				try{
					if (element == width){ drill.setWidth(Integer.parseInt((String)value));}
					if (element == height){ drill.setHeight(Integer.parseInt((String)value));}
				}catch(Exception ex){}
				
				notifyChangeOccured();
				viewer.refresh();

			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentMap c = (ComponentMap)getComponentDefinition();
				if (c == null){return null;}
				MapDrillInfo drill = c.getDrillInfo();
				if (element == enable){	return drill.isDrillable();}
				if (element == zoneParam){	return drill.isSendCategory();}
				if (element == targetPage){	return drill.getTarget().ordinal() ;}
				if (element == width){ return drill.getWidth() + "";} //$NON-NLS-1$
				if (element == height){ return drill.getHeight() + "";} //$NON-NLS-1$
				if (element == targetModel){
					switch(drill.getTarget()){
					case FolderPage:
						if (drill.getModelPage() != null){return drill.getModelPage();}
						break;
					case PopupPage:
						if (drill.getFolderPageName() != null){return drill.getFolderPageName();}
						break;
						
					}
					
				}
				return null;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				if (element == targetModel){
					ComponentMap c = (ComponentMap)getComponentDefinition();
					MapDrillInfo drill = c.getDrillInfo();
					switch(drill.getTarget()){
					case FolderPage:
						boolean filled = false;
						for(IBaseElement e : Activator.getDefault().getProject().getFdModel().getContent()){
							if (e instanceof Folder){
								modelPageCbo.setInput(((Folder)e).getContent());
								filled = true;
								break;
							}
						}
						if (!filled){
							modelPageCbo.setInput(Collections.EMPTY_LIST);
						}
						break;
					case PopupPage:
						if (Activator.getDefault().getProject() instanceof MultiPageFdProject){
							modelPageCbo.setInput(((MultiPageFdProject)Activator.getDefault().getProject()).getPagesModels());
						}
						else{
							modelPageCbo.setInput(Collections.EMPTY_LIST);
						}
						break;
					}
				}
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				if (getComponentDefinition() == null){return false;}
				return ((Property)element).getCellEditor() != null;
			}
		});
		
		
		
	
		List input = new ArrayList();
		input.add(enable);input.add(zoneParam);input.add(targetPage);input.add(popup);
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
		
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentMap c = (ComponentMap)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				MapDatas datas = (MapDatas)c.getDatas();
				if (element == datasource){return dataSourceCbo.getViewer().getCCombo().getText();}
				if (element == dataset){if (getComponentDefinition().getDatas().getDataSet() == null){return "";}return getComponentDefinition().getDatas().getDataSet().getName();} //$NON-NLS-1$

				try{
					if (element == zoneId){return datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(datas.getZoneIdFieldIndex() - 1).getColumnLabel();}
					if (element == valueFile){return datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(datas.getValueFieldIndex() - 1).getColumnLabel();}
				}catch(Exception ex){
					
				}
				return ""; //$NON-NLS-1$
			}
		});
	
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentMap c = (ComponentMap)getComponentDefinition();
				if (c == null){return;}
				MapDatas datas = (MapDatas)c.getDatas();
				if (element == dataset){datas.setDataSet((DataSet)value); fieldCbo.setInput(((DataSet)value).getDataSetDescriptor().getColumnsDescriptors());refreshParameters();}
				if (element == datasource){dataSetCbo.setInput(getComponentDefinition().getDictionary().getDatasets());}

				if (element == zoneId){datas.setZoneIdFieldIndex(((ComboBoxViewerCellEditor)getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1 );}
				if (element == valueFile){datas.setValueFieldIndex(((ComboBoxViewerCellEditor)getCellEditor(element)).getViewer().getCCombo().getSelectionIndex()+ 1 );}
				notifyChangeOccured();
				resize();
			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentMap c = (ComponentMap)getComponentDefinition();
				if (c == null){return null;}
				MapDatas datas = (MapDatas)c.getDatas();
				try{
					if (element == dataset){return datas.getDataSet();}
					if (element == datasource){return c.getDictionary().getDatasource(datas.getDataSet().getDataSourceName());}
					if (element == zoneId){((List)fieldCbo.getViewer().getInput()).get(datas.getZoneIdFieldIndex());}
					if (element == valueFile){((List)fieldCbo.getViewer().getInput()).get(datas.getValueFieldIndex());}

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
				((ComponentMap)getComponentDefinition()).addColorRange(range);
				((List)colorViewer.getInput()).add(new PropertyMapColor(range, colorViewer.getTree()));
				colorViewer.refresh();
			}
		};
		
		final Action removeRange = new Action(Messages.MapEditor_28){
			public void run(){
				PropertyMapColor range = (PropertyMapColor)((IStructuredSelection)colorViewer.getSelection()).getFirstElement();
				((ComponentMap)getComponentDefinition()).removeColorRange(range.getRange());
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
	
	protected void createMap(ExpandBar parent){
		final TreeViewer viewer = createViewer(parent);
		TreeViewerColumn valueCol = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.MapEditor_29);
		
		
		mapCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		mapCbo.setContenProvider(new ArrayContentProvider());
		mapCbo.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IMapDefinition)element).getLabel();
			}
		});
		
		final Property map = new Property(Messages.MapEditor_30, mapCbo);
		final Property showlabel = new Property(Messages.MapEditor_31, new CheckboxCellEditor(viewer.getTree()));
		
		
		
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentMap c = (ComponentMap)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				MapInfo info = (MapInfo)c.getMapInfo();

				if (element == map){	return info.getSwfFileName() + "";} //$NON-NLS-1$
				if (element == showlabel){	return c.isShowLabels()+ "";} //$NON-NLS-1$
				
				return ""; //$NON-NLS-1$
			}
		});
		valueCol.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentMap c = (ComponentMap)getComponentDefinition();
				if (c == null){return;}
				if (element == map){c.getMapInfo().updateValues(createMapInfo((IMapDefinition)value));}
				if (element == showlabel){	c.setShowLabels((Boolean)value);}
				notifyChangeOccured();
				viewer.refresh();

			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentMap c = (ComponentMap)getComponentDefinition();
				if (c == null){return null;}
				if (element == map){
					for(IMapDefinition def : (List<IMapDefinition>)mapCbo.getViewer().getInput()){
						if (def.getFusionMapObjectId() == ((MapInfo)c.getMapInfo()).getMapObjectId()){
							return def;
						}
					}
				;}
				if (element == showlabel){	return c.isShowLabels();}
				return null;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				if (getComponentDefinition() == null){return false;}
				return ((Property)element).getCellEditor() != null;
			}
		});
		
		
		
	
		List input = new ArrayList();
		input.add(map);input.add(showlabel);
		viewer.setInput(input);
	}
	public MapEditor(Composite parent) {
		super(parent);
		fillBar();
	}
	
	
	
	
	
	public static  org.eclipse.swt.graphics.Color getColor(Color col) {
		if (col == null){
			return null;
		}
		String c = "#"; //$NON-NLS-1$
		String t = Integer.toHexString(col.getRed());
		if (t.length() == 1){
			t = "0" + t; //$NON-NLS-1$
		}
		c = c + t;
		
		t = Integer.toHexString(col.getGreen());
		if (t.length() == 1){
			t = "0" + t; //$NON-NLS-1$
		}
		c = c + t;
		
		t = Integer.toHexString(col.getBlue()) ;
		if (t.length() == 1){
			t = "0" + t; //$NON-NLS-1$
		}
		c = c + t;
		
		
		if (ColorManager.getColorRegistry().get(c) == null){
			ColorManager.getColorRegistry().put(c, new RGB(col.getRed(), col.getGreen(), col.getBlue()));
		}
		return ColorManager.getColorRegistry().get(c);
	}
	
	public void setInput(EditPart editPart, ComponentConfig conf, IComponentDefinition component){
		super.setInput(editPart, conf, component);
		dataSourceCbo.setInput(component.getDictionary().getDatasources());
		dataSetCbo.setInput(component.getDictionary().getDatasets());
		if(component.getDatas().getDataSet() != null){
			dataSourceCbo.setValue(component.getDictionary().getDatasource(component.getDatas().getDataSet().getDataSourceName()));
			fieldCbo.setInput(component.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors());
		}
		else{
			dataSetCbo.setInput(Collections.EMPTY_LIST);
		}
		
		
		if (modelPageCbo != null){
			if (Activator.getDefault().getProject() instanceof MultiPageFdProject){
				modelPageCbo.setInput(((MultiPageFdProject)Activator.getDefault().getProject()).getPagesModels());
			}
			else{
				modelPageCbo.setInput(Collections.EMPTY_LIST);
			}
		}
		
		
		//gather maps
		try{
			
			RemoteMapDefinitionService reg = new RemoteMapDefinitionService();
			reg.setVanillaRuntimeUrl(bpm.fd.repository.ui.Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl());
			
			mapCbo.getViewer().setInput(reg.getAllMapDefinition());
		}catch(Exception ex){
			mapCbo.getViewer().setInput(Collections.EMPTY_LIST);
		}
		
		
		List colors = new ArrayList();
		for(ColorRange r : ((ComponentMap)component).getColorRanges()){
			colors.add(new PropertyMapColor(r, colorViewer.getTree()));
		}
		colorViewer.setInput(colors);
		
//		viewer.expandAll();
		
	}


	private static MapInfo createMapInfo(IMapDefinition mapDef) {
		MapInfo info = new MapInfo();
		info.setMapId(mapDef.getId());
		info.setMapType(mapDef.getMapType());
		try {
			info.setFusionMap(!mapDef.getFusionMapObject().getType().equals("Vanilla Map"));
		} catch(Exception e1) {
			e1.printStackTrace();
		}
			
		if(mapDef.getFusionMapObjectId() != null) {
			info.setMapObjectId(mapDef.getFusionMapObjectId());
			info.setSwfFileName(mapDef.getFusionMapObject().getSwfFileName());
		}
		
		else if(mapDef.getOpenLayersObjectId() != null) {
			info.setMapObjectId(mapDef.getOpenLayersObjectId());
		}
	
		return info; 
	}


	@Override
	protected void fillBar() {
		createDatas(getControl());
		createParameters();
		createMap(getControl());
		createDrillDown(getControl());
		createColors(getControl());
	}
}

