package org.fasd.views.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPDimensionGroup;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeDimGroup;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeLevel;
import org.fasd.views.DimensionView;
import org.fasd.views.operations.AddDimensionGroupOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionAddDimGrp extends Action {
	private DimensionView view;
	private TreeViewer viewer;
	private UndoContext undoContext;

	public ActionAddDimGrp(DimensionView view, TreeViewer tree, UndoContext undoContext) {
		super(LanguageText.ActionAddDimGrp_New_Group);
		this.view = view;
		this.viewer = tree;
		this.undoContext = undoContext;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/folder.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		if (!FreemetricsPlugin.getDefault().isDocOpened()) {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionAddDimGrp_Information, LanguageText.ActionAddDimGrp_Create_Or_open_Schema);
			return;
		}

		ISelection selection = viewer.getSelection();

		if (!(selection instanceof IStructuredSelection)) {
			System.out.println("unstructured for delete, aborted"); //$NON-NLS-1$
			return;
		}

		IStructuredSelection ss = (IStructuredSelection) selection;
		Object object = ss.getFirstElement();
		System.out.println("class is " + object.getClass()); //$NON-NLS-1$

		if (object instanceof TreeDimGroup) {
			OLAPDimensionGroup group = ((TreeDimGroup) object).getOLAPDimensionGroup();
			try {
				view.getOperationHistory().execute(new AddDimensionGroupOperation(LanguageText.ActionAddDimGrp_Add_Dimension_Group, undoContext, group, new OLAPDimensionGroup(LanguageText.ActionAddDimGrp_New_DimensionGroup)), null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		else if (!(object instanceof TreeLevel) || !(object instanceof TreeDim) || !(object instanceof TreeHierarchy)) {
			try {
				view.getOperationHistory().execute(new AddDimensionGroupOperation(LanguageText.ActionAddDimGrp_Add_DimensionGroup, undoContext, new OLAPDimensionGroup(LanguageText.ActionAddDimGrp_New_DimensionGroup)), null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} else {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionAddDimGrp_Warning, LanguageText.ActionAddDimGrp_Sel_a_Cube_To_Add_To_Gp);
		}
	}

}
