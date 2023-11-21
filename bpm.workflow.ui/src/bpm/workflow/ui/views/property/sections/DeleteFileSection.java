package bpm.workflow.ui.views.property.sections;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IServer;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.DeleteFileActivity;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.ListServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.dialogs.DialogBrowseContent;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the delete file activity
 * 
 * @author Charles MARTIN
 * 
 */
public class DeleteFileSection extends AbstractPropertySection {
	public static Color mainBrown = new Color(Display.getDefault(), 209, 177, 129);
	public static Color secondBrown = new Color(Display.getDefault(), 238, 226, 208);

	private Text select;

	private Node node;
	private Button validPath;
	private Combo server;
	private Button browse;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());

		org.eclipse.swt.widgets.Group general = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
		general.setLayout(new GridLayout(3, false));
		general.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		general.setText(Messages.DeleteFileSection_0);

		CLabel labelLabel = getWidgetFactory().createCLabel(general, Messages.DeleteFileSection_1);
		labelLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		server = new Combo(general, SWT.READ_ONLY);
		server.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		server.addSelectionListener(adaptder);

		CLabel labelcom = getWidgetFactory().createCLabel(general, Messages.DeleteFileSection_3);
		labelcom.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		select = getWidgetFactory().createText(general, ""); //$NON-NLS-1$
		select.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		validPath = new Button(general, SWT.PUSH);
		validPath.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		validPath.setText(Messages.DeleteFileSection_2);

		validPath.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((DeleteFileActivity) node.getWorkflowObject()).setPath(select.getText());
				if(select.getText().contains("{$")) { //$NON-NLS-1$
					String finalstring = new String(select.getText());

					List<String> varsString = new ArrayList<String>();
					for(Variable variable : ListVariable.getInstance().getVariables()) {
						varsString.add(variable.getName());
					}
					for(String nomvar : varsString) {
						String toto = finalstring.replace("{$" + nomvar + "}", ListVariable.getInstance().getVariable(nomvar).getValues().get(0)); //$NON-NLS-1$ //$NON-NLS-2$
						if(!toto.equalsIgnoreCase(finalstring)) {
							((WorkflowModel) Activator.getDefault().getCurrentModel()).addResource(ListVariable.getInstance().getVariable(nomvar));

						}

						finalstring = toto;
					}

				}
			}

		});

		browse = new Button(general, SWT.PUSH);
		browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browse.setText(Messages.DeleteFileSection_6);

		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileServer server = (FileServer) ((DeleteFileActivity) node.getWorkflowObject()).getServer();
				String typeofServer = server.getTypeServ();

				if(typeofServer.equalsIgnoreCase("Hard Drive")) { //$NON-NLS-1$

					String url = server.getUrl();
					if(url.contains("{$VANILLA_HOME}")) { //$NON-NLS-1$
						MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.DeleteFileSection_9, Messages.DeleteFileSection_10);

					}
					if(url.contains("{$VANILLA_FILES}")) { //$NON-NLS-1$

						MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.DeleteFileSection_12, Messages.DeleteFileSection_13);

					}
					else {
						StringBuffer cheminbuf = new StringBuffer();
						cheminbuf.append(url);

						if(((FileServer) server).getRepertoireDef() != null) {
							cheminbuf.append(((FileServer) server).getRepertoireDef() + "\\"); //$NON-NLS-1$
						}
						String testchemin = cheminbuf.toString();
						File test = new File(testchemin);
						List<List<String>> resultat = new ArrayList<List<String>>();

						for(File file : test.listFiles()) {
							List<String> nameFiles = new ArrayList<String>();
							nameFiles.add(file.getName());
							resultat.add(nameFiles);
						}

						ArrayList<String> nom = new ArrayList<String>();
						nom.add(Messages.DeleteFileSection_15);

						DialogBrowseContent dialogbrowse = new DialogBrowseContent(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), resultat, nom);
						dialogbrowse.open();

					}

				}
				else {
					try {
						FTPClient ftp = new FTPClient();
						ftp.connect(server.getUrl());
						ftp.login(server.getLogin(), server.getPassword());
						ftp.setFileType(FTP.BINARY_FILE_TYPE);
						if(server.getRepertoireDef() != null) {
							ftp.changeWorkingDirectory(server.getRepertoireDef());
						}

						List<List<String>> resultat = new ArrayList<List<String>>();

						for(FTPFile file : ftp.listFiles()) {
							List<String> nameFiles = new ArrayList<String>();
							nameFiles.add(file.getName());
							resultat.add(nameFiles);
						}

						ArrayList<String> nom = new ArrayList<String>();
						nom.add(Messages.DeleteFileSection_18);

						DialogBrowseContent dialogbrowse = new DialogBrowseContent(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), resultat, nom);
						dialogbrowse.open();
						ftp.logout();
						ftp.disconnect();
					} catch(Exception etttt) {
						etttt.printStackTrace();
					}
				}

			}

		});
	}

	@Override
	public void refresh() {

		if(((DeleteFileActivity) node.getWorkflowObject()).getPath() != null) {

			select.setText(((DeleteFileActivity) node.getWorkflowObject()).getPath());
		}

		String[] listeVar = new String[ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel()).length - 2];
		int i = 0;
		for(String string : ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())) {
			if(!string.equalsIgnoreCase("{$VANILLA_FILES}") && !string.equalsIgnoreCase("{$VANILLA_HOME}")) { //$NON-NLS-1$ //$NON-NLS-2$
				listeVar[i] = string;
				i++;
			}

		}
		new ContentProposalAdapter(select, new TextContentAdapter(), new SimpleContentProposalProvider(listeVar), Activator.getDefault().getKeyStroke(), Activator.getDefault().getAutoActivationCharacters());

		List<Server> servers = ListServer.getInstance().getServers();
		List<Server> temp = new ArrayList<Server>();
		for(Server s : servers) {
			if(s.getClass() == ((IServer) node.getWorkflowObject()).getServerClass()) {

				temp.add(s);
			}
		}

		int selectTo = -1;
		String[] items = new String[temp.size()];
		int index = 0;
		for(Server s : temp) {
			items[index] = s.getName();
			if(s.getClass() == ((IServer) node.getWorkflowObject()).getServerClass() && ((IServer) node.getWorkflowObject()).getServer() != null && s.getName().equalsIgnoreCase(((IServer) node.getWorkflowObject()).getServer().getName())) {
				selectTo = index;
			}
			index++;
		}

		server.setItems(items);
		server.select(selectTo);

	}

	@Override
	public void aboutToBeShown() {

		super.aboutToBeShown();

	}

	public void aboutToBeHidden() {

		super.aboutToBeHidden();
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();

	}

	SelectionAdapter adaptder = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			Server s = null;
			if(server.getText() != null) {
				s = ListServer.getInstance().getServer(server.getText());
				if(s != null && s instanceof FileServer) {
					((DeleteFileActivity) node.getWorkflowObject()).setServer(s);
					((WorkflowModel) Activator.getDefault().getCurrentModel()).addResource(s);
				}
			}

		}

	};

}
