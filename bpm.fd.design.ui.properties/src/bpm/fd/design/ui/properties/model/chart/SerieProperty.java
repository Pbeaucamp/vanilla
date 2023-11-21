package bpm.fd.design.ui.properties.model.chart;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

import bpm.fd.api.core.model.components.definition.IComponentDataFilter;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation.MeasureRendering;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.chart.ChartOrderingType;
import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.api.core.model.datas.filters.ValueFilter;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGroup;
import bpm.fd.design.ui.properties.model.chart.FilterCellEditor.FilterCellEditorData;

public class SerieProperty extends PropertyGroup{

	private DataAggregation agg;
	private IChartData chartDatas;
	
	private Property aggregatorP;
	private Property measureName;
	private Property valueIndex;
	private Property rendering;
	private Property primaryAxis;
	private Property splited;
	private PropertyGroup filters;
	private PropertyGroup colors;

	private Property ordering;
	
	private ComboBoxViewerCellEditor paletteEditor;
	
	public SerieProperty(boolean isMulti, DataAggregation agg, IChartData chartDatas, CellEditor fieldEditor) {
		super(Messages.SerieProperty_0 + agg.getMeasureName(), null);
		this.agg = agg;
		this.chartDatas = chartDatas;
		
		aggregatorP = new Property(Messages.SerieProperty_1, new ComboBoxCellEditor(fieldEditor.getControl().getParent(), DataAggregation.AGGREGATORS_NAME, SWT.READ_ONLY));
		measureName = new Property(Messages.SerieProperty_2, new TextCellEditor(fieldEditor.getControl().getParent()));
		valueIndex = new Property(Messages.SerieProperty_3, fieldEditor);
		rendering = new Property(Messages.SerieProperty_4, new ComboBoxCellEditor(fieldEditor.getControl().getParent(), MeasureRendering.names, SWT.READ_ONLY));
		primaryAxis = new Property(Messages.SerieProperty_5, new CheckboxCellEditor(fieldEditor.getControl().getParent()));
		splited = new Property(Messages.SerieProperty_6, new CheckboxCellEditor(fieldEditor.getControl().getParent()));
		filters = new PropertyGroup(Messages.SerieProperty_7, new FilterCellEditor(fieldEditor.getControl().getParent()));
		colors = new PropertyGroup(Messages.SerieProperty_8, null);
		
		ComboBoxViewerCellEditor orderTypeCbo = new ComboBoxViewerCellEditor(fieldEditor.getControl().getParent(), SWT.READ_ONLY);
		orderTypeCbo.setContenProvider(new ArrayContentProvider());
		orderTypeCbo.setLabelProvider(new LabelProvider());
		orderTypeCbo.setInput(ChartOrderingType.values());
		
		ordering = new Property(Messages.SerieProperty_9, orderTypeCbo);
		
		add(measureName);
		add(aggregatorP);
		add(valueIndex);
		add(rendering);
		add(primaryAxis);
		add(splited);
		add(ordering);
		add(filters);
		add(colors);
		for(IComponentDataFilter f : agg.getFilter()){
			
			Property p = new FilterProperty(((ValueFilter)f));
			filters.add(p);
		}
		int i = 0;
		for(String s : agg.getColorsCode()){
			colors.add(new PropertyColor(i++, agg, s, new ColorCellEditor(fieldEditor.getControl().getParent())));
		}
		
	}
	
	public DataAggregation getDataAggeregation(){
		return agg;
	}

	public String getPropertyValueString(Object element) {
		if (element == aggregatorP){
			return DataAggregation.AGGREGATORS_NAME[agg.getAggregator()];
		}
		
		if (element == measureName){return agg.getMeasureName();}
		if (agg.getValueFieldIndex() == null){
			return ""; //$NON-NLS-1$
		}
		if (filters.getProperties().contains(element)){
			return ((FilterProperty)element).getValue();
		}
		try{
			if (element == valueIndex){if(agg.getValueFieldIndex() == null){return null;}else return ((ColumnDescriptor)((List)((ComboBoxViewerCellEditor)valueIndex.getCellEditor()).getViewer().getInput()).get(agg.getValueFieldIndex() -1)).getColumnLabel();}
			if (element == rendering){return agg.getRendering().name();}
			if (element == primaryAxis){return agg.isPrimaryAxis() + "";} //$NON-NLS-1$
			if (element == splited){return agg.isApplyOnDistinctSerie() + "";} //$NON-NLS-1$
			if (element == ordering){return agg.getOrderType().name();} //$NON-NLS-1$
			
		}catch(Exception ex){
			ex.printStackTrace();
			return ""; //$NON-NLS-1$
		}
		
		return null;
	}
	public Object getPropertyValue(Object element) {
		if (element == aggregatorP){
			return agg.getAggregator();
		}
		if (element == filters){
			return new FilterCellEditorData(agg, chartDatas);
		}
		if (element == measureName){return agg.getMeasureName();}
		if (element == valueIndex){return agg.getValueFieldIndex();}
		if (element == rendering){return agg.getRendering().ordinal();}
		if (element == primaryAxis){return agg.isPrimaryAxis();}
		if (element == splited){return agg.isApplyOnDistinctSerie();}
		if (element == ordering){return agg.getOrderType();} //$NON-NLS-1$

		return null;
	}
	public void setPropertyValue(Object element, Object value) {
		if (element == filters){
			
		}
		if (element == aggregatorP){
			agg.setAggregator((Integer)value);
		}
				if (element == measureName){agg.setMeasureName((String)value);}
		if (element == valueIndex){if (value == null){agg.setValueFieldIndex(null);}else agg.setValueFieldIndex(((List)((ComboBoxViewerCellEditor)valueIndex.getCellEditor()).getViewer().getInput()).indexOf(value) + 1);}
		if (element == rendering){agg.setRendering(MeasureRendering.values()[(Integer)value]);}
		if (element == primaryAxis){agg.setPrimaryAxis((Boolean)value);}
		if (element == splited){agg.setApplyOnDistinctSerie((Boolean)value);}
		if (element == ordering){agg.setOrderType((ChartOrderingType) value);} //$NON-NLS-1$
	}

	public void refresh() {
		filters.clear();

		for(IComponentDataFilter f : agg.getFilter()){
			
			Property p = new FilterProperty(((ValueFilter)f));
			filters.add(p);
		}
		colors.clear();
		int i = 0;
		for(String s : agg.getColorsCode()){
			colors.add(new PropertyColor(i++, agg, s, new ColorCellEditor(aggregatorP.getCellEditor().getControl().getParent())));
		}
		

	}

	public List<Property> getColorProperties() {
		return (List<Property>)colors.getProperties();
	}
}
