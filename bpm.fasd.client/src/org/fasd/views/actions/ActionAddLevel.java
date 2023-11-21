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
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.views.DimensionView;
import org.fasd.views.dialogs.DialogAddLevel;
import org.fasd.views.operations.AddLevelOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionAddLevel extends Action {
	private DimensionView view;
	private TreeViewer viewer;
	private UndoContext undoContext;

	public ActionAddLevel(DimensionView view, TreeViewer viewer, UndoContext undoContext) {
		super(LanguageText.ActionAddLevel_New_Level);
		this.view = view;
		this.viewer = viewer;
		this.undoContext = undoContext;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/level.gif"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		if (!FreemetricsPlugin.getDefault().isDocOpened()) {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionAddLevel_Information, LanguageText.ActionAddLevel_Create_Or_Open_Schema);
			return;
		}
		ISelection s = viewer.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			Object o = ss.getFirstElement();

			if (o instanceof TreeHierarchy) {
				ArrayList<DataObjectItem> columns = new ArrayList<DataObjectItem>();
				for (DataSource ds : FreemetricsPlugin.getDefault().getFAModel().getDataSources()) {
					for (DataObject t : ds.getDataObjects()) {
						if (t.getName().equalsIgnoreCase(((TreeHierarchy) o).getOLAPHierarchy().getTable())) {
							for (DataObjectItem c : t.getColumns())
								columns.add(c);
						}
					}
				}
				DialogAddLevel dial = new DialogAddLevel(view.getSite().getShell());
				;
				if (dial.open() == Dialog.OK) {
					OLAPHierarchy hiera = ((TreeHierarchy) o).getOLAPHierarchy();
					try {
						view.getOperationHistory().execute(new AddLevelOperation(LanguageText.ActionAddLevel_Add_Level, undoContext, hiera, dial.getLevel()), null, null);
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

}
