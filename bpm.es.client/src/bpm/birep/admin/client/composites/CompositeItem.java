package bpm.birep.admin.client.composites;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.dialog.DilaogPickupUser;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.Variable;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class CompositeItem extends Composite {

	private RepositoryItem item;

	private Text name, comment, type, internalVersion, publicVersion, nbMaxHisto;
	private Text creator, modifier, modification, creation;

	private Text owner;
	private User itemOwner;

	private Text directoryItemId;
	private Text modelId;

	private ComboViewer associatedServer;

	private boolean modified = false;
	private Button display;

	private Button supportAndroid;
	private Button availableOdata;

	private ComboViewer comboFormatting;
	private Label lFormat;

	private List<Variable> formatVariables;

	private List<String> availableFormats = Arrays.asList("", "html", "pdf", "xls", "xlsx", "csv", "ods", "doc", "docx", "odt", "ppt", "pht", "pptx", "postscript"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$
	private ComboViewer comboDefaultReportFormat;
	private Label lDefaultReportFormat;

	public CompositeItem(Composite parent, int style, RepositoryItem item) {
		super(parent, style);
		this.item = item;

		// item.getDefaultFormat();
		this.setLayout(new GridLayout(2, false));

		Label l1 = new Label(this, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l1.setText(Messages.CompositeItem_0);

		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				modified = true;
			}
		});

		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.CompositeItem_1);

		type = new Text(this, SWT.BORDER);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.setEnabled(false);

		if (isOnServer()) {
			Label _l5 = new Label(this, SWT.NONE);
			_l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			_l5.setText(Messages.CompositeItem_2);

			associatedServer = new ComboViewer(this, SWT.READ_ONLY);
			associatedServer.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			associatedServer.setContentProvider(new IStructuredContentProvider() {

				@Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

				@Override
				public void dispose() { }

				@Override
				public Object[] getElements(Object inputElement) {
					List l = (List) inputElement;
					List res = new ArrayList();
					for (Object o : l) {
						Server server = (Server) o;
						if (isServerUsable(server, CompositeItem.this.item)) {
							res.add(server);
						}
					}

					return res.toArray(new Object[res.size()]);
				}
			});
			associatedServer.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(Object element) {
					return ((Server) element).getName() + " - " + ((Server) element).getUrl(); //$NON-NLS-1$
				}
			});

			try {
				initServerExecutor();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		Label _l0 = new Label(this, SWT.NONE);
		_l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		_l0.setText(Messages.CompositeItem_3);

		directoryItemId = new Text(this, SWT.READ_ONLY);
		directoryItemId.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

//		Label _l3 = new Label(this, SWT.NONE);
//		_l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		_l3.setText(Messages.CompositeItem_6);
//
//		modelId = new Text(this, SWT.READ_ONLY);
//		modelId.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label _l5 = new Label(this, SWT.NONE);
		_l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		_l5.setText(Messages.CompositeItem_9);

		display = new Button(this, SWT.CHECK);
		display.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		_l5 = new Label(this, SWT.NONE);
		_l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		_l5.setText(Messages.CompositeItem_10);

		supportAndroid = new Button(this, SWT.CHECK);
		supportAndroid.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		if (item.getType() == IRepositoryApi.FMDT_TYPE) {
			_l5 = new Label(this, SWT.NONE);
			_l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			_l5.setText(Messages.CompositeItem_32);
			
			availableOdata = new Button(this, SWT.CHECK);
			availableOdata.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		}

		// to show only if it is a report
		if (item.isReport()) {

			Label _l53 = new Label(this, SWT.NONE);
			_l53.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			_l53.setText(Messages.CompositeItem_15);

			Composite compo = new Composite(this, SWT.NONE);
			compo.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			compo.setLayout(new GridLayout(2, false));

			comboFormatting = new ComboViewer(compo, SWT.NONE);
			comboFormatting.getCombo().setLayoutData(new GridData());
			comboFormatting.setContentProvider(new IStructuredContentProvider() {

				@Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

				@Override
				public void dispose() {}

				@Override
				@SuppressWarnings("unchecked")
				public Object[] getElements(Object inputElement) {
					List l = (List) inputElement;
					return l.toArray(new Object[l.size()]);
				}
			});
			comboFormatting.setLabelProvider(new LabelProvider() {

				@Override
				public String getText(Object element) {
					if (element instanceof Variable)
						return ((Variable) element).getName();
					else
						return "error"; //$NON-NLS-1$
				}
			});
			comboFormatting.setInput(getReportFormattingVariables());
			comboFormatting.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					if (!comboFormatting.getSelection().isEmpty()) {
						Variable var = (Variable) ((StructuredSelection) comboFormatting.getSelection()).getFirstElement();
						lFormat.setText(var.getValue());
					}
				}
			});
			comboFormatting.setInput(getReportFormattingVariables());

			lFormat = new Label(compo, SWT.NONE);
			lFormat.setLayoutData(new GridData());

			Label _l54 = new Label(this, SWT.NONE);
			_l54.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			_l54.setText(Messages.CompositeItem_151);

			Composite compo2 = new Composite(this, SWT.NONE);
			compo2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			compo2.setLayout(new GridLayout(2, false));

			comboDefaultReportFormat = new ComboViewer(compo2, SWT.NONE);
			comboDefaultReportFormat.getCombo().setLayoutData(new GridData());
			comboDefaultReportFormat.setContentProvider(new IStructuredContentProvider() {

				@Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

				@Override
				public void dispose() {}

				@Override
				@SuppressWarnings("unchecked")
				public Object[] getElements(Object inputElement) {
					List l = (List) inputElement;
					return l.toArray(new Object[l.size()]);
				}
			});
			comboDefaultReportFormat.setLabelProvider(new LabelProvider() {
				public String getText(Object element) {
					if (element instanceof String)
						return (String) element;
					else
						return "error"; //$NON-NLS-1$
				}
			});
			comboDefaultReportFormat.setInput(getReportFormattingVariables());
			comboDefaultReportFormat.setInput(availableFormats);

			lDefaultReportFormat = new Label(compo, SWT.NONE);
			lDefaultReportFormat.setLayoutData(new GridData());
		}

		Label l5 = new Label(this, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 3));
		l5.setText(Messages.CompositeItem_17);

		comment = new Text(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		comment.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		comment.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				modified = true;

			}

		});

		Label l7 = new Label(this, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.CompositeItem_19);

		internalVersion = new Text(this, SWT.BORDER);
		internalVersion.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		internalVersion.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				modified = true;
			}
		});

		Label l8 = new Label(this, SWT.NONE);
		l8.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l8.setText(Messages.CompositeItem_20);

		publicVersion = new Text(this, SWT.BORDER);
		publicVersion.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		publicVersion.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				modified = true;

			}

		});

		if (item.isReport()) {
			Label l9 = new Label(this, SWT.NONE);
			l9.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			l9.setText(Messages.CompositeItem_80);

			nbMaxHisto = new Text(this, SWT.BORDER);
			nbMaxHisto.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			nbMaxHisto.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					try {
						Integer.parseInt(nbMaxHisto.getText());
					} catch (Exception e1) {
						e1.printStackTrace();
						nbMaxHisto.setText(0 + ""); //$NON-NLS-1$
					}
					modified = true;
				}

			});
		}

		Label l10 = new Label(this, SWT.NONE);
		l10.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l10.setText(Messages.CompositeItem_21);

		creator = new Text(this, SWT.BORDER);
		creator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		creator.setEnabled(false);

		/*
		 * owner
		 */
		l10 = new Label(this, SWT.NONE);
		l10.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l10.setText(Messages.CompositeItem_22);

		Composite _c = new Composite(this, SWT.NONE);
		_c.setLayout(new GridLayout(2, false));
		_c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		owner = new Text(_c, SWT.BORDER);
		owner.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		owner.setEnabled(false);

		Button b = new Button(_c, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText(Messages.CompositeItem_23);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DilaogPickupUser d = new DilaogPickupUser(getShell());

				if (d.open() == DilaogPickupUser.OK) {
					itemOwner = d.getSelectedUser();
					owner.setText(itemOwner.getName());
					modified = true;
				}
			}
		});

		Label l11 = new Label(this, SWT.NONE);
		l11.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l11.setText(Messages.CompositeItem_24);

		creation = new Text(this, SWT.BORDER);
		creation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		creation.setEnabled(false);

		Label l12 = new Label(this, SWT.NONE);
		l12.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l12.setText(Messages.CompositeItem_25);

		modifier = new Text(this, SWT.BORDER);
		modifier.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		modifier.setEnabled(false);

		Label l13 = new Label(this, SWT.NONE);
		l13.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l13.setText(Messages.CompositeItem_26);

		modification = new Text(this, SWT.BORDER);
		modification.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		modification.setEnabled(false);

		Composite c = new Composite(this, SWT.NONE);
		c.setLayout(new GridLayout(2, true));
		c.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

		Button ok = new Button(c, SWT.PUSH);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		ok.setText(Messages.CompositeItem_27);
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (modified) {
					try {
						if (isOnServer()) {
							if (!associatedServer.getSelection().isEmpty()) {
								IStructuredSelection ss = (IStructuredSelection) associatedServer.getSelection();
								CompositeItem.this.item.setRuntimeUrl(((Server) ss.getFirstElement()).getUrl());
							}
						}
						if (itemOwner != null) {
							CompositeItem.this.item.setOwnerId(CompositeItem.this.itemOwner.getId());
						}
						else {
							CompositeItem.this.item.setOwnerId(0);
						}

						CompositeItem.this.item.setComment(comment.getText());
						CompositeItem.this.item.setItemName(name.getText());
						CompositeItem.this.item.setInternalVersion(internalVersion.getText());
						CompositeItem.this.item.setPublicVersion(publicVersion.getText());
						CompositeItem.this.item.setComment(comment.getText());
						CompositeItem.this.item.setDisplay(display.getSelection());

						CompositeItem.this.item.setAndroidSupported(supportAndroid.getSelection());

						if (CompositeItem.this.item.getType() == IRepositoryApi.FMDT_TYPE) {
							CompositeItem.this.item.setAvailableOData(availableOdata.getSelection());
						}

						if (CompositeItem.this.item.isReport()) {
							if (!comboFormatting.getSelection().isEmpty()) {
								Variable var = (Variable) ((StructuredSelection) comboFormatting.getSelection()).getFirstElement();
								CompositeItem.this.item.setFormattingVariableId(var.getId());
								CompositeItem.this.item.setNbMaxHisto(Integer.parseInt(nbMaxHisto.getText()));
							}

							if (!comboDefaultReportFormat.getSelection().isEmpty()) {
								String defaultReportFormat = (String) ((StructuredSelection) comboDefaultReportFormat.getSelection()).getFirstElement();
								CompositeItem.this.item.setDefaultFormat(defaultReportFormat);
							}
						}

						Activator.getDefault().getRepositoryApi().getAdminService().update(CompositeItem.this.item);

						ViewTree v = (ViewTree) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewTree.ID);
						if (v != null) {
							v.refresh();
						}

					} catch (Exception e1) {
						MessageDialog.openError(getShell(), Messages.CompositeItem_28, e1.getMessage());
						e1.printStackTrace();
					}
					modified = false;
				}

				fillData();
			}
		});

		Button cancel = new Button(c, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		cancel.setText(Messages.CompositeItem_29);
		cancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				fillData();

			}
		});

		fillData();
	}

	protected boolean isServerUsable(Server server, RepositoryItem item) {
		switch (item.getType()) {
		case IRepositoryApi.FASD_TYPE:
			if (server.getComponentNature().equals(VanillaComponentType.COMPONENT_UNITEDOLAP)) {
				return true;
			}
			else {
				return false;
			}
		case IRepositoryApi.GTW_TYPE:
			if (server.getComponentNature().equals(VanillaComponentType.COMPONENT_GATEWAY)) {
				return true;
			}
			else {
				return false;
			}
		case IRepositoryApi.CUST_TYPE:
		case IRepositoryApi.FWR_TYPE:
			if (server.getComponentNature().equals(VanillaComponentType.COMPONENT_REPORTING)) {
				return true;
			}
			else {
				return false;
			}
		}
		return true;
	}

	private void initServerExecutor() throws Exception {
		try {
			associatedServer.setInput(Activator.getDefault().getVanillaApi().getVanillaSystemManager().getServerNodes(false));
		} catch (Exception e) {
			associatedServer.setInput(new ArrayList<Server>());
		}
	}

	public void fillData() {
		name.setText(item.getItemName());
		comment.setText(item.getComment() != null ? item.getComment() : ""); //$NON-NLS-1$
		type.setText(IRepositoryApi.TYPES_NAMES[item.getType()]);
		if (item.getInternalVersion() == null) {
			internalVersion.setText("1"); //$NON-NLS-1$
		}
		else {
			internalVersion.setText(item.getInternalVersion() + ""); //$NON-NLS-1$
		}
		publicVersion.setText(item.getPublicVersion() + ""); //$NON-NLS-1$

		creator.setText(item.getOwnerId() + ""); //$NON-NLS-1$

		supportAndroid.setSelection(this.item.isAndroidSupported());
		if (this.item.getType() == IRepositoryApi.FMDT_TYPE) {
			availableOdata.setSelection(this.item.isAvailableOData());
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
		if (item.getDateCreation() != null) {
			creation.setText(sdf.format(item.getDateCreation()));
		}

		if (item.getDateModification() != null)
			modification.setText(sdf.format(item.getDateModification()));

		if (item.getModifiedBy() == null) {
			modifier.setText(item.getOwnerId() + ""); //$NON-NLS-1$
		}
		else {
			modifier.setText(item.getModifiedBy() + ""); //$NON-NLS-1$
		}

		if (item.getOwnerId().equals("")) { //$NON-NLS-1$
			modifier.setEnabled(false);
		}

		display.setSelection(item.isDisplay());

		directoryItemId.setText(String.valueOf(item.getId()));
//		modelId.setText(item.getModelId() + "");

		if (isOnServer()) {
			for (Server s : (List<Server>) associatedServer.getInput()) {
				if (item.getRuntimeUrl() != null && !item.getRuntimeUrl().equals("default") && s.getUrl().equals(item.getRuntimeUrl())) { //$NON-NLS-1$
					associatedServer.setSelection(new StructuredSelection(s));
					break;
				}
			}
		}

		if (item.isReport()) {
			nbMaxHisto.setText(item.getNbMaxHisto() + ""); //$NON-NLS-1$
			int formatId = item.getFormattingVariableId();

			Variable sel = null;
			for (Variable v : formatVariables) {
				if (v.getId() == formatId) {
					sel = v;
					break;
				}
			}
			if (sel != null) {
				comboFormatting.setSelection(new StructuredSelection(sel));
				lFormat.setText(sel.getValue());
			}
			else {
				MessageDialog.openError(getShell(), Messages.CompositeItem_31, Messages.CompositeItem_69 + item.getId() + Messages.CompositeItem_70);
			}
			
			if(item.getDefaultFormat() != null && !item.getDefaultFormat().isEmpty()) {
				comboDefaultReportFormat.getCombo().setText(item.getDefaultFormat());
			}
		}
	}

	private boolean isOnServer() {
		switch (item.getType()) {
		// case IRepositoryConnection.FAR_TYPE:
		// case IRepositoryConnection.FAV_TYPE:
		case IRepositoryApi.FASD_TYPE:
			// case IRepositoryConnection.FD_TYPE:
		case IRepositoryApi.FWR_TYPE:
		case IRepositoryApi.GTW_TYPE:
		case IRepositoryApi.BIW_TYPE:
			return true;

		case IRepositoryApi.CUST_TYPE:
			switch (item.getSubtype()) {
			case IRepositoryApi.BIRT_REPORT_SUBTYPE:
			case IRepositoryApi.JASPER_REPORT_SUBTYPE:
				return true;
			}
		}
		return false;
	}

	public List<Variable> getReportFormattingVariables() {

		formatVariables = new ArrayList<Variable>();

		Variable def = new Variable();
		def.setId(0);
		def.setName("default"); //$NON-NLS-1$
		def.setType(Variable.TYPE_OBJECT);
		def.setValue(""); //$NON-NLS-1$

		formatVariables.add(def);

		try {
			for (Variable v : Activator.getDefault().getVanillaApi().getVanillaSystemManager().getVariables()) {
				if (v.getType() == Variable.TYPE_OBJECT) {
					formatVariables.add(v);
				}
			}
		} catch (Exception e) {
			MessageDialog.openError(getShell(), Messages.CompositeItem_73, Messages.CompositeItem_74 + e.getMessage());
			e.printStackTrace();
		}

		return formatVariables;
	}
}
