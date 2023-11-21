package bpm.vanilla.server.ui.wizard;

import java.util.Properties;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.server.client.ServerType;
import bpm.vanilla.server.client.communicators.TaskRunnerCommunicator;
import bpm.vanilla.server.ui.Activator;

public class RunningOptionPage extends WizardPage{

	private Combo formatOutput;
	private Combo taskPriority;
	
	
	private Button storeInGed;
	private Text reportName;
	
	public RunningOptionPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		Label l = null;
		
		if (Activator.getDefault().getServerRemote().getServerType() == ServerType.REPORTING){
			l = new Label(main, SWT.NONE);
			l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			l.setText("Report Output Format");
			
			formatOutput = new Combo(main, SWT.READ_ONLY);
			formatOutput.setItems(new String[]{"PDF", "HTML"});
			formatOutput.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			formatOutput.select(0);
			
			storeInGed = new Button(main, SWT.CHECK);
			storeInGed.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
			storeInGed.setText("Store Generated Report In Vanilla Ged");
			storeInGed.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					reportName.setEnabled(storeInGed.getSelection());
				}
			});
			
			l = new Label(main, SWT.NONE);
			l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			l.setText("Generated Report Name");
			
			reportName = new Text(main, SWT.BORDER);
			reportName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			reportName.setEnabled(false);
		}
		
		
//		l = new Label(main, SWT.NONE);
//		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l.setText("Number of run");
//		
//		runNumber = new Text(main, SWT.NONE);
//		runNumber.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		runNumber.setText("5");
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Priority");
		
		taskPriority = new Combo(main, SWT.READ_ONLY);
		taskPriority.setItems(new String[]{TaskRunnerCommunicator.PRIORITY_LOW, TaskRunnerCommunicator.PRIORITY_NORMAL, TaskRunnerCommunicator.PRIORITY_HIGHT});
		taskPriority.select(1);
		taskPriority.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		setControl(main);
		
	}
	
	
	public Properties getRunProperties(){
		Properties p = new Properties();
		if (Activator.getDefault().getServerRemote().getServerType() == ServerType.REPORTING){
			p.setProperty("outputFormat", formatOutput.getText());
			if (storeInGed.getSelection()){
				p.setProperty("outputName", reportName.getText());	
			}
		}
		p.setProperty("taskPriority", taskPriority.getText());
		return p;
	}
	@Override
	public boolean isPageComplete() {
		if (storeInGed != null && storeInGed.getSelection()){
			return !reportName.getText().trim().equals("");
		}
		return super.isPageComplete();
	}
}
