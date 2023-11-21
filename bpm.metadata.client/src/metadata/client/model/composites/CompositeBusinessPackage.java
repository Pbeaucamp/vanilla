package metadata.client.model.composites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import metadata.client.helper.GroupHelper;
import metadata.client.i18n.Messages;
import metadata.client.viewer.TableLabelProvider;
import metadataclient.Activator;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;

public class CompositeBusinessPackage extends Composite {
	private static final Color RED = new Color(Display.getDefault(), 255, 0, 0);

	public class TElement{
		Integer pos;
		IBusinessTable table;
	}
	
	private TableViewer orderViewer;
	
	private HashMap<IBusinessTable, Integer> tableOrder = new HashMap<IBusinessTable, Integer>();

	private Text name;
	private Text description;
	
	private CheckboxTreeViewer tableGroups;
	private CheckboxTreeViewer explorableViewer;
	
	private Viewer viewer;
	private boolean containApply = false;
	private IBusinessPackage pack;
	
	
	private ComboViewer locale;
	private Text outputName;
	
	private int begin = 0;
	private int step = 1000;
	private Button isExplorable;
	
	private Label errorLabel;
	private Button ok, cancel;
	private boolean isFilling = false;
	
	public CompositeBusinessPackage(Composite parent, int style, Viewer viewer, boolean containApply, IBusinessPackage pack) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.containApply = containApply;
		this.pack = pack;
		this.viewer = viewer;

		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		errorLabel = new Label(this, SWT.NONE);
    	errorLabel.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 2, 1));
    	errorLabel.setForeground(RED);
    	errorLabel.setVisible(false);
		
		TabItem itemGal = new TabItem(tabFolder, SWT.NONE, 0);
		itemGal.setText(Messages.CompositeBusinessPackage_0); //$NON-NLS-1$
		itemGal.setControl(createGeneral(tabFolder));
    	
    	TabItem itemSec = new TabItem(tabFolder, SWT.NONE, 1);
    	itemSec.setText(Messages.CompositeBusinessPackage_1); //$NON-NLS-1$
    	itemSec.setControl(createSecurity(tabFolder));
    	
    	TabItem tableOrder = new TabItem(tabFolder, SWT.NONE, 2);
		tableOrder.setText(Messages.CompositeBusinessPackage_6);
		tableOrder.setControl(buildOrder(tabFolder));
    	
    	setInput();
    	fillData();
	}
	
	private void setErrorMessage(String message){
		if (message == null){
			errorLabel.setVisible(false);
		}
		else{
			errorLabel.setText(message);
			errorLabel.setVisible(true);
		}
		
	}
	
	
	private Control createToolbar(Composite parent){
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData());
		
		ToolItem checkAll = new ToolItem(toolbar, SWT.PUSH);
		checkAll.setToolTipText("Check All"); //$NON-NLS-1$
		checkAll.setImage(Activator.getDefault().getImageRegistry().get("check")); //$NON-NLS-1$
		checkAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				tableGroups.setAllChecked(true);
				for(Object o : ((IStructuredContentProvider)tableGroups.getContentProvider()).getElements(tableGroups.getInput())){
					pack.setGranted(((Secu)o).groupName, true);
				}
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		ToolItem uncheckAll = new ToolItem(toolbar, SWT.PUSH);
		uncheckAll.setToolTipText("Uncheck All"); //$NON-NLS-1$
		uncheckAll.setImage(Activator.getDefault().getImageRegistry().get("uncheck")); //$NON-NLS-1$
		uncheckAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				tableGroups.setAllChecked(false);
				for(Object o : ((IStructuredContentProvider)tableGroups.getContentProvider()).getElements(tableGroups.getInput())){
					pack.setGranted(((Secu)o).groupName, false);
				}
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		return toolbar;
	}
	
	private Control createSecurity(TabFolder folder){
		Composite parent = new Composite(folder, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l0 = new Label(parent, SWT.NONE);
		l0.setLayoutData(new GridData());
		l0.setText(Messages.CompositeBusinessPackage_2); //$NON-NLS-1$

		
		createToolbar(parent);
		
		tableGroups = new CheckboxTreeViewer(parent, SWT.V_SCROLL  | SWT.VIRTUAL);
		tableGroups.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		tableGroups.setContentProvider(new ITreeContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Secu> list = (List<Secu>) inputElement;
				return list.toArray(new Secu[list.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				return null;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
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
				pack.setGranted(((Secu)event.getElement()).groupName, event.getChecked());
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
		
		
		return parent;
	}

	
	private Control createGeneral(TabFolder folder){
		
		Composite parent = new Composite(folder, SWT.NONE);
		parent.setLayout(new GridLayout(3, true));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeBusinessPackage_3); //$NON-NLS-1$
		
		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		name.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				
				for(IBusinessPackage p: ((BusinessModel)pack.getBusinessModel()).getBusinessPackages("none")){ //$NON-NLS-1$
					if ( p != null && p != pack && p.getName().equals(name.getText())){
						setErrorMessage(Messages.CompositeBusinessPackage_13);
						break;
					}
					else{
						setErrorMessage(null);
					}
				}
				
				
				
				
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 2));
		l2.setText(Messages.CompositeBusinessPackage_4); //$NON-NLS-1$
		
		description = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 2));

		
		Group grpInternationalisation = new Group(parent, SWT.NONE);
		grpInternationalisation.setLayout(new GridLayout(3, false));
		grpInternationalisation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		grpInternationalisation.setText("Internationalisation");
		
		Label l3 = new Label(grpInternationalisation, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.CompositeBusinessPackage_5); //$NON-NLS-1$
		
		locale = new ComboViewer(grpInternationalisation, SWT.READ_ONLY);
		locale.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		locale.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Locale> l = (List<Locale>)inputElement;
				return l.toArray(new Locale[l.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		locale.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Locale)element).getDisplayName();
			}
			
		});
		locale.setInput(Activator.getDefault().getModel().getLocales());
		try{
			locale.setSelection(new StructuredSelection(Locale.getDefault()));
		}catch(Exception ex){
			
		}
		locale.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)locale.getSelection();
				
				if (ss.isEmpty()){
					return;
				}
				String s = pack.getOuputName((Locale)ss.getFirstElement());
				if (s == null){
					s= ""; //$NON-NLS-1$
				}
				outputName.setText(s);
				
			}

		});
		
		
		Label l4 = new Label(grpInternationalisation, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.CompositeBusinessPackage_7); //$NON-NLS-1$
		
		outputName = new Text(grpInternationalisation, SWT.BORDER);
		outputName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		outputName.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				IStructuredSelection ss = (IStructuredSelection)locale.getSelection();
				
				if (ss.isEmpty()){
					return;
				}
				pack.setOutputName((Locale)ss.getFirstElement(), outputName.getText());
				notifyListeners(SWT.Selection, new Event());
			}
			
		});

		isExplorable = new Button(parent, SWT.CHECK);
		isExplorable.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		isExplorable.setText(Messages.CompositeBusinessPackage_14);
		isExplorable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		Label l5 = new Label(parent, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 3, 1));
		l5.setText(Messages.CompositeBusinessPackage_15); 

		
		explorableViewer = new CheckboxTreeViewer(parent, SWT.BORDER | SWT.V_SCROLL);
		explorableViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		explorableViewer.setContentProvider(new ITreeContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<IBusinessTable>  l = (List<IBusinessTable>)inputElement;
				return l.toArray(new IBusinessTable[l.size()]);
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
		explorableViewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((IBusinessTable)element).getName();
			}
			
		});
		explorableViewer.addCheckStateListener(new ICheckStateListener() {
			
			public void checkStateChanged(CheckStateChangedEvent event) {
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
				
		if (containApply){
			Composite bar = new Composite(this, SWT.NONE);
			bar.setLayout(new GridLayout(2, true));
			bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
			
			ok = new Button(bar, SWT.PUSH);
			ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			ok.setText(Messages.CompositeBusinessPackage_8); //$NON-NLS-1$-
			ok.setEnabled(false);
			ok.addSelectionListener(new SelectionAdapter(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					setData();
					if (viewer!= null){
						viewer.refresh();
						Activator.getDefault().setChanged();
					}
					cancel.setEnabled(false);
					ok.setEnabled(false);
				}
				
			});
			
			cancel = new Button(bar, SWT.PUSH);
			cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			cancel.setText(Messages.CompositeBusinessPackage_9); //$NON-NLS-1$
			cancel.setEnabled(false);
			cancel.addSelectionListener(new SelectionAdapter(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					fillData();
					cancel.setEnabled(false);
					ok.setEnabled(false);
				}
				
			});
			
			addListener(SWT.Selection, new Listener() {
				
				public void handleEvent(Event event) {
					if (!isFilling){
						ok.setEnabled(isFilled());
						cancel.setEnabled(true);
					}
					
				}
			});
			
		}
		return parent;
	}
	
	private void fillData(){
		isFilling = true;
		name.setText(pack.getName());
		description.setText(pack.getDescription());
		isExplorable.setSelection(pack.isExplorable());
		explorableViewer.setInput(pack.getExplorableTables());
		List<IBusinessTable> l = pack.getFirstAccessibleTables();
		explorableViewer.setCheckedElements(l.toArray(new Object[l.size()]));
		
		
		
		for(Secu s : (List<Secu>)tableGroups.getInput()){

			if (pack.isGrantedFor(s.groupName)){
				s.visible = true;
				
				tableGroups.setChecked(s, true);
			}
			else{
				s.visible = false;
				tableGroups.setChecked(s, false);
			}
		}
		
//		List<Secu> ss =  (List<Secu>)tableGroups.getInput();
//		for(Secu s : ss){
//			tableGroups.setChecked(s, s.visible);
//
//		}
		
		tableGroups.refresh();
		
//		viewer.refresh();
		
		//XXX Leave it else it doesn't work.
		//Don't ask...
//		Object[] c = tableGroups.getCheckedElements();
		
		int i = 0;
		for(IBusinessTable t : pack.getOrderedTables("none")){ //$NON-NLS-1$
			tableOrder.put(t, i++);
		}
		
		orderViewer.setInput(tableOrder);
		isFilling = false;
	}
	
	private void setData(){
		List<Secu> groups = (List<Secu>)tableGroups.getInput();
		
		for(Secu s : groups){
			if (tableGroups.getChecked(s)){
				s.visible = true;
				pack.setGranted(s.groupName, true);
				
			}
			else{
				s.visible = false;
				pack.setGranted(s.groupName, false);
			}
		}
		pack.setName(name.getText());
		pack.setDescription(description.getText());
		pack.setExplorable(isExplorable.getSelection());
		
		List<IBusinessTable> l = pack.getFirstAccessibleTables();
		
		for(IBusinessTable t : l){
			pack.removeAccessible(t);
		}

		for(Object t : explorableViewer.getCheckedElements()){
			pack.addAccessible((IBusinessTable)t);
		}
		
		for(IBusinessTable t : tableOrder.keySet()){
			pack.order(t.getName(), tableOrder.get(t));
		}
	}
	
	private void setInput(){
		List<Secu> ss = new ArrayList<Secu>();
		
		for(String s : GroupHelper.getGroups(begin, step)){
			Secu c = new Secu();
			c.groupName = s;
			c.visible = false;
			ss.add(c);
		}
		tableGroups.setInput(ss);
		List<IBusinessTable> l = new ArrayList<IBusinessTable>();
		l.addAll(pack.getBusinessTables("none")); //$NON-NLS-1$
		
		fillData();
	}
	
	
	
	private Control buildOrder(TabFolder folder){
		Composite parent = new Composite(folder, SWT.NONE);
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		ToolItem up = new ToolItem(toolbar, SWT.PUSH);
		up.setToolTipText(Messages.CompositePackage_5); //$NON-NLS-1$
		up.setImage(Activator.getDefault().getImageRegistry().get("up")); //$NON-NLS-1$
		up.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (orderViewer.getSelection().isEmpty()){
					return;
				}
				
				
				TElement el = (TElement)((IStructuredSelection)orderViewer.getSelection()).getFirstElement();
				

				for(IBusinessTable t : tableOrder.keySet()){
					if (tableOrder.get(t).equals(el.pos - 1)){
						tableOrder.put(t, el.pos);
						tableOrder.put(el.table, el.pos - 1);
						orderViewer.refresh();
						orderViewer.setSelection(new StructuredSelection(el));
						
						int i = 0;
						for(TableItem it : orderViewer.getTable().getItems()){
							if (((TElement)it.getData()).table == el.table){
								orderViewer.getTable().select(i);
								break;
							}
							else{
								i++;
							}
						}
						
						break;
					}
					
					
				}
				notifyListeners(SWT.Selection, new Event());
			}
			
		});
		
		ToolItem down = new ToolItem(toolbar, SWT.PUSH);
		down.setToolTipText(Messages.CompositePackage_7); //$NON-NLS-1$
		down.setImage(Activator.getDefault().getImageRegistry().get("down")); //$NON-NLS-1$
		down.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (orderViewer.getSelection().isEmpty()){
					return;
				}
				
				
				TElement el = (TElement)((IStructuredSelection)orderViewer.getSelection()).getFirstElement();
				

				for(IBusinessTable t : tableOrder.keySet()){
					if (tableOrder.get(t).equals(el.pos + 1)){
						tableOrder.put(t, el.pos);
						tableOrder.put(el.table, el.pos + 1);
						orderViewer.refresh();
						
						
						int i = 0;
						for(TableItem it : orderViewer.getTable().getItems()){
							if (((TElement)it.getData()).table == el.table){
								orderViewer.getTable().select(i);
								break;
							}
							else{
								i++;
							}
						}
						
						break;
					}
					
					
				}
				notifyListeners(SWT.Selection, new Event());
			}
			
		});
		
		
		orderViewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		orderViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		orderViewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				HashMap<IBusinessTable, Integer> in = (HashMap<IBusinessTable, Integer>)inputElement;
				
				List<TElement> l = new ArrayList<TElement>();
				for(IBusinessTable t : in.keySet()){
					TElement e = new TElement();
					e.pos = in.get(t);
					e.table = t;
					l.add(e);
				}
				
				return l.toArray(new TElement[l.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
			}
			
		});
		orderViewer.setLabelProvider(new TableLabelProvider(){

			@Override
			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == 0){
					return ((TElement)element).pos + ""; //$NON-NLS-1$
				}
				else{
					return ((TElement)element).table.getName();
				}

			}
			
		});
		orderViewer.setSorter(new ViewerSorter(){

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return ((TElement)e1).pos.compareTo(((TElement)e2).pos);
			}
			
		});
		
		orderViewer.setInput(tableOrder);
		orderViewer.getTable().setHeaderVisible(true);
		TableColumn order = new TableColumn(orderViewer.getTable(), SWT.BORDER| SWT.FLAT);
		order.setText(Messages.CompositePackage_10); //$NON-NLS-1$
		order.setWidth(100);
		
		
		TableColumn table = new TableColumn(orderViewer.getTable(), SWT.BORDER| SWT.FLAT);
		table.setText(Messages.CompositePackage_11); //$NON-NLS-1$
		table.setWidth(300);
		
		return parent;
	}
	
	private class Secu{
		public String groupName;
		Boolean visible;
	}
	
	public boolean isFilled() {
		return !errorLabel.isVisible();
	}
}
