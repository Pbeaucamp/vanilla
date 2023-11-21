package bpm.birep.admin.client.dialog;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;

public class DialogDelRepUser extends Dialog {

	private Composite composite ;
	private Repository repository;
	private String repname;
	
	public DialogDelRepUser(Shell parentShell, User user, String repname) {
		super(parentShell);
		this.repname = repname;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(500, 270);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		getShell().setText(Messages.DialogDelRepUser_0);
		
		
		Label nl = new Label(composite, SWT.NONE);
		nl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		nl.setText(Messages.DialogDelRepUser_1 + repname);
		
		
		return composite;
		
	}
	
	public Repository getRepository(){
		return repository;
	}
	

	@Override
	protected void okPressed() {
		List<Repository> listRep;
		try {
			listRep = Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getRepositories();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Iterator<Repository> it = listRep.iterator();
		int size = listRep.size();
		int i = 1;
		while (i <= size){
			Repository rep = (Repository)it.next();
			if(repname.equals(rep.getName())){
				repository = new Repository();
				repository = rep;
				break;
			}
			else{
				i++;
			}
		}
		
		super.okPressed();
	}
	
}
