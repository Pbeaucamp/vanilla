package bpm.inlinedatas.oda.driver.ui.dialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class InLineMessageDialog {
	
	public static void showError(Shell pShell, String message){
		
		
		MessageDialog.openError(pShell, "InLine Data : Error Message", message);
		
	}

}
