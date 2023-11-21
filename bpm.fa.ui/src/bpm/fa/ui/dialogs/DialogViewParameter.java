package bpm.fa.ui.dialogs;



import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.repository.RepositoryCubeView;

public class DialogViewParameter extends Dialog{
	

	private OLAPCube cube;
	private RepositoryCubeView view;
	public DialogViewParameter(Shell parentShell,OLAPCube cube, RepositoryCubeView view) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.cube = cube;
		this.view = view;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		for(Parameter p : view.getParameters()){
			
			Label l = new Label(main, SWT.NONE);
			l.setLayoutData(new GridData());
			l.setText(p.getName());
			
			ComboViewer viewer = new ComboViewer(main, SWT.READ_ONLY);
			viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			viewer.setContentProvider(new ArrayContentProvider());
			viewer.setLabelProvider(new LabelProvider(){
				@Override
				public String getText(Object element) {
					String s = (String)element;
					
					try{
						int i = s.lastIndexOf(".[");
						return s.substring(i + 2, s.lastIndexOf("]"));
					}catch (Exception e) {
						// TODO: handle exception
					}
					
					
					return super.getText(element);
				}
			});
			
			final Parameter _p = p;
			viewer.addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					String val = (String)((IStructuredSelection)event.getSelection()).getFirstElement();
					_p.setValue(val);
					_p.setUname(val);
					try{
						int i = val.lastIndexOf(".[");
						_p.setValue(val.substring(i + 2, val.lastIndexOf("]")));
					}catch (Exception e) {
						// TODO: handle exception
					}
				}
			});
			
			viewer.setInput(cube.getParametersValues(p.getLevel()));
		}
		
		
		return main;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,true);
	}
	
}
