package bpm.gateway.ui.composites.schedule;


import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.tools.SchedulerHelper;
import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class DialogSchedule extends Dialog {

	private Text url, jobName;
	private Text login;
	private Text password;
	private Combo groups;
	private TimeInfoComposite crono;
	private IRepositoryApi sock;
	private RepositoryItem directoryItem;
	
	private Combo gatewayServerUrl;
	private TableViewer parameters;
	private HashMap<Parameter, String> map = new HashMap<Parameter, String>();
	
	
	private Button ok ;
	
	public DialogSchedule(Shell parent, IRepositoryApi sock, RepositoryItem directoryItem) {
		super(parent);
		setShellStyle(getShellStyle() | SWT.RESIZE );
		this.sock = sock;
		this.directoryItem = directoryItem;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group group = new Group(c, SWT.NONE);
		group.setText(Messages.DialogSchedule_0);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
				
		Label lbl = new Label(group, SWT.None);
		lbl.setText(Messages.DialogSchedule_1);
		lbl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		url = new Text(group, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		url.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				ok.setEnabled(isFilled());
			}
			
		});
		url.setText("http://localhost:8080/scheduler"); //$NON-NLS-1$
		
		
		Label lblUsername = new Label(group, SWT.None);
		lblUsername.setText(Messages.DialogSchedule_3);
		lblUsername.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		login = new Text(group, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		login.setText(Messages.DialogSchedule_4);
		login.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				ok.setEnabled(isFilled());
			}
			
		});
		
		
		Label lblPassword = new Label(group, SWT.None);
		lblPassword.setText(Messages.DialogSchedule_5);
		lblPassword.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		password = new Text(group, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		password.setText(Messages.DialogSchedule_6);
		
		Button connect = new Button(c, SWT.PUSH);
		connect.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		connect.setText(Messages.DialogSchedule_7);
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
		l.setText(Messages.DialogSchedule_8);
		
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
		l0.setText(Messages.DialogSchedule_9);
		
		jobName = new Text(_c, SWT.BORDER);
		jobName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		jobName.setText(Messages.DialogSchedule_10);
		jobName.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				ok.setEnabled(isFilled());
				
			}
			
		});
		
		Group ggateway = new Group(c, SWT.NONE);
		ggateway.setLayout(new GridLayout(2, false));
		ggateway.setLayoutData(new GridData(GridData.FILL_BOTH));
		ggateway.setText(Messages.DialogSchedule_11);
		
		
		Label l5 = new Label(ggateway, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText(Messages.DialogSchedule_13);
		
		parameters = new TableViewer(ggateway, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		parameters.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		parameters.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				Collection<Parameter> p = (Collection<Parameter>)inputElement;
				return p.toArray(new Parameter[p.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
			}
			
		});
		parameters.setLabelProvider(new ITableLabelProvider(){

			public void addListener(ILabelProviderListener listener) {
				
			}

			public void dispose() {
				
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex){
				case 0:
					return ((Parameter)element).getName();
				case 1: 
					return ((Parameter)element).getClass().getCanonicalName();
				case 2 :
					return map.get((Parameter)element);
				}
				return null;
			}
			
		});
		
		
		TableColumn c1 = new TableColumn(parameters.getTable(), SWT.NONE);
		c1.setText(Messages.DialogSchedule_14);
		c1.setWidth(150);
		
		TableColumn c2 = new TableColumn(parameters.getTable(), SWT.NONE);
		c2.setText(Messages.DialogSchedule_15);
		c2.setWidth(150);
		
		TableColumn c3 = new TableColumn(parameters.getTable(), SWT.NONE);
		c3.setText(Messages.DialogSchedule_16);
		c3.setWidth(150);
		
		
		parameters.setColumnProperties(new String[]{"name", "type", "value"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		parameters.setCellModifier(new ICellModifier(){
			public boolean canModify(Object element, String property) {
				return property.equals("value"); //$NON-NLS-1$
			}

			public Object getValue(Object element, String property) {
				if (property.equals("value")){ //$NON-NLS-1$
					return map.get((Parameter)element);
				}
				
				return null;
			}

			public void modify(Object element, String property, Object value) {
				map.put((Parameter)((TableItem)element).getData(), (String)value);
				parameters.refresh();
			}
				
				
		});
	
		parameters.getTable().setHeaderVisible(true);
		parameters.getTable().setLinesVisible(true);
		
		
		parameters.setCellEditors(new CellEditor[]{null, null,
				new TextCellEditor(parameters.getTable()) });
		
		crono = new TimeInfoComposite(c, SWT.NONE);
		crono.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		
		fillParameters();
		
		return c;
	}

	
	private void fillParameters(){
		try{
			for(Parameter p : sock.getRepositoryService().getParameters(this.directoryItem)){
				map.put(p, ""); //$NON-NLS-1$
			}
			
			parameters.setInput(map.keySet());
		}catch(Exception e){
			e.printStackTrace();
			MessageDialog.openWarning(getShell(), Messages.DialogSchedule_23, e.getMessage());
		}
		
	}
	
	
	private void connect(){
		
		
		
		
		
		try {
			List<String> groupNames =SchedulerHelper.getGroupNames(this.url.getText());
			groups.setItems(groupNames.toArray(new String[groupNames.size()]));

		} catch (Exception e1) {
			
			e1.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogSchedule_24, Messages.DialogSchedule_25 + e1.getMessage());
		}
				
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogSchedule_26);
		getShell().setSize(800, 600);
		
	}

	@Override
	protected void okPressed() {
		try{
			submitTask();
		}catch(Exception e){
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogSchedule_27, e.getMessage());
			return;
		}
		super.okPressed();
	}
	
	
	
	private void submitTask() throws Exception{
		StringBuffer params = new StringBuffer();
		for(Parameter p : map.keySet()){
			if (!map.get(p).equals("")){ //$NON-NLS-1$
				params.append(p.getName() + "]" + map.get(p) + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			
		}
		
		
		StringBuffer buf = new StringBuffer();
		buf.append("<gatewayJob>\n"); //$NON-NLS-1$
		buf.append("    <name>"   + jobName.getText()   + "</name>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <url>" + sock.getContext().getRepository().getUrl() + "</url>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <user>" + sock.getContext().getVanillaContext().getLogin() + "</user>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <pass>" + sock.getContext().getVanillaContext().getPassword() + "</pass>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <group>" + groups.getText() + "</group>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <directoryItemId>" + directoryItem.getId() + "</directoryItemId>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <maxNumRows>10000</maxNumRows>\n"); //$NON-NLS-1$
				buf.append("    <gatewayParameters>" + params.toString() + "</gatewayParameters>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <desc></desc>\n"); //$NON-NLS-1$
		buf.append("    <cronstr>" + crono.getCronString() + "</cronstr>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("    <start>"   + crono.getStartTime()   + "</start>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("</gatewayJob>\n"); //$NON-NLS-1$
//		
		URL url = new URL(this.url.getText() + "/AddGatewayJob"); //$NON-NLS-1$
//		
//		//System.out.println(buf.toString());
		post(url, buf.toString());
	}

	
	
	

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		ok = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		ok.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
	}
	
	private boolean isFilled(){
		return !(groups.getText().trim().equals("") && jobName.getText().trim().equals("")&& url.getText().trim().equals("") && login.getText().trim().equals("")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	
	
	/**
	 * send xml task to server
	 * @param url
	 * @param xml
	 * @throws Exception
	 */
	private void post(URL url, String xml) throws Exception{
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST"); //$NON-NLS-1$
		sock.setRequestProperty("Content-type", "text/xml"); //$NON-NLS-1$ //$NON-NLS-2$
		
		//send datas
		PrintWriter pw = new PrintWriter(sock.getOutputStream());
		pw.write(xml);
		pw.close();
		InputStream is = sock.getInputStream();
		
		sock.connect();
		String result = IOUtils.toString(is);
		is.close();
		sock.disconnect();
	}
}
