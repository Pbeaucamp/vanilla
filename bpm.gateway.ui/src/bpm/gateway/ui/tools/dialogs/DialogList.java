package bpm.gateway.ui.tools.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorPart;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;

public class DialogList extends Dialog {

	
	private ListViewer viewer;
	
	private String value;
	
	public DialogList(Shell parentShell, String values) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.value = values;
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ToolBar toolbar = new ToolBar(container, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		ToolItem it = new ToolItem(toolbar, SWT.PUSH);
		it.setToolTipText(Messages.DialogList_0);
		it.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.add_16));
		it.addSelectionListener(new SelectionAdapter(){

			public void widgetSelected(SelectionEvent e) {
				IEditorPart ePart = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				
				GatewayEditorInput input = (GatewayEditorInput)ePart.getEditorInput();
				
				DialogPickupParameter d = new DialogPickupParameter(getShell(), input.getDocumentGateway().getParameters());
				if (d.open() == DialogPickupConstant.OK){
					List<String> l = ((List<String>)viewer.getInput());
					l.add(d.getValue());
					viewer.setInput(l);
					
				}
				
			}
			
		});
		
		
		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.DialogList_1);
		del.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.del_16));
		del.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				
				for(Object o : ss.toList()){
					((List)viewer.getInput()).remove(o);
					
				}
				
				viewer.refresh();
			}
			
		});
		
		viewer = new ListViewer(container, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				
				return (String)element;
			}
			
		});
		
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>)inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		
		List<String> l = new ArrayList<String>();
		
		if (value != null){
			for(String s : value.split("]")){ //$NON-NLS-1$
				l.add(s);
			}
		}
		
		viewer.setInput(l);
		
		return container;
	}
	
	public String getValue(){
		return value;
	}


	@Override
	protected void okPressed() {
		StringBuffer buf = new StringBuffer();
		
		for(String s : (List<String>)viewer.getInput()){
			if (!buf.toString().equals("")){ //$NON-NLS-1$
				buf.append("]"); //$NON-NLS-1$
			}
			buf.append(s);
		}
		
		value = buf.toString();
		
		super.okPressed();
	}


	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogList_5);
		getShell().setSize(400, 300);
	}

	
}
