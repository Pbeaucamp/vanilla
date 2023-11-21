package bpm.birep.admin.client.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.views.ViewGroup;
import bpm.birep.admin.client.views.ViewRole;
import bpm.vanilla.platform.core.beans.Role;

public class CompositeRole extends Composite {

	private Button create, update, delete, read, connect, execute, historic;
	private Text name, application;
	
	private Role role = new Role();
	
	public CompositeRole(Composite parent, int style) {
		super(parent, style);
		buildContent();
	}
	
	public CompositeRole(Composite parent, int style, Role role) {
		super(parent, style);
		this.role = role;
		buildContent();
		fillDatas();
	}

	private void fillDatas(){
		if (role == null){
			return;
		}
		application.setText(role.getType());
		name.setText(role.getName());
		connect.setSelection(role.isAuthorized());
		create.setSelection(role.canCreate());
		update.setSelection(role.canUpdate());
		read.setSelection(role.canRead());
		delete.setSelection(role.canDelete());
		execute.setSelection(role.canExecute());
	}
	
	private void buildContent(){
		this.setLayout(new GridLayout(2, false));
		
		
		Label l0 = new Label(this, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false,1 , 1));
		l0.setText(Messages.CompositeRole_0);
		
		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		
		
		Label l20 = new Label(this, SWT.NONE);
		l20.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false,1 , 1));
		l20.setText(Messages.CompositeRole_1);
		
		application = new Text(this, SWT.BORDER);
		application.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		application.setEnabled(false);
				
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l2.setText(Messages.CompositeRole_2);
		
		
		connect = new Button(this, SWT.CHECK);
		connect.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		connect.setText(Messages.CompositeRole_3);
		
		create = new Button(this, SWT.CHECK);
		create.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		create.setText(Messages.CompositeRole_4);
		
		read = new Button(this, SWT.CHECK);
		read.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		read.setText(Messages.CompositeRole_5);
		
		update = new Button(this, SWT.CHECK);
		update.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		update.setText(Messages.CompositeRole_6);
		
		delete = new Button(this, SWT.CHECK);
		delete.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		delete.setText(Messages.CompositeRole_7);
		

		execute = new Button(this, SWT.CHECK);
		execute.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		execute.setText(Messages.CompositeRole_8);		
		
		historic = new Button(this, SWT.CHECK);
		historic.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		historic.setText(Messages.CompositeRole_9);		
		
		
		Composite c = new Composite(this, SWT.NONE);
		c.setLayout(new GridLayout(2, true));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		Button ok = new Button(c, SWT.PUSH);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		ok.setText(Messages.CompositeRole_10);
		ok.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				setDatas();

				try {
					Activator.getDefault().getVanillaApi().getVanillaSecurityManager().updateRole(role);
				} catch (Exception e1) {
					e1.printStackTrace();
					return;
				}
				
				
				ViewRole v = (ViewRole)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewRole.ID);
				if (v != null){
					v.refresh();
				}	
				ViewGroup vg = (ViewGroup)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewGroup.ID);
				if (vg != null){
					vg.refresh();
				}
				
			}
			
		});
		
		Button cancel = new Button(c, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		cancel.setText(Messages.CompositeRole_11);
		cancel.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				fillDatas();
			}
			
		});
		
	}
	
	
	private void setDatas(){
		if (role == null){
			role = new Role();
		}
		
		role.setName(name.getText());
		if (connect.getSelection()){
			addChar("A"); //$NON-NLS-1$
		}
		else{
			removeChar("A"); //$NON-NLS-1$
		}
		
		if (create.getSelection()){
			addChar("C"); //$NON-NLS-1$
		}
		else{
			removeChar("C"); //$NON-NLS-1$
		}
		if (update.getSelection()){
			addChar("U"); //$NON-NLS-1$
		}
		else{
			removeChar("U"); //$NON-NLS-1$
		}
		
		if (delete.getSelection()){
			addChar("D"); //$NON-NLS-1$
		}
		else{
			removeChar("D"); //$NON-NLS-1$
		}
		
		if (read.getSelection()){
			addChar("R"); //$NON-NLS-1$
		}
		else{
			removeChar("R"); //$NON-NLS-1$
		}
		
		if (execute.getSelection()){
			addChar("E"); //$NON-NLS-1$
		}
		else{
			removeChar("E"); //$NON-NLS-1$
		}
	}
	
	private void addChar(String c){
		String s = role.getGrants();
		
		if (s == null){
			s = ""; //$NON-NLS-1$
		}
		
		if (!s.contains(c)){
			s += c;
			
		}
		
		role.setGrants(s);
		
	}
	
	private void removeChar(String c){
		String s = role.getGrants();
		
		if (s.contains(c)){
			int i = s.indexOf(c);
			if (i >= 0){
				
				if (i+ 1 < s.length()){
					s = s.substring(0, i) + s.substring(i + 1, s.length() - 1);
				}
				else{
					s = s.substring(0, i) ;
				}
				
			}
			
			
			role.setGrants(s);
		}
		
		
	}

	
}
