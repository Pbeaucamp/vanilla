package bpm.gateway.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.ui.composites.labelproviders.DefaultStreamLabelProvider;
import bpm.gateway.ui.i18n.Messages;

public class StreamComposite extends Composite {

	protected CheckboxTableViewer tableViewer;
//	protected StreamDescriptor descriptor;
	protected ISelectionChangedListener listener;
	public static final Font FONT_ACTIVE_CONNECTION = new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD | SWT.ITALIC); //$NON-NLS-1$
	protected Integer toColor;
	
	private boolean isMultipleCheck = false;
	
	private boolean isEditable = false;
	
	
	
	public StreamComposite(Composite parent, int style, StreamDescriptor descriptor, boolean isMultipleCheck, boolean isEditable) {
		super(parent, style);
	
		this.isEditable = isEditable;
		this.isMultipleCheck = isMultipleCheck;
		this.setLayout(new GridLayout());
		build();
		
		this.setBackground(parent.getBackground());
		tableViewer.getTable().setBackground(parent.getBackground());
	}
	
	
	public StreamComposite(Composite parent, int style, boolean isMultipleCheck, boolean isEditable) {
		super(parent, style);
		this.isEditable = isEditable;
		this.isMultipleCheck = isMultipleCheck;
		this.setLayout(new GridLayout());
		build();
		
		this.setBackground(parent.getBackground());
		tableViewer.getTable().setBackground(parent.getBackground());
	}

	public void addCheckListener(ICheckStateListener listener){
		tableViewer.addCheckStateListener(listener);
	}
	
	public void removeCheckListener(ICheckStateListener listener){
		tableViewer.removeCheckStateListener(listener);
	}

	public void addListenerOnViewer(ISelectionChangedListener listener){
		this.listener = listener;
		tableViewer.addSelectionChangedListener(listener);
	}
	
	protected void build(){
		
		tableViewer = CheckboxTableViewer.newCheckList(this, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.V_SCROLL);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		tableViewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<StreamElement> st = (List<StreamElement>)inputElement;
				return st.toArray(new StreamElement[st.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		tableViewer.setLabelProvider(new MyLabelProvider(new LabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		
		
		if (!isMultipleCheck){
			tableViewer.addCheckStateListener(new ICheckStateListener(){

				public void checkStateChanged(CheckStateChangedEvent event) {
					List<StreamElement> input = (List<StreamElement>)tableViewer.getInput();
					for(StreamElement e : input){
						if (event.getChecked() && e != event.getElement()){
							tableViewer.setChecked(e, false);
						}
					}
					
					if (event.getChecked()){
						tableViewer.setSelection(new StructuredSelection(event.getElement()));
					}
					
				}
			
			});

		}
		TableColumn transfoName = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		transfoName.setText(Messages.StreamComposite_1);
		transfoName.setWidth(75);
		
		
		TableColumn originTransfo = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		originTransfo.setText(Messages.StreamComposite_2);
		originTransfo.setWidth(100);
		
		
		TableColumn tableName = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		tableName.setText(Messages.StreamComposite_3);
		tableName.setWidth(75);
	
		
		TableColumn nameCol = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		nameCol.setText(Messages.StreamComposite_4);
		nameCol.setWidth(100);

		
		
		TableColumn typeCol = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		typeCol.setText(Messages.StreamComposite_5);
		typeCol.setWidth(50);
		
		TableColumn classCol = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		classCol.setText(Messages.StreamComposite_6);
		classCol.setWidth(75);
		
		tableViewer.getTable().setHeaderVisible(true);

		
		addEditingFunction();
	}
	
	
	
	private void addEditingFunction(){
		tableViewer.setColumnProperties(new String[]{"Transformation Name", "Origin Transformation", "Table Name", Messages.StreamComposite_13, "Column Type" , "Column Java Class"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		tableViewer.setCellModifier(new DescriptorCellModifier());

		tableViewer.setCellEditors(new CellEditor[]{
				new TextCellEditor(tableViewer.getTable()),
				new TextCellEditor(tableViewer.getTable()), 
				new TextCellEditor(tableViewer.getTable()),
				new TextCellEditor(tableViewer.getTable()),
				new TextCellEditor(tableViewer.getTable()),
				new TextCellEditor(tableViewer.getTable())});

	}
	
//	public void fillDatas(StreamDescriptor descriptor){
//		this.descriptor = descriptor;
//		tableViewer.setInput(descriptor.getStreamElements());
//	}
	
	
	public void fillDatas(List<StreamElement> list){
		tableViewer.setInput(list);
	}
	
	public void setChecked(List<StreamElement> l){
		for(StreamElement e : l){
			tableViewer.setChecked(e, true);
		}
		tableViewer.refresh();
	}
	
	public List<StreamElement> getCheckedElements(){
		List<StreamElement> l = new ArrayList<StreamElement>();
		
		for(Object e : (Object[])tableViewer.getCheckedElements()){
			l.add((StreamElement)e);
		}
		return l;
	}
	
	public List<Integer> getCheckedPosition(){
		List<Integer> l = new ArrayList<Integer>();
		
		Object[] g = tableViewer.getCheckedElements(); 
		for(int i = 0; i < ((List)tableViewer.getInput()).size(); i++){
			Object o = tableViewer.getElementAt(i);
			
			for(Object k : g){
				if (k == o){
					l.add(i);
					break;
				}
			}
//			if (tableViewer.getChecked(tableViewer.getElementAt(i))){
//				l.add(i);
//			}
		}
		
		if (l.isEmpty()){
			l.add(null);
		}
		return l;
		
	}
	
	public List<Integer> getCheckedAndUnCkeckedPosition(){
		
		List<Integer> l = new ArrayList<Integer>();
//		tableViewer.setAllChecked(true);
		Object[] g = tableViewer.getCheckedElements();
		for(int i = 0; i < ((List)tableViewer.getInput()).size(); i++){
			Object o = tableViewer.getElementAt(i);
			
			for(Object k : g){
				if (k == o){
					l.add(i);
					break;
				}
			}

		}
		return l;
		
	}

	
	public List<Integer> getAllElementPositions(){
		
		List<Integer> l = new ArrayList<Integer>();
		Object[] g = tableViewer.getCheckedElements();
		for(int i = 0; i < ((List)tableViewer.getInput()).size(); i++){
			l.add(i);
		}
		return l;
	}
	
	
	public List<StreamElement> getInput(){
		return (List<StreamElement>)tableViewer.getInput();
	}
	
	
	/**
	 * 
	 * @return the index of the selected item
	 */
	public Integer getSelection(){
		IStructuredSelection ss = (IStructuredSelection)tableViewer.getSelection();
		if (ss.isEmpty()){
			tableViewer.refresh();
			return null;
		}
		for(int i = 0; i < ((List)tableViewer.getInput()).size(); i++){
			if (ss.getFirstElement() == tableViewer.getElementAt(i)){
				toColor = i;
				tableViewer.refresh();
				
				return i;
			}
		}
		
		return null;
	}
	
	/**
	 * select i eme element in the viewer
	 * @param i
	 */
	public void setSelection(Integer i){
		toColor = i;
		tableViewer.refresh();
		if (i == null){
			tableViewer.setSelection(new StructuredSelection());
			return;
		}
		
		tableViewer.setSelection(new StructuredSelection(((List<StreamElement>)tableViewer.getInput()).get(i)));
	}

	/**
	 * add the listener if b is true
	 * otherwise remove it
	 * @param b
	 */
	public void desactiveListener(boolean b) {
		if (listener == null){
			return;
		}
		
		if (b){
			tableViewer.removeSelectionChangedListener(listener);
		}
		else{
			tableViewer.addSelectionChangedListener(listener);
		}
		
	}


	/**
	 * uncheck all boxes
	 */
	public void clearCheck() {
		tableViewer.setCheckedElements(new Object[]{});
		
	}

	
	public class MyLabelProvider extends DefaultStreamLabelProvider{
		
		public MyLabelProvider(ILabelProvider provider,
				ILabelDecorator decorator) {
			super(provider, decorator);
			
		}

		
		@Override
		public Font getFont(Object element) {
			if (element instanceof StreamElement){
				

				if (toColor != null && tableViewer.getElementAt(toColor) == element){
					return FONT_ACTIVE_CONNECTION;
				}
				
			}	
			return null;	
			
		}
	}


	public void refresh() {
		tableViewer.refresh();
		
	}
	
	/**
	 * set a new LabelProvider
	 * @param provider
	 */
	public void setLabelProvider(ITableLabelProvider provider){
		
		tableViewer.setLabelProvider(provider);
	}
	
	public ILabelProvider getLabelProvider(){
		return (ILabelProvider)tableViewer.getLabelProvider();
	}
	
	
	
	public class DescriptorCellModifier implements ICellModifier{

		public boolean canModify(Object element, String property) {
			if (property.equals(Messages.StreamComposite_13)){
				return isEditable;
			}
			return false;
		}

		public Object getValue(Object element, String property) {
			if (property.equals(Messages.StreamComposite_13)){ //$NON-NLS-1$
				desactiveListener(true);
				return ((StreamElement)element).name;
			}
			return null;
		}

		public void modify(Object element, String property, Object value) {
			if (property.equals(Messages.StreamComposite_13)){ //$NON-NLS-1$
				
				((StreamElement)((TableItem)element).getData()).name = (String)value;
				tableViewer.refresh();
				desactiveListener(false);
			}
			
		}
		
	}


	private List<Object> elementInError = new ArrayList<Object>();

	public void addInError(Object e){
		elementInError.add(e);
	}
	
	public void removeInError(Object e){
		elementInError.remove(e);
	}
	
	
	public List<Object> getElementInError() {
		
		return elementInError;
	}
}
