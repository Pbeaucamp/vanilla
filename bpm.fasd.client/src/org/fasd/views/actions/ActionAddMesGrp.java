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
import org.fasd.olap.OLAPMeasureGroup;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeLevel;
import org.fasd.utils.trees.TreeMesGroup;
import org.fasd.views.MeasureView;
import org.fasd.views.operations.AddMeasureGroupOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionAddMesGrp extends Action {
	private MeasureView view;
	private TreeViewer viewer;
	private UndoContext undoContext;

	public ActionAddMesGrp(MeasureView view, TreeViewer tree, UndoContext undoContext) {
		super(LanguageText.ActionAddMesGrp_New_Group);
		this.view = view;
		this.viewer = tree;
		this.undoContext = undoContext;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/folder.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		if (!FreemetricsPlugin.getDefault().isDocOpened()) {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionAddMesGrp_Information, LanguageText.ActionAddMesGrp_Create_Or_Open_a_Schema);
			return;
		}

		ISelection selection = viewer.getSelection();

		if (!(selection instanceof IStructuredSelection)) {
			System.out.println("unstructured for delete, aborted"); //$NON-NLS-1$
			return;
		}

		IStructuredSelection ss = (IStructuredSelection) selection;
		Object object = ss.getFirstElement();

		if (object instanceof TreeMesGroup) {
			OLAPMeasureGroup group = ((TreeMesGroup) object).getOLAPMeasureGroup();
			try {
				view.getOperationHistory().execute(new AddMeasureGroupOperation(LanguageText.ActionAddMesGrp_Add_Measure_Group, undoContext, group, new OLAPMeasureGroup(LanguageText.ActionAddMesGrp_New_MeasureGroup)), null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		else if (!(object instanceof TreeLevel) || !(object instanceof TreeDim) || !(object instanceof TreeHierarchy)) {
			try {
				view.getOperationHistory().execute(new AddMeasureGroupOperation(LanguageText.ActionAddMesGrp_Add_Measure_Group, undoContext, new OLAPMeasureGroup(LanguageText.ActionAddMesGrp_New_MeasureGroup)), null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} else {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionAddMesGrp_Warning, LanguageText.ActionAddMesGrp_Select_a_Cube_To_Add);
		}
	}

}
