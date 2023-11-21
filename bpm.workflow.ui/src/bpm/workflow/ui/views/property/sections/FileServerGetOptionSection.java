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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IFileServerGet;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.dialogs.DialogBrowseContent;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the additional informations of a get... activity (file management)
 * 
 * @author Charles MARTIN
 * 
 */
public class FileServerGetOptionSection extends AbstractPropertySection {

	private Node node;
	private Combo butloop, butspec;
	private Text txtpath, txtEx;
	private Button validPath, validEx, browse;

	public FileServerGetOptionSection() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());

		org.eclipse.swt.widgets.Group general = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
		general.setLayout(new GridLayout(1, false));
		general.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		general.setText(Messages.FileServerGetOptionSection_0);

		Composite selection = getWidgetFactory().createFlatFormComposite(general);
		selection.setLayout(new GridLayout(2, false));
		selection.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		butloop = new Combo(selection, SWT.READ_ONLY);
		butloop.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		String[] comp = new String[2];
		comp[0] = Messages.FileServerGetOptionSection_1;
		comp[1] = Messages.FileServerGetOptionSection_2;
		butloop.setItems(comp);
		butloop.addSelectionListener(adaptder1);

		Composite specificfile = getWidgetFactory().createFlatFormComposite(general);
		specificfile.setLayout(new GridLayout(3, false));
		specificfile.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		CLabel lblpath = getWidgetFactory().createCLabel(specificfile, Messages.FileServerGetOptionSection_5);
		lblpath.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtpath = getWidgetFactory().createText(specificfile, ""); //$NON-NLS-1$
		txtpath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		validPath = new Button(specificfile, SWT.PUSH);
		validPath.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		validPath.setText(Messages.FileServerGetOptionSection_3);

		validPath.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((IFileServerGet) node.getWorkflowObject()).setPathSpecific(txtpath.getText());
				if(txtpath.getText().contains("{$")) { //$NON-NLS-1$
					String finalstring = new String(txtpath.getText());

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

		org.eclipse.swt.widgets.Group general2 = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
		general2.setLayout(new GridLayout(1, false));
		general2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		general2.setText(Messages.FileServerGetOptionSection_7);

		Composite selectionEx = getWidgetFactory().createFlatFormComposite(general2);
		selectionEx.setLayout(new GridLayout(2, false));
		selectionEx.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		butspec = new Combo(selectionEx, SWT.READ_ONLY);
		butspec.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		String[] comp2 = new String[2];
		comp2[0] = Messages.FileServerGetOptionSection_8;
		comp2[1] = Messages.FileServerGetOptionSection_9;
		butspec.setItems(comp2);

		butspec.addSelectionListener(adaptder2);

		Composite specificext = getWidgetFactory().createFlatFormComposite(general2);
		specificext.setLayout(new GridLayout(3, false));
		specificext.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		CLabel lblex = getWidgetFactory().createCLabel(specificext, Messages.FileServerGetOptionSection_6);
		lblex.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtEx = getWidgetFactory().createText(specificext, ""); //$NON-NLS-1$
		txtEx.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		validEx = new Button(specificext, SWT.PUSH);
		validEx.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		validEx.setText(Messages.FileServerGetOptionSection_10);

		validEx.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((IFileServerGet) node.getWorkflowObject()).setExtension(txtEx.getText());
			}

		});

		browse = new Button(specificext, SWT.PUSH);
		browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browse.setText(Messages.FileServerGetOptionSection_11);

		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileServer server = (FileServer) ((IFileServerGet) node.getWorkflowObject()).getServer();
				String typeofServer = server.getTypeServ();

				if(typeofServer.equalsIgnoreCase(Messages.FileServerGetOptionSection_12)) {

					String url = server.getUrl();
					if(url.contains("{$VANILLA_HOME}")) { //$NON-NLS-1$
						MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FileServerGetOptionSection_14, Messages.FileServerGetOptionSection_15);

					}
					if(url.contains("{$VANILLA_FILES}")) { //$NON-NLS-1$

						MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FileServerGetOptionSection_17, Messages.FileServerGetOptionSection_4);

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
						nom.add(Messages.FileServerGetOptionSection_20);

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
						nom.add(Messages.FileServerGetOptionSection_23);

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

		try {
			String isSpec = ((IFileServerGet) node.getWorkflowObject()).getIsSpecific();
			if(isSpec != null) {
				if(isSpec.equalsIgnoreCase(Messages.FileServerGetOptionSection_24)) {
					butloop.select(0);

					txtpath.setText(((IFileServerGet) node.getWorkflowObject()).getPathSpecific());

				}
				else {
					butloop.select(1);
					((IFileServerGet) node.getWorkflowObject()).setIsSpecific(Messages.FileServerGetOptionSection_25);
				}
			}
			else {
				butloop.deselectAll();
				txtpath.setText(""); //$NON-NLS-1$
			}

			String isEx = ((IFileServerGet) node.getWorkflowObject()).getIsSpecificEx();
			if(isEx != null) {
				if(isEx.equalsIgnoreCase(Messages.FileServerGetOptionSection_27)) {
					butspec.select(0);

					txtEx.setText(((IFileServerGet) node.getWorkflowObject()).getExtension());

				}
				else {
					butspec.select(1);
					((IFileServerGet) node.getWorkflowObject()).setIsSpecificEx(Messages.FileServerGetOptionSection_28);
				}
			}
			else {
				butspec.deselectAll();
				txtEx.setText(""); //$NON-NLS-1$
			}

			String[] listeVar = new String[ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel()).length - 2];
			int i = 0;
			for(String string : ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())) {
				if(!string.equalsIgnoreCase("{$VANILLA_FILES}") && !string.equalsIgnoreCase("{$VANILLA_HOME}")) { //$NON-NLS-1$ //$NON-NLS-2$
					listeVar[i] = string;
					i++;
				}

			}

			new ContentProposalAdapter(txtpath, new TextContentAdapter(), new SimpleContentProposalProvider(listeVar), Activator.getDefault().getKeyStroke(), Activator.getDefault().getAutoActivationCharacters());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();

	}

	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
	}

	SelectionAdapter adaptder1 = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {

			if(butloop.getText().equalsIgnoreCase(Messages.FileServerGetOptionSection_32)) {
				((IFileServerGet) node.getWorkflowObject()).setIsSpecific(Messages.FileServerGetOptionSection_33);
				txtpath.setText(""); //$NON-NLS-1$
				txtpath.setEnabled(false);
				validPath.setEnabled(false);
			}
			else {
				((IFileServerGet) node.getWorkflowObject()).setIsSpecific(Messages.FileServerGetOptionSection_35);
				txtpath.setEnabled(true);
				validPath.setEnabled(true);
			}
		}

	};

	SelectionAdapter adaptder2 = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {

			if(butspec.getText().equalsIgnoreCase(Messages.FileServerGetOptionSection_36)) {
				((IFileServerGet) node.getWorkflowObject()).setIsSpecificEx(Messages.FileServerGetOptionSection_37);
				txtEx.setEnabled(false);
				validEx.setEnabled(false);
			}
			else {
				((IFileServerGet) node.getWorkflowObject()).setIsSpecificEx(Messages.FileServerGetOptionSection_38);
				txtEx.setText(""); //$NON-NLS-1$
				txtEx.setEnabled(true);
				validEx.setEnabled(true);
			}

		}

	};

}
