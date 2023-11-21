package bpm.gateway.ui.views.property.sections.vanilla;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.vanilla.FreedashFormInput;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogRepositoryObject;
import bpm.gateway.ui.viewer.TreeDirectory;
import bpm.gateway.ui.viewer.TreeItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FDFormInputDefinitionSection extends AbstractPropertySection{
	private FreedashFormInput transfo;
	private RepositoryItem item;
	
	private Text directoryItem;
	private Button browser;
	private static final String FORM_MODEL_TYPE_NAME = "bpm.fd.api.core.model.FdVanillaFormModel"; //$NON-NLS-1$
	private ViewerFilter filter = new ViewerFilter(){
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof TreeItem){
				RepositoryItem it = (RepositoryItem)((TreeItem)element).getItem();
				try{
					return FORM_MODEL_TYPE_NAME.equals(it.getModelType().trim());
				}catch(NullPointerException ex){
					return false;
				}
			}
			else if (element instanceof TreeDirectory){
				return true;
			}
			return false;
			
		}
	};
	
	
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        Assert.isTrue(((Node)((NodePart) input).getModel()).getGatewayModel() instanceof FreedashFormInput);
        this.transfo = (FreedashFormInput)(((Node)((NodePart) input).getModel()).getGatewayModel());
	}
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		
		Label l = getWidgetFactory().createLabel(composite, Messages.FDFormInputDefinitionSection_1);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		directoryItem = getWidgetFactory().createText(composite, "", SWT.BORDER); //$NON-NLS-1$
		directoryItem.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		directoryItem.setEnabled(false);
		
		browser = getWidgetFactory().createButton(composite, "...", SWT.PUSH); //$NON-NLS-1$
		browser.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browser.setEnabled(false);
		browser.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogRepositoryObject dial = new DialogRepositoryObject(
						getPart().getSite().getShell(), 
						IRepositoryApi.FD_TYPE);
				dial.addViewerFilter(filter);
				if (dial.open() == Dialog.OK){
					item = dial.getRepositoryItem();
					directoryItem.setText(item.getItemName());
					transfo.setDirectoryItemId(item.getId());
					
					try {
						loadForm();
						transfo.setDirectoryItem(item);
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
					
					
				}
			}
			
		});
	
	}
	
	
	
	
	@Override
	public void refresh() {

		browser.setEnabled(Activator.getDefault().getRepositoryContext() != null);
		
		if (transfo.getDirectoryItem() == null || (item != null && transfo.getDirectoryItem().getId() != item.getId())){
			try{
				loadForm();
				directoryItem.setText(transfo.getDirectoryItem().getItemName());
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getPart().getSite().getShell(), Messages.FDFormInputDefinitionSection_4, ex.getMessage());
			}
			
			
		}
		else if (item != null){
			directoryItem.setText(item.getItemName());
		}
		else if (transfo.getDirectoryItem() != null){
			directoryItem.setText(transfo.getDirectoryItem().getItemName());
		}
				
	}
	
	private void loadForm(){
		
		if (transfo.getDirectoryItemId() == null || transfo.getDirectoryItemId() <= 0){
			return;
		}
		IRunnableWithProgress r = new IRunnableWithProgress(){

			public void run(IProgressMonitor monitor)throws InvocationTargetException, InterruptedException {
				
				monitor.setTaskName(Messages.FDFormInputDefinitionSection_5);
				monitor.beginTask(Messages.FDFormInputDefinitionSection_6, 1);
				
				try{
					transfo.getDocument().getFreeDashHelper().createDescriptor(transfo);
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getPart().getSite().getShell(), Messages.FDFormInputDefinitionSection_7, ex.getMessage());
				}
				
				
				
			}
			
		};
		
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
	     try {
	    	 service.run(true, false, r);
	    	 	    	 
	     } catch (InvocationTargetException e) {
	        e.printStackTrace();
	     } catch (InterruptedException e) {
	        e.printStackTrace();
	     }
	}
}
