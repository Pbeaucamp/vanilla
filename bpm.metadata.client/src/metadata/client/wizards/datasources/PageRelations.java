package metadata.client.wizards.datasources;

import java.util.ArrayList;
import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.model.dialog.DialogForeignKeyImporter;
import metadata.client.model.dialog.DialogRelation;
import metadata.client.trees.TreeJoin;
import metadata.client.trees.TreeParent;
import metadata.client.trees.TreeRelation;
import metadata.client.viewer.TreeContentProvider;
import metadata.client.viewer.TreeLabelProvider;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.logical.sql.SQLDataSource;



public class PageRelations extends WizardPage {

	private TreeViewer viewer;
	
	protected PageRelations(String pageName) {
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
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(4, true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Button b = new Button(container, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		b.setText(Messages.PageRelations_0); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IDataSource ds = ((DataSourceWizard)getWizard()).dataSource;
				DialogRelation d = null;
				
				d = new DialogRelation(getShell(), ds);
				
				
				if (d.open() == Dialog.OK){
					((DataSourceWizard)getWizard()).dataSource.addRelation(d.getRelation());
					
				}
				createModel();
			}
			
		});
	
		Button b2 = new Button(container, SWT.PUSH);
		b2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		b2.setText(Messages.PageRelations_1); //$NON-NLS-1$
		b2.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IDataSource ds = ((DataSourceWizard)getWizard()).dataSource;
				DialogRelation d = null;
				
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				if (ss.getFirstElement() instanceof TreeRelation){
					d = new DialogRelation(getShell(), ds, ((TreeRelation)ss.getFirstElement()).getRelation());
					d.open();
					createModel();
				}
				
				
			}
		});
		
		b2 = new Button(container, SWT.PUSH);
		b2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		b2.setText(Messages.PageRelations_3); 
		b2.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				IDataSource ds = ((DataSourceWizard)getWizard()).dataSource;
				for(Object o : ss.toList()){
					if (o instanceof TreeRelation){
						ds.removeRelation(((TreeRelation)o).getRelation());
					}
					createModel();
				}
			}
		});
		
		b2 = new Button(container, SWT.PUSH);
		b2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		b2.setText(Messages.PageRelations_4); 
		b2.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IDataSource ds = ((DataSourceWizard)getWizard()).dataSource;
				DialogForeignKeyImporter dial = new DialogForeignKeyImporter(getShell(), ((SQLDataSource)ds));
				if (dial.open() == DialogForeignKeyImporter.OK){
					List<IDataStream> _t = new ArrayList<IDataStream>();
					for(Relation r : dial.getRelations()){
						_t.clear();
						_t.add(r.getLeftTable());
						_t.add(r.getRightTable());
						if (((SQLDataSource)ds).getRelations(_t).isEmpty()){
							ds.addRelation(r);
						}
					}
					createModel();
				}
			}
		});
		
		
		
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 4, 1));
		l.setText(Messages.PageRelations_2); //$NON-NLS-1$
		
		viewer = new TreeViewer(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 4, 1));
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setContentProvider(new TreeContentProvider());
		
		
	}
	
	protected void createModel(){
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		IDataSource ds = ((DataSourceWizard)getWizard()).dataSource;
		
		for(Relation r : ds.getRelations()){
			TreeRelation tr = new TreeRelation(r);
			
			for(Join j : r.getJoins()){
				tr.addChild(new TreeJoin(j));
			}
			root.addChild(tr);
		}
		
		viewer.setInput(root);
	}


	@Override
	public boolean isPageComplete() {
		return false;
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	@Override
	public IWizardPage getNextPage() {
		return null;
	}
}
