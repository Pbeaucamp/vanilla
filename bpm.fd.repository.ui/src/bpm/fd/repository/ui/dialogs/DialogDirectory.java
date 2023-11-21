package bpm.fd.repository.ui.dialogs;

import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.repository.ui.Messages;
import bpm.fd.repository.ui.composites.CompositeDirectory;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;


public class DialogDirectory extends Dialog {

	private RepositoryDirectory dir;
	private CompositeDirectory composite;
	private List<Group> groups;
	private ComboViewer combo;
	private String groupname;
	
	public DialogDirectory(Shell parentShell, List<Group> groups){
		super(parentShell);
		dir = new RepositoryDirectory();
		dir.setDateCreation(new Date());
		this.groups = groups;
	}
	
	public DialogDirectory(Shell parentShell, RepositoryDirectory dir) {
		super(parentShell);
		this.dir = dir;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout());
		
		composite =new CompositeDirectory(parent, SWT.NONE, dir);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.fillData();
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogDirectory_0);
		
		combo = new ComboViewer(parent, SWT.READ_ONLY);
		combo.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		combo.getCombo().addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				groupname = combo.getCombo().getText();
			}
		});
		combo.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		combo.setInput(groups.toArray(new Group[groups.size()]));
		
		
		return composite;
	}
	
	public RepositoryDirectory getDirectory(){
		return dir;
	}

	@Override
	protected void okPressed() {
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
