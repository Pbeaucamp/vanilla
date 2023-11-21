package metadata.client.model.composites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import metadata.client.helper.GroupHelper;
import metadata.client.i18n.Messages;
import metadata.client.model.dialog.DialogComplexFilter;
import metadata.client.model.dialog.DialogFilter;
import metadata.client.model.dialog.DialogLogicalFilter;
import metadata.client.model.dialog.DialogSqlFilter2;
import metadata.client.viewer.TableLabelProvider;
import metadataclient.Activator;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.UnitedOlapBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.SqlQueryFilter;

public class CompositeBusinessTable extends Composite {

	public class TElement{
		Integer pos;
		IDataStreamElement col;
	}
	
	private static int begin = 725;
	private static int step = 20;
	
	private List<String> groupList;
	private TableViewer orderViewer;
	
	private HashMap<IDataStreamElement, Integer> columnsOrder = new LinkedHashMap<IDataStreamElement, Integer>();

	
	
	private IBusinessTable businessTable;
	private Text description, name;
	private Viewer v;
	
	private CheckboxTreeViewer tableGroups, filterGroups;
	private TableViewer tableFilters;
	
	private ComboViewer locale;
	private Text outputName;
	private Button drillable;
	
	public CompositeBusinessTable(Composite parent, int style, Viewer v, IBusinessTable businessTable) {
		super(parent, style);
		this.businessTable = businessTable;
		this.v = v;
		this.setLayout(new GridLayout(2, false));
		groupList = GroupHelper.getGroups(begin, step);	
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		TabItem itemGal = new TabItem(tabFolder, SWT.NONE, 0);
		itemGal.setText(Messages.CompositeBusinessTable_0); //$NON-NLS-1$
		itemGal.setControl(createGeneral(tabFolder));
    	
   	
    	TabItem itemSec = new TabItem(tabFolder, SWT.NONE, 1);
    	itemSec.setText(Messages.CompositeBusinessTable_1); //$NON-NLS-1$
    	itemSec.setControl(createSecurity(tabFolder));
    	
    	
    	TabItem itemFilter = new TabItem(tabFolder, SWT.NONE, 2);
    	itemFilter.setText(Messages.CompositeBusinessTable_2); //$NON-NLS-1$
    	itemFilter.setControl(createFilter(tabFolder));
    	
    	TabItem itemOrder = new TabItem(tabFolder, SWT.NONE, 3);
    	itemOrder.setText(Messages.CompositeBusinessTable_6);
    	itemOrder.setControl(buildOrder(tabFolder));
    	
    	setInput();
    	
    	if (businessTable != null){
			fillDatas();
		}

    	
	}
	
	
	
