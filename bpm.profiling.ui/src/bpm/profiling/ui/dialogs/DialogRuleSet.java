package bpm.profiling.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.profiling.database.bean.RuleSetBean;
import bpm.profiling.ui.composite.CompositeRuleSet;

public class DialogRuleSet extends Dialog {
	private CompositeRuleSet composite;
	private RuleSetBean ruleSet;
	
	public DialogRuleSet(Shell parentShell) {
		super(parentShell);
		
	}
	
	
	public DialogRuleSet(Shell parentShell, RuleSetBean ruleSet) {
		super(parentShell);
		this.ruleSet = ruleSet;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new CompositeRuleSet(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return composite;
	}

	@Override
	protected void okPressed() {
		composite.performChanges();
		ruleSet = composite.getInput();
		
		
		super.okPressed();
	}
	
	
	public RuleSetBean getRuleSetBean(){
		return ruleSet;
	}


	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		getShell().setText("Rule Set Definition");
		if (ruleSet != null){
			composite.setInput(ruleSet);
		}
	}

}
