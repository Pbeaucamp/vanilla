package metadata.client.wizards.datasources;

import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.trees.TreeCubeOlap;
import metadata.client.trees.TreeParent;
import metadata.client.trees.TreeTable;
import metadata.client.viewer.TreeContentProvider;
import metadata.client.viewer.TreeLabelProvider;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.physical.ITable;
import bpm.metadata.layer.physical.olap.UnitedOlapConnection;

public class PageOlap extends WizardPage {
	
	private TreeViewer tree;
	private UnitedOlapConnection selectedOlap;
	private Text name;
	private Text selected;
	private String dsname;

	public PageOlap(String pageName) {
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
	
	private void createPageContent(Composite composite){
		Composite container = new Composite(composite, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l0 = new Label(container, SWT.NONE);
		l0.setLayoutData(new GridData());
		l0.setText(Messages.PageOlap_0); //$NON-NLS-1$
		
		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				
				dsname = name.getText();
				getContainer().updateButtons();
			}
			
		});

		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l.setText(Messages.PageOlap_1); //$NON-NLS-1$
		
				
		tree = new TreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		tree.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 4));
		tree.setContentProvider(new TreeContentProvider());
		tree.setLabelProvider(new TreeLabelProvider());
		tree.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)tree.getSelection();
				
				if (ss.isEmpty()){
					return;
				}
				getContainer().updateButtons();
			}
			
		});
		
		Button b = new Button(container, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		b.setText(Messages.PageOlap_2); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection ss = (StructuredSelection)tree.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeCubeOlap)){
					return;
				}
				selectedOlap = ((TreeCubeOlap)ss.getFirstElement()).getColumn();
				selected.setText(selectedOlap.getCubeName());
				getContainer().updateButtons();
			}
			
		});
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(Messages.PageOlap_3); //$NON-NLS-1$

		selected = new Text(container, SWT.BORDER);
		selected.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		selected.setEnabled(false);

	}
	
	public void createModel(List<UnitedOlapConnection> l) throws Exception{
				
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		
		for(UnitedOlapConnection s : l){
			TreeCubeOlap tp = new TreeCubeOlap(s);
			for(ITable t : s.connect()){
				TreeTable tt = new TreeTable(t);
				tp.addChild(tt);
			}
			root.addChild(tp);
		}
		
		
		tree.setInput(root);		
	}

	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	
	
	
	@Override
	public boolean isPageComplete() {
		if (getWizard().getPreviousPage(this) instanceof PageType){
			if (((PageType)getWizard().getPreviousPage(this)).getType() == 1){
				return !(name.getText().equals("") || selectedOlap == null);
			}
			else{
				return true;
			}
				
		}
		
		return true;
		
		
	}

	public UnitedOlapConnection getOlapConnection(){
		return selectedOlap;
	}
	
	public String getName(){
		return dsname;
	}

	@Override
	protected boolean isCurrentPage() {
		return super.isCurrentPage();
	}
}
