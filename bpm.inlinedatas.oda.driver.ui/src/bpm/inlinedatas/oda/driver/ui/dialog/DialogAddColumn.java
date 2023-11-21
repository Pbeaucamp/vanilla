package bpm.inlinedatas.oda.driver.ui.dialog;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.inlinedatas.oda.driver.ui.model.ColumnsDescription;

//***** show dialog to specify column's name and type	

public class DialogAddColumn extends TitleAreaDialog {

	private String title, message, dialogMessage;
	private String tempName;
	@SuppressWarnings("unchecked")
	private Class tempType;

	private Text txtName;
	private Combo comboType;

	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		this.setMessage(message);
		this.setTitle(dialogMessage);
		this.getShell().setText(title);
		return contents;
	}

	protected Control createDialogArea(Composite parent) {

		tempType = null;
		tempName = "";

		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(1, false));


		Composite inComposite = new Composite(composite, SWT.NONE);
		inComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		inComposite.setLayout(new GridLayout(3, true));

		//--- Label + Text Column's Name
		Label labelName = new Label(inComposite, SWT.NONE);
		labelName.setText("Column's Name:");
		labelName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		txtName = new Text(inComposite, SWT.BORDER | SWT.SINGLE);
		txtName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

		txtName.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				
				tempName = txtName.getText();
				
				if(comboType.getSelectionIndex() == -1){
					tempType = null;
				}
				
				else{
					tempType = ColumnsDescription.TYPES_NAMES[comboType.getSelectionIndex()];
				}
				

				if(testFields(tempName)){
					getButton(OK).setEnabled(true);
				}
				else{
					getButton(OK).setEnabled(false);
				}

			}

		});



		//--- Label + Combo Column's Type	
		Label labelCombo = new Label(inComposite, SWT.NONE);
		labelCombo.setText("Data's Type:");
		labelCombo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		comboType = new Combo(inComposite, SWT.BORDER | SWT.READ_ONLY);;
		comboType.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));



		//--- Combo content and Listenner on items

		for(int i = 0; i < ColumnsDescription.TYPES_NAMES.length ; i++){
			comboType.add(ColumnsDescription.TYPES_NAMES[i].getSimpleName());
		}

		comboType.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {	
			}

			public void widgetSelected(SelectionEvent e) {
				
				tempName = txtName.getText();
				
				if(comboType.getSelectionIndex() == -1){
					tempType = null;
				}
				
				else{
					tempType = ColumnsDescription.TYPES_NAMES[comboType.getSelectionIndex()];
				}

				if(testFields(tempName)){
					getButton(OK).setEnabled(true);
				}
				else{
					getButton(OK).setEnabled(false);
				}
			}


		}
		);



		return composite;
	}

	public DialogAddColumn(Shell parentShell, String pTitle,
			String pMessage, String pDialogMessage) {
		super(parentShell);
		this.title = pTitle;
		this.message = pMessage;
		this.dialogMessage = pDialogMessage;
		this.setShellStyle(this.getShellStyle() | SWT.RESIZE);
	}


	//++++++ Method to test Fields

	public boolean testFields(String pName ){

		if(pName.length() == 0 || comboType.getText().length() == 0){
			this.setErrorMessage("Column's name of column's type missing.");	
			return false;
		}
		
		else if(txtName.getText().contains(";") || txtName.getText().contains("!") || txtName.getText().contains(":")){
			setErrorMessage("Caracters \" ; \" , \" ! \" and \" : \" are forbidden.");
			return false;
		}

		else{
			this.setErrorMessage(null);
			return true; 
		}

	}

	// Build the "ok" Button. this button is'nt enable until a correct Test
	protected void createButtonsForButtonBar(Composite parent) {
		this.createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);

		getButton(OK).setEnabled(false);

	}


	public String getTempName() {
		return tempName;
	}


	public Text getTxtName() {
		return txtName;
	}

	@SuppressWarnings("unchecked")
	public Class getTempType() {
		return tempType;
	}

	@SuppressWarnings("unchecked")
	public void setTempType(Class tempType) {
		this.tempType = tempType;
	}



}
