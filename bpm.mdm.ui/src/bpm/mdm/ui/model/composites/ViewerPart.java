package bpm.mdm.ui.model.composites;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.Section;

import bpm.mdm.ui.Activator;

public class ViewerPart extends SectionPart implements IPartSelectionListener{
	private TreeViewer viewer;
	public ViewerPart(Section section, TreeViewer viewer) {
		super(section);
		this.viewer = viewer;
		
	}
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		TreePath[] exp = viewer.getExpandedTreePaths();
		viewer.setInput(Activator.getDefault().getMdmProvider().getModel());
		viewer.setExpandedTreePaths(exp);
		
	}

}
