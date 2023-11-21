package bpm.gateway.ui.dialogs.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.ui.i18n.Messages;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.logical.RelationException;

public class CompositeRelation extends Composite {

	private class DataStreamLabelProvider extends LabelProvider{
		@Override
		public String getText(Object element) {
			return ((IDataStream)element).getName();
		}
	}
	private class ContentProvider implements IStructuredContentProvider{

		public Object[] getElements(Object inputElement) {
			Collection c = (Collection)inputElement;
			return c.toArray(new Object[c.size()]);
		}

		public void dispose() {
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
		}
		
	}
	
	private ListViewer leftCols, rightCols;
	private ComboViewer leftTable, rightTable;
	private ListViewer joinsViewer;
	private Button leftOuter, rightOuter;
	
	private Text txtQuery;
	
	private IDataSource dataSource;
	private Relation relation;
	
	private Label labelRelation;
	
//	private Button c0n, c01,c1n, c11;
	
	
	private Viewer v;
	private boolean applyChange;
	
	public CompositeRelation(Composite parent, int style, Viewer v, boolean applyChange, IDataSource dataSource, Relation relation) {
		super(parent, style);
		this.dataSource = dataSource;
		this.relation = relation;
		this.v = v;
		this.applyChange = applyChange;
		buildContent();
	}
	
	
	
	
	public CompositeRelation(Composite parent, int style, Viewer v, boolean applyChange, Relation relation) {
		super(parent, style);
		this.dataSource = relation.getDataSource();
		this.relation = relation;
		this.v = v;
		this.applyChange = applyChange;
		buildContent();
		
	}
	
