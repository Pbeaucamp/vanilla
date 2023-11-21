package bpm.workflow.ui.dialogs;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.SAXException;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.workflow.runtime.model.WorkflowDigester;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.editors.WorkflowEditorInput;
import bpm.workflow.ui.editors.WorkflowMultiEditorPart;
import bpm.workflow.ui.viewer.TreeContentProvider;
import bpm.workflow.ui.viewer.TreeDirectory;
import bpm.workflow.ui.viewer.TreeItem;
import bpm.workflow.ui.viewer.TreeLabelProvider;
import bpm.workflow.ui.viewer.TreeParent;

/**
 * Dialog for opening a workflow in the welcome dialog
 * 
 * @author Charles MARTIN
 * 
 */
public class DialOpenRepositoryBIW extends Dialog {

	private TreeViewer treeViewer;
	private TreeItem item;

	public DialOpenRepositoryBIW(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);

	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		buildTree();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite filter = new Composite(parent, SWT.NONE);
		filter.setLayout(new GridLayout(2, false));
		filter.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		treeViewer = new TreeViewer(container);
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new TreeLabelProvider());
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) treeViewer.getSelection();

				if(ss.isEmpty()) {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					return;
				}

				if(ss.getFirstElement() instanceof TreeItem) {
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
				else {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}

			}

		});

		return container;
	}

	private void buildTree() {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		IRepository rep;

		try {

			rep = new Repository(Activator.getDefault().getRepositoryConnection(), IRepositoryApi.BIW_TYPE);

			for(RepositoryDirectory dir : rep.getRootDirectories()) {
				TreeDirectory td = new TreeDirectory(dir);
				buildChilds(td, rep);

				for(RepositoryItem it : rep.getItems(dir)) {

					TreeItem ti = new TreeItem(it);
					td.addChild(ti);

				}
				root.addChild(td);
			}

		} catch(Exception e) {
			e.printStackTrace();
			this.close();
			MessageDialog.openError(getShell(), Messages.DialOpenRepositoryBIW_1, e.getMessage());
		}

		this.treeViewer.setInput(root);

	}

	private void buildChilds(TreeDirectory parent, IRepository rep) throws Exception {

		RepositoryDirectory dir = ((TreeDirectory) parent).getDirectory();

		for(RepositoryDirectory d : rep.getChildDirectories(dir)) {
			TreeDirectory td = new TreeDirectory(d);
			parent.addChild(td);
			for(RepositoryItem di : rep.getItems(d)) {
				TreeItem ti = new TreeItem(di);
				td.addChild(ti);

			}

			buildChilds(td, rep);
		}

	}

	@Override
	protected void okPressed() {
		item = (TreeItem) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
		if(item instanceof TreeItem) {

			IRepositoryApi sock = Activator.getDefault().getRepositoryConnection();
			try {
				String xml = sock.getRepositoryService().loadModel(((TreeItem) item).getItem());

				Activator.getDefault().setCurrentDirect(((TreeItem) item).getItem());

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

				} catch(Exception e) {
					e.printStackTrace();
				}

			} catch(Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), Messages.DialOpenRepositoryBIW_3, e.getMessage());
			}

		}
		super.okPressed();
	}

	/**
	 * 
	 * @return the Idirectoryitem of the selected workflow
	 */
	public TreeItem getRepositoryItem() {
		return item;
	}
}
