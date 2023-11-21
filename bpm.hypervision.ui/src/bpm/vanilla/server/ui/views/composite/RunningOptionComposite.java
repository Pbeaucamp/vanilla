package bpm.vanilla.server.ui.views.composite;

import java.util.Properties;

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

import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.beans.tasks.TaskPriority;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;

public class RunningOptionComposite extends Composite {

	private Combo formatOutput;
	private Combo taskPriority;

	private Button storeInGed;
	private Text reportName;

	public RunningOptionComposite(Composite parent, int style) {
		super(parent, style);
		
		Composite main = new Composite(this, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = null;
		if (Activator.getDefault().getServerType() == ServerType.REPORTING){
			l = new Label(main, SWT.NONE);
			l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			l.setText(Messages.RunningOptionPage_0);
			
			formatOutput = new Combo(main, SWT.READ_ONLY);
			formatOutput.setItems(new String[]{"PDF", "HTML"}); //$NON-NLS-1$ //$NON-NLS-2$
			formatOutput.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			formatOutput.select(0);
			
			storeInGed = new Button(main, SWT.CHECK);
			storeInGed.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
			storeInGed.setText(Messages.RunningOptionPage_3);
			storeInGed.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					reportName.setEnabled(storeInGed.getSelection());
				}
			});
			
			l = new Label(main, SWT.NONE);
			l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			l.setText(Messages.RunningOptionPage_4);
			
			reportName = new Text(main, SWT.BORDER);
			reportName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			reportName.setEnabled(false);
		}
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.RunningOptionPage_5);
		
		taskPriority = new Combo(main, SWT.READ_ONLY);
		taskPriority.setItems(new String[]{TaskPriority.LOW_PRIORITY.getLabel(), TaskPriority.NORMAL_PRIORITY.getLabel(), TaskPriority.HIGH_PRIORITY.getLabel()});
		taskPriority.select(1);
		taskPriority.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
	}

	
	public Properties getRunProperties(){
		Properties p = new Properties();
		if (Activator.getDefault().getServerType() == ServerType.REPORTING){
			p.setProperty("outputFormat", formatOutput.getText()); //$NON-NLS-1$
			if (storeInGed.getSelection()){
				p.setProperty("outputName", reportName.getText());	 //$NON-NLS-1$
			}
		}
		p.setProperty("taskPriority", taskPriority.getText()); //$NON-NLS-1$
		return p;
	}
	
	public boolean isPageComplete() {
		if (storeInGed != null && storeInGed.getSelection()){
			return !reportName.getText().trim().equals(""); //$NON-NLS-1$
		}
		return true;
	}
	
}
