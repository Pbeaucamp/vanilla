package bpm.metadata.ui.query.resources.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.ui.query.Activator;
import bpm.metadata.ui.query.i18n.Messages;

public class CompositeComplexFilter extends Composite implements ResourceBuilder {

	
	private ComplexFilter filter;
	private Text name;
	private TableViewer value;
	private Combo operator;

	private Label errorLabel;
	
	public CompositeComplexFilter(Composite parent, int style, ComplexFilter filter) {
		super(parent, style);
		this.setLayout(new GridLayout());
		buildContent();
		this.filter = filter;
		fillDatas();
	}
	
	

	private void buildContent(){
		createGeneral(this);
	}
	
	
	private Control createGeneral(Composite root){
		Composite parent = new Composite(root, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeComplexFilter_0);
		
		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
			
		Label l4 = new Label(parent, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER,false, false));
		l4.setText(Messages.CompositeComplexFilter_1);
		
		operator = new Combo(parent, SWT.READ_ONLY);
		operator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		operator.setItems(ComplexFilter.OPERATORS);
		operator.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		Label l5 = new Label(parent, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING,false, false));
		l5.setText(Messages.CompositeComplexFilter_2);
		
		ToolBar bar = new ToolBar(parent, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		ToolItem add = new ToolItem(bar, SWT.PUSH);
		add.setToolTipText(Messages.CompositeComplexFilter_3);
		add.setImage(Activator.getDefault().getImageRegistry().get("addCol2")); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog d = new InputDialog(getShell(), Messages.CompositeComplexFilter_4, Messages.CompositeComplexFilter_5, "newValue", null); //$NON-NLS-1$
				if(d.open() == InputDialog.OK){
					
					switch(operator.getSelectionIndex()){
					case 7:
					case 8:
						((List<String>)value.getInput()).add(d.getValue());
						break;
					case 10:
						if (((List<String>)value.getInput()).size() < 2){
							((List<String>)value.getInput()).add(d.getValue());
						}
						else{
							((List<String>)value.getInput()).clear();
							((List<String>)value.getInput()).add(d.getValue());
						}
						break;
					default:
						((List<String>)value.getInput()).clear();
						((List<String>)value.getInput()).add(d.getValue());
						break;
					}
					
					
				}
				value.refresh();	
				
			}
			
		});
		
		ToolItem del = new ToolItem(bar, SWT.PUSH);
		del.setToolTipText(Messages.CompositeComplexFilter_8);
		del.setImage(Activator.getDefault().getImageRegistry().get("delCol2")); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)value.getSelection();
				for(Object o : ss.toArray()){
					((List<String>)value.getInput()).remove(o);
				}
				value.refresh();	
				
			}
			
		});
		
		
		value = new TableViewer(parent, SWT.BORDER);
		value.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		value.setContentProvider(new ArrayContentProvider());
		
		TableViewerColumn col = new TableViewerColumn(value, SWT.NONE);
		col.getColumn().setWidth(300);
		col.setLabelProvider(new ColumnLabelProvider());
		col.setEditingSupport(new EditingSupport(value) {
			TextCellEditor editor = new TextCellEditor(value.getTable(), SWT.NONE);
			@Override
			protected void setValue(Object element, Object value) {
				int i = ((List)CompositeComplexFilter.this.value.getInput()).indexOf(element);
				((List)CompositeComplexFilter.this.value.getInput()).set(i, (String)value);
				CompositeComplexFilter.this.value.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {return (String)element;}
			
			@Override
			protected CellEditor getCellEditor(Object element) {return editor;}
			
			@Override
			protected boolean canEdit(Object element) {return true;}
		});

		return parent;
	}

	private void fillDatas(){
		
		name.setText(filter.getName());
		operator.setText(filter.getOperator());
		if (filter.getValue() == null){
			value.setInput(new ArrayList<String>());
		}
		else{
			value.setInput(filter.getValue());
		}
		

	}
	
	public IResource getResource(){

		filter.setName(name.getText());
		filter.setOperator(operator.getText());
		
		filter.clearValue();
		for(String s : (List<String>)value.getInput()){
			filter.setValue(s);
		}
		
		return filter;
	}
	

	/**
	 * 
	 * @return true if the composite is rightly filled
	 */
	public boolean isFilled(){
		return !errorLabel.isVisible() && !name.getText().trim().equals("") && !operator.getText().trim().equals("") && !((List<String>)value.getInput()).isEmpty(); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
