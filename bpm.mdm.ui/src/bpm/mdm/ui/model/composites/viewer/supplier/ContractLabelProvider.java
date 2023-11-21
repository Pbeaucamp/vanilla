package bpm.mdm.ui.model.composites.viewer.supplier;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;

public class ContractLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Contract supplier = (Contract) element;
		switch (columnIndex) {
		case Contract.ID:
			
			return ""+supplier.getId();
		case Contract.NAME:
			
			return supplier.getName();
		case Contract.EXTERNAL_SOURCE:
			
			return supplier.getExternalSource();
		case Contract.EXTERNAL_ID:
			
			return supplier.getExternalId();

		default:
			break;
		}
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		
		
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		
		
	}

}
