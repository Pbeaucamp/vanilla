package bpm.gateway.ui.views.property.sections.d4c;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import bpm.gateway.core.Server;
import bpm.gateway.core.server.file.FileCSV;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.inputs.FileInputXLS;
import bpm.gateway.ui.composites.file.FileCsvComposite;
import bpm.gateway.ui.composites.file.FileXlsComposite;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;

public class D4CComposite extends Composite {

	private Node node;
	private D4CInput transfo;
	
	private Composite compositeD4c;
	private ComboViewer comboPackages, comboResources;

	private StackLayout stackedLayout;
	private Composite stackComposite;

	private FileCsvComposite csvComposite;
	private FileXlsComposite xlsComposite;

	public D4CComposite(Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, int style) {
		super(parent, style);

		createContent(parent, widgetFactory);
	}

	private void createContent(Composite parent, TabbedPropertySheetWidgetFactory widgetFactory) {
		this.setLayout(new GridLayout());
	
		Composite composite = widgetFactory.createComposite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	
		compositeD4c = widgetFactory.createComposite(composite, SWT.NONE);
		compositeD4c.setLayout(new GridLayout(2, false));
		compositeD4c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		Label lblPackage = widgetFactory.createLabel(compositeD4c, Messages.D4CComposite_0, SWT.NONE);
		lblPackage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		comboPackages = new ComboViewer(compositeD4c, SWT.DROP_DOWN | SWT.PUSH);
		comboPackages.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		comboPackages.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				CkanPackage pack = (CkanPackage) element;
				return pack.getName();
			}
		});
		comboPackages.setContentProvider(new ArrayContentProvider());
		comboPackages.addSelectionChangedListener(packageListener);

		Label lblResource = widgetFactory.createLabel(compositeD4c, Messages.D4CComposite_1, SWT.NONE);
		lblResource.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));

		comboResources = new ComboViewer(compositeD4c, SWT.DROP_DOWN | SWT.PUSH);
		comboResources.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		comboResources.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				CkanResource res = (CkanResource) element;
				return res.getName();
			}
		});
		comboResources.setContentProvider(new ArrayContentProvider());
		comboResources.addSelectionChangedListener(resourceListener);

		stackedLayout = new StackLayout();
		stackComposite = new Composite(composite, SWT.NONE);
		stackComposite.setLayout(stackedLayout);
		stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		csvComposite = new FileCsvComposite(stackComposite, SWT.BORDER, widgetFactory);
		csvComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		xlsComposite = new FileXlsComposite(stackComposite, SWT.BORDER, widgetFactory);
		xlsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}

	public void refresh(List<CkanPackage> packages) {
		if (transfo != null) {
			comboPackages.removeSelectionChangedListener(packageListener);
			comboResources.removeSelectionChangedListener(resourceListener);
			comboPackages.setInput(packages != null ? packages.toArray() : null);

			if (transfo.getSelectedResource() != null || (transfo.getResourceId() != null && !transfo.getResourceId().isEmpty())) {
				if (transfo.getSelectedPackage() == null || transfo.getSelectedResource() == null) {
					transfo.initDescriptor();
				}
				
				String packageId = transfo.getSelectedPackage().getId();
				String resourceId = transfo.getSelectedResource().getId();
				
				for (CkanPackage p : packages) {
					if (p.getId().equals(packageId)) {
						comboPackages.setSelection(new StructuredSelection(p));
						for (CkanResource r : p.getResources()) {
							if (r.getId().equals(resourceId)) {
								comboResources.setInput(p.getResources());
								comboResources.setSelection(new StructuredSelection(r));

								showComposite(r);
								break;
							}
						}
						break;
					}
				}
			}
			else {
				comboResources.setInput(new ArrayList<CkanResource>().toArray());
			}
			comboPackages.addSelectionChangedListener(packageListener);
			comboResources.addSelectionChangedListener(resourceListener);
		}
	}

	public void showComposite(CkanResource resource) {
		String format = resource.getFormat();
		if (transfo instanceof D4CInput) {
			if (format.equalsIgnoreCase("csv")) { //$NON-NLS-1$
				stackedLayout.topControl = csvComposite;
				csvComposite.refresh(node);
			}
			else if (format.equalsIgnoreCase("xls") || format.equalsIgnoreCase("xlsx")) { //$NON-NLS-1$ //$NON-NLS-2$
				stackedLayout.topControl = xlsComposite;
				xlsComposite.refresh(node);
			}
			else {
				MessageDialog.openInformation(getShell(), Messages.D4CComposite_2, Messages.D4CComposite_3);
			}
		}
		else {
			stackedLayout.topControl = null;
		}
		stackComposite.layout();
	}

	ISelectionChangedListener resourceListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			CkanResource resource = (CkanResource) ((IStructuredSelection) event.getSelection()).getFirstElement();
			transfo.setSelectedResource(resource);

			showComposite(resource);
		}
	};

	ISelectionChangedListener packageListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			CkanPackage pack = (CkanPackage) ((IStructuredSelection) event.getSelection()).getFirstElement();
			transfo.setSelectedPackage(pack);
			comboResources.setInput(pack.getResources() != null ? pack.getResources().toArray() : new ArrayList<CkanResource>().toArray());
		}
	};

	public void setNode(Node node) {
		this.node = node;
		if (node.getGatewayModel() instanceof D4CInput) {
			this.transfo = (D4CInput) node.getGatewayModel();
		}
	}

	public void aboutToBeHidden() {
		try {
			if (stackedLayout.topControl == csvComposite) {
				FileInputCSV csv = (FileInputCSV) csvComposite.getFileTransformation();
				Server server = transfo.getServer();
				((FileCSV) csv).setServer(server);

//				csv.setDefinition(transfo.getSelectedContract().getId() + ""); //$NON-NLS-1$
				transfo.setFileTransfo(csv);
			}
			else if (stackedLayout.topControl == xlsComposite) {
				FileInputXLS xls = (FileInputXLS) xlsComposite.getFileTransformation();
//				xls.setDefinition(transfo.getSelectedContract().getId() + ""); //$NON-NLS-1$
				transfo.setFileTransfo(xls);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		comboPackages.getCombo().setEnabled(enabled);
		comboResources.getCombo().setEnabled(enabled);
		
		csvComposite.setEnabled(enabled);
		xlsComposite.setEnabled(enabled);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		compositeD4c.setVisible(visible);
		
		stackComposite.setVisible(visible);
	}
}
