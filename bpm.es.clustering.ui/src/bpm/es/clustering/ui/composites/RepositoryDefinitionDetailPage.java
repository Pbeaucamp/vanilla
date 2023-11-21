package bpm.es.clustering.ui.composites;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.es.clustering.ui.Messages;
import bpm.vanilla.platform.core.beans.Repository;

public class RepositoryDefinitionDetailPage  implements IDetailsPage{

	
	private IManagedForm managedForm;
	private Text name;
	private Text id;
	private Text url;
	private Text description;
	
	private Repository input;
	
	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		Section  s = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		s.setLayout(new GridLayout());
		s.setLayoutData(new GridData(GridData.FILL_BOTH));
		s.setText(Messages.RepositoryDefinitionDetailPage_0);
		s.setDescription(Messages.RepositoryDefinitionDetailPage_1);
		
		Composite client = toolkit.createComposite(s);
		client.setLayout(new GridLayout(2, false));
		client.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = toolkit.createLabel(client, Messages.RepositoryDefinitionDetailPage_2);
		l.setLayoutData(new GridData());
		
		name = toolkit.createText(client, "", SWT.FLAT); //$NON-NLS-1$
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.setEnabled(false);
		
		l = toolkit.createLabel(client, Messages.RepositoryDefinitionDetailPage_4);
		l.setLayoutData(new GridData());
		
		id = toolkit.createText(client, "", SWT.FLAT); //$NON-NLS-1$
		id.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		id.setEnabled(false);
		
		l = toolkit.createLabel(client, Messages.RepositoryDefinitionDetailPage_6);
		l.setLayoutData(new GridData());
		
		url = toolkit.createText(client, "", SWT.FLAT); //$NON-NLS-1$
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		url.setEnabled(false);
		
		l = toolkit.createLabel(client, Messages.RepositoryDefinitionDetailPage_8);
		l.setLayoutData(new GridData());
		
		description = toolkit.createText(client, "", SWT.FLAT); //$NON-NLS-1$
		description.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		description.setEnabled(false);
		
		s.setClient(client);
		
		toolkit.paintBordersFor(client);
		
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
			input = null;
		}
		else{
			input = (Repository)ss.getFirstElement();
		}
		
		update();
		
	}

	private void update(){
		if (input == null){
			name.setText(""); //$NON-NLS-1$
			id.setText(""); //$NON-NLS-1$
			url.setText(""); //$NON-NLS-1$
			description.setText(""); //$NON-NLS-1$
		}
		else{
			name.setText(input.getName() != null ? input.getName() : ""); //$NON-NLS-1$
			id.setText(input.getId() + ""); //$NON-NLS-1$
			url.setText(input.getUrl() != null ? input.getUrl() : ""); //$NON-NLS-1$
			description.setText(input.getSociete() != null ? input.getSociete() : ""); //$NON-NLS-1$
		}
	}
	
	
}
