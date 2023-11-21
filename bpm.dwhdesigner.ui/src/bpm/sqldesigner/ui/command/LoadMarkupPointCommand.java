package bpm.sqldesigner.ui.command;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.document.SchemaView;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.xml.ReadData;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.editor.SQLDesignEditorInput;
import bpm.sqldesigner.ui.editor.SQLDesignGraphicalEditor;
import bpm.sqldesigner.ui.snapshot.editor.SnapshotEditor;
import bpm.sqldesigner.ui.snapshot.editor.SnapshotEditorInput;
import bpm.sqldesigner.ui.view.RequestsView;

public class LoadMarkupPointCommand extends Command {

	private String file;
//	private TreeView treeView;
	private String errors;
	public LoadMarkupPointCommand(Request renameReq) {
		file = (String) renameReq.getExtendedData().get("file"); //$NON-NLS-1$
//		treeView = (TreeView) renameReq.getExtendedData().get("workbenchPart");

	}

	@Override
	public boolean canExecute() {
		return true;
	}

	@Override
	public void execute() {
		DatabaseCluster databaseCluster;
		try {
			
			
			databaseCluster = ReadData.readCatalogsList(file);
			
			
			Activator.getDefault().getWorkspace().addDatabaseCluster(databaseCluster);
			
			//read designed views
			
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				
			
			IViewReference ref = page.findViewReference(RequestsView.ID);
			((RequestsView)ref.getView(true)).addTab(databaseCluster);
			
			
//			for(SchemaView v : databaseCluster.getSchemaViews()){
//				SQLDesignEditorInput input = new SQLDesignEditorInput(v);
//				try {
//					IEditorPart editorPart = page.openEditor(input,SQLDesignGraphicalEditor.ID,true);
//					SQLDesignGraphicalEditor editor = (SQLDesignGraphicalEditor)editorPart;
//					editor.setSchemaLoaded(true);
//					((ScalableRootEditPart) editor.getViewer()
//							.getRootEditPart()).getZoomManager()
//							.setZoom(v.getScale());
//					editor.getViewer().setContents(v.getSchema());
//					Activator.getDefault().getWorkspace().addSchemaView(v);
//					editor.setSchemaLoaded(true);
//					
//				} catch (PartInitException e) {
//					e.printStackTrace();
//				}
//			}
//			
//			for(DocumentSnapshot v : databaseCluster.getDocumentSnapshots()){
//				SnapshotEditorInput input = new SnapshotEditorInput(v);
//				try {
//					IEditorPart editorPart = page.openEditor(input,SnapshotEditor.ID,true);
//							
//					Activator.getDefault().getWorkspace().addSnapshot(v);
//				} catch (PartInitException e) {
//					e.printStackTrace();
//				}
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
			setLabel(e.getMessage());
			errors = e.getMessage();
		}
	}

	public String getErrors(){
		return errors;
	}
}
