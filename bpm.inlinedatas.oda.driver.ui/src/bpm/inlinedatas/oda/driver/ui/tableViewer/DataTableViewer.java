package bpm.inlinedatas.oda.driver.ui.tableViewer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.datechooser.DateChooserComboCellEditor;
import org.eclipse.nebula.widgets.formattedtext.DateFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import bpm.inlinedatas.oda.driver.ui.Activator;
import bpm.inlinedatas.oda.driver.ui.icons.IconNames;
import bpm.inlinedatas.oda.driver.ui.model.CellDescription;
import bpm.inlinedatas.oda.driver.ui.model.ColumnsDescription;

public class DataTableViewer extends TableViewer {

	private ArrayList<ColumnsDescription> listeColViewer;

	private List<ArrayList<CellDescription>> listInput;
	private DateChooserComboCellEditor dateEditor;
	private Composite parent;

	public DataTableViewer(Composite pParent, ArrayList<ColumnsDescription> listCol) {

		super(pParent, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		parent = pParent;

		listeColViewer = new ArrayList<ColumnsDescription>();
		listeColViewer.addAll(listCol);

		listInput = new ArrayList<ArrayList<CellDescription>>();

		// Build columns
		for(ColumnsDescription currentCol : listeColViewer) {
			structureAddCol(currentCol, pParent.getShell());
		}

		this.setContentProvider(new MyContentProvider());

		this.setInput(listInput);

	}

	// **************** Method to add a new columninto the viewer

	public void structureAddCol(final ColumnsDescription pCol, final Shell pShell) {

		TableViewerColumn viewerCol = new TableViewerColumn(this, SWT.NONE);

		// LabelProvider
		viewerCol.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {

				ArrayList<CellDescription> line = (ArrayList<CellDescription>) element;
				Object obj = line.get(pCol.getIndexCol()).getValueCell();

				if(obj instanceof Date) {

					Date d = (Date) obj;
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

					return format.format(d);
				}

				return obj != null ? obj.toString() : "";

			}

			@Override
			public Color getBackground(Object element) {
				ArrayList<CellDescription> line = (ArrayList<CellDescription>) element;

				// If value is modified > white or red
				if(line.get(pCol.getIndexCol()).isValueModified()) {

					// If value is valid > white
					if(line.get(pCol.getIndexCol()).isValueValid()) {
						return null;
					}

					// if not valid > red
					else {
						return new Color(Display.getDefault(), 255, 89, 122);
					}

				}

				else
					return null;

			}

		});

		// Columns caracteristics
		viewerCol.getColumn().setWidth(100);
		viewerCol.getColumn().setText(pCol.getColName());
		viewerCol.getColumn().setToolTipText(pCol.getColType().getSimpleName());
		viewerCol.getColumn().setMoveable(false);
		viewerCol.getColumn().setImage(Activator.getDefault().getImageRegistry().get(IconNames.COLUMN));

		// Editing support : Date chooser if Date type, text editor in other cases

		if(pCol.getColType() == Date.class) {

			viewerCol.setEditingSupport(new DateComboEditor(this, pCol));
		}

		else {
			viewerCol.setEditingSupport(new EditingSupport(this) {
				TextCellEditor txtEditor = new TextCellEditor((Composite) getControl());

				@Override
				protected boolean canEdit(Object element) {
					return true;
				}

				@Override
				protected CellEditor getCellEditor(Object element) {
					return txtEditor;
				}

				@Override
				protected Object getValue(Object element) {

					ArrayList<CellDescription> line = (ArrayList<CellDescription>) element;
					Object obj = line.get(pCol.getIndexCol()).getValueCell();

					return obj != null ? obj.toString() : "";
				}

				@Override
				protected void setValue(Object element, Object value) {

					ArrayList<CellDescription> line = (ArrayList<CellDescription>) element;

					try {
						line.get(pCol.getIndexCol()).setValueCell((String) value);

						line.get(pCol.getIndexCol()).setValueModified(true);
						line.get(pCol.getIndexCol()).setValueValid(true);

					} catch(Exception e) {

						line.get(pCol.getIndexCol()).setValueModified(true);
						line.get(pCol.getIndexCol()).setValueValid(false);
					}

					String sValue = (String) value;
					if(sValue.equals("")) {
						line.get(pCol.getIndexCol()).setValueModified(false);
					}

					refresh();
				}

			});
		}

	}

	// **************** Content Provider

	protected static class MyContentProvider implements IStructuredContentProvider {

		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {

			Collection localInputElement = (Collection) inputElement;

			return localInputElement.toArray(new Object[localInputElement.size()]);
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}
	}

	class DateComboEditor extends EditingSupport {

		private Viewer myViewer;
		private ColumnsDescription col;

		public DateComboEditor(ColumnViewer viewer, ColumnsDescription col) {

			super(viewer);
			myViewer = viewer;
			this.col = col;

			// Date chooser Editor
			dateEditor = new DateChooserComboCellEditor((Composite) getControl());
			dateEditor.getCombo().setFooterVisible(true);

			DateFormatter formatter = new DateFormatter("dd/MM/yyyy");
			dateEditor.getCombo().setFormatter(formatter);

		}

		protected boolean canEdit(Object element) {
			return true;
		}

		protected CellEditor getCellEditor(Object element) {

			return dateEditor;
		}

		protected Object getValue(Object element) {

			ArrayList<CellDescription> line = (ArrayList<CellDescription>) element;
			Object obj = line.get(col.getIndexCol()).getValueCell();

			dateEditor.getCombo().setValue((Date) obj);

			return obj != null ? obj.toString() : "";
		}

		protected void setValue(Object element, Object value) {
			ArrayList<CellDescription> line = (ArrayList<CellDescription>) element;

			String strDate = dateEditor.getCombo().getText();

			line.get(col.getIndexCol()).setValueCell(strDate);

			line.get(col.getIndexCol()).setValueModified(true);
			line.get(col.getIndexCol()).setValueValid(true);

			refresh();
		}
	}

	public ArrayList<ColumnsDescription> getListeColViewer() {
		return listeColViewer;
	}

	public void setListeColViewer(ArrayList<ColumnsDescription> listeColViewer) {
		this.listeColViewer = listeColViewer;
	}

	public List<ArrayList<CellDescription>> getListInput() {
		return listInput;
	}

	public void setListInput(List<ArrayList<CellDescription>> listInput) {
		this.listInput = listInput;
	}

}
