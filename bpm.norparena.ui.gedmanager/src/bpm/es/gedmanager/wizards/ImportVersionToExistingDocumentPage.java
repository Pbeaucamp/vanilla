package bpm.es.gedmanager.wizards;

import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import bpm.es.gedmanager.Activator;
import bpm.es.gedmanager.Messages;
import bpm.es.gedmanager.api.GedModel;
import bpm.es.gedmanager.icons.IconsNames;
import bpm.es.gedmanager.views.viewers.DocumentViewerComparator;
import bpm.vanilla.platform.core.beans.ged.GedDocument;

public class ImportVersionToExistingDocumentPage extends WizardPage {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String[] TITLES = { Messages.GedDocumentsComposite_25, Messages.GedDocumentsComposite_27 };

	private TableViewer documentTable;
	private Text txtFile;
	private Button btnFile;

	private String fileName;

	private GedModel model;

	private GedDocument selectedDoc;

	protected ImportVersionToExistingDocumentPage(String pageName, GedModel model) {
		super(pageName);
		this.model = model;
	}

	@Override
	public void createControl(Composite parent) {

		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblDoc = new Label(main, SWT.NONE);
		lblDoc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblDoc.setText("Document");

		documentTable = new TableViewer(main);
		createColumns(documentTable);
		documentTable.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		documentTable.setContentProvider(provider);
		documentTable.setLabelProvider(labelProvider);
		documentTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedDoc = (GedDocument) ((IStructuredSelection) event.getSelection()).getFirstElement();
			}
		});

		Label lblFile = new Label(main, SWT.NONE);
		lblFile.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblFile.setText("File");

		txtFile = new Text(main, SWT.BORDER);
		txtFile.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtFile.setEnabled(false);
		txtFile.addModifyListener(listener);

		btnFile = new Button(main, SWT.PUSH);
		btnFile.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnFile.setText("...");
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dial = new FileDialog(ImportVersionToExistingDocumentPage.this.getShell());
				fileName = dial.open();
				txtFile.setText(fileName);
			}
		});

		setControl(main);

		try {
			createInput();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void createColumns(TableViewer viewer) {

		int[] bounds = { 150, 300, 125, 50, 100 };

		for (int i = 0; i < TITLES.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(TITLES[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);

			column.getColumn().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String text = ((TableColumn) e.getSource()).getText();
					for (int i = 0; i < TITLES.length; i++) {
						if (text.equals(TITLES[i])) {
							documentTable.setComparator(new DocumentViewerComparator(i));
							break;
						}
					}
				}
			});
		}
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private void createInput() throws Exception {
		documentTable.setInput(model.getDocuments());
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	public void updateWizardButtons() {
		getContainer().updateButtons();
	}

	ModifyListener listener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			updateWizardButtons();
		}
	};

	IStructuredContentProvider provider = new IStructuredContentProvider() {
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return ((List<GedDocument>) inputElement).toArray();
		}
	};

	ITableLabelProvider labelProvider = new ITableLabelProvider() {
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
			GedDocument doc = (GedDocument) element;
			switch (columnIndex) {
			case 0:
				return doc.getName();
			case 2:
				try {
					return sdf.format(doc.getCreationDate());
				} catch (Exception e) {
					e.printStackTrace();
					return ""; //$NON-NLS-1$
				}
			default:
				return ""; //$NON-NLS-1$
			}
		}

		public Image getColumnImage(Object element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Activator.getDefault().getImageRegistry().get(IconsNames.document);
			default:
				return null;
			}
		}
	};

	@Override
	public boolean isPageComplete() {
		if (txtFile.getText() != null && !txtFile.getText().equals("") && selectedDoc != null) {
			return true;
		}
		return false;
	}

	public String getFile() {
		return txtFile.getText();
	}

	public GedDocument getDocument() {
		return selectedDoc;
	}
}
