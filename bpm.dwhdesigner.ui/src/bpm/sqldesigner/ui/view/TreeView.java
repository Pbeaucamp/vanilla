package bpm.sqldesigner.ui.view;

import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.sqldesigner.api.database.ExtractData;
import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.document.SchemaView;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.procedure.SQLProcedure;
import bpm.sqldesigner.api.model.view.SQLView;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.action.CompareAction;
import bpm.sqldesigner.ui.action.OpenClusterAction;
import bpm.sqldesigner.ui.action.SaveMarkupPointAction;
import bpm.sqldesigner.ui.editor.SQLDesignEditorInput;
import bpm.sqldesigner.ui.editor.SQLDesignGraphicalEditor;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.model.ClustersManager;
import bpm.sqldesigner.ui.model.ListClusters;
import bpm.sqldesigner.ui.snapshot.editor.SnapshotEditor;
import bpm.sqldesigner.ui.snapshot.editor.SnapshotEditorInput;
import bpm.sqldesigner.ui.view.tree.DatabaseTreeContentProvider;
import bpm.sqldesigner.ui.view.tree.MenuTree;
import bpm.sqldesigner.ui.view.tree.TreeLabelProvider;

public class TreeView extends ViewPart implements PropertyChangeListener{

	public static final String ID = "bpm.sqldesigner.ui.treeview"; //$NON-NLS-1$

	TreeViewer tree;
	private ListClusters listClusters = ClustersManager.getInstance();
	private SaveMarkupPointAction saveMarkupPointAction;
	private CompareAction compareAction;
	private LightweightSystem lws;
	private Action delete, openInEditor;
	
	

	private Canvas canvas;

	private SashForm sh;

	
	public TreeView() {
		Activator.getDefault().addWorkspaceListener(this);
	}
	@Override
	public void createPartControl(Composite parent) {
		sh = new SashForm(parent, SWT.VERTICAL);
		sh.setLayoutData(new GridData(GridData.FILL_BOTH));
		sh.setLayout(new GridLayout(1, false));

		tree = new TreeViewer(sh);
		tree.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		tree.setLabelProvider(new TreeLabelProvider());
		tree.setContentProvider(new DatabaseTreeContentProvider());

		tree.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				Node node = (Node) ((TreeSelection) tree.getSelection()).getFirstElement();
				try {
					expandTreeData(node);
				} catch (SQLException e) {
					e.printStackTrace();
				}

				
				tree.refresh();

				saveMarkupPointAction.update();
				compareAction.update();

			}
		});

		tree.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				
				if (tree.getSelection().isEmpty()){
					return;
				}

				Object o = ((IStructuredSelection)tree.getSelection()).getFirstElement();
				if (o instanceof DocumentSnapshot || o instanceof SchemaView){
					openInEditor.run();
				}
				else if (o instanceof Catalog || o instanceof Schema){
					openSchemaView();
				}
			}

		});

		tree.addTreeListener(new ITreeViewerListener() {


			public void treeCollapsed(TreeExpansionEvent event) {
			}

			public void treeExpanded(TreeExpansionEvent event) {
				Node node = (Node) event.getElement();
				try {
					expandTreeData(node);
				} catch (SQLException e) {
					e.printStackTrace();
				}

				tree.refresh();
				tree.expandToLevel(node, 1);
			}

		});

		tree.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK,
				new Transfer[] { FileTransfer.getInstance() },
				new DragSourceListener() {


					public void dragFinished(DragSourceEvent event) {
					}

					public void dragSetData(DragSourceEvent event) {
						Node node = (Node) ((TreeSelection) tree.getSelection())
								.getFirstElement();
						List<String> parameters = new ArrayList<String>();

						if ((node instanceof SQLView)
								|| (node instanceof SQLProcedure)
								|| (node instanceof DatabaseCluster))
							return;

						for (; node != null; node = node.getParent()) {
							parameters.add(node.getName());
						}

						Collections.reverse(parameters);
						event.data = parameters.toArray(new String[] {});
					}


					public void dragStart(DragSourceEvent event) {
					}

				});

		

		tree.setInput(listClusters);
		createToolbar();

		canvas = new Canvas(sh, SWT.NONE);
		canvas.setLayoutData(new GridData(GridData.CENTER, GridData.END, true,
				false));

		lws = new LightweightSystem(canvas);

		sh.setWeights(new int[] { 6, 1 });

		MenuTree.createMenu(tree);
	}

	

	@Override
	public void setFocus() {

	}

	private void expandTreeData(Node node) throws SQLException {
		if (node == null){
			return;
		}
		if (node.isNotLoaded() && node.getDatabaseConnection() != null) {

			if (node instanceof Catalog) {
				Catalog catalog = (Catalog) node;

				ExtractData.extractSchemas(catalog);

				if (catalog.getSchemas().size() == 1) {
					Schema schema = catalog.getSchema("null"); //$NON-NLS-1$
					if (schema != null) {
						ExtractData.extractTablesAndViews(schema);
						ExtractData.extractProcedures(schema);
						schema.setNotLoaded(false);
					}
				}

				catalog.setNotLoaded(false);

			} else if (node instanceof Schema) {
				Schema schema = (Schema) node;
				ExtractData.extractTablesAndViews(schema);
				ExtractData.extractProcedures(schema);
				schema.setNotLoaded(false);

			} else if (node instanceof Table) {
				Table table = (Table) node;
				ExtractData.extractColumns(table);
				table.setNotLoaded(false);
			}
		}
	}

	private void createToolbar() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(new OpenClusterAction(this));
		saveMarkupPointAction = new SaveMarkupPointAction(this);
		mgr.add(saveMarkupPointAction);
		compareAction = new CompareAction(this);
		mgr.add(compareAction);
		
		
		
		
		
		delete = new Action(Messages.TreeView_2){
			public void run(){
				IStructuredSelection ss = (IStructuredSelection)tree.getSelection();
				if (ss.isEmpty()){
					return;
				}
				
				List<IEditorReference> l = new ArrayList<IEditorReference>();
				if (ss.getFirstElement() instanceof DocumentSnapshot){
					((DocumentSnapshot)ss.getFirstElement()).getDatabaseCluster().removeDocumentSnapshot((DocumentSnapshot)ss.getFirstElement());

					for(IEditorReference ref : Activator.getDefault().getEditorsFor(SnapshotEditorInput.class)){
						
						try{
							if (((SnapshotEditorInput)ref.getEditorInput()).getSnapshot() == ss.getFirstElement()){
								l.add(ref);
							}
						}catch(Exception ex){
							ex.printStackTrace();
						}
						
					}
					
				}
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditors(l.toArray(new IEditorReference[l.size()]), false);
				
				
				tree.refresh();
			}
		};
		delete.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("close"))); //$NON-NLS-1$
		mgr.add(delete);
		
		
		openInEditor = new Action(Messages.TreeView_4){
			public void run(){
				Object o = ((IStructuredSelection)tree.getSelection()).getFirstElement();
				
				IEditorInput input = null;
				String editorId = null;
				if (o instanceof SchemaView){
					
					
					for(IEditorReference r : Activator.getDefault().getEditorsFor(SQLDesignEditorInput.class)){
						
						try{
							if (((SQLDesignEditorInput)r.getEditorInput()).getSchemaView() == o){
								Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(r.getPart(false));
								return;
							}
						}catch(Exception ex){
							ex.printStackTrace();
						}
						
					}
					input = new SQLDesignEditorInput((SchemaView)o);
					editorId = new String(SQLDesignGraphicalEditor.ID);	
				}
				else if (o instanceof DocumentSnapshot){
					
					
					for(IEditorReference r : Activator.getDefault().getEditorsFor(SnapshotEditorInput.class)){
						
						try{
							if (((SnapshotEditorInput)r.getEditorInput()).getSnapshot() == o){
								Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(r.getPart(false));
								return;
							}
						}catch(Exception ex){
							ex.printStackTrace();
						}
						
					}
					input = new SnapshotEditorInput((DocumentSnapshot)o);
					editorId = new String(SnapshotEditor.ID);
				}
				
				if (input != null && editorId != null){
					try {
						IEditorPart editorPart = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, editorId, true);
						
						if (input instanceof SQLDesignEditorInput){
							((SQLDesignGraphicalEditor)editorPart).setSchemaLoaded(true);
						}
						
					} catch (PartInitException e) {
						
						e.printStackTrace();
					}
				}
				
				
			}
		};
	}

