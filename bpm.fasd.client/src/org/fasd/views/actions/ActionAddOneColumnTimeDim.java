package org.fasd.views.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.fasd.cubewizard.dimension.DateDimensionWizard;
import org.fasd.datasource.DatasourceOda;
import org.fasd.i18N.LanguageText;
import org.fasd.views.DimensionView;
import org.fasd.views.operations.AddDimensionOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionAddOneColumnTimeDim extends Action {

	private DimensionView view;
	private UndoContext undoCtx;

	public ActionAddOneColumnTimeDim(DimensionView view, UndoContext undoCtx) {
		super(LanguageText.ActionAddOneColumnTimeDim_0);
		this.view = view;
		this.undoCtx = undoCtx;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/datedim.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {

		if (FreemetricsPlugin.getDefault().getFAModel().getDataSources() == null || FreemetricsPlugin.getDefault().getFAModel().getDataSources().size() == 0 || !(FreemetricsPlugin.getDefault().getFAModel().getDataSources().get(0) instanceof DatasourceOda)) {
			MessageDialog.openWarning(view.getSite().getShell(), LanguageText.ActionAddOneColumnTimeDim_1, LanguageText.ActionAddOneColumnTimeDim_2);
		}

		else {
			DateDimensionWizard wizard = new DateDimensionWizard();
			wizard.init(view.getSite().getWorkbenchWindow().getWorkbench(), (IStructuredSelection) view.getSite().getWorkbenchWindow().getSelectionService().getSelection());
			WizardDialog dialog = new WizardDialog(view.getSite().getShell(), wizard);
			dialog.create();

			if (dialog.open() == Dialog.OK) {
				try {
					view.getOperationHistory().execute(new AddDimensionOperation(LanguageText.ActionAddDimension_Add_Dimension, undoCtx, FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema(), wizard.getResultDimension()), null, null);

				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
