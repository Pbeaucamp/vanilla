package bpm.gateway.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.Filter;
import bpm.gateway.core.transformations.utils.Condition;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogList;
import bpm.gateway.ui.tools.dialogs.DialogPickupConstant;
import bpm.gateway.ui.tools.dialogs.DialogPickupParameter;

public class FilterTable2 extends Composite {
	
		
	public static Color mainBrown = new Color(Display.getDefault(), 209,177, 129);
	public static Color secondBrown = new Color(Display.getDefault(), 238,226, 208);

	private TreeViewer viewer;
	private Filter transfo;
	
	private Transformation dragedTransfo;
		
	private ComboBoxCellEditor fieldEditor;

	public FilterTable2(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		buildViewer();
	}

	
	
	public void addSelectionListener(ISelectionChangedListener listener){
		viewer.addSelectionChangedListener(listener);
	}
	
	public void removeSelectionListener(ISelectionChangedListener listener){
		viewer.removeSelectionChangedListener(listener);
	}
	
	
	public void setFilter(Filter transfo){
		this.transfo = transfo;
		
		viewer.setInput(transfo.getOutputs());
		refreshFields();
		
	}
	
	
	private void buildViewer(){
		viewer = new TreeViewer(this, SWT.V_SCROLL| SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(new ITreeContentProvider(){

			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof Transformation){
					List<Condition> l = new ArrayList<Condition>();
					
					for(Condition c : transfo.getConditions()){
						if (c.getOutput() == parentElement){
							l.add(c);
						}
					}
					return l.toArray(new Condition[l.size()]);
					
				}
				return null;
			}

			public Object getParent(Object element) {
				if (element instanceof Condition){
					return ((Condition)element).getOutput();
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				if (element instanceof Transformation){
					for(Condition  c: transfo.getConditions()){
						if (c.getOutput() == element){
							return true;
						}
					}
				}
				return false;
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

			public Object[] getElements(Object inputElement) {
				List<Transformation> l = (List)(inputElement);
				return l.toArray(new Transformation[l.size()]);
			}

			
		});
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TreeViewerColumn outputCol = new TreeViewerColumn(viewer, SWT.NONE);
		outputCol.getColumn().setText(Messages.FilterTable2_0);
		outputCol.getColumn().setWidth(200);
		outputCol.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof Transformation){
					return ((Transformation)element).getName();
				}
				return null;
//				return "";
			}

