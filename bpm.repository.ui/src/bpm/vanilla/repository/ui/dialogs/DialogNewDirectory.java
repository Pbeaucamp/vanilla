package bpm.vanilla.repository.ui.dialogs;

import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.repository.ui.Messages;


public class DialogNewDirectory extends Dialog {
	private Text itemName;
	
	private Text description;
	private ComboViewer groups;
	
	public static final String P_NAME = "name"; //$NON-NLS-1$
	public static final String P_DESCRIPTION = "desc"; //$NON-NLS-1$
	public static final String P_GROUP = "group"; //$NON-NLS-1$
	
	
	private List<Group> groupList;
	private Properties props = new Properties();
	
	public DialogNewDirectory(Shell parentShell, List<Group> groups) {
		super(parentShell);
		this.groupList = groups;
	}
	
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogNewDirectory_3);
		
		itemName = new Text(main, SWT.BORDER);
		itemName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		itemName.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				updateButtons();
				
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 3));
		l.setText(Messages.DialogNewDirectory_4);
		
		description = new Text(main, SWT.BORDER );
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 3));

	

		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogNewDirectory_5);
		
		groups = new ComboViewer(main, SWT.READ_ONLY);
		groups.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groups.getCombo().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtons();
			}
		});
		groups.setContentProvider(new ArrayContentProvider());
		groups.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		groups.setInput(groupList.toArray(new Group[groupList.size()]));
		return parent;
	}


	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogNewDirectory_6);
		getShell().setSize(400, 300);
		
	}
	
	private void updateButtons(){
		getButton(IDialogConstants.OK_ID).setEnabled(groups.getCombo().getSelectionIndex() != -1 && !"".equals(itemName.getText())); //$NON-NLS-1$
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	public Properties getProperties(){
		
		return props;
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		props = new Properties();
		props.setProperty(P_DESCRIPTION, description.getText());
		props.setProperty(P_NAME, itemName.getText());
		props.setProperty(P_GROUP, groups.getCombo().getText());
		
		super.okPressed();
	}
	
	
}
