package bpm.fd.design.ui.properties.model.editors;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.NumberFormatOptions;
import bpm.fd.api.core.model.components.definition.gauge.ComplexGaugeDatas;
import bpm.fd.api.core.model.components.definition.gauge.ComponentGauge;
import bpm.fd.api.core.model.components.definition.gauge.GaugeOptions;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGaugeMaxima;
import bpm.fd.design.ui.properties.model.PropertyGroup;
import bpm.fd.design.ui.properties.model.chart.ChartEditor;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;

public class GaugeEditor extends ComponentEditor{
	private ComboBoxViewerCellEditor dataSourceCbo;
	private ComboBoxViewerCellEditor dataSetCbo;
	private ComboBoxViewerCellEditor fieldCbo;
	
	private PropertyGaugeMaxima min;
	private PropertyGaugeMaxima max;
	
	private PropertyGaugeMaxima tolerance;
	private PropertyGaugeMaxima target;
	
	private PropertyGroup manualmaxima;
	private PropertyGroup targetType ;
	public GaugeEditor(Composite parent) {
		super(parent);
		fillBar();
	}

	@Override
	protected void fillBar() {
		createDatas(getControl());
		createGauge(getControl());
//		createParameters();
		createNumberFormat(getControl());
	}
	private NumberFormatOptions getNumberFormatOptions(){
		return (NumberFormatOptions)getComponentDefinition().getOptions(NumberFormatOptions.class);
	}
	protected void createNumberFormat(ExpandBar parent){
		final TreeViewer viewer = createViewer(parent);
		TreeViewerColumn valueCol = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.GaugeEditor_0);
		
