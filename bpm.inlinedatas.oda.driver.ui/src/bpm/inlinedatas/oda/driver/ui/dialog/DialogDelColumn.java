package bpm.inlinedatas.oda.driver.ui.dialog;

	import java.util.ArrayList;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
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

		
		public class DialogDelColumn extends TitleAreaDialog {

			private String title, message, dialogMessage;
			private String tempColDelName;
			
			protected ArrayList<ColumnsDescription> listeColToDel;
			
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
				
				tempColDelName = "";
				
				Composite composite = (Composite) super.createDialogArea(parent);
				composite.setLayout(new GridLayout(1, false));
				

				Composite inComposite = new Composite(composite, SWT.NONE);
				inComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
				inComposite.setLayout(new GridLayout(3, true));
			
			//--- Label + Combo Column's Type	
				Label labelCombo = new Label(inComposite, SWT.NONE);
				labelCombo.setText("Columns' List");
				labelCombo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
				
				comboType = new Combo(inComposite, SWT.BORDER | SWT.READ_ONLY);;
				comboType.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));

	
			//--- Combo content and Listenner on items
				
				for (ColumnsDescription current: listeColToDel){
					
					comboType.add(current.getColName());				
				}
				
				
				comboType.addSelectionListener(new SelectionListener(){

							public void widgetDefaultSelected(SelectionEvent e) {	
							}

							public void widgetSelected(SelectionEvent e) {
								tempColDelName = comboType.getText();
								
						 		getButton(OK).setEnabled(true);
							}
				 			
				 			
				 		}
				 		);
			
		
				 	
				return composite;
			}

			public DialogDelColumn(Shell parentShell, String pTitle,
					String pMessage, String pDialogMessage, ArrayList<ColumnsDescription> listeCol) {
				super(parentShell);
				this.title = pTitle;
				this.message = pMessage;
				this.dialogMessage = pDialogMessage;
				this.setShellStyle(this.getShellStyle() | SWT.RESIZE);
				
				listeColToDel = new ArrayList<ColumnsDescription>();
				listeColToDel.addAll(listeCol);
			}
			
		
		// Build the "ok" Button. this button is'nt enable until a correct Test
			protected void createButtonsForButtonBar(Composite parent) {
				this.createButton(parent, IDialogConstants.OK_ID,
						IDialogConstants.OK_LABEL, true);
				
				getButton(OK).setEnabled(false);
				
			}


			public Text getTxtName() {
				return txtName;
			}

			public String getTempColDelName() {
				return tempColDelName;
			}

			

		}
