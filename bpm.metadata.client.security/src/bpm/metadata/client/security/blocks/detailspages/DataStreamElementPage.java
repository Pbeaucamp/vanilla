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
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.metadata.client.security.compositehelpers.CompositeVisbilityHelper;
import bpm.metadata.layer.logical.IDataStreamElement;

public class DataStreamElementPage extends SecurizablePage{
	private CompositeVisbilityHelper visibilityBuilder;
	
	public void createContents(Composite parent) {
		super.createContents(parent);
		
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
		visibilityBuilder = new CompositeVisbilityHelper(toolkit, client, SWT.NONE);
		s.setExpanded(true);
		
		s.setClient(client);
		
//		toolkit.paintBordersFor(composite);
		managedForm.addPart(new SectionPart(s));
		parent.layout();

	}
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		IStructuredSelection ss = (IStructuredSelection)selection;
		
		visibilityBuilder.setDatas((IDataStreamElement)ss.getFirstElement());
	}
}
