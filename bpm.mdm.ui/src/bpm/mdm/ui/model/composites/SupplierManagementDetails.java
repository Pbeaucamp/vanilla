package bpm.mdm.ui.model.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.EnterpriseDimension;
import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.ui.Activator;
import bpm.mdm.ui.dialog.AddContractDialog;
import bpm.mdm.ui.dialog.AddSupplierDialog;
import bpm.mdm.ui.dialog.DocumentItemsDialog;
import bpm.mdm.ui.i18n.Messages;
import bpm.mdm.ui.model.composites.viewer.supplier.SupplierContentProvider;
import bpm.mdm.ui.model.composites.viewer.supplier.SupplierLabelProvider;
import bpm.mdm.ui.views.SupplierManagementView;

public class SupplierManagementDetails extends MasterDetailsBlock {

	private FormToolkit toolkit;

	private TableViewer supplierViewer, contractViewer;
	private SectionPart partSupplier, partContract;

	private SupplierManagementView supplierManagementView;

	private Section sectionSupplier, sectionContract;

	private List<Supplier> suppliers;

	private Supplier selectedSupplier;

	public SupplierManagementDetails(SupplierManagementView supplierManagementView) {
		this.supplierManagementView = supplierManagementView;

	}

	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		toolkit = managedForm.getToolkit();

		Composite main = toolkit.createComposite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		sectionSupplier = toolkit.createSection(main, Section.TWISTIE | Section.TITLE_BAR);
		sectionSupplier.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		sectionSupplier.addExpansionListener(new _ExpansionListener());
		toolkit.paintBordersFor(sectionSupplier);
		sectionSupplier.setText(Messages.SupplierManagementDetails_0);

		Composite compositeSupplier = toolkit.createComposite(sectionSupplier);
		compositeSupplier.setLayout(new GridLayout());
		compositeSupplier.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		toolkit.paintBordersFor(compositeSupplier);

		createSuppliersToolBar(compositeSupplier);

		supplierViewer = createSupplierViewer(compositeSupplier, managedForm);
		toolkit.paintBordersFor(supplierViewer.getTable());
		sectionSupplier.setClient(compositeSupplier);

		sectionContract = toolkit.createSection(main, Section.TWISTIE | Section.TITLE_BAR);
		sectionContract.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		sectionContract.addExpansionListener(new _ExpansionListener());
		toolkit.paintBordersFor(sectionContract);
		sectionContract.setText(Messages.SupplierManagementDetails_1);

		Composite compositeContract = toolkit.createComposite(sectionContract);
		compositeContract.setLayout(new GridLayout());
		toolkit.paintBordersFor(compositeContract);

		createContractToolBar(compositeContract);
		contractViewer = createContractViewer(compositeContract, managedForm);
		toolkit.paintBordersFor(contractViewer.getTable());
		sectionContract.setClient(compositeContract);

		// Section sectionDimension = toolkit.createSection(main,
		// Section.TWISTIE | Section.TITLE_BAR);
		// sectionDimension.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		// false, 1, 1));
		// sectionDimension.addExpansionListener(new _ExpansionListener());
		// toolkit.paintBordersFor(sectionDimension);
		// sectionDimension.setText("Dimensions d'entreprise");
		//
		// Composite compositeDimension =
		// toolkit.createComposite(sectionDimension);
		// compositeDimension.setLayout(new GridLayout());
		// toolkit.paintBordersFor(compositeDimension);

		// createContractToolBar(compositeDimension);
		// dimensionViewer = createDimensionViewer(compositeDimension,
		// managedForm);
		// toolkit.paintBordersFor(dimensionViewer.getTree());
		// sectionDimension.setClient(compositeDimension);

		partSupplier = new SectionPart(sectionSupplier);
		managedForm.addPart(partSupplier);

