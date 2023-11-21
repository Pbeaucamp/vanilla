package bpm.forms.design.ui.views.content.pages;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.Messages;
import bpm.forms.design.ui.composite.CompositeForm;
import bpm.forms.design.ui.composite.CompositeFormDefinition;
import bpm.forms.design.ui.views.content.ContentMasterDetailsBlock;

public class PageFormDefinition implements IDetailsPage{

	
	private CompositeFormDefinition formDefinitionComposite;
	private Button ok;
	private Button cancel;
	
	private ContentMasterDetailsBlock masterBlock;
	private boolean dirty = false;
	private IManagedForm form;
	
	public PageFormDefinition(ContentMasterDetailsBlock masterBlock){
		this.masterBlock = masterBlock;
	}
	@Override
	public void createContents(Composite parent) {
	
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		formDefinitionComposite.createContent(parent);
		formDefinitionComposite.getClient().setLayoutData(new GridData(GridData.FILL_BOTH));
		formDefinitionComposite.getClient().addListener(SWT.Modify, new Listener(){

			@Override
			public void handleEvent(Event event) {
				dirty = true;
				form.dirtyStateChanged();
				masterBlock.addDirtyObject(formDefinitionComposite.getInput());
				
			}
			
		});
	}

	@Override
	public void commit(boolean onSave) {
		if (!onSave){
			return;
		}
		try{
			Activator.getDefault().getServiceProvider().getDefinitionService().update(formDefinitionComposite.getInput());
			masterBlock.removeDirtyObject(formDefinitionComposite.getInput());
			dirty = false;
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(formDefinitionComposite.getClient().getShell(), "Error when performing update", ex.getMessage()); //$NON-NLS-1$
		}
	}

	@Override
	public void dispose() {
		formDefinitionComposite.getClient().dispose();
		
	}

	@Override
	public void initialize(IManagedForm form) {
		this.form = form;
		formDefinitionComposite = new CompositeFormDefinition(form.getToolkit());
		
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isStale() {
		
		return false;
	}

	@Override
	public void refresh() {
		
		
	}

	@Override
	public void setFocus() {
		
		
	}

	@Override
	public boolean setFormInput(Object input) {
		
		return false;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		if (selection.isEmpty()){
			formDefinitionComposite.getClient().setVisible(false);
		}
		
		Object o = ((IStructuredSelection)selection).getFirstElement();
		if (o instanceof IFormDefinition){
			formDefinitionComposite.setInput((IFormDefinition)o);
			formDefinitionComposite.getClient().setVisible(true);
			formDefinitionComposite.setEnabled(!((IFormDefinition)o).isDesigned());
			
		}
		else{
			formDefinitionComposite.getClient().setVisible(false);
		}
		
		
	}

}
