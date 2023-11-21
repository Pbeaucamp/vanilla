package bpm.profiling.ui.view.design.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.profiling.ui.views.ViewDesign;

public class ActionInit implements IViewActionDelegate {

	private ViewDesign viewDesign;
	
	public void init(IViewPart view) {
		viewDesign = (ViewDesign)view;

	}

	public void run(IAction action) {
		

	}

	public void selectionChanged(IAction action, ISelection selection) {
		

	}

}
