package bpm.gateway.ui.resource.service.wizard;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.gateway.ui.composites.CalculatorComposite;
import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.beans.service.IService;

public class DialogServiceCalculation extends Dialog {

	private CalculatorComposite calcComposite;
	private List<IService> inputs;
	private IService selectedOutput;
	
	public DialogServiceCalculation(Shell parentShell, List<IService> inputs, IService selectedOutput) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.inputs = inputs;
		this.selectedOutput = selectedOutput;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		calcComposite = new CalculatorComposite(main, SWT.NONE, CalculatorComposite.TYPE_WEB);
		calcComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		return main;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogCalculationEditor_2);
		getShell().setSize(800, 600);
		
		if (inputs != null){
			calcComposite.setInput(inputs, selectedOutput);
		}
	}

	@Override
	protected void okPressed() {
		if(selectedOutput != null){
			selectedOutput.setValue(calcComposite.getScript());
		}
		super.okPressed();
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
//	 */
//	@Override
//	protected void createButtonsForButtonBar(Composite parent) {
//		Button bt = createButton(parent, IDialogConstants.BACK_ID, Messages.DialogCalculationEditor_21,true);
//		bt.addSelectionListener(new SelectionAdapter() {
//		@Override
//		public void widgetSelected(SelectionEvent e) {
//			try{
//				MessageDialog.openInformation(getShell(), Messages.DialogCalculationEditor_22, validateScript());
//			}catch(Exception ex){
//				MessageDialog.openError(getShell(), Messages.DialogCalculationEditor_23, ex.getMessage());
//				return;
//			}
//		}});
//		super.createButtonsForButtonBar(parent);
//		
//	}
}
