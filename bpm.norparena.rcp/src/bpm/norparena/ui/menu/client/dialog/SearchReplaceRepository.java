package bpm.norparena.ui.menu.client.dialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import bpm.norparena.ui.menu.Activator;
import bpm.norparena.ui.menu.Messages;
import bpm.norparena.ui.menu.client.helpers.SearchReplace;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;

public class SearchReplaceRepository extends DialogSearchReplace {

	public SearchReplaceRepository(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void okPressed() {
		IRepository rep = null;
		try {
			rep = Activator.getDefault().getIRepository();
		} catch (Exception e) {
			e.printStackTrace();
		}
		IRepositoryApi sock = Activator.getDefault().getSocket();
		try{
			SearchReplace.getInstance(searchString.getText(), newString.getText()).run(sock, rep);
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.SearchReplaceRepository_0, ex.getMessage());
			return;
		}
		super.okPressed();
	}
	
	

}
