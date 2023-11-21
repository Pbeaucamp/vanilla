package bpm.forms.design.ui.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.forms.core.design.IForm;
import bpm.forms.design.ui.Messages;
import bpm.forms.design.ui.tools.converters.DateToTextConverter;
import bpm.forms.design.ui.tools.converters.TextToDateConverter;

public class CompositeForm {
	
	private Text name;
	
	private Text lifeExpectancyHours;
	private Text lifeExpectancyDays;
	private Text lifeExpectancyMonths;
	private Text lifeExpectancyYears;
	
	
	private Text description;
	private Text creation;
	
//	private Text creator;
//	private Text id;
	private FormToolkit toolkit;
	
	private Composite client;
	
	private IForm form;
	private DataBindingContext bindingCtx = new DataBindingContext();
	private List<IObservableValue> listened = new ArrayList<IObservableValue>();
	private IChangeListener changeListener = new IChangeListener() {
		
		@Override
		public void handleChange(ChangeEvent event) {
			if (getClient() != null && !getClient().isDisposed()){
				getClient().notifyListeners(SWT.Modify, new Event());
			}
			
			
		}
	};
	
	
	public CompositeForm(FormToolkit toolkit){
		this.toolkit = toolkit;
	}
	
	
	
	public Composite getClient(){
		return client;
	}
	
