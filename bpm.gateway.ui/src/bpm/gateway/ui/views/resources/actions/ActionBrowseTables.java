package bpm.gateway.ui.views.resources.actions;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;

import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.dialogs.database.DataBaseBrowserDialog;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.gef.model.GIDModel;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.NodeException;
import bpm.gateway.ui.gef.part.GIDEditPart;

public class ActionBrowseTables extends Action {

	private Viewer viewer;

	public ActionBrowseTables(String name, Viewer viewer) {
		super(name);
		this.viewer = viewer;
	}

	public void run() {
		IStructuredSelection ss = getSelection();

		if (ss.isEmpty() || !(ss.getFirstElement() instanceof DataBaseConnection || ss.getFirstElement() instanceof DataBaseServer)) {
			return;
		}
		
		DataBaseServer server = null;
		if (ss.getFirstElement() instanceof DataBaseServer) {
			server = (DataBaseServer) ss.getFirstElement();
		}
		else if (ss.getFirstElement() instanceof DataBaseConnection) {
			DataBaseConnection connection = (DataBaseConnection) ss.getFirstElement();
			server = (DataBaseServer) connection.getServer();
		}

		DataBaseBrowserDialog d = new DataBaseBrowserDialog(viewer.getControl().getShell(), server);
		if (d.open() == Dialog.OK) {
			GatewayEditorPart editor = Activator.getDefault().getCurrentEditor();
			GatewayEditorInput input = (GatewayEditorInput) editor.getEditorInput();
			if (input.getContainer() != null) {
				try {
					DataBaseInputStream dbInput = new DataBaseInputStream();
					dbInput.setName(d.getTableName());
					dbInput.setServer(server);
					dbInput.setDefinition(d.getSqlStatement());
					dbInput.setPositionX(300);
					dbInput.setPositionY(20);

					Node node = new Node();
					node.setName(dbInput.getName());
					node.setTransformation(dbInput);

					ISelection s = editor.getSite() != null && editor.getSite().getSelectionProvider() != null ? editor.getSite().getSelectionProvider().getSelection() : null;
					if (s != null && !s.isEmpty()){
						Object o = ((IStructuredSelection)s).getFirstElement();
						if (o instanceof GIDEditPart) {
							GIDModel gidModel = (GIDModel) ((GIDEditPart) o).getModel();
							int x = gidModel.getX();
							int y = gidModel.getY();

							Rectangle constraint = new Rectangle();
				            constraint.x = 20 + x;
				            constraint.y = 40 + y;
				            constraint.width = 50;
				            constraint.height = 50;
							node.setLayout(constraint);
							
							gidModel.addChild(node);
							return;
						}
					}

					input.getContainer().addChild(node);
				} catch (NodeException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private IStructuredSelection getSelection() {
		return (IStructuredSelection) viewer.getSelection();
	}
}
