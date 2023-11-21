package metadata.client.model.dialog;

import java.util.ArrayList;
import java.util.List;

import metadata.client.i18n.Messages;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class DialogMultipleString extends Dialog{

	private List<String> values = new ArrayList<String>();
	private TableViewer viewer;
	
	
	public DialogMultipleString(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		
		
		Label l = new Label(parent, SWT.MULTI);
		l.setText(Messages.DialogMultipleString_0);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		viewer = new TableViewer(parent, SWT.MULTI | SWT.FULL_SELECTION  | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setLabelProvider(new LabelProvider());
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>)inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {}
			
			
		});
		
		
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		TableColumn col = new TableColumn(viewer.getTable(), SWT.NONE);
		col.setWidth(400);
		col.setText(Messages.DialogMultipleString_1);
		
		viewer.setColumnProperties(new String[]{"values"}); //$NON-NLS-1$
		
		
		final MenuManager menuMgr = new MenuManager();
        menuMgr.add(new Action(Messages.DialogMultipleString_3){
        	public void run(){
        		((List<String>)viewer.getInput()).add("newValue"); //$NON-NLS-1$
        		viewer.refresh();
        	}
        });
        
        menuMgr.add(new Action(Messages.DialogMultipleString_5){
        	public void run(){
        		IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
        		
        		for(Object o : ss.toList()){
        			((List)viewer.getInput()).remove(o);
        		}
        		viewer.refresh();
        		
        	}
        });
       
        viewer.getControl().setMenu(menuMgr.createContextMenu(viewer.getControl()));

        viewer.setCellModifier(new ICellModifier(){

			public boolean canModify(Object element, String property) {
				return true;
			}

			public Object getValue(Object element, String property) {
				return element;
			}

			public void modify(Object element, String property, Object value) {
				
				Object s = ((TableItem)element).getData();;
				List l = (List)viewer.getInput();
				int i = l.indexOf(s);
				l.set(i, value);
				viewer.refresh();
			}
        	
        });

        viewer.setCellEditors(new CellEditor[]{
				new TextCellEditor(viewer.getTable())});
		viewer.setInput(new ArrayList<String>());
		return viewer.getControl();
	}


	@Override
	protected void okPressed() {
		for(String s : (List<String>)viewer.getInput()){
			values.add(s);
		}
		super.okPressed();
	}
	
	
	public List<String> getValues(){
		return values;
	}


	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
	}
	

}
