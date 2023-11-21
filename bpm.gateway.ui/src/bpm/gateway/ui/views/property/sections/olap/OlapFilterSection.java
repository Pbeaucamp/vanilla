package bpm.gateway.ui.views.property.sections.olap;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.olap.DimensionFilter;
import bpm.gateway.core.transformations.olap.FilterClause;
import bpm.gateway.core.transformations.olap.OlapFactExtractorTranformation;
import bpm.gateway.core.transformations.olap.OlapHelper;
import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class OlapFilterSection extends AbstractPropertySection{
	private static final String NONE = "--- None ---"; //$NON-NLS-1$
	private OlapFactExtractorTranformation transfo;
	private TreeViewer viewer;
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        Assert.isTrue(((Node)((NodePart) input).getModel()).getGatewayModel() instanceof OlapFactExtractorTranformation);
        this.transfo = (OlapFactExtractorTranformation)(((Node)((NodePart) input).getModel()).getGatewayModel());
	}
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		viewer = new TreeViewer(getWidgetFactory().createTree(composite, SWT.BORDER | SWT.FULL_SELECTION));
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);
		
		viewer.setContentProvider(new ITreeContentProvider(){

			public Object[] getChildren(Object parentElement) {
				List<FilterClause> l = ((DimensionFilter)parentElement).getFilters();
				return l.toArray(new Object[l.size()]);
			}

			public Object getParent(Object element) {
				if (element instanceof FilterClause){
					return ((FilterClause)element).getParent();
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				if (element instanceof DimensionFilter){
					return ((DimensionFilter)element).getFilters().size() > 0;
				}
				return false;
				
			}

			public Object[] getElements(Object inputElement) {
				List<DimensionFilter> l = transfo.getDimensionFilters();
				return l.toArray(new Object[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
		});
		
		
		createContextMenu();
	
		final TreeViewerColumn nameCol = new TreeViewerColumn(viewer, SWT.NONE);
		nameCol.getColumn().setText(Messages.OlapFilterSection_1);
		nameCol.getColumn().setWidth(200);
		nameCol.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof DimensionFilter){
					return ((DimensionFilter)element).getName() + ""; //$NON-NLS-1$
				}
				else{
					return ""; //$NON-NLS-1$
				}
				
			}
			
		});
		nameCol.setEditingSupport(new EditingSupport(viewer){
			TextCellEditor editor = new TextCellEditor(viewer.getTree());
			@Override
			protected boolean canEdit(Object element) {
				return (element instanceof DimensionFilter);
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				return ((DimensionFilter)element).getName();
			}

			@Override
			protected void setValue(Object element, Object value) {
				((DimensionFilter)element).setName((String)value);
				viewer.refresh();
				
			}
			
		});
		
		
		TreeViewerColumn lvlCol = new TreeViewerColumn(viewer, SWT.NONE);
		lvlCol.getColumn().setText(Messages.OlapFilterSection_4);
		lvlCol.getColumn().setWidth(200);
		lvlCol.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof FilterClause){
					return ((FilterClause)element).getLevelName();
				}
				return ""; //$NON-NLS-1$
			}
			
		});
		
		TreeViewerColumn valueCol = new TreeViewerColumn(viewer, SWT.NONE);
		valueCol.getColumn().setText(Messages.OlapFilterSection_6);
		valueCol.getColumn().setWidth(200);
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof FilterClause){
					
					return ((FilterClause)element).getValue() + ""; //$NON-NLS-1$
				}
				return ""; //$NON-NLS-1$
			}
		});
		valueCol.setEditingSupport(new EditingSupport(viewer){
			ClauseCellEditor editor = new ClauseCellEditor(viewer.getTree());
			
			@Override
			protected boolean canEdit(Object element) {
				return (element instanceof FilterClause);
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				if (element instanceof FilterClause){
					return ((FilterClause)element).getValue();
				}
				return null;
				
			}

			@Override
			protected void setValue(Object element, Object value) {
				((FilterClause)element).setValue((String)value);
				viewer.refresh();
			}
		});
		
		
		TreeViewerColumn outCol = new TreeViewerColumn(viewer, SWT.NONE);
		outCol.getColumn().setText(Messages.OlapFilterSection_9);
		outCol.getColumn().setWidth(200);
		outCol.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof DimensionFilter){
					return ((DimensionFilter)element).getOutputName() + ""; //$NON-NLS-1$
				}
				else{
					return ""; //$NON-NLS-1$
				}
				
			}

			@Override
			public Color getBackground(Object element) {
				if (element instanceof DimensionFilter){
					if (((DimensionFilter)element).getOutputName() == null || ((DimensionFilter)element).getOutputName().equals(NONE)){
						ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();

						return reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY);
					}
				}
				
				return null;
			}
			
			
			
		});
		outCol.setEditingSupport(new EditingSupport(viewer){
			ComboBoxCellEditor editor = new ComboBoxCellEditor(viewer.getTree(), new String[]{}, SWT.READ_ONLY);
			@Override
			protected boolean canEdit(Object element) {
				return element instanceof DimensionFilter;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				List<String> l = new ArrayList<String>();
				l.add(NONE);
				for(Transformation t : transfo.getOutputs()){
					l.add(t.getName());
				}
				
				editor.setItems(l.toArray(new String[l.size()]));
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				if (element instanceof DimensionFilter){
					if (((DimensionFilter)element).getOutputName() == null || ((DimensionFilter)element).getOutputName().equals(NONE)){
						return 0;
					}
					else{
						for(int i = 1; i < editor.getItems().length; i++){
							if (((DimensionFilter)element).getOutputName() == editor.getItems()[i]){
								return i;
							}
						}
					}
				}
				
	
				return null;
			}

			@Override
			protected void setValue(Object element, Object value) {
				if (element instanceof DimensionFilter){
					((DimensionFilter)element).setOutputTransformation(editor.getItems()[(Integer)value]);
				
				}
				viewer.refresh();
			}
			
		});
		
	}
	
	private void createContextMenu(){
		MenuManager mgr = new MenuManager();
		mgr.add(new Action(Messages.OlapFilterSection_12){
			public void run(){
				InputDialog d = new InputDialog(getPart().getSite().getShell(),
					Messages.OlapFilterSection_13, Messages.OlapFilterSection_14, Messages.OlapFilterSection_15, null);
				
				if (d.open() == InputDialog.OK){
					DimensionFilter df = new DimensionFilter();
					df.setName(d.getValue());
					
					try{
						for(String s : transfo.getDocument().getOlapHelper().getLevelNames(true, transfo)){
							df.addLevel(s);
						}
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openWarning(getPart().getSite().getShell(), Messages.OlapFilterSection_16, Messages.OlapFilterSection_17 + ex.getMessage());
					}
					
					
					
					transfo.addDimensionFilter(df);
					viewer.refresh();
				}
				
			}
		});
		
		final Action deleteAction = new Action(Messages.OlapFilterSection_18){
			public void run(){
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				if (ss.getFirstElement() instanceof DimensionFilter){
					transfo.removeDimensionFilter((DimensionFilter)ss.getFirstElement());
				}
				
				viewer.refresh();
			}
		};
		
		mgr.add(deleteAction);
		
		mgr.addMenuListener(new IMenuListener(){

			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof DimensionFilter)){
					deleteAction.setEnabled(false);
				}
				else{
					deleteAction.setEnabled(true);
				}
				
			}
			
		});
	
		viewer.getControl().setMenu(mgr.createContextMenu(viewer.getControl()));
	}
	
	
	@Override
	public void refresh() {
		viewer.setInput(transfo.getDimensionFilters());
	}
	
	private class ClauseCellEditor extends DialogCellEditor{
		
		public ClauseCellEditor(Composite parent) {
			super(parent);
			
		}

		@Override
		protected Object openDialogBox(Control cellEditorWindow) {
			FilterClause fc = (FilterClause )((IStructuredSelection)viewer.getSelection()).getFirstElement();
			
			fc.getLevelName();
			
			DialogOlapLevelValues d = new DialogOlapLevelValues(cellEditorWindow.getShell(),
					transfo, fc.getParent(), fc.getLevelName());
			
			if (d.open() == DialogOlapLevelValues.OK){
				return d.getValue();
			}
			
			return null;
		}
		
	}
	
}
