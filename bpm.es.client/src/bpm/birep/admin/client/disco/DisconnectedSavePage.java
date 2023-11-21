package bpm.birep.admin.client.disco;

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import adminbirep.Messages;
import bpm.vanilla.platform.core.beans.Group;


public class DisconnectedSavePage extends WizardPage{

	private Combo cbGroups;
	private Text txtLimit, txtPath;
	
	private List<Group> groups;
	
	public DisconnectedSavePage(String pageName, List<Group> groups) {
		super(pageName);
		this.groups = groups;
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Label lblGroup = new Label(main, SWT.NONE);
		lblGroup.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblGroup.setText(Messages.DisconnectedSavePage_0);
		
		cbGroups = new Combo(main, SWT.READ_ONLY);
		cbGroups.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		for(Group grp : groups) {
			cbGroups.add(grp.getName());
		}
		cbGroups.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				
				getContainer().updateButtons();
			}
		});
		
		Label lblLimit = new Label(main, SWT.NONE);
		lblLimit.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblLimit.setText(Messages.DisconnectedSavePage_1);
		
		txtLimit = new Text(main, SWT.BORDER);
		txtLimit.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		txtLimit.setText(Messages.DisconnectedSavePage_2);
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DisconnectedSavePage_3);
		
		txtPath = new Text(main, SWT.BORDER);
		txtPath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtPath.addModifyListener(listener);
		
		Button btnChooseDir = new Button(main, SWT.PUSH);
		btnChooseDir.setText("..."); //$NON-NLS-1$
		btnChooseDir.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirDial = new DirectoryDialog(getShell());
				dirDial.setFilterPath("C:\\"); //$NON-NLS-1$
				
				String path = dirDial.open();
				if(path != null) {
					txtPath.setText(path);
				}
				
				getContainer().updateButtons();
			}
		});

		setControl(main);
	}

	@Override
	public boolean isPageComplete() {
		return !txtPath.getText().isEmpty() && !getGroupName().isEmpty();
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	
	public String getSelectedPath() {
		return txtPath.getText();
	}
	
	public String getGroupName() {
		return cbGroups.getText();
	}
	
	public int getLimitRows() {
		try {
			return Integer.parseInt(txtLimit.getText());
		} catch(Exception e) {
			return 1000;
		}
	}
	
	private ModifyListener listener = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
		}
	};

}
