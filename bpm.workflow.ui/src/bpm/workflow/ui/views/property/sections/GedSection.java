package bpm.workflow.ui.views.property.sections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.workflow.runtime.model.activities.vanilla.GedActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class GedSection extends AbstractPropertySection {

	private GedActivity activity;
	
	private Composite mdmComposite;
	private Button btnLoadSupplier;
	private Button btnAddToMDM, btnAddToGed;
	private ComboViewer cbSupplier, cbContract;
	
	private List<Supplier> suppliers;

	public GedSection() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		Group group = getWidgetFactory().createGroup(parent, "File Management"); //$NON-NLS-1$
		group.setLayout(new GridLayout());

		btnAddToGed = getWidgetFactory().createButton(group, Messages.GedSection_1, SWT.RADIO);
		btnAddToGed.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnAddToGed.setSelection(true);

		btnAddToMDM = getWidgetFactory().createButton(group, Messages.GedSection_2, SWT.RADIO);
		btnAddToMDM.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		mdmComposite = getWidgetFactory().createComposite(group);
		mdmComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mdmComposite.setLayout(new GridLayout(2, false));
		
		btnLoadSupplier = getWidgetFactory().createButton(mdmComposite, Messages.GedSection_3, SWT.PUSH);
		btnLoadSupplier.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		btnLoadSupplier.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if(Activator.getDefault().getRepositoryContext() == null) {
						throw new Exception(Messages.DialogRepositoryObject_3);
					}
					
					loadSuppliers();
				} catch(Exception ex) {
					ex.printStackTrace();
					MessageDialog.openWarning(getPart().getSite().getShell(), Messages.GedSection_4, ex.getMessage());
				}
			}
		});
		
		Label lblSuppliers = getWidgetFactory().createLabel(mdmComposite, Messages.GedSection_5);
		lblSuppliers.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		
		cbSupplier = new ComboViewer(mdmComposite, SWT.READ_ONLY | SWT.PUSH);
		cbSupplier.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		cbSupplier.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

			@Override
			public void dispose() { }

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<Supplier> l = (List<Supplier>) inputElement;
				return l.toArray(new Supplier[l.size()]);
			}
		});
		cbSupplier.setLabelProvider(new LabelProvider() {
			
			@Override
			public String getText(Object element) {
				return ((Supplier) element).getName();
			}
		});
		
		Label lblContract = getWidgetFactory().createLabel(mdmComposite, Messages.GedSection_6);
		lblContract.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));

		cbContract = new ComboViewer(mdmComposite, SWT.READ_ONLY | SWT.PUSH);
		cbContract.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		cbContract.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

			@Override
			public void dispose() { }

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<Contract> l = (List<Contract>) inputElement;
				return l.toArray(new Contract[l.size()]);
			}
		});
		cbContract.setLabelProvider(new LabelProvider() {
			
			@Override
			public String getText(Object element) {
				return ((Contract) element).getName();
			}
		});
	}
	
	private void enableMDMPart(boolean enabled) {
		mdmComposite.setEnabled(enabled);
		btnLoadSupplier.setEnabled(enabled);
		cbSupplier.getCombo().setEnabled(enabled);
		cbContract.getCombo().setEnabled(enabled);
	}

	private List<Supplier> loadSuppliers() throws Exception {
		IVanillaContext ctx = Activator.getDefault().getVanillaContext();
		
		IMdmProvider provider = new MdmRemote(ctx.getLogin(), ctx.getPassword(), ctx.getVanillaUrl(), null, null);
		provider.loadModel();
		
		List<Supplier> suppliers = provider.getSuppliers();
		if(suppliers != null) {
			cbSupplier.setInput(suppliers);
		}
		else {
			cbSupplier.setInput(new ArrayList<Supplier>());
		}
		
		return suppliers;
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.activity = (GedActivity) ((Node) ((NodePart) input).getModel()).getWorkflowObject();
	}

	@Override
	public void refresh() {
		btnAddToMDM.removeSelectionListener(typeListener);
		btnAddToGed.removeSelectionListener(typeListener);
		cbSupplier.removeSelectionChangedListener(changeSupplier);
		cbContract.removeSelectionChangedListener(changeContract);

		btnAddToMDM.setSelection(activity.addToMDM());
		btnAddToGed.setSelection(!activity.addToMDM());

		enableMDMPart(activity.addToMDM());
		
		if(activity.addToMDM()) {
			try {
				if(suppliers == null) {
					suppliers = loadSuppliers();
				}
				
				if(activity.getSupplierId() != null && suppliers != null) {
					for(Supplier sup : suppliers) {
						if(sup.getId().equals(activity.getSupplierId())) {
							cbSupplier.setSelection(new StructuredSelection(sup));
							
							cbContract.setInput(sup.getContracts() != null ? sup.getContracts() : new ArrayList<Contract>());
							if(activity.getContractId() != null && sup.getContracts() != null) {
								for(Contract cont : sup.getContracts()) {
									if(cont.getId().equals(activity.getContractId())) {
										cbContract.setSelection(new StructuredSelection(cont));
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openWarning(getPart().getSite().getShell(), Messages.GedSection_7, e.getMessage());
			}
		}

		btnAddToMDM.addSelectionListener(typeListener);
		btnAddToGed.addSelectionListener(typeListener);
		cbSupplier.addSelectionChangedListener(changeSupplier);
		cbContract.addSelectionChangedListener(changeContract);
	}
	
	private SelectionAdapter typeListener = new SelectionAdapter() {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			activity.setAddToMDM(btnAddToMDM.getSelection());
			enableMDMPart(btnAddToMDM.getSelection());
		}
	};
	
	private ISelectionChangedListener changeSupplier = new ISelectionChangedListener() {
		
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection ss = (IStructuredSelection)cbSupplier.getSelection();

			if (!ss.isEmpty() && ss.getFirstElement() instanceof Supplier){
				Supplier sup = (Supplier)ss.getFirstElement();
				activity.setSupplierId(sup.getId());
				
				cbContract.setInput(sup.getContracts() != null ? sup.getContracts() : new ArrayList<Contract>());
			}
		}
	};
	
	private ISelectionChangedListener changeContract = new ISelectionChangedListener() {
		
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection ss = (IStructuredSelection)cbContract.getSelection();

			if (!ss.isEmpty() && ss.getFirstElement() instanceof Contract){
				Contract cont = (Contract)ss.getFirstElement();
				activity.setContractId(cont.getId());
			}
		}
	};
}
