package bpm.gateway.ui.views.property.sections.transformations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.calcul.GeoCondition;
import bpm.gateway.core.transformations.calcul.GeoFilter;
import bpm.gateway.core.transformations.kml.KMLInput;
import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;

public class GeoFilterSection extends AbstractPropertySection {

	private Node node;
	private NodePart nodePart;

	private Text txtShape;
	private ComboViewer geoColumn;

	private TableViewer table;

	private List<String> targets;
	private List<String> kmls;
	// private ModifyListener shapeListener = new ModifyListener() {
	// public void modifyText(ModifyEvent evt) {
	// ((GeoFilter)node.getGatewayModel()).setGeoShape(txtShape.getText());
	// }
	// };

	private ISelectionChangedListener columnListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			String lat = (String) ((IStructuredSelection) event.getSelection()).getFirstElement();
			((GeoFilter) node.getGatewayModel()).setGeoElementName(lat);
			((GeoFilter) node.getGatewayModel()).refreshDescriptor();
		}
	};
	private TableViewerColumn colTarget;
	private TableViewerColumn colKml;
	private TableViewerColumn colPlacemark;

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		this.nodePart = (NodePart) input;

	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		Label lblColumn = getWidgetFactory().createLabel(composite, Messages.GeoFilterSection_0, SWT.NONE);
		lblColumn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		geoColumn = new ComboViewer(composite, SWT.DROP_DOWN | SWT.PUSH);
		geoColumn.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		geoColumn.setLabelProvider(new LabelProvider());

		geoColumn.setContentProvider(new ArrayContentProvider());
		geoColumn.addSelectionChangedListener(columnListener);

		ToolBar toolbar = new ToolBar(composite, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.FilterSection_0);
		add.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.add_16));
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((GeoFilter) node.getGatewayModel()).addCondition(new GeoCondition());
				table.setInput(((GeoFilter) node.getGatewayModel()).getConditions());
				table.refresh();
			}
		});
		// add.setEnabled(false);

		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.FilterSection_1);
		del.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.del_16));
		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) table.getSelection();
				for(Object o : ss.toList()) {
					if(o instanceof GeoCondition) {
						((GeoFilter) node.getGatewayModel()).getConditions().remove(o);
					}
				}
				table.setInput(((GeoFilter) node.getGatewayModel()).getConditions());
				table.refresh();
			}
		});
		// del.setEnabled(false);

		table = new TableViewer(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		table.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		table.setContentProvider(new ArrayContentProvider());
		table.getTable().setHeaderVisible(true);
		table.getTable().setLinesVisible(true);

		colTarget = new TableViewerColumn(table, SWT.NONE);
		colTarget.getColumn().setWidth(150);
		colTarget.getColumn().setText(Messages.GeoFilterSection_1);
		colTarget.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((GeoCondition) element).targetName;
			}
		});

		colKml = new TableViewerColumn(table, SWT.NONE);
		colKml.getColumn().setWidth(150);
		colKml.getColumn().setText(Messages.GeoFilterSection_2);
		colKml.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((GeoCondition) element).inputKml;
			}
		});

		colPlacemark = new TableViewerColumn(table, SWT.NONE);
		colPlacemark.getColumn().setWidth(150);
		colPlacemark.getColumn().setText(Messages.GeoFilterSection_3);
		colPlacemark.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((GeoCondition) element).placeMarkName;
			}
		});

		// Label lblShapeDesc = getWidgetFactory().createLabel(composite, "The shape can be a polygon or a circle.\nFor a polygon, the syntax is the coordinates part of a kml polygon like : \n" +
		// "  -1.2414507,47.9739917,0\n" +
		// "  -1.9940142,47.6974426,0\n" +
		// "  -1.4282183,47.3040713,0\n" +
		// "  -1.2414507,47.9739917,0\n" +
		// "with a line break or a space between every point.\nFor a circle : \n"
		// + "circle(longitude,latitude,radius)", SWT.NONE);
		// lblShapeDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		//
		// Label lblShape = getWidgetFactory().createLabel(composite, "Geo shape", SWT.NONE);
		// lblShape.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		//
		// txtShape = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		// txtShape.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		//		txtShape.setText(""); //$NON-NLS-1$
		// txtShape.addModifyListener(shapeListener);
	}

	@Override
	public void refresh() {
		try {
			List<String> inputFieldsNames = new ArrayList<>();
			try {
				for(Transformation t : ((GeoFilter) node.getGatewayModel()).getInputs()) {
					if(!(t instanceof KMLInput) && !(t instanceof MdmContractFileInput && ((MdmContractFileInput)t).getFileTransfo() instanceof KMLInput)) {
						for(StreamElement e : t.getDescriptor(((GeoFilter) node.getGatewayModel())).getStreamElements()) {
							inputFieldsNames.add(e.name);
						}
						break;
					}
				}
			} catch(ServerException e) {

				e.printStackTrace();
			}
			geoColumn.setInput(inputFieldsNames.toArray(new String[inputFieldsNames.size()]));

			if(((GeoFilter) node.getGatewayModel()).getGeoElementName() != null) {
				geoColumn.setSelection(new StructuredSelection(((GeoFilter) node.getGatewayModel()).getGeoElementName()));
			}
			// txtShape.setText(((GeoFilter)node.getGatewayModel()).getGeoShape());
			table.setInput(((GeoFilter) node.getGatewayModel()).getConditions());

			targets = new ArrayList<>();
			for(Transformation t : ((GeoFilter) node.getGatewayModel()).getOutputs()) {
				targets.add(t.getName());
			}

			colTarget.setEditingSupport(new EditingSupport(table) {
				ComboBoxCellEditor targetEditor = new ComboBoxCellEditor(table.getTable(), targets.toArray(new String[0]), SWT.READ_ONLY);

				@Override
				protected void setValue(Object element, Object value) {
					try {
						((GeoCondition) element).targetName = targets.get((Integer) value);
					} catch(Exception e) {}
					table.refresh();
				}

				@Override
				protected Object getValue(Object element) {
					if(targets.indexOf(((GeoCondition) element).targetName) >= 0) {
						return targets.indexOf(((GeoCondition) element).targetName);
					}
					return 0;
				}

				@Override
				protected CellEditor getCellEditor(Object element) {
					return targetEditor;
				}

				@Override
				protected boolean canEdit(Object element) {
					return true;
				}
			});
			colPlacemark.setEditingSupport(new EditingSupport(table) {
				// TextCellEditor editor = new TextCellEditor(table.getTable(), SWT.NONE);
				@Override
				protected void setValue(Object element, Object value) {
					((GeoCondition) element).placeMarkName = ((ComboBoxCellEditor) getCellEditor(element)).getItems()[(Integer) value];
					table.refresh();
				}

				@Override
				protected Object getValue(Object element) {
					int i = 0;
					for(String s : ((ComboBoxCellEditor) getCellEditor(element)).getItems()) {
						if(s.equals(((GeoCondition) element).placeMarkName)) {
							return i;
						}
						i++;
					}
					return 0;
				}

				@Override
				protected CellEditor getCellEditor(Object element) {

					String kml = ((GeoCondition) element).inputKml;
					List<String> values = new ArrayList<>();
					try {
						values = ((GeoFilter) node.getGatewayModel()).getPlaceMarerskByKml(kml);
					} catch(Exception e) {
						e.printStackTrace();
					}

					ComboBoxCellEditor targetEditor = new ComboBoxCellEditor(table.getTable(), values.toArray(new String[0]), SWT.READ_ONLY);
					return targetEditor;
				}

				@Override
				protected boolean canEdit(Object element) {
					return true;
				}
			});

			kmls = new ArrayList<>();
			kmls.add("Polygons"); //$NON-NLS-1$
			for(Transformation t : ((GeoFilter) node.getGatewayModel()).getInputs()) {
				if(t instanceof KMLInput || (t instanceof MdmContractFileInput && ((MdmContractFileInput)t).getFileTransfo() instanceof KMLInput)) {
					kmls.add(t.getName());
				}
			}

			colKml.setEditingSupport(new EditingSupport(table) {
				ComboBoxCellEditor kmlEditor = new ComboBoxCellEditor(table.getTable(), kmls.toArray(new String[0]), SWT.READ_ONLY);

				@Override
				protected void setValue(Object element, Object value) {
					try {
						((GeoCondition) element).inputKml = kmls.get((Integer) value);
					} catch(Exception e) {

					}
					table.refresh();
				}

				@Override
				protected Object getValue(Object element) {
					if(kmls.indexOf(((GeoCondition) element).inputKml) >= 0) {
						return kmls.indexOf(((GeoCondition) element).inputKml);
					}
					return 0;
				}

				@Override
				protected CellEditor getCellEditor(Object element) {
					return kmlEditor;
				}

				@Override
				protected boolean canEdit(Object element) {
					return true;
				}
			});
			super.refresh();
		} catch(Exception e) {}
	}
}
