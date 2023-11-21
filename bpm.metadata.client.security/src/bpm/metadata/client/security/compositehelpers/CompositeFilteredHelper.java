package bpm.metadata.client.security.compositehelpers;

import java.util.ArrayList;
import java.util.List;

import metadata.client.helper.GroupHelper;
import metadata.client.i18n.Messages;
import metadata.client.model.composites.HideGroupsFilter;
import metadata.client.model.dialog.DialogComplexFilter;
import metadata.client.model.dialog.DialogFilter;
import metadata.client.model.dialog.DialogLogicalFilter;
import metadata.client.model.dialog.DialogSqlFilter2;
import metadataclient.Activator;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
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
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.metadata.IFiltered;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.olap.UnitedOlapDatasource;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.SqlQueryFilter;

public class CompositeFilteredHelper{
	protected class Secu{
		public String groupName;
		Boolean visible;
	}
	
	private TableViewer filterViewer;
	
	protected CheckboxTreeViewer tableGroups;
	private ToolItem edit;
	private IFiltered stream;
	
	protected FormToolkit toolkit;
	
	public CompositeFilteredHelper(FormToolkit toolkit, Composite parent,int style) {
		this.toolkit = toolkit;
	}

	
	
	
	public void createFilters(final Composite parent){
		
		Composite main = toolkit.createComposite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event) {
				parent.notifyListeners(SWT.Selection, event);
				
			}
		});
		createFiltersToolbar(main);
		createFilterViewer(main);
		
		createToolbar(main);
		createGroupViewer(main);
	}
	private void createFilterViewer(Composite parent){
		filterViewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.FULL_SELECTION);
		filterViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		filterViewer.setLabelProvider(new ITableLabelProvider(){


			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}


			public String getColumnText(Object element, int columnIndex) {
				IFilter el = (IFilter)element;
				
				if (columnIndex == 0){
					
						return el.getName();
//					}
//					return el.getOrigin().getName();
				}
				else if (columnIndex == 1){
					String s = ""; //$NON-NLS-1$
					boolean first = true;
					
					if (el instanceof Filter){
						for(String v : ((Filter)el).getValues()){
							if (first){
								first = false;
								s += Messages.CompositeDataStream_11; //$NON-NLS-1$
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
					else if (el instanceof SqlQueryFilter){
						try{
							s += ((SqlQueryFilter)el).getSqlWhereClause();
						}catch(Exception ex){
							
						}
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
		filterViewer.setContentProvider(new IStructuredContentProvider(){


			public Object[] getElements(Object inputElement) {
				List<IFilter> l = (List<IFilter>)inputElement;
				return l.toArray(new IFilter[l.size()]);
			}


			public void dispose() {
				
				
			}


			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		filterViewer.setInput(new ArrayList<IFilter>());
		
		filterViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)filterViewer.getSelection();
				edit.setEnabled(!ss.isEmpty());
				
				if(ss.isEmpty()){
					tableGroups.addFilter(new HideGroupsFilter());
					return;
				}
				else{
					tableGroups.setFilters(new ViewerFilter[]{});
				}
				
				IFilter e = (IFilter)ss.getFirstElement();
				for(Secu s : (List<Secu>)tableGroups.getInput()){
//					List d = ;
					if (stream.isFilterApplyedToGroup(s.groupName, e)){
						s.visible = true;
						tableGroups.setChecked(s, true);
					}
					else{
						s.visible = false;
						tableGroups.setChecked(s, false);
					}
	
				}
				
			}
			
		});
		
		TableColumn c1 = new TableColumn(filterViewer.getTable(), SWT.NONE);
		c1.setText(Messages.CompositeDataStream_14); 
		c1.setWidth(100);
		
		TableColumn c2 = new TableColumn(filterViewer.getTable(), SWT.NONE);
		c2.setText(Messages.CompositeDataStream_15); 
		c2.setWidth(300);

		filterViewer.getTable().setHeaderVisible(true);

		
	}
	
	
	private void createFiltersToolbar(final Composite parent){
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.CompositeDataStream_2);
		add.setImage(Activator.getDefault().getImageRegistry().get("filter")); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogLogicalFilter dial = null;
				if (stream instanceof IDataStream ){
					dial = new DialogLogicalFilter(parent.getShell(), (IDataStream)stream);
				}
				else if (stream instanceof IBusinessTable){
					dial = new DialogLogicalFilter(parent.getShell(), (IBusinessTable)stream);
				}
				else{
					return;
				}
				
				if (dial.open() == DialogLogicalFilter.OK){
					((List<IFilter>)filterViewer.getInput()).add(dial.getFilter());
					
					for(Secu s : (List<Secu>)tableGroups.getInput()){
						stream.addFilter(s.groupName, dial.getFilter());
					}
					
					filterViewer.refresh();
					filterViewer.setSelection(new StructuredSelection(dial.getFilter()));
					parent.notifyListeners(SWT.Selection, new Event());
				}
			}
			
		});
		
		
		ToolItem addComplex = new ToolItem(toolbar, SWT.PUSH);
		addComplex.setToolTipText(Messages.CompositeDataStream_4);
		addComplex.setImage(Activator.getDefault().getImageRegistry().get("filter_complex")); //$NON-NLS-1$
		addComplex.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				DialogComplexFilter dial = null;
				if (stream instanceof IDataStream ){
					dial = new DialogComplexFilter(parent.getShell(), (IDataStream)stream);
				}
				else if (stream instanceof IBusinessTable){
					dial = new DialogComplexFilter(parent.getShell(), (IBusinessTable)stream);
				}
				else{
					return;
				}
				
				if (dial.open() == DialogLogicalFilter.OK){
					((List<IFilter>)filterViewer.getInput()).add(dial.getFilter());
					
					for(Secu s : (List<Secu>)tableGroups.getInput()){
						stream.addFilter(s.groupName, dial.getFilter());
					}
					
					filterViewer.refresh();
					filterViewer.setSelection(new StructuredSelection(dial.getFilter()));
					parent.notifyListeners(SWT.Selection, new Event());
				}
			}
			
		});
		
		
		
		
		ToolItem addSql = new ToolItem(toolbar, SWT.PUSH);
		addSql.setToolTipText(Messages.CompositeDataStream_6);
		addSql.setImage(Activator.getDefault().getImageRegistry().get("filter_sql")); //$NON-NLS-1$
		addSql.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				DialogSqlFilter2 dial = null;
				if (stream instanceof IDataStream ){
					dial = new DialogSqlFilter2(parent.getShell(), (IDataStream)stream);
				}
				else if (stream instanceof IBusinessTable){
					dial = new DialogSqlFilter2(parent.getShell(), (IBusinessTable)stream);
				}
				else{
					return;
				}
				if (dial.open() == DialogLogicalFilter.OK){
					((List<IFilter>)filterViewer.getInput()).add(dial.getFilter());
					
					for(Secu s : (List<Secu>)tableGroups.getInput()){
						stream.addFilter(s.groupName, dial.getFilter());
					}
					parent.notifyListeners(SWT.Selection, new Event());
					filterViewer.refresh();
					filterViewer.setSelection(new StructuredSelection(dial.getFilter()));
				}
			}
			
		});
		
		
		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.CompositeDataStream_9);
		del.setImage(Activator.getDefault().getImageRegistry().get("delete")); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)filterViewer.getSelection();
				
				for(Object o : ss.toList()){
					if (o instanceof IFilter){
						((List<IFilter>)filterViewer.getInput()).remove(o);
						
						stream.removeFilter((IFilter)o);
						filterViewer.refresh();
						
					}
				}
				parent.notifyListeners(SWT.Selection, new Event());
			}
			
		});
		
		
		edit = new ToolItem(toolbar, SWT.PUSH);
