package metadata.client.model.composites;

import java.util.ArrayList;
import java.util.List;

import metadata.client.helper.GroupHelper;
import metadata.client.i18n.Messages;
import metadata.client.model.dialog.DialogComplexFilter;
import metadata.client.model.dialog.DialogCustomType;
import metadata.client.model.dialog.DialogFilter;
import metadata.client.model.dialog.DialogLogicalFilter;
import metadata.client.model.dialog.DialogSqlFilter2;
import metadataclient.Activator;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.olap.UnitedOlapDataStream;
import bpm.metadata.layer.logical.sql.SQLDataStream;
import bpm.metadata.layer.physical.sql.SQLTable;
import bpm.metadata.misc.Type;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.SqlQueryFilter;

public class CompositeDataStream extends Composite {
	private static final Color RED = new Color(Display.getDefault(), 255, 0, 0);
	
	
	
	private Text name, desc, outputLenght, weight, origin;
	private Text sqlType;
	
	private Combo type;
	private Viewer viewer;
	
	private ComboViewer customType;
	
	private IDataStream data;
	private boolean containApply = true;
	
	private CheckboxTableViewer tableGroups;
	private TableViewer tableFilters;
		
	private Label errorLabel;
	private Button ok, cancel;
	private boolean isFilling = false;
	
