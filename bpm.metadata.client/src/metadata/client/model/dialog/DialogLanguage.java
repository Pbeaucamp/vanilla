package metadata.client.model.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DialogLanguage extends Dialog {

	private CheckboxTableViewer viewer;
	
	private List<Locale> locale =  new ArrayList<Locale>();
	
	public DialogLanguage(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
				
		viewer = new CheckboxTableViewer(c, SWT.BORDER | SWT.V_SCROLL);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Locale> l = (List<Locale>)inputElement;
				return l.toArray(new Locale[l.size()]);
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {}
			
		});
		viewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Locale)element).getDisplayName();

			}
			
		});
		viewer.setSorter(new ViewerSorter(){
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return ((Locale)e1).getDisplayName().compareTo(((Locale)e2).getDisplayName());
			}
		});
		setInput();
	
		return super.createDialogArea(parent);
	}
	
	private void setInput(){
		List<Locale> l = new ArrayList<Locale>();
		
		
		for(String s :Locale.getISOLanguages()){
			l.add(new Locale(s));
		}
		
		for(Locale c : Activator.getDefault().getModel().getLocales()){
			if (!l.contains(c)){
				l.add(c);
			}
		}
		
		viewer.setInput(l);
		
		for(Locale c : Activator.getDefault().getModel().getLocales()){
			if (l.contains(c)){
				viewer.setChecked(c, true);
			}
		}
	}


	@Override
	protected void okPressed() {
		for(Object l : (Object[])viewer.getCheckedElements()){
			locale.add((Locale)l);
		}
		super.okPressed();
	}

	
	public List<Locale> getLocales(){
		return locale;
	}


	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogLanguage_0); //$NON-NLS-1$
		getShell().setSize(400, 300);
	}
}
