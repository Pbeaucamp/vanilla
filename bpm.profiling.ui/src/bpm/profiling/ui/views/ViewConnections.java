package bpm.profiling.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;

import bpm.profiling.runtime.core.Column;
import bpm.profiling.runtime.core.Connection;
import bpm.profiling.runtime.core.Table;
import bpm.profiling.ui.Activator;
import bpm.profiling.ui.connection.wizard.ConnectionWizard;
import bpm.profiling.ui.trees.TreeColumn;
import bpm.profiling.ui.trees.TreeConnection;
import bpm.profiling.ui.trees.TreeContentProvider;
import bpm.profiling.ui.trees.TreeLabelProvider;
import bpm.profiling.ui.trees.TreeObject;
import bpm.profiling.ui.trees.TreeParent;
import bpm.profiling.ui.trees.TreeTable;

public class ViewConnections extends ViewPart {

	public static final String ID = "bpm.profiling.ui.views.ViewConnections";
	
	private TreeViewer viewer;
	private ToolBar toolbar;
	private Action addConnection, deleteConnection;

	public ViewConnections() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

				
		viewer = new TreeViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setComparator(new ViewerComparator());
		
		
		getSite().setSelectionProvider(viewer);
		createActions();
		createToolbar();
		loadModels();
	}

	@Override
	public void setFocus() {
		

	}
	
	public void loadModels(){
		TreeParent root = new TreeParent("");
		
		for(Connection c : Activator.getDefault().getConnections()){
			TreeConnection tc = new TreeConnection(c);
			root.addChild(tc);
			for(Table t : c.getTables()){
				TreeTable tt = new TreeTable(t);
				tc.addChild(tt);
				
				for(Column cc : t.getColumns()){
					TreeColumn tcc = new TreeColumn(cc);
					tt.addChild(tcc);
				}
			}
		}
		
		viewer.setInput(root);
	
	}

	
	private void createToolbar(){
		ToolItem connection = new ToolItem(toolbar, SWT.PUSH);
		connection.setImage(Activator.getDefault().getImageRegistry().get("connection"));
		connection.setToolTipText(addConnection.getText());
		connection.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				addConnection.run();
			}
		});
		
		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText("delete connection");
		del.setImage(Activator.getDefault().getImageRegistry().get("delete"));
		del.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				deleteConnection.run();
			}
		});
		

	}

	private void createActions(){
		addConnection = new Action("new Connection"){
			public void run(){
				ConnectionWizard wizard = new ConnectionWizard();
				wizard.init(Activator.getDefault().getWorkbench(),
							(IStructuredSelection)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());
				
				WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
				dialog.create();
				dialog.getShell().setSize(800, 600);
				dialog.getShell().setText(""); 
				
				if (dialog.open() ==  WizardDialog.OK){   
					
					Activator.getDefault().getConnections().add(wizard.getConnection());
					

					
					
					TreeConnection tc = new TreeConnection(wizard.getConnection());
					for(Table t : wizard.getConnection().getTables()){
						TreeTable tt = new TreeTable(t);
						tc.addChild(tt);
						
						for(Column cc : t.getColumns()){
							TreeColumn tcc = new TreeColumn(cc);
							tt.addChild(tcc);
						}
					}
					
					((TreeParent)viewer.getInput()).addChild(tc);
					viewer.refresh();
					
				
					
				}
			}
		};
		addConnection.setToolTipText("Add new Connection");
//		addConnection.setImageDescriptor(newImage)
	
	
		
	
		deleteConnection = new Action("Delete Connections"){
			public void run(){
				if (viewer.getSelection().isEmpty()){
					return;
				}
				
				for(Object o : ((IStructuredSelection)viewer.getSelection()).toList()){
					if (o instanceof TreeConnection){
						
						try{
							boolean b = Activator.helper.getAnalysisManager().deleteConnection(((TreeConnection)o).getConnection());
							Activator.helper.getConnectionManager().deleteConnection(((TreeConnection)o).getConnection());
							
							for(Connection c : Activator.getDefault().getConnections()){
								if (c.getId() == ((TreeConnection)o).getConnection().getId()){
									Activator.getDefault().getConnections().remove(c);
									break;
								}
							}
							
							
							
							if (b){
								IViewPart v = getSite().getWorkbenchWindow().getActivePage().findView(ViewAnalysis.ID);
								
								if (v != null){
									((ViewAnalysis)v).setInput();
								}
							}
							
							((TreeConnection)o).getParent().removeChild((TreeConnection)o);
							Activator.getDefault().getConnections().remove(((TreeConnection)o).getConnection());
						}catch(Exception e){
							e.printStackTrace();
							
						}
						
						
						
					}
				}
				
				viewer.refresh();
			}
		};
	}
	
	
	
	public void setSelecion(Column col) {
		Table t = col.getTable();
		Connection c = t.getConnection();
		
		TreeParent root = (TreeParent)viewer.getInput();
		
		for(TreeObject o  : root.getChildren()){
				TreeConnection tc = (TreeConnection)o;
				
				if (tc.getConnection() == c){
					for(TreeObject oo : tc.getChildren()){
						TreeTable tt = (TreeTable)oo;
						
						if (tt.getTable() == t){
							for(TreeObject ooo : tt.getChildren()){
								TreeColumn tcol = (TreeColumn)ooo;
								
								if (tcol.getColumn() == col){
									viewer.setSelection(new StructuredSelection(tcol));
									viewer.setExpandedElements(new Object[]{tt, tc});
									viewer.refresh();
									return;
								}
							}
						
						}
					}
				
				}
		}
		
		
	}
}
