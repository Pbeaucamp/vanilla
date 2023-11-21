package bpm.es.pack.manager.imp;

import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import adminbirep.Activator;
import bpm.es.pack.manager.I18N.Messages;
import bpm.es.pack.manager.utils.ItemImage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.workplace.core.model.ImportItem;

public class CompositeImportItem extends Composite{
	
	private TableViewer exportContents;
	
	public CompositeImportItem(Composite parent, int style){
		super(parent, style);
		buildContent(parent);
	}
	
	private void buildContent(Composite parent){
		
		this.setLayout(new GridLayout(2, false));
		this.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		exportContents = new TableViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		exportContents.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		exportContents.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<ImportItem> l = (List<ImportItem>) inputElement;
				return l.toArray(new ImportItem[l.size()]);
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		exportContents.setLabelProvider(new ITableLabelProvider() {

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				if (columnIndex == 0) {
					ImageRegistry reg = Activator.getDefault().getImageRegistry();
					return reg.get(ItemImage.getKeyForType(((ImportItem) element).getType(), null));
				}
				return null;
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
					case 0:
						return ((ImportItem) element).getName();
					case 2:
						return IRepositoryApi.TYPES_NAMES[((ImportItem) element).getType()];
					case 1:
						return ((ImportItem) element).getPath();
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

		});
		exportContents.getTable().setHeaderVisible(true);

		TableColumn colName = new TableColumn(exportContents.getTable(), SWT.NONE);
		colName.setText(Messages.CompositeImportItem_0);
		colName.setWidth(200);

		TableColumn colPath = new TableColumn(exportContents.getTable(), SWT.NONE);
		colPath.setText(Messages.ViewPackage_23);
		colPath.setWidth(200);

		TableColumn colType = new TableColumn(exportContents.getTable(), SWT.NONE);
		colType.setText(Messages.ViewPackage_24);
		colType.setWidth(200);
	}

	public void loadDetails(List<ImportItem> importItems) {
		exportContents.setInput(importItems);
		exportContents.refresh();
	}
}
