package metadata.client.wizards.business.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.trees.TreeConnection;
import metadata.client.trees.TreeDataSource;
import metadata.client.trees.TreeDataStream;
import metadata.client.trees.TreeDataStreamElement;
import metadata.client.trees.TreeObject;
import metadata.client.trees.TreeParent;
import metadata.client.viewer.TreeContentProvider;
import metadata.client.viewer.TreeLabelProvider;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.UnitedOlapBusinessTable;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.olap.UnitedOlapDataStream;
import bpm.metadata.layer.logical.olap.UnitedOlapDataStreamElement;
import bpm.metadata.layer.logical.olap.UnitedOlapDatasource;
import bpm.metadata.layer.logical.sql.SQLDataStream;
import bpm.metadata.layer.physical.olap.UnitedOlapLevelColumn;

public class PageTable extends WizardPage {

	private TreeViewer logicalTables;
	private ListViewer columns;
	private Text name;
	protected List<Object> elements = new ArrayList<Object>();

	protected boolean nameModified = false;
	
	protected boolean isOlap = false;
	private UnitedOlapDatasource olapDs;
	
	private Button add, addAll;
	
	protected PageTable(String pageName) {
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
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.PageTable_0); //$NON-NLS-1$
		
		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		name.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				nameModified = true;
				
			}
			
		});
		
		//the datasources nodes
		logicalTables = new TreeViewer(container, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		logicalTables.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		logicalTables.setContentProvider(new TreeContentProvider());
		logicalTables.setLabelProvider(new TreeLabelProvider());
		logicalTables.addDoubleClickListener(new IDoubleClickListener(){

			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection ss = (IStructuredSelection)logicalTables.getSelection();
				if (ss.isEmpty()){
					return;
				}

				if (ss.getFirstElement() instanceof TreeDataSource && ((TreeDataSource)ss.getFirstElement()).getDataSource() instanceof UnitedOlapDatasource){
					
					if (!elements.isEmpty()){
						if (MessageDialog.openQuestion(getShell(), Messages.PageTable_1, Messages.PageTable_2)){ //$NON-NLS-1$ //$NON-NLS-2$
							olapDs = (UnitedOlapDatasource)((TreeDataSource)ss.getFirstElement()).getDataSource();
							isOlap = true;
							elements.clear();
							elements.add(olapDs);
						}
					}
					else{
						olapDs = (UnitedOlapDatasource)((TreeDataSource)ss.getFirstElement()).getDataSource();
						isOlap = true;
						elements.clear();
						elements.add(olapDs);
					}
					
				}
				else if (ss.getFirstElement() instanceof TreeDataStream && ((TreeDataStream)ss.getFirstElement()).getDataStream() instanceof SQLDataStream){
					TreeDataStream tds = (TreeDataStream)ss.getFirstElement();
					for(IDataStreamElement e : tds.getDataStream().getElements()){
						elements.add(e);
					}
					
					if (!nameModified){
						name.setText(tds.getName());
					}
				}
				
				else{
					for(Object o : ss.toList()){
						if(o instanceof TreeDataStreamElement && !(((TreeDataStreamElement)o).getDataStreamElement() instanceof UnitedOlapDataStreamElement)){
							elements.add(((TreeDataStreamElement)o).getDataStreamElement());
						}
					}
				}
				
				
				
				columns.refresh();
				
			}
			
		});
		logicalTables.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)logicalTables.getSelection();
				
				if (ss.getFirstElement() instanceof TreeDataSource && ((TreeDataSource)ss.getFirstElement()).getDataSource() instanceof UnitedOlapDatasource){
					addAll.setEnabled(false);
					add.setEnabled(true);
				}
				else{
					addAll.setEnabled(true);
					add.setEnabled(false);
				}
				
				if (ss.getFirstElement() instanceof TreeConnection){
					addAll.setEnabled(false);
					add.setEnabled(false);
				}
				
				if(ss.getFirstElement() instanceof TreeDataStream){
					if (((TreeDataStream)ss.getFirstElement()).getDataStream() instanceof UnitedOlapDataStream){
						addAll.setEnabled(false);
						add.setEnabled(true);
					}
					else{
						addAll.setEnabled(true);
						add.setEnabled(true);
					}
				}
				
				if (ss.getFirstElement() instanceof TreeDataStreamElement){
					if (((TreeDataStreamElement)ss.getFirstElement()).getDataStreamElement() instanceof UnitedOlapDataStreamElement){
						addAll.setEnabled(false);
						add.setEnabled(true);
					}
					else{
						addAll.setEnabled(true);
						add.setEnabled(true);
					}
				}
				
				
			}
			
		});
		logicalTables.setSorter(new ViewerSorter(){
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				
				if (e1.getClass() != e2.getClass()){
					if (e1 instanceof TreeConnection){
						return -1;
					}
					else if (e2 instanceof TreeConnection){
						return 1;
					}
				}
				
				return super.compare(viewer, e1, e2);
			}
		});
		
		Composite bar = new Composite(container, SWT.NONE);
		bar.setLayout(new GridLayout());
		bar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		columns = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		columns.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		columns.setContentProvider(new ContentProvider());
		columns.setLabelProvider(new MyLabelProvider());
		columns.setSorter(new ViewerSorter());
		
		//create the buttons for bar
		add = new Button(bar, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		add.setText(Messages.PageTable_3); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)logicalTables.getSelection();
				if (ss.isEmpty()){
					return;
				}

				
				for(Object o : ss.toList()){
					if (o instanceof TreeDataSource && ((TreeDataSource)o).getDataSource() instanceof UnitedOlapDatasource){
						
						if (!elements.isEmpty()){
							if (MessageDialog.openQuestion(getShell(), Messages.PageTable_4, Messages.PageTable_5)){ //$NON-NLS-1$ //$NON-NLS-2$
								olapDs = (UnitedOlapDatasource)((TreeDataSource)o).getDataSource();
								isOlap = true;
								elements.clear();
								elements.add(olapDs);
							}
						}
						else{
							olapDs = (UnitedOlapDatasource)((TreeDataSource)o).getDataSource();
							isOlap = true;
							elements.clear();
							elements.add(olapDs);
						}
						
					}
					else if (ss.getFirstElement() instanceof TreeDataStream && ((TreeDataStream)ss.getFirstElement()).getDataStream() instanceof SQLDataStream){
						TreeDataStream tds = (TreeDataStream)ss.getFirstElement();
						for(IDataStreamElement el : tds.getDataStream().getElements()){
							elements.add(el);
						}
						
						if (!nameModified){
							name.setText(tds.getName());
						}
					}
					else if (ss.getFirstElement() instanceof TreeDataStream && ((TreeDataStream)ss.getFirstElement()).getDataStream() instanceof UnitedOlapDataStream){
						isOlap = true;
						TreeDataStream tds = (TreeDataStream)ss.getFirstElement();
						for(IDataStreamElement el : tds.getDataStream().getElements()){
							elements.add(el);
						}
						
						if (!nameModified){
							name.setText(tds.getName());
						}
					}
					
					else{
						if(o instanceof TreeDataStreamElement && (((TreeDataStreamElement)o).getDataStreamElement() instanceof UnitedOlapDataStreamElement)){
							isOlap = true;
						}
						elements.add(((TreeDataStreamElement)o).getDataStreamElement());
					}
				}
				
				
				
				
				
				columns.refresh();
			}
			
		});
		
		addAll = new Button(bar, SWT.PUSH);
		addAll.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		addAll.setText(Messages.PageTable_6); //$NON-NLS-1$
		addAll.addSelectionListener(new SelectionAdapter(){


			public void widgetSelected(SelectionEvent e) {
				
				for(Object ds : (List)((TreeParent)logicalTables.getInput()).getChildren()){
					for(Object t : ((TreeDataSource)ds).getDataSource().getDataStreams()){
						for(Object o : ((IDataStream)t).getElements()){
							//if(o instanceof TreeDataStreamElement){
								elements.add((IDataStreamElement)o);
							//}
						}
					}
				}
					
				
				columns.refresh();
			}
			
		});
		
		
		Button del = new Button(bar, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		del.setText(Messages.PageTable_7); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)columns.getSelection();
				for(Object o : ss.toList()){
					elements.remove(o);
				}
				columns.refresh();
			}
			
		});
		
		Button delAll = new Button(bar, SWT.PUSH);
		delAll.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		delAll.setText(Messages.PageTable_8); //$NON-NLS-1$
		delAll.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				elements.clear();
				columns.refresh();

			}
			
		});
		
		buildModel();
	}
	
	
	private void buildModel(){
		Collection<AbstractDataSource> list = Activator.getDefault().getModel().getDataSources();
		
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		for(AbstractDataSource d : list){
			root.addChild(new TreeDataSource(d, false));
		}
		
		fillData();
		nameModified = false;
		columns.setInput(elements);
		logicalTables.setInput(root);
		//setLoD();
	}
	
	protected void fillData(){
		IBusinessTable bt = ((BusinessTableWizard)getWizard()).getBusinessTable();
		if (bt != null){
			for(IDataStreamElement e : bt.getColumns("none")){ //$NON-NLS-1$
				elements.add(e);
			}
			name.setText(bt.getName());
			isOlap = bt instanceof UnitedOlapBusinessTable;
		}
		
	}

	
	private class ContentProvider implements IStructuredContentProvider{


		public Object[] getElements(Object inputElement) {
			List<Object> l = (List<Object>)inputElement;
			
			return l.toArray(new Object[l.size()]);
		}


		public void dispose() {
			
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
			
		}
		
	}
	
	private class MyLabelProvider extends LabelProvider{


		public String getText(Object element) {
			if (element instanceof IDataStreamElement){
				IDataStreamElement c = (IDataStreamElement)element;
				if (c != null)
					return c.getName();
				else
					return null;
			}
			else if (element instanceof UnitedOlapDatasource){
				return ((UnitedOlapDatasource)element).getName();
			}
			return null;
		}
		
	}
	
	protected String getTableName(){
		return name.getText();
	}
	
	
	private void setLoD(){
		logicalTables.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)logicalTables.getSelection();
				
				if (ss.isEmpty()){
					return;
				}
				
				if (ss.getFirstElement() instanceof TreeDataStreamElement){
					TreeDataStreamElement tc = (TreeDataStreamElement)ss.getFirstElement();
					
					if (tc.getDataStreamElement() instanceof UnitedOlapDataStreamElement 
							&&
						tc.getDataStreamElement().getOrigin() instanceof UnitedOlapLevelColumn){
						
//						MemberOlap mb = (MemberOlap)tc.getDataStreamElement().getOrigin();
//						//find the cube
//						TreeDataSource c = findCube(tc);
//						if ( c == null){
//							return;
//						}
//						((OlapConnection)((OLAPDataSource)c.getDataSource()).getConnection()).addChilds(mb);
//						
//						for(MemberOlap mm : mb.getChilds()){
//							OLAPDataStreamElement el = new OLAPDataStreamElement(mm);
//							
//							((OLAPDataStreamElement)tc.getDataStreamElement()).addChild(el);
//							//el.setDataStream(((OLAPDataStreamElement)tc.getDataStreamElement()).getDataStream());
//							((OLAPDataStreamElement)tc.getDataStreamElement()).getDataStream().addColumn(el);
//			
//							//el.setDataStream(((OLAPDataStreamElement)tc.getDataStreamElement()).getDataStream());
//							tc.addChild(new TreeDataStreamElement(el));
//						}
						
						logicalTables.refresh();
					}
					
					
				}
				
			}
			
		});
	}
	private TreeDataSource findCube(TreeObject to){
		if (to.getParent() instanceof TreeDataSource &&
			((TreeDataSource)to.getParent()).getDataSource() instanceof UnitedOlapDatasource){
			return (TreeDataSource)to.getParent();
		}
		else if (to.getParent() != null){
			return findCube(to.getParent());
		}
		
		return null;
	}
}
