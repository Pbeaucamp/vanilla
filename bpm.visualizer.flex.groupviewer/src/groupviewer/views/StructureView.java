package groupviewer.views;

import groupviewer.GroupDataLoader;
import groupviewer.tree.TreeContentProvider;
import groupviewer.tree.TreeObject;
import groupviewer.tree.TreeParent;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import bpm.birep.admin.client.trees.TreeLabelProvider;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;

public class StructureView extends ViewPart implements IPropertyChangeListener {

	public static final String ID="groupviewer.views.StructureView";
	private TreeViewer viewer;
	private TreeParent root;
	
	public StructureView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2,false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		createTree(main);
		//fillTree();
	}
	private void createTree(Composite container){
		viewer = new TreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true,2, 1));
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		root = new TreeParent("Root");
		viewer.setInput(root);	
		getSite().getWorkbenchWindow().getPartService().addPartListener(new IPartListener(){

			public void partActivated(IWorkbenchPart part) {
				
			}

			public void partBroughtToTop(IWorkbenchPart part) {			
			}

			public void partClosed(IWorkbenchPart part) {
				if (part instanceof GroupViewerView) {
					GroupViewerView view = (GroupViewerView) part;
					removeListener(view);
				}
			}

			public void partDeactivated(IWorkbenchPart part) {	
			}

			public void partOpened(IWorkbenchPart part) {
				if (part instanceof GroupViewerView) {
					GroupViewerView view = (GroupViewerView) part;
					addListener(view);
				}
			}
			
		});
	}
	
	private void addListener(GroupViewerView view) {
		view.addPartPropertyListener(this);
	}
	private void removeListener(GroupViewerView view){
		view.removePartPropertyListener(this);
	}
	@Override
	public void setFocus() {
	}
	private void fillTree(){

		Runnable tFillTree = new Runnable(){
			public void run() {
				
				GroupDataLoader groupDataLoader = new GroupDataLoader();
				root = (TreeParent) viewer.getInput();
				if (root.hasChildren())
					root.removeAllChild();
				
				Collection<Group> grpList = groupDataLoader.getGroupList();
				for (Group g : grpList){
					TreeParent treeGroup = new TreeParent (g.getName(), g);
					List<User> usrList = groupDataLoader.getUsersInGroup(g);
					for (User u : usrList){
						treeGroup.addChild(new TreeObject(u.getName(),u));
					}
					root.addChild(treeGroup);
				}
				viewer.expandAll();
				viewer.refresh();
			}
			
		};
		org.eclipse.swt.custom.BusyIndicator.showWhile(Display.getCurrent(), tFillTree);
		
	}
	public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
		if (event.getProperty() == GroupViewerView.DATA_PROP){
			fillTree();
		}
	}
}
