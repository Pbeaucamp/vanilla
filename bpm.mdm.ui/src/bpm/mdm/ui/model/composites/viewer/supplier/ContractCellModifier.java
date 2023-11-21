package bpm.mdm.ui.model.composites.viewer.supplier;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import bpm.mdm.model.supplier.Contract;

public class ContractCellModifier extends EditingSupport {
	public static final int ID = 0;
	public static final int NAME = 1;
	public static final int EXTERNAL_SOURCE = 2;
	public static final int EXTERNAL_ID = 3;
	private ColumnViewer viewer;
	
	private TextCellEditor editor;
	private int type;
	
	public ContractCellModifier(ColumnViewer viewer, int type) {
		super(viewer);
		editor = new TextCellEditor((Composite)viewer.getControl());
		this.viewer = viewer;
		this.type = type;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		Contract supplier = (Contract) element;
		switch (type) {
			case ID : return supplier.getId();
			case NAME : return supplier.getName();
			case EXTERNAL_SOURCE : return supplier.getExternalSource();
			case EXTERNAL_ID : return supplier.getExternalId();
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		try {
			Contract supplier = (Contract) element;
			switch (type) {
//				case ID : supplier.setId(Integer.parseInt((String) value));break;
				case NAME : supplier.setName((String) value);break;
				case EXTERNAL_SOURCE : supplier.setExternalSource((String) value);break;
				case EXTERNAL_ID : supplier.setExternalId((String) value);break;
			}
			getViewer().refresh();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
}
