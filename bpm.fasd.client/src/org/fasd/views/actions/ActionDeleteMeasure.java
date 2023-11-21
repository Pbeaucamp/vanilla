package org.fasd.views.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPMeasureGroup;
import org.fasd.olap.OLAPSchema;
import org.fasd.utils.trees.TreeMes;
import org.fasd.utils.trees.TreeMesGroup;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeRoot;
import org.fasd.views.MeasureView;
import org.fasd.views.operations.DeleteMeasureOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionDeleteMeasure extends Action {
	private MeasureView view;
	private TreeViewer tree;
	private UndoContext undoContext;

	public ActionDeleteMeasure(MeasureView view, TreeViewer tree, UndoContext undoContext) {
		super(LanguageText.ActionDeleteMeasure_Del);
		this.view = view;
		this.tree = tree;
		this.undoContext = undoContext;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/del.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		ISelection selection = tree.getSelection();

		if (!(selection instanceof IStructuredSelection)) {
			System.out.println("unstructured for delete, aborted"); //$NON-NLS-1$
			return;
		}

		IStructuredSelection ss = (IStructuredSelection) selection;
		Object object = ss.getFirstElement();
		System.out.println("class is " + object.getClass()); //$NON-NLS-1$

		if (object instanceof TreeMesGroup) {

			OLAPMeasureGroup group = ((TreeMesGroup) object).getOLAPMeasureGroup();
			if (group.getParent() != null) {
				group.getParent().removeChild(group);
			} else {
				FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().removeMeasureGroup(group);
			}

			FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMesGroup().setChanged();
			FreemetricsPlugin.getDefault().getFAModel().setChanged();
		}

		else if (object instanceof TreeParent) {
			TreeParent tp = (TreeParent) object;
			System.out.println("name = " + tp.getName()); //$NON-NLS-1$

			TreeParent dad = tp.getParent();

			if (dad instanceof TreeRoot && tp instanceof TreeMes) {
				OLAPSchema sch = ((TreeRoot) dad).getOLAPSchema();
				for (OLAPCube c : sch.getCubes()) {
					for (OLAPMeasure m : c.getMes()) {
						if (m == ((TreeMes) object).getOLAPMeasure()) {
							c.removeMes(m);
							break;
						}

					}
				}

				try {
					view.getOperationHistory().execute(new DeleteMeasureOperation(LanguageText.ActionDeleteMeasure_Del_Measure, undoContext, sch, ((TreeMes) tp).getOLAPMeasure()), null, null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}		

			}

		}
	}
}
