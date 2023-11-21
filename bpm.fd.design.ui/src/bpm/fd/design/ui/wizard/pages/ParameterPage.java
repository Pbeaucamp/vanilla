package bpm.fd.design.ui.wizard.pages;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.viewer.contentproviders.ListContentProvider;

public class ParameterPage extends WizardPage{
	
	private TableViewer viewer;
	private Composite main;
	
	public ParameterPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		main = new Composite(parent,SWT.NONE);
		main.setLayout(new GridLayout());
		createViewer();
		
		setControl(main);
		
	}
	
	
	private void createViewer(){

		viewer = new TableViewer(main, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(new ListContentProvider<ParameterDescriptor>());
		
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		TableViewerColumn nameCol = new TableViewerColumn(viewer, SWT.NONE);
		nameCol.getColumn().setText(Messages.ParameterPage_0);
		nameCol.getColumn().setWidth(100);
		nameCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				return ((ParameterDescriptor)element).getName();
			}
		});
		
		TableViewerColumn typeName = new TableViewerColumn(viewer, SWT.NONE);
		typeName.getColumn().setText(Messages.ParameterPage_1);
		typeName.getColumn().setWidth(200);
		typeName.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				return ((ParameterDescriptor)element).getTypeName();
			}
		});
		
		TableViewerColumn paramLabel = new TableViewerColumn(viewer, SWT.NONE);
		paramLabel.getColumn().setText(Messages.ParameterPage_2);
		paramLabel.getColumn().setWidth(200);
		paramLabel.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				return ((ParameterDescriptor)element).getLabel();
			}
		});
		
		paramLabel.setEditingSupport(new EditingSupport(viewer){
			TextCellEditor editor = new TextCellEditor(viewer.getTable());
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				return ((ParameterDescriptor)element).getLabel();
			}

			@Override
			protected void setValue(Object element, Object value) {
				((ParameterDescriptor)element).setLabel((String)value);
				viewer.refresh();
			}
			
		});

	}
	
	public void setInput(DataSet ds){
		viewer.setInput(ds.getDataSetDescriptor().getParametersDescriptors());
	}

	
	public void update(DataSet ds, List<ParameterDescriptor> originalPd){
		for(ParameterDescriptor pd : ds.getDataSetDescriptor().getParametersDescriptors()){
			
//			if (viewer != null && viewer.getInput() != null){
				for(ParameterDescriptor _pd : originalPd){//(List<ParameterDescriptor>)viewer.getInput()){
					if (pd.getName().equals(_pd.getName())){
						pd.setLabel(_pd.getLabel());
						break;
					}
				}
//			}
			
		}
	}
}
