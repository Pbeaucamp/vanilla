package bpm.metadata.client.security;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import bpm.metadata.MetaData;
import bpm.metadata.client.security.blocks.SecurityMasterDetailBlock;
import bpm.metadata.client.security.composite.CompositeClientView;

public class SecurityView extends ViewPart {

	public  static final String ID = "bpm.metadata.client.security.SecurityView";
	private FormToolkit formToolkit;
	private ScrolledForm form;
	private SecurityMasterDetailBlock block;
	private CompositeClientView clientView;
	
	public SecurityView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		
		formToolkit = new FormToolkit(parent.getDisplay());
		
		final CTabFolder folder = new CTabFolder(parent, SWT.BORDER | SWT.BOTTOM);
		folder.setLayout(new GridLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		form = formToolkit.createScrolledForm(folder);
		form.setLayout(new GridLayout());
		
//		formToolkit.decorateFormHeading(form.getForm());

		CTabItem it = new CTabItem(folder, SWT.NONE);
		it.setText("Security Design");
		
		block = new SecurityMasterDetailBlock();
		ManagedForm managedForm = new ManagedForm(formToolkit, form);
		block.createContent(managedForm);
		
		it.setControl(form);
		folder.setSelection(it);
		
		final CTabItem it2 = new CTabItem(folder, SWT.NONE);
		it2.setText("User View");
				
		clientView = new CompositeClientView(folder, SWT.NONE, formToolkit);
		clientView.setLayoutData(new GridData(GridData.FILL_BOTH));
		it2.setControl(clientView);
		
		folder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (folder.getSelection() == it2){
					clientView.loadGroups();
					getSite().setSelectionProvider(clientView.getViewer());
				}
				else{
					getSite().setSelectionProvider(block.getViewer());
				}
			}
		});
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(new IPartListener() {
			
			public void partOpened(IWorkbenchPart part) {
				
				
			}
			
			public void partDeactivated(IWorkbenchPart part) {
				
				
			}
			
			public void partClosed(IWorkbenchPart part) {
				
				
			}
			
			public void partBroughtToTop(IWorkbenchPart part) {
				
				
			}
			
			public void partActivated(IWorkbenchPart part) {
				
				setModel(metadataclient.Activator.getDefault().getModel());
								
			}
		});
	}

	@Override
	public void setFocus() {
		

	}

	public void setModel(MetaData model){
		block.setFmdt(model);
	}
}
