package bpm.gateway.ui.views.property.sections;

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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.webservice.WebServiceVanillaInput;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.resource.service.wizard.ServiceDefinitionWizard;
import bpm.vanilla.platform.core.beans.service.ServiceTransformationDefinition;

public class WebServiceVanillaInputSection extends AbstractPropertySection {

	private ComboViewer methods;
	private Button addService, editService, deleteService;

	private WebServiceVanillaInput webServiceVanillaTransfo;
	private List<ServiceTransformationDefinition> serviceDefinitions;
	
	public WebServiceVanillaInputSection() {

	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.webServiceVanillaTransfo = (WebServiceVanillaInput) ((Node) ((NodePart) input).getModel()).getGatewayModel();
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(3, false));

		addService = getWidgetFactory().createButton(composite, Messages.WebServiceVanillaInputSection_0, SWT.PUSH);
		addService.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		addService.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Activator.getDefault().getRepositoryContext() == null){
					MessageDialog.openInformation(getPart().getSite().getShell(), Messages.WebServiceVanillaInputSection_1, Messages.FmdtInputSection_17);
					return;
				}
				
				ServiceDefinitionWizard wizard = new ServiceDefinitionWizard(webServiceVanillaTransfo, null);

				WizardDialog dialog = new WizardDialog(getPart().getSite().getShell(), wizard);
				dialog.create();
				dialog.getShell().setSize(800, 600);
				dialog.getShell().setText("Service Definition Wizard"); //$NON-NLS-1$
				
				if (dialog.open() == WizardDialog.OK){
					loadServiceTransformation();
				}
			}
		});

		editService = getWidgetFactory().createButton(composite, Messages.WebServiceVanillaInputSection_2, SWT.PUSH);
		editService.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		editService.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Activator.getDefault().getRepositoryContext() == null){
					MessageDialog.openInformation(getPart().getSite().getShell(), Messages.WebServiceVanillaInputSection_3, Messages.FmdtInputSection_17);
					return;
				}

				ServiceTransformationDefinition definition = (ServiceTransformationDefinition) ((IStructuredSelection)methods.getSelection()).getFirstElement();
				if(definition != null){
					ServiceDefinitionWizard wizard = new ServiceDefinitionWizard(webServiceVanillaTransfo, definition);
	
					WizardDialog dialog = new WizardDialog(getPart().getSite().getShell(), wizard);
					dialog.create();
					dialog.getShell().setSize(800, 600);
					dialog.getShell().setText("Service Definition Wizard"); //$NON-NLS-1$
					
					if (dialog.open() == WizardDialog.OK){
						loadServiceTransformation();
					}
				}
			}
		});

		deleteService = getWidgetFactory().createButton(composite, Messages.WebServiceVanillaInputSection_4, SWT.PUSH);
		deleteService.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		deleteService.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Activator.getDefault().getRepositoryContext() == null){
					MessageDialog.openInformation(getPart().getSite().getShell(), Messages.WebServiceVanillaInputSection_5, Messages.FmdtInputSection_17);
					return;
				}

				ServiceTransformationDefinition definition = (ServiceTransformationDefinition) ((IStructuredSelection)methods.getSelection()).getFirstElement();
				if(definition != null){
					try {
						webServiceVanillaTransfo.getDocument().getWebServiceVanillaHelper().deleteService(definition);
						loadServiceTransformation();
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getPart().getSite().getShell(), Messages.WebServiceVanillaInputSection_6, Messages.WebServiceVanillaInputSection_7 + e1.getMessage());
					}

					webServiceVanillaTransfo.setWebServiceDefinitionId(0);
				}
			}
		});

		Label l = getWidgetFactory().createLabel(composite, Messages.WebServiceVanillaInputSection_8);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		methods = new ComboViewer(composite, SWT.READ_ONLY); //$NON-NLS-1$
		methods.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		methods.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((ServiceTransformationDefinition)element).getName();
			}
		});
		methods.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ServiceTransformationDefinition definition = (ServiceTransformationDefinition) ((IStructuredSelection)methods.getSelection()).getFirstElement();
				if(definition != null){
					webServiceVanillaTransfo.setWebServiceDefinitionId(definition.getId());
				}
			}
		});
		methods.setContentProvider(new IStructuredContentProvider() {

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List l = (List) inputElement;
				return l.toArray(new Object[l.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});
	}

	private void loadServiceTransformation() {
		try {
			serviceDefinitions = webServiceVanillaTransfo.getDocument().getWebServiceVanillaHelper().getMethodNames();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(serviceDefinitions != null){
			methods.setInput(serviceDefinitions);
			
			if(webServiceVanillaTransfo.getWebServiceDefinitionId() > 0){
				int i=0;
				for (ServiceTransformationDefinition def : serviceDefinitions) {
					if (def.getId() == webServiceVanillaTransfo.getWebServiceDefinitionId()) {
						methods.getCombo().select(i);
						break;
					}
					i++;
				}
			}
		}
	}

	@Override
	public void refresh() {
		
		if (Activator.getDefault().getRepositoryContext() == null){
			MessageDialog.openInformation(getPart().getSite().getShell(), Messages.WebServiceVanillaInputSection_9, Messages.FmdtInputSection_17);
			return;
		}
		
		loadServiceTransformation();
	}
}
