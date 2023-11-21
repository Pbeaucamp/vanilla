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
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.ui.internal.preferences.PropertyListenerList;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.slicer.SlicerData;
import bpm.fd.api.core.model.components.definition.slicer.SlicerOptions;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGroup;
import bpm.fd.design.ui.properties.model.PropertyLevel;
import bpm.fd.design.ui.properties.model.chart.FilterProperty;
import bpm.fd.design.ui.properties.model.chart.SerieProperty;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;
import bpm.fd.design.ui.tools.ColorManager;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;

public class SlicerEditor extends ComponentEditor{
//	private static final String noLevel = "--- NONE ---";
	private ComboBoxViewerCellEditor fieldCbo;
	private ComboBoxViewerCellEditor dataSourceCbo;
	private ComboBoxViewerCellEditor dataSetCbo;
	

	
	private PropertyGroup dimension;
	
	public SlicerEditor(Composite parent) {
		super(parent);
		fillBar();
	}
	protected void fillBar(){
		createSlicer(getControl());
		createParameters();
		createDatas(getControl());
		createDimension(getControl());
	}

	public void setInput(EditPart editPart, ComponentConfig config, IComponentDefinition component){
		dimension.clear();
		
		for(int i = 0; i < ((SlicerData)component.getDatas()).getLevelCategoriesIndex().size(); i++){
			dimension.add(new PropertyLevel(((SlicerData)component.getDatas()).getLevelCategoriesIndex().get(i), Messages.SlicerEditor_0 + (i+1), fieldCbo));
		}
		
		
		
		super.setInput(editPart, config, component);
		dataSourceCbo.setInput(component.getDictionary().getDatasources());
		dataSetCbo.setInput(component.getDictionary().getDatasets());
		
		
		if(component.getDatas() != null && component.getDatas().getDataSet() != null){
			dataSourceCbo.setValue(component.getDictionary().getDatasource(component.getDatas().getDataSet().getDataSourceName()));
			fieldCbo.setInput(component.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors());
		}
		else{
			dataSetCbo.setInput(Collections.EMPTY_LIST);
		}
		
//		viewer.expandAll();
		
	}
	
	private void createDimension(ExpandBar parent){
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		final ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.SlicerEditor_1);
		
		fieldCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		fieldCbo.setContenProvider(new ArrayContentProvider());
		fieldCbo.setLabelProvider(new DatasLabelProvider());
		
