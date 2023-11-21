package bpm.vanilla.server.client.ui.clustering.menu.views.fmdt;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.vanilla.platform.core.beans.FMDTQueryBean;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;

public class DialogFmdtQuery extends Dialog{
	private FMDTQueryBean bean;
	private FormToolkit tk = new FormToolkit(Display.getDefault());
	
	protected DialogFmdtQuery(Shell parentShell, FMDTQueryBean bean) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.bean = bean;
	}
	
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = tk.createComposite(parent);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		tk.adapt(main);
		tk.paintBordersFor(main);
		
		Group type = new Group(main, SWT.NONE);
		type.setLayout(new GridLayout());
		type.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		type.setText(Messages.DialogFmdtQuery_0);
		type.setBackground(tk.getColors().getBackground());
		tk.adapt(type);
		tk.paintBordersFor(type);
		
		final Button b = tk.createButton(type, Messages.DialogFmdtQuery_1, SWT.RADIO);
		b.setLayoutData(new GridData());
		
		
		final Button b2 = tk.createButton(type, Messages.DialogFmdtQuery_2, SWT.RADIO);
		b2.setLayoutData(new GridData());
		
		
		final Text q = tk.createText(main, "", SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL); //$NON-NLS-1$
		q.setLayoutData(new GridData(GridData.FILL_BOTH));
		q.setEditable(false);
		
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (b.getSelection()){
					q.setText(bean.getEffectiveQuery() + ""); //$NON-NLS-1$
				}
				else{
					q.setText(bean.getFmdtQuery() + ""); //$NON-NLS-1$
				}
			}
		});
		b.setSelection(true);
		q.setText(bean.getEffectiveQuery() + ""); //$NON-NLS-1$
		
		
		if (bean.getFailureCause() != null){
			Label l = tk.createLabel(main, Messages.DialogFmdtQuery_3, SWT.NONE); 
			l.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
			
			Text error = tk.createText(main, bean.getFailureCause(), SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL); //$NON-NLS-1$
			error.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false));
			error.setEditable(false);
		}
		return main;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		getShell().setText(Messages.DialogFmdtQuery_7);
	}

}
