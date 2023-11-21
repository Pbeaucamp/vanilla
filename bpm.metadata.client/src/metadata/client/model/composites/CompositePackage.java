package metadata.client.model.composites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.viewer.TableLabelProvider;
import metadataclient.Activator;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.UnitedOlapBusinessPackage;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.olap.UnitedOlapDatasource;
import bpm.metadata.resource.IResource;

public class CompositePackage extends Composite {
	private static final Color RED = new Color(Display.getDefault(), 255, 0, 0);
	
	public class Element{
		Integer pos;
		IBusinessTable table;
	}
	
	private Button checkAll, uncheckAll;
	
	private TableViewer orderViewer;
	
	private HashMap<IBusinessTable, Integer> tableOrder = new HashMap<IBusinessTable, Integer>();
	
	private CheckboxTableViewer viewer;
	private Text name;
	private Text description;
	
	private BusinessModel model;
	private IBusinessPackage busPack;
	
	private Label errorLabel;
	
	public CompositePackage(Composite parent, int style, BusinessModel model, IBusinessPackage pack) {
		super(parent, style);
		this.model = model;
		this.busPack = pack;
		
		this.setLayout(new GridLayout());
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		errorLabel = new Label(this, SWT.NONE);
    	errorLabel.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 2, 1));
    	errorLabel.setForeground(RED);
    	errorLabel.setVisible(false);
		
		TabItem itemGal = new TabItem(tabFolder, SWT.NONE, 0);
		itemGal.setText(Messages.CompositePackage_3); //$NON-NLS-1$
		itemGal.setControl(buildContent(tabFolder));

		
		TabItem tableOrder = new TabItem(tabFolder, SWT.NONE, 0);
		tableOrder.setText(Messages.CompositePackage_4);  //$NON-NLS-1$
		tableOrder.setControl(buildOrder(tabFolder));
		
		
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
				
				
				Element el = (Element)((IStructuredSelection)orderViewer.getSelection()).getFirstElement();
				

				for(IBusinessTable t : tableOrder.keySet()){
					if (tableOrder.get(t).equals(el.pos - 1)){
						tableOrder.put(t, el.pos);
						tableOrder.put(el.table, el.pos - 1);
						orderViewer.refresh();
						orderViewer.setSelection(new StructuredSelection(el));
						
						int i = 0;
						for(TableItem it : orderViewer.getTable().getItems()){
							if (((Element)it.getData()).table == el.table){
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
				
				
				Element el = (Element)((IStructuredSelection)orderViewer.getSelection()).getFirstElement();
				

				for(IBusinessTable t : tableOrder.keySet()){
					if (tableOrder.get(t).equals(el.pos + 1)){
						tableOrder.put(t, el.pos);
						tableOrder.put(el.table, el.pos + 1);
						orderViewer.refresh();
						
						
						int i = 0;
						for(TableItem it : orderViewer.getTable().getItems()){
							if (((Element)it.getData()).table == el.table){
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
				HashMap<IBusinessTable, Integer> in = (HashMap<IBusinessTable, Integer>)inputElement;
				
				List<Element> l = new ArrayList<Element>();
				for(IBusinessTable t : in.keySet()){
					Element e = new Element();
					e.pos = in.get(t);
					e.table = t;
					l.add(e);
				}
				
				return l.toArray(new Element[l.size()]);
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
					return ((Element)element).pos + ""; //$NON-NLS-1$
				}
				else{
					return ((Element)element).table.getName();
				}

			}
			
		});
		orderViewer.setSorter(new ViewerSorter(){

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return ((Element)e1).pos.compareTo(((Element)e2).pos);
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
	
	
	private Control buildContent(TabFolder folder){
		Composite parent = new Composite(folder, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(Messages.CompositePackage_0); //$NON-NLS-1$
		
		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				
				for(IBusinessPackage p: ((BusinessModel)model).getBusinessPackages("none")){ //$NON-NLS-1$
					if ( p != null && p != busPack && p.getName().equals(name.getText())){
						setErrorMessage(Messages.CompositePackage_8);
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
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l2.setText(Messages.CompositePackage_1); //$NON-NLS-1$
		
		description = new Text(parent, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Composite bar = new Composite(parent, SWT.NONE);
		bar.setLayout(new GridLayout(2, false));
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		checkAll = new Button(bar, SWT.PUSH);
		checkAll.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		checkAll.setText(Messages.CompositePackage_9);
		checkAll.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(true);
				
				
				for(IBusinessTable t : model.getBusinessTables()){
					tableOrder.put(t, tableOrder.size());
				}
				rebuildOrder();
			}
			
		});
		
		uncheckAll = new Button(bar, SWT.PUSH);
		uncheckAll.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		uncheckAll.setText(Messages.CompositePackage_12);
		uncheckAll.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(false);
				tableOrder.clear();
				
				rebuildOrder();
			}
		});
		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l3.setText(Messages.CompositePackage_2); //$NON-NLS-1$
		
		viewer = new CheckboxTableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof IBusinessTable){
					return ((IBusinessTable)element).getName();
				}
				else if (element instanceof IResource){
					return ((IResource)element).getName();
				}
				 return null;
			}
			
		});
		viewer.setContentProvider(new IStructuredContentProvider(){


			public Object[] getElements(Object inputElement) {
				List<Object> l = (List<Object>)inputElement;
				return l.toArray(new Object[l.size()]);
			}


			public void dispose() {
				
			}


			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		viewer.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				
				if (!(event.getElement() instanceof IBusinessTable)){
					return;
				}
				if (event.getChecked()){
					tableOrder.put((IBusinessTable)event.getElement(), tableOrder.size());
				}
				else{
					Integer i = tableOrder.get((IBusinessTable)event.getElement());
					
					for(IBusinessTable t : tableOrder.keySet()){
						if (tableOrder.get(t) > i){
							tableOrder.put(t, tableOrder.get(t) - 1);
						}
					}
					
					
					
					tableOrder.remove((IBusinessTable)event.getElement());
					
				}
				rebuildOrder();
			}
			
		});
		
		viewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

				//set the viewer input
		List<Object> list = new ArrayList<Object>();
		for(IBusinessTable t : model.getBusinessTables()){
			list.add(t);
		}
		for(IResource r : model.getResources()){
			list.add(r);
		}
		viewer.setInput(list);
		
		return parent;
	}
	
	
	private void rebuildOrder(){
		orderViewer.setInput(tableOrder);
		
	}
	
	private void fillData(){
		if (busPack != null){
			name.setText(busPack.getName());
			description.setText(busPack.getDescription());
			
			for(Object o : (List<Object>)viewer.getInput()){
				if (o instanceof AbstractBusinessTable && busPack.getBusinessTables("none").contains(o)){ //$NON-NLS-1$
						viewer.setChecked(o, true);
				}
				else if (o instanceof IResource && busPack.getResources().contains(o)){
					viewer.setChecked(o, true);
				}
					
			}
			
			
		}
	}
	
	public void setPackageDatas(){
		if (busPack == null){
			
			boolean onOlap = false;
			for(AbstractDataSource ds : model.getModel().getDataSources()) {
				if(ds instanceof UnitedOlapDatasource) {
					onOlap = true;
				}
				break;
			}
			
			if(onOlap) {
				busPack = new UnitedOlapBusinessPackage();
			}
			
			else {
				busPack = new BusinessPackage();
			}
		}
		busPack.setName(name.getText());
		busPack.setDescription(description.getText());
		
		busPack.getBusinessTables("none").clear(); //$NON-NLS-1$
		busPack.getResources().clear();
		
		for(Object o : viewer.getCheckedElements()){
			if ( o instanceof IResource){
				busPack.addResource((IResource)o);
			}
			else if ( o instanceof IBusinessTable){
				busPack.addBusinessTable((IBusinessTable)o);
			}
		}
		
		for(IBusinessTable t : tableOrder.keySet()){
			busPack.order(t.getName(), tableOrder.get(t));
		}
		
		
	}

	public IBusinessPackage getBusinessPackage(){
		return busPack;
	}


	public boolean isFilled() {
		return !errorLabel.isVisible();
	}
}
