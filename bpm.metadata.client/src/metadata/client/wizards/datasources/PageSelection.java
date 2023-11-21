package metadata.client.wizards.datasources;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.model.dialog.DialogFormula;
import metadata.client.model.dialog.DialogPhysicalTable;
import metadata.client.model.dialog.fields.DialogFieldsValues;
import metadata.client.trees.TreeColumn;
import metadata.client.trees.TreeDataStream;
import metadata.client.trees.TreeFormula;
import metadata.client.trees.TreeObject;
import metadata.client.trees.TreeParent;
import metadata.client.trees.TreeTable;
import metadata.client.viewer.TreeContentProvider;
import metadata.client.viewer.TreeLabelProvider;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.ITable;
import bpm.metadata.layer.physical.sql.SQLTable;

public class PageSelection extends WizardPage {

	private TreeViewer logical;
	private CheckboxTreeViewer physical;
	private Button formula;
	private AbstractDataSource dataSource;
	
	protected PageSelection(String pageName) {
		super(pageName);
	}


	public void createControl(Composite parent) {
		//create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(true);


	}
	
	private void createPageContent(Composite parent){
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ToolBar toolbar = new ToolBar(container, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		fillToolbar(toolbar);
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l.setText(Messages.PageSelection_0); //$NON-NLS-1$
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l2.setText(Messages.PageSelection_1); //$NON-NLS-1$

		
		physical = new CheckboxTreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
		physical.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));
		physical.setContentProvider(new TreeContentProvider());
		physical.setLabelProvider(new TreeLabelProvider());
		physical.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				final IStructuredSelection ss =  (IStructuredSelection)physical.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeTable)){
					return;
				}
				
				BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
					public void run(){
						for(Object o : ss.toList()){
							if (!(o instanceof TreeTable)){
								continue;
							}
							TreeTable tt = (TreeTable)o;
							
							
							
							if (tt.getTable() instanceof SQLTable){
								SQLTable t = (SQLTable)tt.getTable();
								
								if (!t.hasColumns()){
									for(IColumn c : t.getColumns()){
										TreeColumn tc = new TreeColumn(c);
										tt.addChild(tc);
									}
								}
								
							}
						}
						
						
						
						physical.refresh();
					}
				});
				
				
			}
			
		});
		physical.addCheckStateListener(new ICheckStateListener() {
			
			public void checkStateChanged(CheckStateChangedEvent event) {
				
					
				if (event.getElement() instanceof TreeTable && event.getChecked()){
					
					if (((TreeTable)event.getElement()).getChildren().isEmpty()){
						physical.setSelection(new StructuredSelection(event.getElement()), true);
					}
					
					for(Object o : ((TreeTable)event.getElement()).getChildren()){
						physical.setChecked(o, true);	
					}
					
				}
				else if(event.getElement() instanceof TreeColumn && event.getChecked())  {
					physical.setChecked(((TreeColumn)event.getElement()).getParent(), true);
				}
				else if (event.getElement() instanceof TreeTable && !event.getChecked()){
					for(Object o : ((TreeTable)event.getElement()).getChildren()){
						physical.setChecked(o, false);	
					}
				}
				else if (event.getElement() instanceof TreeParent && event.getChecked()){
					TreeParent p = (TreeParent)event.getElement();
					for(Object o : p.getChildren()){
						physical.setChecked(o, true);
						
						if (o instanceof TreeParent){
							
							final Object _O = o;	
							BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
								public void run(){
									TreeTable tt = (TreeTable)_O;
									if (tt.getTable() instanceof SQLTable){
										SQLTable t = (SQLTable)tt.getTable();
										
										if (!t.hasColumns()){
											for(IColumn c : t.getColumns()){
												TreeColumn tc = new TreeColumn(c);
												tt.addChild(tc);
											}
										}
									}
	
									physical.refresh();
								}
							});
							
							for(Object _o : ((TreeParent)o).getChildren()){
								physical.setChecked(_o, true);
							}
						}
					}
				}
				
			}
		});
		
		Composite buttons = new Composite(container, SWT.NONE);
		buttons.setLayout(new GridLayout(1, false));
		buttons.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));
		
		
				
		logical = new TreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI| SWT.VIRTUAL);
		logical.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));
		logical.setContentProvider(new TreeContentProvider());
		logical.setLabelProvider(new TreeLabelProvider());
		logical.addSelectionChangedListener(new ISelectionChangedListener(){


			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)logical.getSelection();
				
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeDataStream)){
					return;
				}
				else{
					formula.setEnabled(true);
				}
				
				
			}
			
		});
		
		
		
		createLogicalModel();
		
		
		
		//fill Buttons
		
		Button add = new Button(buttons, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		add.setText(Messages.PageSelection_2); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
			
				
				for(Object o : physical.getCheckedElements()){
					if(o instanceof TreeTable){
						boolean empty = true;
						
						for(TreeObject _o : ((TreeTable)o).getChildren()){
							if (physical.getChecked(_o)){
								empty = false;
								break;
							}
						}
						if (empty){
							MessageDialog.openInformation(getShell(), Messages.PageSelection_9, Messages.PageSelection_10);
							return;
						}
						
						IDataStream s = dataSource.add(((TreeTable)o).getTable());
						
						for(TreeObject _c : ((TreeTable)o).getChildren()){
							if (!physical.getChecked(_c)){
								s.removeDataStreamElement(s.getElementNamed(((TreeColumn)_c).getColumn().getName()));
							}
						}
						
						s.setType(IDataStream.UNDEFINED);
						if (s != null){
							TreeDataStream tds = new TreeDataStream(s);
							((TreeParent)logical.getInput()).addChild(tds);
							logical.refresh();
						}
					}
				}
				getContainer().updateButtons();
			}
			
		});
		
		Button addFact = new Button(buttons, SWT.PUSH);
		addFact.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		addFact.setText(Messages.PageSelection_3); //$NON-NLS-1$
		addFact.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
			
				
				for(Object o : physical.getCheckedElements()){
					if(o instanceof TreeTable){
						
						
						boolean empty = true;
						
						for(TreeObject _o : ((TreeTable)o).getChildren()){
							if (physical.getChecked(_o)){
								empty = false;
								break;
							}
						}
						if (empty){
							MessageDialog.openInformation(getShell(), Messages.PageSelection_12, Messages.PageSelection_13);
							return;
						}
						IDataStream s = dataSource.add(((TreeTable)o).getTable());
						s.setType(IDataStream.FACT_TABLE);
						if (s != null){
							TreeDataStream tds = new TreeDataStream(s);
							((TreeParent)logical.getInput()).addChild(tds);
							logical.refresh();
						}
					}
				}
				getContainer().updateButtons();
			}
			
		});
		
		
		Button addDim = new Button(buttons, SWT.PUSH);
		addDim.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		addDim.setText(Messages.PageSelection_4); //$NON-NLS-1$
		addDim.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
			
				
				for(Object o : physical.getCheckedElements()){
					if(o instanceof TreeTable){
						
						boolean empty = true;
						
						for(TreeObject _o : ((TreeTable)o).getChildren()){
							if (physical.getChecked(_o)){
								empty = false;
								break;
							}
						}
						if (empty){
							MessageDialog.openInformation(getShell(), Messages.PageSelection_14, Messages.PageSelection_15);
							return;
						}
						
						IDataStream s = dataSource.add(((TreeTable)o).getTable());
						s.setType(IDataStream.DIMENSION_TABLE);
						if (s != null){
							TreeDataStream tds = new TreeDataStream(s);
							((TreeParent)logical.getInput()).addChild(tds);
							logical.refresh();
						}
					}
				}
				getContainer().updateButtons();
			}
			
		});
		

		
		Button addAll = new Button(buttons, SWT.PUSH);
		addAll.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		addAll.setText(Messages.PageSelection_5); //$NON-NLS-1$
		addAll.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(TreeObject o : ((TreeParent)physical.getInput()).getChildren()){
					if(o instanceof TreeTable){
						IDataStream s = dataSource.add(((TreeTable)o).getTable());
						s.setType(IDataStream.UNDEFINED);
						if (s != null){
							TreeDataStream tds = new TreeDataStream(s);
							((TreeParent)logical.getInput()).addChild(tds);
							
						}
					}
				}
				logical.refresh();
				getContainer().updateButtons();
			}
		});
		
		Button del = new Button(buttons, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		del.setText(Messages.PageSelection_6); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Iterator it = ((IStructuredSelection)logical.getSelection()).iterator();
				while(it.hasNext() ){
					Object o = it.next();
					if(o instanceof TreeDataStream){
						dataSource.remove(((TreeDataStream)o).getDataStream());
						((TreeParent)logical.getInput()).removeChild((TreeObject)o);
						
					}
				}
				
				logical.refresh();
				getContainer().updateButtons();
			}
			
		});
		
		Button delAll = new Button(buttons, SWT.PUSH);
		delAll.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		delAll.setText(Messages.PageSelection_7); //$NON-NLS-1$
		delAll.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				for(TreeObject o : ((TreeParent)logical.getInput()).getChildren()){
					dataSource.remove(((TreeDataStream)o).getDataStream());
					
				}
				((TreeParent)logical.getInput()).removeAll();
				logical.refresh();
				getContainer().updateButtons();
			}
			
		});
	
	
		formula = new Button(buttons, SWT.PUSH);
		formula.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		formula.setText(Messages.PageSelection_8); //$NON-NLS-1$
		formula.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				//get the logical table selected
				IStructuredSelection ss = (IStructuredSelection)logical.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeDataStream)){
					return;
				}
				
				TreeDataStream tds = (TreeDataStream)ss.getFirstElement();
				
				//get the tables from the tree
				List<IDataStream> list = new ArrayList<IDataStream>();
				TreeParent root = (TreeParent) logical.getInput();
				
				list.add(tds.getDataStream());
				
