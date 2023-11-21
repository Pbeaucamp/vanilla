package bpm.oda.driver.reader.wizards.gateway;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TargetPage extends WizardPage{
	private static final String[] SERVER_TYPE = new String[]{
		"FileSystem"
	};
	
	protected static final String[] OUTPUT_TYPE = new String[]{
		"CSV", "XLS", "XML"
	};
	
	
	private Combo serverType;
	private Composite serverDefinitionComposite;
	
	private Text outputFileName;
	private Combo outputType;
	
	private GatewayBean bean;
	
	protected TargetPage(String pageName, GatewayBean bean) {
		super(pageName);
		this.bean = bean;
	}


	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("ServerType");
		
		serverType = new Combo(main, SWT.READ_ONLY);
		serverType.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 2, 1));
		serverType.setItems(SERVER_TYPE);
		serverType.select(0);
		serverType.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getContainer().updateButtons();
				
			}
		});
		
		
		Group fileCmp = new Group(main, SWT.NONE);
		fileCmp.setLayout(new GridLayout(3, false));
		fileCmp.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		fileCmp.setText("File Step Configuration");
		
		l = new Label(fileCmp, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("OutputType");
		
		outputType = new Combo(fileCmp, SWT.READ_ONLY);
		outputType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		outputType.setItems(OUTPUT_TYPE);
		outputType.select(0);
		outputType.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				bean.setStepOutputType(outputType.getText());
				getContainer().updateButtons();
				
			}
		});
		bean.setStepOutputType(outputType.getText());
		
		l = new Label(fileCmp, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("OutputFileName");
		
		outputFileName = new Text(fileCmp, SWT.BORDER);
		outputFileName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		outputFileName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				bean.setStepOutputFileName(outputFileName.getText());
				getContainer().updateButtons();
				
			}
		});
		bean.setStepOutputFileName(outputFileName.getText());
		
		Button browse = new Button(fileCmp, SWT.PUSH);
		browse.setText("...");
		browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog d = new FileDialog(getShell(), SWT.SAVE);
				d.setFilterExtensions(new String[]{"*." + outputType.getText().toLowerCase()});
				
				String s = d.open();
				if (s !=null){
					outputFileName.setText(s);
					bean.setStepOutputFileName(outputFileName.getText());
				}
				getContainer().updateButtons();
			}
		});
		
		
		final Button del = new Button(fileCmp, SWT.CHECK);
		del.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		del.setText("Delete File First");
		del.setSelection(true);
		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bean.setDeleteFile(del.getSelection());
			}
		});
		
		setControl(main);
		
	}

	
	@Override
	public boolean isPageComplete() {
		return serverType.getSelectionIndex() > -1 && outputType.getSelectionIndex() > -1 && !"".equals(outputFileName.getText().trim());
	}
}
