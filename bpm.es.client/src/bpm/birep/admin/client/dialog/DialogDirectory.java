package bpm.birep.admin.client.dialog;

import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Messages;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class DialogDirectory extends Dialog {

	private RepositoryDirectory dir;
	private CompositeDirectory composite;
	private List<Group> groups;
	private Combo combo;
	private String groupname;
	private int type = -1;
	
	public DialogDirectory(Shell parentShell, List<Group> groups, int type){
		super(parentShell);
		dir = new RepositoryDirectory();
		dir.setDateCreation(new Date());
		this.groups = groups;
		this.type = type;
	}
	
	public DialogDirectory(Shell parentShell, RepositoryDirectory dir) {
		super(parentShell);
		this.dir = dir;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout());
		
		composite =new CompositeDirectory(parent, SWT.NONE, dir, type);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.fillData();
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogDirectory_0);
		
		combo = new Combo(parent, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		for(Group s : groups)
			combo.add(s.getName());
		
		combo.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				groupname = combo.getText();
			}
			
		});
		return composite;
	}
	
	public RepositoryDirectory getDirectory(){
		return dir;
	}

	@Override
	protected void okPressed() {
//		if (combo.getText().equals("")){
//			MessageDialog.openInformation(getShell(), "Information", "Please select the group in which you want to add this Directory");
//			return;
//		}
		composite.setDirectory();
		super.okPressed();
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogDirectory_1);
		getShell().setSize(400, 300);
	}

	public String getGroupName(){
		return groupname;
	}
}