	private void buildContent(){
		this.setLayout(new GridLayout(2, true));
		
		if ( applyChange){
			final Button b1 = new Button(this, SWT.CHECK);
			b1.setText(Messages.CompositeRelation_0);
			b1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			b1.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					leftCols.setSelection(StructuredSelection.EMPTY);
					leftTable.setSelection(StructuredSelection.EMPTY);
					if (b1.getSelection()){
						leftTable.addFilter(new RelationViewerFilter());
					}
					else{
						leftTable.setFilters(new ViewerFilter[]{});
					}
					leftTable.refresh();
				}
			});
			
			final Button b2 = new Button(this, SWT.CHECK);
			b2.setText(Messages.CompositeRelation_1);
			b2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			b2.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					rightCols.setSelection(StructuredSelection.EMPTY);
					rightTable.setSelection(StructuredSelection.EMPTY);
					if (b2.getSelection()){
						rightTable.addFilter(new RelationViewerFilter());
					}
					else{
						rightTable.setFilters(new ViewerFilter[]{});
					}
					rightTable.refresh();
				}
			});
			
		}
		leftTable = new ComboViewer(this, SWT.READ_ONLY);
		leftTable.getCombo().setVisibleItemCount(15);
		leftTable.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		leftTable.setContentProvider(new ContentProvider());
		leftTable.setLabelProvider(new DataStreamLabelProvider());
		leftTable.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()){
					leftCols.setInput(new ArrayList<IDataStreamElement>());
					return;
				}
				IDataStream t = (IDataStream)((IStructuredSelection)leftTable.getSelection()).getFirstElement();
				List<IDataStreamElement> l = new ArrayList<IDataStreamElement>();
				
				for(IDataStreamElement el : t.getElements()){
					l.add(el);
				}
				leftCols.setInput(l);
			}
			
		});

		
		rightTable = new ComboViewer(this, SWT.READ_ONLY);
		rightTable.getCombo().setVisibleItemCount(15);
		rightTable.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		rightTable.setContentProvider(new ContentProvider());
		rightTable.setLabelProvider(new DataStreamLabelProvider());
		rightTable.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()){
					leftCols.setInput(new ArrayList<IDataStreamElement>());
					return;
				}
				
				IDataStream t = (IDataStream)((IStructuredSelection)rightTable.getSelection()).getFirstElement();
				List<IDataStreamElement> l = new ArrayList<IDataStreamElement>();
				
				for(IDataStreamElement el : t.getElements()){
					l.add(el);
				}
				rightCols.setInput(l);
			}
			
		});
		
		leftCols = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL);
		leftCols.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		leftCols.setContentProvider(new ContentProvider());
		leftCols.setLabelProvider(new MyLabelProvider());
		leftCols.setComparator(new ViewerComparator(){

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return ((IDataStreamElement)e1).getName().compareTo(((IDataStreamElement)e2).getName());
			}
			
		});
		
		rightCols = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL);
		rightCols.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		rightCols.setContentProvider(new ContentProvider());
		rightCols.setLabelProvider(new MyLabelProvider());
		rightCols.setComparator(new ViewerComparator(){

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return ((IDataStreamElement)e1).getName().compareTo(((IDataStreamElement)e2).getName());
			}
			
		});

		leftOuter = new Button(this, SWT.CHECK);
		leftOuter.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		leftOuter.setText(Messages.CompositeRelation_2);
		leftOuter.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyListeners(SWT.Selection, new Event());
				if (joinsViewer.getSelection().isEmpty()){
					return;
				}
				for(Object o : ((IStructuredSelection)joinsViewer.getSelection()).toList()){
					
					boolean set = leftOuter.getSelection();
					
					if (set){
						if (((Join)o).getOuter() == Join.RIGHT_OUTER){
							((Join)o).setOuter(Join.FULL_OUTER);
						}
						else{
							((Join)o).setOuter(Join.LEFT_OUTER);
						}
					}
					else{
						if (((Join)o).getOuter() == Join.FULL_OUTER){
							((Join)o).setOuter(Join.RIGHT_OUTER);
						}
						else{
							((Join)o).setOuter(Join.INNER);
						}
					}
				}
				joinsViewer.refresh();
				refreshTextQuery(false);
			}
			
			
		});
		
		rightOuter = new Button(this, SWT.CHECK);
		rightOuter.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		rightOuter.setText(Messages.CompositeRelation_3);
		rightOuter.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyListeners(SWT.Selection, new Event());
				if (joinsViewer.getSelection().isEmpty()){
					return;
				}
				for(Object o : ((IStructuredSelection)joinsViewer.getSelection()).toList()){
					
					boolean set = rightOuter.getSelection();
					
					if (set){
						if (((Join)o).getOuter() == Join.LEFT_OUTER){
							((Join)o).setOuter(Join.FULL_OUTER);
						}
						else{
							((Join)o).setOuter(Join.RIGHT_OUTER);
						}
					}
					else{
						if (((Join)o).getOuter() == Join.FULL_OUTER){
							((Join)o).setOuter(Join.LEFT_OUTER);
						}
						else{
							((Join)o).setOuter(Join.INNER);
						}
					}
				}
				joinsViewer.refresh();
				refreshTextQuery(false);
			}
		});
		
		
		
