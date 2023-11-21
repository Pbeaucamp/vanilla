package bpm.birep.admin.client.dialog;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.vanilla.platform.core.beans.Repository;

public class DialogAddRepUser extends Dialog {

	private Composite composite ;
	private Combo addRepUser;
	private Repository repository;
	private Repository rep;

	
	public DialogAddRepUser(Shell parentShell) {
		super(parentShell);
	}
	
	public String[] getListRep() throws Exception{
		List<Repository> listrep = Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getRepositories();
		Iterator<Repository> it = listrep.iterator();
		int size = listrep.size();
		int i = 0;
		String[] nameRep = new String[size];
		while (i < size){
			rep = (Repository)it.next();
			nameRep[i] = rep.getName();
			i++;
		}
		return nameRep;
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
		getShell().setText(Messages.DialogAddRepUser_0);
		
		
		Label nl = new Label(composite, SWT.NONE);
		nl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		nl.setText(Messages.DialogAddRepUser_1);
		
		addRepUser  = new Combo(composite, SWT.READ_ONLY);
		addRepUser.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		try {
			addRepUser.setItems(getListRep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
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
		int index = addRepUser.getSelectionIndex();
		while (i <= size){
			Repository rep = (Repository)it.next();
			if(addRepUser.getItem(index).equals(rep.getName())){
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
