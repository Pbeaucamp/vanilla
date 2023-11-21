package bpm.vanilla.server.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.birep.admin.connection.AdminAccess;
import bpm.birep.admin.datas.vanilla.Group;

public class DialogGroupPickers extends Dialog{

	private String vanillaUrl;
	private CheckboxTableViewer viewer;
	private List<String> groups = new ArrayList<String>();
	
	public DialogGroupPickers(Shell parentShell, String vanillaUrl) {
		super(parentShell);
		this.vanillaUrl = vanillaUrl;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Select Groups");
		
		viewer = CheckboxTableViewer.newCheckList(main, SWT.BORDER);
		viewer.setLabelProvider(new LabelProvider());
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		return main;
	}

	public List<String> getGroups(){
		return groups;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		for(Object o : viewer.getCheckedElements()){
			groups.add((String)o);
		}
		super.okPressed();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText("Select Groups");
		
		AdminAccess a = new AdminAccess(vanillaUrl);
		List<String> l = new ArrayList<String>();
		try {
			for(Group g : a.getGroups()){
				l.add(g.getName());
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		viewer.setInput(l);
	}
	
	
}
