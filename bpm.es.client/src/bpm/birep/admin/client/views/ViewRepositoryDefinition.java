package bpm.birep.admin.client.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.dialog.DialogCreateRepository;
import bpm.birep.admin.client.dialog.DialogRepository;
import bpm.birep.admin.client.trees.TreeContentProvider;
import bpm.birep.admin.client.trees.TreeLabelProvider;
import bpm.birep.admin.client.trees.TreeParent;
import bpm.birep.admin.client.trees.TreeRepositoryDefinition;
import bpm.birep.admin.client.trees.TreeUser;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserRep;

public class ViewRepositoryDefinition extends ViewPart {

	public static final String ID = "bpm.birep.admin.client.viewrepository"; //$NON-NLS-1$

	private TreeViewer viewer;
	private Action addRepository, delRepository;
	
	public ViewRepositoryDefinition() { }

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createTree(container);
		 getSite().setSelectionProvider(viewer);

		 setDnd();
	}
	
	
	private void createTree(Composite container){
		createActions();
		createToolbar(container);
		
		viewer = new TreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				ViewTree view = (ViewTree)adminbirep.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewTree.ID);
				
				if (view == null){
					return;
				}
				
				for(Object o : ss.toList()){
					if (o instanceof TreeRepositoryDefinition){
						Repository g = ((TreeRepositoryDefinition)o).getRepository();
						try {
							Activator.getDefault().resetRepository(g);
							view.createInput();
							view.refresh();
						} catch (Exception ex) {
							ex.printStackTrace();
							MessageDialog.openError(getSite().getShell(), Messages.ViewRepositoryDefinition_1, Messages.ViewRepositoryDefinition_2 + ex.getMessage());
						}
					}
				}
			}
			
		});
		
		try {
			createInput();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void createInput() throws Exception{
				
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		for(Repository r : Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getRepositories()){
			TreeRepositoryDefinition tr = new TreeRepositoryDefinition(r);
			root.addChild(tr);
			
			
			for(UserRep ur : Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getUserRepByRepositoryId(r.getId())){
				int id = ur.getUserId();
				User u = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUserById(id);
				TreeUser tu = new TreeUser(u);
				tr.addChild(tu);
			}

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
		add.setToolTipText(Messages.ViewRepositoryDefinition_4);
		add.setImage(reg.get("add")); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				addRepository.run();
			}
			
		});
		
		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.ViewRepositoryDefinition_6);
		del.setImage(reg.get("del")); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				delRepository.run();
			}
		});
		
	
	}
	
	private void createActions(){
		addRepository = new Action(Messages.ViewRepositoryDefinition_8){
			public void run(){
				
				DialogCreateRepository dial = new DialogCreateRepository(getSite().getShell());
				if (dial.open() == DialogRepository.OK){
										
					try {
						Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().addRepository(dial.getRepositoryDefinition());
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					try {
						createInput();
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
				}
				
				viewer.refresh();
				
			}
		};
		
		delRepository = new Action(Messages.ViewRepositoryDefinition_11){
			public void run(){
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				
				for(Object o : ss.toList()){
					if (o instanceof TreeRepositoryDefinition){
						try {
							Repository g = ((TreeRepositoryDefinition)o).getRepository();
							ArrayList<UserRep> usersrep = (ArrayList<UserRep>)Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getUserRepByRepositoryId(g.getId());
							Iterator it = usersrep.iterator();
							while (it.hasNext()){
								UserRep userrep = (UserRep)it.next();
								Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().delUserRep(userrep);
							}
							Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().deleteRepository(g);
							
							((TreeRepositoryDefinition)o).getParent().removeChild((TreeRepositoryDefinition)o);
							viewer.refresh();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					else if (o instanceof TreeUser){
						User user = ((TreeUser)o).getUser();
						TreeUser tuser = (TreeUser)o;
						TreeRepositoryDefinition tr = (TreeRepositoryDefinition)tuser.getParent();
						User u;
						try {
							u = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUserByLogin(user.getLogin());
							UserRep ur = null;
							
							
							for(Repository r : Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getRepositories()){
								
								if (r.getName().equals(tr.getName())){
									
									for(UserRep _ur : Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getUserRepByRepositoryId(r.getId())){
										if (_ur.getUserId() == u.getId()){
											ur = _ur;
											break;
										}
									}
									
									break;
								}
							}
							
							Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().delUserRep(ur);
							
							TreeUser tu = (TreeUser)o;
							tu.getParent().removeChild(tu);
							viewer.refresh();
						} catch (Exception e) {
							e.printStackTrace();
						}	
					}
				}
			}
		};
		
	}
	
	private void setDnd(){
		DropTarget target = new DropTarget(viewer.getTree(), DND.DROP_COPY);
		target.setTransfer(new Transfer[]{TextTransfer.getInstance()});
		target.addDropListener(new DropTargetListener(){

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
				
			}

			public void dragLeave(DropTargetEvent event) { }

			public void dragOperationChanged(DropTargetEvent event) { }

			public void dragOver(DropTargetEvent event) { }

			public void drop(DropTargetEvent event) {
				Object o = ((TreeItem) event.item).getData();
				String[] buf = ((String)event.data).split("/"); //$NON-NLS-1$
				
				
				if (!(o instanceof TreeRepositoryDefinition))
					return;
				
				TreeRepositoryDefinition tRep= (TreeRepositoryDefinition)o;
				
				if (buf[0].equals("USR")){ //$NON-NLS-1$
					//recup user
					User user;
					try {
						user = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUserById(Integer.parseInt(buf[1]));
					} catch (NumberFormatException e) {
						e.printStackTrace();
						return;
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					
					try {
						List<UserRep> users = Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getUserRepByRepositoryId(tRep.getRepository().getId());
						for(UserRep us : users){
							if(us.getUserId() == user.getId()){
								return;
							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					UserRep ur = new UserRep();
					ur.setUserId(user.getId());
					
					try {
						for(Repository r : Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getRepositories()){
							if (r.getName().equals(tRep.getRepository().getName())){
								ur.setRepositoryId(r.getId());
								break;
							}
						}
						Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().addUserRep(ur);
						
						TreeUser tu = new TreeUser(user);
						tRep.addChild(tu);
						viewer.refresh();
					} catch (Exception e) {
						e.printStackTrace();
						MessageDialog.openWarning(getSite().getShell(), Messages.ViewRepositoryDefinition_12, e.getMessage());
					}
						
				}
			}

			public void dropAccept(DropTargetEvent event) { }
			
		});
	}
	
	public Repository getSelectedRepository(){
		IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
		Repository g = null;
		for(Object o : ss.toList()){
			if (o instanceof TreeRepositoryDefinition){
				g = ((TreeRepositoryDefinition)o).getRepository();
			}
		}
		return g;
	}
}
