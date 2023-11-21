package bpm.gateway.ui.views.property.sections.mdm;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.file.FileCSV;
import bpm.gateway.core.server.file.FileSystemServer;
import bpm.gateway.core.server.file.MdmFileServer;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.inputs.FileInputShape;
import bpm.gateway.core.transformations.inputs.FileInputXLS;
import bpm.gateway.core.transformations.kml.KMLInput;
import bpm.gateway.core.transformations.mdm.IMdmContract;
import bpm.gateway.core.transformations.mdm.IMdmContractInput;
import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
import bpm.gateway.core.transformations.mdm.MdmHelper;
import bpm.gateway.core.veolia.ConnectorXML;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.composites.StreamComposite;
import bpm.gateway.ui.composites.file.FileCsvComposite;
import bpm.gateway.ui.composites.file.FileXlsComposite;
import bpm.gateway.ui.composites.labelproviders.DefaultStreamLabelProvider;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.i18n.Messages;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;

public class MdmContractComposite extends Composite {

	private static Color BLUE = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get("bpm.gateway.ui.colors.mappingexists"); //$NON-NLS-1$

	private Node node;
	private IMdmContract transfo;
	
	private Composite compositeMdm;
	private ComboViewer comboSuppliers, comboContracts;

	private Composite compositeMetadata;
	private StreamComposite gridMetadata;
	
	private StackLayout stackedLayout;
	private Composite stackComposite;

	private FileCsvComposite csvComposite;
	private FileXlsComposite xlsComposite;

	private List<Meta> meta;
	private boolean uncheckOnPurpose;
	private boolean showContractWithNoDocument;

	public MdmContractComposite(Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, int style, boolean showContractWithNoDocument) {
		super(parent, style);
		this.showContractWithNoDocument = showContractWithNoDocument;

		createContent(parent, widgetFactory);
	}

