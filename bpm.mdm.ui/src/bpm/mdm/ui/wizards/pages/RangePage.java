package bpm.mdm.ui.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.mdm.model.Rule;
import bpm.mdm.model.rules.RangeRule;
import bpm.mdm.ui.i18n.Messages;

public class RangePage extends WizardPage implements IRulePage{
	private final static String[] intervals = new String[]{
		"]min,max[","[min,max[","[min,max]","]min,max]" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	};
	
	private Text minVal, maxVal;
	private Combo intervalType;

	
	private Rule editedRule;
	public RangePage(String pageName) {
		super(pageName);
		
	}
	
	public RangePage(String pageName, Rule editedrule) {
		this(pageName);
		this.editedRule= editedrule;
	}

	@Override
	public void setRule(Rule rule) {
		((RangeRule)rule).setMaxValue(Long.parseLong(maxVal.getText()));
		((RangeRule)rule).setMinValue(Long.parseLong(minVal.getText()));
		switch(intervalType.getSelectionIndex()){
		case 0:
			((RangeRule)rule).setIncludeMax(false);
			((RangeRule)rule).setIncludeMin(false);
			break;
		case 1:
			((RangeRule)rule).setIncludeMax(false);
			((RangeRule)rule).setIncludeMin(true);
			break;
		case 2:
			((RangeRule)rule).setIncludeMax(true);
			((RangeRule)rule).setIncludeMin(true);
			break;
		case 3:
			((RangeRule)rule).setIncludeMax(true);
			((RangeRule)rule).setIncludeMin(false);
			break;
			
		}
		
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		setControl(main);
		
		Label lblMinimumValue = new Label(main, SWT.NONE);
		lblMinimumValue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMinimumValue.setText(Messages.RangePage_4);
		
		minVal = new Text(main, SWT.BORDER);
		minVal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		minVal.setText("0"); //$NON-NLS-1$
		
		
		Label lblMaximumvalue = new Label(main, SWT.NONE);
		lblMaximumvalue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMaximumvalue.setText(Messages.RangePage_6);
		
		maxVal = new Text(main, SWT.BORDER);
		maxVal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		maxVal.setText(Integer.MAX_VALUE  +""); //$NON-NLS-1$
		
		
		Label lblIntervalType = new Label(main, SWT.NONE);
		lblIntervalType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIntervalType.setText(Messages.RangePage_8);
		
		intervalType = new Combo(main, SWT.NONE);
		intervalType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		intervalType.setItems(intervals);
		intervalType.select(0);
		
		fill();
	}

	private void fill(){
		if (editedRule != null){
			
			maxVal.setText(((RangeRule)editedRule).getMaxValue()+""); //$NON-NLS-1$
			minVal.setText(((RangeRule)editedRule).getMinValue()+""); //$NON-NLS-1$
			
			if (((RangeRule)editedRule).isIncludeMax() && ((RangeRule)editedRule).isIncludeMin()){
				intervalType.select(2);
			}
			else if (((RangeRule)editedRule).isIncludeMax()){
				intervalType.select(3);
			}
			else if (((RangeRule)editedRule).isIncludeMin()){
				intervalType.select(1);
			}
			else{
				intervalType.select(0);
			}
		}
		
		minVal.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();

			}
		});
		maxVal.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();

			}
		});
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	
	@Override
	public boolean isPageComplete() {
		try{
			Integer.parseInt(minVal.getText());
			setErrorMessage(null);
		}catch(Exception ex){
			
			setErrorMessage(Messages.RangePage_11);
		}
		try{
			Integer.parseInt(maxVal.getText());
			setErrorMessage(null);
		}catch(Exception ex){
			setErrorMessage(Messages.RangePage_12);
		}
		
		return getErrorMessage() == null;
	}
}
