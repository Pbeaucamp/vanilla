package bpm.es.parameters.ui.viewer;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.es.parameters.ui.Activator;
import bpm.es.parameters.ui.views.DatasProviderHelper;
import bpm.vanilla.platform.core.repository.ILinkedParameter;
import bpm.vanilla.platform.core.repository.Parameter;


public class ParameterColumnLableProvider extends ColumnLabelProvider{
	
	
	public static enum ColumnType{
		Name, ProviderName, ValueColumn, ParentName, LabelColumn, MultipleValuesColumn;
	}
	
	private ColumnType type;
	private DatasProviderHelper helper;
	public ParameterColumnLableProvider(ColumnType type, DatasProviderHelper helper){
		this.type = type;
		this.helper = helper;
	}
	
	
	@Override
	public String getText(Object element) {
		String s = null;
		switch(type){
		case Name:
			if (element instanceof Parameter){
				s = ((Parameter)element).getName();
			}
			else if (element instanceof ILinkedParameter){
				
//				ILinkedParameter p = (ILinkedParameter)element;
//				if (p.getInternalParameterName() == null || p.getInternalParameterName().equals("")){
//					try {
//						helper.reInitParameterName(p);
//					} catch (Exception e) {
//						
//						e.printStackTrace();
//					}
//				}
				
				s = ((ILinkedParameter)element).getInternalParameterName();
			}
			
			break;
		case ProviderName:
			if (element instanceof Parameter){
				s = ((Parameter)element).getDataProviderName();
			}
			
			break;
		case ValueColumn:
			if (element instanceof Parameter){
				Parameter p = (Parameter)element;
				
//				if (p.getValueColumnName() == null ){
//					try {
//						helper.reInitColumnNames(p);
//					} catch (Exception e) {
//						
//						e.printStackTrace();
//					}
//				}
				
				
				s = ((Parameter)element).getValueColumnName();
			}
			
			break;
		case LabelColumn:
			if (element instanceof Parameter){
				Parameter p = (Parameter)element;
				
//				if (p.getValueColumnName() == null ){
//					try {
//						helper.reInitColumnNames(p);
//					} catch (Exception e) {
//						
//						e.printStackTrace();
//					}
//				}
//				
				
				s = ((Parameter)element).getLabelColumnName();
			}
			
			break;
		case ParentName :
			if (element instanceof ILinkedParameter){
				
				
				s = ((ILinkedParameter)element).getProviderParameterName();
			}
			
			break;

		}
		
		if (s == null){
			return ""; //$NON-NLS-1$
		}
		return s;
	}

	
	@Override
	public Image getImage(Object element) {
		if (type == ColumnType.MultipleValuesColumn && element instanceof Parameter){
			if (((Parameter)element).isAllowMultipleValues()){
				return Activator.getDefault().getImageRegistry().get("checked"); //$NON-NLS-1$
			}
			else{
				return Activator.getDefault().getImageRegistry().get("unchecked"); //$NON-NLS-1$
			}
		}
		return super.getImage(element);
	}
}
