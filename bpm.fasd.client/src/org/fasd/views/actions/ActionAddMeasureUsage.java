package org.fasd.views.actions;

import java.util.ArrayList;

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
import org.fasd.olap.OLAPMeasure;
import org.fasd.utils.trees.TreeCube;
import org.fasd.views.CubeView;
import org.fasd.views.dialogs.DialogSelectMeasure;
import org.fasd.views.operations.AddMesToCubeOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionAddMeasureUsage extends Action {
	private CubeView view;
	private TreeViewer viewer;
	private UndoContext undoContext;

	public ActionAddMeasureUsage(CubeView view, TreeViewer viewer, UndoContext undoContext) {
		super(LanguageText.ActionAddMeasureUsage_New_Measure_Usage);
		this.view = view;
		this.viewer = viewer;
		this.undoContext = undoContext;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_measure.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		if (!FreemetricsPlugin.getDefault().isDocOpened()) {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionAddMeasureUsage_Information, LanguageText.ActionAddMeasureUsage_Create_a_New_Schema);
			return;
		}
		ISelection s = viewer.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			Object o = ss.getFirstElement();

			if (o instanceof TreeCube) {
				ArrayList<OLAPMeasure> mes = new ArrayList<OLAPMeasure>();
				for (OLAPMeasure m : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getMeasures()) {
					if (!((TreeCube) o).getOLAPCube().getMes().contains(m)) {
						mes.add(m);
					}

				}
				DialogSelectMeasure dial = new DialogSelectMeasure(view.getSite().getShell(), ((TreeCube) o).getOLAPCube().getMes());
				if (dial.open() == Dialog.OK) {
					OLAPCube cube = ((TreeCube) o).getOLAPCube();
					try {
						view.getOperationHistory().execute(new AddMesToCubeOperation(LanguageText.ActionAddMeasureUsage_Add_MeasureUsage, undoContext, cube, dial.getMes()), null, null);
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

}