//				for(TreeObject o : root.getChildren()){
//					if (o instanceof TreeDataStream){
//						list.add(((TreeDataStream)o).getDataStream().getOrigin());
//					}
//				}
				
				
				DialogFormula dial = new DialogFormula(getShell(),list);
				if (dial.open() == Window.OK){
					TreeFormula tf = new TreeFormula(dial.getFormula());
					tds.addChild(tf);
					tds.getDataStream().addCalculatedElement(tf.getFormula());
					logical.refresh();
				}
				getContainer().updateButtons();
			}
			
		});
		formula.setEnabled(false);
	}
	
	protected void createModel(AbstractDataSource datasource) throws Exception{
		this.dataSource = datasource;
		
		List<IDataStream> toRemove = new ArrayList<IDataStream>();
		for(IDataStream ds :  datasource.getDataStreams()){
			
			try {
				if (datasource.getConnection().getTable(ds.getOrigin().getName()) == null){
					toRemove.add(ds);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		for(IDataStream ds : toRemove){
			this.dataSource.remove(ds);
		}
		createLogicalModel();
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		for(ITable t : datasource.getConnection().connect()){
			if (t instanceof SQLTable && t.getName().contains(".")){ //$NON-NLS-1$
				String shName = t.getName().substring(0, t.getName().indexOf(".")); //$NON-NLS-1$
				TreeObject found = null;
				
				for(TreeObject c : root.getChildren()){
					if (c.getName().equals(shName)){
						found = c;
						break;
					}
				}
				
				if (found == null){
					TreeParent tsch = new TreeParent(shName);
					root.addChild(tsch);
					
					TreeTable tt = new TreeTable(t);
					tsch.addChild(tt);
				}
				else{
					TreeTable tt = new TreeTable(t);
					((TreeParent)found).addChild(tt);
				}
				
				
				
			}
			else{
				TreeTable tt = new TreeTable(t);
				root.addChild(tt);
			}
			
			
		}
		
		physical.setInput(root);		
	}
	
	private void createLogicalModel(){
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		logical.setInput(root);
		
		if (this.dataSource != null){
			for(IDataStream ds : this.dataSource.getDataStreams()){
				TreeDataStream tds = new TreeDataStream(ds);
				root.addChild(tds);
				
			
			}
		}
		logical.refresh();
	}

	@Override
	public IWizardPage getNextPage() {
		if (getWizard() instanceof DataSourceWizard){
			((DataSourceWizard)getWizard()).propertiesPage.createModel((TreeParent)logical.getInput());
		}
		
		return super.getNextPage();
	}

	
	private void fillToolbar(ToolBar bar){
		
		ToolItem check = new ToolItem(bar, SWT.PUSH);
		check.setToolTipText(Messages.PageSelection_16);
		check.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeParent root = (TreeParent)physical.getInput();
				
				List<Object> toCheck = new ArrayList<Object>();
				
				for(TreeObject o : root.getChildren()){
					toCheck.add(o);
					physical.setSelection(new StructuredSelection(o));
					if (o instanceof TreeParent){
						
						for(TreeObject oo : ((TreeParent)o).getChildren()){
							toCheck.add(oo);
							if (oo instanceof TreeParent){
								physical.setSelection(new StructuredSelection(oo));
								toCheck.addAll(((TreeParent)oo).getChildren());
							}
							
						}
					}
					
						
				}
					
				
				physical.setSelection(new StructuredSelection(toCheck));
				
				
				
				physical.setCheckedElements(toCheck.toArray(new Object[toCheck.size()]));
			}
		});
		check.setImage(Activator.getDefault().getImageRegistry().get("check")); //$NON-NLS-1$
		
		ToolItem uncheck = new ToolItem(bar, SWT.PUSH);
		uncheck.setToolTipText(Messages.PageSelection_18);
		uncheck.setImage(Activator.getDefault().getImageRegistry().get("uncheck")); //$NON-NLS-1$
		uncheck.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				physical.setCheckedElements(new Object[]{});
			}
		});

		
		ToolItem browse = new ToolItem(bar, SWT.PUSH);
		browse.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)physical.getSelection();
				
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeTable)){
					return;
				}
				
				ITable table = ((TreeTable)ss.getFirstElement()).getTable();

				DialogPhysicalTable dial = new DialogPhysicalTable(getShell(), table);
				dial.open();
				
			}
			
		});
		browse.setToolTipText(Messages.PageSelection_11); //$NON-NLS-1$
		browse.setImage(Activator.getDefault().getImageRegistry().get("browse")); //$NON-NLS-1$
		
		
		ToolItem browseCol = new ToolItem(bar, SWT.PUSH);
		browseCol.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)physical.getSelection();
				
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeTable)){
					return;
				}
				
				ITable table = ((TreeTable)ss.getFirstElement()).getTable();
				
				
				DialogFieldsValues d = new DialogFieldsValues(getShell(),table);
				d.open();
				
			}
			
		});
		browseCol.setToolTipText(Messages.PageSelection_11); //$NON-NLS-1$
		browseCol.setImage(Activator.getDefault().getImageRegistry().get("browse_col")); //$NON-NLS-1$

	}


	@Override
	public boolean isPageComplete() {
		if (dataSource != null || !logical.getSelection().isEmpty()){
			return true;
		}
		return false;
	}

}
