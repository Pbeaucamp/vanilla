package bpm.birep.admin.client.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import adminbirep.Messages;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Role;

public class DialogRole extends Dialog {

	private Button create, update, delete, read, connect, execute, authorized, historic;
	private ListViewer viewer;
	private List<Role> list = new ArrayList<Role>();
	private Text name;
	
	public DialogRole(Shell parentShell) {
		super(parentShell);
		
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		name = new Text(c, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		viewer = new ListViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		viewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setLabelProvider(new LabelProvider());
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				return (String[]) inputElement;
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		viewer.setInput(IRepositoryApi.TYPES_NAMES);
		
		Composite cc = new Composite(c, SWT.NONE);
		cc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		cc.setLayout(new GridLayout());
		
		connect = new Button(cc, SWT.CHECK);
		connect.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		connect.setText(Messages.DialogRole_0);
		
		create = new Button(cc, SWT.CHECK);
		create.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		create.setText(Messages.DialogRole_1);
		
		read = new Button(cc, SWT.CHECK);
		read.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		read.setText(Messages.DialogRole_2);
		
		update = new Button(cc, SWT.CHECK);
		update.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		update.setText(Messages.DialogRole_3);
		
		delete = new Button(cc, SWT.CHECK);
		delete.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		delete.setText(Messages.DialogRole_4);
		

		execute = new Button(cc, SWT.CHECK);
		execute.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		execute.setText(Messages.DialogRole_5);		
		
		historic = new Button(cc, SWT.CHECK);
		historic.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		historic.setText(Messages.DialogRole_6);		

		
		return c;
	}
	

	public List<Role> getRoles(){
		return list;
	}

	@Override
	protected void okPressed() {
		
		IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
		
		for(Object o : ss.toList()){
			if (o instanceof String){
				Role r = new Role();
				r.setType((String)o);
				r.setGrants(getGrants());
				r.setName(name.getText() + (String)o);
				list.add(r);
			}
		}
		
		super.okPressed();
	}
	
	
	public String getGrants() {
		String s = ""; //$NON-NLS-1$
		if (connect.getSelection()){
			s += "A"; //$NON-NLS-1$
		}
		
		if (create.getSelection()){
			s +=  "C"; //$NON-NLS-1$
		}
		
		if (update.getSelection()){
			s += "U"; //$NON-NLS-1$
		}
		
		if (delete.getSelection()){
			s += "D"; //$NON-NLS-1$
		}
		
		if (read.getSelection()){
			s += "R"; //$NON-NLS-1$
		}
		
		if (execute.getSelection()){
			s +=  "E"; //$NON-NLS-1$
		}

		return s;
	}
}