		final Property decSeparator = new Property(Messages.GaugeEditor_1, new TextCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_DECIMAL_SEPARATOR]);
		final Property thSeparator = new Property(Messages.GaugeEditor_2, new TextCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_THOUSAND_SEPARATOR]);
		final Property decNumber = new Property(Messages.GaugeEditor_3, new TextCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_DECIMALS]);
		final Property forceDecimal = new Property(Messages.GaugeEditor_4, new CheckboxCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_FORCE_DECIMALS]);
		final Property prefix = new Property(Messages.GaugeEditor_5, new TextCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_NUMBER_PREFIX]);
		final Property suffix = new Property(Messages.GaugeEditor_6, new TextCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_NUMBER_SUFFIX]);
		final Property applyformat = new Property(Messages.GaugeEditor_7, new CheckboxCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_FORMAT_NUMBER]);
		final Property applyScaleFormat = new Property(Messages.GaugeEditor_8, new CheckboxCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_FORMAT_NUMBER_SCALE]);
		List input = new ArrayList();
		input.add(applyformat);
		input.add(decSeparator);
		input.add(thSeparator);
		input.add(decNumber);
		input.add(forceDecimal);
		input.add(prefix);
		input.add(suffix);
		input.add(applyScaleFormat);
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if (getComponentDefinition() == null){return "";} //$NON-NLS-1$
				if (element == forceDecimal){
					return getNumberFormatOptions().isForceDecimal() + ""; //$NON-NLS-1$
				}
				if (element == applyScaleFormat){
					return "" + getNumberFormatOptions().isFormatNumberScale(); //$NON-NLS-1$
				}
				return "" + getNumberFormatOptions().getValue(((Property)element).getKeyProperty()); //$NON-NLS-1$
			}
		});
		
		valueCol.setEditingSupport(new EditingSupport(viewer) {
			@Override
			protected void setValue(Object element, Object value) {
				if (getComponentDefinition() == null){return;}
				NumberFormatOptions opts = getNumberFormatOptions();
				if (element == decSeparator){
					opts.setDecimalSeparator((String)value);
				}
				if (element == thSeparator){
					opts.setThousandSeparator((String)value);
				}
				if (element == decNumber){
					opts.setDecimals(Integer.valueOf((String)value));
				}
				if (element == forceDecimal){
					opts.setForceDecimal((Boolean)value);
				}
				if (element == prefix){
					opts.setNumberPrefix((String)value);
				}
				if (element == suffix){
					opts.setNumberSuffix((String)value);
				}
				if (element == applyformat){
					opts.setFormatNumber((Boolean)value);
				}
				if (element == applyScaleFormat){
					opts.setFormatNumberScale((Boolean)value);
				}
			
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				if (getComponentDefinition() == null){return "";} //$NON-NLS-1$
				NumberFormatOptions opts = getNumberFormatOptions();
				if (element == forceDecimal){
					return opts.isForceDecimal();
				}
				else if (element == applyformat){
					return opts.isFormatNumber();
				}
				else if (element == applyScaleFormat){
					return opts.isFormatNumberScale();
				}
				else{
					return opts.getValue(((Property)element).getKeyProperty());
				}
				
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return element instanceof Property;
			}
		});
		viewer.setInput(input);
	}
	
	
	
	
	private void createDatas(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.GaugeEditor_14);
		
		
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
		
		final Property datasource = new Property(Messages.GaugeEditor_15, dataSourceCbo);
		final Property dataset = new Property(Messages.GaugeEditor_16, dataSetCbo);
		final Property fieldvalue = new Property(Messages.GaugeEditor_17, fieldCbo);
		manualmaxima = new PropertyGroup(Messages.GaugeEditor_18, new CheckboxCellEditor(viewer.getTree()));
		targetType = new PropertyGroup(Messages.GaugeEditor_19, new CheckboxCellEditor(viewer.getTree()));
		final PropertyGroup expectation = new PropertyGroup(Messages.GaugeEditor_20, new CheckboxCellEditor(viewer.getTree()));
		
		
		final PropertyGroup targeted = new PropertyGroup(Messages.GaugeEditor_21, new CheckboxCellEditor(viewer.getTree()));
		targeted.add(targetType);
		
		final Property minExpec = new Property(Messages.GaugeEditor_22, fieldCbo);
		final Property maxExpec = new Property(Messages.GaugeEditor_23, fieldCbo);
		expectation.add(minExpec);
		expectation.add(maxExpec);
		
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentGauge c = (ComponentGauge)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				ComplexGaugeDatas datas = (ComplexGaugeDatas)c.getDatas();
				
				if (element instanceof PropertyGaugeMaxima){return ((PropertyGaugeMaxima)element).getPropertyValueString();}
				if (element == datasource){return dataSourceCbo.getViewer().getCCombo().getText();}
				if (element == dataset){if (getComponentDefinition().getDatas().getDataSet() == null){return "";}return getComponentDefinition().getDatas().getDataSet().getName();} //$NON-NLS-1$
				if (element == manualmaxima){return datas.isRangeManual() + "";} //$NON-NLS-1$
				if (element == expectation){return datas.isExpectedFieldsUsed() + "";} //$NON-NLS-1$
				if (element == targeted){return datas.isTargetNeeded() + "";} //$NON-NLS-1$
				if (element == targetType){return datas.isUseFieldForTarget()+ "";} //$NON-NLS-1$
				

				try{
					if (element == fieldvalue){return datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(datas.getIndexValue() - 1).getColumnLabel();}
					if (element == maxExpec){return datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(datas.getIndexMaxSeuil() - 1).getColumnLabel();}
					if (element == minExpec){return datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(datas.getIndexMinSeuil() - 1).getColumnLabel();}
				}catch(Exception ex){
					
				}
				return ""; //$NON-NLS-1$
			}
		});
	
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentGauge c = (ComponentGauge)getComponentDefinition();
				if (c == null){return;}
				ComplexGaugeDatas datas = (ComplexGaugeDatas)c.getDatas();
				if (element instanceof PropertyGaugeMaxima){((PropertyGaugeMaxima)element).setPropertyValue(value);}
				if (element == dataset){datas.setDataSet((DataSet)value); fieldCbo.setInput(((DataSet)value).getDataSetDescriptor().getColumnsDescriptors());refreshParameters();}
				if (element == datasource){dataSetCbo.setInput(getComponentDefinition().getDictionary().getDatasets());}

				if (element == fieldvalue){datas.setIndexValue(((ComboBoxViewerCellEditor)getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1 );}
				if (element == maxExpec){datas.setIndexMaxSeuil(((ComboBoxViewerCellEditor)getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1 );}
				if (element == minExpec){datas.setIndexMinSeuil(((ComboBoxViewerCellEditor)getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1 );}

				if (element == manualmaxima){datas.setManualRange((Boolean)value);}
				if (element == expectation){datas.setUseExpected((Boolean)value);}
				try{
					if (element == targeted){datas.setUseTarget((Boolean)value);}
					if (element == targetType){datas.setUseFieldForTarget((Boolean)value);}
				}catch(Exception ex){}
				notifyChangeOccured();
				resize();
			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentGauge c = (ComponentGauge)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				ComplexGaugeDatas datas = (ComplexGaugeDatas)c.getDatas();
				if (element instanceof PropertyGaugeMaxima){return ((PropertyGaugeMaxima)element).getPropertyValue();}
				try{
					if (element == manualmaxima){return datas.isRangeManual();}
					if (element == dataset){return datas.getDataSet();}
					if (element == datasource){return c.getDictionary().getDatasource(datas.getDataSet().getDataSourceName());}
					if (element == fieldvalue){((List)fieldCbo.getViewer().getInput()).get(datas.getIndexValue());}
					if (element == maxExpec){((List)fieldCbo.getViewer().getInput()).get(datas.getIndexMaxSeuil());}
					if (element == minExpec){((List)fieldCbo.getViewer().getInput()).get(datas.getIndexMinSeuil());}
				}catch(Exception ex){}
				if (element == dataset){return datas.isRangeManual();}
				if (element == expectation){return datas.isExpectedFieldsUsed();}
				if (element instanceof PropertyGaugeMaxima){return ((PropertyGaugeMaxima)element).getPropertyValue();}
				if (element == targeted){return datas.isTargetNeeded();}
				if (element == targetType){return datas.isUseFieldForTarget();}
				return null;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				ComponentGauge c = (ComponentGauge)getComponentDefinition();
				if (c == null){return false;}
				ComplexGaugeDatas datas = (ComplexGaugeDatas)c.getDatas();
				if (element == target || element == tolerance){
					return datas.isTargetNeeded();
				}
				if (element == maxExpec || element == minExpec){
					return datas.isExpectedFieldsUsed();
				}
				return element instanceof Property && ((Property)element).getCellEditor() != null;
			}
		});
		
		List input = new ArrayList();
		input.add(datasource);
		input.add(dataset);
		input.add(fieldvalue);
		input.add(manualmaxima);
		input.add(targeted);
		input.add(expectation);
		
		viewer.setInput(input);
	}
	private void createGauge(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.GaugeEditor_32);
		
		
		
		final Property badColor = new Property(Messages.GaugeEditor_33, new ColorCellEditor(viewer.getTree()));
		final Property mediumColor = new Property(Messages.GaugeEditor_34, new ColorCellEditor(viewer.getTree()));
		final Property goodColor = new Property(Messages.GaugeEditor_35, new ColorCellEditor(viewer.getTree()));
		final Property inner = new Property(Messages.GaugeEditor_36, new TextCellEditor(viewer.getTree()));
		final Property outer = new Property(Messages.GaugeEditor_37, new TextCellEditor(viewer.getTree()));
		final Property startangle = new Property(Messages.GaugeEditor_38, new TextCellEditor(viewer.getTree()));
		final Property stopangle = new Property(Messages.GaugeEditor_39, new TextCellEditor(viewer.getTree()));
		final Property showvalues = new Property(Messages.GaugeEditor_40, new CheckboxCellEditor(viewer.getTree()));
		final Property asBulb = new Property("Render as bulb chart", new CheckboxCellEditor(viewer.getTree()));

		PropertyGroup cofiguration = new PropertyGroup(Messages.GaugeEditor_41);
		cofiguration.add(inner);cofiguration.add(outer);cofiguration.add(startangle);cofiguration.add(stopangle);
		PropertyGroup colors = new PropertyGroup(Messages.GaugeEditor_42);
		colors.add(badColor);colors.add(mediumColor);colors.add(goodColor);
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				ComponentGauge c = (ComponentGauge)getComponentDefinition();
				if (c == null){return "";} //$NON-NLS-1$
				GaugeOptions opts = (GaugeOptions)c.getOptions(GaugeOptions.class); 
				if (c == null){return "";} //$NON-NLS-1$
				if (element == badColor){return "";} //$NON-NLS-1$
				if (element == mediumColor){return "";} //$NON-NLS-1$
				if (element == goodColor){return "";} //$NON-NLS-1$
				if (element == inner){return opts.getInnerRadius() + "";} //$NON-NLS-1$
				if (element == outer){return opts.getOuterRadius() + "";} //$NON-NLS-1$
				if (element == startangle){return opts.getStartAngle() + "";} //$NON-NLS-1$
				if (element == stopangle){return opts.getStopAngle() + "";} //$NON-NLS-1$
				if (element == showvalues){return opts.isShowValues() + "";} //$NON-NLS-1$
				if (element == asBulb){return opts.isBulb() + "";} //$NON-NLS-1$
				return ""; //$NON-NLS-1$
			}
			@Override
			public org.eclipse.swt.graphics.Color getBackground(Object element) {
				ComponentGauge c = (ComponentGauge)getComponentDefinition();
				if (c == null){return null;}
				GaugeOptions opts = (GaugeOptions)c.getOptions(GaugeOptions.class);
				Color col = null;
				if (element == badColor){
					col = opts.getColorBadValue();
				}
				if (element == mediumColor){
					col = opts.getColorMediumValue();
				}
				if (element == goodColor){
					col = opts.getColorGoodValue();
				}
				if (col != null){
					return ChartEditor.getColor(col);
				}
				return super.getBackground(element);
			}
		});
		
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				ComponentGauge c = (ComponentGauge)getComponentDefinition();
				if (c == null){return ;}
				GaugeOptions opts = (GaugeOptions)c.getOptions(GaugeOptions.class); 
				try{
					if (element == badColor){opts.setColorBadValue(new Color(((RGB)value).red, ((RGB)value).green, ((RGB)value).blue));}
					if (element == mediumColor){opts.setColorMediumValue(new Color(((RGB)value).red, ((RGB)value).green, ((RGB)value).blue));}
					if (element == goodColor){opts.setColorGoodValue(new Color(((RGB)value).red, ((RGB)value).green, ((RGB)value).blue));}
					if (element == inner){opts.setInnerRadius(Integer.valueOf((String)value));}
					if (element == outer){opts.setOuterRadius(Integer.valueOf((String)value));}
					if (element == startangle){opts.setStartAngle(Integer.valueOf((String)value));}
					if (element == stopangle){opts.setStopAngle(Integer.valueOf((String)value));}
					if (element == showvalues){opts.setShowValues((Boolean) value);}
					if (element == asBulb){opts.setBulb((Boolean) value);}
				}catch(Exception ex){}
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				ComponentGauge c = (ComponentGauge)getComponentDefinition();
				if (c == null){return null;}
				GaugeOptions opts = (GaugeOptions)c.getOptions(GaugeOptions.class); 
				if (element == badColor){return getRGB(opts.getColorBadValue());}
				if (element == mediumColor){return getRGB(opts.getColorMediumValue());}
				if (element == goodColor){return getRGB(opts.getColorGoodValue());}
				if (element == inner){return opts.getInnerRadius() + "";} //$NON-NLS-1$
				if (element == outer){return opts.getOuterRadius()+ "";} //$NON-NLS-1$
				if (element == startangle){return opts.getStartAngle()+ "";} //$NON-NLS-1$
				if (element == stopangle){return opts.getStopAngle()+ "";} //$NON-NLS-1$
				if (element == showvalues){return opts.isShowValues();}
				if (element == asBulb){return opts.isBulb();}
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
		input.add(cofiguration);input.add(colors);input.add(showvalues);input.add(asBulb);
		viewer.setInput(input);
	}

	private Object getRGB(Color col) {
		if (col == null){return null;}
		return new RGB(col.getRed(), col.getGreen(), col.getBlue());
	}
	
	public void setInput(EditPart editPart, ComponentConfig conf, IComponentDefinition component){
		
		dataSourceCbo.setInput(component.getDictionary().getDatasources());
		dataSetCbo.setInput(component.getDictionary().getDatasets());
		if(component.getDatas().getDataSet() != null){
			dataSourceCbo.setValue(component.getDictionary().getDatasource(component.getDatas().getDataSet().getDataSourceName()));
			fieldCbo.setInput(component.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors());
		}
		else{
			dataSetCbo.setInput(Collections.EMPTY_LIST);
		}
		
		
		min = new PropertyGaugeMaxima(
				PropertyGaugeMaxima.MIN, 
				(ComplexGaugeDatas)component.getDatas(), 
				Messages.GaugeEditor_58, 
				new TextCellEditor(fieldCbo.getViewer().getControl().getParent()),
				fieldCbo);
		max =  new PropertyGaugeMaxima(
				PropertyGaugeMaxima.MAX, 
				(ComplexGaugeDatas)component.getDatas(), 
				Messages.GaugeEditor_59, 
				new TextCellEditor(fieldCbo.getViewer().getControl().getParent()),
				fieldCbo);
		manualmaxima.clear();
		manualmaxima.add(min);manualmaxima.add(max);
		
		
		target = new PropertyGaugeMaxima(
				PropertyGaugeMaxima.TARGET, 
				(ComplexGaugeDatas)component.getDatas(), 
				Messages.GaugeEditor_60, 
				new TextCellEditor(fieldCbo.getViewer().getControl().getParent()),
				fieldCbo);
		tolerance =  new PropertyGaugeMaxima(
				PropertyGaugeMaxima.TOLERANCE, 
				(ComplexGaugeDatas)component.getDatas(), 
				Messages.GaugeEditor_61, 
				new TextCellEditor(fieldCbo.getViewer().getControl().getParent()),
				fieldCbo);

		targetType.clear();
		targetType.add(target);targetType.add(tolerance);
		
		super.setInput(editPart, conf, component);
//		viewer.expandAll();
		
	}
}