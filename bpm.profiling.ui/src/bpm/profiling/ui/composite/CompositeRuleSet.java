package bpm.profiling.ui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.profiling.database.bean.RuleSetBean;

public class CompositeRuleSet extends Composite {

	private Text name;
	private Text description;
	private Combo logical; 
	private RuleSetBean ruleSet;
	
	
	public CompositeRuleSet(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(2, false));
		buildContent();
	}
	
	private void buildContent(){
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("RuleSet Name");
		
		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l2.setText("Description");
		
		description = new Text(this, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText("Logical Operator");
		
		logical = new Combo(this, SWT.READ_ONLY);
		logical.setItems(new String[]{"AND", "OR"});
		logical.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		

	}

	public void setInput(RuleSetBean bean){
		this.ruleSet = bean;
		name.setText(bean.getName());
		description.setText(bean.getDescription());
		logical.select(bean.getLogicalOperator());
		
	}
	
	public RuleSetBean getInput(){
		return this.ruleSet;
	}
	
	public void performChanges(){
		if (ruleSet == null){
			ruleSet = new RuleSetBean();
		}
		ruleSet.setName(name.getText());
		ruleSet.setDescription(description.getText());
		ruleSet.setLogicalOperator(logical.getSelectionIndex());
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		description.setEnabled(enabled);
		logical.setEnabled(enabled);
		name.setEnabled(enabled);
		
	}
	
	
}
