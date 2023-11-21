package bpm.sqldesigner.query.editor.filter;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.sqldesigner.query.Activator;
import bpm.sqldesigner.query.SQLDesignerComposite;
import bpm.sqldesigner.query.editor.SQLEditor;
import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Table;
import bpm.sqldesigner.query.model.filter.ColumnFilter;
import bpm.sqldesigner.query.model.filter.ColumnFilterWithOperation;
import bpm.sqldesigner.query.model.filter.ColumnFiltersManager;

public class FiltersComposite extends Composite {

	private Combo filterNamesCombo;
	private Label labelFilter;
	private Combo opCombo;
	private StyledText textFilter;
	private ToolItem applyFilterItem;
	private ApplyFilterListener applyFilterListener;
	private StyledText filterName;
	private NamesComboListener listenerCombo;
	private SQLEditor viewer;
	private ISelectionChangedListener editorListener;
	private ToolItem trashToolItem;
	private Listener trashListener;
	private SQLDesignerComposite designerComposite;
	private Combo predefinedFiltersCombo;
	private Button newFilterButton;
	private Button existingFiltersButton;
	private Button predefinedFiltersButton;
	private int filtersDeletedCount = 0;

	public FiltersComposite(Composite parent, int style,
			final SQLDesignerComposite designerComposite) {
		super(parent, style);

		viewer = designerComposite.getEditor();
		this.designerComposite = designerComposite;

		/***********************************************************************
		 * first line
		 **********************************************************************/
		Composite filterCNames1 = new Composite(this, SWT.NONE);
		filterCNames1.setLayout(new GridLayout(5, false));
		filterCNames1.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.CENTER, true, false, 5, 1));

		newFilterButton = new Button(filterCNames1, SWT.RADIO);
		newFilterButton.setText("New Filter");

		existingFiltersButton = new Button(filterCNames1, SWT.RADIO);
		existingFiltersButton.setText("Existing Filters");
		existingFiltersButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				filterNamesCombo.setEnabled(true);
				filterNamesCombo.deselectAll();
				predefinedFiltersCombo.setEnabled(false);
				predefinedFiltersCombo.deselectAll();
				filterName.setText("");
				textFilter.setText("");
				opCombo.select(0);
			}

		});

		filterNamesCombo = new Combo(filterCNames1, SWT.READ_ONLY | SWT.BORDER);
		filterNamesCombo.setEnabled(false);

		predefinedFiltersButton = new Button(filterCNames1, SWT.RADIO);
		predefinedFiltersButton.setText("Predefined Filters");
		predefinedFiltersButton
				.addSelectionListener(new PredefinedFilterListener());

		predefinedFiltersCombo = new Combo(filterCNames1, SWT.READ_ONLY
				| SWT.BORDER);
		predefinedFiltersCombo.setEnabled(false);
		predefinedFiltersCombo
				.addSelectionListener(new PredefinedFilterComboListener());

		/***********************************************************************
		 * separator line
		 **********************************************************************/
		Label separator = new Label(this, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				false, false, 5, 1));
		/***********************************************************************
		 * second line
		 **********************************************************************/
		Composite filterCNames = new Composite(this, SWT.NONE);
		filterCNames.setLayout(new GridLayout(2, false));
		filterCNames.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
				true, false, 2, 1));

		Label label1 = new Label(filterCNames, SWT.NONE);
		label1.setLayoutData(new GridData(GridData.FILL));
		label1.setText("Filter's name :");

		filterName = new StyledText(filterCNames, SWT.BORDER);
		filterName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
				true, false, 1, 1));

		Composite voidC = new Composite(this, SWT.NONE);
		voidC.setLayout(new GridLayout(1, false));
		voidC.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
				false, 1, 1));
		Composite voidC2 = new Composite(this, SWT.NONE);
		voidC2.setLayout(new GridLayout(1, false));
		voidC2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
				false, 1, 1));
		Composite voidC3 = new Composite(this, SWT.NONE);
		voidC3.setLayout(new GridLayout(1, false));
		voidC3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
				false, 1, 1));
		/***********************************************************************
		 * separator line
		 **********************************************************************/
		Label separator2 = new Label(this, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator2.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				false, false, 5, 1));
		/***********************************************************************
		 * third line
		 **********************************************************************/
		Composite filterValueC = new Composite(this, SWT.NONE);
		filterValueC.setLayout(new GridLayout(4, false));
		filterValueC.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
				true, false, 5, 1));

		labelFilter = new Label(filterValueC, SWT.NONE);
		labelFilter.setText("                                       ");
		labelFilter.setLayoutData(new GridData(GridData.CENTER,
				GridData.CENTER, false, false, 1, 1));

		opCombo = new Combo(filterValueC, SWT.READ_ONLY);
		opCombo.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER,
				false, false, 1, 1));

		for (String op : ColumnFilter.OPERATORS) {
			opCombo.add(op);
		}
		opCombo.select(0);
		opCombo.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				String opValue = opCombo.getText();
				if (opValue.equals(" IS NULL")
						|| opValue.equals(" IS NOT NULL")) {
					textFilter.setEditable(false);
					textFilter.setEnabled(false);
					textFilter.setBackground(getDisplay().getSystemColor(
							SWT.COLOR_GRAY));
				} else if (!textFilter.getEditable()) {
					textFilter.setEditable(true);
					textFilter.setEnabled(true);
					textFilter.setBackground(getDisplay().getSystemColor(
							SWT.COLOR_WHITE));
				}
			}

		});

		textFilter = new StyledText(filterValueC, SWT.BORDER);
		textFilter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		ToolBar filterToolBar = new ToolBar(filterValueC, SWT.FLAT);
		Listener cancelListener = new Listener() {
			public void handleEvent(Event event) {
				close();
			}
		};

		trashToolItem = new ToolItem(filterToolBar, SWT.NONE);
		trashToolItem.setImage(Activator.getDefault().getImageRegistry().get(
				"trash"));
		trashToolItem.setToolTipText("Delete");

		ToolItem toolItem1 = new ToolItem(filterToolBar, SWT.NONE);
		toolItem1.setImage(Activator.getDefault().getImageRegistry().get(
				"cancel"));
		toolItem1.setToolTipText("Cancel");
		toolItem1.addListener(SWT.Selection, cancelListener);

		applyFilterItem = new ToolItem(filterToolBar, SWT.NONE);
		applyFilterItem.setImage(Activator.getDefault().getImageRegistry().get(
				"valid"));
		applyFilterItem.setToolTipText("Apply");

		/***********************************************************************
		 * Editor Listener
		 **********************************************************************/

		editorListener = new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				List<?> selected = designerComposite.getEditor()
						.getSelectedEditParts();
				if (selected.size() == 1) {
					EditPart editpart = (EditPart) selected.iterator().next();
					if (editpart.getModel() instanceof Column) {
						Column c = (Column) editpart.getModel();
						textFilter.insert(" "
								+ ((Table) c.getParent()).getName() + "."
								+ c.getName());
						textFilter
								.setCaretOffset(textFilter.getText().length());
						textFilter.setFocus();
					}
				}

			}

		};

	}

	public void openFilterComposite(Column model) {
		if (isVisible())
			return;
		setVisible(true);

		List<ColumnFilterWithOperation> filters = ColumnFiltersManager
				.getInstance().getFilters(model);
		filterNamesCombo.removeAll();

		if (filters.size() != 0) {
			Iterator<ColumnFilterWithOperation> it = filters.iterator();
			while (it.hasNext())
				filterNamesCombo.add(it.next().getName());
		} else
			existingFiltersButton.setEnabled(false);

		List<ColumnFilter> predefinedFilters = ColumnFiltersManager
				.getInstance().getPredefinedFilters();
		if (predefinedFilters != null) {
			boolean found;
			int i;
			int j = 0;
			Iterator<ColumnFilter> it = predefinedFilters.iterator();
			while (it.hasNext()) {
				String name = it.next().getName();
				String[] items = filterNamesCombo.getItems();
				i = 0;
				found = false;
				while (i < items.length && !found) {
					found = items[i].equals(name);
					i++;
				}
				if (!found) {
					predefinedFiltersCombo.add(name);
				} else
					j++;

			}
			if (j == predefinedFilters.size()) {
				predefinedFiltersButton.setEnabled(false);
			}
		} else
			predefinedFiltersButton.setEnabled(false);

		listenerCombo = new NamesComboListener(filters, model);
		filterNamesCombo.addSelectionListener(listenerCombo);

		newFilterButton.addSelectionListener(new NewFilterListener(filters));
		newFilterButton.setSelection(true);

		designerComposite.setGroupFiltersText(model.getName() + " filters : ");
		textFilter.setText("");
		filterName.setEditable(true);
		filterName.setEnabled(true);
		filterName.setBackground(getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		filterName.setText("filter" + (filters.size() + 1));
		filterNamesCombo.select(0);
		opCombo.select(0);
		trashToolItem.setEnabled(false);

		textFilter.setFocus();

		labelFilter.setText(model.getName());

		applyFilterListener = new ApplyFilterListener(model);
		applyFilterItem.addListener(SWT.Selection, applyFilterListener);
		viewer.addSelectionChangedListener(editorListener);
	}

	public void close() {
		setVisible(false);
		applyFilterItem.removeListener(SWT.Selection, applyFilterListener);
		filterNamesCombo.removeSelectionListener(listenerCombo);
		viewer.removeSelectionChangedListener(editorListener);
		Composite tampon = getParent().getParent();
		dispose();
		tampon.layout();
	}

	protected class ApplyFilterListener implements Listener {

		private Column column;

		public ApplyFilterListener(Column c) {
			column = c;
		}

		public void handleEvent(Event event) {

			ColumnFilterWithOperation cFilter = new ColumnFilterWithOperation();
			String name = filterName.getText();

			if (name.equals(""))
				name = "filter"
						+ (ColumnFiltersManager.getInstance()
								.getFilters(column).size() + 1);

			cFilter.setName(name);

			String value = textFilter.getText();
			String opValue = opCombo.getText();
			if (!value.equals("") || opValue.equals(" IS NULL")
					|| opValue.equals(" IS NOT NULL")) {
				cFilter.setValue(value);
				cFilter.setOperation(opCombo.getText());
				ColumnFiltersManager.getInstance().putFilter(column, cFilter);
			}
			close();

		}
	}

	protected class NewFilterListener implements SelectionListener {
		private List<ColumnFilterWithOperation> filters;

		public NewFilterListener(List<ColumnFilterWithOperation> l) {
			filters = l;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			filterName.setText("filter" + (filters.size() + 1));
			textFilter.setText("");
			filterName.setEditable(true);
			filterName.setEnabled(true);
			filterName.setBackground(getDisplay().getSystemColor(
					SWT.COLOR_WHITE));
			trashToolItem.setEnabled(false);
			filterNamesCombo.setEnabled(false);
			opCombo.select(0);
			predefinedFiltersCombo.setEnabled(false);
		}
	}

	protected class PredefinedFilterListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			textFilter.setText("");
			filterName.setEditable(true);
			filterName.setEnabled(true);
			filterName.setBackground(getDisplay().getSystemColor(
					SWT.COLOR_WHITE));
			trashToolItem.setEnabled(false);
			filterNamesCombo.setEnabled(false);
			filterNamesCombo.deselectAll();
			predefinedFiltersCombo.setEnabled(true);
			predefinedFiltersCombo.deselectAll();
			filterName.setText("");
			opCombo.select(0);
		}
	}

	protected class NamesComboListener implements SelectionListener {
		private List<ColumnFilterWithOperation> filters;
		private Column column;

		public NamesComboListener(List<ColumnFilterWithOperation> l,
				Column column) {
			filters = l;
			this.column = column;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			final int index = filterNamesCombo.getSelectionIndex();

			filterName.setText(filterNamesCombo.getItem(index));
			textFilter.setText(filters.get(index).getValue());
			filterName.setEditable(false);
			filterName.setEnabled(false);
			filterName.setBackground(getDisplay()
					.getSystemColor(SWT.COLOR_GRAY));
			opCombo.setText(filters.get(index).getOperation());

			trashListener = new Listener() {

				public void handleEvent(Event event) {
					ColumnFiltersManager.getInstance().remove(column,
							filters.get(index));
					filtersDeletedCount++;
					filterNamesCombo.remove(index);
					newFilterButton.setSelection(true);
					trashToolItem.setEnabled(false);
					trashToolItem.removeListener(SWT.Selection, this);
					filterName.setText("filter" + (filters.size() + 1));
					textFilter.setText("");
					filterName.setEditable(true);
					filterName.setEnabled(true);
					filterName.setBackground(getDisplay().getSystemColor(
							SWT.COLOR_WHITE));
					opCombo.select(0);

					filterNamesCombo.setEnabled(false);
					existingFiltersButton.setSelection(false);

					if (filters.size() == 0)
						existingFiltersButton.setEnabled(false);
				}
			};
			trashToolItem.addListener(SWT.Selection, trashListener);
			trashToolItem.setEnabled(true);

		}
	};

	protected class PredefinedFilterComboListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			List<ColumnFilter> predefinedFilters = ColumnFiltersManager
					.getInstance().getPredefinedFilters();

			String cName = predefinedFiltersCombo
					.getItem(predefinedFiltersCombo.getSelectionIndex());

			boolean found = false;
			Iterator<ColumnFilter> it = predefinedFilters.iterator();
			ColumnFilter c = null;
			while (it.hasNext() && !found) {
				c = it.next();
				if (c.getName().equals(cName))
					found = true;
			}
			if (found) {
				if (c.needsApostrophe())
					textFilter.setText("'" + c.getValue() + "'");
				else
					textFilter.setText(c.getValue());
				filterName.setText(c.getName());
				filterName.setEditable(false);
				filterName.setEnabled(false);
				filterName.setBackground(getDisplay().getSystemColor(
						SWT.COLOR_GRAY));
			}
		}
	}
}
