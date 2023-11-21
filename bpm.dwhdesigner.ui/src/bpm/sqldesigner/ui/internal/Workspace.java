package bpm.sqldesigner.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.document.SchemaView;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.editor.SQLDesignEditorInput;
import bpm.sqldesigner.ui.editor.SQLDesignGraphicalEditor;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.snapshot.editor.SnapshotEditor;
import bpm.sqldesigner.ui.snapshot.editor.SnapshotEditorInput;
import bpm.sqldesigner.ui.view.RequestsView;
import bpm.sqldesigner.ui.view.TreeView;

/**
 * simple class to hold the open DataBaseClusters in the workspace
 * opened SchemaView, DOcumentSnapshot
 * 
 * A workspace can be saved and reloaded from the File Menu
 * @author ludo
 *
 */
public class Workspace {

	private List<DatabaseCluster> openedClusters = new ArrayList<DatabaseCluster>();
	
	private List<DocumentSnapshot> openedSnapshots = new ArrayList<DocumentSnapshot>();
	private List<SchemaView> openedSchemaViews= new ArrayList<SchemaView>();
	
	private boolean closed = false;
	
	private String fileName;
	
	private IPartListener closeEditorListener =  new IPartListener(){

		public void partActivated(IWorkbenchPart part) {
			
			
		}

		public void partBroughtToTop(IWorkbenchPart part) {
			
			
		}

		public void partClosed(IWorkbenchPart part) {
			if (part instanceof SnapshotEditor){
				remove(((SnapshotEditorInput)((SnapshotEditor)part).getEditorInput()).getSnapshot());
			}
			else if (part instanceof SQLDesignGraphicalEditor){
				remove(((SQLDesignEditorInput)((SQLDesignGraphicalEditor)part).getEditorInput()).getSchemaView());
			}
			
			
		}

		public void partDeactivated(IWorkbenchPart part) {
			
			
		}

		public void partOpened(IWorkbenchPart part) {
			try{
				if (part instanceof SnapshotEditor){
					addSnapshot(((SnapshotEditorInput)((SnapshotEditor)part).getEditorInput()).getSnapshot());
				}
				else if (part instanceof SQLDesignGraphicalEditor){
					addSchemaView(((SQLDesignEditorInput)((SQLDesignGraphicalEditor)part).getEditorInput()).getSchemaView());
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}

			
		}

		
		
	};
	
	public Workspace(){
		
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(closeEditorListener);
	}
	
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private void refreshTreeViewer(){
		if (closed){
			return;
		}
		IViewReference ref = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findViewReference(TreeView.ID);
		
		
		if (ref != null){
			((TreeView)ref.getView(false)).refresh();
		}

	}
	
	public void addDatabaseCluster(DatabaseCluster cluster) throws Exception{
		if (cluster == null){
			return;
		}
		
		for(DatabaseCluster c : openedClusters){
			if (cluster == c || cluster.getFileName().equals(c.getFileName())){
//				throw new Exception("This database cluster is already opened");
				return;
			}
		}
		
		openedClusters.add(cluster);
		refreshTreeViewer();
		
		IViewReference reqViewref = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findViewReference(RequestsView.ID);
		((RequestsView)reqViewref.getView(true)).addTab(cluster);
	}
	
	public void remove(DatabaseCluster cluster){
		openedClusters.remove(cluster);
		
		List toRemove = new ArrayList(); 
		for(SchemaView v : openedSchemaViews){
			if (v.getCluster() == cluster){
				toRemove.add(v);
			}
		}
		openedSchemaViews.removeAll(toRemove);
		
		toRemove.clear();
		
		for(DocumentSnapshot v : openedSnapshots){
			if (v.getCluster() == cluster){
				toRemove.add(v);
			}
		}
		openedSnapshots.removeAll(toRemove);
		refreshTreeViewer();
		
		IViewReference reqViewref = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findViewReference(RequestsView.ID);
		((RequestsView)reqViewref.getView(true)).removeTab(cluster);
	}
	
	
	public void close(){
		closed = true;
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().removePartListener(closeEditorListener);
		
		
		IViewReference reqViewref = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findViewReference(RequestsView.ID);
		
		for(DatabaseCluster cluster : getOpenedClusters()){
			((RequestsView)reqViewref.getView(true)).removeTab(cluster);
		}
		
	}
	
	public void addSnapshot(DocumentSnapshot snapshot) throws Exception{
		if (snapshot == null){
			return;
		}
		
		for(DocumentSnapshot s : openedSnapshots){
			if (s == snapshot){
				throw new Exception(Messages.Workspace_0);
			}
			
		}
		openedSnapshots.add(snapshot);
		refreshTreeViewer();
	}
	
	public void addSchemaView(SchemaView view) throws Exception{
		if (view == null){
			return;
		}
		
		for(SchemaView s : openedSchemaViews){
			if (s == view){
				throw new Exception(Messages.Workspace_1);
			}
			
		}
		openedSchemaViews.add(view);
		refreshTreeViewer();
	}

	public void remove(DocumentSnapshot dwhView){
		openedSnapshots.remove(dwhView);
		refreshTreeViewer();
	}
	
	public void remove(SchemaView view){
		openedSchemaViews.remove(view);
		refreshTreeViewer();
	}

	public List<DatabaseCluster> getOpenedClusters() {
		return new ArrayList<DatabaseCluster>(openedClusters);
	}

	public List<DocumentSnapshot> getOpenedDocumentSnapshots() {
		return new ArrayList<DocumentSnapshot>(openedSnapshots);
	}
	
	public List<SchemaView> getOpenedSchemaViews() {
		return new ArrayList<SchemaView>(openedSchemaViews);
	}
}
