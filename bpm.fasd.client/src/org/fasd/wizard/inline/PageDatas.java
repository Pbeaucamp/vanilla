package org.fasd.wizard.inline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.fasd.datasource.DataInline;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataRow;
import org.fasd.i18N.LanguageText;

public class PageDatas extends WizardPage {

	private TableViewer viewer;
	private Composite container;
	
	@Override
	protected boolean isCurrentPage() {
		return super.isCurrentPage();
	}

	protected PageDatas(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Button add = new Button(container, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		add.setText(LanguageText.PageDatas_Add_Row);
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DataRow r = new DataRow();
				DataObject t = ((InlineWizard)getWizard()).inlineTable;
				for(DataObjectItem it : t.getColumns())
					r.addValue(it.getName(), LanguageText.PageDatas_Undefined);
				
				t.getDatas().addRow(r);
				fillTable();
				getContainer().updateButtons();
			}
			
		});
		
		Button del = new Button(container, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		del.setText(LanguageText.PageDatas_Remove_Row);
		del.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				if (ss.isEmpty())
					return;
				
				DataRow row = (DataRow)ss.getFirstElement();
				DataObject t = ((InlineWizard)getWizard()).inlineTable;
				t.getDatas().removeRow(row);
				fillTable();
				
				getContainer().updateButtons();
			}
			
		});


		viewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		setControl(container);
	}

	public void buildTable() {
		DataObject table = ((InlineWizard)getWizard()).inlineTable;
		List<CellEditor> editors = new ArrayList<CellEditor>();
		List<String> colProp = new ArrayList<String>();
		
		for(TableColumn tc : viewer.getTable().getColumns())
			tc.dispose();
		
		for(DataObjectItem it : table.getColumns()){
			TableColumn column = new TableColumn(viewer.getTable(),SWT.NONE);
			column.setWidth(100);
			column.setText(it.getName());
			editors.add(new TextCellEditor(viewer.getTable()));
			colProp.add(it.getName());
		}
		
		viewer.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				DataRow row = (DataRow)element;
				
				TableColumn tc = viewer.getTable().getColumn(columnIndex);
				return row.getValue(tc.getText());
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
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				if (inputElement != null){
					return (DataRow[])inputElement;
				}
				return null;
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
		});
		viewer.setCellModifier(new ICellModifier(){

			public boolean canModify(Object element, String property) {
				return true;
			}

			public Object getValue(Object element, String property) {
				Object result = null;
				DataRow dataRow = (DataRow) element;
				result = dataRow.getValue(property);
		        return result;	
			}

			public void modify(Object element, String property, Object value) {
				DataRow dataRow = (DataRow)((TableItem)element).getData();
				dataRow.addValue(property, (String)value);
				viewer.refresh();
			}
			
		});
		
		viewer.setColumnProperties(colProp.toArray(new String[colProp.size()]));
		viewer.setCellEditors(editors.toArray(new CellEditor[editors.size()]));

		viewer.getTable().setHeaderVisible(true);
	}

	
	private void fillTable(){
		DataObject t = ((InlineWizard)getWizard()).inlineTable;
		DataInline datas = t.getDatas();
		
		viewer.setInput(datas.getRows().toArray(new DataRow[datas.getRows().size()]));
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public boolean isPageComplete() {
		if (viewer.getTable().getItemCount() > 0)
			return true;
		
		return false;
	}


}
