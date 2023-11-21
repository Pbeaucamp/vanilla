package bpm.fd.design.ui.wizard.pages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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

import bpm.fd.design.ui.component.Messages;
import bpm.fd.repository.ui.dialogs.DialogItemSelection;
import bpm.vanilla.platform.core.IRepositoryApi;

public class DialogImportDictionary extends Dialog{
	

	private Button fileBt, repositoryBt;
	private Object selectedObject;
	
	private Composite selectionComposite;
	private Composite main;
	private Composite fileComposite;
	
//	private String importFileName;
	
	protected DialogImportDictionary(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout());
		
		
		fileBt = new Button(main, SWT.RADIO);
		fileBt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fileBt.setText(Messages.DialogImportDictionary_0);
		fileBt.setSelection(true);
		
		repositoryBt = new Button(main, SWT.RADIO);
		repositoryBt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		repositoryBt.setText(Messages.DialogImportDictionary_1);
		
		repositoryBt.setEnabled(bpm.fd.repository.ui.Activator.getDefault().getRepositoryConnection() != null);
		
		
		selectionComposite = new Composite(main, SWT.NONE);
		selectionComposite.setLayout(new GridLayout(3, false));
		selectionComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(selectionComposite, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogImportDictionary_2);
		
		final Text fName = new Text(selectionComposite, SWT.BORDER);
		fName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fName.setEnabled(false);
		
		Button b = new Button(selectionComposite, SWT.PUSH);
		b.setText(Messages.DialogImportDictionary_3);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (fileBt.getSelection()){
					FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
					fd.setFilterExtensions(new String[]{"*.dictionary", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$
					
					String path = fd.open();
					if (path != null){
						fName.setText(path);
						selectedObject = path;
						getButton(IDialogConstants.OK_ID).setEnabled(true);
					}
					else{
						fName.setText(""); //$NON-NLS-1$
						selectedObject = null;
						getButton(IDialogConstants.OK_ID).setEnabled(false);
					}
				}
				else if (repositoryBt.getSelection()){
					DialogItemSelection dial = new DialogItemSelection(getShell(), IRepositoryApi.FD_DICO_TYPE);
					
					if (dial.open() == DialogItemSelection.OK){
						selectedObject = dial.getDirectoryItem();
						fName.setText(dial.getDirectoryItem().getItemName());
						getButton(IDialogConstants.OK_ID).setEnabled(true);
					}
					else{
						selectedObject = null;
						fName.setText(""); //$NON-NLS-1$
						getButton(IDialogConstants.OK_ID).setEnabled(false);
					}
					
				}
				
				
			}
			
		});

		fileComposite = new Composite(selectionComposite, SWT.NONE);
		fileComposite.setLayout(new GridLayout(3, false));
		fileComposite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1));

		
//		l = new Label(fileComposite, SWT.NONE);
//		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l.setText("Save in dictionary in file");
//		
//		final Text saveFile = new Text(fileComposite, SWT.BORDER);
//		saveFile.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		saveFile.setEnabled(false);
//		
//		final Button b1 = new Button(fileComposite, SWT.PUSH);
//		b1.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
//		b1.setText("...");
//		b1.addSelectionListener(new SelectionAdapter(){
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
//				importFileName = fd.open() ;
//				saveFile.setText(importFileName != null ? importFileName : "");
//				
//			}
//			
//		});
		
//		SelectionListener lst = new SelectionAdapter(){
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				b1.setEnabled(repositoryBt.getSelection());
//			}
//			
//		};
		
//		repositoryBt.addSelectionListener(lst);
//		fileBt.addSelectionListener(lst);
		
		return main;
	}
	
		
	/**
	 * return a file Path or a IDirectoryItemObject
	 * @return
	 */
	public Object getSelectedObject(){
		return selectedObject;
	}
	
//	public String getImportFileName(){
//		return importFileName;
//	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogImportDictionary_8);
		getShell().setSize(400, 300);
	}
	
}