		partContract = new SectionPart(sectionContract);
		managedForm.addPart(partContract);
	}

	private void createContractToolBar(Composite compositeContract) {
		ToolBar bar = new ToolBar(compositeContract, SWT.FLAT);
		bar.setBackground(toolkit.getColors().getBackground());
		bar.setLayoutData(new GridData());

		ToolItem it = new ToolItem(bar, SWT.PUSH);
		it.setToolTipText(Messages.ModelMasterDetails_12);
		it.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddContractDialog dial = new AddContractDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), null, selectedSupplier);
				if (dial.open() == Dialog.OK) {
					try {

						suppliers = Activator.getDefault().getMdmProvider().getSuppliers();
						supplierViewer.setInput(suppliers);
						for (Supplier sup : suppliers) {
							if (sup.getId().intValue() == selectedSupplier.getId().intValue()) {
								selectedSupplier = sup;
								break;
							}
						}
						contractViewer.setInput(selectedSupplier.getContracts());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
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
					Contract c = (Contract) ((IStructuredSelection) contractViewer.getSelection()).getFirstElement();
					Activator.getDefault().getMdmProvider().removeContract(c);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				try {
					suppliers = Activator.getDefault().getMdmProvider().getSuppliers();
					supplierViewer.setInput(suppliers);
					for (Supplier sup : suppliers) {
						if (sup.getId().intValue() == selectedSupplier.getId().intValue()) {
							selectedSupplier = sup;
							break;
						}
					}
					contractViewer.setInput(selectedSupplier.getContracts() != null ? selectedSupplier.getContracts() : new ArrayList<Contract>());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		ToolItem edit = new ToolItem(bar, SWT.PUSH);
		edit.setToolTipText("Edit");
		edit.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE));
		edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Contract contract = (Contract) ((IStructuredSelection) contractViewer.getSelection()).getFirstElement();
					AddContractDialog dial = new AddContractDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), contract, selectedSupplier);
					if (dial.open() == Dialog.OK) {
						try {
							suppliers = Activator.getDefault().getMdmProvider().getSuppliers();
							supplierViewer.setInput(suppliers);
							for (Supplier sup : suppliers) {
								if (sup.getId().intValue() == selectedSupplier.getId().intValue()) {
									selectedSupplier = sup;
									break;
								}
							}
							contractViewer.setInput(selectedSupplier.getContracts());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		ToolItem itemsLinked = new ToolItem(bar, SWT.PUSH);
		itemsLinked.setToolTipText("Items Linked");
		itemsLinked.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEF_VIEW));
		itemsLinked.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Contract c = (Contract) ((IStructuredSelection) contractViewer.getSelection()).getFirstElement();
					
					DocumentItemsDialog d = new DocumentItemsDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), c);
					d.open();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void createSuppliersToolBar(Composite compositeSupplier) {
		ToolBar bar = new ToolBar(compositeSupplier, SWT.FLAT);
		bar.setBackground(toolkit.getColors().getBackground());
		bar.setLayoutData(new GridData());

		ToolItem it = new ToolItem(bar, SWT.PUSH);
		it.setToolTipText(Messages.ModelMasterDetails_12);
		it.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddSupplierDialog dial = new AddSupplierDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), null);
				if (dial.open() == Dialog.OK) {
					try {
						suppliers = Activator.getDefault().getMdmProvider().getSuppliers();
						supplierViewer.setInput(suppliers);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		ToolItem delt = new ToolItem(bar, SWT.PUSH);
		delt.setToolTipText(Messages.ModelMasterDetails_14);
		delt.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
		delt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Supplier c = (Supplier) ((IStructuredSelection) supplierViewer.getSelection()).getFirstElement();
					Activator.getDefault().getMdmProvider().removeSupplier(c);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				try {
					suppliers = Activator.getDefault().getMdmProvider().getSuppliers();
					supplierViewer.setInput(suppliers);

					contractViewer.setInput(new ArrayList<Contract>());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		ToolItem edit = new ToolItem(bar, SWT.PUSH);
		edit.setToolTipText("Edit");
		edit.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE));
		edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Supplier supplier = (Supplier) ((IStructuredSelection) supplierViewer.getSelection()).getFirstElement();
				AddSupplierDialog dial = new AddSupplierDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), supplier);
				if (dial.open() == Dialog.OK) {
					try {
						suppliers = Activator.getDefault().getMdmProvider().getSuppliers();
						supplierViewer.setInput(suppliers);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}

	private TableViewer createContractViewer(Composite compositeContract, final IManagedForm managedForm) {
		final TableViewer table = new TableViewer(compositeContract);
		table.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		int i = 0;
		for (String header : supplierHearders) {
			TableViewerColumn col = new TableViewerColumn(table, SWT.NONE);
			col.getColumn().setWidth(150);
			col.getColumn().setText(header);
			// col.setEditingSupport(new ContractCellModifier(table, i));
			i += 1;
		}
		table.setContentProvider(new SupplierContentProvider());
		table.setLabelProvider(new SupplierLabelProvider());
		table.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(partContract, table.getSelection());
				managedForm.getForm().layout(true);

			}
		});
		table.getTable().setHeaderVisible(true);
		table.getTable().setLinesVisible(true);

		table.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Contract selectedContract = (Contract) ((IStructuredSelection) event.getSelection()).getFirstElement();

				try {
					Activator.getDefault().getSupplierDetail().initData(suppliers, null, null, selectedContract);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		return table;
	}

	private TreeViewer createDimensionViewer(Composite compositeDimension, IManagedForm managedForm) {

		TreeViewer tree = new TreeViewer(compositeDimension);
		tree.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		tree.setContentProvider(new ITreeContentProvider() {
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			@Override
			public void dispose() {

			}

			@Override
			public Object[] getElements(Object inputElement) {
				return ((List) inputElement).toArray();
			}

			@Override
			public boolean hasChildren(Object element) {
				EnterpriseDimension dimension = (EnterpriseDimension) element;
				return dimension.getChildren() != null && dimension.getChildren().size() > 0;
			}

			@Override
			public Object getParent(Object element) {
				EnterpriseDimension dimension = (EnterpriseDimension) element;
				return dimension.getParent();
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				EnterpriseDimension dimension = (EnterpriseDimension) parentElement;
				return dimension.getChildren().toArray();
			}
		});

		tree.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				EnterpriseDimension dimension = (EnterpriseDimension) element;
				return dimension.getName();
			}
		});

		tree.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				EnterpriseDimension selectedSupplier = (EnterpriseDimension) ((IStructuredSelection) event.getSelection()).getFirstElement();

				try {
					Activator.getDefault().getSupplierDetail().initData(suppliers, null, selectedSupplier, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return tree;
	}

	private String[] supplierHearders = new String[] { Messages.SupplierManagementDetails_4, Messages.SupplierManagementDetails_5, Messages.SupplierManagementDetails_6, Messages.SupplierManagementDetails_7 };

	private TableViewer createSupplierViewer(Composite compositeSupplier, final IManagedForm managedForm) {
		final TableViewer table = new TableViewer(compositeSupplier);
		table.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		int i = 0;
		for (String header : supplierHearders) {
			TableViewerColumn col = new TableViewerColumn(table, SWT.NONE);
			col.getColumn().setWidth(150);
			col.getColumn().setText(header);
			// col.setEditingSupport(new SupplierCellModifier(table, i));
			i += 1;
		}
		table.setContentProvider(new SupplierContentProvider());
		table.setLabelProvider(new SupplierLabelProvider());
		table.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(partSupplier, table.getSelection());
				managedForm.getForm().layout(true);

			}
		});
		table.getTable().setHeaderVisible(true);
		table.getTable().setLinesVisible(true);

		table.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Supplier selectedSupplier = (Supplier) ((IStructuredSelection) event.getSelection()).getFirstElement();

				changeSelection(selectedSupplier);

			}
		});

		return table;
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {

	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {

	}

	public void refresh() {

	}

	private static class _ExpansionListener implements IExpansionListener {
		@Override
		public void expansionStateChanging(ExpansionEvent e) {
			((Section) e.getSource()).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, e.getState(), 1, 1));

		}

		@Override
		public void expansionStateChanged(ExpansionEvent e) {
			((Section) e.getSource()).getParent().layout(true);

		}
	}

	private void changeSelection(Supplier supplier) {
		if (supplier != null) {
			this.selectedSupplier = supplier;
		}

		cleanList();

		contractViewer.setInput(selectedSupplier.getContracts() != null ? selectedSupplier.getContracts() : new ArrayList<Contract>());
	}

	private void cleanList() {
		List<Contract> cleanContracts = new ArrayList<Contract>();

		List<Contract> contracts = selectedSupplier.getContracts();
		for (Contract contract : contracts) {
			if (contract != null) {
				contract.setParent(selectedSupplier);
				cleanContracts.add(contract);
			}
		}

		selectedSupplier.setContracts(cleanContracts);
	}

	public void initData(List<Supplier> suppliers) {
		this.suppliers = suppliers;
		supplierViewer.setInput(suppliers);
		// dimensionViewer.setInput(model.getDimensions());
		// contractViewer.setInput(model.getContracts());

		// try {
		// sectionSupplier.setExpanded(true);
		// sectionContract.setExpanded(true);
		// } catch (Throwable e) {
		// e.printStackTrace();
		// }
	}
}
