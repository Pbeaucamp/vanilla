package bpm.es.dndserver.views.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.es.dndserver.Messages;

public class DialogCreateDirectory extends Dialog {

	private Text tname;
	private String name;
	private Combo groupList;
	
	private String selectedGroup;
	
	private List<String> availableGroups;
	
	public DialogCreateDirectory(Shell parentShell, String name, List<String> groups) {
		super(parentShell);
		
		this.availableGroups = groups;
//		for (Group gr : groups) {
//			availableGroups.put(gr.getName(), gr);
//		}
		
	}

//	@Override
//	protected void initializeBounds() {
//		getShell().setSize(500, 270);
//	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(1, false));
		main.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label lname = new Label(main, SWT.NONE);
		lname.setText(Messages.DialogCreateDirectory_0);
		
		tname = new Text(main, SWT.BORDER);
		tname.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		tname.setText(""); //$NON-NLS-1$
		
		Label lgroup = new Label(main, SWT.NONE);
		lgroup.setText(Messages.DialogCreateDirectory_2);
		
		groupList = new Combo(main, SWT.BORDER);
		groupList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		groupList.setItems(availableGroups.toArray(new String[0]));
		groupList.select(0);
		
		return main;
	}
	
	protected void okPressed() {	
		name = tname.getText();
		
		if (tname == null || tname.equals("")) { //$NON-NLS-1$
			MessageDialog.openError(getShell(), Messages.DialogCreateDirectory_4, Messages.DialogCreateDirectory_5);
			return;
		}
		
		selectedGroup = groupList.getText();
		//selectedGroup = availableGroups.get(groupName);
		
		super.okPressed();
	}
	
	public String getName() {
		return name;
	}
	
	public String getSelectedGroup() {
		return selectedGroup;
	}
}

