package org.fasd.views.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.fasd.i18N.LanguageText;
import org.fasd.views.CubeView;
import org.fasd.views.dialogs.DialogCube;
import org.fasd.views.operations.AddCubeOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionAddCube extends Action {
	private CubeView view;
	private UndoContext undoContext;

	public ActionAddCube(CubeView view, UndoContext undoContext) {
		super(LanguageText.ActionAddCube_New_Cube);
		this.view = view;
		this.undoContext = undoContext;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/cube.gif"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		if (!FreemetricsPlugin.getDefault().isDocOpened()) {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionAddCube_information, LanguageText.ActionAddCube_Create_or_Open_Schema);
			return;
		}

		DialogCube dial = new DialogCube(view.getSite().getShell());

		if (dial.open() == Dialog.OK) {
			try {
				view.getOperationHistory().execute(new AddCubeOperation(LanguageText.ActionAddCube_Add_Cube, undoContext, FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema(), dial.getCube()), null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

	}
}
