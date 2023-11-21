package bpm.fd.design.ui.component.chart.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericOptions;
import bpm.fd.design.ui.component.Messages;

public class ChartGenericOptionsPage extends WizardPage{
	public static final String PAGE_NAME = "bpm.fd.design.ui.component.chart.pages.ChartGenericOptionPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.ChartGenericOptionsPage_1;
	public static final String PAGE_DESCRIPTION = Messages.ChartGenericOptionsPage_2;

	
	protected Text caption;
	protected Text subCaption;
	protected Button showLabels;
	protected Button showValues;
	protected IComponentOptions options;
	
	
	protected ChartGenericOptionsPage(String pageName) {
		super(pageName);
		
	}
	
	
	public ChartGenericOptionsPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	
	protected Composite createContent(Composite parent){
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ChartGenericOptionsPage_3);
		
		caption = new Text(main, SWT.BORDER);
		caption.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ChartGenericOptionsPage_4);
		
		subCaption = new Text(main, SWT.BORDER);
		subCaption.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		showLabels = new Button(main, SWT.CHECK);
		showLabels.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		showLabels.setText(Messages.ChartGenericOptionsPage_5);
		
		showValues = new Button(main, SWT.CHECK);
		showValues.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		showValues.setText(Messages.ChartGenericOptionsPage_6);
		
		
		return main;
	}
	
	public void createControl(Composite parent) {
		setControl(createContent(parent));
		fill();
	}
	
	public IComponentOptions getOptions(){
		if (getControl() == null || getControl().isDisposed()){
			return options;
		}
		GenericOptions opt = new GenericOptions();
		opt.setCaption(caption.getText());
		opt.setSubCaption(subCaption.getText());
		opt.setShowLabel(showLabels.getSelection());
		opt.setShowValues(showValues.getSelection());
		return opt;
	}
	
	public void setOptions(IComponentOptions options, ChartNature nature){
		this.options = options.getAdapter(nature);
			
	}
	
	protected void fill(){
		if (getControl() == null || getControl().isDisposed()){
			return;
		}
		caption.setText(((GenericOptions)options).getCaption());
		subCaption.setText(((GenericOptions)options).getSubCaption());
		showLabels.setSelection(((GenericOptions)options).isShowLabel());
		showValues.setSelection(((GenericOptions)options).isShowValues());
	}
}
