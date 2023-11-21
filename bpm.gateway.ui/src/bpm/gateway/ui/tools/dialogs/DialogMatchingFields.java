package bpm.gateway.ui.tools.dialogs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.ui.i18n.Messages;

public class DialogMatchingFields extends Dialog {
	
	private CheckboxTableViewer viewer;
	private StreamDescriptor left, right;
	private Button caseSensitive;
	
	private List<Point> checked;
	
	public DialogMatchingFields(Shell parentShell, StreamDescriptor left, StreamDescriptor right) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.left = left;
		this.right = right;
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		caseSensitive = new Button(container, SWT.CHECK);
		caseSensitive.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		caseSensitive.setText(Messages.DialogMatchingFields_0);
		caseSensitive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fillDatas();
			}
		});
		
		
		viewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		viewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				Point p = (Point)element;
				StreamElement l = left.getStreamElements().get(p.x);
				StreamElement r = right.getStreamElements().get(p.y);				
					
					return l.tableName + "." + l.name + "=" + r.tableName + "." + r.name; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			
		});
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Point> l = (List<Point>)inputElement;
				return l.toArray(new Point[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		return container;
	}

	
	private void fillDatas(){
		List<Point> pts = new ArrayList<Point>();
		try{
			for(int i = 0; i < left.getColumnCount(); i++){
				for(int k = 0; k < right.getColumnCount(); k++){
					if (caseSensitive.getSelection()){
						if (left.getStreamElements().get(i).name.toLowerCase().trim().equals(right.getStreamElements().get(k).name.toLowerCase().trim())){
							Point p = new Point(i,k);
							pts.add(p);
						}
					}
					else{
						if (left.getStreamElements().get(i).name.trim().equals(right.getStreamElements().get(k).name.trim())){
							Point p = new Point(i,k);
							pts.add(p);
						}
					}
					
				}
				
			}
			
		}catch(Exception e){
			
		}
				
		viewer.setInput(pts);
		viewer.setAllChecked(true);
	}


	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		fillDatas();
		
	}


	@Override
	protected void okPressed() {
		checked = new ArrayList<Point>();
		for(Object o : viewer.getCheckedElements()){
			checked.add((Point)o);
		}
		super.okPressed();
	}
	
	public List<Point> getChecked(){
		return checked;
	}
	
}
