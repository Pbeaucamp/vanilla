package bpm.birep.admin.client.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.dialog.DialogCreateVariable;
import bpm.birep.admin.client.dialog.DialogRepository;
import bpm.birep.admin.client.trees.TreeContentProvider;
import bpm.birep.admin.client.trees.TreeLabelProvider;
import bpm.birep.admin.client.trees.TreeParent;
import bpm.birep.admin.client.trees.TreeVariable;
import bpm.vanilla.platform.core.beans.Variable;

public class ViewVariable extends ViewPart {

	public static final String ID = "bpm.birep.admin.client.viewvariable"; //$NON-NLS-1$

	private TreeViewer viewer;
	private Action addVariable, delVariable;
	
	public ViewVariable() {

	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createTree(container);
		getSite().setSelectionProvider(viewer);
	}
	
	
	private void createTree(Composite container){
		createActions();
		createToolbar(container);
		
		viewer = new TreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		
		
		try {
			createInput();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void createInput() throws Exception{
				
		TreeParent root = new TreeParent("Variables"); //$NON-NLS-1$
		
		for(Variable v : Activator.getDefault().getVanillaApi().getVanillaSystemManager().getVariables()){
			TreeVariable tr = new TreeVariable(v);
			root.addChild(tr);
		}
			
		viewer.setInput(root);
	}
	

	@Override
	public void setFocus() { }
	
	public void refresh(){
		viewer.refresh();
	}

	private void createToolbar(Composite parent){
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		
		ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.ViewVariable_2);
		add.setImage(reg.get("add")); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				addVariable.run();
			}
			
		});
		
		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.ViewVariable_4);
		del.setImage(reg.get("del")); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				delVariable.run();
			}
		});
		
	
	}
	
	private void createActions(){
		addVariable = new Action(Messages.ViewVariable_6){
			public void run(){
				
				DialogCreateVariable dial = new DialogCreateVariable(getSite().getShell());
				if (dial.open() == DialogRepository.OK){
										
					try {
						Integer trouver = Activator.getDefault().getVanillaApi().getVanillaSystemManager().addVariable(dial.getVariable());
						if (trouver == null){
							MessageDialog.openError(getSite().getShell(), Messages.ViewVariable_7, dial.getVariable().getName());
						}
						createInput();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
				viewer.refresh();
				
			}
		};
		
		delVariable = new Action(Messages.ViewVariable_8){
			public void run(){
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				
				for(Object o : ss.toList()){
					if (o instanceof TreeVariable){
						Variable v = ((TreeVariable)o).getVariable();

						try {
							Activator.getDefault().getVanillaApi().getVanillaSystemManager().deleteVariable(v);
							((TreeVariable)o).getParent().removeChild((TreeVariable)o);
							viewer.refresh();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		
	}
	
}
