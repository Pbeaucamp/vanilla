package org.fasd.cubewizard.dimension.preload;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.PreloadConfig;
import org.freeolap.FreemetricsPlugin;

public class CompositePreloadConfig extends Composite {
	private TableViewer tableViewer;
	private PreloadConfig config;

	public CompositePreloadConfig(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		Label lblDefineTheLevel = new Label(this, SWT.NONE);
		lblDefineTheLevel.setBounds(0, 0, 55, 15);
		lblDefineTheLevel.setText(LanguageText.CompositePreloadConfig_0);

		tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new ArrayContentProvider());
		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setBounds(0, 0, 85, 85);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((OLAPHierarchy) element).getUniqueName();
			}
		});

		TableColumn tblclmnHierarchy = tableViewerColumn.getColumn();
		tblclmnHierarchy.setWidth(200);
		tblclmnHierarchy.setText(LanguageText.CompositePreloadConfig_1);

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (config == null) {
					return ""; //$NON-NLS-1$
				}
				Integer lvlNumber = config.getLevelNumber(((OLAPHierarchy) element).getUniqueName());
				if (lvlNumber == null || lvlNumber == -1) {
					return LanguageText.CompositePreloadConfig_3;
				}
				return ((OLAPHierarchy) element).getLevels().get(lvlNumber).getName();
			}
		});

		TableColumn tblclmnLevelnumber = tableViewerColumn_1.getColumn();
		tblclmnLevelnumber.setWidth(200);
		tblclmnLevelnumber.setText(LanguageText.CompositePreloadConfig_4);

		setEditingSupport(tableViewerColumn_1);
	}

	private void setEditingSupport(TableViewerColumn col) {
		EditingSupport sup = new EditingSupport(tableViewer) {
			ComboBoxCellEditor editor = new ComboBoxCellEditor(tableViewer.getTable(), new String[] {}, SWT.PUSH);

			@Override
			protected void setValue(Object element, Object value) {
				int val = (Integer) value;
				config.setHierarchyLevel(((OLAPHierarchy) element).getUniqueName(), val - 1);
				tableViewer.refresh();
			}

			@Override
			protected Object getValue(Object element) {
				Integer i = config.getLevelNumber(((OLAPHierarchy) element).getUniqueName());
				if (i == null || i == -1) {
					return 0;
				}
				return i + 1;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				String[] items = new String[((OLAPHierarchy) element).getLevels().size() + 1];
				items[0] = LanguageText.CompositePreloadConfig_2;
				for (int i = 1; i < items.length; i++) {
					items[i] = ((OLAPHierarchy) element).getLevels().get(i - 1).getName();
				}
				editor.setItems(items);
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		};

		col.setEditingSupport(sup);
	}

	public void init(List<OLAPHierarchy> l) {

		tableViewer.setInput(l);

		if (FreemetricsPlugin.getDefault().getCurrentModel().getPreloadConfig() != null) {
			config = new PreloadConfig();
			for (OLAPHierarchy hiera : l) {
				config.setHierarchyLevel(hiera.getUniqueName(), FreemetricsPlugin.getDefault().getCurrentModel().getPreloadConfig().getLevelNumber(hiera.getUniqueName()));
			}
		} else {
			config = new PreloadConfig();
		}

		tableViewer.refresh();
	}

	public PreloadConfig getConfig() {
		return config;
	}
}
