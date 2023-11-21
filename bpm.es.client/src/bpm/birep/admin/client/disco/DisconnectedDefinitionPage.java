package bpm.birep.admin.client.disco;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import adminbirep.Messages;


public class DisconnectedDefinitionPage extends WizardPage{
	
	private Text name;
	private Text description;
//	private Button btnMobile, btnOffline;
	
//	private List<RepositoryItem> items;
	
	public DisconnectedDefinitionPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DisconnectedDefinitionPage_0);
		
		name = new Text(main, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(listener);

//		Composite group = new Composite(main, SWT.NONE);
//		group.setLayout(new GridLayout(2, true));
//		group.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
//
//		btnMobile = new Button(group, SWT.RADIO);
//		btnMobile.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		btnMobile.setText("Package Mobile");
//
//		btnOffline = new Button(group, SWT.RADIO);
//		btnOffline.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		btnOffline.setText("Package Offline");
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 1));
		l2.setText(Messages.DisconnectedDefinitionPage_1);
		
		description = new Text(main, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
//		if(containsNotReport(items)) {
//			btnMobile.setEnabled(false);
//			btnOffline.setSelection(true);
//		}
//		else {
//			btnMobile.setSelection(true);
//		}
		
		setControl(main);
	}

//	private boolean containsNotReport(List<RepositoryItem> items2) {
//		if(items != null) {
//			for(RepositoryItem item : items) {
//				if(item.getType() == IRepositoryApi.FD_TYPE
//							|| item.getType() == IRepositoryApi.FD_DICO_TYPE
//							|| item.getType() == IRepositoryApi.FASD_TYPE
//							|| item.getType() == IRepositoryApi.EXTERNAL_DOCUMENT
//							|| item.getType() == IRepositoryApi.EXTERNAL_FILE) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}

	@Override
	public boolean isPageComplete() {
		return !name.getText().isEmpty();
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}
	
	public String getSelectedName() {
		return name.getText();
	}
	
	public String getDescription() {
		return description.getText();
	}
	
//	public boolean isMobile() {
//		return btnMobile.getSelection();
//	}
	
	private ModifyListener listener = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
		}
	};

}
