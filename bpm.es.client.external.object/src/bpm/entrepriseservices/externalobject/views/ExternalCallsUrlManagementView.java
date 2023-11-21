package bpm.entrepriseservices.externalobject.views;

import java.text.SimpleDateFormat;
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
import org.eclipse.jface.wizard.WizardDialog;
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
import bpm.entrepriseservices.externalobject.Messages;
import bpm.entrepriseservices.externalobject.wizard.ExternalUrlWizard;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ExternalCallsUrlManagementView extends ViewPart implements ISelectionListener {
	public static final String ID = "bpm.entrepriseservices.externalobject.views.ExternalCallsUrlManagement"; //$NON-NLS-1$

	private TableViewer tableViewer;
	private Action remove;
	private PublicUrl current;
	
	private Button btnCreateUrl;
	
	public ExternalCallsUrlManagementView() {
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
		
		btnCreateUrl = new Button(main,SWT.PUSH);
		btnCreateUrl.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false, 2, 1));
		btnCreateUrl.setText(Messages.ExternalCallsUrlManagementView_1);
		
		btnCreateUrl.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (ExternalCallsView.selectedObject instanceof TreeItem) {
					ExternalUrlWizard wiz = new ExternalUrlWizard(((TreeItem) ExternalCallsView.selectedObject).getItem());
					WizardDialog dial = new WizardDialog(getSite().getShell(), wiz);
					dial.create();
					if (dial.open() == Dialog.OK) {
						updateTableViewer();
					}
					
				}
				super.widgetSelected(e);
			}
		});
		btnCreateUrl.setEnabled(false);
		
		createActions();
		createPublicUrlViewers(main);
		updateTableViewer();

	}
	
	protected void updateTableViewer() {
		if (ExternalCallsView.selectedObject instanceof TreeItem) {
			RepositoryItem oa = ((TreeItem) ExternalCallsView.selectedObject).getItem();
			try {
				List<PublicUrl> urls = Activator.getDefault().getVanillaApi().getVanillaExternalAccessibilityManager().getPublicUrlsByItemIdRepositoryId(oa.getId(), Activator.getDefault().getCurrentRepository().getId());
				tableViewer.setInput(urls);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void createPublicUrlViewers(Composite parent) {
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,2, 1));
		l.setText(Messages.ExternalCallsUrlManagementView_2);
		
		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		tableViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
		tableViewer.setContentProvider(new IStructuredContentProvider(){

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<PublicUrl> l = (List<PublicUrl>)inputElement;
				return l.toArray(new PublicUrl[l.size()]);
			}

			@Override
			public void dispose() {
				
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
		});
		tableViewer.setLabelProvider(new ITableLabelProvider(){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
			
			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex){
				case 0:
					return ((PublicUrl)element).getId() + ""; //$NON-NLS-1$
				case 1:
					Group g;
					try {
						g = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupById(((PublicUrl)element).getGroupId());
						return g.getName();
					} catch (Exception e) {
						e.printStackTrace();
						return ""; //$NON-NLS-1$
					}
					
					
				case 2:
					if (((PublicUrl)element).getCreationDate() != null)
						return sdf.format(((PublicUrl)element).getCreationDate());
					else
						return ""; //$NON-NLS-1$
				case 3:
					if (((PublicUrl)element).getEndDate() != null)
						return sdf.format(((PublicUrl)element).getEndDate());
					else
						return ""; //$NON-NLS-1$
				case 4: 
					return ((PublicUrl)element).getOutputFormat();		
					
				}
				return ""; //$NON-NLS-1$
			}

			@Override
			public void addListener(ILabelProviderListener listener) {
				
			}

			@Override
			public void dispose() {

			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {
				
			}
			
		});
		
		
		TableColumn name = new TableColumn(tableViewer.getTable(), SWT.NONE);
		name.setText(Messages.ExternalCallsUrlManagementView_9);
		name.setWidth(50);
		
		TableColumn group = new TableColumn(tableViewer.getTable(), SWT.NONE);
		group.setText(Messages.ExternalCallsUrlManagementView_10);
		group.setWidth(130);
		
		TableColumn comment = new TableColumn(tableViewer.getTable(), SWT.NONE);
		comment.setText(Messages.ExternalCallsUrlManagementView_11);
		comment.setWidth(180);
		
		TableColumn format = new TableColumn(tableViewer.getTable(), SWT.NONE);
		format.setText(Messages.ExternalCallsUrlManagementView_12);
		format.setWidth(180);
		
		TableColumn version = new TableColumn(tableViewer.getTable(), SWT.NONE);
		version.setText(Messages.ExternalCallsUrlManagementView_13);
		version.setWidth(80);
		
		TableViewerColumn url = new TableViewerColumn(tableViewer, SWT.NONE);
		url.getColumn().setText(Messages.ExternalCallsUrlManagementView_14);
		url.getColumn().setWidth(300);
		
		url.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				if (element instanceof PublicUrl) {
					String url = null;
					
					
					try{
						url = Activator.getDefault().getVanillaApi().getVanillaExternalUrl();
						if (!url.endsWith("/")) { //$NON-NLS-1$
							url += "/"; //$NON-NLS-1$
						}
						url += VanillaConstants.VANILLA_EXTERNAL_CALL + "?publickey=" + ((PublicUrl)element).getPublicKey(); //$NON-NLS-1$
						return url;
					}catch(Exception ex){
						ex.printStackTrace();;
					}
					return ""; //$NON-NLS-1$
					
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
				if (element instanceof PublicUrl) {
					
					try{
						String url = Activator.getDefault().getVanillaApi().getVanillaExternalUrl();
						if (url.endsWith("/")) { //$NON-NLS-1$
							url= "/"; //$NON-NLS-1$
						}
						url += VanillaConstants.VANILLA_EXTERNAL_CALL + "?publickey=" + ((PublicUrl)element).getPublicKey(); //$NON-NLS-1$
						return url;
					}catch(Exception ex){
						ex.printStackTrace();
						return ""; //$NON-NLS-1$
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
			ExternalCallsView.selectedObject = null;
			btnCreateUrl.setEnabled(false);
			return;
		}
		if (ss.getFirstElement() instanceof TreeItem){
			ExternalCallsView.selectedObject = (TreeItem)ss.getFirstElement();
			updateTableViewer();
			
			btnCreateUrl.setEnabled(true);
		}
		else{
			ExternalCallsView.selectedObject = null;
			
			btnCreateUrl.setEnabled(false);
		}
			
		
	}
	
	public void createActions() {
		remove = new Action(Messages.ExternalCallsUrlManagementView_25){
			public void run(){
				if (current != null) {
					try {
						Activator.getDefault().getVanillaApi().getVanillaExternalAccessibilityManager().deletePublicUrl(current.getId());
					} catch (Exception e) {
						e.printStackTrace();
					}
					updateTableViewer();
				}
			}
		};
		
		remove.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("del")); //$NON-NLS-1$
	}
	
	public class ViewerMenuListener implements IMenuListener{

		private Viewer _viewer;

		public ViewerMenuListener(Viewer v){
			this._viewer = v;
		}
		public void menuAboutToShow(IMenuManager mgr) {
			IStructuredSelection ss =  (IStructuredSelection)_viewer.getSelection();
			Object o = ss.getFirstElement();

			if (o instanceof PublicUrl) {
				current = (PublicUrl) o;
				mgr.add(remove);
			}

			mgr.update();
		}
	}

}
