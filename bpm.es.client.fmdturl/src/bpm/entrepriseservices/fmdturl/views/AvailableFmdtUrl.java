package bpm.entrepriseservices.fmdturl.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.entrepriseservices.fmdturl.Messages;
import bpm.entrepriseservices.fmdturl.dialogs.DialogFmdtUrl;
import bpm.vanilla.platform.core.beans.FmdtUrl;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class AvailableFmdtUrl extends ViewPart implements ISelectionListener {
	public static final String ID = "bpm.entrepriseservices.fmdturl.views.AvailableFmdtUrl"; //$NON-NLS-1$

	private TableViewer tableViewer;
	private Action remove;
	private Action update;
	private FmdtUrl current;
	
	
	public AvailableFmdtUrl() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(this);
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Button btn = new Button(main,SWT.PUSH);
		btn.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false, 2, 1));
		btn.setText(Messages.AvailableFmdtUrl_1);
		
		btn.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (TreeFmdt.selectedObject != null && TreeFmdt.selectedObject instanceof TreeItem) {
					DialogFmdtUrl d = new DialogFmdtUrl(getSite().getShell(), ((TreeItem) TreeFmdt.selectedObject).getItem(), null);
					
					if (d.open() == Dialog.OK) {
						updateTableViewer();
					}
				}
			}
		});
		
		createActions();
		createPublicUrlViewers(main);
		updateTableViewer();

	}
	
	protected void updateTableViewer() {
		if (TreeFmdt.selectedObject instanceof TreeItem) {
			RepositoryItem oa = ((TreeItem) TreeFmdt.selectedObject).getItem();
			try {
				
				tableViewer.setInput(Activator.getDefault().getVanillaApi().getVanillaExternalAccessibilityManager().getFmdtUrlForItemId(oa.getId()));
			} catch (Exception e1) {
				tableViewer.setInput(new ArrayList<FmdtUrl>());
			}
		}
	}

	private void createPublicUrlViewers(Composite parent) {
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,2, 1));
		l.setText(Messages.AvailableFmdtUrl_2);
		
		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		tableViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
		tableViewer.setContentProvider(new IStructuredContentProvider(){

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<FmdtUrl> l = (List<FmdtUrl>) inputElement;
				return l.toArray(new FmdtUrl[l.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		tableViewer.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex){
				case 0:
					return ((FmdtUrl)element).getName();
				case 1:
					return ((FmdtUrl)element).getDescription();
				case 2:
					return ((FmdtUrl)element).getGroupName();
				case 3:
					return ((FmdtUrl)element).getModelName();
				case 4:
					return ((FmdtUrl)element).getPackageName() != null ? ((FmdtUrl)element).getPackageName() : ""; //$NON-NLS-1$
				}
				return ""; //$NON-NLS-1$
			}

			public void addListener(ILabelProviderListener listener) {
				
			}

			public void dispose() {

				
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				
			}
			
		});
		
		
		TableColumn name = new TableColumn(tableViewer.getTable(), SWT.NONE);
		name.setText(Messages.AvailableFmdtUrl_5);
		name.setWidth(100);
		
		TableColumn description = new TableColumn(tableViewer.getTable(), SWT.NONE);
		description.setText(Messages.AvailableFmdtUrl_6);
		description.setWidth(150);
		
		TableColumn group = new TableColumn(tableViewer.getTable(), SWT.NONE);
		group.setText(Messages.AvailableFmdtUrl_7);
		group.setWidth(80);
		
		TableColumn model = new TableColumn(tableViewer.getTable(), SWT.NONE);
		model.setText(Messages.AvailableFmdtUrl_8);
		model.setWidth(100);
		
		TableColumn pack = new TableColumn(tableViewer.getTable(), SWT.NONE);
		pack.setText(Messages.AvailableFmdtUrl_9);
		pack.setWidth(100);
		
		TableViewerColumn url = new TableViewerColumn(tableViewer, SWT.NONE);
		url.getColumn().setText(Messages.AvailableFmdtUrl_10);
		url.getColumn().setWidth(350);
		
		url.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				if (element instanceof FmdtUrl) {
					FmdtUrl fu = (FmdtUrl) element;
					
					String url = ""; //$NON-NLS-1$
					String vUrl = ""; //$NON-NLS-1$
					try{
						 vUrl = Activator.getDefault().getVanillaApi().getVanillaSystemManager().getVanillaSetup().getVanillaRuntimeServersUrl();
					}catch(Exception ex){
						ex.printStackTrace();
						
					}
					int end = vUrl.lastIndexOf(":"); //$NON-NLS-1$
					end = vUrl.substring(end).indexOf("/"); //$NON-NLS-1$
				
					url = vUrl.replace("http://", "jdbc:fmdt://") ; //$NON-NLS-1$ //$NON-NLS-2$
					
					url = url + "/" + fu.getName();  //$NON-NLS-1$
					
					return url;
				}
				else
					return ""; //$NON-NLS-1$
			}

		});
		
		url.setEditingSupport(new EditingSupport(tableViewer) {
			TextCellEditor editor;
			
			@Override
			protected void setValue(Object element, Object value) {
				
			}
			
			@Override
			protected Object getValue(Object element) {
				if (element instanceof FmdtUrl) {
					FmdtUrl fu = (FmdtUrl) element;
					
					String url = ""; //$NON-NLS-1$
					
					try{
						String vUrl = Activator.getDefault().getVanillaApi().getVanillaSystemManager().getVanillaSetup().getVanillaRuntimeServersUrl();
						int end = vUrl.lastIndexOf(":"); //$NON-NLS-1$
						end = vUrl.substring(end).indexOf("/"); //$NON-NLS-1$
					
						url = vUrl.replace("http://", "jdbc:fmdt://") ; //$NON-NLS-1$ //$NON-NLS-2$
						
						url = url + "/" + fu.getName();  //$NON-NLS-1$
						
						return url;
					}catch(Exception ex){
						ex.printStackTrace();
						return "error"; //$NON-NLS-1$
					}
					
				}
				else
					return ""; //$NON-NLS-1$
			
			
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				if (editor == null){
					editor =  new TextCellEditor((Composite)tableViewer.getControl());       
				}
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		
		
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		
		createContextMenu();
	}
	
	protected void createContextMenu() {
		MenuManager  menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new ViewerMenuListener(tableViewer));   
		tableViewer.getControl().setMenu(menuMgr.createContextMenu(tableViewer.getControl()));

	}

	@Override
	public void setFocus() { }

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IStructuredSelection ss = (IStructuredSelection)selection;
		
		if (ss.isEmpty()){	
			TreeFmdt.selectedObject = null;
			return;
		}
		if (ss.getFirstElement() instanceof TreeItem){
			TreeFmdt.selectedObject = (TreeItem)ss.getFirstElement();
			updateTableViewer();
		}
		else{
			TreeFmdt.selectedObject = null;
		}
			
		
	}
	
	public void createActions() {
		remove = new Action(Messages.AvailableFmdtUrl_27){
			public void run(){
				if (current != null) {
					try {
						Activator.getDefault().getVanillaApi().getVanillaExternalAccessibilityManager().deleteFmdtUrl(current);
					} catch (Exception e) {
						e.printStackTrace();
					}
					updateTableViewer();
				}
			}
		};
		
		remove.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("del")); //$NON-NLS-1$
		
		update = new Action(Messages.AvailableFmdtUrl_29){
			public void run(){
				if (current != null) {
					if (TreeFmdt.selectedObject != null && TreeFmdt.selectedObject instanceof TreeItem) {
						DialogFmdtUrl d = new DialogFmdtUrl(getSite().getShell(), ((TreeItem) TreeFmdt.selectedObject).getItem(), current);
						
						if (d.open() == Dialog.OK) {
							updateTableViewer();
						}
					}
					
				}
			}
		};
		
		update.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("update")); //$NON-NLS-1$
	}
	
	public class ViewerMenuListener implements IMenuListener{

		private Viewer _viewer;

		public ViewerMenuListener(Viewer v){
			this._viewer = v;
		}
		public void menuAboutToShow(IMenuManager mgr) {
			IStructuredSelection ss =  (IStructuredSelection)_viewer.getSelection();
			Object o = ss.getFirstElement();

			if (o instanceof FmdtUrl) {
				current = (FmdtUrl) o;
				mgr.add(remove);
				mgr.add(update);
			}

			mgr.update();
		}
	}

}
