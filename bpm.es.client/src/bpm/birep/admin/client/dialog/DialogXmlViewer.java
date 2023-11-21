package bpm.birep.admin.client.dialog;

import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import adminbirep.Activator;
import adminbirep.Messages;
import adminbirep.icons.Icons;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogXmlViewer extends Dialog {

	private RepositoryItem item;
	private Text xml;
	private boolean isModfied = false;
	private String content;

	private boolean canImport;

	public DialogXmlViewer(Shell parentShell, RepositoryItem item) {
		super(parentShell);
		this.item = item;
		this.canImport = true;
	}

	public DialogXmlViewer(Shell parentShell, String content) {
		super(parentShell);
		this.content = content;
		this.canImport = false;
	}

	private void fillData() {
		try {
			String xml = Activator.getDefault().getRepositoryApi().getRepositoryService().loadModel(item);
			this.xml.setText(xml);
		} catch (Exception e) {
			this.xml.setText(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		c.setLayout(new GridLayout());
		createToolbar(c);

		xml = new Text(c, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		xml.setLayoutData(new GridData(GridData.FILL_BOTH));
		xml.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				isModfied = true;
				content = xml.getText();
			}

		});

		if (item != null) {
			fillData();
		}
		else if (content != null) {
			xml.setText(content);
		}

		return c;
	}

	private void createToolbar(Composite c) {
		ToolBar toolBar = new ToolBar(c, SWT.NONE);

		ToolItem search = new ToolItem(toolBar, SWT.PUSH);
		search.setImage(Activator.getDefault().getImageRegistry().get("browse")); //$NON-NLS-1$
		search.setToolTipText(Messages.DialogXmlViewer_1);
		search.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogSearch dial = new DialogSearch(getParentShell(), xml);
				dial.open();
			}
		});

		ToolItem searchAndReplace = new ToolItem(toolBar, SWT.PUSH);
		searchAndReplace.setImage(Activator.getDefault().getImageRegistry().get("searchreplace")); //$NON-NLS-1$
		searchAndReplace.setToolTipText(Messages.DialogXmlViewer_3);
		searchAndReplace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogSearchReplaceXml dial = new DialogSearchReplaceXml(getParentShell(), xml.getText());
				if (dial.open() == Dialog.OK) {
					xml.setText(dial.getNewXml());
				}
			}
		});

		if (canImport) {
			ToolItem importNewVersion = new ToolItem(toolBar, SWT.PUSH);
			importNewVersion.setImage(Activator.getDefault().getImageRegistry().get(Icons.IMPORT_VANILLA_PLACE));
			importNewVersion.setToolTipText(Messages.DialogXmlViewer_4);
			importNewVersion.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FileDialog fd = new FileDialog(getParentShell());
					String path = fd.open();

					if (path != null) {
						try {
							DialogEncoding dial = new DialogEncoding(getParentShell());
							if (dial.open() == Dialog.OK) {
								String encoding = dial.getEncodingName();

								FileInputStream fis = new FileInputStream(path);
								String xml = IOUtils.toString(fis, encoding);
								fis.close();
								Activator.getDefault().getRepositoryApi().getRepositoryService().updateModel(item, xml);

								isModfied = false;
								close();

								MessageDialog.openInformation(getParentShell(), Messages.DialogXmlViewer_5, Messages.DialogXmlViewer_6);
							}
						} catch (Exception e1) {
							e1.printStackTrace();
							MessageDialog.openError(getParentShell(), Messages.DialogXmlViewer_7, e1.getMessage());
						}
					}
				}
			});
		}
	}

	public boolean isModified() {
		return isModfied;
	}

	public String getXml() {
		return content;
	}
}
