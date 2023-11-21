package bpm.profiling.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.profiling.database.bean.RuleSetBean;
import bpm.profiling.runtime.core.Condition;
import bpm.profiling.ui.Activator;
import bpm.profiling.ui.composite.CompositeCondition;

public class DialogCondition extends Dialog {

	private CompositeCondition composite;
	private RuleSetBean ruleSet;
	private List<Condition> inputs;
	
	
	public DialogCondition(Shell parentShell, RuleSetBean ruleSet) {
		super(parentShell);
		this.ruleSet = ruleSet;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);;
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText("add condition");
		add.setImage(Activator.getDefault().getImageRegistry().get("add"));
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Condition c = new Condition();
				c.setRuleSetId(ruleSet.getId());
				try {
					Activator.helper.getAnalysisManager().createCondition(c);
					inputs.add(c);
					composite.setContent(inputs);
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), "Error while creating condition", e1.getMessage());
				}
			}
			
		});
		
		
		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText("remove condition");
		del.setImage(Activator.getDefault().getImageRegistry().get("delete"));
		del.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				Condition c = composite.getSelectedCondition();
				if (c == null){
					return;
				}
				try {
					Activator.helper.getAnalysisManager().deleteCondition(c);
					inputs.remove(c);
					composite.setContent(inputs);
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), "Error while deleting condition", e1.getMessage());
				}
			}
		});
		
		composite = new CompositeCondition(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return composite;
	}

	

	
	private void setDatas(){
		inputs = Activator.helper.getAnalysisManager().getConditionForRuleSet(ruleSet);
		composite.setContent(inputs);
	}
	
	@Override
	protected void okPressed() {
		super.okPressed();
	}
	
	@Override
	protected void initializeBounds() {
		setShellStyle(getShellStyle() | SWT.RESIZE);
		getShell().setSize(400, 300);
		getShell().setText("Tag content");
		
		setDatas();
	}
}
