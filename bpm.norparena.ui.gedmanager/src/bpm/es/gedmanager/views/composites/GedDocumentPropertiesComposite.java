package bpm.es.gedmanager.views.composites;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import bpm.es.gedmanager.Activator;
import bpm.es.gedmanager.Messages;
import bpm.es.gedmanager.api.GedModel;
import bpm.es.gedmanager.dialogs.DialogChangePerempDate;
import bpm.es.gedmanager.icons.IconsNames;
import bpm.es.gedmanager.views.viewers.DocumentViewerComparator;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;

/**
 * Used to show/update the properties of a GedDocument This will also display the versions and the security of the document
 * 
 * @author Marc Lanquetin
 * 
 */
public class GedDocumentPropertiesComposite extends Composite {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Text txtId;
	private Text txtName;
	private TableViewer versionsTable;
	private CheckboxTreeViewer securityTree;

	private GedDocument document;
	private GedModel model;

	private List<Group> currentgroups = new ArrayList<Group>();

	public GedDocumentPropertiesComposite(Composite parent, int style, GedModel model) {
		super(parent, style);
		this.model = model;

		Label lblId = new Label(this, SWT.NONE);
		lblId.setText("Id");
		lblId.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		txtId = new Text(this, SWT.BORDER);
		txtId.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtId.setEnabled(false);

		Label lblName = new Label(this, SWT.NONE);
		lblName.setText("Name");
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		txtName = new Text(this, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		securityTree = new CheckboxTreeViewer(this);
		securityTree.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		securityTree.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				try {
					GedDocumentPropertiesComposite.this.model.updateSecurity(document, (Group) event.getElement(), event.getChecked());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		createSecurityTreeProviders();

		versionsTable = new TableViewer(this);
		createColumns();
		versionsTable.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		createVersionTableProvider();
	}

	private void createSecurityTreeProviders() {
		securityTree.setContentProvider(new ITreeContentProvider() {

			public Object[] getChildren(Object parentElement) {
				if (parentElement == currentgroups) {
					List<Group> l = new ArrayList<Group>((List<Group>) parentElement);

					List<Group> toRemove = new ArrayList<Group>();
					for (Group g : l) {
						if (g.getParentId() != null) {
							toRemove.add(g);
						}
					}
					l.removeAll(toRemove);

					return l.toArray(new Group[l.size()]);
				}
				else {
					List<Group> l = new ArrayList<Group>();
					for (Group g : currentgroups) {
						if (((Group) parentElement).getId().equals(g.getParentId())) {
							l.add(g);
						}
					}
					if (l.size() == 0) {
						return null;
					}
					return l.toArray(new Group[l.size()]);
				}

			}

			public Object getParent(Object element) {
				if (element instanceof Group && ((Group) element).getParentId() == null) {
					return currentgroups;
				}
				else {
					for (Group g : currentgroups) {
						if (g.getId().equals(((Group) element).getParentId())) {
							return g;
						}
					}
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				for (Group g : currentgroups) {
					if (((Group) element).getId().equals(g.getParentId())) {
						return true;
					}
				}
				return false;
			}

			public Object[] getElements(Object inputElement) {
				List<Group> l = new ArrayList<Group>((List<Group>) inputElement);

				List<Group> toRemove = new ArrayList<Group>();
				for (Group g : l) {
					if (g.getParentId() != null) {
						toRemove.add(g);
					}
				}
				l.removeAll(toRemove);

				return l.toArray(new Group[l.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		securityTree.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Group) element).getName();
			}
		});
	}

	/**
	 * Create the Label and content providers for the versions table
	 */
	private void createVersionTableProvider() {

		MenuManager menu = new MenuManager();
		menu.add(indexDocAction);
		menu.add(changePeremption);

		versionsTable.getControl().setMenu(menu.createContextMenu(versionsTable.getControl()));
		versionsTable.setContentProvider(new IStructuredContentProvider() {
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				if (inputElement != null && inputElement instanceof GedDocument) {
					return ((GedDocument) inputElement).getDocumentVersions().toArray();
				}
				return null;
			}
		});
		versionsTable.setLabelProvider(new ITableLabelProvider() {
			public void removeListener(ILabelProviderListener listener) {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void dispose() {
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public String getColumnText(Object element, int columnIndex) {
				DocumentVersion doc = (DocumentVersion) element;
				switch (columnIndex) {
				case 0:
					return doc.getParent().getName();
				case 1:
					if (doc.isIndexed()) {
						return doc.getSummary() != null && !doc.getSummary().equals("") ? doc.getSummary() : "No summary for this document";
					}
					return "The document is not indexed";
				case 2:
					return sdf.format(doc.getModificationDate());
				case 3:
					return "V " + doc.getVersion();
				case 4:
					return doc.getFormat();
				case 5:
					if (doc.getPeremptionDate() == null) {
						return "";
					}
					return sdf.format(doc.getPeremptionDate());
				default:
					return ""; //$NON-NLS-1$
				}
			}

			public Image getColumnImage(Object element, int columnIndex) {
				DocumentVersion doc = (DocumentVersion) element;

				switch (columnIndex) {
				case 0:
					if (doc.getFormat().toLowerCase().endsWith("pdf")) //$NON-NLS-1$
						return Activator.getDefault().getImageRegistry().get(IconsNames.document_pdf);
					else if (doc.getFormat().toLowerCase().endsWith("txt") || //$NON-NLS-1$
							doc.getFormat().toLowerCase().endsWith("doc"))
						return Activator.getDefault().getImageRegistry().get(IconsNames.document_word);
					else if (doc.getFormat().toLowerCase().endsWith("odt"))
						return Activator.getDefault().getImageRegistry().get(IconsNames.document_odt);
					else if (doc.getFormat().toLowerCase().endsWith("xls"))
						return Activator.getDefault().getImageRegistry().get(IconsNames.document_excel);
					else if (doc.getFormat().toLowerCase().equals("ods"))
						return Activator.getDefault().getImageRegistry().get(IconsNames.document_ods);
					else if (doc.getFormat().toLowerCase().endsWith("zip") || //$NON-NLS-1$
							doc.getFormat().toLowerCase().endsWith("rar")) //$NON-NLS-1$
						return Activator.getDefault().getImageRegistry().get(IconsNames.document_compressed);
					else
						return Activator.getDefault().getImageRegistry().get(IconsNames.document);
				default:
					return null;
				}
			}
		});
	}

	private String[] titles = { Messages.GedDocumentsComposite_25, Messages.GedDocumentsComposite_26, Messages.GedDocumentsComposite_27, Messages.GedDocumentsComposite_28, Messages.GedDocumentsComposite_29, "Peremption date" };

	/**
	 * Create the columns for the versions table
	 */
	private void createColumns() {

		int[] bounds = { 120, 200, 125, 70, 70, 100 };

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(versionsTable, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);

			column.getColumn().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String text = ((TableColumn) e.getSource()).getText();
					int i = 0;
					for (; i < titles.length; i++) {
						if (text.equals(titles[i])) {
							break;
						}
					}

					versionsTable.setComparator(new DocumentViewerComparator(i));
				}
			});
		}
		Table table = versionsTable.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

	}

