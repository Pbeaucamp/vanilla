package bpm.es.pack.manager.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;

import adminbirep.Activator;
import bpm.es.pack.manager.I18N.Messages;
import bpm.es.pack.manager.views.ViewWorkplace;

public class ActionPlaceRefresh extends Action {

	private ViewWorkplace view = null;
	
	public ActionPlaceRefresh(IViewPart view) {
		this.view = (ViewWorkplace)view;
		this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("refresh")); //$NON-NLS-1$
		this.setText(Messages.ActionPlaceRefresh_0);
		this.setToolTipText(Messages.ActionPlaceRefresh_1);
	}

	public void run() {
		view.refreshView();
	}
}
