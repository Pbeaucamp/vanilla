package org.fasd.drill.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.CubeView;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPMeasure;
import org.fasd.utils.trees.TreeContentProvider;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeLabelProvider;
import org.fasd.utils.trees.TreeMember;
import org.fasd.utils.trees.TreeObject;
import org.fasd.utils.trees.TreeParent;

public class DialogDrill extends Dialog {

	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
	}

	private CheckboxTreeViewer tree;
	private ComboViewer dimensions;
	private ListViewer measures;
	private OLAPCube cube;
	private Text name;
	private ListViewer rows, cols;
	private CubeView view;

	public DialogDrill(Shell parentShell, OLAPCube cube) {
		super(parentShell);
		this.cube = cube;
	}

	public DialogDrill(Shell parentShell, OLAPCube cube, CubeView view) {
		super(parentShell);
		this.cube = cube;
		this.view = view;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l0 = new Label(container, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		l0.setText(LanguageText.DialogDrill_0);

		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Composite selection = new Composite(container, SWT.NONE);
		selection.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		selection.setLayout(new GridLayout(2, false));

		Label l = new Label(selection, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(LanguageText.DialogDrill_1);

		dimensions = new ComboViewer(selection, SWT.BORDER);
		dimensions.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dimensions.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<OLAPDimension> dims = (List<OLAPDimension>) inputElement;
				return dims.toArray(new OLAPDimension[dims.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		dimensions.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((OLAPDimension) element).getName();
			}

		});
		dimensions.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) dimensions.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				OLAPDimension dim = (OLAPDimension) ss.getFirstElement();

				TreeParent tp = dim.createModel();
				tree.setInput(tp);

			}

		});

		Label l2 = new Label(selection, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l2.setText(LanguageText.DialogDrill_2);

		tree = new CheckboxTreeViewer(selection, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		tree.setLabelProvider(new TreeLabelProvider());
		tree.setContentProvider(new TreeContentProvider());

		Label l3 = new Label(selection, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l3.setText(LanguageText.DialogDrill_3);

		measures = new ListViewer(selection, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		measures.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		measures.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<OLAPMeasure> mes = (List<OLAPMeasure>) inputElement;
				return mes.toArray(new OLAPMeasure[mes.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		measures.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((OLAPMeasure) element).getName();
			}

		});

		Composite buttonBar = new Composite(container, SWT.NONE);
		buttonBar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		buttonBar.setLayout(new GridLayout(1, false));

		Button addCol = new Button(buttonBar, SWT.PUSH);
		addCol.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		addCol.setText(LanguageText.DialogDrill_4);
		addCol.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object[] checkd = (Object[]) tree.getCheckedElements();
				List<String> l = (List<String>) cols.getInput();
				boolean error = false;

				for (Object o : checkd) {

					if (o instanceof TreeMember || o instanceof TreeHierarchy) {
						String s = ((TreeObject) o).getFullName().substring(1);
						if (isAllowed(rows, s)) {
							l.add(s);
						} else {
							error = true;
						}
					}

				}
				tree.setAllChecked(false);
				tree.refresh();

				IStructuredSelection ss = (IStructuredSelection) measures.getSelection();

				for (Object o : ss.toList()) {
					String s = "[Measures].[" + ((OLAPMeasure) o).getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$

					if (isAllowed(rows, s)) {
						l.add(s);
					} else {
						error = true;
					}
				}

				measures.setSelection(new StructuredSelection());
				cols.refresh();

				if (error) {
					MessageDialog.openInformation(getShell(), LanguageText.DialogDrill_7, LanguageText.DialogDrill_8);
				}
			}

		});

		Button addRow = new Button(buttonBar, SWT.PUSH);
		addRow.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		addRow.setText(LanguageText.DialogDrill_9);
		addRow.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object[] checkd = (Object[]) tree.getCheckedElements();
				List<String> l = (List<String>) rows.getInput();
				boolean error = false;

				for (Object o : checkd) {

					if (o instanceof TreeMember || o instanceof TreeHierarchy) {
						String s = ((TreeObject) o).getFullName().substring(1);
						if (isAllowed(cols, s)) {
							l.add(s);
						} else {
							error = true;
						}
					}

				}
				tree.setAllChecked(false);
				tree.refresh();

				IStructuredSelection ss = (IStructuredSelection) measures.getSelection();

				for (Object o : ss.toList()) {
					String s = "[Measures].[" + ((OLAPMeasure) o).getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$

					if (isAllowed(cols, s)) {
						l.add(s);
					} else {
						error = true;
					}
				}

				measures.setSelection(new StructuredSelection());
				rows.refresh();
				if (error) {
					MessageDialog.openInformation(getShell(), LanguageText.DialogDrill_12, LanguageText.DialogDrill_13);
				}
			}

		});

		Button remove = new Button(buttonBar, SWT.PUSH);
		remove.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		remove.setText(LanguageText.DialogDrill_14);
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) cols.getSelection();
				List l = (List) cols.getInput();
				for (Object o : ss.toList()) {
					l.remove(o);
				}

				ss = (IStructuredSelection) rows.getSelection();
				l = (List) rows.getInput();
				for (Object o : ss.toList()) {
					l.remove(o);
				}

				cols.refresh();
				rows.refresh();
			}

		});

		Composite query = new Composite(container, SWT.NONE);
		query.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		query.setLayout(new GridLayout(1, false));

		Label l4 = new Label(query, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l4.setText(LanguageText.DialogDrill_15);

		cols = new ListViewer(query, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		cols.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		cols.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>) inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		cols.setLabelProvider(new LabelProvider());

		Label l5 = new Label(query, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l5.setText(LanguageText.DialogDrill_16);

		rows = new ListViewer(query, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		rows.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		rows.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>) inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		rows.setLabelProvider(new LabelProvider());

		createModels();
		return container;
	}

	private void createModels() {
		dimensions.setInput(cube.getDims());
		measures.setInput(cube.getMes());

		if (view == null) {
			cols.setInput(new ArrayList<String>());
			rows.setInput(new ArrayList<String>());
		} else {
			cols.setInput(view.getCols());
			rows.setInput(view.getRows());
		}

	}

	private void setCubeView() {
		view = new CubeView();

		List<String> c = (List<String>) cols.getInput();
		List<String> r = (List<String>) rows.getInput();

		view.setName(name.getText());

		for (String s : c) {
			view.addCol(s);
		}

		for (String s : r) {
			view.addRow(s);
		}
	}

	@Override
	protected void okPressed() {
		setCubeView();
		super.okPressed();
	}

	public CubeView getCubeView() {
		return view;
	}

	/**
	 * check if the specified viewer contains a dimension member of fullname
	 * member dimension's
	 * 
	 * @param v
	 * @param fullname
	 * @return
	 */
	private boolean isAllowed(Viewer v, String fullname) {
		boolean result = true;

		List<String> l = (List<String>) v.getInput();
		String begin = fullname.split("]")[0]; //$NON-NLS-1$

		for (String s : l) {
			if (s.contains(begin)) {
				result = false;
				break;
			}
		}

		return result;
	}
}
