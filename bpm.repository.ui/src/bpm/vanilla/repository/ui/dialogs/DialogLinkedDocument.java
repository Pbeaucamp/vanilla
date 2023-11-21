package bpm.vanilla.repository.ui.dialogs;

import java.io.File;
import java.util.Collection;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.repository.ui.Messages;

public class DialogLinkedDocument extends Dialog{

	private Text displayName;
	private Text comment;
	private Text file;
	private Combo groups;
	private File document;
	private String _comment;
	private String display;
	private Collection<String> groupInput;
	private String groupSelected;

	public DialogLinkedDocument(Shell parentShell, Collection<String> groups) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.groupInput = groups;
	}


	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogLinkedDocument_0);
		
		displayName = new Text(main, SWT.BORDER);
		displayName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		displayName.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				updateButtons();
				
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogLinkedDocument_1);
		
		file = new Text(main, SWT.BORDER);
		file.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		file.setEnabled(false);
		
		Button b = new Button(main, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.setToolTipText(Messages.DialogLinkedDocument_3);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				fd.setFilterExtensions(IRepositoryApi.AVAILABLE_FORMATS);
				document = new File(fd.open());
				
				if (!document.exists()){
					document = null;
				}
				else{
					file.setText(document.getAbsolutePath());
					updateButtons();
				}
				
			}
		});
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogLinkedDocument_4);
		
		groups = new Combo(main, SWT.READ_ONLY );
		groups.setItems(groupInput.toArray(new String[groupInput.size()]));
		groups.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		if (!groupInput.isEmpty()){
			groups.select(0);
		}
		groups.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtons();
			}
		});
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l.setText(Messages.DialogLinkedDocument_5);
		
		comment = new Text(main, SWT.BORDER);
		comment.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		
		
		return main;
	}


	private void updateButtons(){
		if (document == null || "".equals(displayName.getText().trim()) || groups.getSelectionIndex() < 0){ //$NON-NLS-1$
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
		else{
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		}
	}
	
	@Override
	protected void okPressed() {
		display = displayName.getText();
		_comment = comment.getText();
		groupSelected = groups.getText();
		super.okPressed();
	}
	
	public String getGroup(){
		return groupSelected;
	}
	
	public File getFile(){
		return document;
	}
	
	public String getDisplayName(){
		return display;
	}
	
	public String getComment(){
		return _comment;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogLinkedDocument_7);
	}
}
