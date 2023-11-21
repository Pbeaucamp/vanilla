package bpm.vanilla.properties.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class AboutVanillaConfigurator extends Dialog {

	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public AboutVanillaConfigurator(Shell parent, int style) {
		super(parent, style);
		setText(Messages.About_Vanilla_Conf);
		
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | SWT.CLOSE);
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shell.setSize(674, 265);
		shell.setText(getText());
		shell.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		lblNewLabel.setImage(SWTResourceManager.getImage(AboutVanillaConfigurator.class, "/bpm/vanilla/properties/ui/img/Vanilla_Logo_3.4.gif"));
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.widthHint = 279;
		gd_lblNewLabel.heightHint = 250;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
			
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setLayout(new GridLayout(1, false));
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite.heightHint = 204;
		gd_composite.widthHint = 240;
		composite.setLayoutData(gd_composite);
		new Label(composite, SWT.NONE);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_1.setText(Messages.VANILLA_CONFIGURATOR);
		new Label(composite, SWT.NONE);
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_2.setText(Messages.Latest_Vanilla_Version);
		new Label(composite, SWT.NONE);
		
		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		lblNewLabel_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_3.setText(Messages.Product_Of);
		new Label(composite, SWT.NONE);
		
		Link link = new Link(composite, SWT.NONE);
		link.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		link.setText("<A>"+Messages.Link_BPM+"</A>");
		link.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				Program.launch(Messages.Link_BPM);				
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {				
			}
		});
		
	}
}
