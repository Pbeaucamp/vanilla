package bpm.vanilla.map.design.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.norparena.mapmanager.Messages;

public class DialogMapType extends Dialog {

	private Composite mainComposite;
	private Button btnFlash;
	private Button btnOpenLayers;
	private Button btnOpenGis;
	
	private String mapType = "Flash"; //$NON-NLS-1$
	
	public DialogMapType(Shell parentShell) {
		super(parentShell);
		this.setShellStyle(SWT.SHELL_TRIM);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		newShell.setText(Messages.DialogMapType_1);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		btnFlash = new Button(mainComposite, SWT.RADIO);
		btnFlash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		btnFlash.setText(Messages.DialogMapType_2);
		btnFlash.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapType = "Flash"; //$NON-NLS-1$
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		btnOpenLayers = new Button(mainComposite, SWT.RADIO);
		btnOpenLayers.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		btnOpenLayers.setText(Messages.DialogMapType_4);
		btnOpenLayers.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapType = "OpenLayers"; //$NON-NLS-1$
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		btnOpenGis = new Button(mainComposite, SWT.RADIO);
		btnOpenGis.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		btnOpenGis.setText("Add An OpenGis Map");
		btnOpenGis.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapType = "OpenGis"; //$NON-NLS-1$
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		return mainComposite;
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}
	
	public String getMapType() {
		return mapType;
	}
}
