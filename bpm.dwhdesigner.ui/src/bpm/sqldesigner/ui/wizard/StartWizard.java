package bpm.sqldesigner.ui.wizard;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.sqldesigner.api.xml.SaveData;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.editor.save.SaveConstants;
import bpm.sqldesigner.ui.i18N.Messages;

public class StartWizard extends Wizard implements INewWizard{

	private class StartPage extends WizardPage {

//		private Text textFile;
//		private Button radioLoad;
		private Button radioSave;
		private Text textFileSave;
//		private List list;

		public StartPage(String pageName) {
			super(pageName);
			setTitle(Messages.StartWizard_0);
			setDescription(Messages.StartWizard_1);
		}

		
		public void createControl(Composite parent) {
			Composite compositeMain = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout(1, false);
			
			compositeMain.setLayout(layout);
			compositeMain.setLayoutData(new GridData(GridData.FILL_BOTH));
		
			Composite compositeSave = new Composite(compositeMain, SWT.NONE);
			compositeSave.setLayout(new GridLayout(1, false));
			compositeSave.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			radioSave = new Button(compositeSave, SWT.RADIO);
			radioSave.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			radioSave.setSelection(false);
			radioSave.setText(Messages.StartWizard_2);
			radioSave.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					radioSave.setSelection(true);

				}

			});

			Group groupSave = new Group(compositeSave, SWT.NONE);
			groupSave.setLayoutData(new GridData(GridData.FILL_BOTH));
			groupSave.setLayout(new GridLayout(3, false));
			groupSave.setText(Messages.StartWizard_3);

			Label labelSave = new Label(groupSave, SWT.NONE);
			labelSave.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			labelSave.setText(Messages.StartWizard_4);

			textFileSave = new Text(groupSave, SWT.BORDER);
			textFileSave.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

			Button fileBrowse2 = new Button(groupSave, SWT.NONE);
			fileBrowse2.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
			fileBrowse2.setImage(Activator.getDefault().getImageRegistry().get(
					"folder")); //$NON-NLS-1$
			fileBrowse2.addSelectionListener(new SelectionListener() {

				
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				
				public void widgetSelected(SelectionEvent e) {
					FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
					fd.setText(Messages.StartWizard_6);
					fd.setFilterPath("C:/"); //$NON-NLS-1$
					fd.setFilterExtensions(new String[] { "*." //$NON-NLS-1$
							+ SaveConstants.EXTENTION });
					String selected = fd.open();
					textFileSave.setText(selected);
				}

			});

			setControl(compositeMain);

		}
	}

	private StartPage startPage;

	public StartWizard() {
		startPage = new StartPage("OpenWorkspace"); //$NON-NLS-1$
		addPage(startPage);
	}

	
	public boolean performFinish() {
		if (startPage.radioSave.getSelection()) {

			String fileText = startPage.textFileSave.getText();

			if (SaveData.checkFile(fileText)) {
				boolean erase = MessageDialog
						.openQuestion(
								getShell(),
								Messages.StartWizard_10,
								Messages.StartWizard_11
										+ fileText
										+ Messages.StartWizard_12);
				if (!erase)
					return false;
			}

			Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().setText("SQL Designer - " + fileText); //$NON-NLS-1$
			
		} 
		else{
				return false;
		}
		return true;
	}

	
	public boolean performCancel() {
		Activator.getDefault().getWorkbench().close();
		return true;
	}


	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}

}