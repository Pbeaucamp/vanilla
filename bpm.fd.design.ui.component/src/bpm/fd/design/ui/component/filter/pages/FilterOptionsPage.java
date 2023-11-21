package bpm.fd.design.ui.component.filter.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.components.definition.filter.FilterOptions;
import bpm.fd.design.ui.component.Messages;

public class FilterOptionsPage extends WizardPage{

	public static final String PAGE_NAME = "bpm.fd.design.ui.component.filter.pages.FilterOptionsPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.FilterOptionsPage_1;
	public static final String PAGE_DESCRIPTION = Messages.FilterOptionsPage_2;

	
	private Text firstValueText;
	private Button submit;
	private Button defineFirst;
	private Button required;
	
	public FilterOptionsPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
			
	
	protected FilterOptionsPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		
		submit = new Button(main, SWT.CHECK);
		submit.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		submit.setText(Messages.FilterOptionsPage_3);
		submit.setSelection(true);
			
		
		defineFirst = new Button(main, SWT.CHECK);
		defineFirst.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		defineFirst.setText(Messages.FilterOptionsPage_4);
		defineFirst.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				firstValueText.setEnabled(defineFirst.getSelection());
			}
			
		});
		
		required = new Button(main, SWT.CHECK);
		required.setLayoutData(new GridData(GridData.FILL,GridData.CENTER, true, false, 2, 1));
		required.setText(Messages.FilterOptionsPage_6);
		required.setSelection(false);
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.FilterOptionsPage_5);
		
		firstValueText = new Text(main, SWT.BORDER);
		firstValueText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		firstValueText.setEnabled(false);
		
		setControl(main);
	}


	public FilterOptions getOptions() {
		FilterOptions opt = new FilterOptions();
		if (defineFirst.getSelection()){
			opt.setSelectFirstValue(true);
			opt.setDefaultValue(firstValueText.getText());
		}
		else{
			opt.setSelectFirstValue(false);
		}
		opt.setSubmitOnChange(submit.getSelection());
		opt.setRequired(required.getSelection());
		return opt;
	}

}
