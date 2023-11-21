package bpm.workflow.ui.actions;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.SAXException;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.workflow.runtime.model.WorkflowDigester;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.BiWorkFlowActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.editors.WorkflowEditorInput;
import bpm.workflow.ui.editors.WorkflowMultiEditorPart;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Open a Workflow in a new tab from a right click on a node
 * 
 * @author Charles MARTIN
 * 
 */
public class ActionOpenBIW extends Action {

	String path;

	public ActionOpenBIW() {

	}

	public void run() {

		ISelection s = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();

		if(s.isEmpty()) {
			return;
		}

		Object o = ((IStructuredSelection) s).getFirstElement();

		if(o instanceof NodePart) {

			IRepositoryApi sock = Activator.getDefault().getRepositoryConnection();
			try {

				Node n = (Node) ((NodePart) o).getModel();
				Object workobj = n.getWorkflowObject();

				if(workobj instanceof BiWorkFlowActivity) {
					System.out.println(((BiWorkFlowActivity) workobj).getBiObject().getId());
					int id = ((BiWorkFlowActivity) workobj).getBiObject().getId();
					RepositoryItem item = sock.getRepositoryService().getDirectoryItem(id);
					String xml = sock.getRepositoryService().loadModel(item);

					Activator.getDefault().setCurrentDirect(item);

					WorkflowModel doc = null;
					InputStream is = IOUtils.toInputStream(xml, "UTF-8"); //$NON-NLS-1$
					try {
						doc = WorkflowDigester.getModel(WorkflowModel.class.getClassLoader(), is);
					} catch(IOException e1) {
						e1.printStackTrace();
					} catch(SAXException e1) {
						e1.printStackTrace();
					}

					WorkflowEditorInput input = new WorkflowEditorInput(doc.getName(), doc);

					try {
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						page.openEditor(input, WorkflowMultiEditorPart.ID, true);
						Activator.getDefault().getSessionSourceProvider().setModelOpened(true);

					} catch(Exception e) {
						e.printStackTrace();
					}

				}
			} catch(Exception e) {
				e.printStackTrace();

			}
		}

	}

}