		dimension = new PropertyGroup(Messages.SlicerEditor_2, new DialogCellEditor(viewer.getTree()) {
			
			@Override
			protected Object openDialogBox(Control cellEditorWindow) {
				Property p = new Property(Messages.SlicerEditor_3 + (dimension.getProperties().size() + 1), fieldCbo);
				dimension.add(p);
				((SlicerData)getComponentDefinition().getDatas()).addLevel();

				viewer.expandAll();
				viewer.refresh();
				resizeAllItems();
				return null;
			}
		});
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (getComponentDefinition() == null){return "";}; //$NON-NLS-1$
				if (element == dimension){return "";} //$NON-NLS-1$
				if (((Property)element).getParent() instanceof PropertyLevel){return ((PropertyLevel)((Property)element).getParent()).getLabelString((Property)element);}
				return ""; //$NON-NLS-1$
			}

		});
	
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (getComponentDefinition() == null){return;}
				if (((Property)element).getParent() instanceof PropertyLevel){((PropertyLevel)((Property)element).getParent()).setValue((Property)element, value);}
			
				notifyChangeOccured();
				viewer.refresh();
				viewer.expandAll();
				resizeItem(item);
			}
			
			@Override
			protected Object getValue(Object element) {
				if (getComponentDefinition() == null){return null;}
				if (((Property)element).getParent() instanceof PropertyLevel){return ((PropertyLevel)((Property)element).getParent()).getValue((Property)element);}
				
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
	
		
		MenuManager mgr = new MenuManager();
		final Action removeLevel = new Action(Messages.SlicerEditor_7){
			public void run(){
				Property p = (Property)((IStructuredSelection)viewer.getSelection()).getFirstElement();
				int index = ((PropertyGroup)p.getParent()).getProperties().indexOf(p);
				((SlicerData)getComponentDefinition().getDatas()).removeLevel(index);
				notifyChangeOccured();
				//refresh the viewer's model
				dimension.clear();
				for(int i = 0; i < ((SlicerData)getComponentDefinition().getDatas()).getLevelCategoriesIndex().size(); i++){
					dimension.add(new Property(Messages.SlicerEditor_8 + (i+1), fieldCbo));
				}
				viewer.refresh();
				viewer.expandAll();
				resizeItem(item);
			}
		};
		mgr.add(removeLevel);
		mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				if (ss.isEmpty() || ss.getFirstElement() instanceof PropertyGroup){
					removeLevel.setEnabled(false);
				}
				else{
					removeLevel.setEnabled(true);
				}
				
			}
		});
		
		viewer.getTree().setMenu(mgr.createContextMenu(viewer.getControl()));
		List<PropertyGroup> input = new ArrayList<PropertyGroup>();
		input.add(dimension);
		viewer.setInput(input);
	}
	
	
	
	private void createDatas(ExpandBar parent){
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		final ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.SlicerEditor_9);
		
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
		
		final Property datasource = new Property(Messages.SlicerEditor_10, dataSourceCbo);
		final Property dataset = new Property(Messages.SlicerEditor_11, dataSetCbo);
		
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (getComponentDefinition() == null){return "";}; //$NON-NLS-1$
				if (element instanceof Property && ((Property)element).getParent() instanceof SerieProperty){
					return ((SerieProperty)((Property)element).getParent()).getPropertyValueString(element);
				}
				if (element instanceof FilterProperty){
					return ((FilterProperty)element).getValue();
				}
				
				if (element == datasource){return dataSourceCbo.getViewer().getCCombo().getText();}
				
				try{
					if (element == dataset){if (getComponentDefinition().getDatas().getDataSet() == null){return "";}return getComponentDefinition().getDatas().getDataSet().getName();} //$NON-NLS-1$
				}catch(Exception ex){
					
				}
				
				return ""; //$NON-NLS-1$
			}

		});
	
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (getComponentDefinition() == null){return;}
				SlicerData data = ((SlicerData)getComponentDefinition().getDatas());
				
				if (element == datasource){dataSetCbo.setInput(getComponentDefinition().getDictionary().getDatasets());}

			
				try{
					if (element == dataset){data.setDataSet((DataSet)value); fieldCbo.setInput(((DataSet)value).getDataSetDescriptor().getColumnsDescriptors());refreshParameters();}
					
				}catch(Exception ex){}
				notifyChangeOccured();
				viewer.refresh();
				viewer.expandAll();
				resizeItem(item);
			}
			
			@Override
			protected Object getValue(Object element) {
				if (getComponentDefinition() == null){return null;}
				try{
					if (element == dataset){return getComponentDefinition().getDatas().getDataSet();}
					if (element == datasource){return getComponentDefinition().getDictionary().getDatasource(getComponentDefinition().getDatas().getDataSet().getDataSourceName());}
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
		viewer.setInput(input);
	}
	
	
	
	private void createSlicer(ExpandBar parent){
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);

		final ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.SlicerEditor_15);
				
		final Property submit = new Property(Messages.SlicerEditor_16, new CheckboxCellEditor(viewer.getTree()));
		final Property linkedLevels = new Property(Messages.SlicerEditor_17, new CheckboxCellEditor(viewer.getTree()));
		final Property defaultColor = new Property(Messages.SlicerEditor_18, new ColorCellEditor(viewer.getTree()));
		final Property activeColor = new Property(Messages.SlicerEditor_19, new ColorCellEditor(viewer.getTree()));
		
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (getComponentDefinition() == null){return "";}; //$NON-NLS-1$
				SlicerOptions opts =(SlicerOptions)getComponentDefinition().getOptions(SlicerOptions.class);
				if (element == submit){return opts.isSubmitOnCheck() + "" ;} //$NON-NLS-1$
				if (element == linkedLevels){return opts.isLevelLinked() + "" ;} //$NON-NLS-1$
				
				return ""; //$NON-NLS-1$
			}

			@Override
			public org.eclipse.swt.graphics.Color getBackground(Object element) {
				if (getComponentDefinition() == null){return null;}
				SlicerOptions opts =(SlicerOptions)getComponentDefinition().getOptions(SlicerOptions.class);
				Color c = null;
				if (element == defaultColor){c = opts.getDefaultColor();}
				if (element == activeColor){c = opts.getActiveColor();}
				if (c != null){
					/*
					 * convert color into colorCode
					 */
					StringBuffer buf = new StringBuffer("#"); //$NON-NLS-1$
					String s = ""; //$NON-NLS-1$
					s = Integer.toHexString(c.getRed());
					if (s.length() < 2){
						s = "0" + s; //$NON-NLS-1$
					}
					buf.append(s);
					s = Integer.toHexString(c.getGreen());
					if (s.length() < 2){
						s = "0" + s; //$NON-NLS-1$
					}
					buf.append(s);
					s = Integer.toHexString(c.getBlue());
					if (s.length() < 2){
						s = "0" + s; //$NON-NLS-1$
					}
					buf.append(s);
					
					org.eclipse.swt.graphics.Color col  = ColorManager.getColorRegistry().get(buf.toString());
					if (col == null){
						ColorManager.getColorRegistry().put(buf.toString(), new RGB(c.getRed(), c.getGreen(), c.getBlue()));
						col  = ColorManager.getColorRegistry().get(buf.toString());
					}
					return col;
					
				}
				return null;
			}
		});
	
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (getComponentDefinition() == null){return;}
				SlicerOptions opts =(SlicerOptions)getComponentDefinition().getOptions(SlicerOptions.class);
				if (element == submit){opts.setSubmitOnCheck((Boolean)value);}
				if (element == linkedLevels){opts.setLevelLinked((Boolean)value);}
				if (element == defaultColor){opts.setDefaultColor(new Color(((RGB)value).red, ((RGB)value).green, ((RGB)value).blue));}
				if (element == activeColor){opts.setActiveColor(new Color(((RGB)value).red, ((RGB)value).green, ((RGB)value).blue));}

			
				
				notifyChangeOccured();
				viewer.refresh();
				viewer.expandAll();
				resizeItem(item);
			}
			
			@Override
			protected Object getValue(Object element) {
				if (getComponentDefinition() == null){return null;}
				SlicerOptions opts =(SlicerOptions)getComponentDefinition().getOptions(SlicerOptions.class);
				if (element == submit){return opts.isSubmitOnCheck();}
				if (element == linkedLevels){return opts.isLevelLinked();}
				Color c = null;
				if (element == defaultColor){c = opts.getDefaultColor();}
				if (element == activeColor){c = opts.getActiveColor();}
				if (c != null){
					return new RGB(c.getRed(), c.getGreen(), c.getBlue());
				}

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
		
		List<Property> input = new ArrayList<Property>();
		input.add(submit);input.add(linkedLevels);
		input.add(defaultColor);input.add(activeColor);
		viewer.setInput(input);
	}
	
	
}
