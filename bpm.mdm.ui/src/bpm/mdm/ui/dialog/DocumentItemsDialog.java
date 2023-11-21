package bpm.mdm.ui.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.DocumentItem;
import bpm.mdm.ui.Activator;
import bpm.mdm.ui.i18n.Messages;
import bpm.mdm.ui.model.composites.viewer.DocumentItemContentProvider;
import bpm.mdm.ui.model.composites.viewer.DocumentItemLabelProvider;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.dialogs.DialogDirectoryItemPicker;

public class DocumentItemsDialog extends Dialog {

	private Contract contract;

	private TableViewer tableViewer;

	public DocumentItemsDialog(Shell parentShell, Contract contract) {
		super(parentShell);
		this.contract = contract;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 500);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite compositeSupplier = new Composite(comp, SWT.NONE);
		compositeSupplier.setLayout(new GridLayout());
		compositeSupplier.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createToolBar(compositeSupplier);

		tableViewer = createSupplierViewer(compositeSupplier);
		loadDocumentItems();

		return comp;
	}

	private void loadDocumentItems() {
		try {
			IRepositoryApi repApi = Activator.getDefault().getRepositoryApi();
			
			List<DocumentItem> items = Activator.getDefault().getMdmProvider().getDocumentItems(contract.getId());
			if (items != null) {
				for (DocumentItem docItem : items) {
					RepositoryItem item = repApi.getRepositoryService().getDirectoryItem(docItem.getItemId());
					docItem.setItem(item);
				}
			}
			tableViewer.setInput(items);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createToolBar(Composite compositeContract) {
		ToolBar bar = new ToolBar(compositeContract, SWT.FLAT);
		bar.setLayoutData(new GridData());

		ToolItem it = new ToolItem(bar, SWT.PUSH);
		it.setToolTipText(Messages.ModelMasterDetails_12);
		it.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					IRepositoryApi repositoryApi = Activator.getDefault().getRepositoryApi();
					int[] type = new int[3];
					type[0] = IRepositoryApi.GTW_TYPE;
					type[1] = IRepositoryApi.BIW_TYPE;
					type[2] = IRepositoryApi.CUST_TYPE;
					int[] subtype = null;

					DialogDirectoryItemPicker d = new DialogDirectoryItemPicker(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), repositoryApi, type, subtype);
					if (d.open() == DialogDirectoryItemPicker.OK) {
						RepositoryItem item = d.getDirectoryItem();

						DocumentItem docItem = new DocumentItem();
						docItem.setContractId(contract.getId());
						docItem.setItem(item);

						Activator.getDefault().getMdmProvider().saveOrUpdateDocumentItem(docItem);
						
						loadDocumentItems();
						tableViewer.refresh();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		final ToolItem delt = new ToolItem(bar, SWT.PUSH);
		delt.setToolTipText(Messages.ModelMasterDetails_14);
		delt.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
		delt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					DocumentItem doc = (DocumentItem) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
					Activator.getDefault().getMdmProvider().removeDocumentItem(doc);
					
					loadDocumentItems();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private TableViewer createSupplierViewer(Composite compositeSupplier) {
		final TableViewer table = new TableViewer(compositeSupplier);
		table.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

		TableViewerColumn colName = new TableViewerColumn(table, SWT.NONE);
		colName.getColumn().setWidth(150);
		colName.getColumn().setText("Name");

		TableViewerColumn colType = new TableViewerColumn(table, SWT.NONE);
		colType.getColumn().setWidth(150);
		colType.getColumn().setText("Type");

		table.setContentProvider(new DocumentItemContentProvider());
		table.setLabelProvider(new DocumentItemLabelProvider());
		table.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// managedForm.fireSelectionChanged(partSupplier, table.getSelection());
				// managedForm.getForm().layout(true);
			}
		});
		table.getTable().setHeaderVisible(true);
		table.getTable().setLinesVisible(true);

		table.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// Supplier selectedSupplier = (Supplier) ((IStructuredSelection) event.getSelection()).getFirstElement();
				// changeSelection(selectedSupplier);
			}
		});

		return table;
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}
}
