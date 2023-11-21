package bpm.es.datasource.audit.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import bpm.es.datasource.analyzer.parsers.BIRTAnalyzer;
import bpm.es.datasource.analyzer.parsers.FASDAnalyzer;
import bpm.es.datasource.analyzer.parsers.FDDicoAnalyzer;
import bpm.es.datasource.analyzer.parsers.FWRAnalyzer;
import bpm.es.datasource.analyzer.parsers.IAnalyzer;
import bpm.es.datasource.analyzer.ui.Messages;
import bpm.es.datasource.analyzer.ui.icons.IconsNames;
import bpm.es.datasource.audit.AuditConfig;
import bpm.es.datasource.audit.AuditResult;
import bpm.es.datasource.audit.Auditer;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.viewers.RepositoryLabelProvider;

public class TestView extends ViewPart {

	public static final String ID = "bpm.es.datasource.audit.ui.views.TestView"; //$NON-NLS-1$
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private CheckboxTableViewer groupViewer;
	private TableViewer itemViewer;
	private TreeViewer resultViewer;
	private Button auditBtn;
	private HashMap<AuditConfig, List<AuditResult>> result;

	public TestView() {
	}

	@Override
	public void createPartControl(Composite parent) {

		Form frmBiObjectAudit = formToolkit.createForm(parent);
		formToolkit.paintBordersFor(frmBiObjectAudit);
		frmBiObjectAudit.setText(Messages.TestView_1);
		frmBiObjectAudit.getBody().setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite = new Composite(frmBiObjectAudit.getBody(), SWT.NONE);
		formToolkit.adapt(composite);
		formToolkit.paintBordersFor(composite);
		composite.setLayout(new GridLayout(1, false));

		Section sctnAuditDefinition = formToolkit.createSection(composite, Section.TWISTIE | Section.TITLE_BAR);
		sctnAuditDefinition.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sctnAuditDefinition.setBounds(0, 0, 98, 21);
		formToolkit.paintBordersFor(sctnAuditDefinition);
		sctnAuditDefinition.setText(Messages.TestView_2);
		sctnAuditDefinition.setExpanded(true);

		Composite composite_1 = new Composite(sctnAuditDefinition, SWT.NONE);
		formToolkit.adapt(composite_1);
		formToolkit.paintBordersFor(composite_1);
		sctnAuditDefinition.setClient(composite_1);
		composite_1.setLayout(new GridLayout(2, true));

		Composite c = new Composite(composite_1, SWT.NONE);
		c.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(c);
		formToolkit.paintBordersFor(c);

		c.setLayout(new GridLayout(2, false));

		Label lblDropTheObjects = new Label(c, SWT.NONE);
		lblDropTheObjects.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblDropTheObjects.setBounds(0, 0, 55, 15);
		formToolkit.adapt(lblDropTheObjects, true, true);
		lblDropTheObjects.setText(Messages.TestView_3);

		Button b = formToolkit.createButton(c, "", SWT.PUSH); //$NON-NLS-1$
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setImage(bpm.es.datasource.analyzer.ui.Activator.getDefault().getImageRegistry().get(IconsNames.DEL));
		b.setToolTipText(Messages.TestView_5);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (itemViewer.getInput() != null) {
					((List) itemViewer.getInput()).clear();
					itemViewer.refresh();
					auditBtn.setEnabled(false);
				}

			}
		});

		c = new Composite(composite_1, SWT.NONE);
		c.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(c);
		formToolkit.paintBordersFor(c);

		c.setLayout(new GridLayout(3, false));

		Label lblSelectTheGroups = new Label(c, SWT.NONE);
		lblSelectTheGroups.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblSelectTheGroups.setBounds(0, 0, 55, 15);
		formToolkit.adapt(lblSelectTheGroups, true, true);
		lblSelectTheGroups.setText(Messages.TestView_6);

		b = formToolkit.createButton(c, "", SWT.PUSH); //$NON-NLS-1$
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setToolTipText(Messages.TestView_8);
		b.setImage(bpm.es.datasource.analyzer.ui.Activator.getDefault().getImageRegistry().get(IconsNames.REFRESH));
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					groupViewer.setInput(Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups());
				} catch (Exception ex) {
					ex.printStackTrace();
					groupViewer.setInput(new ArrayList());
					MessageDialog.openError(getSite().getShell(), Messages.TestView_9, Messages.TestView_10 + ex.getMessage());
				}

			}
		});

		final Button checkAll = formToolkit.createButton(c, "", SWT.CHECK); //$NON-NLS-1$
		checkAll.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		checkAll.setToolTipText(Messages.TestView_12);
		checkAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List l = null;
				if (checkAll.getSelection()) {
					l = (List) groupViewer.getInput();
				}
				else {
					l = Collections.EMPTY_LIST;
				}

				groupViewer.setCheckedElements(l.toArray(new Object[l.size()]));
				groupViewer.refresh();
			}
		});

		itemViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		itemViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		itemViewer.getTable().setBounds(0, 0, 200, 50);
		formToolkit.paintBordersFor(itemViewer.getTable());

		groupViewer = CheckboxTableViewer.newCheckList(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		groupViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		groupViewer.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (groupViewer.getCheckedElements().length != ((List) groupViewer.getInput()).size()) {
					checkAll.setGrayed(true);
				}

				if (groupViewer.getCheckedElements().length > 0 && itemViewer.getInput() != null && !((List) itemViewer.getInput()).isEmpty()) {
					auditBtn.setEnabled(true);
				}
				else {
					auditBtn.setEnabled(false);
				}

			}

		});

		formToolkit.paintBordersFor(groupViewer.getTable());

		Section sctnAuitResult = formToolkit.createSection(composite, Section.TWISTIE | Section.TITLE_BAR);
		sctnAuitResult.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(sctnAuitResult);
		sctnAuitResult.setText(Messages.TestView_13);
		sctnAuitResult.setExpanded(true);

		Composite composite_2 = new Composite(sctnAuitResult, SWT.NONE);
		formToolkit.adapt(composite_2);
		formToolkit.paintBordersFor(composite_2);
		sctnAuitResult.setClient(composite_2);
		composite_2.setLayout(new GridLayout(1, false));

		Composite composite_3 = new Composite(composite_2, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_3.setBounds(0, 0, 64, 64);
		formToolkit.adapt(composite_3);
		formToolkit.paintBordersFor(composite_3);
		composite_3.setLayout(new GridLayout(4, false));

		auditBtn = new Button(composite_3, SWT.PUSH);
		auditBtn.setLayoutData(new GridData());
		auditBtn.setToolTipText(Messages.TestView_14);
		auditBtn.setEnabled(false);
		auditBtn.setImage(bpm.es.datasource.analyzer.ui.Activator.getDefault().getImageRegistry().get(IconsNames.AUDIT));
		auditBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				runAudit();
			}
		});

		final Button bStart = new Button(composite_3, SWT.PUSH);
		bStart.setLayoutData(new GridData());
		bStart.setImage(bpm.es.datasource.analyzer.ui.Activator.getDefault().getImageRegistry().get(IconsNames.STOP));
		bStart.setToolTipText(Messages.TestView_15);
		bStart.setEnabled(false);
		bStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				turnOnSelection(false);
			}
		});

		final Button bStop = new Button(composite_3, SWT.PUSH);
		bStop.setLayoutData(new GridData());
		bStop.setImage(bpm.es.datasource.analyzer.ui.Activator.getDefault().getImageRegistry().get(IconsNames.START));
		bStop.setToolTipText(Messages.TestView_16);
		bStop.setEnabled(false);
		bStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				turnOnSelection(true);
			}
		});

		resultViewer = new TreeViewer(composite_3, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		resultViewer.getTree().setHeaderVisible(true);
		resultViewer.getTree().setLinesVisible(true);
		resultViewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 4, 1));
		resultViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				bStop.setEnabled(!event.getSelection().isEmpty());
				bStart.setEnabled(!event.getSelection().isEmpty());

			}
		});

		formToolkit.paintBordersFor(resultViewer.getTree());

		/*
		 * created Viewers
		 */
		createItemViewer();
		createGroupViewer();
		createResultViewer();
	}

	protected void turnOnSelection(boolean value) {
		StringBuffer buf = new StringBuffer();

		for (Object o : ((IStructuredSelection) resultViewer.getSelection()).toList()) {
			if (o instanceof AuditConfig) {
				AuditConfig c = (AuditConfig) o;
				try {
					RepositoryItem d = ((RepositoryItem) c.getRootItem());
					d.setOn(value);
					Activator.getDefault().getRepositoryApi().getAdminService().update(d);
					if (value) {
						buf.append(d.getItemName() + Messages.TestView_17);
					}
					else {
						buf.append(d.getItemName() + Messages.TestView_18);
					}

				} catch (Exception ex) {
					if (value) {
						buf.append(Messages.TestView_19 + c.getRootItem().getItemName() + " " + ex.getMessage() + "\n");  //$NON-NLS-1$//$NON-NLS-2$
					}
					else {
						buf.append(Messages.TestView_22 + c.getRootItem().getItemName() + " " + ex.getMessage() + "\n");  //$NON-NLS-1$//$NON-NLS-2$
					}

				}
			}
		}

		MessageDialog.openInformation(getSite().getShell(), Messages.TestView_25, buf.toString());

	}

	private void createResultViewer() {
		resultViewer.getTree().setHeaderVisible(true);
		resultViewer.getTree().setLinesVisible(true);
		resultViewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;

				return c.toArray(new Object[c.size()]);
			}

			@Override
			public boolean hasChildren(Object element) {
				List l = result.get(element);
				if (l == null) {
					return false;
				}
				return !l.isEmpty();
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof AuditConfig) {
					Collection c = result.get(parentElement);
					if (c != null) {
						return c.toArray(new Object[c.size()]);
					}
				}
				return null;
			}
		});

		TreeViewerColumn col = new TreeViewerColumn(resultViewer, SWT.NONE);
		col.getColumn().setText(Messages.TestView_26);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ResultColumnLabelProvider(Type.GROUP));

		col = new TreeViewerColumn(resultViewer, SWT.NONE);
		col.getColumn().setText(Messages.TestView_27);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ResultColumnLabelProvider(Type.ITEM));

		col = new TreeViewerColumn(resultViewer, SWT.NONE);
		col.getColumn().setText(Messages.TestView_28);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ResultColumnLabelProvider(Type.DEPENDANCYITEM));

		col = new TreeViewerColumn(resultViewer, SWT.NONE);
		col.getColumn().setText(Messages.TestView_29);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ResultColumnLabelProvider(Type.DATASET));

		col = new TreeViewerColumn(resultViewer, SWT.NONE);
		col.getColumn().setText(Messages.TestView_30);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ResultColumnLabelProvider(Type.FAILURE));

		col = new TreeViewerColumn(resultViewer, SWT.NONE);
		col.getColumn().setText(Messages.TestView_31);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ResultColumnLabelProvider(Type.QUERY));
	}

	private void createItemViewer() {
		itemViewer.setContentProvider(new ArrayContentProvider());
		itemViewer.setLabelProvider(new RepositoryLabelProvider());
		itemViewer.getTable().setHeaderVisible(true);
		itemViewer.getTable().setLinesVisible(true);

		TableColumn column = new TableColumn(itemViewer.getTable(), SWT.NONE);
		column.setText(Messages.TestView_32);
		column.setWidth(200);

		final Transfer[] transferts = new Transfer[] { TextTransfer.getInstance() };
		this.itemViewer.addDropSupport(DND.DROP_COPY, transferts, new DropTargetListener() {

			@Override
			public void dropAccept(DropTargetEvent event) {
				event.detail = event.operations;

			}

			@Override
			public void drop(DropTargetEvent event) {
				String directoryItemId = (String) event.data;
				RepositoryItem item = null;

				try {
					item = Activator.getDefault().getRepositoryApi().getRepositoryService().getDirectoryItem(Integer.parseInt(directoryItemId));
				} catch (Exception ex) {
					return;
				}

				if (item == null) {
					return;
				}
				if (itemViewer.getInput() == null) {
					itemViewer.setInput(new ArrayList());
				}
				((List) itemViewer.getInput()).add(item);
				auditBtn.setEnabled(groupViewer.getCheckedElements().length > 0);
				itemViewer.refresh();
			}

			@Override
			public void dragOver(DropTargetEvent event) {
				event.detail = event.operations;

			}

			@Override
			public void dragOperationChanged(DropTargetEvent event) {
			}

			@Override
			public void dragLeave(DropTargetEvent event) {
			}

			@Override
			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;

			}
		});
	}

	private void createGroupViewer() {
		groupViewer.setContentProvider(new ArrayContentProvider());
		groupViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Group) element).getName();
			}
		});
		groupViewer.getTable().setHeaderVisible(true);
		groupViewer.getTable().setLinesVisible(true);

		TableColumn column = new TableColumn(groupViewer.getTable(), SWT.NONE);
		column.setText(Messages.TestView_33);
		column.setWidth(200);

	}

	@Override
	public void setFocus() {
	}

	private void runAudit() {
		List<AuditConfig> confs = new ArrayList<AuditConfig>();

		for (Object o : (List) itemViewer.getInput()) {
			RepositoryItem item = (RepositoryItem) o;

			List<RepositoryItem> list = new ArrayList<RepositoryItem>();
			if (item.getType() == IRepositoryApi.FASD_TYPE || item.getType() == IRepositoryApi.FWR_TYPE || (item.getType() == IRepositoryApi.CUST_TYPE)) {
				list.add(item);
			}
			try {
				list.addAll(Activator.getDefault().getRepositoryApi().getRepositoryService().getNeededItems(item.getId()));
				for (RepositoryItem it : list) {
					IAnalyzer a = null;
					switch (it.getType()) {
						case IRepositoryApi.FD_DICO_TYPE:
							a = new FDDicoAnalyzer();
							break;
						case IRepositoryApi.CUST_TYPE:
							if (it.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
								a = new BIRTAnalyzer();
							}
							break;
						case IRepositoryApi.FWR_TYPE:
							a = new FWRAnalyzer();
							break;
						case IRepositoryApi.FASD_TYPE:
							a = new FASDAnalyzer();
					}

					if (a == null) {
						continue;
					}

					for (Object group : groupViewer.getCheckedElements()) {
						try {
							String xml = Activator.getDefault().getRepositoryApi().getRepositoryService().loadModel(it);

							confs.add(new AuditConfig(item, it, xml, (Group) group, a));

						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

		String userName = null;
		String password = null;
		try {
			userName = Activator.getDefault().getLogin();
			password = Activator.getDefault().getUserPassword();
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(getSite().getShell(), Messages.TestView_7, Messages.TestView_11);
		}

		Repository rep = Activator.getDefault().getCurrentRepository();
		Auditer auditer = new Auditer();

		result = auditer.audit(new BaseRepositoryContext(new BaseVanillaContext(Activator.getDefault().getRepositoryApi().getContext().getVanillaContext().getVanillaUrl(), userName, password), null, rep), confs);

		resultViewer.setInput(result.keySet());
	}

	static public enum Type {
		ITEM, DEPENDANCYITEM, GROUP, DATASET, FAILURE, QUERY
	}

	private class ResultColumnLabelProvider extends ColumnLabelProvider {

		private Type type;

		public ResultColumnLabelProvider(Type type) {
			this.type = type;
		}

		@Override
		public String getText(Object element) {
			String s = null;
			switch (type) {
				case DATASET:
					if (element instanceof AuditConfig) {
						return ""; //$NON-NLS-1$
					}
					return ((AuditResult) element).getDataSetName();
				case DEPENDANCYITEM:
					if (element instanceof AuditResult) {
						return ""; //$NON-NLS-1$
					}
					return ((AuditConfig) element).getAnalyzedItem().getItemName();
				case FAILURE:
					if (element instanceof AuditResult) {
						s = ((AuditResult) element).getFailulreCause();
						if (s == null) {
							return Messages.TestView_36;
						}
						return s;
					}
					return ""; //$NON-NLS-1$
				case GROUP:
					if (element instanceof AuditResult) {
						return ""; //$NON-NLS-1$
					}
					return ((AuditConfig) element).getGroup().getName();
				case ITEM:
					if (element instanceof AuditResult) {
						return ""; //$NON-NLS-1$
					}
					return ((AuditConfig) element).getRootItem().getItemName();
				case QUERY:
					if (element instanceof AuditResult) {
						s = ((AuditResult) element).getEffectiveQuery();
						if (s == null) {
							return "Could not generate"; //$NON-NLS-1$
						}
						return s;
					}
					return ""; //$NON-NLS-1$
			}
			return ""; //$NON-NLS-1$
		}

		@Override
		public Color getBackground(Object element) {
			if (element instanceof AuditConfig) {
				boolean containsError = false;
				boolean fullError = true;
				for (AuditResult l : result.get((AuditConfig) element)) {
					if (l.getFailulreCause() == null) {
						fullError = false;
					}
					else {
						containsError = true;
					}
				}
				if (fullError) {
					return RED;
				}
				else if (containsError) {
					return WARN;
				}

			}
			else {
				if (((AuditResult) element).getFailulreCause() != null) {
					return RED;
				}
			}
			return super.getBackground(element);
		}
	}

	private final static Color RED = new Color(Display.getDefault(), 255, 0, 0);
	private final static Color WARN = new Color(Display.getDefault(), 255, 201, 14);
}
