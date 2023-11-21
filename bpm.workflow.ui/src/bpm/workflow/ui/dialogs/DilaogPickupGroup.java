package bpm.workflow.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.vanilla.platform.core.beans.Group;
import bpm.workflow.ui.Messages;

/**
 * Dialog for picking a group
 * @author Charles MARTIN
 *
 */
public class DilaogPickupGroup extends Dialog {
	
	private ComboViewer combo;
	private String selectedGroup;
	
	public DilaogPickupGroup(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DilaogPickupGroup_0);
		
		
		
		combo = new ComboViewer(parent, SWT.READ_ONLY);
		combo.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		combo.setContentProvider(new ArrayContentProvider());
		combo.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		combo.getCombo().addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedGroup = combo.getCombo().getText();
			}
		});
		
		try{
			List<Group> groups = bpm.workflow.ui.Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups();
			combo.setInput(groups.toArray(new Group[groups.size()]));
		}catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openWarning(getShell(), Messages.DilaogPickupGroup_1, e.getMessage());
		}
		
		return c;
	}


	@Override
	protected void okPressed() {
		
		super.okPressed();
	}
	
	/**
	 * 
	 * @return the selected group
	 */
	public String getSelectedGroup(){
		return selectedGroup;
	}
	
}
