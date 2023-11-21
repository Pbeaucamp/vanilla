package bpm.sqldesigner.query.editor.filter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.sqldesigner.query.Activator;
import bpm.sqldesigner.query.SQLDesignerComposite;
import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.filter.ColumnFilterWithOperation;
import bpm.sqldesigner.query.model.filter.ColumnFiltersManager;
import bpm.sqldesigner.query.output.SqlOutputManager;

public class FiltersManagerComposite extends Composite {

	private StyledText filtersManagerText;
	private HashMap<MenuItem, String> filtersValues = new HashMap<MenuItem, String>();
	private ToolItem applyFilterItem;
	private Menu menu;
	static String filtersSave = "";

	public FiltersManagerComposite(Composite parent, int style,
			SQLDesignerComposite designerComposite) {
		super(parent, style);
		createWidgets(this);
	}

	private void createWidgets(Composite parent) {
		final ToolBar toolBar = new ToolBar(this, SWT.HORIZONTAL | SWT.FLAT);
		toolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolBar.setLayout(new GridLayout(4, false));

		ToolItem item1 = new ToolItem(toolBar, SWT.PUSH);
		item1.setText("AND");

		item1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (filtersManagerText.getText().equals("\n\n"))
					filtersManagerText.setText("");
				filtersManagerText.insert(" AND");
				filtersManagerText.setCaretOffset(filtersManagerText.getText().length());
			}
		});

		ToolItem item2 = new ToolItem(toolBar, SWT.PUSH);
		item2.setText("OR");

		item2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (filtersManagerText.getText().equals("\n\n"))
					filtersManagerText.setText("");
				filtersManagerText.insert(" OR");
				filtersManagerText.setCaretOffset(filtersManagerText.getText().length());
			}
		});

		ToolItem item3 = new ToolItem(toolBar, SWT.PUSH);
		item3.setText("(");

		item3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (filtersManagerText.getText().equals("\n\n"))
					filtersManagerText.setText("");
				filtersManagerText.insert(" (");
				filtersManagerText.setCaretOffset(filtersManagerText.getText().length());
			}
		});

		ToolItem item4 = new ToolItem(toolBar, SWT.PUSH);
		item4.setText(")");

		item4.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (filtersManagerText.getText().equals("\n\n"))
					filtersManagerText.setText("");
				filtersManagerText.insert(" )");
				filtersManagerText.setCaretOffset(filtersManagerText.getText().length());
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		menu = new Menu(this);
		HashMap<Column, List<ColumnFilterWithOperation>> filters = ColumnFiltersManager
				.getInstance().getAllFilters();
		Set<Column> keys = filters.keySet();

		Iterator<Column> it = keys.iterator();

		while (it.hasNext()) {
			Column c = it.next();
			List<ColumnFilterWithOperation> list = filters.get(c);
			Iterator<ColumnFilterWithOperation> itFilters = list.iterator();
			String columnName = c.getName();

			while (itFilters.hasNext()) {
				ColumnFilterWithOperation filter = itFilters.next();
				MenuItem item = new MenuItem(menu, SWT.CASCADE);
				item.setText(columnName + "." + filter.getName()
						+ "     |     " + filter.getOperation()
						+ filter.getValue());
				filtersValues.put(item, columnName + "." + filter.getName());
				item.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent e) {
					}

					public void widgetSelected(SelectionEvent e) {
						String value = filtersValues.get(e.getSource());
						if (filtersManagerText.getText().equals("\n\n"))
							filtersManagerText.setText("");
						filtersManagerText.insert(" " + value);
						filtersManagerText.setCaretOffset(filtersManagerText.getText().length());
					}

				});
			}
		}

		final ToolItem item = new ToolItem(toolBar, SWT.DROP_DOWN);
		item.setText("Filters");
		item.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				Rectangle rect = item.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = toolBar.toDisplay(pt);
				menu.setLocation(pt.x, pt.y);
				menu.setVisible(true);

			}
		});

		// 2e line
		filtersManagerText = new StyledText(this, SWT.BORDER | SWT.MULTI
				| SWT.WRAP | SWT.V_SCROLL);
		filtersManagerText
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		filtersManagerText.setText(filtersSave);
		if (filtersManagerText.getText().equals(""))
			filtersManagerText.setText("\n\n");

		// 3e line - cancel & apply buttons
		ToolBar filterToolBar = new ToolBar(this, SWT.FLAT);
		Listener cancelListener = new Listener() {
			public void handleEvent(Event event) {
				close();
			}
		};

		ToolItem toolItem1 = new ToolItem(filterToolBar, SWT.NONE);
		toolItem1.setImage(Activator.getDefault().getImageRegistry().get(
				"cancel"));
		toolItem1.setToolTipText("Cancel");
		toolItem1.addListener(SWT.Selection, cancelListener);

		applyFilterItem = new ToolItem(filterToolBar, SWT.NONE);
		applyFilterItem.setImage(Activator.getDefault().getImageRegistry().get(
				"valid"));
		applyFilterItem.setToolTipText("Apply");
		applyFilterItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				if (!filtersManagerText.getText().equals("\n\n")) {
					decodeFilter();
					filtersSave = filtersManagerText.getText();
				}
				close();
			}
		});

	}

	protected void decodeFilter() {
		String value = filtersManagerText.getText();
		StringBuffer valueBuffer = new StringBuffer(value);
		if (!value.trim().equals("")) {

			char[] charArray = value.toCharArray();

			for (int i = 0; i < charArray.length; i++) {
				if (charArray[i] == '.') {
					int j = 1;
					char c = 'a';
					String cName = "";
					while (c != ' ' && c != ')' && c != '(' && j <= i) {
						c = charArray[i - j];
						if (c != ' ' && c != ')' && c != '(')
							cName = c + cName;
						j++;
					}

					j = 1;
					c = 'a';
					String fName = "";
					while (c != ' ' && c != ')' && c != '('
							&& i + j < charArray.length) {
						c = charArray[i + j];
						if (c != ' ' && c != ')' && c != '(')
							fName = fName + c;
						j++;
					}

					Column column = ColumnFiltersManager.getInstance()
							.findColumn(cName);
					ColumnFilterWithOperation filter = ColumnFiltersManager
							.getInstance().findFilter(column, fName);
					String tableName = column.getParent().getName();

					int x = valueBuffer.indexOf(cName + "." + fName);
					valueBuffer.replace(x, x + (cName + "." + fName).length(),
							tableName + "." + cName + filter.getOperation()
									+ filter.getValue());
				}
			}
			SqlOutputManager.getInstanceIfExists().setFilter(
					valueBuffer.substring(0));
		} else
			SqlOutputManager.getInstanceIfExists().setFilter("");
	}

	protected void close() {
		setVisible(false);
		Composite tampon = getParent().getParent();
		dispose();
		tampon.layout();
	}

	class DropdownSelectionListener extends SelectionAdapter {
		private ToolItem dropdown;

		private Menu menu;

		public DropdownSelectionListener(ToolItem dropdown) {
			this.dropdown = dropdown;
			menu = new Menu(dropdown.getParent().getShell());
		}

		public void add(String item) {
			MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText(item);
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					MenuItem selected = (MenuItem) event.widget;
					dropdown.setText(selected.getText());
				}
			});
		}

		public void widgetSelected(SelectionEvent event) {
			if (event.detail == SWT.ARROW) {
				ToolItem item = (ToolItem) event.widget;
				Rectangle rect = item.getBounds();
				Point pt = item.getParent()
						.toDisplay(new Point(rect.x, rect.y));
				menu.setLocation(pt.x, pt.y + rect.height);
				menu.setVisible(true);
			}
		}
	}

}
