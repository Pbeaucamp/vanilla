package bpm.workflow.ui.composites;

import java.io.UnsupportedEncodingException;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

import bpm.workflow.ui.Messages;

/**
 * Composite for the browse of the datas which are in a location
 * 
 * @author CAMUS, MARTIN
 * 
 */
public class CompositeTableBrowser extends Composite {

	private TableViewer table;
	private List<List<String>> model;
	private List<String> colNames;
	private Object data;

	/**
	 * Create a browser composite
	 * 
	 * @param parent
	 *            : the parent composite
	 * @param style
	 * @param colNames
	 *            : the name of the columns
	 * @param model
	 *            : the datas
	 */
	public CompositeTableBrowser(Composite parent, int style, List<String> colNames, List<List<String>> model) {
		super(parent, style);
		this.setLayout(new GridLayout());
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

		Label l = new Label(val, SWT.NONE);;
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		l.setText(Messages.CompositeTableBrowser_0);

		table = new TableViewer(val, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		table.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		table.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				List l = (List) inputElement;

				return l.toArray(new Object[l.size()]);
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

		});
		table.setLabelProvider(new Decorator(new LabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));

		if(colNames != null) {
			for(String s : colNames) {
				TableColumn c = new TableColumn(table.getTable(), SWT.NONE);
				c.setText(s);
				c.setWidth(100);
			}
		}
		else if(model.get(0) != null) {
			for(int i = 0; i < model.get(0).size(); i++) {
				TableColumn c = new TableColumn(table.getTable(), SWT.NONE);
				c.setWidth(100);
			}
		}

	}

	public void setData() {
		if(!table.getSelection().isEmpty()) {
			data = ((IStructuredSelection) table.getSelection()).getFirstElement();
		}

	}

	public void fill() {
		if(model.size() != 0) {

			table.setInput(model);
			table.getTable().setHeaderVisible(true);
			table.getTable().setLinesVisible(false);
		}
	}

	@Override
	public Object getData() {
		return data;
	}

	private class Decorator extends DecoratingLabelProvider implements ITableLabelProvider {

		public Decorator(ILabelProvider provider, ILabelDecorator decorator) {
			super(provider, decorator);
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if(element == null)
				return ""; //$NON-NLS-1$
			try {
				Object o = ((List<Object>) element).get(columnIndex);
				try {
					return o == null ? "NULL" : new String(o.toString().getBytes("UTF-8"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} catch(UnsupportedEncodingException e) {
					return o == null ? "NULL" : o.toString(); //$NON-NLS-1$
				}
			} catch(ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				return Messages.CompositeTableBrowser_5;
			}

		}

	}
}
