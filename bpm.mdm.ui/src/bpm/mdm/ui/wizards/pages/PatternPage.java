package bpm.mdm.ui.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.mdm.model.Rule;
import bpm.mdm.model.rules.PatternRule;
import bpm.mdm.ui.i18n.Messages;

public class PatternPage extends WizardPage implements IRulePage{

	private Text pattern;
	private Rule editedRule;
	public PatternPage(String pageName) {
		super(pageName);
		
	}

	public PatternPage(String pageName, Rule editedrule) {
		this(pageName);
		this.editedRule= editedrule;
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.PatternPage_0);
		
		pattern = new Text(main, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		
		pattern.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		setControl(main);
		fill();
	}
	private void fill(){
		if (editedRule != null){
			pattern.setText(((PatternRule)editedRule).getPattern());
		}
		
		pattern.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
		});
	}
	@Override
	public boolean isPageComplete() {
		
		return !pattern.getText().isEmpty();
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public void setRule(Rule rule) {
		((PatternRule)rule).setPattern(pattern.getText());
		
	}
	
}
