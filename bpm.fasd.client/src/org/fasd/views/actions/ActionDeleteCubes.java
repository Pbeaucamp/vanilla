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
import org.fasd.olap.OLAPSchema;
import org.fasd.olap.virtual.VirtualCube;
import org.fasd.utils.trees.TreeAggregate;
import org.fasd.utils.trees.TreeCube;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeDimGroup;
import org.fasd.utils.trees.TreeMes;
import org.fasd.utils.trees.TreeMesGroup;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeRoot;
import org.fasd.utils.trees.TreeVirtualCube;
import org.fasd.utils.trees.TreeVirtualDim;
import org.fasd.utils.trees.TreeVirtualMes;
import org.fasd.views.CubeView;
import org.fasd.views.operations.DeleteCubeOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionDeleteCubes extends Action {
	private CubeView view;
	private TreeViewer tree;
	private UndoContext undoContext;

	public ActionDeleteCubes(CubeView view, TreeViewer tree, UndoContext undoContext) {
		super(LanguageText.ActionDeleteCubes_Del);
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

		if (object instanceof TreeParent) {
			TreeParent tp = (TreeParent) object;
			System.out.println("name = " + tp.getName()); //$NON-NLS-1$

			TreeParent dad = tp.getParent();

			if (dad instanceof TreeRoot) {
				OLAPSchema sch = ((TreeRoot) dad).getOLAPSchema();
				try {
					if (tp instanceof TreeCube)
						view.getOperationHistory().execute(new DeleteCubeOperation(LanguageText.ActionDeleteCubes_Del_Cube, undoContext, sch, ((TreeCube) tp).getOLAPCube()), null, null);

					if (tp instanceof TreeVirtualCube)
						view.getOperationHistory().execute(new DeleteCubeOperation(LanguageText.ActionDeleteCubes_Del_Virtual_Cube, undoContext, sch, ((TreeVirtualCube) tp).getVirtualCube()), null, null);

				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			} else if (dad instanceof TreeCube && tp instanceof TreeDim) {
				OLAPCube cube = ((TreeCube) dad).getOLAPCube();
				try {
					view.getOperationHistory().execute(new DeleteCubeOperation(LanguageText.ActionDeleteCubes_Del_DimUsage, undoContext, cube, ((TreeDim) tp).getOLAPDimension()), null, null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			} else if (dad instanceof TreeCube && tp instanceof TreeMes) {
				OLAPCube cube = ((TreeCube) dad).getOLAPCube();
				try {
					view.getOperationHistory().execute(new DeleteCubeOperation(LanguageText.ActionDeleteCubes_Del_MeasureUsage, undoContext, cube, ((TreeMes) tp).getOLAPMeasure()), null, null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			} else if (dad instanceof TreeCube && tp instanceof TreeMesGroup) {
				OLAPCube cube = ((TreeCube) dad).getOLAPCube();
				cube.removeMesGroup(((TreeMesGroup) tp).getOLAPMeasureGroup());
				FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
			} else if (dad instanceof TreeCube && tp instanceof TreeDimGroup) {
				OLAPCube cube = ((TreeCube) dad).getOLAPCube();
				cube.removeDimGroup(((TreeDimGroup) tp).getOLAPDimensionGroup());
				FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
			} else if (dad instanceof TreeCube && tp instanceof TreeDim) {
				OLAPCube cube = ((TreeCube) dad).getOLAPCube();
				try {
					view.getOperationHistory().execute(new DeleteCubeOperation(LanguageText.ActionDeleteCubes_Del_DimUsage, undoContext, cube, ((TreeDim) tp).getOLAPDimension()), null, null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			} else if (dad instanceof TreeCube && tp instanceof TreeAggregate) {
				OLAPCube cube = ((TreeCube) dad).getOLAPCube();
				try {
					view.getOperationHistory().execute(new DeleteCubeOperation(LanguageText.ActionDeleteCubes_Del_DimUsage, undoContext, cube, ((TreeAggregate) tp).getAgg()), null, null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}

			else if (dad instanceof TreeVirtualCube && tp instanceof TreeVirtualMes) {
				VirtualCube cube = ((TreeVirtualCube) dad).getVirtualCube();
				try {
					view.getOperationHistory().execute(new DeleteCubeOperation(LanguageText.ActionDeleteCubes_Del_Virtual_Measure, undoContext, cube, ((TreeVirtualMes) tp).getVirtualMeasure()), null, null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			} else if (dad instanceof TreeVirtualCube && tp instanceof TreeVirtualDim) {
				VirtualCube cube = ((TreeVirtualCube) dad).getVirtualCube();
				try {
					view.getOperationHistory().execute(new DeleteCubeOperation(LanguageText.ActionDeleteCubes_Del_Virtual_Dim, undoContext, cube, ((TreeVirtualDim) tp).getVirtualDimension()), null, null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
