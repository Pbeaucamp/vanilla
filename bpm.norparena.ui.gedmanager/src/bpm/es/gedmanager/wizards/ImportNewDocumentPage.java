package bpm.es.gedmanager.wizards;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ImportNewDocumentPage extends WizardPage {

	private Text txtName;
//	private TreeViewer repositoryTree;
	private Text txtFile;
	private Button btnFile;
	
	private String fileName;
	
	protected ImportNewDocumentPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite p) {
		
		Composite parent = new Composite(p, SWT.NONE);
		parent.setLayout(new GridLayout(3, false));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblName = new Label(parent, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL,SWT.FILL, false, false));
		lblName.setText("Name");
		
		txtName = new Text(parent, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		txtName.addModifyListener(listener);
		
		Label lblRep = new Label(parent, SWT.NONE);
		lblRep.setLayoutData(new GridData(SWT.FILL,SWT.FILL, false, false));
		lblRep.setText("Directory");
		
//		repositoryTree = new TreeViewer(parent);
//		repositoryTree.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
//		repositoryTree.setContentProvider(new TreeContentProvider());
//		repositoryTree.setLabelProvider(new TreeLabelProvider() {
//			@Override
//			public String getText(Object element) {
//				return super.getText(element);
//			}
//		});
		
		
		
		Label lblFile = new Label(parent, SWT.NONE);
		lblFile.setLayoutData(new GridData(SWT.FILL,SWT.FILL, false, false));
		lblFile.setText("File");
		
		txtFile = new Text(parent, SWT.BORDER);
		txtFile.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtFile.setEnabled(false);
		txtFile.addModifyListener(listener);
		
		btnFile = new Button(parent, SWT.PUSH);
		btnFile.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnFile.setText("...");
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dial = new FileDialog(ImportNewDocumentPage.this.getShell());
				fileName = dial.open();
				txtFile.setText(fileName);
			}
		});
		
		setControl(parent);
		
//		createInput();
	}


	@Override
	public boolean isPageComplete() {
		if(txtName.getText() != null && !txtName.getText().equals("") 
				&& txtFile.getText() != null && !txtFile.getText().equals("")) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	public void updateWizardButtons() {
		getContainer().updateButtons();
	}
	
//	public void createInput() {
//		
//		try {
//			TreeParent root = new TreeParent(""); //$NON-NLS-1$
//			for(RepositoryDirectory d : Activator.getDefault().getIRepository().getRootDirectories()){
//				TreeDirectory tp = new TreeDirectory(d);
//				root.addChild(tp);
//				ViewTree.buildChilds(tp, false);
//			}
//			repositoryTree.setInput(root);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	ModifyListener listener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			updateWizardButtons();
		}
	};
	
	public String getName() {
		return txtName.getText();
	}
	
	public String getFilename() {
		return txtFile.getText();
	}

//	public int getDirectoryId() {
//		int id = -1;
//		try {
//			id = ((RepositoryDirectory)((TreeParent)((IStructuredSelection)repositoryTree.getSelection()).getFirstElement()).getData()).getId();
//		} catch (Exception e) {
////			e.printStackTrace();
//		}
//		return id;
//	}
}
