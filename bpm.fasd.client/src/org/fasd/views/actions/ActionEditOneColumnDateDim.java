package org.fasd.views.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.fasd.cubewizard.dimension.DateDimensionWizard;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPDimension;
import org.fasd.views.DimensionView;
import org.fasd.views.operations.AddDimensionOperation;
import org.fasd.views.operations.DeleteDimsOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionEditOneColumnDateDim extends Action {

	private DimensionView view;
	private UndoContext undoCtx;
	private OLAPDimension dim;

	public ActionEditOneColumnDateDim(DimensionView view, UndoContext undoCtx) {
		super(LanguageText.ActionEditOneColumnDateDim_0);
		this.view = view;
		this.undoCtx = undoCtx;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/datedim.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		DateDimensionWizard wizard = new DateDimensionWizard(dim);
		wizard.init(view.getSite().getWorkbenchWindow().getWorkbench(), (IStructuredSelection) view.getSite().getWorkbenchWindow().getSelectionService().getSelection());
		WizardDialog dialog = new WizardDialog(view.getSite().getShell(), wizard);
		dialog.create();

		if (dialog.open() == Dialog.OK) {
			try {

				view.getOperationHistory().execute(new DeleteDimsOperation(LanguageText.ActionDeleteDims_Del_Dimension, undoCtx, FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema(), dim), null, null);

				view.getOperationHistory().execute(new AddDimensionOperation(LanguageText.ActionAddDimension_Add_Dimension, undoCtx, FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema(), wizard.getResultDimension()), null, null);

			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void setDimension(OLAPDimension dim) {
		this.dim = dim;
	}
}
