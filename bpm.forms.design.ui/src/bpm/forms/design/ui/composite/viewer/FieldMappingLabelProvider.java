package bpm.forms.design.ui.composite.viewer;

import java.util.HashMap;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.design.ui.Activator;

public class FieldMappingLabelProvider extends ColumnLabelProvider{
	
	private static HashMap<Long, String> targetTableNames = new HashMap<Long, String>(); 
	
	
	public enum FieldType{
		Description, DatabaseFieldId, UiFieldId, Multivalued, TargetTable, SqlDataType; 
	}
	
	
	private FieldType type;
	
	public FieldMappingLabelProvider(FieldType type){
		this.type = type;
	}
	
	@Override
	public String getText(Object element) {
		IFormFieldMapping f = (IFormFieldMapping)element;
		switch(type){
		case DatabaseFieldId:
			return f.getDatabasePhysicalName();
		case Description:
			return f.getDescription();
		case Multivalued:
			return f.isMultiValued() + ""; //$NON-NLS-1$
		case UiFieldId:
			return f.getFormFieldId();
			
		case TargetTable:
			if (targetTableNames.get(f.getTargetTableId()) == null){
				return loadTableName(f.getTargetTableId());
			}
			else{
				return targetTableNames.get(f.getTargetTableId());
			}
		case SqlDataType : 
			return f.getSqlDataType();
		}
		return ""; //$NON-NLS-1$
	}
	
	
	private String loadTableName(Long l){
		
		
		try{
			String s = Activator.getDefault().getServiceProvider().getDefinitionService().getTargetTable(l).getName();
			targetTableNames.put(l, s);
			return s;
		}catch(Exception ex){
			ex.printStackTrace();
			return l + ""; //$NON-NLS-1$
		}

	}
}
