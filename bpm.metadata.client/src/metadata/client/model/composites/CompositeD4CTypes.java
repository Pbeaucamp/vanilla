package metadata.client.model.composites;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;

public class CompositeD4CTypes extends Composite {

	private IDataStream dataStream;
	private TableViewer viewer;

	public CompositeD4CTypes(Composite parent, int style, IDataStream dataStream) {
		super(parent, style);
		this.dataStream = dataStream;

		buildContent();
		fillData();
	}

	private void buildContent() {
		this.setLayout(new GridLayout());
		this.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		viewer = new TableViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer.setContentProvider(new ArrayContentProvider());

		createColumns();

	}

	private void createColumns() {
		TableViewerColumn colCol = new TableViewerColumn(viewer, SWT.NONE);
		colCol.getColumn().setText("Colonne");
		colCol.getColumn().setWidth(100);
		colCol.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((IDataStreamElement) element).getOutputName();
			};
		});

		addCheckbox("Facette", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isFacette()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setFacette(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isFacette();
			}
		});

		addCheckbox("Facette multi", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isFacetteMultiple()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setFacetteMultiple(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isFacetteMultiple();
			}
		});

		addCheckbox("Tableau", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isTableau()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setTableau(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isTableau();
			}
		});

		addCheckbox("Infobulle", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isInfobulle()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setInfobulle(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isInfobulle();
			}
		});

		addCheckbox("Tri", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isTri()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setTri(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isTri();
			}
		});

		addCheckbox("Date ponctuelle", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isDatePonctuelle()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setDatePonctuelle(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isDatePonctuelle();
			}
		});

		addCheckbox("Date début", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isDateDebut()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setDateDebut(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isDateDebut();
			}
		});

		addCheckbox("Date fin", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isDateFin()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setDateFin(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isDateFin();
			}
		});

		addCheckbox("Images", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isImages()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setImages(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isImages();
			}
		});

		addCheckbox("Nuage de mots", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isNuageDeMot()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setNuageDeMot(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isNuageDeMot();
			}
		});

		addCheckbox("Nuage de mots (nombre)", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isNuageDeMotNombre()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setNuageDeMotNombre(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isNuageDeMotNombre();
			}
		});

		addCheckbox("Date heure", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isDateHeure()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setDateHeure(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isDateHeure();
			}
		});

		addCheckbox("Frise libellé", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isFriseLibelle()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setFriseLibelle(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isFriseLibelle();
			}
		});

		addCheckbox("Frise description", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isFriseDescription()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setFriseDescription(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isFriseDescription();
			}
		});

		addCheckbox("Frise date", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isFriseDate()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setFriseDate(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isFriseDate();
			}
		});

		addCheckbox("Affichage Popup", new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((IDataStreamElement) element).getD4cTypes().isDisplayPopup()) {
					return Character.toString((char) 0x2611);
				}
				else {
					return Character.toString((char) 0x2610);
				}
			};
		}, new CustomEditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((IDataStreamElement) element).getD4cTypes().setDisplayPopup(Boolean.valueOf((boolean) value));
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IDataStreamElement) element).getD4cTypes().isDisplayPopup();
			}
		});
	}

	private void addCheckbox(String label, ColumnLabelProvider labelProvider, CustomEditingSupport editingSupport) {
		TableViewerColumn colFriseDate = new TableViewerColumn(viewer, SWT.NONE);
		colFriseDate.getColumn().setText(label);
		colFriseDate.getColumn().setWidth(100);
		colFriseDate.setLabelProvider(labelProvider);
		colFriseDate.setEditingSupport(editingSupport);
	}

	private void fillData() {
		viewer.setInput(dataStream.getElements());
	}

	private abstract class CustomEditingSupport extends EditingSupport {

		private CheckboxCellEditor cellEditor = new CheckboxCellEditor();

		public CustomEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return cellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

	}
}
