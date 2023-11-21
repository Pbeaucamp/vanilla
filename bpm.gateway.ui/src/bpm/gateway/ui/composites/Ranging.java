package bpm.gateway.ui.composites;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
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

import bpm.gateway.core.transformations.calcul.Range;
import bpm.gateway.core.transformations.calcul.RangingTransformation;
import bpm.gateway.core.transformations.calcul.Scripting;
import bpm.gateway.ui.i18n.Messages;


public class Ranging extends Composite {

	private TableViewer viewer;
	
	
	private RangingTransformation transfo;
	
	
	public Ranging(Composite parent, int style) {
		super(parent, style);
		buildContent();
		
	}
	
	public void addSelectionListener(ISelectionChangedListener listener){
		viewer.addSelectionChangedListener(listener);
	}
	
	public void removeSelectionListener(ISelectionChangedListener listener){
		viewer.removeSelectionChangedListener(listener);
	}	
	
	public void setInput(RangingTransformation transfo){
		this.transfo = transfo;
		
		viewer.setInput(transfo.getRanges());
		
		
		
		
		
		
	}
	
	private void buildContent(){
		this.setLayout(new GridLayout());
		
				
		viewer = new TableViewer(this, SWT.V_SCROLL| SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Range> l = (List<Range>)inputElement;
				return l.toArray(new Range[l.size()]);

			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {}
			
		});
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex){
				case 0:
					if (element != null && ((Range)element).getIntervalType() >= 0){
						return Scripting.INTERVALS_TYPE[((Range)element).getIntervalType()];
					}

				case 1:
					if (((Range)element).getFirstValue() == null){
						return "X"; //$NON-NLS-1$
					}
					return ((Range)element).getFirstValue();
				case 2:
					if (((Range)element).getFirstValue() == null){
						return "X"; //$NON-NLS-1$
					}
					return ((Range)element).getSecondValue();
				case 3:
					if (((Range)element).getFirstValue() == null){
						return "X"; //$NON-NLS-1$
					}
					return ((Range)element).getOutput();
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
		 
		TableColumn typeCol = new TableColumn(viewer.getTable(), SWT.NONE);
		typeCol.setText(Messages.Ranging_3);
		typeCol.setWidth(200);
		
		TableColumn firstCol = new TableColumn (viewer.getTable(), SWT.NONE);
		firstCol.setText(Messages.Ranging_4);
		firstCol.setWidth(200);
		
		TableColumn secondCol = new TableColumn(viewer.getTable(), SWT.NONE);
		secondCol.setText(Messages.Ranging_5);
		secondCol.setWidth(200);
		
		TableColumn outputCol = new TableColumn(viewer.getTable(), SWT.NONE);
		outputCol.setText(Messages.Ranging_6);
		outputCol.setWidth(200);
			
		
		//set the columns properties
		viewer.setColumnProperties(new String[]{"Type", "first", "second", "output"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		viewer.setCellModifier(new RangingCellModifier());
	
		viewer.getTable().setHeaderVisible(true);
		
		viewer.setCellEditors(new CellEditor[]{
				new ComboBoxCellEditor(viewer.getTable(), Scripting.INTERVALS_TYPE), 
				new TextCellEditor(viewer.getTable()),
				new TextCellEditor(viewer.getTable()),
				new TextCellEditor(viewer.getTable())});

	}
	

	
	private class RangingCellModifier implements ICellModifier{

		public boolean canModify(Object element, String property) {
			
			return true;
		}

		public Object getValue(Object element, String property) {
			if (property.equals("Type")){ //$NON-NLS-1$
				return ((Range)element).getIntervalType();
				
			}
			else if (property.equals("first")){ //$NON-NLS-1$
				return ((Range)element).getFirstValue();

			}
			else if (property.equals("second")){ //$NON-NLS-1$
				return ((Range)element).getSecondValue();
			}
			else if (property.equals("output")){ //$NON-NLS-1$
				return ((Range)element).getOutput();
			}
			
			return null;
		}

		public void modify(Object element, String property, Object value) {
			if (property.equals("Type")){ //$NON-NLS-1$
				((Range)((TableItem)element).getData()).setIntervalType((Integer)value);
			}
			else if (property.equals("first")){ //$NON-NLS-1$
				((Range)((TableItem)element).getData()).setFirstValue((String)value);

			}
			else if (property.equals("second")){ //$NON-NLS-1$
				((Range)((TableItem)element).getData()).setSecondValue((String)value);
			}
			else if (property.equals("output")){ //$NON-NLS-1$
				((Range)((TableItem)element).getData()).setOutput((String)value);
			}
			
			viewer.refresh();
			
		}
		
	}

}