	private Control createFilter(TabFolder folder){
		Composite parent = new Composite(folder, SWT.NONE);
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
	
		
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.CompositeBusinessTable_3); //$NON-NLS-1$
		add.setImage(Activator.getDefault().getImageRegistry().get("filter")); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogLogicalFilter dial = new DialogLogicalFilter(getShell(), businessTable);
				if (dial.open() == DialogLogicalFilter.OK){
					((List<IFilter>)tableFilters.getInput()).add(dial.getFilter());
					
					for(Secu s : (List<Secu>)filterGroups.getInput()){
						businessTable.addFilter(s.groupName, dial.getFilter());
					}
					
					tableFilters.refresh();
					
				}
			}
			
		});
		
		if(!(businessTable instanceof UnitedOlapBusinessTable)) {
		
			ToolItem addComplex = new ToolItem(toolbar, SWT.PUSH);
			addComplex.setToolTipText(Messages.CompositeBusinessTable_5); //$NON-NLS-1$
			addComplex.setImage(Activator.getDefault().getImageRegistry().get("filter_complex")); //$NON-NLS-1$
			addComplex.addSelectionListener(new SelectionAdapter(){
	
				@Override
				public void widgetSelected(SelectionEvent e) {
					DialogComplexFilter dial = new DialogComplexFilter(getShell(), businessTable);
					if (dial.open() == DialogLogicalFilter.OK){
						((List<IFilter>)tableFilters.getInput()).add(dial.getFilter());
						
						for(Secu s : (List<Secu>)filterGroups.getInput()){
							businessTable.addFilter(s.groupName, dial.getFilter());
						}
						
						tableFilters.refresh();
						
					}
				}
				
			});
			
			
			
			
			ToolItem addSql = new ToolItem(toolbar, SWT.PUSH);
			addSql.setToolTipText(Messages.CompositeBusinessTable_7); //$NON-NLS-1$
			addSql.setImage(Activator.getDefault().getImageRegistry().get("filter_sql")); //$NON-NLS-1$
			addSql.addSelectionListener(new SelectionAdapter(){
	
				@Override
				public void widgetSelected(SelectionEvent e) {
					DialogSqlFilter2 dial = new DialogSqlFilter2(getShell(), businessTable);
					if (dial.open() == DialogLogicalFilter.OK){
						((List<IFilter>)tableFilters.getInput()).add(dial.getFilter());
						
						for(Secu s : (List<Secu>)filterGroups.getInput()){
							businessTable.addFilter(s.groupName, dial.getFilter());
						}
						
						tableFilters.refresh();
						
					}
				}
				
			});
		}
			
		final ToolItem edit = new ToolItem(toolbar, SWT.PUSH);
		edit.setToolTipText("Edit"); //$NON-NLS-1$
		edit.setEnabled(false);
		edit.setImage(Activator.getDefault().getImageRegistry().get("edit")); //$NON-NLS-1$
		edit.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)tableFilters.getSelection();
				IFilter f = (IFilter)ss.getFirstElement();
				
				if (f instanceof SqlQueryFilter){
					DialogSqlFilter2 d = new DialogSqlFilter2(getShell(), (SqlQueryFilter)f);
					d.open();
				}
				else if (f instanceof ComplexFilter){
					DialogComplexFilter d = new DialogComplexFilter(getShell(), (ComplexFilter)f);
					d.open();
					tableFilters.refresh();
				}
				else if (f instanceof Filter){
					DialogFilter d = new DialogFilter(getShell(), (Filter)f, businessTable instanceof UnitedOlapBusinessTable);
					d.open();
					tableFilters.refresh();
				}
				
			}
			
		});
		

		
		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setImage(Activator.getDefault().getImageRegistry().get("delete")); //$NON-NLS-1$
		del.setToolTipText(Messages.CompositeBusinessTable_10); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)tableFilters.getSelection();
				
				for(Object o : ss.toList()){
					if (o instanceof IFilter){
						((List<IFilter>)tableFilters.getInput()).remove(o);
						
						businessTable.removeFilter((IFilter)o);
						tableFilters.refresh();
						
					}
				}
			}
			
		});
		
		
		
		
		
		//filters
		GridData gridTable = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1);
		gridTable.minimumHeight = 250;
		
		tableFilters = new TableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableFilters.getTable().setLayoutData(gridTable);
		tableFilters.setLabelProvider(new ITableLabelProvider(){


			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}


			public String getColumnText(Object element, int columnIndex) {
				IFilter el = (IFilter)element;
				
				if (columnIndex == 0){
					return el.getOrigin().getName();
				}
				else if (columnIndex == 1){
					String s = ""; //$NON-NLS-1$
					boolean first = true;
					
					if (el instanceof Filter){
						for(String v : ((Filter)el).getValues()){
							if (first){
								first = false;
								s += " in "; //$NON-NLS-1$
							}
							else{
								s += ","; //$NON-NLS-1$
							}
							s += v;
						}
					}
					else if (el instanceof ComplexFilter){
						s += ((ComplexFilter)el).getOperator() + " " + ((ComplexFilter)el).getValue(); //$NON-NLS-1$
						
						
					}
					
					
					return s;
				}
				return null;
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
		tableFilters.setContentProvider(new IStructuredContentProvider(){


			public Object[] getElements(Object inputElement) {
				List<IFilter> l = (List<IFilter>)inputElement;
				return l.toArray(new IFilter[l.size()]);
			}


			public void dispose() {
				
			}


			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		tableFilters.setInput(new ArrayList<IFilter>());
		
		tableFilters.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)tableFilters.getSelection();
				
				if(ss.isEmpty()){
					edit.setEnabled(false);
					return;
				}
				edit.setEnabled(true);
				IFilter e = (IFilter)ss.getFirstElement();
				for(Secu s : (List<Secu>)filterGroups.getInput()){
					if (businessTable.getFilterFor(s.groupName).contains(e)){
						s.visible = true;
						filterGroups.setChecked(s, true);
					}
					else{
						s.visible = false;
						filterGroups.setChecked(s, false);
					}
	
				}
			}
			
		});
		
		TableColumn c1 = new TableColumn(tableFilters.getTable(), SWT.NONE);
		c1.setText(Messages.CompositeBusinessTable_15); //$NON-NLS-1$
		c1.setWidth(50);
		
		TableColumn c2 = new TableColumn(tableFilters.getTable(), SWT.NONE);
		c2.setText(Messages.CompositeBusinessTable_16); //$NON-NLS-1$
		c2.setWidth(50);

		tableFilters.getTable().setHeaderVisible(true);

		
		
		filterGroups = new CheckboxTreeViewer(parent, SWT.V_SCROLL);
		filterGroups.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		filterGroups.setContentProvider(new ITreeContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<Secu> list = (List<Secu>) inputElement;
				return list.toArray(new Secu[list.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
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
		
		filterGroups.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Secu)element).groupName;
			}
			
		});
		
		filterGroups.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				Secu s = (Secu)event.getElement();
				s.visible = event.getChecked();
				
				IStructuredSelection ss = (IStructuredSelection)tableFilters.getSelection();
				if (ss.isEmpty()){
					return;
				}
				
				IFilter f = (IFilter)ss.getFirstElement();
				if (event.getChecked()){
					businessTable.addFilter(s.groupName, f);
				}
				else{
					businessTable.removeFilter(s.groupName, f);
				}
			}
			
		});
		
		List<Secu> l = new ArrayList<Secu>();
		for(String s : groupList){
			Secu sc = new Secu();
			sc.groupName = new String (s);
			sc.visible = true;
			l.add(sc);
		}
		
		filterGroups.setInput(l);
		
		return parent;
	}
	
	
	
	private Control createGeneral(TabFolder folder){
		Composite parent = new Composite(folder, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeBusinessTable_17); //$NON-NLS-1$
		
		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.CompositeBusinessTable_18); //$NON-NLS-1$
		
		description = new Text(parent, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Group grpInternationalisation = new Group(parent, SWT.NONE);
		grpInternationalisation.setLayout(new GridLayout(2, false));
		grpInternationalisation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		grpInternationalisation.setText("Internationalisation");
		
		Label l3 = new Label(grpInternationalisation, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.CompositeBusinessTable_19); //$NON-NLS-1$
		
		locale = new ComboViewer(grpInternationalisation, SWT.READ_ONLY);
		locale.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
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
		locale.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)locale.getSelection();
				
				if (ss.isEmpty()){
					return;
				}
				String s = ((AbstractBusinessTable)businessTable).getOuputName((Locale)ss.getFirstElement());
				if (s == null){
					s= ""; //$NON-NLS-1$
				}
				outputName.setText(s);
				
			}

		});
		
		
		Label l4 = new Label(grpInternationalisation, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.CompositeBusinessTable_21); //$NON-NLS-1$
		
		outputName = new Text(grpInternationalisation, SWT.BORDER);
		outputName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		outputName.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				IStructuredSelection ss = (IStructuredSelection)locale.getSelection();
				
				if (ss.isEmpty()){
					return;
				}
				((AbstractBusinessTable)businessTable).setOutputName((Locale)ss.getFirstElement(), outputName.getText());
				
			}
			
		});
		
		drillable = new Button(parent, SWT.CHECK);
		drillable.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		drillable.setText(Messages.CompositeBusinessTable_8);
		
		
		Button ok = new Button(this, SWT.PUSH);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,	false));
		ok.setText(Messages.CompositeBusinessTable_22); //$NON-NLS-1$
		ok.addSelectionListener(new SelectionAdapter(){
			
			public void widgetSelected(SelectionEvent e) {
				setData();
				if (v != null){
					v.refresh();
					Activator.getDefault().setChanged();
				}
				
			}
			
		});
		
		Button cancel = new Button(this, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,	false));
		cancel.setText(Messages.CompositeBusinessTable_23); //$NON-NLS-1$
		cancel.addSelectionListener(new SelectionAdapter(){
			
			public void widgetSelected(SelectionEvent e) {
				fillDatas();
				
			}
			
		});
		return parent;
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
					businessTable.setGranted((String)o, true);
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
					businessTable.setGranted((String)o, false);
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
		l0.setText(Messages.CompositeBusinessTable_24); //$NON-NLS-1$
		
		
		
		createToolbar(parent);
		
		tableGroups = new CheckboxTreeViewer(parent, SWT.V_SCROLL  | SWT.VIRTUAL);
		tableGroups.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		tableGroups.setContentProvider(new ITreeContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<String> list = (List<String>) inputElement;
				return list.toArray(new String[list.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
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
		
		tableGroups.setLabelProvider(new LabelProvider());
		
		return parent;
	}
	
	
	private void setInput(){
		tableGroups.setInput(groupList);
		fillDatas();
	}
	
	public void setData(){
		
		for(IBusinessPackage p : businessTable.getModel().getBusinessPackages("none")){ //$NON-NLS-1$
			if (p.getBusinessTable("none", businessTable.getName()) != null){ //$NON-NLS-1$
				//TODO: WTF ? Remove remove and add for now
//				p.removeBusinessTable(businessTable);
				businessTable.setName(name.getText());
//				p.addBusinessTable(businessTable);
			}
		}
		businessTable.setName(name.getText());
		businessTable.setDescription(description.getText());
		
		for(String s : groupList){
			if (tableGroups.getChecked(s)){
				businessTable.setGranted(s, true);
			}
			else{
				businessTable.setGranted(s, false);
			}
		}
		
		businessTable.setIsDrillable(drillable.getSelection());
		
		List<IDataStreamElement> orders = new ArrayList<IDataStreamElement>();
		for(IDataStreamElement t : columnsOrder.keySet()){
//			((AbstractBusinessTable)businessTable).order(t, columnsOrder.get(t));
			int pos = columnsOrder.get(t);
			if (orders.size() > pos) {
				orders.add(pos, t);
			}
			else {
				orders.add(t);
			}
		}
		((AbstractBusinessTable)businessTable).setOrder(orders);
	}
	
	private void fillDatas(){
		if (businessTable != null){
			name.setText(businessTable.getName());
			description.setText(businessTable.getDescription());
			
			drillable.setSelection(businessTable.isDrillable());

			for(String s : groupList){
				if (businessTable.isGrantedFor(s)){
					tableGroups.setChecked(s, true);
				}
				else{
					tableGroups.setChecked(s, false);
				}
			}
			Object[] o = tableGroups.getCheckedElements();
			
			
			tableFilters.setInput(businessTable.getFilters());
			
			
			if (!(businessTable instanceof AbstractBusinessTable)){
				return;
			}
			
			if (((AbstractBusinessTable)businessTable).getOrders() == null){
				return;
			}

			columnsOrder.clear();

			int i = 0;
			for(IDataStreamElement s : ((AbstractBusinessTable)businessTable).getOrders()){
				if (s != null){
					columnsOrder.put(s, i++);
				}
				
			}
			orderViewer.setInput(columnsOrder);
			
		}
	}
	
	
	
	private class Secu{
		public String groupName;
		Boolean visible;
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
				

				for(IDataStreamElement t : columnsOrder.keySet()){
					if (columnsOrder.get(t).equals(el.pos - 1)){
						columnsOrder.put(t, el.pos);
						columnsOrder.put(el.col, el.pos - 1);
						orderViewer.refresh();
												
						int i = 0;
						for(TableItem it : orderViewer.getTable().getItems()){
							if (((TElement)it.getData()).col == el.col){
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
				

				for(IDataStreamElement t : columnsOrder.keySet()){
					if (columnsOrder.get(t).equals(el.pos + 1)){
						columnsOrder.put(t, el.pos);
						columnsOrder.put(el.col, el.pos + 1);
						orderViewer.refresh();
						orderViewer.setSelection(new StructuredSelection(el));
						int i = 0;
						for(TableItem it : orderViewer.getTable().getItems()){
							if (((TElement)it.getData()).col == el.col){
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
			}
		});
		
		
		orderViewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		orderViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		orderViewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				HashMap<IDataStreamElement, Integer> in = (HashMap<IDataStreamElement, Integer>)inputElement;
				
				List<TElement> l = new ArrayList<TElement>();
				for(IDataStreamElement t : in.keySet()){
					if (t != null){
						TElement e = new TElement();
						e.pos = in.get(t);
						e.col = t;
						l.add(e);
					}
					
				}
				
				return l.toArray(new TElement[l.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		orderViewer.setLabelProvider(new TableLabelProvider(){

			@Override
			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == 0){
					return ((TElement)element).pos + ""; //$NON-NLS-1$
				}
				else{
					return ((TElement)element).col.getName();
				}

			}
			
		});
		orderViewer.setSorter(new ViewerSorter(){

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return ((TElement)e1).pos.compareTo(((TElement)e2).pos);
			}
			
		});
		
		orderViewer.setInput(columnsOrder);
		orderViewer.getTable().setHeaderVisible(true);
		TableColumn order = new TableColumn(orderViewer.getTable(), SWT.BORDER| SWT.FLAT);
		order.setText(Messages.CompositeBusinessTable_14);
		order.setWidth(100);
		
		
		TableColumn table = new TableColumn(orderViewer.getTable(), SWT.BORDER| SWT.FLAT);
		table.setText(Messages.CompositeBusinessTable_20); 
		table.setWidth(300);
		
		return parent;
	}
}