//		edit.setImage(Activator.getDefault().getImageRegistry().get(Messages.getString("CompositeDataStream.8"))); //$NON-NLS-1$
		edit.setToolTipText("Edit"); //$NON-NLS-1$
		edit.setEnabled(false);
		edit.setImage(Activator.getDefault().getImageRegistry().get("edit")); //$NON-NLS-1$
		edit.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)filterViewer.getSelection();
				IFilter f = (IFilter)ss.getFirstElement();
				
				if (f instanceof SqlQueryFilter){
					DialogSqlFilter2 d = new DialogSqlFilter2(parent.getShell(), (SqlQueryFilter)f);
					d.open();
				}
				else if (f instanceof ComplexFilter){
					DialogComplexFilter d = new DialogComplexFilter(parent.getShell(), (ComplexFilter)f);
					d.open();
					filterViewer.refresh();
				}
				else if (f instanceof Filter){
					boolean isOlap = (((Filter)f).getOrigin().getDataStream().getDataSource() instanceof UnitedOlapDatasource) ? true : false;
					DialogFilter d = new DialogFilter(parent.getShell(), (Filter)f, isOlap);
					d.open();
					filterViewer.refresh();
				}
				parent.notifyListeners(SWT.Selection, new Event());
			}
			
		});
		
		

	}

	protected void fill() {
		
				
		filterViewer.setInput(stream.getFilters());
	}
	
	public void setFiltered(IFiltered stream){
		this.stream = stream;
		fill();
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
				if (filterViewer.getSelection().isEmpty()){
					return;
				}
				
				
				tableGroups.setAllChecked(true);
				for(Object o : ((IStructuredContentProvider)tableGroups.getContentProvider()).getElements(tableGroups.getInput())){
					stream.addFilter(((Secu)o).groupName, (IFilter)((IStructuredSelection)filterViewer.getSelection()).getFirstElement());
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
				if (filterViewer.getSelection().isEmpty()){
					return;
				}
				tableGroups.setAllChecked(false);
				for(Object o : ((IStructuredContentProvider)tableGroups.getContentProvider()).getElements(tableGroups.getInput())){
					stream.removeFilter(((Secu)o).groupName, (IFilter)((IStructuredSelection)filterViewer.getSelection()).getFirstElement());
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
				if (stream == null){
					return;
				}
				boolean b = tableGroups.getChecked(s);
				if (b){
					stream.addFilter(s.groupName, (IFilter)((IStructuredSelection)filterViewer.getSelection()).getFirstElement());
				}
				else{
					stream.removeFilter(s.groupName, (IFilter)((IStructuredSelection)filterViewer.getSelection()).getFirstElement());
				}
				
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
		tableGroups.addFilter(new HideGroupsFilter());
	}
	

}