	public void createContent(Composite parent){
		if (toolkit == null ){
			toolkit = new FormToolkit(Display.getDefault());
		}
		
		client = toolkit.createComposite(parent, SWT.NONE);
		client.setLayout(new GridLayout(2, false));
		client.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();		
				toolkit = null;
			}
		});
		
		
		Label l = toolkit.createLabel(client, Messages.CompositeForm_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		name = toolkit.createText(client, "", SWT.BORDER); //$NON-NLS-1$
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		
		
		/*
		 * time life panel
		 */
		
		l = toolkit.createLabel(client, Messages.CompositeForm_2);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		lifeExpectancyHours = toolkit.createText(client, "", SWT.BORDER); //$NON-NLS-1$
		lifeExpectancyHours.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		
		l = toolkit.createLabel(client, Messages.CompositeForm_4);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		lifeExpectancyDays = toolkit.createText(client, "", SWT.BORDER); //$NON-NLS-1$
		lifeExpectancyDays.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		l = toolkit.createLabel(client, Messages.CompositeForm_6);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		lifeExpectancyMonths = toolkit.createText(client, "", SWT.BORDER); //$NON-NLS-1$
		lifeExpectancyMonths.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		l = toolkit.createLabel(client, Messages.CompositeForm_8);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		lifeExpectancyYears = toolkit.createText(client, "", SWT.BORDER); //$NON-NLS-1$
		lifeExpectancyYears.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		l = toolkit.createLabel(client, Messages.CompositeForm_10);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		
		description = toolkit.createText(client, "", SWT.BORDER | SWT.MULTI | SWT.WRAP| SWT.V_SCROLL); //$NON-NLS-1$
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
	
		
		l = toolkit.createLabel(client, Messages.CompositeForm_12);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		creation = toolkit.createText(client, "", SWT.BORDER | SWT.MULTI | SWT.WRAP| SWT.V_SCROLL); //$NON-NLS-1$
		creation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		creation.setEnabled(false);
	}


	public void setInput(IForm form){
		this.form = form;
		
		//XXX : should be replaced by XmlForm once DAO is removed from dependancies 
		
		if (bindingCtx != null){
			bindingCtx.dispose();
			bindingCtx = null;
		}
		bindValues();

	}
	
	
	public IForm getInput(){
		return form;
	}
	
	private void bindValues(){
		if (bindingCtx != null){
			bindingCtx.dispose();
		}
		for(IObservableValue o : listened){
			o.removeChangeListener(changeListener);
			if (!o.isDisposed()){
				o.dispose();
			}
		}
		listened.clear();
		
		bindingCtx = new DataBindingContext();
		/*
		 * name
		 */
		IObservableValue myModel = PojoProperties.value(IForm.class, "name").observe(form); //$NON-NLS-1$
		IObservableValue myWidget = WidgetProperties.text(SWT.Modify).observe(name);
		bindingCtx.bindValue(myWidget, myModel);
		myWidget.addChangeListener(changeListener);
		listened.add(myWidget);
		
		
	
		
		/*
		 * description
		 */
		myModel = PojoProperties.value(IForm.class, "description").observe(form); //$NON-NLS-1$
		myWidget = WidgetProperties.text(SWT.Modify).observe(description);
		bindingCtx.bindValue(myWidget, myModel);
		myWidget.addChangeListener(changeListener);
		listened.add(myWidget);
		
		/*
		 * creation
		 */
		myModel = PojoProperties.value(IForm.class, "creationDate").observe(form); //$NON-NLS-1$
		myWidget = WidgetProperties.text(SWT.Modify).observe(creation);
		listened.add(myWidget);
		
		UpdateValueStrategy strat = new UpdateValueStrategy();
		strat.setConverter(new TextToDateConverter("yyyy-MM-dd")); //$NON-NLS-1$
		
		UpdateValueStrategy strat2 = new UpdateValueStrategy();
		strat2.setConverter(new DateToTextConverter("yyyy-MM-dd")); //$NON-NLS-1$
		
		bindingCtx.bindValue(myWidget, myModel, strat, strat2);
		myWidget.addChangeListener(changeListener);
		
		
		/*
		 * ifeExpectancy
		 */
		strat = new UpdateValueStrategy();
		strat.setConverter(new IntegerConverter());
		strat.setBeforeSetValidator(new IntegerValidator(createDecorator(lifeExpectancyHours, LIFE_EXPECTANCY_SYNTAXE_ERROR_MESSAGE)));
		
		
		myModel = PojoProperties.value(IForm.class, "lifeExpectancyHours").observe(form); //$NON-NLS-1$
		myWidget = WidgetProperties.text(SWT.Modify).observe(lifeExpectancyHours);
		bindingCtx.bindValue(myWidget, myModel, strat, null);
		myWidget.addChangeListener(changeListener);
		listened.add(myWidget);
		
		strat = new UpdateValueStrategy();
		strat.setConverter(new IntegerConverter());
		strat.setBeforeSetValidator(new IntegerValidator(createDecorator(lifeExpectancyDays, LIFE_EXPECTANCY_SYNTAXE_ERROR_MESSAGE)));

		
		myModel = PojoProperties.value(IForm.class, "lifeExpectancyDays").observe(form); //$NON-NLS-1$
		myWidget = WidgetProperties.text(SWT.Modify).observe(lifeExpectancyDays);
		bindingCtx.bindValue(myWidget, myModel, strat, null);
		myWidget.addChangeListener(changeListener);
		listened.add(myWidget);
		
		strat = new UpdateValueStrategy();
		strat.setConverter(new IntegerConverter());
		strat.setBeforeSetValidator(new IntegerValidator(createDecorator(lifeExpectancyMonths, LIFE_EXPECTANCY_SYNTAXE_ERROR_MESSAGE)));

		myModel = PojoProperties.value(IForm.class, "lifeExpectancyMonths").observe(form); //$NON-NLS-1$
		myWidget = WidgetProperties.text(SWT.Modify).observe(lifeExpectancyMonths);
		bindingCtx.bindValue(myWidget, myModel, strat, null);
		myWidget.addChangeListener(changeListener);
		listened.add(myWidget);
		
		strat = new UpdateValueStrategy();
		strat.setConverter(new IntegerConverter());
		strat.setBeforeSetValidator(new IntegerValidator(createDecorator(lifeExpectancyYears, LIFE_EXPECTANCY_SYNTAXE_ERROR_MESSAGE)));

		myModel = PojoProperties.value(IForm.class, "lifeExpectancyYears").observe(form); //$NON-NLS-1$
		myWidget = WidgetProperties.text(SWT.Modify).observe(lifeExpectancyYears);
		bindingCtx.bindValue(myWidget, myModel, strat, null);
		myWidget.addChangeListener(changeListener);
		listened.add(myWidget);
	}
	
	private ControlDecoration createDecorator(Text text, String message) {
		ControlDecoration controlDecoration = new ControlDecoration(text,SWT.LEFT | SWT.TOP);
		controlDecoration.setDescriptionText(message);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		controlDecoration.setImage(fieldDecoration.getImage());
		return controlDecoration;
	}



	private class IntegerValidator implements IValidator{
		
		private final ControlDecoration controlDecoration;

		public IntegerValidator(ControlDecoration controlDecoration) {
			super();
			
			this.controlDecoration = controlDecoration;
		}

		@Override
		public IStatus validate(Object value) {
			if (value == null || !(value instanceof Integer) || ((Integer)value).intValue() < 0){
				controlDecoration.show();

				return ValidationStatus.error(LIFE_EXPECTANCY_SYNTAXE_ERROR_MESSAGE);
			}
			
			controlDecoration.hide();
			return Status.OK_STATUS;

		}
	}
	
	private class IntegerConverter implements IConverter{
		@Override
		public Object getToType() {
			return Integer.class;
		}
		
		@Override
		public Object getFromType() {
			return String.class;
		}
		
		@Override
		public Object convert(Object fromObject) {
			try{
				return Integer.parseInt((String)fromObject);
			}catch(NumberFormatException ex){
				
			}
			return null;
		}
	}
	
	private static final String LIFE_EXPECTANCY_SYNTAXE_ERROR_MESSAGE = Messages.CompositeForm_23;
}