//		Group g = new Group(this, SWT.NONE);
//		g.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
//		g.setLayout(new GridLayout(2, true));
//		g.setText(Messages.CompositeRelation_8);
//		
//		c0n = new Button(g, SWT.RADIO);
//		c0n.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		c0n.setText(Cardinality.C_0_n.getLabel());
//		c0n.addSelectionListener(new SelectionAdapter(){
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				if (c0n.getSelection()){
//					
//					leftOuter.setEnabled(true);
//					rightOuter.setEnabled(true);
//					
//					if (relation != null){
//						relation.setCardinality(Cardinality.C_0_n);
//					}
//				}
//				notifyListeners(SWT.Selection, new Event());
//			}
//		});
//		
//		c01 = new Button(g, SWT.RADIO);
//		c01.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		c01.setText(Cardinality.C_0_1.getLabel());
//		c01.addSelectionListener(new SelectionAdapter(){
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				if (c01.getSelection()){
//					
//					leftOuter.setEnabled(true);
//					rightOuter.setEnabled(true);
//					if (relation != null){
//						relation.setCardinality(Cardinality.C_0_1);
//					}
//				}
//				notifyListeners(SWT.Selection, new Event());
//			}
//		});
//		
//		c11 = new Button(g, SWT.RADIO);
//		c11.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		c11.setText(Cardinality.C_1_1.getLabel());
//		c11.addSelectionListener(new SelectionAdapter(){
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				if (c11.getSelection()){
//					
//					leftOuter.setEnabled(false);
//					rightOuter.setEnabled(false);
//					if (relation != null){
//						relation.setCardinality(Cardinality.C_1_1);
//					}
//				}
//				notifyListeners(SWT.Selection, new Event());
//			}
//		});
//		
//		c1n = new Button(g, SWT.RADIO);
//		c1n.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		c1n.setText(Cardinality.C_1_n.getLabel());
//		c1n.addSelectionListener(new SelectionAdapter(){
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				if (c1n.getSelection()){
//					
//					leftOuter.setEnabled(false);
//					rightOuter.setEnabled(false);
//					if (relation != null){
//						relation.setCardinality(Cardinality.C_1_n);
//					}
//					
//				}
//				notifyListeners(SWT.Selection, new Event());
//			}
//		});
		
		
		Composite bar = new Composite(this, SWT.NONE);
		bar.setLayout(new GridLayout(3, true));
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		Button add = new Button(bar, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		add.setText("Add"); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (leftCols.getSelection().isEmpty() || rightCols.getSelection().isEmpty())
					return;
								
				//get the columns
				IDataStreamElement leftElement = (IDataStreamElement)((IStructuredSelection)leftCols.getSelection()).getFirstElement();	
				IDataStreamElement rightElement = (IDataStreamElement)((IStructuredSelection)rightCols.getSelection()).getFirstElement();
				
				try {
					int outer = 0;
					
					if (leftOuter.getSelection() && rightOuter.getSelection()){
						relation.add(leftElement, rightElement, Join.FULL_OUTER);
					}
					else if (leftOuter.getSelection()){
						relation.add(leftElement, rightElement, Join.LEFT_OUTER);
					}
					else if (rightOuter.getSelection()){
						relation.add(leftElement, rightElement, Join.RIGHT_OUTER);
					}
					else{
						relation.add(leftElement, rightElement, Join.INNER);
					}
					
					joinsViewer.setInput(relation.getJoins());
					notifyListeners(SWT.Selection, new Event());
				} catch (ClassNotFoundException e1) {
					MessageDialog.openWarning(getShell(), "Warning", e1.getMessage()); //$NON-NLS-1$
				} catch (RelationException e1) {
					MessageDialog.openWarning(getShell(), "Warning", e1.getMessage()); //$NON-NLS-1$
				}
				
				refreshTextQuery(false);
				
			}
			
		});
		
		Button del = new Button(bar, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		del.setText("Remove"); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)joinsViewer.getSelection();
				
				for(Object o : ss.toList()){
					relation.remove((Join)o);
				
				}
				
				joinsViewer.refresh();
				notifyListeners(SWT.Selection, new Event());
				refreshTextQuery(false);
			}
		});
		
		joinsViewer = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		joinsViewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		joinsViewer.setContentProvider(new IStructuredContentProvider(){


			public Object[] getElements(Object inputElement) {
				List<Join> l = (List<Join>)inputElement;
				return l.toArray(new Join[l.size()]);
			}


			public void dispose() {	}


			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {	}
			
		});
		joinsViewer.setLabelProvider(new LabelProvider());
		joinsViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				if (joinsViewer.getSelection().isEmpty()){
					return;
				}
				
				IStructuredSelection ss = (IStructuredSelection)joinsViewer.getSelection();
				Join j = (Join)ss.getFirstElement();
				
				if (j.getOuter() == Join.FULL_OUTER){
					rightOuter.setSelection(true);
					leftOuter.setSelection(true);
				}
				else if (j.getOuter() == Join.LEFT_OUTER){
					rightOuter.setSelection(false);
					leftOuter.setSelection(true);
				}
				else if (j.getOuter() == Join.RIGHT_OUTER){
					rightOuter.setSelection(true);
					leftOuter.setSelection(false);
				}
				else{
					rightOuter.setSelection(false);
					leftOuter.setSelection(false);
				}
				
				refreshTextQuery(false);
			}
			
		});
		add.setEnabled(true);
		del.setEnabled(true);
		
		labelRelation = new Label(this, SWT.NONE);
		labelRelation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		labelRelation.setText(Messages.CompositeRelation_4);
		
		txtQuery = new Text(this, SWT.BORDER | SWT.MULTI);
		txtQuery.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		txtQuery.addModifyListener(queryModifyListener);
	}

	private ModifyListener queryModifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			if(!relation.isOuterJoin()) { 
				relation.setFinalRelation(txtQuery.getText());
			}
			else {
				if (joinsViewer.getSelection().isEmpty()){
					return;
				}
				else {
					IStructuredSelection ss = (IStructuredSelection)joinsViewer.getSelection();
					Join j = (Join)ss.getFirstElement();
					j.setOnStatement(txtQuery.getText());
				}
			}
		}
	};
	
	private void refreshTextQuery(boolean init) {
		txtQuery.removeModifyListener(queryModifyListener);
		if(relation.isOuterJoin()) {
			if (joinsViewer.getSelection().isEmpty()){
				return;
			}
			IStructuredSelection ss = (IStructuredSelection)joinsViewer.getSelection();
			Join j = (Join)ss.getFirstElement();
			if(j.getOnStatement() != null && !j.getOnStatement().isEmpty() && !j.getOnStatement().equals("null")) { //$NON-NLS-1$
				txtQuery.setText(j.getOnStatement());
			}
			else {
				txtQuery.setText(j.getDefaultOnStatement());
			}
			
		}
		else {
			if(init && relation.getFinalRelation() != null && !relation.getFinalRelation().isEmpty() && !relation.getFinalRelation().equals("null")) { //$NON-NLS-1$
				txtQuery.setText(relation.getFinalRelation());
			}
			else {
				txtQuery.setText(relation.getBasicWhereClause());
				relation.setFinalRelation(txtQuery.getText());
			}
		}
		txtQuery.addModifyListener(queryModifyListener);
	}




	public void createModel(){
		
		List<IDataStream> _l = new ArrayList<IDataStream>(dataSource.getDataStreams());
		
		Collections.sort(_l, new Comparator<IDataStream>() {

			public int compare(IDataStream o1, IDataStream o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});;
		
		rightTable.setInput(_l);
		leftTable.setInput(_l);
		
		if (relation != null){
			
//			switch(relation.getCardinality()){
//			case C_0_n:
//				c0n.setSelection(true);
//				c11.setSelection(false);
//				c1n.setSelection(false);
//				c01.setSelection(false);
//				break;
//			case C_1_1:
//				c0n.setSelection(false);
//				c11.setSelection(true);
//				c1n.setSelection(false);
//				c01.setSelection(false);
//				break;
//			case C_1_n:
//				c0n.setSelection(false);
//				c11.setSelection(false);
//				c1n.setSelection(true);
//				c01.setSelection(false);
//				break;
//			case C_0_1:
//				c0n.setSelection(false);
//				c11.setSelection(false);
//				c1n.setSelection(false);
//				c01.setSelection(true);
//				break;
//			}
			
			joinsViewer.setInput(relation.getJoins());
			
			for(int i = 0; i < dataSource.getDataStreams().size(); i++){
				if (dataSource.getDataStreams().get(i) == relation.getLeftTable()){
					leftTable.setSelection(new StructuredSelection(dataSource.getDataStreams().get(i)));
							
					List<IDataStreamElement> l = new ArrayList<IDataStreamElement>();
					
					for(IDataStreamElement el : relation.getLeftTable().getElements()){
						l.add(el);
					}
					leftCols.setInput(l);
				}
				if (dataSource.getDataStreams().get(i) == relation.getRightTable()){
					rightTable.setSelection(new StructuredSelection(dataSource.getDataStreams().get(i)));
					
					List<IDataStreamElement> l = new ArrayList<IDataStreamElement>();
					
					for(IDataStreamElement el : relation.getRightTable().getElements()){
						l.add(el);
					}
					rightCols.setInput(l);
					
				}
			}
			
		}
		
		refreshTextQuery(true);
	}
	
	
	
	
	
	private class MyLabelProvider extends LabelProvider{

		@Override
		public String getText(Object element) {
			IDataStreamElement c = (IDataStreamElement)element;
			if (c != null)
				return c.getName();
			else
				return null;
		}
		
	}
	
	private class  RelationViewerFilter extends ViewerFilter{

		@Override
		public boolean select(Viewer viewer, Object parentElement,	Object element) {
			for(Relation r : dataSource.getRelations()){
				if (r.isUsingTable((IDataStream)element)){
					return false;
				}
			}
			return true;
		}
		
	}
}
