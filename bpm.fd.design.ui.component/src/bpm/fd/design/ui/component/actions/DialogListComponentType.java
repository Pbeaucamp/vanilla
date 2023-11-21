package bpm.fd.design.ui.component.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.wizards.NewWizardRegistry;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.wizard.IWizardComponent;
import bpm.fd.repository.ui.Activator;

public class DialogListComponentType extends Dialog{

	
	private TableViewer list;
	private IWizard wiz;
	
	public DialogListComponentType(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() |  SWT.RESIZE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		list = new TableViewer(parent, SWT.BORDER);
		list.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		list.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List l = (List)inputElement;
				return l.toArray(new Object[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
		});
		list.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				
				return ((IWizardDescriptor)element).getLabel();
			}
			
			@Override
			public Image getImage(Object element) {
				if (((IWizardDescriptor)element).getImageDescriptor() != null){
					return ((IWizardDescriptor)element).getImageDescriptor().createImage();				
				}
				return null;
			
			}
			
		});
		
		fillViewer();
		
		return list.getControl();
	}
	
	private void fillViewer(){
		List<IWizardDescriptor> descs = new ArrayList<IWizardDescriptor>();
		
		for(IWizardCategory c : NewWizardRegistry.getInstance().getRootCategory().getCategories()){
			if (c.getId().equals("bpm.fd.design.ui.freedashComponentCategory")){ //$NON-NLS-1$
				for(IWizardDescriptor d : c.getWizards()){
					descs.add(d);
				}			
			}
			
		}
		
		list.setInput(descs);
	}

	public IWizard getWizard(){
		return wiz;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		if (list.getSelection().isEmpty()){
			return;
		}
		IWizardDescriptor d = (IWizardDescriptor)((IStructuredSelection)list.getSelection()).getFirstElement();
		
		try {
			IWorkbenchWizard _w = d.createWizard();
			
			if (_w instanceof IWizardComponent){
				/*
				 * some components need an active connection to a VanillaRepository
				 * to e defined, if this is one of those components
				 * we launch a connection to the repository if needed
				 */
				if (((IWizardComponent)_w).needRepositoryConnections()){
					
					if (bpm.fd.repository.ui.Activator.getDefault().getRepositoryConnection() == null){
//						DialogConnect dial = new DialogConnect(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
//						if (dial.open() != DialogConnect.OK){
//							return;
//						}
						
//						SampleAction connectAction = new SampleAction();
//						connectAction.init(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow());
//						connectAction.run(null);
						
						
						IHandlerService handlerService = (IHandlerService) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
						try {
							handlerService.executeCommand("bpm.repository.ui.commands.connection", null);
						} catch (Exception e) {
							
							e.printStackTrace();
						} 
						
						if (bpm.fd.repository.ui.Activator.getDefault().getRepositoryConnection() == null){
							MessageDialog.openInformation(getShell(), Messages.DialogListComponentType_1, Messages.DialogListComponentType_2);
							return;
						}
						
					}
				}
			}
			
			wiz = _w;
		} catch (CoreException e) {
			e.printStackTrace();
		}
			
		
			
			
		super.okPressed();
	}
		
		
		
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogListComponentType_3);
		getShell().setSize(400, 300);
	}
	
//	IWizard wiz = null;
//	
//	for(IWizardCategory c : NewWizardRegistry.getInstance().getRootCategory().getCategories()){
//		if (c.getId().equals("bpm.fd.design.ui.freedashComponentCategory")){
//			for(IWizardDescriptor d : c.getWizards()){
//				try {
//					IWorkbenchWizard _w = d.createWizard();
//					
//					if (_w instanceof IWizardComponent){
//						if (((IWizardComponent)_w).getComponentClass() == newElement){
//							wiz = _w;
//							break;
//						}
//					}
//				} catch (CoreException e) {
//					e.printStackTrace();
//				}
//			}			
//		}
//		
//	}
//	
//	if (wiz == null){
//		MessageDialog.openWarning(shell, "Unable to find the correct Wizard", "It seems that no Wizard is registered for this component type. The component might not been implemented yet or this is a bug.");
//		return;
//	}
//	
//
//	WizardDialog d = new WizardDialog(shell, wiz);
//	d.setMinimumPageSize(800, 600);
//	if (d.open() == WizardDialog.OK){
//		ceatedComponent = ((IWizardComponent)wiz).getComponent();
//		((Cell)parent).addBaseElementToContent(ceatedComponent);
//		
//	}
}

