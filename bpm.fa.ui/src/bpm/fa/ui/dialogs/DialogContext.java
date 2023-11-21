package bpm.fa.ui.dialogs;



import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.fa.ui.Messages;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class DialogContext extends Dialog{

	
	private RuntimeContext ctx;
	private Text login, password;//, groupName, groupId;
	private Combo cbGroup;
	private	IVanillaAPI vanillaApi;

	public DialogContext(Shell parentShell) {
		super(parentShell);
		
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout(2, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogContext_0);
		l.setLayoutData(new GridData());
		
		login = new Text(main, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		login.setText("system"); //$NON-NLS-1$
		
		l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogContext_2);
		l.setLayoutData(new GridData());
		
		password = new Text(main, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		password.setText("system"); //$NON-NLS-1$
		
		Button btnLoadGrp = new Button(main, SWT.PUSH);
		btnLoadGrp.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		btnLoadGrp.setText(Messages.DialogContext_4);
		
		l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogContext_5);
		l.setLayoutData(new GridData());
		
		cbGroup = new Combo(main, SWT.DROP_DOWN);
		cbGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		btnLoadGrp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					IRepositoryContext ctx = bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getRepositoryContext();
					
					vanillaApi = new RemoteVanillaPlatform(ctx.getVanillaContext().getVanillaUrl(), 
							ctx.getVanillaContext().getLogin(), ctx.getVanillaContext().getPassword());
					List<Group> groups = vanillaApi.getVanillaSecurityManager().getGroups();
					
					String[] items = new String[groups.size()];
					int i = 0;
					for(Group grp : groups) {
						items[i] = grp.getName();
						i++;
					}
					
					cbGroup.setItems(items);
					
				} catch(Throwable t) {
					MessageDialog.openError(getParentShell(), Messages.DialogContext_6, Messages.DialogContext_7);
				}
			}
		});
		
		return main;
	}
	
	@Override
	protected void okPressed() {
		String groupName = cbGroup.getItem(cbGroup.getSelectionIndex());
		Integer groupId = null;
		try {
			groupId = vanillaApi.getVanillaSecurityManager().getGroupByName(groupName).getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ctx = new RuntimeContext(login.getText(), 
				password.getText(), 
				groupName, 
				groupId);

		super.okPressed();
	}
	
	public IRuntimeContext getContext(){
		return ctx;
	}
}
