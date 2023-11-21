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

import bpm.metadata.client.security.compositehelpers.CompositeFilteredHelper;
import bpm.metadata.layer.logical.IDataStream;

public class DataStreamPage implements IDetailsPage{
	private CompositeFilteredHelper helper;
	private IManagedForm managedForm;
	
	public void createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		FormToolkit toolkit = managedForm.getToolkit();
		
		Section  s = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE);
		s.setLayout(new GridLayout());
		s.setLayoutData(new GridData(GridData.FILL_BOTH));
		s.setText("Groups Visibilty");
		s.setDescription("Select the groups that will be able to see this DataStreamElement values");
		
		Composite client = toolkit.createComposite(s);
		client.setLayout(new GridLayout());
		client.setLayoutData(new GridData(GridData.FILL_BOTH));
		client.addListener(SWT.Selection, new Listener(){

			public void handleEvent(Event event) {
				Activator.getDefault().setChanged();
				
			}
			
		});
		helper = new CompositeFilteredHelper(toolkit, client, SWT.NONE);
		
		helper.createFilters(client);
		
		s.setExpanded(true);
		
		s.setClient(client);
		
//		toolkit.paintBordersFor(composite);
		managedForm.addPart(new SectionPart(s));
		
		parent.layout();

	}
	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ss = (IStructuredSelection)selection;
		helper.setFiltered((IDataStream)ss.getFirstElement());
	}
	public void commit(boolean onSave) {
		
		
	}
	public void dispose() {
		
		
	}
	public void initialize(IManagedForm form) {
		this.managedForm = form;
		
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
}
