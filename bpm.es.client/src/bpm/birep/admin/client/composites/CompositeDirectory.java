package bpm.birep.admin.client.composites;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.content.views.ViewTree;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class CompositeDirectory extends Composite {

	private RepositoryDirectory directory;
	private Text name, comment, creation;
		
	private boolean modified = false;
	private Text directoryId;
	
	private Button showed;
	
	public CompositeDirectory(Composite parent, int style, RepositoryDirectory di) {
		super(parent, style);
		directory = di;

		this.setLayout(new GridLayout(2, true));
		
		org.eclipse.swt.widgets.Group gal = new org.eclipse.swt.widgets.Group(this, SWT.NONE);
		gal.setLayout(new GridLayout(2, false));
		gal.setText(Messages.CompositeDirectory_0);
		gal.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		Label l1 = new Label(gal, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(Messages.CompositeDirectory_1);
		
		name = new Text(gal, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				modified = true;
			}
		});
		
		Label _l = new Label(gal, SWT.NONE);
		_l.setLayoutData(new GridData());
		_l.setText(Messages.CompositeDirectory_2);
		
		showed = new Button(gal, SWT.CHECK);
		showed.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l4 = new Label(gal, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(Messages.CompositeDirectory_3);
		
		creation = new Text(gal, SWT.BORDER);
		creation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		creation.setEnabled(false);
		
		
		Label _l1 = new Label(gal, SWT.NONE);
		_l1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		_l1.setText(Messages.CompositeDirectory_4);
		
		directoryId = new Text(gal, SWT.READ_ONLY);
		directoryId.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		Label l5 = new Label(gal, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 4));
		l5.setText(Messages.CompositeDirectory_5);
		
		comment = new Text(gal, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		comment.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));
		comment.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				modified = true;
			}
		});
	
		Button ok = new Button(this, SWT.PUSH);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		ok.setText(Messages.CompositeDirectory_6);
		ok.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (modified){
					try {
						directory.setComment(comment.getText());
						directory.setName(name.getText());
						directory.setShowed(showed.getSelection());
						Activator.getDefault().getRepositoryApi().getRepositoryService().update(directory);
						
						ViewTree v = (ViewTree)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewTree.ID);
						if (v != null){
							v.refresh();
						}
						
					} catch (Exception e1) {
						MessageDialog.openError(getShell(), Messages.CompositeDirectory_7, e1.getMessage());
						e1.printStackTrace();
					}
					modified = false;
				}
				
				fillData();
			}
		});
		
		
		Button cancel = new Button(this, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		cancel.setText(Messages.CompositeDirectory_8);
		cancel.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				fillData();
			
				modified = false;
			}
		});

		fillData();
	}

	

	public void fillData(){
		name.setText(directory.getName());
		comment.setText(directory.getComment());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); //$NON-NLS-1$
		creation.setText(sdf.format(directory.getDateCreation()));
		directoryId.setText("" + directory.getId()); //$NON-NLS-1$
		showed.setSelection(directory.isShowed());
	}
	
	public void setDirectory(){
		directory.setName(name.getText());
		directory.setComment(comment.getText());
		directory.setDateCreation(directory.getDateCreation()!=null?directory.getDateCreation():Calendar.getInstance().getTime());
	}
}
