package org.fasd.views.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPDimensionGroup;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPSchema;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeDimGroup;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeLevel;
import org.fasd.utils.trees.TreeParent;
import org.fasd.views.DimensionView;
import org.fasd.views.operations.DeleteDimsOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionDeleteDims extends Action {
	private DimensionView view;
	private TreeViewer tree;
	private UndoContext undoContext;

	public ActionDeleteDims(DimensionView view, TreeViewer tree, UndoContext undoContext) {
		super(LanguageText.ActionDeleteDims_Del);
		this.view = view;
		this.tree = tree;
		this.undoContext = undoContext;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/del.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		ISelection selection = tree.getSelection();

		if (!(selection instanceof IStructuredSelection)) {
			System.out.println("unstructured for delete, aborted"); //$NON-NLS-1$
			return;
		}

		IStructuredSelection ss = (IStructuredSelection) selection;
		Object object = ss.getFirstElement();
		System.out.println("class is " + object.getClass()); //$NON-NLS-1$

		if (object instanceof TreeDimGroup) {

			OLAPDimensionGroup group = ((TreeDimGroup) object).getOLAPDimensionGroup();
			FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().removeDimensionGroup(group);
			FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimGroup().setChanged();
		}

		// dimension/cube or not
		else if (object instanceof TreeDim) {
			TreeParent tp = (TreeParent) object;
			OLAPSchema sch = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema();
			for (OLAPCube c : sch.getCubes()) {
				for (OLAPDimension d : c.getDims()) {
					if (d == ((TreeDim) object).getOLAPDimension()) {
						c.removeDim(d);
						break;
					}
				}
			}
			try {
				view.getOperationHistory().execute(new DeleteDimsOperation(LanguageText.ActionDeleteDims_Del_Dimension, undoContext, sch, ((TreeDim) tp).getOLAPDimension()), null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			view.refresh();
		} else if (object instanceof TreeParent) {
			TreeParent tp = (TreeParent) object;
			System.out.println("name = " + tp.getName()); //$NON-NLS-1$

			TreeParent dad = tp.getParent();

			if (dad instanceof TreeDim) {
				OLAPDimension dim = ((TreeDim) dad).getOLAPDimension();
				try {
					view.getOperationHistory().execute(new DeleteDimsOperation(LanguageText.ActionDeleteDims_Del_Hierarchy, undoContext, dim, ((TreeHierarchy) tp).getOLAPHierarchy()), null, null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			} else if (dad instanceof TreeHierarchy) {
				OLAPHierarchy dim = ((TreeHierarchy) dad).getOLAPHierarchy();
				try {
					view.getOperationHistory().execute(new DeleteDimsOperation(LanguageText.ActionDeleteDims_Del_Lvl, undoContext, dim, ((TreeLevel) tp).getOLAPLevel()), null, null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
