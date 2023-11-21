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
import org.fasd.olap.OLAPDimension;
import org.fasd.utils.trees.TreeDim;
import org.fasd.views.DimensionView;
import org.fasd.views.dialogs.DialogAddHiera;
import org.fasd.views.operations.AddHieraOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionAddHiera extends Action {
	private DimensionView view;
	private TreeViewer viewer;
	private UndoContext undoContext;

	public ActionAddHiera(DimensionView view, TreeViewer viewer, UndoContext undoContext) {
		super(LanguageText.ActionAddHiera_New_Hierarchy);
		this.view = view;
		this.viewer = viewer;
		this.undoContext = undoContext;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/hierarchy.gif"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		if (!FreemetricsPlugin.getDefault().isDocOpened()) {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionAddHiera_Information, LanguageText.ActionAddHiera_Create_Or_Open_Schema);
			return;
		}
		ISelection s = viewer.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			Object o = ss.getFirstElement();

			if (o instanceof TreeDim) {

				OLAPDimension dim = ((TreeDim) o).getOLAPDimension();
				DialogAddHiera dial = new DialogAddHiera(view.getSite().getShell(), dim.getName());
				if (dial.open() == Dialog.OK) {

					try {
						view.getOperationHistory().execute(new AddHieraOperation(LanguageText.ActionAddHiera_Add_Hierarchy, undoContext, dim, dial.getHiera()), null, null);
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
}
