package bpm.vanilla.server.ui.dialogs;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import bpm.repository.api.model.IRepositoryConnection;
import bpm.vanilla.repository.ui.icons.IconsRegistry;
import bpm.vanilla.server.client.ServerType;
import bpm.vanilla.server.client.communicators.TaskDatas;
import bpm.vanilla.server.client.communicators.TaskList;
import bpm.vanilla.server.client.communicators.TaskRunnerCommunicator;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.icons.Icons;
import bpm.vanilla.server.ui.internal.TaskListManager;
import bpm.vanilla.server.ui.wizard.TaskDataWizard;
import bpm.vanilla.server.ui.wizard.list.ListWizard;

public class DialogListManager extends Dialog{

	private class PropertiesContentProvider implements IStructuredContentProvider{
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
			
		}
		
		public void dispose() {
			
			
		}
		
		public Object[] getElements(Object inputElement) {
			Properties c = (Properties)inputElement;
			
			List<String> l  = new ArrayList<String>();
			Enumeration e = c.propertyNames();
			while(e.hasMoreElements()){
				l.add((String)e.nextElement());
			}
			
			return l.toArray(new Object[l.size()]);
		}
	};
	
	private TreeViewer managerViewer;
	private TableViewer propertiesViewer;
	private TableViewer parameterViewer;
	
	private Button runSelectedList;
	private String vanillaUrl;
	
	private ToolItem createList, deleteList, saveOnRepository;
	
	public DialogListManager(Shell parentShell, String vanillaUrl) {
		super(parentShell);
		this.vanillaUrl = vanillaUrl;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createToolbar(main);
		
		buildManagerViewer(main);
		createMenu();
		
		Composite sec = new Composite(main, SWT.NONE);
		sec.setLayout(new GridLayout());
		sec.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		buildProperties(sec);
		buildParameters(sec);
		return main;
	}
	
	
	private void createToolbar(Composite parent){
		ToolBar bar = new ToolBar(parent, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2,1));
		
		createList = new ToolItem(bar, SWT.PUSH);
		createList.setToolTipText("Create TaskList");
		createList.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				ListWizard wiz = new ListWizard();
				WizardDialog dial = new WizardDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);
				
				dial.open();
				managerViewer.refresh();
			}
			
		});
		createList.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD));
		
		deleteList = new ToolItem(bar, SWT.PUSH);
		deleteList.setToolTipText("Delete");
		deleteList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object o = ((IStructuredSelection)managerViewer.getSelection()).getFirstElement();
				
				if (o instanceof TaskDatas){
					TaskDatas t = (TaskDatas)o;
					t.getParent().removeTask(t);
				}
				else if (o instanceof TaskList){
					
					((TaskListManager)managerViewer.getInput()).removeList((TaskList)o);
				}
				
				
				
				
				managerViewer.refresh();
				try{
					Activator.getDefault().getTaskListManager().save();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		deleteList.setImage(Activator.getDefault().getImageRegistry().get(Icons.DELETE));
		deleteList.setEnabled(false);
		
		saveOnRepository = new ToolItem(bar, SWT.PUSH);
		saveOnRepository.setToolTipText("saveOnRepository");
		saveOnRepository.setText("saveOnRepository");
		saveOnRepository.setImage(Activator.getDefault().getImageRegistry().get(Icons.SAVE));
		saveOnRepository.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				TaskList list = (TaskList)((IStructuredSelection)managerViewer.getSelection()).getFirstElement();
				String xml = "";
				try{
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					XMLWriter wr = new XMLWriter(os, OutputFormat.createPrettyPrint());
					wr.write(list.getElement());
					xml = os.toString();
					wr.close();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				Activator.getDefault().setCurrentModel(list);
				
				ExportTaskListWizard wiz = new ExportTaskListWizard(xml);
				
				WizardDialog d = new WizardDialog(getShell(), wiz);
				d.open();
				
				Activator.getDefault().setCurrentModel(null);		
				
				
			}
		});
		saveOnRepository.setEnabled(false);
		
	}
	
	private void buildManagerViewer(Composite main){
		
		
		managerViewer = new TreeViewer(main, SWT.BORDER);
		managerViewer.setLabelProvider(new LabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				if (element instanceof TaskDatas){
					return ((TaskDatas)element).getName();
				}
				if (element instanceof TaskList){
					return ((TaskList)element).getName();
				}
				return super.getText(element);
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
			 */
			@Override
			public Image getImage(Object element) {
				ImageRegistry reg = bpm.vanilla.repository.ui.Activator.getDefault().getImageRegistry();
				if (element instanceof TaskDatas){
					
					switch(((TaskDatas)element).getObjectType()){
					case IRepositoryConnection.GTW_TYPE:
						return reg.get(IconsRegistry.GTW);
					case IRepositoryConnection.FWR_TYPE:
						return reg.get(IconsRegistry.FWR);
					case IRepositoryConnection.CUST_TYPE:
						if (((TaskDatas)element).getObjectSubType() == IRepositoryConnection.JASPER_REPORT_SUBTYPE){
							return reg.get(IconsRegistry.JASPER);
						}
						else{
							return reg.get(IconsRegistry.BIRT);
						}
					}
				}
				if (element instanceof TaskList){
					return reg.get(IconsRegistry.FOLDER);
				}
				return super.getImage(element);
			}
			
			
			
		});
		managerViewer.setContentProvider(new ITreeContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				TaskListManager mgr = (TaskListManager)inputElement;
				List l = mgr.getLists();
				
				return l.toArray(new Object[l.size()]);
			}
			
			public boolean hasChildren(Object element) {
				if (element instanceof TaskList){
					return ((TaskList)element).getTasks().size() > 0;
				}
				return false;
			}
			
			public Object getParent(Object element) {
				if (element instanceof TaskList){
					return null;
				}
				else if (element instanceof TaskDatas){
					return ((TaskDatas)element).getParent();
				}
				return null; 
			}
			
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof TaskList){
					List l = ((TaskList)parentElement).getTasks();
					return l.toArray(new Object[l.size()]);
				}
				return null;
			}
		});
		managerViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		managerViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				deleteList.setEnabled(!managerViewer.getSelection().isEmpty());
				if (managerViewer.getSelection().isEmpty()){
					runSelectedList.setEnabled(false);
					saveOnRepository.setEnabled(false);
					return;
				}
				Object o = ((IStructuredSelection)managerViewer.getSelection()).getFirstElement();
				if (o instanceof TaskDatas){
					propertiesViewer.setInput(((TaskDatas)o).getTaskProperties());;
					parameterViewer.setInput(((TaskDatas)o).getTaskParameters());;
					runSelectedList.setEnabled(false);
					saveOnRepository.setEnabled(false);
					return;
				}
				runSelectedList.setEnabled(true);
				saveOnRepository.setEnabled(true);
				
			}
		});
	
		
		
		managerViewer.addFilter(new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				ServerType type = Activator.getDefault().getServerRemote().getServerType();
				
				if (element instanceof TaskList){
					return type == ((TaskList)element).getType();
				}
				else if (element instanceof TaskDatas){
					return type == ((TaskList)parentElement).getType();
				}
				return false;
				
				
			}
		});
	}
	
	private void buildProperties(Composite parent){
		Label l = new Label(parent, SWT.NONE);
		l.setText("Task Properties");
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		propertiesViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		propertiesViewer.getTable().setLinesVisible(true);
		propertiesViewer.getTable().setHeaderVisible(true);
		propertiesViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		propertiesViewer.setContentProvider(new PropertiesContentProvider());
		 
		TableViewerColumn pName = new TableViewerColumn(propertiesViewer, SWT.NONE);
		pName.getColumn().setText("Name");
		pName.getColumn().setWidth(200);
		pName.setLabelProvider(new ColumnLabelProvider());
		
		TableViewerColumn pValue = new TableViewerColumn(propertiesViewer, SWT.NONE);
		pValue.getColumn().setText("Value");
		pValue.getColumn().setWidth(200);
		pValue.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				
				return((Properties)propertiesViewer.getInput()).getProperty((String)element);
			}
			
		});
		pValue.setEditingSupport(new EditingSupport(propertiesViewer) {
			
			ComboBoxCellEditor priority = new ComboBoxCellEditor(propertiesViewer.getTable(), new String[]{TaskRunnerCommunicator.PRIORITY_LOW, TaskRunnerCommunicator.PRIORITY_NORMAL, TaskRunnerCommunicator.PRIORITY_HIGHT});
			
			@Override
			protected void setValue(Object element, Object value) {
				
				((Properties)propertiesViewer.getInput()).setProperty((String)element, priority.getItems()[(Integer)value]);
				propertiesViewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				String k = ((Properties)propertiesViewer.getInput()).getProperty((String)element);
				if (k == null){
					return -1;
				}
				for(int i = 0; i < priority.getItems().length; i++){
					if (k.equals(priority.getItems()[i])){
						return i;
					}
				}
				return -1;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				
				return priority;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				if ("taskPriority".equals(element)){
					return true;
				}
				return false;
			}
		});
		
	
	}
	private void buildParameters(Composite parent){
		Label l = new Label(parent, SWT.NONE);
		l.setText("Task Parameters");
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		parameterViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		parameterViewer.getTable().setLinesVisible(true);
		parameterViewer.getTable().setHeaderVisible(true);
		parameterViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		parameterViewer.setContentProvider(new PropertiesContentProvider() );
		
		TableViewerColumn pName = new TableViewerColumn(parameterViewer, SWT.NONE);
		pName.getColumn().setText("Name");
		pName.getColumn().setWidth(200);
		pName.setLabelProvider(new ColumnLabelProvider());
		
		
		TableViewerColumn pValue = new TableViewerColumn(parameterViewer, SWT.NONE);
		pValue.getColumn().setText("Value");
		pValue.getColumn().setWidth(200);
		pValue.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				
				return((Properties)parameterViewer.getInput()).getProperty((String)element);
			}
			
		});
		pValue.setEditingSupport(new EditingSupport(parameterViewer) {
			TextCellEditor editor = new TextCellEditor(parameterViewer.getTable());
			@Override
			protected void setValue(Object element, Object value) {
				((Properties)parameterViewer.getInput()).setProperty((String)element, (String)value);
				parameterViewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				String k = ((Properties)parameterViewer.getInput()).getProperty((String)element);
				if (k == null){
					return "";
				}
				return k;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		


	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		getShell().setText("List Manager");
		managerViewer.setInput(Activator.getDefault().getTaskListManager());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		parent.setLayout(new GridLayout(2, true));
		Button b = createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
		b.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}
			
		});
		
		
		runSelectedList = new Button(parent, SWT.PUSH);
		runSelectedList.setText("Run");
		runSelectedList.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		runSelectedList.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				final TaskList tl = (TaskList)((IStructuredSelection)managerViewer.getSelection()).getFirstElement();
				final DialogGroupPickers d = new DialogGroupPickers(getShell(), vanillaUrl);
				
				if (d.open() != DialogGroupPickers.OK){
					return;
				}
				IRunnableWithProgress r = new IRunnableWithProgress(){

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						
						monitor.setTaskName("Run tasks");
						
						
						for(String s : d.getGroups()){
							monitor.beginTask("Send Tasks for group " + s, tl.getTasks().size());
							int counter = 0;
							for(TaskDatas t : tl.getTasks()){
								monitor.subTask("Task " + t.getName());
								t.getTaskProperties().setProperty("groupName", s);
								
								try {
									Activator.getDefault().getServerRemote().launchTask(t);
								} catch (Exception e1) {
									
									e1.printStackTrace();
								}
								monitor.worked(++counter);
							}
							
						}
						
						
						
						
						
					}
					
				};
				
				IProgressService service = PlatformUI.getWorkbench().getProgressService();
			     try {
			    	 service.run(true, false, r);
			    	 
			    	 
			     } catch (InvocationTargetException ex) {
			        ex.printStackTrace();
			     } catch (InterruptedException ex) {
			        ex.printStackTrace();
			     }
				
				
			}
			
		});
		runSelectedList.setEnabled(false);
	}

	
	private void createMenu(){
		MenuManager mgr = new MenuManager();
		
		final Action removeTask = new Action("Remove Task From List"){
			public void run(){
				TaskDatas t = (TaskDatas)((IStructuredSelection)managerViewer.getSelection()).getFirstElement();
				t.getParent().removeTask(t);
				managerViewer.refresh();
				try{
					Activator.getDefault().getTaskListManager().save();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		};
		removeTask.setText("Remove Task From List");
		
		final Action addTask = new Action("Add Task To List"){
			public void run(){
				TaskList l = (TaskList)((IStructuredSelection)managerViewer.getSelection()).getFirstElement();
				TaskDataWizard wiz = new TaskDataWizard(l);
				
				WizardDialog d = new WizardDialog(getShell(), wiz);
				d.open();
				managerViewer.refresh();
				
				try{
					Activator.getDefault().getTaskListManager().save();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		};
		addTask.setText("Add Task To List");
		mgr.add(addTask);
		mgr.add(removeTask);
		
		mgr.addMenuListener(new IMenuListener() {
			
			public void menuAboutToShow(IMenuManager manager) {
				if (managerViewer.getSelection().isEmpty()){
					removeTask.setEnabled(false);
					addTask.setEnabled(false);
				}
				else{
					Object o = ((IStructuredSelection)managerViewer.getSelection()).getFirstElement();
					if (o instanceof TaskList){
						removeTask.setEnabled(false);
						addTask.setEnabled(true);
					}
					else if (o instanceof TaskDatas){
						removeTask.setEnabled(true);
						addTask.setEnabled(false);
					}
					else{
						removeTask.setEnabled(false);
						addTask.setEnabled(false);
					}
				}
				
			}
		});
	
		managerViewer.getTree().setMenu(mgr.createContextMenu(managerViewer.getControl()));
	}
}
