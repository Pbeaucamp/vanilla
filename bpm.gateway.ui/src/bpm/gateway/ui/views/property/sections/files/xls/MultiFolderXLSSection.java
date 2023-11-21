package bpm.gateway.ui.views.property.sections.files.xls;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.file.FileXLSHelper;
import bpm.gateway.core.transformations.outputs.MultiFolderXLS;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class MultiFolderXLSSection extends AbstractPropertySection {
	
	
	
	
	
	/*
	 * models
	 */
	
	private Node node;
	private MultiFolderXLS transfo;
	private TableViewer viewer;
	private Button header, append, delete;
		
	private ComboBoxCellEditor sheetNameEditor;
	private List<String> sheetNames;
	
	
	private SelectionListener optsListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource() == header){
				transfo.setIncludeHeader(header.getSelection());
			}
			else if (e.getSource() == append){
				transfo.setAppend(append.getSelection());
			}
			else if (e.getSource() == delete){
				transfo.setDelete(delete.getSelection());
			}
		}
	};
	
	
	public MultiFolderXLSSection() {
		
	}
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout());
		
		header = getWidgetFactory().createButton(composite, Messages.MultiFolderXLSSection_0, SWT.CHECK);
		header.setLayoutData(new GridData());
		
		delete = getWidgetFactory().createButton(composite, Messages.MultiFolderXLSSection_1, SWT.CHECK);
		delete.setLayoutData(new GridData());
		
		
		append = getWidgetFactory().createButton(composite, Messages.MultiFolderXLSSection_2, SWT.CHECK);
		append.setLayoutData(new GridData());
		
		viewer = new TableViewer(getWidgetFactory().createTable(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION ));
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer.setContentProvider(new ArrayContentProvider());
		
		
		sheetNameEditor = new ComboBoxCellEditor(viewer.getTable(), new String[]{});
		
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.MultiFolderXLSSection_3);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Transformation)element).getName();
			}
		});
		
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.MultiFolderXLSSection_4);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				String s = transfo.getSheetName(((Transformation)element));
				if (s == null){
					return ""; //$NON-NLS-1$
				}
				return s;
			}
		});
		
		col.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				
				if (value instanceof Integer){
					Integer i = (Integer)value;
					
					if (i < 0){
						String s = null;
						if (sheetNameEditor.getControl() instanceof CCombo ){
							s = ((CCombo)sheetNameEditor.getControl()).getText();
						}
						else if (sheetNameEditor.getControl() instanceof Combo){
							s = ((Combo)sheetNameEditor.getControl()).getText();
						}
						try{
							if (s != null && !s.trim().isEmpty()){
								transfo.adaptInput(((Transformation)element).getName(), s);
							}
							else{
								transfo.adaptInput(((Transformation)element).getName(), null);
							}
						}catch(Exception ex){
							ex.printStackTrace();
						}
						
					}
					else{
						try{
							transfo.adaptInput(((Transformation)element).getName(), sheetNameEditor.getItems()[i]);
						}catch(Exception ex){
							try {
								transfo.adaptInput(((Transformation)element).getName(), null);
							} catch (Exception e) {
								
								e.printStackTrace();
							}
						}
					}
					
					
				}
				else if (value instanceof String){
					try {
						transfo.adaptInput(((Transformation)element).getName(), (String)value);
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				}
				viewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				String s = transfo.getSheetName((Transformation)element);
				
				if (s == null){
					return -1;
				}
				for(int i = 0; i < sheetNameEditor.getItems().length; i++){
					if (sheetNameEditor.getItems()[i].equals(s)){
						return i;
					}
				}
				return -1;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return sheetNameEditor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
        this.transfo = (MultiFolderXLS)node.getGatewayModel();
	}
	
	
	@Override
	public void refresh() {
		
		
		
		
		List<String> sheetNames = new ArrayList<String>();
		
		try{
			if (transfo.getDefinition() != null && new File(transfo.getDefinition()).exists()){
				sheetNames.addAll(FileXLSHelper.getWorkSheetsNames(new FileInputStream(transfo.getDefinition())));
			}
			
		}catch(Exception ex){
			
		}
		
		for(String s  : transfo.getSheetNames()){
			if (Collections.binarySearch(sheetNames, s)< 0 ){
				sheetNames.add(s);
			}
			
		}
		
		sheetNameEditor.setItems(sheetNames.toArray(new String[sheetNames.size()]));
		viewer.setInput(transfo.getInputs());
		
		
		/*
		 * options controls
		 */
		header.removeSelectionListener(optsListener);
		delete.removeSelectionListener(optsListener);
		append.removeSelectionListener(optsListener);
		
		header.setSelection(transfo.isIncludeHeader());
		append.setSelection(transfo.isAppend());
		delete.setSelection(transfo.isDelete());
		
		header.addSelectionListener(optsListener);
		delete.addSelectionListener(optsListener);
		append.addSelectionListener(optsListener);

		
	}
	
	
	
	
	@Override
	public void aboutToBeShown() {
//		node.addPropertyChangeListener(listenerConnection);
		super.aboutToBeShown();
	}


	@Override
	public void aboutToBeHidden() {
		if (node != null){
//			node.removePropertyChangeListener(listenerConnection);
		}
		super.aboutToBeHidden();
	}

	
	
	
}
