package bpm.metadata.ui.query.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.query.QuerySql;
import bpm.metadata.resource.Prompt;
import bpm.metadata.ui.query.i18n.Messages;

public class DialogQueryPrompt extends Dialog{
	
	
	
	private QuerySql query;
	private TableViewer viewer;
	private Map<Prompt, List<String>> datas = new HashMap<Prompt, List<String>>();
	
	public DialogQueryPrompt(Shell parentShell, QuerySql query, Map<Prompt, List<String>> values) {
		super(parentShell);
		this.query = query;
		if (values != null){
			datas = values;
		}
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		viewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		viewer .getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer .getTable().setHeaderVisible(true);
		viewer .getTable().setLinesVisible(true);
		viewer.setContentProvider(new ArrayContentProvider());
		
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE) ;
		col.getColumn().setText(Messages.DialogQueryPrompt_0);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Prompt)element).getOutputName();
			}
		});
		
		col = new TableViewerColumn(viewer, SWT.NONE) ;
		col.getColumn().setText(Messages.DialogQueryPrompt_1);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				List<String> l = datas.get((Prompt)element);
				if (l == null){
					return ""; //$NON-NLS-1$
				}
				StringBuffer buf = new StringBuffer();
				for(String s : l){
					if (buf.length() > 0){
						buf.append(","); //$NON-NLS-1$
					}
					buf.append(s);
				}
				return buf.toString();
			}
		});
		col.setEditingSupport(new EditingSupport(viewer) {
			TextCellEditor ed = new TextCellEditor(viewer.getTable(), SWT.NONE);
			@Override
			protected void setValue(Object element, Object value) {
				List l = datas.get((Prompt)element);
//				if (l == null){
					l = new ArrayList<String>();
					datas.put((Prompt)element, l);
//				}
				l.add((String)value);
				viewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				List l = datas.get((Prompt)element);
				if (l != null && !l.isEmpty()){
					return l.get(0);
				}
				return ""; //$NON-NLS-1$
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return ed;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		
		return viewer.getTable();
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		viewer.setInput(query.getPrompts());
	}
	
	public Map<Prompt, List<String>> getValues(){
		return datas;
	}
}
