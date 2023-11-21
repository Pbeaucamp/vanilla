package bpm.google.table.oda.driver.ui.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class MessageTable {
	
	private final static  String MESSAGE_TITLE = "Google Table ODA Driver Informations";
	
	
	
	
	public static void  showError(Shell shell, String message){
		MessageDialog.openError(shell, MESSAGE_TITLE, message);
	}

}