			@Override
			public Color getBackground(Object element) {
				return getColor(element);
			}

			
			
		});
		
		TreeViewerColumn fieldCol = new TreeViewerColumn(viewer, SWT.NONE);
		fieldCol.getColumn().setText(Messages.FilterTable2_1);
		fieldCol.getColumn().setWidth(200);
		fieldCol.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof Condition){
					Condition c = (Condition)element;
					try {
						return c.getStreamElementName().split("::")[1]; //$NON-NLS-1$
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				return null;
			}
			@Override
			public Color getBackground(Object element) {
				return getColor(element);
			}
			
		});
		
		TreeViewerColumn operatorCol = new TreeViewerColumn (viewer, SWT.NONE);
		operatorCol.getColumn().setText(Messages.FilterTable2_2);
		operatorCol.getColumn().setWidth(50);
		operatorCol.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof Condition){
					return ((Condition)element).getOperator();
				}
				return null;
			}
			@Override
			public Color getBackground(Object element) {
				return getColor(element);
			}
			
		});
		
		TreeViewerColumn valueCol = new TreeViewerColumn(viewer, SWT.NONE);
		valueCol.getColumn().setText(Messages.FilterTable2_3);
		valueCol.getColumn().setWidth(250);
		valueCol.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof Condition){
					
					if (((Condition)element).getOperatorConstant() == Condition.IN){
						return ((Condition)element).getValue().replace("]", ", \r\n"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					
					return ((Condition)element).getValue();
				}
				return null;
			}
			@Override
			public Color getBackground(Object element) {
				return getColor(element);
			}
			
		});
		
		
		
		TreeViewerColumn logicalCol = new TreeViewerColumn(viewer, SWT.NONE);
		logicalCol.getColumn().setText(Messages.FilterTable2_6);
		logicalCol.getColumn().setWidth(50);
		logicalCol.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof Condition){
					try{
						return Condition.LOGICALS[((Condition)element).getLogical()];
					}catch(NullPointerException e){
						
					}
				}
				
				return ""; //$NON-NLS-1$
			}
			@Override
			public Color getBackground(Object element) {
				return getColor(element);
			}
		});
		
		//set the columns properties
		viewer.setColumnProperties(new String[]{"output", "field", "operator", "value", "logical"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		viewer.setCellModifier(new FilterCellModifier());
	
		viewer.getTree().setHeaderVisible(true);
		
		
		buildCellEditors();
		
		
		viewer.setCellEditors(new CellEditor[]{
				new TextCellEditor(viewer.getTree()),
				fieldEditor, 
				new ComboBoxCellEditor(viewer.getTree(), Condition.OPERATORS),
				new ParameterDialogCellEditor(viewer.getTree()),
				new ComboBoxCellEditor(viewer.getTree(), Condition.LOGICALS)});
		
		setDnd();
		
	}
	
	public void refreshFields(){
		List<String> l = new ArrayList<String>();
		try{
			for(StreamElement e : transfo.getDescriptor(transfo).getStreamElements()){
				l.add(e.name);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if (fieldEditor == null){
			fieldEditor = new ComboBoxCellEditor(viewer.getTree(), l.toArray(new String[l.size()]));
			

			viewer.setCellEditors(new CellEditor[]{
					new TextCellEditor(viewer.getTree()),
					fieldEditor, 
					new ComboBoxCellEditor(viewer.getTree(), Condition.OPERATORS),
					new ParameterDialogCellEditor(viewer.getTree().getParent()),
					new ComboBoxCellEditor(viewer.getTree(), Condition.LOGICALS)});

		}
		else{
			fieldEditor.setItems(l.toArray(new String[l.size()]));
		}
				
		
		
		
	}
	
	
	public void refreshViewer(){
		viewer.setInput(transfo.getOutputs());
	}
	
	
	
	
	
	private void buildCellEditors(){
		
		/*
		 * build the fields Editor
		 */
		List<String> fieldsName = new ArrayList<String>();
		fieldEditor = new ComboBoxCellEditor(viewer.getTree(), fieldsName.toArray(new String[fieldsName.size()]));
						
			
		
	}
	
	
	
	
	private class FilterCellModifier implements ICellModifier{

		public boolean canModify(Object element, String property) {
			if (!(element instanceof Condition)){
				return false;
			}
			if (property.equals("logical")){ //$NON-NLS-1$
				
				for(Condition c : transfo.getConditions()){
					if (c != element && c.getOutput() == ((Condition)element).getOutput()){
						return true;
					}
				}
				return false;
				
			}
			
			else if (property.equals("output")){ //$NON-NLS-1$
				return false;
			}
			else if (property.equals("value")){ //$NON-NLS-1$
				if (((Condition)element).getOperatorConstant() == Condition.NULL){
					return false;
				}
				return true;
			}
			return true;
		}

		public Object getValue(Object element, String property) {
			if (property.equals("field")){ //$NON-NLS-1$
				try {
					
					for(int i = 0; i < fieldEditor.getItems().length; i++){
						String f = fieldEditor.getItems()[i];
						String s = ((Condition)element).getStreamElementName().split("::")[1]; //$NON-NLS-1$
						if (s.equals(f)){
							return i;
						}
					}
					
				}catch(Exception e){
					e.printStackTrace();
				}
				return -1;
			}
			else if (property.equals("operator")){ //$NON-NLS-1$
				
				for(int i = 0; i < Condition.OPERATORS.length; i++){
					if (Condition.OPERATORS[i].equals(((Condition)element).getOperator())){
						return i;
					}
				}
				return 0;

			}
			else if (property.equals("logical")){ //$NON-NLS-1$

				
				for(int i = 0; i < Condition.LOGICALS.length; i++){
					if (i == ((Condition)element).getLogical()){
						return i;
					}
				}
				return 0;

			}
			else if (property.equals("value")){ //$NON-NLS-1$
				return ((Condition)element).getValue();
			}
			else if (property.equals("output")){ //$NON-NLS-1$
				for(int i = 0; i < transfo.getOutputs().size(); i++){
					if (transfo.getOutputs().get(i) == ((Condition)element).getOutput()){
						return i;
					}
				}
				return 0;
			}
			return null;
		}

		public void modify(Object element, String property, Object value) {
			if (property.equals("field")){ //$NON-NLS-1$
				Condition c = (Condition)((TreeItem)element).getData(); 
				
				int i = 0;
				try{
					for(StreamElement e : transfo.getDescriptor(transfo).getStreamElements()){
						if (e.name.equals(fieldEditor.getItems()[(Integer)value])){
							c.setStreamElementName(e.getFullName());
							viewer.refresh();
							break;
						}
						i ++ ;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				

			}
			else if (property.equals("operator")){ //$NON-NLS-1$
				if (((Integer)value) < 0){
					return;
				}
				((Condition)((TreeItem)element).getData()).setOperator(Condition.OPERATORS[(Integer)value]);
				viewer.refresh();
			}
			else if (property.equals("logical")){ //$NON-NLS-1$
				if (((Integer)value) < 0){
					return;
				}
				((Condition)((TreeItem)element).getData()).setLogical((Integer)value);
				viewer.refresh();
			}
			else if (property.equals("value")){ //$NON-NLS-1$
				((Condition)((TreeItem)element).getData()).setValue((String)value);
				viewer.refresh();
			}
			
			
		}
		
	}
	
	public IStructuredSelection getSelection(){
		return (IStructuredSelection)viewer.getSelection();
	}
	
	private Color getColor(Object element){
		if (element instanceof Transformation){
			
			return mainBrown;
			
		}
		
		if (element instanceof Condition){
			Transformation parent = (Transformation)((ITreeContentProvider)viewer.getContentProvider()).getParent(element);
			Object[] child = ((ITreeContentProvider)viewer.getContentProvider()).getChildren(parent);
			
			for(int i = 0; i < child.length; i++){
				if (child[i] == element){
					if (i % 2 == 1){
						return null;
					}
					else{
						return secondBrown;
					}
				}
			}
			
			
		}
		return null;
	}



	public void expand(Transformation transfo) {
		List<Object> expanded = new ArrayList<Object>();
		
		for(Object o : viewer.getExpandedElements()){
			expanded.add(o);
		}
		expanded.add(transfo);
		
		viewer.setExpandedElements(expanded.toArray(new Object[expanded.size()]));
		viewer.setSelection(new StructuredSelection(transfo));
		
	}
	
	
	private void setDnd(){
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance()};
		
		viewer.addDragSupport(ops, transfers, new DragSourceListener(){

			public void dragFinished(DragSourceEvent event) { }

			public void dragSetData(DragSourceEvent event) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				
				if (ss.getFirstElement() instanceof Transformation){
					dragedTransfo = (Transformation)ss.getFirstElement();
					event.data = "TSF"; //$NON-NLS-1$
				}
				
			}

			public void dragStart(DragSourceEvent event) { }
			
		});
		
		viewer.addDropSupport(ops, transfers, new DropTargetListener(){

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
				
			}

			public void dragLeave(DropTargetEvent event) { }

			public void dragOperationChanged(DropTargetEvent event) { }

			public void dragOver(DropTargetEvent event) { }

			public void drop(DropTargetEvent event) {
				Object o = ((TreeItem) event.item).getData();
				//cancel
				if (!(o instanceof Transformation)){
					dragedTransfo = null;
					return;
				}
				
				transfo.swapOutputs(dragedTransfo, (Transformation)o);
				setFilter(transfo);
				dragedTransfo = null;
				
			}

			public void dropAccept(DropTargetEvent event) { }
			
		});
	}
		
	
	private class ParameterDialogCellEditor extends DialogCellEditor{

		public ParameterDialogCellEditor(Composite parent, int style) {
			super(parent, style);
		}

		public ParameterDialogCellEditor(Composite parent) {
			super(parent);
		}

		@Override
		protected Object openDialogBox(Control arg0) {
			
			IEditorPart ePart = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
					
			GatewayEditorInput input = (GatewayEditorInput)ePart.getEditorInput();
			
			Object o = ((IStructuredSelection)viewer.getSelection()).getFirstElement();

			if (o instanceof Condition && ((Condition)o).getOperatorConstant() ==Condition.IN){
				DialogList d = new DialogList(this.getControl().getShell(), ((Condition)o).getValue());
				if (d.open() == DialogPickupConstant.OK){
					return d.getValue();
				}
			}
			else{
				DialogPickupParameter d = new DialogPickupParameter(this.getControl().getShell(), input.getDocumentGateway().getParameters());
				if (d.open() == DialogPickupConstant.OK){
					return d.getValue();
				}
			}

			return null;
		}
		
	}
}
