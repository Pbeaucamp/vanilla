package bpm.fd.design.ui.rcp.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;

import bpm.fd.design.ui.rcp.action.ActionConver;

public class ConvertModelHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ActionConver converter = new ActionConver();
		try {
			converter.run();
		} catch (Exception e) {
			e.printStackTrace();
			//MessageDialog.openError(parent, title, message)
		}
		return null;
	}

	

}