//	public void addDb(DatabaseCluster databaseCluster) {
//		if (listClusters.getCluster(databaseCluster.getName()) == null) {
//			listClusters.addCluster(databaseCluster);
//			
//			databaseCluster.addPropertyChangeListener(new PropertyChangeListener() {
//				
//				public void propertyChange(PropertyChangeEvent evt) {
//					tree.refresh();
//					
//				}
//			});
//			tree.collapseAll();
//			tree.refresh();
//		} else
//			MessageDialog.openError(getSite().getShell(), "Error", "A cluster named " + databaseCluster.getName() + " already exists.");
//	}

	public DatabaseCluster dbExists(String host, String port, String name) {
		if (name == null)
			return listClusters.dbExists(host, port);
		else
			return listClusters.mpExists(name);
	}

//	public void removeDb(DatabaseCluster databaseCluster) {
//		listClusters.removeCluster(databaseCluster);
//		tree.collapseAll();
//		tree.refresh();
//
//		if (databaseCluster.getDatabaseConnection() == null)
//			return;
//
//		((RequestsView) Activator.getDefault().getWorkbench()
//				.getActiveWorkbenchWindow().getActivePage().findView(
//						RequestsView.ID)).removeTab(databaseCluster);
//	}

	public ISelection getSelected() {
		return tree.getSelection();
	}

//	public void collapse(DatabaseCluster cluster) {
//		listClusters.removeCluster(cluster);
//	}

//	public TreeViewer getTree() {
//		return tree;
//	}

	

	public void clear() {
		tree.getTree().removeAll();
		listClusters.getClusters().clear();
		tree.collapseAll();
		tree.refresh();
	}



	public void refresh() {
		tree.refresh();
		
	}
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("workspace")){ //$NON-NLS-1$
			tree.setInput(Activator.getDefault().getWorkspace());
		}
		
		refresh();
		
	}



	private void openSchemaView(){
		Object o = ((IStructuredSelection)tree.getSelection()).getFirstElement();
		if (!(o instanceof Schema) && ! (o instanceof Catalog)){
			return;
		}
		
		DatabaseCluster cluster = (DatabaseCluster)((Node)o).getParent();
		
		
		
		SchemaView view = null;
		if (o instanceof Schema){
			view =cluster.getSchemaView((Schema)o);
		}
		else{
			view = cluster.getSchemaView((Catalog)o);
		}
		
		try{
			for(IEditorReference ref : Activator.getDefault().getEditorsFor(SQLDesignEditorInput.class)){
				if (((SQLDesignEditorInput)ref.getEditorInput()).getSchemaView() == view){
					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(ref.getEditor(false));
					return;
				}
			}
		}catch(Exception ex){
			
		}
		
						
		/*
		 * open editor
		 */
		SQLDesignEditorInput input = new SQLDesignEditorInput(view);
		try {
			IEditorPart editorPart = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,SQLDesignGraphicalEditor.ID,false);
			((SQLDesignGraphicalEditor)editorPart).setSchemaLoaded(true);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
