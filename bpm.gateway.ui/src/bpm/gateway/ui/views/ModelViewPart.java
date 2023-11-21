package bpm.gateway.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.GatewayObject;
import bpm.gateway.core.forms.Form;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.gef.model.Link;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.icons.IconsNames;
import bpm.gateway.ui.viewer.TreeLabelProvider;
import bpm.gateway.ui.viewer.TreeObject;

public class ModelViewPart extends ViewPart implements ITabbedPropertySheetPageContributor{
	public static final String ID = "bpm.gateway.ui.views.model"; //$NON-NLS-1$
	
	private TreeViewer treeViewer;
	private Action delete;
	
	
	private ISelectionChangedListener selectionListener = null;
	
	public ModelViewPart() {

	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		treeViewer = new TreeViewer(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		treeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.setContentProvider(new ModelContentProvider());
		treeViewer.setLabelProvider(new TreeLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof TreeObject){
					return ((TreeObject)element).getName();
				}
				else if (element instanceof GatewayObject){
					return ((GatewayObject)element).getName();
				}
				else if (element instanceof String){
					return (String)element;
				}
				else if (element instanceof Variable){
					return ((Variable)element).getName();
				}
				else if (element instanceof Parameter){
					return ((Parameter)element).getName();
				}
				else if (element instanceof Form){
					return ((Form)element).getName();
				}
				return element.getClass().getName();
			}

			@Override
			public Image getImage(Object obj) {
				if (obj instanceof Form){
					return Activator.getDefault().getImageRegistry().get(IconsNames.forms_16);
				}
				if (obj instanceof Parameter){
					return Activator.getDefault().getImageRegistry().get(IconsNames.parameter_16);
				}
				if (obj instanceof Variable){
					return Activator.getDefault().getImageRegistry().get(IconsNames.variable_16);
				}
				if (obj instanceof String){
					return Activator.getDefault().getImageRegistry().get(IconsNames.arrow_right_16);
				}
				return super.getImage(obj);
			}
			
			
			
		});

		this.getSite().getPage().addPartListener(new IPartListener(){

			public void partActivated(IWorkbenchPart part) {
				if (part == ModelViewPart.this){
					treeViewer.addSelectionChangedListener(selectionListener);
				}
				
			}

			public void partBroughtToTop(IWorkbenchPart part) {
				
			}

			public void partClosed(IWorkbenchPart part) {
				
			}

			public void partDeactivated(IWorkbenchPart part) {
				if (part == ModelViewPart.this){
					treeViewer.removeSelectionChangedListener(selectionListener);
				}
				
				
			}

			public void partOpened(IWorkbenchPart part) {
				
			}

			
			
		});
		
		selectionListener = new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				if (part == null){
					return;
				}
				
				IStructuredSelection ss = (IStructuredSelection)treeViewer.getSelection();
				if (ss.isEmpty()){
					return;
				}
				
				Object o = ss.getFirstElement();
				
				((GatewayEditorPart)part).setSelection(o);
				
			}
			
		};
		
		getSite().setSelectionProvider(treeViewer);
		
		ModelViewHelper.createTree(treeViewer);
		
		refresh();

		createAction();
		createContextMenu();
	}

	@Override
	public void setFocus() {

	}

	public void refresh() {
		treeViewer.refresh();
		
		IActionBars ab = getViewSite().getActionBars();
		boolean state = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() != null;
		for(IContributionItem i : ab.getToolBarManager().getItems()){
			i.setVisible(state);
			
		}
		ab.updateActionBars();
	}

	
	public void setSelection(Object o){
		if (o instanceof Node){
			treeViewer.setSelection(new StructuredSelection(((Node)o).getGatewayModel()));
		}
		else if (o instanceof Link){
			Object x = null;
			for(TreeItem it : treeViewer.getTree().getItems()){
				Link l = (Link)o;
				String lName = (l.getSource()!= null ? l.getSource().getName(): "") + " ----> " + (l.getTarget()!= null ? l.getTarget().getName(): ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				x = findLink(it, lName);
				
				if (x != null){
					break;
				}
			}
			if (x == null){
				return;
			}
			
			treeViewer.setSelection(new StructuredSelection(x));
		}
	}
	
	private Object findLink(TreeItem parent, String linkName){
		Object o = parent.getData();
		Object res = null;
		
		if (o instanceof String && linkName.equals(o)){
			return o;
		}
		else{
			for(TreeItem c : parent.getItems()){
				res = findLink(c, linkName);
				if (res != null){
					return res;
				}
				
			}
		}
		return res;
	}

	public String getContributorId() {
		return ID;
	}

	@Override
	//to provide the propertySheet associated to that Part
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySheetPage.class)
            return new TabbedPropertySheetPage(this);
        return super.getAdapter(adapter);
	}
	
	
	
	private void createContextMenu(){
		final MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
            	IStructuredSelection ss =  (IStructuredSelection)treeViewer.getSelection();
            	Object o = ss.getFirstElement();
            	
            	if ( o instanceof Parameter){
            		menuMgr.add(delete);
            	}
            }
        });
	
        
        
        treeViewer.getControl().setMenu(menuMgr.createContextMenu(treeViewer.getControl()));
	}
	
	
	private void createAction(){
		delete = new Action("delete"){ //$NON-NLS-1$
			public void run(){
				Object o = ((IStructuredSelection)treeViewer.getSelection()).getFirstElement();
				
				if (o instanceof Parameter){
					Parameter p = (Parameter)o;
					Activator.getDefault().getCurrentInput().getDocumentGateway().removeParameter(p);
				}
				else if (o instanceof Form){
					Activator.getDefault().getCurrentInput().getDocumentGateway().removeForm((Form)o);
				}
				
				refresh();
				
				
				
			}
		};
	}
}