	private void createContent(Composite parent, TabbedPropertySheetWidgetFactory widgetFactory) {
		this.setLayout(new GridLayout());
	
		Composite composite = widgetFactory.createComposite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	
		compositeMdm = widgetFactory.createComposite(composite, SWT.NONE);
		compositeMdm.setLayout(new GridLayout(2, false));
		compositeMdm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		Label lblSupplier = widgetFactory.createLabel(compositeMdm, Messages.MdmContractComposite_0, SWT.NONE);
		lblSupplier.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		comboSuppliers = new ComboViewer(compositeMdm, SWT.DROP_DOWN | SWT.PUSH);
		comboSuppliers.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		comboSuppliers.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Supplier supplier = (Supplier) element;
				return supplier.getName();
			}
		});
		comboSuppliers.setContentProvider(new ArrayContentProvider());
		comboSuppliers.addSelectionChangedListener(supplierListener);

		Label lblContract = widgetFactory.createLabel(compositeMdm, Messages.MdmContractComposite_1, SWT.NONE);
		lblContract.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));

		comboContracts = new ComboViewer(compositeMdm, SWT.DROP_DOWN | SWT.PUSH);
		comboContracts.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		comboContracts.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Contract contract = (Contract) element;
				return contract.getName();
			}
		});
		comboContracts.setContentProvider(new ArrayContentProvider());
		comboContracts.addSelectionChangedListener(contractListener);
		
		compositeMetadata = widgetFactory.createComposite(compositeMdm, SWT.NONE);
		compositeMetadata.setLayout(new GridLayout());
		compositeMetadata.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		compositeMetadata.setVisible(false);
		
		ToolBar toolbar = new ToolBar(compositeMetadata, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		toolbar.setBackground(composite.getBackground());
		
		ToolItem addSelected = new ToolItem(toolbar, SWT.PUSH);
		addSelected.setText(Messages.SelectionSection_0);
		addSelected.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				uncheckOnPurpose = true;
				
				performAddMeta(gridMetadata.getCheckedElements());
				gridMetadata.refresh();
				gridMetadata.clearCheck();
				try {
					refreshMetadata();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
		});
		
		ToolItem delSelected = new ToolItem(toolbar, SWT.PUSH);
		delSelected.setText(Messages.SelectionSection_1);
		delSelected.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				uncheckOnPurpose = true;
				
				performDelMeta(gridMetadata.getCheckedElements());
				gridMetadata.refresh();
				gridMetadata.clearCheck();
				try {
					refreshMetadata();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		ToolItem addAll = new ToolItem(toolbar, SWT.PUSH);
		addAll.setText(Messages.SelectionSection_2);
		addAll.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				uncheckOnPurpose = true;
				
				performAddMeta(gridMetadata.getInput());
				gridMetadata.refresh();
				gridMetadata.clearCheck();
				try {
					refreshMetadata();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
		});
		
		ToolItem delAllSelected = new ToolItem(toolbar, SWT.PUSH);
		delAllSelected.setText(Messages.SelectionSection_3);
		delAllSelected.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				uncheckOnPurpose = true;
				
				performDelMeta(gridMetadata.getInput());
				gridMetadata.refresh();
				gridMetadata.clearCheck();
				try {
					refreshMetadata();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		gridMetadata = new StreamComposite(compositeMetadata, SWT.NONE, true, false);
		gridMetadata.setLabelProvider(new StreamLabelProvider(gridMetadata.getLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		gridMetadata.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		stackedLayout = new StackLayout();
		stackComposite = new Composite(composite, SWT.NONE);
		stackComposite.setLayout(stackedLayout);
		stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		csvComposite = new FileCsvComposite(stackComposite, SWT.BORDER, widgetFactory);
		csvComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		xlsComposite = new FileXlsComposite(stackComposite, SWT.BORDER, widgetFactory);
		xlsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}

	public void refresh(List<Supplier> suppliers) throws Exception {
		if (transfo != null) {
			comboSuppliers.removeSelectionChangedListener(supplierListener);
			comboContracts.removeSelectionChangedListener(contractListener);
			comboSuppliers.setInput(suppliers.toArray());

			if (transfo.getSelectedContract() != null || (transfo.getContractId() != null && transfo.getContractId() > 0)) {
				if (transfo.getSelectedContract() == null) {
					transfo.initDescriptor();
				}
				
				int supplierId = transfo.getSelectedContract() != null && transfo.getSelectedContract().getParent() != null ? transfo.getSelectedContract().getParent().getId().intValue() : transfo.getSupplierId();
				int contractId = transfo.getSelectedContract() != null ? transfo.getSelectedContract().getId().intValue() : transfo.getContractId();
				
				for (Supplier s : suppliers) {
					if (supplierId == s.getId().intValue()) {
						comboSuppliers.setSelection(new StructuredSelection(s));
						for (Contract c : s.getContracts()) {
							if (contractId == c.getId().intValue()) {
								populateContracts(s.getContracts());
								comboContracts.setSelection(new StructuredSelection(c));
								transfo.setContract(c);

								showComposite(c);
								break;
							}
						}
						break;
					}
				}
			}
			else {
				comboContracts.setInput(new ArrayList<Contract>().toArray());
			}
			comboSuppliers.addSelectionChangedListener(supplierListener);
			comboContracts.addSelectionChangedListener(contractListener);
		}
	}
	
	public void refreshMetadata() throws Exception {
		Contract contract = transfo.getSelectedContract();
		
		IRepositoryApi repositoryApi = ((AbstractTransformation) transfo).getDocument().getMdmHelper().getRepositoryApi();
		List<MetaLink> links = repositoryApi.getMetaService().getMetaLinks(contract.getId(), TypeMetaLink.ARCHITECT, false);
		
		String transfoName = ((AbstractTransformation) transfo).getName();

		this.meta = new ArrayList<Meta>();
		List<StreamElement> l = new ArrayList<StreamElement>();
		for(MetaLink link : links){
			meta.add(link.getMeta());
			l.add(MdmHelper.buildColumn(transfoName, link.getMeta()));
		}
		gridMetadata.fillDatas(l);
//		if (!l.isEmpty() && !uncheckOnPurpose && !init && transfo.isInited()){
//			performAddColumns(streamComposite.getCheckedAndUnCkeckedPosition());
//			streamComposite.refresh();
//			streamComposite.clearCheck();
//			init = true;
//			refresh();
//		}
	}
	
	protected void performAddMeta(List<StreamElement> elements){
		if (transfo != null && transfo instanceof MdmContractFileInput){
			for(StreamElement i : elements) {
				Meta meta = findMeta(i);
				if (meta != null) {
					((MdmContractFileInput) transfo).addMeta(meta);
				}
			}
		}
	}

	protected void performDelMeta(List<StreamElement> elements){
		if (transfo != null && transfo instanceof MdmContractFileInput) {
			for(StreamElement i : elements){
				Meta meta = findMeta(i);
				if (meta != null) {
					((MdmContractFileInput) transfo).removeMeta(meta);
				}
			}
		}
	}
	
	private Meta findMeta(StreamElement i) {
		if (meta != null) {
			for (Meta met : meta) {
				if (i.name.equals(met.getLabel())) {
					return met;
				}
			}
		}
		return null;
	}

	public void showComposite(Contract contract) throws Exception {
		if (contract.getFileVersions() == null && contract.getDocId() != null) {
			try {
				contract.setFileVersions(node.getGatewayModel().getDocument().getMdmHelper().getGedDocument(contract));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		compositeMetadata.setVisible(false);
		
		
		String format = ""; //$NON-NLS-1$
		if (contract.getVersionId() != null) {
			format = contract.getFileVersions().getCurrentVersion(contract.getVersionId()).getFormat();
		}
		else if (contract.getFileVersions() != null) {
			format = contract.getFileVersions().getCurrentVersion(contract.getVersionId()).getFormat();
		}
		if (transfo instanceof ConnectorXML) {
			if (format.equalsIgnoreCase("csv") || format.equalsIgnoreCase("xls") || format.equalsIgnoreCase("xlsx")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				MessageDialog.openInformation(getShell(), Messages.MdmContractComposite_5, Messages.MdmContractComposite_6);
			}
			else {
				stackedLayout.topControl = null;
			}
		}
		else if (transfo instanceof IMdmContractInput) {
			
			if (transfo instanceof KMLInput) {
				if (format.equalsIgnoreCase("kml")) { //$NON-NLS-1$
					stackedLayout.topControl = null;
				}
				else {
					MessageDialog.openInformation(getShell(), Messages.MdmContractComposite_10, Messages.MdmContractComposite_11);
				}
			}
			else if (transfo instanceof FileInputShape) {
				if (format.equalsIgnoreCase("shp")) { //$NON-NLS-1$
					stackedLayout.topControl = null;
				}
				else {
					MessageDialog.openInformation(getShell(), Messages.MdmContractComposite_10, Messages.MdmContractComposite_11);
				}
			}
			else {
				if (format.equalsIgnoreCase("csv") || format.equalsIgnoreCase("json") || format.equalsIgnoreCase("geojson")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					stackedLayout.topControl = csvComposite;
					csvComposite.refresh(node);
				}
				else if (format.equalsIgnoreCase("xls") || format.equalsIgnoreCase("xlsx")) { //$NON-NLS-1$ //$NON-NLS-2$
					stackedLayout.topControl = xlsComposite;
					xlsComposite.refresh(node);
				}
				else {
					MessageDialog.openInformation(getShell(), Messages.MdmContractComposite_10, Messages.MdmContractComposite_11);
				}
				
				if (transfo instanceof MdmContractFileInput) {
					compositeMetadata.setVisible(true);
					refreshMetadata();
				}
			}
		}
		else {
			stackedLayout.topControl = null;
		}
		stackComposite.layout();
	}
	
	//Not used for now, need to find a way to put the format on Contract
//	private boolean formatAllowed(Contract contract) {
//		String format = null;
//		if (contract.getVersionId() != null) {
//			format = contract.getFileVersions().getCurrentVersion(contract.getVersionId()).getFormat();
//		}
//		
//		if (format == null) {
//			return true;
//		}
//		
//		if (transfo instanceof ConnectorXML && (format.equalsIgnoreCase("csv") || format.equalsIgnoreCase("xls") || format.equalsIgnoreCase("xlsx"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//			return false;
//		}
//		else if (transfo instanceof IMdmContractInput && !(format.equalsIgnoreCase("csv") || format.equalsIgnoreCase("xls") || format.equalsIgnoreCase("xlsx"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//			return false;
//		}
//		
//		return true;
//	}

	ISelectionChangedListener contractListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			Contract contract = (Contract) ((IStructuredSelection) event.getSelection()).getFirstElement();
			
			transfo.setFileTransfo(null);
			transfo.setContract(contract);

			try {
				showComposite(contract);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	ISelectionChangedListener supplierListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			Supplier supplier = (Supplier) ((IStructuredSelection) event.getSelection()).getFirstElement();
			populateContracts(supplier.getContracts());
		}
	};
	
	private void populateContracts(List<Contract> contracts) {
		contracts = filterContracts(contracts, showContractWithNoDocument);
		comboContracts.setInput(contracts);
	}

	private List<Contract> filterContracts(List<Contract> contracts, boolean showContractWithNoDocument) {
		List<Contract> filterContracts = new ArrayList<>();
		if (contracts != null) {
			for (Contract contract : contracts) {
				if ((showContractWithNoDocument || (contract.getDocId() != null && contract.getDocId() > 0) /* && formatAllowed(contract)*/)) {
					filterContracts.add(contract);
				}
			}
		}
		return filterContracts;
	}

	public void setNode(Node node) {
		this.node = node;
		if (node.getGatewayModel() instanceof IMdmContract) {
			this.transfo = (IMdmContract) node.getGatewayModel();
		}
	}

	public void aboutToBeHidden() {
		try {
			if (stackedLayout.topControl == csvComposite) {
				FileInputCSV csv = (FileInputCSV) csvComposite.getFileTransformation();
				List<Server> servers = ResourceManager.getInstance().getServers(MdmFileServer.class);

				MdmFileServer srv = null;
				if (servers.size() > 0) {
					srv = (MdmFileServer) servers.get(0);
				}
				else {
					srv = new MdmFileServer("mdmserver", "", Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl(), Activator.getDefault().getRepositoryContext().getVanillaContext().getLogin(), Activator.getDefault().getRepositoryContext().getVanillaContext().getPassword(), Activator.getDefault().getRepositoryContext().getRepository().getId() + ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					try {
						ResourceManager.getInstance().addServer(srv);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				((FileCSV) csv).setServer(srv);

				csv.setDefinition(transfo.getSelectedContract().getId() + ""); //$NON-NLS-1$
				transfo.setFileTransfo(csv);
			}
			else if (stackedLayout.topControl == xlsComposite) {
				FileInputXLS xls = (FileInputXLS) xlsComposite.getFileTransformation();
				xls.setDefinition(transfo.getSelectedContract().getId() + ""); //$NON-NLS-1$
				transfo.setFileTransfo(xls);
			}
			else if (transfo instanceof IMdmContract) {
				List<Server> servers = ResourceManager.getInstance().getServers(MdmFileServer.class);
				
				MdmFileServer srv = null;
				if (servers.size() > 0) {
					srv = (MdmFileServer) servers.get(0);
				}
				else {
					
					try {
						srv = new MdmFileServer("mdmserver", "", Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl(), Activator.getDefault().getRepositoryContext().getVanillaContext().getLogin(), Activator.getDefault().getRepositoryContext().getVanillaContext().getPassword(), Activator.getDefault().getRepositoryContext().getRepository().getId() + ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						ResourceManager.getInstance().addServer(srv);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if (transfo instanceof KMLInput) {
					if (transfo.getSelectedContract() == null) {
						((KMLInput) transfo).setServer(FileSystemServer.getInstance());
						((KMLInput)transfo).refreshDescriptor();
					}
					else {
						((KMLInput) transfo).setServer(srv);
					}
				}
				else if (transfo instanceof FileInputShape) {
					if (!transfo.useMdm() || transfo.getSelectedContract() == null) {
						((FileInputShape) transfo).setServer(FileSystemServer.getInstance());
						((FileInputShape)transfo).refreshDescriptor();
					}
					else {
						((FileInputShape) transfo).setServer(srv);
					}
				}
				else {
					((IMdmContract) transfo).setFileServer(srv);
				}
			}
			else if (transfo.getSelectedContract().getFileVersions().getCurrentVersion(transfo.getSelectedContract().getVersionId()).getFormat().equalsIgnoreCase("kml")) { //$NON-NLS-1$
				KMLInput kml = new KMLInput();
				kml.setDefinition(transfo.getSelectedContract().getId() + ""); //$NON-NLS-1$
				List<Server> servers = ResourceManager.getInstance().getServers(MdmFileServer.class);

				MdmFileServer srv = null;
				if (servers.size() > 0) {
					srv = (MdmFileServer) servers.get(0);
				}
				else {
					srv = new MdmFileServer("mdmserver", "", Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl(), Activator.getDefault().getRepositoryContext().getVanillaContext().getLogin(), Activator.getDefault().getRepositoryContext().getVanillaContext().getPassword(), Activator.getDefault().getRepositoryContext().getRepository().getId() + ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					try {
						ResourceManager.getInstance().addServer(srv);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				((KMLInput) kml).setServer(srv);
				transfo.setFileTransfo(kml);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		comboSuppliers.getCombo().setEnabled(enabled);
		comboContracts.getCombo().setEnabled(enabled);
		
		csvComposite.setEnabled(enabled);
		xlsComposite.setEnabled(enabled);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		compositeMdm.setVisible(visible);
		
		stackComposite.setVisible(visible);
	}
	
	public class StreamLabelProvider extends DefaultStreamLabelProvider{

		public StreamLabelProvider(ILabelProvider provider, ILabelDecorator decorator) {
			super(provider, decorator);
		}
		
		@Override
		public Color getForeground(Object element) {
			if (transfo instanceof MdmContractFileInput){
				if (((MdmContractFileInput) transfo).isMetaSelected((StreamElement) element)){
					return BLUE;
				}
			}

			return super.getForeground(element);
		}

	}
}
