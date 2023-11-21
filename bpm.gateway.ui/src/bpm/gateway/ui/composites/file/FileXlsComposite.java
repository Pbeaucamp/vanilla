package bpm.gateway.ui.composites.file;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.Server;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.file.FileXLSHelper;
import bpm.gateway.core.server.file.MdmFileServer;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.core.transformations.inputs.FileInputXLS;
import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogComboString;

public class FileXlsComposite extends AbstractFileComposite {

	private AbstractTransformation transfo;
	private Text sheetName;
	private Button readFirstLine;

	public FileXlsComposite(Composite parent, int style, TabbedPropertySheetWidgetFactory widgetFactory) {
		super(parent, style);

		this.setLayout(new GridLayout(2, false));

		Label l = widgetFactory.createLabel(this, Messages.FileXLSGeneralSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		Composite c = widgetFactory.createComposite(this);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		sheetName = widgetFactory.createText(c, ""); //$NON-NLS-1$
		sheetName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Button b = widgetFactory.createButton(c, "...", SWT.PUSH); //$NON-NLS-1$
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
				try {

					List<String> names = FileXLSHelper.getWorkSheetsNames((DataStream) transfo);

					FileInputXLS xlsTransfo = getFileXlsTransfo();
					DialogComboString d = new DialogComboString(sh, names, xlsTransfo.getSheetName());
					if (d.open() == Dialog.OK) {
						xlsTransfo.setSheetName(d.getValue());
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openWarning(sh, Messages.FileXLSGeneralSection_3, Messages.FileXLSGeneralSection_4 + e1.getMessage());
				}

			}

		});

		readFirstLine = new Button(c, SWT.CHECK);
		readFirstLine.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		readFirstLine.setText(Messages.FileXLSInputSection_0);
		readFirstLine.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getFileXlsTransfo().setSkipFirstRow(readFirstLine.getSelection());
			}
		});
	}

	public void setInput(Node node) {
		if (node.getGatewayModel() instanceof MdmContractFileInput && ((MdmContractFileInput) node.getGatewayModel()).getFileTransfo() != null) {
			transfo = (FileInputXLS) ((MdmContractFileInput) node.getGatewayModel()).getFileTransfo();
		}
		else if (node.getGatewayModel() instanceof D4CInput && ((D4CInput) node.getGatewayModel()).getFileTransfo() != null) {
			transfo = (D4CInput) node.getGatewayModel();
		}
		else {
			transfo = new FileInputXLS();
			((FileInputXLS) transfo).setEncoding("UTF-8"); //$NON-NLS-1$
		}

		if (transfo instanceof FileInputXLS) {
			List<Server> servers = ResourceManager.getInstance().getServers(MdmFileServer.class);
			MdmFileServer srv = null;
			if (servers.size() > 0) {
				srv = (MdmFileServer) servers.get(0);
			}
			else {
				srv = new MdmFileServer("mdmserver", "", Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl(), Activator.getDefault().getRepositoryContext().getVanillaContext().getLogin(), Activator.getDefault().getRepositoryContext().getVanillaContext().getPassword(), Activator.getDefault().getRepositoryContext().getRepository().getId() + ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				try {
					ResourceManager.getInstance().addServer(srv);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	
			((FileInputXLS) transfo).setDefinition(((MdmContractFileInput) node.getGatewayModel()).getSelectedContract().getId() + ""); //$NON-NLS-1$
			((FileInputXLS) transfo).setServer(srv);
		}
	}
	
	private FileInputXLS getFileXlsTransfo() {
		if (transfo instanceof D4CInput && ((D4CInput) transfo).getFileTransfo() != null) {
			return (FileInputXLS) ((D4CInput) transfo).getFileTransfo();
		}
		else if (transfo instanceof FileInputXLS) {
			return (FileInputXLS) transfo;
		}
		return null;
	}

	@Override
	public AbstractTransformation getFileTransformation() {
		return getFileXlsTransfo();
	}

	@Override
	public void refresh(Node node) {
		setInput(node);

		FileInputXLS xlsTransfo = getFileXlsTransfo();
		sheetName.setText(xlsTransfo.getSheetName());
		readFirstLine.setSelection(xlsTransfo.getSkipFirstRow());
	}
}
