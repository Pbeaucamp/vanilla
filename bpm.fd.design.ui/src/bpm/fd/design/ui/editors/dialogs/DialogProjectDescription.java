package bpm.fd.design.ui.editors.dialogs;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.design.ui.Messages;

public class DialogProjectDescription extends Dialog{

	private Text description;
	private Text author;
	private Text projectName;
	private Text projectVersion;
	private Text modelName;
	private Text dictionaryName;
	
	private File dictionaryFile;
	private FdProjectDescriptor desc;
	
	public DialogProjectDescription(Shell parentShell, FdProject project) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		desc = project.getProjectDescriptor();
	}

	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogProjectDescription_0);
		fillDatas();
	}

	
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		desc.setDescription(description.getText());
		desc.setAuthor(author.getText());
		//TODO : dictionary 
		
		super.okPressed();
	}



	private void fillDatas(){
		description.setText(desc.getDescription());
		author.setText(desc.getAuthor());
		projectName.setText(desc.getProjectName());
		projectVersion.setText(desc.getProjectVersion());
		modelName.setText(desc.getModelName());
		dictionaryName.setText(desc.getDictionaryName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite p) {
		Composite main = new Composite(p, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l3 = new Label(main, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(Messages.DialogProjectDescription_1); 
		
		projectName = new Text(main, SWT.BORDER);
		projectName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		projectName.setEnabled(false);
		
		Label l4 = new Label(main, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(Messages.DialogProjectDescription_2); 
		
		projectVersion = new Text(main, SWT.BORDER);
		projectVersion.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogProjectDescription_3);
		
		modelName = new Text(main, SWT.BORDER);
		modelName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		modelName.setEnabled(false);
		

		
		
		Label l6 = new Label(main, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(Messages.DialogProjectDescription_4); 
		
		dictionaryName = new Text(main, SWT.BORDER);
		dictionaryName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dictionaryName.addModifyListener(new ModifyListener(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e) {
				dictionaryFile = new File(dictionaryName.getText());
				if (!dictionaryFile.exists()){
					dictionaryFile = null;	
				}
				
//				updateButtons();
				
			}
			
		});
		
		
		Button b = new Button(main, SWT.NONE);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell());
				fd.setFilterExtensions(new String[]{"*.dictionary"}); //$NON-NLS-1$
				
				String s = fd.open();
				if (s != null){
					dictionaryFile = new File(s);
					if (dictionaryFile.exists()){
						dictionaryName.setText(s);
					}
					else{
						dictionaryFile = null;
					}
				}
				
//				updateButtons();
				
			}
			
		});
		
		
		Label l1 = new Label(main, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(Messages.DialogProjectDescription_7); 
		
		description = new Text(main, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(Messages.DialogProjectDescription_8); 
		
		author = new Text(main, SWT.BORDER);
		author.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		
		return main;
	}

	
	
}
