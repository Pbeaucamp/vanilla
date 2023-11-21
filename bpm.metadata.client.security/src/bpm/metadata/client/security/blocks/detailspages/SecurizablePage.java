package bpm.metadata.client.security.blocks.detailspages;

import metadataclient.Activator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.metadata.ISecurizable;
import bpm.metadata.client.security.compositehelpers.CompositeSecurizableHelper;

public class SecurizablePage implements IDetailsPage{

	protected IManagedForm managedForm;
	private CompositeSecurizableHelper compositeBuilder;
	
	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Section  s = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE);
		s.setLayout(new GridLayout());
		s.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		s.setText("Groups Availibility");
		s.setDescription("Select the groups that will be able to use this BusinessModel");
		s.setExpanded(true);
		Composite client = toolkit.createComposite(s);
		client.setLayout(new GridLayout());
		client.setLayoutData(new GridData(GridData.FILL_BOTH));
		client.addListener(SWT.Selection, new Listener(){

			public void handleEvent(Event event) {
				Activator.getDefault().setChanged();
				
			}
			
		});
		compositeBuilder = new CompositeSecurizableHelper(toolkit, client, SWT.NONE);
		compositeBuilder.createContent(client);
		
		s.setClient(client);
		
//		toolkit.paintBordersFor(composite);
		managedForm.addPart(new SectionPart(s));
		parent.layout();
	}

	public void commit(boolean onSave) {
		
		
	}

	public void dispose() {
		
		
	}

	public void initialize(IManagedForm form) {
		managedForm = form;
		
	}

	public boolean isDirty() {
		
		return false;
	}

	public boolean isStale() {
		
		return false;
	}

	public void refresh() {
		
		
	}

	public void setFocus() {
		
		
	}

	public boolean setFormInput(Object input) {
		
		return false;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ss = (IStructuredSelection)selection;
		
		if (ss.isEmpty()){
			return;
		}
		else{
			compositeBuilder.setSecurizableDatas((ISecurizable)ss.getFirstElement());
		}
		
	}

}
