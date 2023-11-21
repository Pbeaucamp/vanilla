package bpm.mdm.ui.diff;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.DiffResult;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.ui.i18n.Messages;

public class DiffComparisonPage implements IDetailsPage{
	private TableViewer datas;
	private IManagedForm form;
	private DiffMasterDetails master;

	
	public DiffComparisonPage(DiffMasterDetails master){
		this.master = master;
	}
	@Override
	public void createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		datas = new TableViewer(form.getToolkit().createTable(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL));
		datas.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		datas.getTable().setHeaderVisible(true);
		datas.getTable().setLinesVisible(true);
		datas.setContentProvider(new ArrayContentProvider());
		
		TableViewerColumn col = new TableViewerColumn(datas, SWT.LEFT);
		
		col.getColumn().setText(Messages.DiffComparisonPage_0);
		col.getColumn().setWidth(150);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				return ((String[])element)[0];
			}
		});
		
		col = new TableViewerColumn(datas, SWT.LEFT);
		col.getColumn().setText(Messages.DiffComparisonPage_1);
		col.getColumn().setWidth(150);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				return ((String[])element)[1];
			}
		});
		
		col = new TableViewerColumn(datas, SWT.LEFT);
		col.getColumn().setText(Messages.DiffComparisonPage_2);
		col.getColumn().setWidth(150);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				return ((String[])element)[2];
			}
		});;
	}

	@Override
	public void commit(boolean onSave) {
		
		
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void initialize(IManagedForm form) {
		this.form = form;
		
	}

	@Override
	public boolean isDirty() {
		
		return false;
	}

	@Override
	public boolean isStale() {
		
		return false;
	}

	@Override
	public void refresh() {
		
		
	}

	@Override
	public void setFocus() {
		
		
	}

	@Override
	public boolean setFormInput(Object input) {
		
		return false;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		DiffResult res = master.getDiff();
		Row row = (Row)((IStructuredSelection)selection).getFirstElement();
		
		Row baseRow = res.getOriginalRow(row);
		
		List<String[]> l = new ArrayList<String[]>();
		Entity entity = master.getEntity();
		
		for(Attribute a : entity.getAttributes()){
			String[] s = new String[3];
			s[0] = a.getName();
			if (baseRow != null){
				s[1] = baseRow.getValue(a) + ""; //$NON-NLS-1$
			}
			else{
				s[1] = ""; //$NON-NLS-1$
			}
			
			s[2] = row.getValue(a) + ""; //$NON-NLS-1$
			l.add(s);
		}
		datas.setInput(l);
	}
	
}
