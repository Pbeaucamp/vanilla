/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package bpm.fd.design.ui.css.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;


public class CssImportWizardPage extends WizardPage {
	
	protected FileFieldEditor editor;
	private Text newFileName;
	private String[] extensionFiles;
	
	public CssImportWizardPage(String pageName, String[] extensionFiles) {
		super(pageName);
		this.extensionFiles = extensionFiles;
		setTitle(pageName); //NON-NLS-1
		setDescription(Messages.CssImportWizardPage_0); //NON-NLS-1
	}

	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
	 */
	protected InputStream getInitialContents() {
		try {
			return new FileInputStream(new File(editor.getStringValue()));
		} catch (FileNotFoundException e) {
			return null;
		}
	}


	public void createControl(Composite parent) {
		final Composite fileSelectionArea = new Composite(parent, SWT.NONE);
		GridData fileSelectionData = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL);
		fileSelectionArea.setLayoutData(fileSelectionData);

		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 3;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		fileSelectionLayout.marginWidth = 0;
		fileSelectionLayout.marginHeight = 0;
		fileSelectionArea.setLayout(fileSelectionLayout);
		
		editor = new FileFieldEditor("fileSelect",Messages.CssImportWizardPage_2,fileSelectionArea); //NON-NLS-1 //NON-NLS-2 //$NON-NLS-1$
		editor.getTextControl(fileSelectionArea).addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				String s = editor.getTextControl(fileSelectionArea).getText();
				
			}
			
		});

		editor.setFileExtensions(extensionFiles);
		fileSelectionArea.moveAbove(null);
		
		Label l = new Label(fileSelectionArea, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CssImportWizardPage_3);
		
		newFileName = new Text(fileSelectionArea, SWT.BORDER);
		newFileName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		newFileName.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
		});
		
		setControl(fileSelectionArea);
		
	}


	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		FdProject project = Activator.getDefault().getProject();

		for(bpm.fd.api.core.model.resources.IResource r : project.getResources()){
			
			if (r.getName().equals(newFileName.getText())){
				setErrorMessage(Messages.CssImportWizardPage_4 + newFileName.getText() + Messages.CssImportWizardPage_5);
				return false;
			}
		}
		
		
		if (editor.getStringValue().equals("")){ //$NON-NLS-1$
			setErrorMessage(Messages.CssImportWizardPage_7);
			return false;
		}
		try{
			String ext = editor.getStringValue().substring(editor.getStringValue().lastIndexOf(".")); //$NON-NLS-1$
			if (newFileName.getText().equals("")){ //$NON-NLS-1$
				setErrorMessage(Messages.CssImportWizardPage_10);
				return false;
			}
			else if (!newFileName.getText().endsWith(ext)){
				
				setErrorMessage(Messages.CssImportWizardPage_11 + ext);
				return false;
			}
		}catch(Exception ex){
			String ext = editor.getStringValue().substring(editor.getStringValue().lastIndexOf(".")); //$NON-NLS-1$
			setErrorMessage(Messages.CssImportWizardPage_13 + ext);
			return false;
		}
		
		setErrorMessage(null);
		return true;
	}

	public IFile createNewFile() throws Exception{
		IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
		IWorkspaceRoot r = workspace.getRoot();
		IProject p = r.getProject(Activator.getDefault().getProject().getProjectDescriptor().getProjectName());
		;
		
		
		
		IFile f = null;
		
		if (!newFileName.getText().contains(".")){ //$NON-NLS-1$
			String ext = editor.getStringValue().substring(editor.getStringValue().lastIndexOf(".")); //$NON-NLS-1$
			f = p.getFile(newFileName.getText() + ext);
		}
		else{
			f = p.getFile(newFileName.getText());
		}
		
		
		try{
			FileInputStream fis = new FileInputStream(editor.getStringValue());
			f.create(fis, true, null);
		}catch(Exception e){
			e.printStackTrace();
		}
		
//		f.createLink(Platform.getLocation().append(f.getFullPath()), IResource.REPLACE, null);
		
		
		return f;
	}
	
}
