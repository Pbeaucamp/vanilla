package bpm.birep.admin.client.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import adminbirep.Activator;
import bpm.vanilla.platform.core.beans.Server;

/**
 * 
 * @author ludo
 * @deprecated no more used, server are dynamically registered in the platform
 *  * delete candidate
 */
public class DialogServer{}
//extends Dialog{
//	
//	private Text name;
//	private Text description;
//	private Text url;
//	private Combo type;
//	private Server server; 
//	
//	public DialogServer(Shell parentShell) {
//		super(parentShell);
//		setShellStyle(getShellStyle() | SWT.RESIZE);
//	}
//
//	@Override
//	protected Control createDialogArea(Composite parent) {
//		Composite container = new Composite(parent, SWT.NONE);
//		container.setLayout(new GridLayout(2, false));
//		container.setLayoutData(new GridData(GridData.FILL_BOTH));
//		
//		Label l = new Label(container, SWT.NONE);
//		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l.setText("Server Name");
//		
//		name = new Text(container, SWT.BORDER);
//		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		
//		Label l2 = new Label(container, SWT.NONE);
//		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 3));
//		l2.setText("Description");
//		
//		description = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP);
//		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
//		
//		
//		Label l3 = new Label(container, SWT.NONE);
//		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l3.setText("Server Url");
//		
//		url = new Text(container, SWT.BORDER);
//		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		
//		
//		Label l4 = new Label(container, SWT.NONE);
//		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l4.setText("Server Type");
//		
//		type = new Combo(container, SWT.READ_ONLY);
//		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		type.setItems(Server.TYPE_NAMES);
//		return container;
//	}
//
//	@Override
//	protected void okPressed() {
//		server = new Server();
//		server.setName(name.getText());
//		server.setDescription(description.getText());
//		server.setUrl(url.getText());
//		server.setType(type.getSelectionIndex());
//		
//		try {
//			Activator.getDefault().getManager().addServer(server);
//			super.okPressed();
//		} catch (Exception e) {
//			server = null;
//			e.printStackTrace();
//			MessageDialog.openError(getShell(), "Unable To Create Server", e.getMessage());
//		}
//		
//		
//	}
//	
//	public Server getServer(){
//		return server;
//	}
//}
