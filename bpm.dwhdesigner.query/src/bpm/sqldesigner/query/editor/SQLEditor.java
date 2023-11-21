package bpm.sqldesigner.query.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.connection.JoinConnection;
import bpm.sqldesigner.query.part.AppEditPartFactory;

public class SQLEditor extends ScrollingGraphicalViewer {

	private ZoomManager zoomManager;
	private FilterContextMenuProvider filterMenu;
	private LinkContextMenuProvider linkMenu;

	public SQLEditor(Composite editorC, EditDomain editDomain) {
		super();
		createControl(editorC);
		getControl().setBackground(ColorConstants.listBackground);
		getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		setEditPartFactory(new AppEditPartFactory());

		SQLRootPart rootEditPart = new SQLRootPart();
		setRootEditPart(rootEditPart);

		/***********************************************************************
		 * Zoom
		 **********************************************************************/
		double[] zoomLevels;
		ArrayList<String> zoomContributions;

		zoomManager = rootEditPart.getZoomManager();
		// getActionRegistry().registerAction(new ZoomInAction(manager));
		// getActionRegistry().registerAction(new ZoomOutAction(manager));

		zoomLevels = new double[] { 0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 2.5, 3.0,
				4.0, 5.0, 10.0, 20.0 };
		zoomManager.setZoomLevels(zoomLevels);

		zoomContributions = new ArrayList<String>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		zoomManager.setZoomLevelContributions(zoomContributions);

		filterMenu = new FilterContextMenuProvider(this);
		linkMenu = new LinkContextMenuProvider(this);

		setContextMenu(new NullContextMenuProvider(this));

		addSelectionChangedListener(new SelectionChangedListener());

	}

	public ZoomManager getZoomManager() {
		return zoomManager;
	}

	private SQLEditor getViewer() {
		return this;
	}

	protected class SelectionChangedListener implements
			ISelectionChangedListener {

		public void selectionChanged(SelectionChangedEvent event) {
			List<?> selected = getSelectedEditParts();
			if (selected.size() == 1) {
				EditPart e = (EditPart) selected.iterator().next();
				if (e.getModel() instanceof Column) {
					setContextMenu(filterMenu);
				} else if (e.getModel() instanceof JoinConnection) {
					setContextMenu(linkMenu);
				} else {
					setContextMenu(new NullContextMenuProvider(getViewer()));
				}
			}
		}
	}

}
