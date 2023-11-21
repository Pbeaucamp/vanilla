package bpm.gateway.ui.dialogs.utils.fields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.inputs.DataPreparationInput;
import bpm.gateway.core.transformations.inputs.FMDTInput;
import bpm.gateway.ui.i18n.Messages;
import bpm.metadata.layer.business.IBusinessPackage;

public class DialogFieldsValues extends Dialog {

	private ComboViewer fields;
	private ChartComposite chartComposite;
	private TableViewer distinctValues;
	private Transformation stream;
	private Collection<IBusinessPackage> fmdtPackages;
	private List<StreamElement> cols;

	public DialogFieldsValues(Shell parentShell, Transformation stream) {
		super(parentShell);
		this.stream = stream;
	}

	/**
	 * This constructor is made for a FDMTInput only. Don't use it for something
	 * else.
	 * 
	 * @param parentShell
	 * @param stream
	 * @param fmdtPackages
	 * @param cols
	 */
	public DialogFieldsValues(Shell parentShell, Transformation stream, Collection<IBusinessPackage> fmdtPackages, List<StreamElement> cols) {
		super(parentShell);
		this.stream = stream;
		this.fmdtPackages = fmdtPackages;
		this.cols = cols;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogFieldsValues_0);

		fields = new ComboViewer(main, SWT.READ_ONLY);
		fields.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fields.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		fields.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((StreamElement) element).name;
			}

		});
		fields.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (fields.getSelection().isEmpty()) {
					return;

				}

				StreamElement e = (StreamElement) ((IStructuredSelection) fields.getSelection()).getFirstElement();
				try {
					List<List<Object>> l = new ArrayList<List<Object>>();
					if (stream instanceof FMDTInput) {
						l = FieldValuesHelper.getRowsForFmdt((FMDTInput) stream, fmdtPackages, cols, e);
					}
					else if (stream instanceof DataPreparationInput) {
						l = FieldValuesHelper.getRowsForDataPreparation((DataPreparationInput) stream, e);
					}
					else {
						l = FieldValuesHelper.getRows(stream, e);
					}
					distinctValues.setInput(l);
					chartComposite.setChart(buildChart(l));
				} catch (Throwable e1) {
					e1.printStackTrace();
				}

			}

		});

		TabFolder folder = new TabFolder(main, SWT.NONE);
		folder.setLayout(new GridLayout());
		folder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		TabItem distinctValues = new TabItem(folder, SWT.NONE);
		distinctValues.setText(Messages.DialogFieldsValues_1);
		distinctValues.setControl(createDistinctValues(folder));

		TabItem chartTab = new TabItem(folder, SWT.NONE);
		chartTab.setText(Messages.DialogFieldsValues_2);
		chartTab.setControl(createChart(folder));

		return main;
	}

	private Composite createChart(Composite parent) {
		chartComposite = new ChartComposite(parent, SWT.NONE);
		chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		return chartComposite;
	}

	private Composite createDistinctValues(Composite parent) {
		distinctValues = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		distinctValues.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				List l = (List) inputElement;
				return l.toArray(new Object[l.size()]);
			}
		});
		distinctValues.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		distinctValues.getTable().setHeaderVisible(true);
		distinctValues.getTable().setLinesVisible(true);

		TableColumn col = new TableColumn(distinctValues.getTable(), SWT.CENTER);
		col.setText(Messages.DialogFieldsValues_3);
		col.setWidth(300);

		TableColumn col1 = new TableColumn(distinctValues.getTable(), SWT.CENTER);
		col1.setText(Messages.DialogFieldsValues_4);
		col1.setWidth(300);

		distinctValues.setLabelProvider(new ITableLabelProvider() {

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
				List<Object> row = (List) element;
				if (row.get(columnIndex) != null) {
					return row.get(columnIndex).toString();
				}
				return "null"; //$NON-NLS-1$
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});

		return distinctValues.getTable();
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		getShell().setText(Messages.DialogFieldsValues_6);
		try {
			fields.setInput(stream.getDescriptor(stream).getStreamElements());
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	private JFreeChart buildChart(List<List<Object>> l) {
		DefaultPieDataset dataset = new DefaultPieDataset();

		for (List<Object> row : l) {
			if (row.get(0) == null) {
				dataset.setValue("null", (Number) row.get(1)); //$NON-NLS-1$
			}
			else {
				dataset.setValue(row.get(0).toString(), (Number) row.get(1));
			}

		}
		JFreeChart chart = ChartFactory.createPieChart(Messages.DialogFieldsValues_8, dataset, false, false, false);
		return chart;
	}

}
