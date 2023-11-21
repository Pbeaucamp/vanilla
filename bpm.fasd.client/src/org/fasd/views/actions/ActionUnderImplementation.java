package org.fasd.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.fasd.i18N.LanguageText;

public class ActionUnderImplementation extends Action {
	private Shell shell;
	
	public ActionUnderImplementation(Shell shell, String name){
		super(name);
		this.shell = shell;
	}
	
	public void run(){
		MessageDialog.openInformation(shell, LanguageText.ActionUnderImplementation_Information, LanguageText.ActionUnderImplementation_Under_impl);
	}

}
