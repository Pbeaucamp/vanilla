package org.fasd.views.actions;

import java.util.ArrayList;
import java.util.List;

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
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPRelation;
import org.fasd.utils.trees.TreeColumn;
import org.fasd.utils.trees.TreeConnection;
import org.fasd.utils.trees.TreeDatabase;
import org.fasd.utils.trees.TreeRelation;
import org.fasd.utils.trees.TreeTable;
import org.fasd.views.SQLView;
import org.fasd.views.operations.DeleteConnectionOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionDeleteConnection extends Action {
	private SQLView view;
	private TreeViewer tree;
	private UndoContext undoContext;

	public ActionDeleteConnection(SQLView view, TreeViewer tree, UndoContext undoContext) {
		super(LanguageText.ActionDeleteConnection_Del);
		this.view = view;
		this.tree = tree;
		this.undoContext = undoContext;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/del.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		System.out.println("Action remove connection"); //$NON-NLS-1$
		ISelection selection = tree.getSelection();

		if (!(selection instanceof IStructuredSelection)) {
			System.out.println("unstructured for delete, aborted"); //$NON-NLS-1$
			return;
		}

		IStructuredSelection ss = (IStructuredSelection) selection;
		Object object = ss.getFirstElement();
		System.out.println("class is " + object.getClass()); //$NON-NLS-1$
		FAModel model = FreemetricsPlugin.getDefault().getFAModel();

		if (object instanceof TreeDatabase) {
			DataSource driver = ((TreeDatabase) object).getDriver();
			try {
				view.getOperationHistory().execute(new DeleteConnectionOperation(LanguageText.ActionDeleteConnection_Del_DS, undoContext, model, driver), null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} else if (object instanceof TreeConnection) {
			DataSourceConnection sock = ((TreeConnection) object).getDriver();
			if (sock.getParent().getDriver() == sock) {
				MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionDeleteConnection_0, LanguageText.ActionDeleteConnection_1);
				return;
			} else {
				sock.getParent().removeConnection(sock);
				view.refresh(true);
			}
		} else if (object instanceof TreeRelation) {
			OLAPRelation r = ((TreeRelation) object).getOLAPRelation();
			try {
				view.getOperationHistory().execute(new DeleteConnectionOperation(LanguageText.ActionDeleteConnection_Del_Rel, undoContext, model, r), null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} else if (object instanceof TreeColumn) {
			DataObjectItem item = ((TreeColumn) object).getColumn();
			if (((TreeColumn) object).getColumn().getType().equals("calculated")) { //$NON-NLS-1$
				try {
					view.getOperationHistory().execute(new DeleteConnectionOperation(LanguageText.ActionDeleteConnection_Del_Cal_Column, undoContext, model, item), null, null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		} else if (object instanceof TreeTable) {
			DataObject table = ((TreeTable) object).getTable();

			List cascade = new ArrayList();

			for (OLAPRelation r : model.getRelations()) {
				if ((r.getLeftObject() != null && r.getLeftObject().getId().equals(table.getId()) || (r.getRightObject() != null && r.getRightObject().getId().equals(table.getId())))) {
					cascade.add(r);
				}
			}

			for (OLAPDimension d : model.getOLAPSchema().getDimensions()) {
				for (OLAPHierarchy h : d.getHierarchies()) {
					for (OLAPLevel l : h.getLevels()) {
						if ((l.getItem() != null && l.getItem().getParent().getId().equals(table.getId())) || (l.getClosureTable() != null && l.getClosureTable().getId().equals(table.getId()))) {
							cascade.add(l);
						}
					}
				}
			}

			for (OLAPCube c : model.getOLAPSchema().getCubes()) {
				if (c.getFactDataObject().getId().equals(table.getId())) {
					cascade.add(c);
				}

			}

			StringBuffer message = new StringBuffer();
			if (!cascade.isEmpty()) {
				message.append(LanguageText.ActionDeleteConnection_2);
			} else {
				message.append(LanguageText.ActionDeleteConnection_3);
			}

			boolean b = MessageDialog.openQuestion(tree.getControl().getShell(), LanguageText.ActionDeleteConnection_4, message.toString());

			if (b) {
				boolean refrehDims = false;
				boolean refrehMes = false;
				boolean refrehCubes = false;

				message = new StringBuffer();

				for (Object o : cascade) {
					if (o instanceof OLAPRelation) {
						model.removeRelation((OLAPRelation) o);
						message.append(LanguageText.ActionDeleteConnection_5 + ((OLAPRelation) o).getName() + "\n"); //$NON-NLS-1$
					} else if (o instanceof OLAPLevel) {
						((OLAPLevel) o).getParent().removeLevel((OLAPLevel) o);
						message.append(LanguageText.ActionDeleteConnection_7 + ((OLAPLevel) o).getName() + "\n"); //$NON-NLS-1$
						refrehDims = true;
					} else if (o instanceof OLAPMeasure) {
						model.getOLAPSchema().removeMeasure((OLAPMeasure) o);
						message.append(LanguageText.ActionDeleteConnection_9 + ((OLAPMeasure) o).getName() + "\n"); //$NON-NLS-1$
						refrehMes = true;
					} else if (o instanceof OLAPCube) {
						model.getOLAPSchema().removeCube((OLAPCube) o);
						message.append(LanguageText.ActionDeleteConnection_11 + ((OLAPCube) o).getName() + "\n"); //$NON-NLS-1$
						refrehCubes = true;
					}
				}
				table.getDataSource().removeDataObject(table);
				message.append(LanguageText.ActionDeleteConnection_13 + table.getName());

				FreemetricsPlugin.getDefault().getFAModel().getListDataSource().setChanged();
				if (refrehDims) {
					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
				}
				if (refrehMes) {
					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMeasures().setChanged();
				}
				if (refrehCubes) {
					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
				}

				MessageDialog.openInformation(tree.getControl().getShell(), LanguageText.ActionDeleteConnection_14, message.toString());
			}
		}

	}
}
