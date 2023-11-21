package bpm.vanilla.repository.ui.dialogs;

import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.repository.ui.Messages;


public class DialogNewItem extends Dialog {
	private Text itemName;
	private Text author;
	private Text description;
	private Text version;
	private CheckboxTableViewer table;
	
	public static final String P_NAME = "name"; //$NON-NLS-1$
	public static final String P_AUTHOR = "author"; //$NON-NLS-1$
	public static final String P_DESCRIPTION = "desc"; //$NON-NLS-1$
	public static final String P_VERSION = "version"; //$NON-NLS-1$
	public static final String P_GROUP = "group"; //$NON-NLS-1$
	
	
	private List<Group> groupList;
	private Properties props = new Properties();
	public DialogNewItem(Shell parentShell, List<Group> groups) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.groupList = groups;
	}
	
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogNewItem_5);
		
		itemName = new Text(main, SWT.BORDER);
		itemName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		itemName.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				updateButton();
				
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogNewItem_6);
		
		description = new Text(main, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogNewItem_7);
		
		version = new Text(main, SWT.BORDER);
		version.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogNewItem_8);
		
		author = new Text(main, SWT.BORDER);
		author.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 2, 1));
		l.setText(Messages.DialogNewItem_9);
		
		table = new CheckboxTableViewer(main, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		table.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		table.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		table.setContentProvider(new ArrayContentProvider());
		table.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateButton();
				
			}
		});
		table.setInput(groupList);
		
		
		return parent;
	}


	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogNewItem_10);
		getShell().setSize(400, 300);
		
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
		props.setProperty(P_AUTHOR, author.getText());
		props.setProperty(P_NAME, itemName.getText());
		props.setProperty(P_VERSION, version.getText());
		
		boolean first = true;
		for(Object o : table.getCheckedElements()){
			if (first){
				props.setProperty(P_GROUP, ((Group)o).getName());
				first = false;
			}
			else{
				props.setProperty(P_GROUP, props.getProperty(P_GROUP) + ";" + ((Group)o).getName()); //$NON-NLS-1$
			}
			
		}
		
		
		super.okPressed();
	}
	
	private void updateButton(){
		boolean enabled = table.getCheckedElements().length > 0;
		enabled = enabled && !itemName.getText().isEmpty();
		
		getButton(IDialogConstants.OK_ID).setEnabled(enabled);
	}
	
}
