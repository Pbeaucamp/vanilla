package bpm.fd.design.ui.editors.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;

public class DialogLocale extends Dialog{
	
	private CheckboxTableViewer viewer;
	private List<Locale> locales = new ArrayList<Locale>();
	
	public DialogLocale(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		viewer = new CheckboxTableViewer(parent, SWT.BORDER  | SWT.V_SCROLL);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				return (Locale[])inputElement;
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		viewer.setLabelProvider(new LabelProvider());
		viewer.addFilter(new ViewerFilter(){

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				for(String s : Activator.getDefault().getProject().getLocale()){
					if (element.toString().equals(s)){
						return false;
					}
				}
				return true;
			}
			
		});
		viewer.setInput(Locale.getAvailableLocales());
		
		return viewer.getControl();
	}

	public List<Locale> getLocale(){
		return locales;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogLocale_0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		for(Object o : viewer.getCheckedElements()){
			locales.add((Locale)o);
		}
		super.okPressed();
	}
	
	

}
