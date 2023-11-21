package metadata.client.wizards.datasources;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeDataStream;
import metadata.client.model.composites.CompositeDataStreamElement;
import metadata.client.trees.TreeDataStream;
import metadata.client.trees.TreeDataStreamElement;
import metadata.client.trees.TreeParent;
import metadata.client.viewer.TreeContentProvider;
import metadata.client.viewer.TreeLabelProvider;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;

public class PageProperties extends WizardPage {
	
	private TreeViewer viewer;
	private Composite container, details;
	

	protected PageProperties(String pageName) {
		super(pageName);
	}


	public void createControl(Composite parent) {
		//create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(true);


	}
	
	private void createPageContent(Composite parent){
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(Messages.PageProperties_0); //$NON-NLS-1$
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l2.setText(Messages.PageProperties_1); //$NON-NLS-1$
		
		viewer = new TreeViewer(container, SWT.BORDER);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){


			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				Object o = ss.getFirstElement();
				if (o instanceof TreeDataStream){
					//properties details
					if (details != null && !details.isDisposed())
						details.dispose();
					
					IDataStream d = ((TreeDataStream)o).getDataStream();
					
					details = new CompositeDataStream(container, SWT.NONE, viewer,  true, d);
					details.setLayoutData(new GridData(GridData.FILL_BOTH));
					container.layout();
				}
				else if (o instanceof TreeDataStreamElement){
					if (details != null && !details.isDisposed())
						details.dispose();
					
					IDataStreamElement d = ((TreeDataStreamElement)o).getDataStreamElement();
					
					details = new CompositeDataStreamElement(container, SWT.NONE, viewer, true, d);
					details.setLayoutData(new GridData(GridData.FILL_BOTH));
					container.layout();
				}
				
			}
			
		});
		
		
		
		
		details= new Composite(container, SWT.NONE);
		details.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
	}
	
	protected void createModel(TreeParent root){
		viewer.setInput(root);
	}

	@Override
	public IWizardPage getNextPage() {
		((DataSourceWizard)getWizard()).relationsPage.createModel();
		return super.getNextPage();
	}
}
