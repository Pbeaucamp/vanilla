package org.fasd.views.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPCube;
import org.fasd.utils.trees.TreeCube;
import org.fasd.views.CubeView;
import org.fasd.views.dialogs.DialogSelectDimension;
import org.fasd.views.operations.AddDimToCubeOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionAddDimUsage extends Action {
	private CubeView view;
	private TreeViewer viewer;
	private UndoContext undoContext;

	public ActionAddDimUsage(CubeView view, TreeViewer viewer, UndoContext undoContext) {
		super(LanguageText.ActionAddDimUsage_New_Dimension_Usage);
		this.view = view;
		this.viewer = viewer;
		this.undoContext = undoContext;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/dimension.gif"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		if (!FreemetricsPlugin.getDefault().isDocOpened()) {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionAddDimUsage_Information, LanguageText.ActionAddDimUsage_Create_or_Open_Schema);
			return;
		}
		ISelection s = viewer.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			Object o = ss.getFirstElement();

			if (o instanceof TreeCube) {
				DialogSelectDimension dial = new DialogSelectDimension(view.getSite().getShell(), ((TreeCube) o).getOLAPCube().getDims());
				if (dial.open() == Dialog.OK) {
					OLAPCube cube = ((TreeCube) o).getOLAPCube();
					try {
						view.getOperationHistory().execute(new AddDimToCubeOperation(LanguageText.ActionAddDimUsage_Add_Dimension_Usage, undoContext, cube, dial.getDim()), null, null);
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

}
