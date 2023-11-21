package bpm.birep.admin.client.dialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.helpers.SearchReplace;
import bpm.vanilla.platform.core.IRepositoryApi;


public class SearchReplaceRepository extends DialogSearchReplace {

	public SearchReplaceRepository(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void okPressed() {
		IRepositoryApi sock = Activator.getDefault().getRepositoryApi();
		try{
			SearchReplace.getInstance(searchString.getText(), newString.getText()).run(sock);
		} catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.SearchReplaceRepository_0, ex.getMessage());
			return;
		}
		super.okPressed();
	}
}
