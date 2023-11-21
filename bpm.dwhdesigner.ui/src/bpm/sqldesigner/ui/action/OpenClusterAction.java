package bpm.sqldesigner.ui.action;

import java.util.HashMap;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.command.LoadMarkupPointCommand;
import bpm.sqldesigner.ui.command.OpenClusterCommand;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.wizard.OpenClusterWizard;

public class OpenClusterAction extends WorkbenchPartAction {

	public OpenClusterAction(IWorkbenchPart part) {
		super(part);
		setImageDescriptor(ImageDescriptor.createFromImage(Activator
				.getDefault().getImageRegistry().get("databasesNew"))); //$NON-NLS-1$
		setToolTipText(Messages.OpenClusterAction_1);
	}

	@Override
	public void run() {
		Shell shell = new Shell(Activator.getDefault().getWorkbench()
				.getDisplay());
		OpenClusterWizard wizard = new OpenClusterWizard();
		WizardDialog dialog = new WizardDialog(shell, wizard);

		dialog.create();
		dialog.getShell().setLayout(new GridLayout());
		dialog.setBlockOnOpen(false);
		// dialog.getShell().setSize(350, 550);
		dialog.getShell().pack();
		dialog.open();
		dialog.setBlockOnOpen(true);
		wizard.clearText();

		if (dialog.open() == Window.OK) {
//			Command command;
//			if (wizard.getMode().equals("Extract")) {
//
//				String host = wizard.getHost();
//				String port = wizard.getPort();
//				String login = wizard.getLogin();
//				String pass = wizard.getPass();
//				String dBName = wizard.getDBName();
//				String driver = wizard.getDriver();
//				command = createOpenClusterCommand(host, port, login, pass,
//						dBName, driver);
//			} else {
//				String file = wizard.getFile();
//
//				command = createLoadMarkupPointCommand(file);
//			}
//
//			if (command.canExecute()) 
//				command.execute();
//			
//
//			else
//				MessageDialog.openError(
//						getWorkbenchPart().getSite().getShell(), "Error",
//						command.getLabel());
		}
	}

	private Command createOpenClusterCommand(String host, String port,
			String login, String pass, String dBName, String driver) {
		Request renameReq = new Request("openCluster"); //$NON-NLS-1$

		HashMap<String, Object> reqData = new HashMap<String, Object>();
		reqData.put("host", host); //$NON-NLS-1$
		reqData.put("port", port); //$NON-NLS-1$
		reqData.put("login", login); //$NON-NLS-1$
		reqData.put("pass", pass); //$NON-NLS-1$
		reqData.put("dBName", dBName); //$NON-NLS-1$
		reqData.put("driver", driver); //$NON-NLS-1$
		reqData.put("workbenchPart", getWorkbenchPart()); //$NON-NLS-1$
		renameReq.setExtendedData(reqData);

		return new OpenClusterCommand(renameReq);

	}

	private  Command createLoadMarkupPointCommand(String file) {
		Request renameReq = new Request("loadMarkupPoint"); //$NON-NLS-1$

		HashMap<String, Object> reqData = new HashMap<String, Object>();
		reqData.put("file", file); //$NON-NLS-1$
		reqData.put("workbenchPart", getWorkbenchPart()); //$NON-NLS-1$
		renameReq.setExtendedData(reqData);

		return new LoadMarkupPointCommand(renameReq);

	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

}
