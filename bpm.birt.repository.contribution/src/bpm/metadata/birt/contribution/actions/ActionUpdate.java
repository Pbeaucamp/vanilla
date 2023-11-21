package bpm.metadata.birt.contribution.actions;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.birt.report.designer.internal.ui.editors.ReportEditorInput;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.forms.editor.FormEditor;

import bpm.metadata.birt.contribution.Activator;
import bpm.metadata.birt.contribution.helper.BirtDependency;
import bpm.metadata.birt.contribution.helper.BirtReport;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ActionUpdate implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	@Override
	public void run(IAction action) {
		FormEditor p = org.eclipse.birt.report.designer.internal.ui.util.UIUtil.getActiveReportEditor();
		org.eclipse.birt.report.designer.internal.ui.editors.ReportEditorInput i = (ReportEditorInput) p.getEditorInput();
		p.doSave(new IProgressMonitor() {	
			@Override
			public void worked(int work) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void subTask(String name) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setTaskName(String name) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCanceled(boolean value) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isCanceled() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void internalWorked(double work) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void done() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beginTask(String name, int totalWork) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
//		IURIEditorInput input = (IURIEditorInput) p.getEditorInput();
//		URI uri = input.getURI();
		String path = i.getPath().toString().toString().replace("file:/", "");

		Integer itemId = Activator.getDefault().getItemIdByPath(path);

		if(itemId != null) {

			try {
				FileInputStream fis = new FileInputStream(path);

				String xml = IOUtils.toString(fis, "UTF-8");
				fis.close();

				BirtReport reportFile = new BirtReport(new File(path), xml);
				IRepositoryApi sock = Activator.getDefault().getRepositoryApi();
				RepositoryItem item = sock.getRepositoryService().getDirectoryItem(itemId);
				sock.getRepositoryService().updateModel(item, xml);
				StringBuffer errors = new StringBuffer();
				for(BirtDependency dep : reportFile.getDependencies()) {
					if(!dep.hasError()) {
						File f = dep.getDepFile();
						LinkedDocument ld = null;
						try {
							ld = sock.getDocumentationService().attachDocumentToItem(item, new FileInputStream(f), dep.getFileName(), "Dependency of " + item.getName(), dep.getDepFile().getName().substring(dep.getDepFile().getName().lastIndexOf(".") + 1), "");
							sock.getAdminService().addGroupForLinkedDocument(sock.getContext().getGroup(), ld);
						} catch(Exception ex) {
							if(ld == null) {
								errors.append("Failed to attach file " + dep.getFileName() + "\n");
							}
							else {
								errors.append("Failed to authorize access for group " + sock.getContext().getGroup().getName() + " on " + dep.getFileName() + "\n");
							}
						}

					}
				}
				Shell dialog = new Shell(window.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				dialog.setSize(100, 100);
				MessageDialog.openInformation(dialog, "Update report", "Report updated with success");

			} catch(Exception e) {
				Shell dialog = new Shell(window.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				dialog.setSize(100, 100);
				MessageDialog.openError(dialog, "Error while updating report ", e.getMessage()); //$NON-NLS-1$
				e.printStackTrace();
			}
		}
		else {
			Shell dialog = new Shell(window.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			dialog.setSize(100, 100);
			MessageDialog.openInformation(dialog, "Update report", "This report wasn't loaded from the repository");
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
