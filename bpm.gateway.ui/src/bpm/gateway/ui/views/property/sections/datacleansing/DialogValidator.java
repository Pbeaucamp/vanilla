package bpm.gateway.ui.views.property.sections.datacleansing;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.transformations.cleansing.ValidatorHelper;
import bpm.gateway.core.transformations.cleansing.ValidationCleansing.Validator;
import bpm.gateway.core.transformations.cleansing.ValidationCleansing.ValidatorType;
import bpm.gateway.ui.i18n.Messages;

public class DialogValidator extends Dialog{
	
	
	private Validator validator;

	private static Color RED = new Color(Display.getDefault(), 255, 0, 0);
	
	private static Color GREEN = new Color(Display.getDefault(), 0, 255, 0);
	
	private static Validator IP_ADDRESS = new Validator(
			"\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b",  //$NON-NLS-1$
			Messages.DialogValidator_2, 
			ValidatorType.Regex);
	
	
	private static Validator EMAIL_ADRESS = new Validator(
			"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", //$NON-NLS-1$
			Messages.DialogValidator_1,
			ValidatorType.Regex);
	

	private static Validator SIMPLE_DATE = new Validator(
			"yyyy-MM-dd", //$NON-NLS-1$
			Messages.DialogValidator_3,
			ValidatorType.Date
		);
	
	private static Validator SIMPLE_DATE_TIME_24 = new Validator(
			"yyyy-MM-dd HH:mm:ss", //$NON-NLS-1$
			Messages.DialogValidator_0,
			ValidatorType.Date
		);
	
	private static Validator SIMPLE_DATE_TIME_12 = new Validator(
			"yyyy-MM-dd hh:mm:ss", //$NON-NLS-1$
			Messages.DialogValidator_9,
			ValidatorType.Date
		);
	
	
	public static Validator[] PREDEFINED = new Validator[]{
		IP_ADDRESS, EMAIL_ADRESS, SIMPLE_DATE, SIMPLE_DATE_TIME_12, SIMPLE_DATE_TIME_24
	};
	
	
	
	private Button /*decimalFormat, */dateFormat, predefinedRegex, regex;
	private ComboViewer predefined;
	
	private Text format;
	private Text testField;
	private Label testResult;
	
	private Text tooltip; 
	
	public DialogValidator(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE) ;
	}
	
	public DialogValidator(Shell parentShell, Validator validator) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE) ;
		this.validator = validator;
		
	}
	
	
	private void fillData(){
		if (validator == null){
			return;
		}
		
		switch(validator.getType()){
		case Date:
			dateFormat.setSelection(true);
			predefinedRegex.setSelection(false);
			regex.setSelection(false);
			break;
		case Regex:
			boolean predf = false;
			for(Validator v : PREDEFINED){
				if (v == validator){
					predf = true;
					predefined.setSelection(new StructuredSelection(v));
					predefinedRegex.setSelection(true);
					regex.setSelection(false);
					format.setEnabled(false);
					break;
				}
			}
			
			if (!predf){
				predefinedRegex.setSelection(false);
				regex.setSelection(true);
			}
			
			format.setText(validator.getRegex());
			
		}
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		getShell().setText(Messages.DialogValidator_10);
		fillData();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		tooltip = new Text(main, SWT.MULTI  | SWT.WRAP);
		tooltip.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 3, 1));
		tooltip.setEnabled(false);
		
		Group type = new Group(main, SWT.NONE);
		type.setLayout(new GridLayout(2, false));
		type.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1));
		type.setText(Messages.DialogValidator_11);
		
//		decimalFormat = new Button(type, SWT.RADIO);
//		decimalFormat.setText("Decimal Format");
//		decimalFormat.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
//		decimalFormat.addSelectionListener(new SelectionAdapter(){
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				format.setEnabled(!predefinedRegex.getSelection());
//			}
//		});
	
		dateFormat = new Button(type, SWT.RADIO);
		dateFormat.setText(Messages.DialogValidator_12);
		dateFormat.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		dateFormat.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				format.setEnabled(!predefinedRegex.getSelection());
				tooltip.setText(Messages.DialogValidator_13);
				main.layout();
			}
		});
		
		predefinedRegex = new Button(type, SWT.RADIO);
		predefinedRegex.setText(Messages.DialogValidator_14);
		predefinedRegex.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		predefinedRegex.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				predefined.getControl().setEnabled(predefinedRegex.getSelection());
				format.setEnabled(!predefinedRegex.getSelection());
				tooltip.setText(Messages.DialogValidator_15);
				main.layout();
			}
		});
		
		
		
		predefined = new ComboViewer(type, SWT.READ_ONLY);
		predefined.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		predefined.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Object[] c = (Object[])inputElement;
				return c;
			}
		});
		predefined.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Validator)element).getName();
			}
		});
		predefined.getControl().setEnabled(false);
		predefined.setInput(PREDEFINED);
		predefined.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				Validator v = (Validator)((IStructuredSelection)predefined.getSelection()).getFirstElement();
				format.setText(v.getRegex());
				
			}
		});
		
		regex = new Button(type, SWT.RADIO);
		regex.setText(Messages.DialogValidator_16);
		regex.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		regex.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				format.setEnabled(!predefinedRegex.getSelection());
				tooltip.setText(Messages.DialogValidator_17);
				main.layout();
			}
		});
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		l.setText(Messages.DialogValidator_18);
		
		format = new Text(main, SWT.BORDER | SWT.FILL | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		format.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogValidator_19);
		
		testField = new Text(main, SWT.BORDER);
		testField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Button test = new Button(main, SWT.PUSH);
		test.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		test.setText(Messages.DialogValidator_20);
		test.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					if (ValidatorHelper.isValid(testField.getText(), format.getText(), getValidatorType())){
						testResult.setText(Messages.DialogValidator_21);
						testResult.setForeground(GREEN);
					}
					else{
						testResult.setText(Messages.DialogValidator_22);
						testResult.setForeground(RED);
					}
				}catch(Exception ex){
					testResult.setText(Messages.DialogValidator_23 + ex.getMessage());
					testResult.setForeground(RED);
				}
				
			
			}
		});
		
		testResult = new Label(main, SWT.NONE);
		testResult.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		

		return main;
	}

	
	private ValidatorType getValidatorType(){
		
		
		if (dateFormat.getSelection()){
			return ValidatorType.Date;
		}
//		else if (decimalFormat.getSelection()){
//			return ValidatorType.Decimal;
//		}
		else if (predefinedRegex.getSelection()){
			Validator v = (Validator)((IStructuredSelection)predefined.getSelection()).getFirstElement();
			if (v != null){
				return v.getType();
			}
		}
		
		return ValidatorType.Regex;
		
	}
	
	@Override
	protected void okPressed() {
		if (predefinedRegex.getSelection()){
			validator = (Validator)((IStructuredSelection)predefined.getSelection()).getFirstElement();
		}
		else{
			if (validator == null){
				validator = new Validator(format.getText(), "", getValidatorType()); //$NON-NLS-1$
			}
			validator.setType(getValidatorType().name());
			validator.setRegex(format.getText());
		}
		super.okPressed();
	}
	
	public Validator getValidator(){
		return validator;
	}
}
