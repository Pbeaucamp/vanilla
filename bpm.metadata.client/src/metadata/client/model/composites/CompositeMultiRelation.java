package metadata.client.model.composites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.MultiDSRelation;
import bpm.metadata.layer.logical.RelationException;

public class CompositeMultiRelation extends Composite {

	private Combo leftSource, rightSource;
	private ListViewer leftCols, rightCols;
	private Combo leftTable, rightTable;
	private ListViewer joinsViewer;

	private MultiDSRelation relation;
	private boolean applyChange;
	
	public CompositeMultiRelation(Composite parent, int style, boolean applyChange, MultiDSRelation relation) {
		super(parent, style);
		this.relation = relation;
		this.applyChange = applyChange;
		buildContent();
		createModel();
	}
	
		
	private void buildContent(){
		this.setLayout(new GridLayout(2, true));
		
		leftSource = new Combo(this, SWT.READ_ONLY);
		leftSource.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		leftSource.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				IDataSource ds = Activator.getDefault().getModel().getDataSource(leftSource.getText());
				
				List<String> l = new ArrayList<String>();
				
				for(IDataStream t : ds.getDataStreams()){
					l.add(t.getName());
				}
				leftTable.setItems(l.toArray(new String[l.size()]));

				
			}
		});
		
		rightSource = new Combo(this, SWT.READ_ONLY);
		rightSource.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		rightSource.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				IDataSource ds = Activator.getDefault().getModel().getDataSource(rightSource.getText());
				
				List<String> l = new ArrayList<String>();
				
				for(IDataStream t : ds.getDataStreams()){
					l.add(t.getName());
				}
				rightTable.setItems(l.toArray(new String[l.size()]));

				
			}
		});
		
		leftTable = new Combo(this, SWT.READ_ONLY);
		leftTable.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		leftTable.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IDataSource dataSource = Activator.getDefault().getModel().getDataSource(leftSource.getText());
				IDataStream t = dataSource.getDataStreamNamed(leftTable.getText());
				
				List<IDataStreamElement> l = new ArrayList<IDataStreamElement>();
				
				for(IDataStreamElement el : t.getElements()){
					l.add(el);
				}
				leftCols.setInput(l);
			}
			
		});
		
		
		rightTable = new Combo(this, SWT.READ_ONLY);
		rightTable.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		rightTable.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IDataSource dataSource = Activator.getDefault().getModel().getDataSource(rightSource.getText());
				IDataStream t = dataSource.getDataStreamNamed(rightTable.getText());
				
				List<IDataStreamElement> l = new ArrayList<IDataStreamElement>();
				
				for(IDataStreamElement el : t.getElements()){
					l.add(el);
				}
				rightCols.setInput(l);
				
			}
			
		});
		
		
		
		Button rightOuter = new Button(this, SWT.CHECK);
		rightOuter.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		rightOuter.setText(Messages.CompositeMultiRelation_0); //$NON-NLS-1$
		rightOuter.setEnabled(false);
		
		Button leftOuter = new Button(this, SWT.CHECK);
		leftOuter.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		leftOuter.setText(Messages.CompositeMultiRelation_1); //$NON-NLS-1$
		leftOuter.setEnabled(false);
		
		leftCols = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL);
		leftCols.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		leftCols.setContentProvider(new ContentProvider());
		leftCols.setLabelProvider(new MyLabelProvider());
		
		
		rightCols = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL);
		rightCols.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		rightCols.setContentProvider(new ContentProvider());
		rightCols.setLabelProvider(new MyLabelProvider());
		
		
		
		
		Composite bar = new Composite(this, SWT.NONE);
		bar.setLayout(new GridLayout(2, true));
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		Button add = new Button(bar, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		add.setText(Messages.CompositeMultiRelation_2); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (leftCols.getSelection().isEmpty() || rightCols.getSelection().isEmpty())
					return;
								
				//get the columns
				IDataStreamElement leftElement = (IDataStreamElement)((IStructuredSelection)leftCols.getSelection()).getFirstElement();	
				IDataStreamElement rightElement = (IDataStreamElement)((IStructuredSelection)rightCols.getSelection()).getFirstElement();
				
				try {
					relation.addJoin(leftElement, rightElement);
					joinsViewer.setInput(relation.getJoins());
				} catch (ClassNotFoundException e1) {
					Activator.getLogger().error(e1.getMessage(), e1);
					MessageDialog.openWarning(getShell(), Messages.CompositeMultiRelation_3, e1.getMessage()); //$NON-NLS-1$
				} catch (RelationException e1) {
					Activator.getLogger().error(e1.getMessage(), e1);
					MessageDialog.openWarning(getShell(), Messages.CompositeMultiRelation_4, e1.getMessage()); //$NON-NLS-1$
				}
				
			}
			
		});
		
		Button del = new Button(bar, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		del.setText(Messages.CompositeMultiRelation_5); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)joinsViewer.getSelection();
				
				for(Object o : ss.toList()){
					relation.removeJoin((Join)o);
				
				}
				
				joinsViewer.refresh();
			}
		});
		
		joinsViewer = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		joinsViewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		joinsViewer.setContentProvider(new IStructuredContentProvider(){


			public Object[] getElements(Object inputElement) {
				List<Join> l = (List<Join>)inputElement;
				return l.toArray(new Join[l.size()]);
			}


			public void dispose() {}


			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {}
			
		});
		joinsViewer.setLabelProvider(new LabelProvider());
		
		if (applyChange){
			add.setEnabled(true);
			del.setEnabled(true);
		}
		else{
			add.setEnabled(false);
			del.setEnabled(false);
		}
		
	}

	
	private void createModel(){
		for(IDataSource d : Activator.getDefault().getModel().getDataSources()){
			leftSource.add(d.getName());
			rightSource.add(d.getName());
		}
		
		if (relation != null){
			joinsViewer.setInput(relation.getJoins());
			
			int i = 0;
			if (relation.getLeftDataSource() != null){
				for(String s : leftSource.getItems()){
					if (s.equals(relation.getLeftDataSource().getName())){
						leftSource.select(i);
						
						if (relation.getLeftTable() != null){
							List<String> l = new ArrayList<String>();
							int j =0;
							for(IDataStream t : relation.getLeftDataSource().getDataStreams()){
								l.add(t.getName());
								if (t != relation.getLeftTable()){
									j++;
								}
							}
							leftTable.setItems(l.toArray(new String[l.size()]));
							leftTable.select(j);
						}
						break;
					}
					i++;
				}
			}
			
			i = 0;
			if (relation.getRightDataSource() != null){
				for(String s : rightSource.getItems()){
					if (s.equals(relation.getRightDataSource().getName())){
						rightSource.select(i);
						if (relation.getRightTable() != null){
							List<String> l = new ArrayList<String>();
							int j =0;
							for(IDataStream t : relation.getRightDataSource().getDataStreams()){
								l.add(t.getName());
								if (t != relation.getRightTable()){
									j++;
								}
							}
							rightTable.setItems(l.toArray(new String[l.size()]));
							rightTable.select(j);
						}
						
						break;
					}
					i++;
				}
			}
			
			if (relation.getRightTable() != null){
				rightCols.setInput(relation.getRightTable().getElements());
			}
			
			if (relation.getLeftTable() != null){
				leftCols.setInput(relation.getLeftTable().getElements());
			}
			
		}
	}
	
	
	
	private class ContentProvider implements IStructuredContentProvider{


		public Object[] getElements(Object inputElement) {
			Collection<IDataStreamElement> l = (Collection<IDataStreamElement>)inputElement;
			
			return l.toArray(new IDataStreamElement[l.size()]);
		}


		public void dispose() {}


		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		
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
}
