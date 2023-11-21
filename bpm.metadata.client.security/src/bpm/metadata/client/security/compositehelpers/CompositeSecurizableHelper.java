package bpm.metadata.client.security.compositehelpers;

import java.util.ArrayList;
import java.util.List;

import metadata.client.helper.GroupHelper;
import metadataclient.Activator;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.metadata.ISecurizable;

public class CompositeSecurizableHelper {
	protected class Secu{
		public String groupName;
		Boolean visible;
	}
	

	protected CheckboxTreeViewer tableGroups;
	protected ISecurizable securizableData;
	protected FormToolkit toolkit;
	
	public CompositeSecurizableHelper(FormToolkit toolkit, Composite parent, int style) {
		this.toolkit = toolkit;
	
	}
	protected Control createToolbar(final Composite parent){
		
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData());
		
		ToolItem checkAll = new ToolItem(toolbar, SWT.PUSH);
		checkAll.setToolTipText("Check All"); //$NON-NLS-1$
		checkAll.setImage(Activator.getDefault().getImageRegistry().get("check"));
		checkAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				tableGroups.setAllChecked(true);
				for(Object o : ((IStructuredContentProvider)tableGroups.getContentProvider()).getElements(tableGroups.getInput())){
					securizableData.setGranted(((Secu)o).groupName, true);
				}
				parent.notifyListeners(SWT.Selection, new Event());
			}
		});
		
		ToolItem uncheckAll = new ToolItem(toolbar, SWT.PUSH);
		uncheckAll.setToolTipText("Uncheck All"); //$NON-NLS-1$
		uncheckAll.setImage(Activator.getDefault().getImageRegistry().get("uncheck"));
		uncheckAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				tableGroups.setAllChecked(false);
				for(Object o : ((IStructuredContentProvider)tableGroups.getContentProvider()).getElements(tableGroups.getInput())){
					securizableData.setGranted(((Secu)o).groupName, false);
				}
				parent.notifyListeners(SWT.Selection, new Event());
			}
		});
		
		return toolbar;
	}
	
	
	protected void createGroupViewer(final Composite main){
		tableGroups = new CheckboxTreeViewer(toolkit.createTree(main, SWT.V_SCROLL  | SWT.VIRTUAL | SWT.CHECK));
		tableGroups.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		tableGroups.setContentProvider(new ITreeContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Object> list = (List<Object>) inputElement;
				return list.toArray(new Object[list.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}

			public Object[] getChildren(Object parentElement) {
				
				return null;
			}

			public Object getParent(Object element) {
				
				return null;
			}

			public boolean hasChildren(Object element) {
				
				return false;
			}
			
		});
		
		tableGroups.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Secu)element).groupName;
			}
		});
		tableGroups.addCheckStateListener(new ICheckStateListener() {
			
			public void checkStateChanged(CheckStateChangedEvent event) {
				Secu s = (Secu)event.getElement();
				if (securizableData == null){
					return;
				}
				boolean b = tableGroups.getChecked(s);
				
				securizableData.setGranted(s.groupName, b);
				main.notifyListeners(SWT.Selection, new Event());
			}
		});
		
		List<String> groups = GroupHelper.getGroups(0, 100);
		
		List<Secu> l = new ArrayList<Secu>();
		
		for(String s : groups){
			Secu _c = new Secu();
			_c.groupName = s;
			_c.visible = false;
			l.add(_c);
		}
		tableGroups.setInput(l);
	}
	
	public void createContent(final Composite main){
		Composite parent = toolkit.createComposite(main, SWT.NONE);
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event) {
				main.notifyListeners(SWT.Selection, event);
				
			}
		});
		createToolbar(parent);
		createGroupViewer(parent);
		
	}

	public void setSecurizableDatas(ISecurizable datas){
		this.securizableData = datas;
		fill();
	}

	protected void fill(){
		for(Secu s : (List<Secu>)tableGroups.getInput()){
			if (securizableData.isGrantedFor(s.groupName)){
				s.visible = true;
				
				tableGroups.setChecked(s, true);
			}
			else{
				s.visible = false;
				tableGroups.setChecked(s, false);
			}
		}
		
		List<Secu> i =  (List<Secu>)tableGroups.getInput();
		for(Secu s : i){
			tableGroups.setChecked(s, s.visible);
		}
		Object[] o = tableGroups.getCheckedElements();
		tableGroups.refresh();
		

	}
}
