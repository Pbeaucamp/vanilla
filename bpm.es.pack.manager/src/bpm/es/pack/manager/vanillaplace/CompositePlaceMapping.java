package bpm.es.pack.manager.vanillaplace;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import adminbirep.Messages;

public abstract class CompositePlaceMapping extends Composite{

	protected Composite parent;
	
	protected Button apply;
	
	public CompositePlaceMapping(Composite parent, int style){
		super(parent, style);
		this.parent = parent;
	}
	
	protected void buildContent(){
		Composite c = new Composite(this, SWT.NONE);
		c.setLayout(new GridLayout(2, true));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		apply = new Button(c, SWT.PUSH);
		apply.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		apply.setText(bpm.es.pack.manager.I18N.Messages.CompositePlaceMapping_0);
		apply.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					performChanges();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
		});
	}
	
	public abstract void performChanges() throws Exception;
}
