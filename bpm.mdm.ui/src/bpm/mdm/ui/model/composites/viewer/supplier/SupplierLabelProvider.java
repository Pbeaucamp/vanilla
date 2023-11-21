package bpm.mdm.ui.model.composites.viewer.supplier;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;

public class SupplierLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element == null) {
			return null;
		}

		if (element instanceof Supplier) {
			Supplier supplier = (Supplier) element;
			switch (columnIndex) {
			case Supplier.ID:

				return "" + supplier.getId();
			case Supplier.NAME:

				return supplier.getName();
			case Supplier.EXTERNAL_SOURCE:

				return supplier.getExternalSource();
			case Supplier.EXTERNAL_ID:

				return supplier.getExternalId();

			default:
				break;
			}
		}
		else {
			Contract supplier = (Contract) element;
			switch (columnIndex) {
			case Supplier.ID:

				return "" + supplier.getId();
			case Supplier.NAME:

				return supplier.getName();
			case Supplier.EXTERNAL_SOURCE:

				return supplier.getExternalSource();
			case Supplier.EXTERNAL_ID:

				return supplier.getExternalId();

			default:
				break;
			}
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
