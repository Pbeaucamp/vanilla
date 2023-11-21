package bpm.es.recycled.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;

import bpm.es.recycled.ui.Messages;

import adminbirep.Activator;

public class ViewActionRefresh extends Action {


	
	private RecycledView view = null;
	
	public ViewActionRefresh(IViewPart view) {
		this.view = (RecycledView)view;
		this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("refresh")); //$NON-NLS-1$
		this.setText(Messages.ViewActionRefresh_1);
		this.setToolTipText(Messages.ViewActionRefresh_2);
	}

	public void run() {
		view.loadData();
	}

	

	

}
