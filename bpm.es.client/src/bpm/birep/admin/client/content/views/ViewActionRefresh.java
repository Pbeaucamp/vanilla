package bpm.birep.admin.client.content.views;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;

import adminbirep.Activator;
import adminbirep.Messages;

public class ViewActionRefresh extends Action {


	
	private ViewTree view = null;
	
	public ViewActionRefresh(IViewPart view) {
		this.view = (ViewTree)view;
		this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("refresh")); //$NON-NLS-1$
		this.setText(Messages.ViewActionRefresh_1);
		this.setToolTipText(Messages.ViewActionRefresh_2);
	}

	public void run() {
		view.createInput();
	}

	

	

}
