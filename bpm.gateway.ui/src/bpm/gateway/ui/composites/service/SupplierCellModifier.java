package bpm.gateway.ui.composites.service;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import bpm.gateway.ui.composites.service.ServiceViewerComposite.TypeViewer;
import bpm.vanilla.platform.core.beans.Variable;
import bpm.vanilla.platform.core.beans.service.IService;

public class SupplierCellModifier extends EditingSupport {

	public static final int NAME = 0;
	public static final int TYPE = 1;
	public static final int VALUE = 2;

	private CellEditor editor;
	private int type;
	private TypeViewer typeViewer;

	public SupplierCellModifier(ColumnViewer viewer, int type, TypeViewer typeViewer) {
		super(viewer);
		this.type = type;
		this.typeViewer = typeViewer;

		if (type == TYPE) {
			editor = new ComboBoxCellEditor((Composite) viewer.getControl(), Variable.TYPE_NAMES);
		}
		else {
			editor = new TextCellEditor((Composite) viewer.getControl());
		}
	}

	@Override
	protected boolean canEdit(Object element) {
		switch (type) {
			case NAME:
				if(typeViewer != TypeViewer.TYPE_WEB_SERVICE_INPUT){
					return true;
				}
				else {
					return false;
				}
			case TYPE:
				if(typeViewer != TypeViewer.TYPE_WEB_SERVICE_INPUT){
					return true;
				}
				else {
					return false;
				}
			case VALUE:
				if(typeViewer == TypeViewer.TYPE_WEB_SERVICE_INPUT){
					return true;
				}
				else {
					return false;
				}
		}
		return false;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		IService service = (IService) element;
		switch (type) {
			case NAME:
				return service.getName() != null ? service.getName() : ""; //$NON-NLS-1$
			case TYPE:
				return service.getType() > 0 ? service.getType() : 0;
			case VALUE:
				return service.getValue() != null ? service.getValue() : ""; //$NON-NLS-1$
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		IService service = (IService) element;
		switch (type) {
			case NAME:
				service.setName((String) value);
				break;
			case TYPE:
				service.setType((Integer)value);
				break;
			case VALUE:
				service.setValue((String) value);
				break;
		}
		getViewer().refresh();
	}
	
//	private int getType(String typeName){
//		int index = 0;
//		for(String type : Variable.TYPE_NAMES){
//			if(type.equals(typeName)){
//				return index;
//			}
//			index++;
//		}
//		return -1;
//	}
}