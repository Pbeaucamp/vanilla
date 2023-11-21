package bpm.profiling.ui.composite;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import bpm.profiling.runtime.core.Condition;
import bpm.profiling.ui.Activator;

public class CompositeCondition extends Composite {

	private TableViewer viewer;
	
	public CompositeCondition(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		buildContent();
	}
	
	private void buildContent(){
		
		viewer =  new TableViewer(this, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Condition> l = (List<Condition>)inputElement;
				return l.toArray(new Condition[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		viewer.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex){
				case 0:
					return Condition.operators[((Condition)element).getOperator()];
				case 1:
					return ((Condition)element).getValue1();
				case 2:
					return ((Condition)element).getValue2();
					
				}
				return "";
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
		
		
		TableColumn operator = new TableColumn(viewer.getTable(), SWT.NONE);
		operator.setText("Operator");
		operator.setWidth(100);
		
		TableColumn value1 = new TableColumn(viewer.getTable(), SWT.NONE);
		value1.setText("Value1");
		value1.setWidth(100);
		
		TableColumn value2 = new TableColumn(viewer.getTable(), SWT.NONE);
		value2.setText("Value2");
		value2.setWidth(100);
		
		
		viewer.setColumnProperties(new String[]{"operator", "value1", "value2"});
		viewer.setCellModifier(new ConditionCellModifier());
		
		viewer.setCellEditors(new CellEditor[]{
				new ComboBoxCellEditor(viewer.getTable(), Condition.operators), 
				new TextCellEditor(viewer.getTable()),
				new TextCellEditor(viewer.getTable())});
		
		
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		
	}

	
	private class ConditionCellModifier implements ICellModifier{

		public boolean canModify(Object element, String property) {
			Condition c = (Condition)element;
			
			switch(c.getOperator()){
			case Condition.DIF:
			case Condition.EQUAL:
			case Condition.GREAT:
			case Condition.GREAT_E:
			case Condition.LESS:
			case Condition.LESS_E:
			case Condition.LIKE:
				if (property.equals("value1")){
					return true;
				}
				if (property.equals("value2")){
					return false;
				}
			case Condition.IN:
				return true;
			case Condition.NULL:
				if (property.equals("value1")  || property.equals("value2")){
					return false;
				}
			case Condition.BETWEEN:
				return true;
			}
			
			return true;
		}

		public Object getValue(Object element, String property) {
			if (property.equals("operator")){
				return ((Condition)element).getOperator();
			}
			else if (property.equals("value1")){
				return ((Condition)element).getValue1();
			}
			else {
				return ((Condition)element).getValue2();
			}
		}

		public void modify(Object element, String property, Object value) {
			if (property.equals("operator")){
				((Condition)((TableItem)element).getData()).setOperator((Integer)value);
				
				Condition c = ((Condition)((TableItem)element).getData());
				
				switch(c.getOperator()){
				case Condition.DIF:
				case Condition.EQUAL:
				case Condition.GREAT:
				case Condition.GREAT_E:
				case Condition.LESS:
				case Condition.LESS_E:
				case Condition.LIKE:
					c.setValue2("");
					break;
				case Condition.NULL:
					c.setValue2("");
					c.setValue1("");
					break;
				
				}
				
				
			}
			else if (property.equals("value1")){
				((Condition)((TableItem)element).getData()).setValue1((String)value);
			}
			else {
				((Condition)((TableItem)element).getData()).setValue2((String)value);
			}
			Activator.helper.getAnalysisManager().updateCondition(((Condition)((TableItem)element).getData()));
			viewer.refresh();
			
			
		}
		
	}
	
	
	public void setContent(List<Condition> conditions){
		viewer.setInput(conditions);
	}

	public Condition getSelectedCondition() {
		if (viewer != null && !viewer.getTable().isDisposed()){
			IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
			
			if (ss.isEmpty()){
				return null;
			}
			
			return (Condition)ss.getFirstElement();
		}
		return null;
	}
	
	
}

