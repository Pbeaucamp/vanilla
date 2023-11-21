package bpm.sqldesigner.ui.utils;

import java.util.List;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

import bpm.sqldesigner.ui.i18N.Messages;

public class CompositeTableBrowser extends Composite {

	private TableViewer table;
	private List<List<Object>> model;
	private List<String> colNames;
	private Object data;
	public static Color mainBrown = new Color(Display.getDefault(), 209, 177,
			129);
	public static Color secondBrown = new Color(Display.getDefault(), 238, 226,
			208);

	public CompositeTableBrowser(Composite parent, int style,
			List<String> colNames, List<List<Object>> model) {
		super(parent, style);
		setLayout(new GridLayout());
		this.colNames = colNames;
		this.model = model;
		buildContent();
	}

	private void buildContent() {
		Composite comp = new Composite(this, SWT.NONE);
		comp.setLayout(new FillLayout(SWT.VERTICAL));
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite val = new Composite(comp, SWT.NONE);
		val.setLayout(new GridLayout());

		Label l = new Label(val, SWT.NONE);
		;
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true,
				false));
		l.setText(Messages.CompositeTableBrowser_0);

		table = new TableViewer(val, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.VIRTUAL | SWT.FULL_SELECTION);
		table.getTable().setLayoutData(
				new GridData(GridData.FILL, GridData.FILL, true, true));
		table.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				List l = (List) inputElement;

				return l.toArray(new Object[l.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

		});
		table.setLabelProvider(new Decorator(new LabelProvider(), PlatformUI
				.getWorkbench().getDecoratorManager().getLabelDecorator()));

		for (String s : colNames) {
			TableColumn c = new TableColumn(table.getTable(), SWT.NONE);
			c.setText(s);
			c.setWidth(100);
		}

	}

	public void setData() {
		if (!table.getSelection().isEmpty()) {
			data = ((IStructuredSelection) table.getSelection())
					.getFirstElement();
		}

	}

	public void fill() {
		if (model.size() != 0) {

			table.setInput(model);
			table.getTable().setHeaderVisible(true);
			table.getTable().setLinesVisible(false);
		}
	}

	@Override
	public Object getData() {
		return data;
	}

	private class Decorator extends DecoratingLabelProvider implements
			ITableLabelProvider {

		public Decorator(ILabelProvider provider, ILabelDecorator decorator) {
			super(provider, decorator);
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element == null)
				return ""; //$NON-NLS-1$
			try {
				Object o = ((List<Object>) element).get(columnIndex);
				return o == null ? "NULL" : o.toString(); //$NON-NLS-1$
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				return Messages.CompositeTableBrowser_3;
			}

		}

		@Override
		public Color getBackground(Object element) {
			if (model.indexOf(element) % 2 == 0) {
				return mainBrown;
			} else {
				return secondBrown;
			}

		}

	}
}