	public CompositeDataStream(Composite parent, int style, Viewer viewer, boolean containApply, IDataStream data) {
		super(parent, style);
		this.data = data;
		this.containApply = containApply;
		this.viewer = viewer;
		this.setLayout(new GridLayout());
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		
		errorLabel = new Label(this, SWT.NONE);
    	errorLabel.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 2, 1));
    	errorLabel.setForeground(RED);
    	errorLabel.setVisible(false);
		
		TabItem itemGal = new TabItem(tabFolder, SWT.NONE, 0);
		itemGal.setText(Messages.CompositeDataStream_0); //$NON-NLS-1$
		itemGal.setControl(createGeneral(tabFolder));
    	
		
    	TabItem itemFilters = new TabItem(tabFolder, SWT.NONE, 1);
    	itemFilters.setText(Messages.CompositeDataStream_1); //$NON-NLS-1$
    	itemFilters.setControl(createFilter(tabFolder));
    	
    	TabItem itemD4C = new TabItem(tabFolder, SWT.NONE, 2);
    	itemD4C.setText("DataViz");
    	itemD4C.setControl(new CompositeD4CTypes(tabFolder, SWT.NONE, data));
    	
		fillData();
	}
	private Control createFilter(TabFolder folder){
		Composite parent = new Composite(folder, SWT.NONE);
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
	
		
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.CompositeDataStream_2); 
		add.setImage(Activator.getDefault().getImageRegistry().get("filter")); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogLogicalFilter dial = new DialogLogicalFilter(getShell(), data);
				if (dial.open() == DialogLogicalFilter.OK){
					((List<IFilter>)tableFilters.getInput()).add(dial.getFilter());
					
					for(Secu s : (List<Secu>)tableGroups.getInput()){
						data.addFilter(s.groupName, dial.getFilter());
					}
					
					tableFilters.refresh();
					tableFilters.setSelection(new StructuredSelection(dial.getFilter()));
				}
			}
			
		});
		
		if(!(data instanceof UnitedOlapDataStream)) {
			ToolItem addComplex = new ToolItem(toolbar, SWT.PUSH);
			addComplex.setToolTipText(Messages.CompositeDataStream_4); //$NON-NLS-1$
			addComplex.setImage(Activator.getDefault().getImageRegistry().get("filter_complex")); //$NON-NLS-1$
			addComplex.addSelectionListener(new SelectionAdapter(){
	
				@Override
				public void widgetSelected(SelectionEvent e) {
					DialogComplexFilter dial = new DialogComplexFilter(getShell(), data);
					if (dial.open() == DialogLogicalFilter.OK){
						((List<IFilter>)tableFilters.getInput()).add(dial.getFilter());
						
						for(String g : dial.getFilter().getGrants().keySet()) {
							if(dial.getFilter().getGrants().get(g)) {
								data.addFilter(g, dial.getFilter());
							}
						}
//						for(Secu s : (List<Secu>)tableGroups.getInput()){
//							data.addFilter(s.groupName, dial.getFilter());
//						}
						
						tableFilters.refresh();
						tableFilters.setSelection(new StructuredSelection(dial.getFilter()));
					}
				}
				
			});
			
			
			
			
			ToolItem addSql = new ToolItem(toolbar, SWT.PUSH);
			addSql.setToolTipText(Messages.CompositeDataStream_6); //$NON-NLS-1$
			addSql.setImage(Activator.getDefault().getImageRegistry().get("filter_sql")); //$NON-NLS-1$
			addSql.addSelectionListener(new SelectionAdapter(){
	
				@Override
				public void widgetSelected(SelectionEvent e) {
					DialogSqlFilter2 dial = new DialogSqlFilter2(getShell(), data);
					if (dial.open() == DialogLogicalFilter.OK){
						((List<IFilter>)tableFilters.getInput()).add(dial.getFilter());
						for(String g : dial.getFilter().getGrants().keySet()) {
							if(dial.getFilter().getGrants().get(g)) {
								data.addFilter(g, dial.getFilter());
							}
						}
//						for(Secu s : (List<Secu>)tableGroups.getInput()){
//							
//						}
						
						tableFilters.refresh();
						tableFilters.setSelection(new StructuredSelection(dial.getFilter()));
					}
				}
				
			});
		}
		
		
		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.CompositeDataStream_9); //$NON-NLS-1$
		del.setImage(Activator.getDefault().getImageRegistry().get("delete")); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)tableFilters.getSelection();
				
				for(Object o : ss.toList()){
					if (o instanceof IFilter){
						((List<IFilter>)tableFilters.getInput()).remove(o);
						
						data.removeFilter((IFilter)o);
						tableFilters.refresh();
						tableFilters.setSelection(StructuredSelection.EMPTY);
					}
				}
			}
			
		});
		
		
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
					DialogFilter d = new DialogFilter(getShell(), (Filter)f, (data instanceof UnitedOlapDataStream));
					d.open();
					tableFilters.refresh();
				}
				
			}
			
		});
		
		
		ToolItem checkAll = new ToolItem(toolbar, SWT.PUSH);
		checkAll.setToolTipText("Check All"); //$NON-NLS-1$
		checkAll.setImage(Activator.getDefault().getImageRegistry().get("check")); //$NON-NLS-1$
		checkAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				IStructuredSelection ss = (IStructuredSelection)tableFilters.getSelection();
				if (ss.isEmpty()){
					return;
				}
				tableGroups.setAllChecked(true);
				IFilter f = (IFilter)ss.getFirstElement();
				for(Object o : ((IStructuredContentProvider)tableGroups.getContentProvider()).getElements(tableGroups.getInput())){
					data.addFilter(((Secu)o).groupName, f);
				}
				
			}
		});
		
		ToolItem uncheckAll = new ToolItem(toolbar, SWT.PUSH);
		uncheckAll.setToolTipText("Uncheck All"); //$NON-NLS-1$
		uncheckAll.setImage(Activator.getDefault().getImageRegistry().get("uncheck")); //$NON-NLS-1$
		uncheckAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)tableFilters.getSelection();
				if (ss.isEmpty()){
					return;
				}
				tableGroups.setAllChecked(false);
				IFilter f = (IFilter)ss.getFirstElement();
				for(Object o : ((IStructuredContentProvider)tableGroups.getContentProvider()).getElements(tableGroups.getInput())){
					data.removeFilter(((Secu)o).groupName, f);
				}
			}
		});
		
		//filters
		tableFilters = new TableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableFilters.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tableFilters.setLabelProvider(new ITableLabelProvider(){


			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}


			public String getColumnText(Object element, int columnIndex) {
				IFilter el = (IFilter)element;
				
				if (columnIndex == 0){
					
					return el.getName();
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
		tableFilters.setContentProvider(new IStructuredContentProvider(){


			public Object[] getElements(Object inputElement) {
				List<IFilter> l = (List<IFilter>)inputElement;
				return l.toArray(new IFilter[l.size()]);
			}


			public void dispose() {
				
			}


			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {}
			
		});
		tableFilters.setInput(new ArrayList<IFilter>());
		
		tableFilters.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)tableFilters.getSelection();
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
					if (data.isFilterApplyedToGroup(s.groupName, e)){
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
		
		TableColumn c1 = new TableColumn(tableFilters.getTable(), SWT.NONE);
		c1.setText(Messages.CompositeDataStream_14); //$NON-NLS-1$
		c1.setWidth(100);
		
		TableColumn c2 = new TableColumn(tableFilters.getTable(), SWT.NONE);
		c2.setText(Messages.CompositeDataStream_15); //$NON-NLS-1$
		c2.setWidth(300);

		tableFilters.getTable().setHeaderVisible(true);

		
		
		tableGroups = new CheckboxTableViewer(parent, SWT.V_SCROLL);
		tableGroups.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		tableGroups.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Secu> list = (List<Secu>) inputElement;
				return list.toArray(new Secu[list.size()]);
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {}
			
		});
		
		tableGroups.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Secu)element).groupName;
			}
			
		});
		
		tableGroups.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				Secu s = (Secu)event.getElement();
				s.visible = event.getChecked();
				
				IStructuredSelection ss = (IStructuredSelection)tableFilters.getSelection();
				if (ss.isEmpty()){
					return;
				}
				
				IFilter f = (IFilter)ss.getFirstElement();
				if (event.getChecked()){
					data.addFilter(s.groupName, f);
				}
				else{
					data.removeFilter(s.groupName, f);
				}
			}
			
		});

		tableGroups.setInput(getGroups());
		tableGroups.addFilter(new HideGroupsFilter());

		return parent;
	}
	
	

	
	private Control createGeneral(TabFolder folder){
		Composite parent = new Composite(folder, SWT.NONE);
		parent.setLayout(new GridLayout(3, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeDataStream_16); //$NON-NLS-1$
		
		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		name.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				for(IDataStream s : data.getDataSource().getDataStreams()){
					if ( s != null && s != data && s.getName().equals(name.getText())){
						setErrorMessage(Messages.CompositeDataStream_10);
						break;
					}
					else{
						setErrorMessage(null);
					}
				}
				
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
		
		Label lt = new Label(parent, SWT.NONE);
		lt.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lt.setText(Messages.CompositeDataStream_25); //$NON-NLS-1$
		
		customType = new ComboViewer(parent, SWT.READ_ONLY);
		customType.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		customType.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof Type) {
					return ((Type) element).getLabel();
				}
				return super.getText(element);
			}
			
		});
		
		customType.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
			public void dispose() {}
			
			public Object[] getElements(Object inputElement) {
				return ((List<Type>) inputElement).toArray();
			}
		});
		
		customType.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				notifyListeners(SWT.Selection, new Event());				
			}
		});
		
		Button btn = new Button(parent, SWT.PUSH);
		btn.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		btn.setText("..."); //$NON-NLS-1$
		btn.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {

				DialogCustomType dial = new DialogCustomType(getShell(), Messages.CompositeDataStream_13);
				if (dial.open() == Window.OK) {
					String lbl = dial.getNewCustomType();
					Type t = new Type();
					t.setLabel(lbl);
					Activator.getDefault().getCurrentModel().addType(t);
					customType.setInput(Activator.getDefault().getCurrentModel().getTypes());
					notifyListeners(SWT.Selection, new Event());
				}
				
			
				
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		customType.setInput(Activator.getDefault().getCurrentModel().getTypes());
		
		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 2));
		l2.setText(Messages.CompositeDataStream_17); //$NON-NLS-1$
		
		desc = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 2));
		desc.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
		
		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.CompositeDataStream_18); //$NON-NLS-1$
		
		outputLenght = new Text(parent, SWT.BORDER);
		outputLenght.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		outputLenght.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				try{
					Integer.parseInt(outputLenght.getText());
					setErrorMessage(null);
				}catch(Exception ex){
					setErrorMessage(Messages.CompositeDataStream_26);
				}
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
		
		Label l4 = new Label(parent, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.CompositeDataStream_19); //$NON-NLS-1$
		
		weight = new Text(parent, SWT.BORDER);
		weight.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		weight.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				try{
					Integer.parseInt(weight.getText());
					setErrorMessage(null);
				}catch(Exception ex){
					setErrorMessage(Messages.CompositeDataStream_27);
				}
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
		
		Label l5 = new Label(parent, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText(Messages.CompositeDataStream_20); //$NON-NLS-1$
		
		type = new Combo(parent, SWT.READ_ONLY);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		type.setItems(IDataStream.TYPES_NAMES);
		type.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		Label l6 = new Label(parent, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText(Messages.CompositeDataStream_21); //$NON-NLS-1$
		
		origin = new Text(parent, SWT.BORDER);
		origin.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,2,1));
		origin.setEnabled(false);
		
		if (data instanceof SQLDataStream){
			Label l7 = new Label(parent, SWT.NONE);
			l7.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			l7.setText(Messages.CompositeDataStream_22); //$NON-NLS-1$
			
			sqlType = new Text(parent, SWT.BORDER);
			sqlType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
			sqlType.setEnabled(false);
		}
		
		if (containApply){
			Composite bar = new Composite(this, SWT.NONE);
			bar.setLayout(new GridLayout(2, true));
			bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
			
			ok = new Button(bar, SWT.PUSH);
			ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			ok.setText(Messages.CompositeDataStream_23); //$NON-NLS-1$
			ok.setEnabled(false);
			ok.addSelectionListener(new SelectionAdapter(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					setData();
					if (viewer != null){
						viewer.refresh();
						Activator.getDefault().setChanged();
					}
					cancel.setEnabled(false);
					ok.setEnabled(false);
				}
				
			});
			
			cancel = new Button(bar, SWT.PUSH);
			cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			cancel.setText(Messages.CompositeDataStream_24); //$NON-NLS-1$-
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
	
	
	private void setErrorMessage(String message){
		if (message == null){
			errorLabel.setVisible(false);
		}
		else{
			errorLabel.setText(message);
			errorLabel.setVisible(true);
		}
		
	}
	
	private void fillData(){
		boolean error = false;
		
		if (data != null){
			isFilling = true;
			name.setText(data.getName());
			desc.setText(data.getDescription());
			outputLenght.setText(String.valueOf(data.getOutputLength()));
			weight.setText(String.valueOf(data.getWeight()));
			
			if (data.getOrigin() != null){
				origin.setText(data.getOrigin().getName());
			}
			else{
				origin.setText(data.getOriginName());
			}
			
			type.select(data.getType());
			if (sqlType != null){
				try{
					sqlType.setText(((SQLTable)data.getOrigin()).getSqlType());
				}catch(Exception ex){
					error = true;
					setErrorMessage(Messages.CompositeDataStream_28 + data.getOriginName() + Messages.CompositeDataStream_29);
				}
				
			}
			
			tableFilters.setInput(data.getFilters());
			tableGroups.setInput(getGroups());
			
			isFilling = false;
			
			if (!error){
				setErrorMessage(null);
			}
			
			if (data.getCustomType() != null) {
				customType.setSelection(new StructuredSelection(data.getCustomType()));
			}
		}
	}
	
	public void setData () {
		data.setName(name.getText());
		data.setDescription(desc.getText());
		data.setType(type.getSelectionIndex());
		try{
			data.setOutputLength(Integer.parseInt(outputLenght.getText()));
			
		}catch(NumberFormatException e){
			Activator.getLogger().error(e.getMessage(), e);
			outputLenght.selectAll();
			return;
		}
		
		try{
			data.setWeight(Integer.parseInt(weight.getText()));
			
		}catch(NumberFormatException e){
			Activator.getLogger().error(e.getMessage(), e);
			weight.selectAll();
			return;
		}
		
		ISelection selection = customType.getSelection();
		if (!selection.isEmpty()) {
			Object o = ((StructuredSelection) selection).getFirstElement();
			data.setCustomType((Type) o);
		}
		
		IStructuredSelection ss = (IStructuredSelection)tableFilters.getSelection();
		if (ss.isEmpty()){
			return;
		}
		fillData();
	}			
	
	private List<Secu> getGroups(){

		
		List<Secu> l = new ArrayList<Secu>();
		for(String o : GroupHelper.getGroups(0, 0)){ 
			Secu s = new Secu();
			s.visible = true;
			s.groupName = o; 
			l.add(s);
		}
		return l;
	}

	private class Secu{
		public String groupName;
		Boolean visible;
	}
	
	private boolean isFilled() {
		return !errorLabel.isVisible();
	}
	
	
}
