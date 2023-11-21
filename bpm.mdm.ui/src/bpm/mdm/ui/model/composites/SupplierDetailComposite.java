package bpm.mdm.ui.model.composites;

import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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
import bpm.mdm.model.supplier.EnterpriseDimensionElement;
import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.ui.Activator;
import bpm.mdm.ui.dialog.AddNewFileDialog;
import bpm.mdm.ui.i18n.Messages;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;

public class SupplierDetailComposite extends MasterDetailsBlock {

	private FormToolkit toolkit;
	
	private TableViewer supplierViewer,dimensionViewer, contractViewer;
	private SectionPart partSupplier,partDimension,partContract;
	private Section sectionContract;

	private List<Supplier> suppliers;

	private Supplier selectedSupplier;
	
	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		toolkit = managedForm.getToolkit();
		
		Composite main = toolkit.createComposite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
//		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
//		Section sectionSupplier = toolkit.createSection(main, Section.TWISTIE | Section.TITLE_BAR);
//		sectionSupplier.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
//		sectionSupplier.addExpansionListener(new _ExpansionListener());
//		toolkit.paintBordersFor(sectionSupplier);
//		sectionSupplier.setText("Fournisseurs - fichiers associés");
//		
//		Composite compositeSupplier = toolkit.createComposite(sectionSupplier);
//		compositeSupplier.setLayout(new GridLayout());
//		toolkit.paintBordersFor(compositeSupplier);
//		
//		createSectionToolBar(compositeSupplier);
//		
//		supplierViewer = createSupplierViewer(compositeSupplier, managedForm);
//		toolkit.paintBordersFor(supplierViewer.getTable());
//		sectionSupplier.setClient(compositeSupplier);
		
		//contract
		sectionContract = toolkit.createSection(main, Section.TWISTIE | Section.TITLE_BAR);
		sectionContract.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		sectionContract.addExpansionListener(new _ExpansionListener());
		toolkit.paintBordersFor(sectionContract);
		sectionContract.setText(Messages.SupplierDetailComposite_0);
		
		Composite compositeContract = toolkit.createComposite(sectionContract);
		compositeContract.setLayout(new GridLayout());
		toolkit.paintBordersFor(compositeContract);
		
		createSectionToolBar(compositeContract);
		contractViewer = createContractViewer(compositeContract, managedForm);
		toolkit.paintBordersFor(contractViewer.getTable());
		sectionContract.setClient(compositeContract);
		
		
		
//		//Dimensions entreprises
//		
//		Section sectionDimension = toolkit.createSection(main, Section.TWISTIE | Section.TITLE_BAR);
//		sectionDimension.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
//		sectionDimension.addExpansionListener(new _ExpansionListener());
//		toolkit.paintBordersFor(sectionDimension);
//		sectionDimension.setText("Dimensions d'entreprise - membres");
//		
//		Composite compositeDimension = toolkit.createComposite(sectionDimension);
//		compositeDimension.setLayout(new GridLayout());
//		toolkit.paintBordersFor(compositeDimension);
//		
//		createSectionToolBar(compositeDimension);
//		dimensionViewer = createDimensionViewer(compositeDimension, managedForm);
//		toolkit.paintBordersFor(dimensionViewer.getTable());
//		sectionDimension.setClient(compositeDimension);
//		
//		
//		partSupplier = new SectionPart(sectionSupplier);
//		managedForm.addPart(partSupplier);
		
