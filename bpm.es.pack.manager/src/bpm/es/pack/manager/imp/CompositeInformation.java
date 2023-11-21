package bpm.es.pack.manager.imp;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import bpm.es.pack.manager.I18N.Messages;

public abstract class CompositeInformation extends Composite{
	
	protected Button apply, cancel;
	protected Composite parent;
	public CompositeInformation(Composite parent, int style){
		super(parent, style);
		this.parent = parent;
	}
	
	protected void buildContent(){
		Composite c = new Composite(this, SWT.NONE);
		c.setLayout(new GridLayout(2, true));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		apply = new Button(c, SWT.PUSH);
		apply.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		apply.setText(bpm.es.pack.manager.I18N.Messages.CompositeInformation_2);
		apply.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (parent.getParent() instanceof CompositeMapping){
					((CompositeMapping)parent.getParent()).applyChanges();
				}
			}
			
		});
		
		
		cancel = new Button(c, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.setText(Messages.CompositeInformation_1);
		cancel.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (parent instanceof CompositeMapping){
					((CompositeMapping)parent).cancelChanges();
				}
			}
		});
		
	}
	
	public abstract Properties getProperties();
	
	public abstract void setProperties(Properties prop);
}
