package org.fasd.views.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPMeasure;
import org.fasd.views.MeasureView;
import org.fasd.views.operations.AddMeasureOperation;
import org.fasd.views.operations.DeleteMeasureOperation;
import org.freeolap.FreemetricsPlugin;

import bpm.fasd.ui.measure.definition.dialog.MeasureDefinitionDialog;

public class ActionEditMeasure extends Action {

	private MeasureView view;
	private UndoContext undoContext;
	private OLAPMeasure editedMeasure;

	public ActionEditMeasure(MeasureView v, UndoContext undoContext) {
		super(LanguageText.ActionEditMeasure_0);
		view = v;
		this.undoContext = undoContext;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_calc.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		MeasureDefinitionDialog dial = new MeasureDefinitionDialog(view.getSite().getShell(), FreemetricsPlugin.getDefault().getFAModel(), editedMeasure);
		if (dial.open() == Dialog.OK) {
			OLAPMeasure f = dial.getMeasure();
			try {
				view.getOperationHistory().execute(new DeleteMeasureOperation(LanguageText.ActionDeleteMeasure_Del_Measure, undoContext, FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema(), editedMeasure), null, null);
				view.getOperationHistory().execute(new AddMeasureOperation(LanguageText.ActionAddMeasure_Add_Calculated_Measure, undoContext, FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema(), f), null, null);

			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public void setMeasure(OLAPMeasure olapMeasure) {
		this.editedMeasure = olapMeasure;
	}
}