	public void initData(GedDocument document) throws Exception {
		this.document = document;

		if (document != null) {
			versionsTable.setInput(document);

			txtId.setText(document.getId() + "");
			txtName.setText(document.getName());

			currentgroups = bpm.norparena.ui.menu.Activator.getDefault().getRemote().getVanillaSecurityManager().getGroups();
			securityTree.setInput(currentgroups);

			List<Integer> authorizedGroups = model.getSecurityForDocument(document);

			List<Group> selected = new ArrayList<Group>();

			for (Group i : currentgroups) {
				for (Integer g : authorizedGroups) {
					if (g.equals(i.getId())) {
						selected.add(i);
						break;
					}
				}
			}

			securityTree.setCheckedElements(selected.toArray());
		}
		else {
			versionsTable.setInput(null);

			txtId.setText("");
			txtName.setText("");

			securityTree.setInput(new ArrayList<Group>());
		}
	}

	private Action indexDocAction = new Action("Index document") {
		@Override
		public void run() {
			DocumentVersion version = (DocumentVersion) ((IStructuredSelection) versionsTable.getSelection()).getFirstElement();
			if (version == null) {
				MessageDialog.openError(getShell(), "Error", "Please select the file to index first.");
				return;
			}
			try {
				model.indexVersion(version);
				model.refresh();
				initData(document);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), "Error", "Error while trying to index the document - " + e.getMessage());
			}
		}
	};

	private Action changePeremption = new Action("Change peremption date") {
		public void run() {

			DialogChangePerempDate dial = new DialogChangePerempDate(getShell(), (DocumentVersion) ((IStructuredSelection) versionsTable.getSelection()).getFirstElement(), model);
			if (dial.open() == Dialog.OK) {

				try {
					model.refresh();
					initData(document);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
	};

}
