package bpm.forms.design.ui.composite;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.forms.core.design.ITargetTable;
import bpm.forms.design.ui.Messages;


public class CompositeTargetTable {
	private Composite client;
	
	private Text label;
	private Text description;
	private Text physicalName;
	
	private ITargetTable targetTable;
	
	private FormToolkit toolkit;
	private DataBindingContext bindingCtx = new DataBindingContext();
	
	private IChangeListener changeListener = new IChangeListener() {
		
		@Override
		public void handleChange(ChangeEvent event) {
			if (getClient() != null && !getClient().isDisposed()){
				getClient().notifyListeners(SWT.Modify, new Event());
			}
			
			
		}
	};
	public CompositeTargetTable(FormToolkit toolkit){
		this.toolkit = toolkit;
	}
	
	public void createContent(Composite parent){
		client = toolkit.createComposite(parent, SWT.BORDER);
		client.setLayout(new GridLayout(2, false));
		client.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (bindingCtx != null){
					bindingCtx.dispose();
					bindingCtx = null;
				}
				
			}
		});
		Label l = toolkit.createLabel(client, Messages.CompositeTargetTable_0, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		label = toolkit.createText(client, "", SWT.BORDER); //$NON-NLS-1$
		label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		l = toolkit.createLabel(client, Messages.CompositeTargetTable_2, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		
		physicalName = toolkit.createText(client, "", SWT.BORDER); //$NON-NLS-1$
		physicalName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		l =toolkit.createLabel(client, Messages.CompositeTargetTable_4, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		description = toolkit.createText(client, "", SWT.BORDER); //$NON-NLS-1$
		description.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

	}
	
	public Composite getClient(){
		return client;
	}
	
	public void setInput(ITargetTable table){
		this.targetTable = table;
		
		
		if (bindingCtx != null){
			bindingCtx.dispose();
			bindingCtx = null;
		}
		bindValues();
		
		
	}
	
	public ITargetTable getInput(){
		return targetTable;
	}
	
	private void bindValues(){
		if (bindingCtx != null){
			bindingCtx.dispose();
		}
		
		bindingCtx = new DataBindingContext();
		/*
		 * name
		 */
		IObservableValue myModel = PojoProperties.value(ITargetTable.class, "name").observe(targetTable); //$NON-NLS-1$
		IObservableValue myWidget = WidgetProperties.text(SWT.Modify).observe(label);
		bindingCtx.bindValue(myWidget, myModel);
		myWidget.addChangeListener(changeListener);
		
		
		/*
		 * description
		 */
		myModel = PojoProperties.value(ITargetTable.class, "description").observe(targetTable); //$NON-NLS-1$
		myWidget = WidgetProperties.text(SWT.Modify).observe(description);
		bindingCtx.bindValue(myWidget, myModel);
		myWidget.addChangeListener(changeListener);
		
		/*
		 * description
		 */
		myModel = PojoProperties.value(ITargetTable.class, "databasePhysicalName").observe(targetTable); //$NON-NLS-1$
		myWidget = WidgetProperties.text(SWT.Modify).observe(physicalName);
		bindingCtx.bindValue(myWidget, myModel);
		myWidget.addChangeListener(changeListener);
	}
}