		partContract = new SectionPart(sectionContract);
		managedForm.addPart(partContract);
		
//		partDimension = new SectionPart(sectionDimension);
//		managedForm.addPart(partDimension);
	}

	private TableViewer createContractViewer(Composite compositeContract, final IManagedForm managedForm) {
		final TableViewer table = new TableViewer(compositeContract);
		table.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		for(String header : supplierHearders) {
			TableViewerColumn col = new TableViewerColumn(table, SWT.NONE);
			col.getColumn().setWidth(150);
			col.getColumn().setText(header);
		}
		table.setContentProvider(new IStructuredContentProvider() {
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
			@Override
			public void dispose() {
				
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				Contract element = (Contract) inputElement;
				if(element.getFileVersions() != null) {
					return element.getFileVersions().getDocumentVersions().toArray();
				}
				return new Contract[0];
			}
		});
		table.setLabelProvider(new ITableLabelProvider() {
			@Override
			public void removeListener(ILabelProviderListener listener) {
				
			}
			
			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}
			
			@Override
			public void dispose() {
				
			}
			
			@Override
			public void addListener(ILabelProviderListener listener) {
				
			}
			
			@Override
			public String getColumnText(Object element, int columnIndex) {
				try {
					DocumentVersion version = (DocumentVersion) element;
					switch (columnIndex) {
					case Supplier.DOC_ID:
						
						return version.getId() + ""; //$NON-NLS-1$
					case Supplier.DOC_NAME:
						
						return version.getParent().getName() + " - V" + version.getVersion(); //$NON-NLS-1$
					case Supplier.DOC_FORMAT:
						
						return version.getFormat();
					case Supplier.DOC_VERSION:
						
						return "V" + version.getVersion(); //$NON-NLS-1$
					case Supplier.DOC_DATE:
						
						return sdf.format(version.getModificationDate());

					default:
						break;
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});
		table.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(partContract, table.getSelection());
				managedForm.getForm().layout(true);
				
			}
		});
		table.getTable().setHeaderVisible(true);
		table.getTable().setLinesVisible(true);
		return table;
	}

	private void createSectionToolBar(final Composite compositeSupplier) {
		ToolBar bar = new ToolBar(compositeSupplier, SWT.FLAT);
		bar.setBackground(toolkit.getColors().getBackground());
		bar.setLayoutData(new GridData());
		
		ToolItem it = new ToolItem(bar, SWT.PUSH);
		it.setToolTipText(Messages.ModelMasterDetails_12);
		it.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(selectedContract != null)  {
					AddNewFileDialog dial = new AddNewFileDialog(compositeSupplier.getShell(), selectedContract, suppliers);
					if(dial.open() == Dialog.OK) {
						MessageDialog.openInformation(compositeSupplier.getShell(), Messages.SupplierDetailComposite_4, Messages.SupplierDetailComposite_5);
						contractViewer.setInput(selectedContract);
					}
				}
				else {
					MessageDialog.openInformation(compositeSupplier.getShell(), "Select a contract", "You need to selected a contract before adding a new file");
				}
			}
		});
		
//		final ToolItem delt = new ToolItem(bar, SWT.PUSH);
//		delt.setToolTipText(Messages.ModelMasterDetails_14);
//		delt.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
//		delt.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				
//			}
//		});
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		
	}

	private String[] dimensionHearders = new String[]{Messages.SupplierDetailComposite_6, Messages.SupplierDetailComposite_7, Messages.SupplierDetailComposite_8, Messages.SupplierDetailComposite_9, Messages.SupplierDetailComposite_10};
	private TableViewer createDimensionViewer(Composite compositeDimension, final IManagedForm managedForm) {
		final TableViewer table = new TableViewer(compositeDimension);
		
		table.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		for(String header : dimensionHearders) {
			TableViewerColumn col = new TableViewerColumn(table, SWT.NONE);
			col.getColumn().setWidth(150);
			col.getColumn().setText(header);
		}
		table.setContentProvider(new IStructuredContentProvider() {
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
			@Override
			public void dispose() {
				
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				return ((List)inputElement).toArray();
			}
		});
		table.setLabelProvider(new ITableLabelProvider() {
			@Override
			public void removeListener(ILabelProviderListener listener) {
				
			}
			
			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}
			
			@Override
			public void dispose() {
				
			}
			
			@Override
			public void addListener(ILabelProviderListener listener) {
				
			}
			
			@Override
			public String getColumnText(Object element, int columnIndex) {
				EnterpriseDimensionElement version = (EnterpriseDimensionElement) element;
				switch (columnIndex) {
				case 0:
					
					return version.getId() + ""; //$NON-NLS-1$
				case 1:
					
					return version.getName();
				case 2:
					
					return "V" + version.getVersion(); //$NON-NLS-1$
				case 3:
					
					return sdf.format(version.getCreationDate());
				case 4:
					
					return ""; //$NON-NLS-1$

				default:
					break;
				}
				return null;
			}
			
			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});
		table.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(partDimension, table.getSelection());
				managedForm.getForm().layout(true);
				
			}
		});
		table.getTable().setHeaderVisible(true);
		table.getTable().setLinesVisible(true);
		
		return table;
	}
	
	
	private String[] supplierHearders = new String[]{Messages.SupplierDetailComposite_14, Messages.SupplierDetailComposite_15, Messages.SupplierDetailComposite_16, Messages.SupplierDetailComposite_17, Messages.SupplierDetailComposite_18};
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$

	private EnterpriseDimension selectedDimension;

	private Contract selectedContract;
	
