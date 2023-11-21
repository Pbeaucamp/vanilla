package bpm.fd.design.ui.properties.model;

import java.util.List;

import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.TextCellEditor;

import bpm.fd.api.core.model.components.definition.slicer.SlicerData.Level;
import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.design.ui.properties.i18n.Messages;

public class PropertyLevel extends PropertyGroup{
	private Property levelName;
	private Property levelField;
	private Level level;
	private ComboBoxViewerCellEditor fieldEditor;
	
	public PropertyLevel(Level level, String name, ComboBoxViewerCellEditor fieldEditor) {
		super(name);
		levelName = new Property(Messages.PropertyLevel_0, new TextCellEditor(fieldEditor.getControl().getParent()));
		levelField = new Property(Messages.PropertyLevel_1, fieldEditor);
		add(levelName);
		add(levelField);
		this.fieldEditor = fieldEditor;
		this.level = level;
		
	}
	
	public String getLabelString(Property child){
		if (child == levelField){
			try{
				Object o = ((List)this.fieldEditor.getViewer().getInput()).get(level.getFieldIndex());
				 return ((ColumnDescriptor)o).getColumnLabel();	
			}catch(Exception ex){
				
			}
		}
		if (child == levelName){
			return level.getLabel();
		}
		return ""; //$NON-NLS-1$
	}
	
	public Object getValue(Property child){
		if (child == levelField){
			try{
				Object o = ((List)this.fieldEditor.getViewer().getInput()).get(level.getFieldIndex());
				 return o;	
			}catch(Exception ex){
				
			}
		}
		if (child == levelName){
			return level.getLabel();
		}
		return null;
	}
	
	public void setValue(Property child, Object value){
		if (child == levelField){
			Integer index = ((List)this.fieldEditor.getViewer().getInput()).indexOf(value);
			if (index == -1){
				level.setFieldIndex(null);
			}
			else{
				level.setFieldIndex(index);
			}
		}
		if (child == levelName){
			level.setLabel((String)value);
		}
	}
}
