package org.fasd.cubewizard.dimension;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPLevel;
import org.fasd.utils.trees.TreeContentProvider;
import org.fasd.utils.trees.TreeLabelProvider;
import org.fasd.utils.trees.TreeLevel;
import org.fasd.utils.trees.TreeParent;

public class DateDimensionLevelsPage extends WizardPage {

	private TreeViewer levelsViewer;

	private TableViewer selectedLevelsViewer;
	private Button btnAdd, btnDel, btnUp, btnDown;

	private List<OLAPLevel> levels = new ArrayList<OLAPLevel>();

	private OLAPDimension dimension;

	protected DateDimensionLevelsPage(String pageName) {
		super(pageName);
	}

	public DateDimensionLevelsPage(String pageName, OLAPDimension dimension) {
		super(pageName);
		this.dimension = dimension;
	}

	public void createControl(Composite parent) {

		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(4, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblLevels = new Label(mainComposite, SWT.NONE);
		lblLevels.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		lblLevels.setText(LanguageText.DateDimensionLevelsPage_0);

		Label lblSelLevels = new Label(mainComposite, SWT.NONE);
		lblSelLevels.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		lblSelLevels.setText(LanguageText.DateDimensionLevelsPage_1);

		levelsViewer = new TreeViewer(mainComposite);
		levelsViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

		btnAdd = new Button(mainComposite, SWT.PUSH);
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
		btnAdd.setText(">"); //$NON-NLS-1$

		Composite tableComp = new Composite(mainComposite, SWT.NONE);
		tableComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

		TableColumnLayout layout = new TableColumnLayout();
		tableComp.setLayout(layout);

		selectedLevelsViewer = new TableViewer(tableComp);
		selectedLevelsViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		selectedLevelsViewer.getTable().setHeaderVisible(true);
		selectedLevelsViewer.getTable().setLinesVisible(true);

		createLevelTableColumns(layout);

		btnUp = new Button(mainComposite, SWT.PUSH);
		btnUp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnUp.setText("+"); //$NON-NLS-1$

		btnDel = new Button(mainComposite, SWT.PUSH);
		btnDel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
		btnDel.setText("<"); //$NON-NLS-1$

		btnDown = new Button(mainComposite, SWT.PUSH);
		btnDown.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnDown.setText("-"); //$NON-NLS-1$

		createButtonListeners();
		createViewerProviders();

		setControl(mainComposite);
		setPageComplete(true);

		preFillData(dimension);
	}

	private void createButtonListeners() {

		btnAdd.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Object[] sel = ((IStructuredSelection) levelsViewer.getSelection()).toArray();
				Object[] inp = (Object[]) selectedLevelsViewer.getInput();
				if (inp == null) {
					inp = new Object[0];
				}
				Object[] res = new Object[sel.length + inp.length];
				for (int i = 0; i < inp.length; i++) {
					res[i] = inp[i];
				}
				for (int i = 0; i < sel.length; i++) {
					res[i + (inp.length)] = sel[i];
				}

				levels.clear();
				for (int i = 0; i < res.length; i++) {
					Object o = res[i];
					if (o instanceof TreeLevel) {
						levels.add(((TreeLevel) o).getOLAPLevel());
					}
				}

				selectedLevelsViewer.setInput(res);
				selectedLevelsViewer.refresh();
				DateDimensionLevelsPage.this.getContainer().updateButtons();
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		btnDel.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Object[] sel = ((IStructuredSelection) selectedLevelsViewer.getSelection()).toArray();
				Object[] inp = (Object[]) selectedLevelsViewer.getInput();
				List<Integer> toRm = new ArrayList<Integer>();
				for (int i = 0; i < sel.length; i++) {
					for (int j = 0; j < inp.length; j++) {
						if (sel[i] == inp[j]) {
							toRm.add(j);
							break;
						}
					}
				}
				Object[] res = new Object[inp.length - toRm.size()];
				int dec = 0;
				for (int i = 0; i < inp.length; i++) {
					if (!toRm.contains(i)) {
						res[i - dec] = inp[i];
					} else {
						dec++;
					}
				}

				levels.clear();
				for (int i = 0; i < res.length; i++) {
					Object o = res[i];
					if (o instanceof TreeLevel) {
						levels.add(((TreeLevel) o).getOLAPLevel());
					}
				}

				selectedLevelsViewer.setInput(res);
				selectedLevelsViewer.refresh();
				DateDimensionLevelsPage.this.getContainer().updateButtons();
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		btnUp.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Object sel = ((IStructuredSelection) selectedLevelsViewer.getSelection()).getFirstElement();
				Object[] inp = (Object[]) selectedLevelsViewer.getInput();
				int index = -1;
				for (int i = 0; i < inp.length; i++) {
					if (inp[i] == sel) {
						index = i;
						break;
					}
				}

				Object prev = inp[index - 1];

				inp[index] = prev;
				inp[index - 1] = sel;

				levels.clear();
				for (int i = 0; i < inp.length; i++) {
					Object o = inp[i];
					if (o instanceof TreeLevel) {
						levels.add(((TreeLevel) o).getOLAPLevel());
					}
				}

				selectedLevelsViewer.setInput(inp);
				selectedLevelsViewer.refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		btnDown.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Object sel = ((IStructuredSelection) selectedLevelsViewer.getSelection()).getFirstElement();
				Object[] inp = (Object[]) selectedLevelsViewer.getInput();
				int index = -1;
				for (int i = 0; i < inp.length; i++) {
					if (inp[i] == sel) {
						index = i;
						break;
					}
				}

				Object prev = inp[index + 1];

				inp[index] = prev;
				inp[index + 1] = sel;

				levels.clear();
				for (int i = 0; i < inp.length; i++) {
					Object o = inp[i];
					if (o instanceof TreeLevel) {
						levels.add(((TreeLevel) o).getOLAPLevel());
					}
				}

				selectedLevelsViewer.setInput(inp);
				selectedLevelsViewer.refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

	}

	private void createViewerProviders() {

		TreeParent input = DateTools.createInputLevels();

		levelsViewer.setLabelProvider(new TreeLabelProvider());
		levelsViewer.setContentProvider(new TreeContentProvider());
		levelsViewer.setAutoExpandLevel(2);

		selectedLevelsViewer.setContentProvider(new TableViewerContentProvider());

		levelsViewer.setInput(input);
		levelsViewer.refresh();
	}

	public boolean canFinish() {
		return levels != null && levels.size() > 0;
	}

	public List<OLAPLevel> getDimensionLevels() {

		for (OLAPLevel level : levels) {
			if (level.getDateColumnOrderPart() == null) {
				level.setDateColumnOrderPart("NONE"); //$NON-NLS-1$
			}
			if (level.getDateColumnOrderPart().equals("NONE")) { //$NON-NLS-1$
				level.setDateColumnOrderPart(""); //$NON-NLS-1$
			}
		}

		return levels;
	}

	private class TableViewerContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			return (Object[]) inputElement;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	public void preFillData(OLAPDimension dimension) {

		if (dimension != null) {

			List<TreeLevel> trLvls = new ArrayList<TreeLevel>();

			for (OLAPLevel lvl : dimension.getHierarchies().get(0).getLevels()) {
				for (Object o : ((TreeParent) levelsViewer.getInput()).getChildren()) {
					if (o instanceof TreeParent) {
						for (Object ob : ((TreeParent) o).getChildren()) {
							if (ob instanceof TreeLevel) {
								TreeLevel lev = (TreeLevel) ob;
								if (lvl.getName().equals(lev.getName())) {
									levels.add(lvl);
									lev.getOLAPLevel().setDateColumnOrderPart(lvl.getDateColumnOrderPart());
									trLvls.add(lev);
								}
							}
						}
					}
				}
			}

			TreeLevel[] input = new TreeLevel[trLvls.size()];

			for (int i = 0; i < trLvls.size(); i++) {
				input[i] = trLvls.get(i);
			}

			selectedLevelsViewer.setInput(input);
			selectedLevelsViewer.refresh();
		}
	}

	private void createLevelTableColumns(TableColumnLayout layout) {

		TableViewerColumn lvlNameColumn = new TableViewerColumn(selectedLevelsViewer, SWT.NONE);
		lvlNameColumn.getColumn().setText(LanguageText.DateDimensionLevelsPage_9);
		layout.setColumnData(lvlNameColumn.getColumn(), new ColumnWeightData(2, ColumnWeightData.MINIMUM_WIDTH, true));
		lvlNameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TreeLevel lvl = (TreeLevel) element;
				return lvl.getOLAPLevel().getName();
			}
		});

		final TableViewerColumn lvlOrderColumn = new TableViewerColumn(selectedLevelsViewer, SWT.READ_ONLY);
		lvlOrderColumn.getColumn().setText(LanguageText.DateDimensionLevelsPage_10);
		layout.setColumnData(lvlOrderColumn.getColumn(), new ColumnWeightData(2, ColumnWeightData.MINIMUM_WIDTH, true));
		final ColumnLabelProvider lblProv = new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TreeLevel lvl = (TreeLevel) element;
				if (lvl.getOLAPLevel().getDateColumnOrderPart() == null) {
					return "NONE"; //$NON-NLS-1$
				}
				return lvl.getOLAPLevel().getDateColumnOrderPart();
			}
		};
		lvlOrderColumn.setLabelProvider(lblProv);

		lvlOrderColumn.setEditingSupport(new EditingSupport(selectedLevelsViewer) {
			ComboBoxCellEditor colOrder = new ComboBoxCellEditor(selectedLevelsViewer.getTable(), DateTools.getOrders());

			@Override
			protected void setValue(Object element, Object value) {
				Integer typeindex = (Integer) value;
				if (typeindex == -1) {
					((TreeLevel) element).getOLAPLevel().setDateColumnOrderPart("NONE"); //$NON-NLS-1$
				} else {
					((TreeLevel) element).getOLAPLevel().setDateColumnOrderPart(DateTools.getOrders()[typeindex]);
				}
				selectedLevelsViewer.refresh();
			}

			@Override
			protected Object getValue(Object element) {
				for (int i = 0; i < DateTools.getOrders().length; i++) {
					if (DateTools.getOrders()[i].equals(((TreeLevel) element).getOLAPLevel().getDateColumnOrderPart())) {
						return i;
					}
				}
				return 0;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return colOrder;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
	}
}