//	private TableViewer createSupplierViewer(Composite compositeSupplier, final IManagedForm managedForm) {
//		final TableViewer table = new TableViewer(compositeSupplier);
//		table.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
//		for(String header : supplierHearders) {
//			TableViewerColumn col = new TableViewerColumn(table, SWT.NONE);
//			col.getColumn().setWidth(150);
//			col.getColumn().setText(header);
//		}
//		table.setContentProvider(new IStructuredContentProvider() {
//			@Override
//			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//				
//			}
//			
//			@Override
//			public void dispose() {
//				
//			}
//			
//			@Override
//			public Object[] getElements(Object inputElement) {
//				try {
//					Supplier element = (Supplier) inputElement;
//					return element.getFileVersions().getDocumentVersions().toArray();
//				} catch (Exception e) {
//					return new DocumentVersion[0];
//				}
//			}
//		});
//		table.setLabelProvider(new ITableLabelProvider() {
//			@Override
//			public void removeListener(ILabelProviderListener listener) {
//				
//			}
//			
//			@Override
//			public boolean isLabelProperty(Object element, String property) {
//				return false;
//			}
//			
//			@Override
//			public void dispose() {
//				
//			}
//			
//			@Override
//			public void addListener(ILabelProviderListener listener) {
//				
//			}
//			
//			@Override
//			public String getColumnText(Object element, int columnIndex) {
//				DocumentVersion version = (DocumentVersion) element;
//				switch (columnIndex) {
//				case Supplier.DOC_ID:
//					
//					return version.getId() + "";
//				case Supplier.DOC_NAME:
//					
//					return version.getParent().getName() + " - V" + version.getVersion();
//				case Supplier.DOC_FORMAT:
//					
//					return version.getFormat();
//				case Supplier.DOC_VERSION:
//					
//					return "V" + version.getVersion();
//				case Supplier.DOC_DATE:
//					
//					return sdf.format(version.getModificationDate());
//
//				default:
//					break;
//				}
//				return null;
//			}
//			
//			@Override
//			public Image getColumnImage(Object element, int columnIndex) {
//				return null;
//			}
//		});
//		table.addSelectionChangedListener(new ISelectionChangedListener() {
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				managedForm.fireSelectionChanged(partSupplier, table.getSelection());
//				managedForm.getForm().layout(true);
//				
//			}
//		});
//		table.getTable().setHeaderVisible(true);
//		table.getTable().setLinesVisible(true);
//		return table;
//	}
	
	private static class _ExpansionListener implements IExpansionListener{
		@Override
		public void expansionStateChanging(ExpansionEvent e) {
			((Section)e.getSource()).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, e.getState(), 1, 1));
			
		}
		
		@Override
		public void expansionStateChanged(ExpansionEvent e) {
			((Section)e.getSource()).getParent().layout(true);
			
		}
	}

	public void initData(List<Supplier> suppliers, Supplier selectedSupplier, EnterpriseDimension selectedDimension, Contract selectedContract) throws Exception {
		
		this.suppliers = suppliers;
//		this.selectedSupplier = selectedSupplier;
//		this.selectedDimension = selectedDimension;
		this.selectedContract = selectedContract;
		
		if(selectedContract != null && selectedContract.getFileVersions() == null && selectedContract.getDocId() != null) {
			GedDocument doc = Activator.getDefault().getGedComponent().getDocumentDefinitionById(selectedContract.getDocId());
			selectedContract.setFileVersions(doc);
		}
		
//		if(selectedSupplier != null) {
//			supplierViewer.setInput(selectedSupplier);
//		}
//		if(selectedDimension != null) {
//			dimensionViewer.setInput(selectedDimension.getElements());
//		}
		if(selectedContract != null) {
			contractViewer.setInput(selectedContract);
		}
		
		sectionContract.setExpanded(true);
	}

	public void refresh() {
		
	}
}
