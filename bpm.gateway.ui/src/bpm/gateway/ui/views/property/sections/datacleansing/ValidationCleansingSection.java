package bpm.gateway.ui.views.property.sections.datacleansing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.cleansing.ValidationCleansing;
import bpm.gateway.core.transformations.cleansing.ValidationCleansing.ValidationOutput;
import bpm.gateway.core.transformations.cleansing.ValidationCleansing.Validator;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class ValidationCleansingSection  extends AbstractPropertySection{
	private ValidationCleansing transfo;
	
	private TreeViewer viewer;
	private ComboBoxCellEditor outputCellEditor;
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.transfo = (ValidationCleansing)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite main = getWidgetFactory().createComposite(parent, SWT.NONE);
		main.setLayout(new FillLayout());
	
		
		viewer = new TreeViewer(getWidgetFactory().createTree(main, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL));
//		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.getTree().setLinesVisible(true);
		viewer.getTree().setHeaderVisible(true);
		
		
		viewer.setContentProvider(new ITreeContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				List l = new ArrayList();
				try{
					l.addAll(((Transformation)inputElement).getDescriptor((Transformation)inputElement).getStreamElements());
				}catch (Exception e) {
					e.printStackTrace();
				}
				return l.toArray(new Object[l.size()]);
			}

			public Object[] getChildren(Object parentElement) {
				if (!(parentElement instanceof StreamElement)){
					return null;
				}
				int pos = -1;
				try{
					pos = transfo.getDescriptor(transfo).getStreamElements().indexOf((StreamElement)parentElement);
				}catch(Exception ex){
					ex.printStackTrace();
					return null;
				}
				
				if (pos < 0){
					return null;
				}
				List<ValidationOutput> l = transfo.getValidators(pos);
				
				return l.toArray(new ValidationOutput[l.size()]);
				
			}

			public Object getParent(Object element) {
				if (!(element instanceof ValidationOutput)){
					return null;
				}
				return transfo.getStreamElementFor((ValidationOutput)element);
			}

			public boolean hasChildren(Object element) {
				if (!(element instanceof StreamElement)){
					return false;
				}
				int pos = -1;
				try{
					pos = transfo.getDescriptor(transfo).getStreamElements().indexOf((StreamElement)element);
				}catch(Exception ex){
					ex.printStackTrace();
					return false;
				}
				
				if (pos < 0){
					return false;
				}
				List<ValidationOutput> l = transfo.getValidators(pos);

				return l.size() > 0;
			}
		});
		
		
		outputCellEditor = new ComboBoxCellEditor(viewer.getTree(), new String[]{});
		
		TreeViewerColumn col = new TreeViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText(Messages.ValidationCleansingSection_0);
		col.getColumn().setWidth(150);
		
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof StreamElement){
					return ((StreamElement)element).name;
				}
				return ""; //$NON-NLS-1$
			}
		});
		
		
		col = new TreeViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText(Messages.ValidationCleansingSection_2);
		col.getColumn().setWidth(200);
		
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof ValidationOutput){
					return ((ValidationOutput)element).getComment();
				}
				return ""; //$NON-NLS-1$
			}
		});
		col.setEditingSupport(new EditingSupport(viewer) {
			TextCellEditor editor = new TextCellEditor(viewer.getTree());
			@Override
			protected void setValue(Object element, Object value) {
				((ValidationOutput)element).setComment((String)value);
				viewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				String s = ((ValidationOutput)element).getComment();
				if (s == null){
					return ""; //$NON-NLS-1$
				}
				return s;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return element instanceof ValidationOutput;
			}
		});
		
		
		col = new TreeViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText(Messages.ValidationCleansingSection_5);
		col.getColumn().setWidth(150);
		
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof ValidationOutput){
					if (((ValidationOutput)element).getOutput() != null){
						return ((ValidationOutput)element).getOutput().getName();	
					}
					
				}
				return Messages.ValidationCleansingSection_6;
			}
		});
		
		col.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				Integer i = (Integer)value;
				if (i < -1){
					((ValidationOutput)element).setOutputName(null);
				}
				else{
					try{
						((ValidationOutput)element).setOutputName(outputCellEditor.getItems()[i]);
					}catch(Exception ex){
						((ValidationOutput)element).setOutputName(null);
					}
				}
				
				viewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				
				String s = null;
				if (((ValidationOutput)element).getOutput() != null){
					s = ((ValidationOutput)element).getOutput().getName();
				}
			
				
				if (s == null || s.equals("")){ //$NON-NLS-1$
					return -1;
				}
				
				for(int i =0; i < outputCellEditor.getItems().length; i++){
					if (outputCellEditor.getItems()[i].equals(s)){
						return i;
					}
				}
				return -1;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return outputCellEditor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return element instanceof ValidationOutput;
			}
		});
		
		
		col = new TreeViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText(Messages.ValidationCleansingSection_8);
		col.getColumn().setWidth(300);
		
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof ValidationOutput){
					if (((ValidationOutput)element).getValidator() != null){
						return ((ValidationOutput)element).getValidator().getRegex();
					}
					
				}
				else if (element instanceof Validator){
					return ((Validator)element).getRegex();
				}
				return ""; //$NON-NLS-1$
			}
		});
		
		col.setEditingSupport(new EditingSupport(viewer) {
			DialogCellEditor editor = new DialogCellEditor(viewer.getTree()) {
				private Validator validator;
				@Override
				protected Object openDialogBox(Control cellEditorWindow) {
					DialogValidator dial = new DialogValidator(cellEditorWindow.getShell(), (Validator)doGetValue());
					if (dial.open() == DialogValidator.OK){
						return dial.getValidator();
					}
					return null;
				}
				
				@Override
				protected Object doGetValue() {
					return validator;
				}
				
				protected void doSetValue(Object value) {
					this.validator = (Validator)value;
				};
			}; 
			@Override
			protected void setValue(Object element, Object value) {
				if (element instanceof ValidationOutput){
					((ValidationOutput)element).setValidator((Validator)value);
					viewer.refresh();
				}
				
				
			}
			
			@Override
			protected Object getValue(Object element) {
				if (element instanceof ValidationOutput){
					return ((ValidationOutput)element).getValidator();
				}
				return null;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return element instanceof ValidationOutput;
			}
		});
		
		buildTreeMenu();
	}
	
	
	private void buildTreeMenu(){
		MenuManager m = new MenuManager();
		
		final Action addRegex = new Action(Messages.ValidationCleansingSection_10){
			public void run(){
				
				try{
					StreamElement e = (StreamElement)((IStructuredSelection)viewer.getSelection()).getFirstElement();
					if (!e.className.equals("java.lang.String")){ //$NON-NLS-1$
						if (!MessageDialog.openQuestion(getPart().getSite().getShell(), Messages.ValidationCleansingSection_12, Messages.ValidationCleansingSection_13)){
							return;
						}
					}
					int position = transfo.getDescriptor(transfo).getStreamElements().indexOf(e);
					
					ValidationOutput v = new ValidationOutput();
					transfo.addValidator(v, position);
					
					viewer.refresh();
					viewer.setSelection(new StructuredSelection(v));
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
		};
		addRegex.setText(Messages.ValidationCleansingSection_14);
		
		final Action removeRegex = new Action(Messages.ValidationCleansingSection_15){
			public void run(){
				
				try{
					ValidationOutput e = (ValidationOutput)((IStructuredSelection)viewer.getSelection()).getFirstElement();
					
					transfo.removeValidator(e);
					
					viewer.refresh();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
		};
		removeRegex.setText(Messages.ValidationCleansingSection_16);
	
		m.add(addRegex);
		m.add(removeRegex);
		
		m.addMenuListener(new IMenuListener() {
			
			public void menuAboutToShow(IMenuManager manager) {
				Object o = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				addRegex.setEnabled(o instanceof StreamElement);
				removeRegex.setEnabled(o instanceof ValidationOutput);
				
				
			}
		});
	
		viewer.getTree().setMenu(m.createContextMenu(viewer.getTree()));
	}
	
	@Override
	public void refresh() {
		List<String> outputNames = new ArrayList<String>();
		
		for(Transformation t  : transfo.getOutputs()){
			outputNames.add(t.getName());
		}
		
		outputCellEditor.setItems(outputNames.toArray(new String[outputNames.size()]));
		
		viewer.setInput(transfo);
	}
}
