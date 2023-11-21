package bpm.profiling.ui.dialogs;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.profiling.runtime.helper.SchedulerHelper;
import bpm.profiling.ui.Activator;
import bpm.profiling.ui.composite.TimeInfoComposite;
import bpm.profiling.ui.preferences.PreferenceConstants;

public class DialogSchedule extends Dialog {

	private Text url, jobName;
	private Text login;
	private Text password;
	private Combo groups;
	private TimeInfoComposite crono;
//	private Query q;
	
	private Button ok ;
	
	public DialogSchedule(Shell parent/*, Query q*/) {
		super(parent);
		setShellStyle(SWT.RESIZE | SWT.TITLE);
//		this.q = q;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group group = new Group(c, SWT.NONE);
		group.setText("Scheduler Server Connection");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
				
		Label lbl = new Label(group, SWT.None);
		lbl.setText("Server Url");
		lbl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		url = new Text(group, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		url.setText(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_SCHEDULER_SERVER_URL));
		
		Label lblUsername = new Label(group, SWT.None);
		lblUsername.setText("Login");
		lblUsername.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		login = new Text(group, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label lblPassword = new Label(group, SWT.None);
		lblPassword.setText("Password");
		lblPassword.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		password = new Text(group, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		
		Button connect = new Button(c, SWT.PUSH);
		connect.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		connect.setText("Connect");
		connect.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				connect();
			}
			
		});
		
		Composite _c = new Composite(c, SWT.NONE);
		_c.setLayout(new GridLayout(2, false));
		_c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(_c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Group");
		
		groups = new Combo(_c, SWT.READ_ONLY);
		groups.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groups.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}

			public void widgetSelected(SelectionEvent e) {
					ok.setEnabled(isFilled());
			}
			
		});
		
		Label l0 = new Label(_c, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l0.setText("Job Name");
		
		jobName = new Text(_c, SWT.BORDER);
		jobName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		jobName.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				ok.setEnabled(isFilled());
				
			}
			
		});
		
		crono = new TimeInfoComposite(c, SWT.NONE);
		crono.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		
		return c;
	}

	private void connect(){
		try{
			List<String> groupNames = SchedulerHelper.getGroups(this.url.getText());
			groups.setItems(groupNames.toArray(new String[groupNames.size()]));	
		}catch(Exception ex){
			MessageDialog.openError(getShell(), "Error occured", "Unable to get Groups from Scheduler Server.\n" + ex.getMessage());
			groups.setItems(new String[]{});
		}
		
	
	}

	@Override
	protected void initializeBounds() {
		getShell().setText("Schedule Task");
		getShell().setSize(800, 600);
		
	}

	@Override
	protected void okPressed() {
		try{
			submitTask();
		}catch(Exception e){
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error", e.getMessage());
			return;
		}
		super.okPressed();
	}
	
	
	
	private void submitTask() throws Exception{
		

		StringBuffer buf = new StringBuffer();
		buf.append("<profilingJob>\n");
		buf.append("    <name>"   + jobName.getText()   + "</name>\n");
		buf.append("    <url>" + Activator.helper.getUrl() + "</url>\n");
		buf.append("    <user>" + Activator.helper.getUser() + "</user>\n");
		buf.append("    <pass>" + Activator.helper.getPassword() + "</pass>\n");
		buf.append("    <group>" + groups.getText() + "</group>\n");
		
		// TODO : replace by analysisId probably
//		buf.append("    <queryId>" + q.getId() + "</queryId>\n");
		
		buf.append("    <driverClassName>" + Activator.helper.getDriverClass() + "</driverClassName>\n");
		buf.append("    <cronstr>" + crono.getCronString() + "</cronstr>\n");
		buf.append("    <start>"   + crono.getStartTime()   + "</start>\n");
		buf.append("</profilingJob>\n");
		
		URL url = new URL(this.url.getText() + "/AddProfileJob");
		
		//System.out.println(buf.toString());
		post(url, buf.toString());
	}

	
	
	private void post(URL url, String xml) throws IOException {
		//send it to servlet
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
		sock.setRequestProperty("Content-type", "text/xml");
		
		//send datas
		PrintWriter pw = new PrintWriter(sock.getOutputStream());
		pw.write(xml);
		pw.close();
		InputStream is = sock.getInputStream();
		
		sock.connect();
		String result = IOUtils.toString(is, "UTF-8");
		is.close();
		sock.disconnect();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		ok = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		ok.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
	}
	
	private boolean isFilled(){
		return !(groups.getText().trim().equals("") && jobName.getText().trim().equals(""));
	}
}
