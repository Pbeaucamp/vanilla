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
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPSchema;
import org.fasd.views.DimensionView;
import org.fasd.views.dialogs.DialogAddDimension;
import org.fasd.views.operations.AddDimensionOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionAddDimension extends Action {
	private DimensionView view;
	private UndoContext undoContext;
	private OLAPDimension dim = null;

	public ActionAddDimension(DimensionView view, UndoContext undoContext) {
		super(LanguageText.ActionAddDimension_New_Dimension);
		this.view = view;
		this.undoContext = undoContext;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/dimension.gif"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		if (!FreemetricsPlugin.getDefault().isDocOpened()) {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionAddDimension_Information, LanguageText.ActionAddDimension_Create_or_Open_Schema);
			return;
		}
		OLAPSchema schema = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema();

		if (dim == null) {
			DialogAddDimension dial = new DialogAddDimension(view.getSite().getShell());
			if (dial.open() == Dialog.OK) {
				OLAPDimension dim = dial.getDimension();
				try {
					view.getOperationHistory().execute(new AddDimensionOperation(LanguageText.ActionAddDimension_Add_Dimension, undoContext, schema, dim), null, null);

				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}

		} else {
			try {
				view.getOperationHistory().execute(new AddDimensionOperation(LanguageText.ActionAddDimension_Add_Dimension, undoContext, schema, dim), null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

		}
	}

	public void run(OLAPDimension dim) {
		this.dim = dim;
		run();
	}
}
