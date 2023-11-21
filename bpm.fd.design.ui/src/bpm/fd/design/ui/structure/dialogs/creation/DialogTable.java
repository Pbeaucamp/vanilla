package bpm.fd.design.ui.structure.dialogs.creation;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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

import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.tools.ColorManager;

public class DialogTable extends Dialog{
	
	private int cols, rows;
	
	private Text colNumber, rowNumber;
	private ModifyListener listener = new ModifyListener(){

		public void modifyText(ModifyEvent e) {
			try{
				Integer.parseInt(((Text)e.widget).getText());
				((Text)e.widget).setBackground(null);
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			}catch(NumberFormatException ex){
				((Text)e.widget).setBackground(ColorManager.getColorRegistry().get(ColorManager.COLOR_WRONG_VALUE_FIELD));
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			}
			
			
		}
		
	};
	
	public DialogTable(Shell parentShell) {
		super(parentShell);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogTable_0);
		
		colNumber = new Text(main, SWT.BORDER);
		colNumber.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		colNumber.setText("3"); //$NON-NLS-1$
		colNumber.addModifyListener(listener);
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.DialogTable_2);
		
		rowNumber = new Text(main, SWT.BORDER);
		rowNumber.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		rowNumber.setText("3"); //$NON-NLS-1$
		rowNumber.addModifyListener(listener);
		
		return main;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		cols = Integer.parseInt(colNumber.getText());
		rows = Integer.parseInt(rowNumber.getText());
		super.okPressed();
	}


	/**
	 * @return the cols
	 */
	public int getCols() {
		return cols;
	}


	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	
}
