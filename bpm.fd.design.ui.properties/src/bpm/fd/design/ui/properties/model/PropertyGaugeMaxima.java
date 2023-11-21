package bpm.fd.design.ui.properties.model;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;

import bpm.fd.api.core.model.components.definition.gauge.ComplexGaugeDatas;

public class PropertyGaugeMaxima extends Property{
	public final static int MIN = 1;
	public final static int MAX = 2;
	public final static int TOLERANCE = 3;
	public final static int TARGET = 4;
	
	private CellEditor manualEditor;
	private CellEditor fieldEditor;
	private ComplexGaugeDatas gaugeDatas;
	private int type;
	/**
	 * 
	 * @param type : MIN or MAX
	 * @param gaugeDatas
	 * @param name
	 * @param manualEditor
	 * @param fieldEditor
	 */
	public PropertyGaugeMaxima(int type, ComplexGaugeDatas gaugeDatas, String name, CellEditor manualEditor, CellEditor fieldEditor) {
		super(name, null);
		this.gaugeDatas = gaugeDatas;
		this.manualEditor = manualEditor;
		this.fieldEditor = fieldEditor;
		this.type = type;
		
	}
	
	@Override
	public CellEditor getCellEditor() {
		switch(type){
		case MAX:
		case MIN:
			if (gaugeDatas.isRangeManual()){
				return manualEditor;
			}
			return fieldEditor;
		case TARGET:
			if (gaugeDatas.isUseFieldForTarget()){
				return fieldEditor;
			}
		case TOLERANCE:
			return manualEditor;
		}
		return null;
	}

	public String getPropertyValueString(){
		try{
			switch(type){
			case MAX:
				if (gaugeDatas.isRangeManual()){
					return gaugeDatas.getMaxValue() + ""; //$NON-NLS-1$
				}
				else{
					 return gaugeDatas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(gaugeDatas.getIndexMax() - 1).getColumnLabel();
				}
			case MIN:
				if (gaugeDatas.isRangeManual()){
					return gaugeDatas.getMinValue() + ""; //$NON-NLS-1$
				}
				else{
					return gaugeDatas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(gaugeDatas.getIndexMin() - 1).getColumnLabel();
				}
			case TARGET:
				if (gaugeDatas.isTargetNeeded()){
					if (gaugeDatas.isUseFieldForTarget()){
						return gaugeDatas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(gaugeDatas.getTargetIndex() - 1).getColumnLabel();
					}
					else{
						return gaugeDatas.getTargetValue() + ""; //$NON-NLS-1$
					}
				}
			case TOLERANCE:
				if (gaugeDatas.isTargetNeeded()){
					return gaugeDatas.getIndexTolerance() + ""; //$NON-NLS-1$
				}
			}
		}catch(Exception ex){
			
		}
		
		
		return ""; //$NON-NLS-1$
		
			
	}
	
	public Object getPropertyValue(){
		Object val = null;
		try{
			switch(type){
			case MAX:
				if (gaugeDatas.isRangeManual()){
					val = gaugeDatas.getMaxValue() + ""; //$NON-NLS-1$
				}
				else{
					val = gaugeDatas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(gaugeDatas.getIndexMin() - 1);
				}
				break;
			case MIN:
				if (gaugeDatas.isRangeManual()){
					val = gaugeDatas.getMinValue() + ""; //$NON-NLS-1$
				}
				else{
					val = gaugeDatas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(gaugeDatas.getIndexMax() - 1);
				}
				break;
			case TARGET:
				if (gaugeDatas.isTargetNeeded()){
					if (gaugeDatas.isUseFieldForTarget()){
						val =  gaugeDatas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(gaugeDatas.getTargetIndex() - 1).getColumnLabel();
					}
					else{
						val =  gaugeDatas.getTargetValue() + ""; //$NON-NLS-1$
					}
				}
				break;
			case TOLERANCE:
				if (gaugeDatas.isTargetNeeded()){
					val =  gaugeDatas.getIndexTolerance() + ""; //$NON-NLS-1$
				}
			}
		}catch(Exception e){}
		
		return val;
	}
	public void setPropertyValue(Object value){
		
		try{
			
			switch(type){
			case MAX:
				if (gaugeDatas.isRangeManual()){
					gaugeDatas.setMaxValue(Float.valueOf((String)value));
				}
				else{
					gaugeDatas.setIndexMax(((ComboBoxViewerCellEditor)fieldEditor).getViewer().getCCombo().getSelectionIndex() + 1 );
				}
				break;
			case MIN:
				if (gaugeDatas.isRangeManual()){
					gaugeDatas.setMinValue(Float.valueOf((String)value));
				}
				else{
					gaugeDatas.setIndexMin(((ComboBoxViewerCellEditor)fieldEditor).getViewer().getCCombo().getSelectionIndex() + 1 );
				}
				break;
			case TARGET:
				if (gaugeDatas.isTargetNeeded()){
					if (gaugeDatas.isUseFieldForTarget()){
						gaugeDatas.setTargetIndex(((ComboBoxViewerCellEditor)fieldEditor).getViewer().getCCombo().getSelectionIndex() + 1 );
					}
					else{
						gaugeDatas.setTargetValue(Float.valueOf((String)value));
					}
				}
				break;
			case TOLERANCE:
				gaugeDatas.setIndexTolerance(Integer.valueOf((String)value));
				break;
			}

		}catch(Exception ex){}
		
	}
}

