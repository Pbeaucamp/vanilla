package bpm.mdm.ui.model.composites.viewer;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.mdm.model.supplier.DocumentItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DocumentItemLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element == null) {
			return null;
		}

		if (element instanceof DocumentItem) {
			DocumentItem item = (DocumentItem) element;
			switch (columnIndex) {
			case 0:
				return item.getItem().getName();
			case 1:
				return getType(item.getItem());
			default:
				break;
			}
		}
		return null;
	}

	private String getType(RepositoryItem item) {
		if (item.getType() == IRepositoryApi.GTW_TYPE) {
			return "ETL";
		}
		else if (item.getType() == IRepositoryApi.BIW_TYPE) {
			return "Workflow";
		}
		else if (item.getType() == IRepositoryApi.CUST_TYPE && item.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
			return "Birt";
		}
		else if (item.getType() == IRepositoryApi.CUST_TYPE && item.getSubtype() == IRepositoryApi.FWR_TYPE) {
			return "Web Report";
		}
		return "Unknown";
	}

	@Override
	public void addListener(ILabelProviderListener listener) { }

	@Override
	public void dispose() { }

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) { }
}
