package bpm.gateway.ui.views.property.sections.transformations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.calcul.GeoDispatch;
import bpm.gateway.core.transformations.kml.KMLInput;
import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class GeoDispatchSection extends AbstractPropertySection {
	
	private GeoDispatch geoDispatch;

	private Button btnOnlyOneColumn;
	private ComboViewer latitudeViewer, longitudeViewer;
	
	private ComboViewer placemarkZoneIdViewer, referenceLatitudeViewer, referenceLongitudeViewer;
	private Text txtDefaultPlacemarkZoneId;

	private ISelectionChangedListener latitudeListener = new ISelectionChangedListener() {

		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection ss = (IStructuredSelection) latitudeViewer.getSelection();

			if (ss.isEmpty()) {
				geoDispatch.setLatitudeIndex((StreamElement) null);
			}
			else {
				geoDispatch.setLatitudeIndex((StreamElement) ss.getFirstElement());
			}
		}
	};

	private ISelectionChangedListener longitudeListener = new ISelectionChangedListener() {

		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection ss = (IStructuredSelection) longitudeViewer.getSelection();

			if (ss.isEmpty()) {
				geoDispatch.setLongitudeIndex((StreamElement) null);
			}
			else {
				geoDispatch.setLongitudeIndex((StreamElement) ss.getFirstElement());
			}
		}
	};

	private SelectionListener sl = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			geoDispatch.setOnlyOneColumnGeoloc(btnOnlyOneColumn.getSelection());
			updateUi();
		}
	};

	private ModifyListener defaultPlacemarkListener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			String defaultPlacemark = txtDefaultPlacemarkZoneId.getText();
			geoDispatch.setDefaultPlacemarkId(defaultPlacemark);
		}
	};

	private ISelectionChangedListener placemarkListener = new ISelectionChangedListener() {

		public void selectionChanged(SelectionChangedEvent event) {
			int index = placemarkZoneIdViewer.getCCombo().getSelectionIndex();

			if (index >= 0) {
				geoDispatch.setPlacemarkIdIndex(index);
			}
			else {
				geoDispatch.setPlacemarkIdIndex((Integer) null);
			}
			geoDispatch.refreshDescriptor();
		}
	};

	private ISelectionChangedListener referenceLatitudeListener = new ISelectionChangedListener() {

		public void selectionChanged(SelectionChangedEvent event) {
			int index = referenceLatitudeViewer.getCCombo().getSelectionIndex();

			if (index >= 0) {
				geoDispatch.setInputReferenceLatitudeIndex(index);
			}
			else {
				geoDispatch.setInputReferenceLatitudeIndex((Integer) null);
			}
		}
	};

	private ISelectionChangedListener referenceLongitudeListener = new ISelectionChangedListener() {

		public void selectionChanged(SelectionChangedEvent event) {
			int index = referenceLongitudeViewer.getCCombo().getSelectionIndex();

			if (index >= 0) {
				geoDispatch.setInputReferenceLongitudeIndex(index);
			}
			else {
				geoDispatch.setInputReferenceLongitudeIndex((Integer) null);
			}
		}
	};

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		// parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());

		Composite main = getWidgetFactory().createComposite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout());
		
		org.eclipse.swt.widgets.Group group = getWidgetFactory().createGroup(main, Messages.GeoDispatchSection_7);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		btnOnlyOneColumn = getWidgetFactory().createButton(group, Messages.GeoDispatchSection_0, SWT.CHECK);
		btnOnlyOneColumn.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l = getWidgetFactory().createLabel(group, Messages.GeoDispatchSection_1);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		latitudeViewer = new ComboViewer(getWidgetFactory().createCCombo(group, SWT.BORDER | SWT.READ_ONLY));
		latitudeViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		latitudeViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((StreamElement) element).name;
			}
		});
		latitudeViewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public void dispose() {

			}

			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});

		l = getWidgetFactory().createLabel(group, Messages.GeoDispatchSection_2);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		longitudeViewer = new ComboViewer(getWidgetFactory().createCCombo(group, SWT.BORDER | SWT.READ_ONLY));
		longitudeViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		longitudeViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((StreamElement) element).name;
			}
		});
		longitudeViewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public void dispose() {

			}

			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		
		org.eclipse.swt.widgets.Group groupKml = getWidgetFactory().createGroup(main, Messages.GeoDispatchSection_8);
		groupKml.setLayout(new GridLayout(2, false));
		groupKml.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		l = getWidgetFactory().createLabel(groupKml, Messages.GeoDispatchSection_3);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		placemarkZoneIdViewer = new ComboViewer(getWidgetFactory().createCCombo(groupKml, SWT.BORDER | SWT.READ_ONLY));
		placemarkZoneIdViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		placemarkZoneIdViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((StreamElement) element).name;
			}
		});
		placemarkZoneIdViewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public void dispose() {

			}

			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});

		l = getWidgetFactory().createLabel(groupKml, Messages.GeoDispatchSection_4);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		referenceLatitudeViewer = new ComboViewer(getWidgetFactory().createCCombo(groupKml, SWT.BORDER | SWT.READ_ONLY));
		referenceLatitudeViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		referenceLatitudeViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((StreamElement) element).name;
			}
		});
		referenceLatitudeViewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public void dispose() {

			}

			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});

		l = getWidgetFactory().createLabel(groupKml, Messages.GeoDispatchSection_5);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		referenceLongitudeViewer = new ComboViewer(getWidgetFactory().createCCombo(groupKml, SWT.BORDER | SWT.READ_ONLY));
		referenceLongitudeViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		referenceLongitudeViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((StreamElement) element).name;
			}
		});
		referenceLongitudeViewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public void dispose() {

			}

			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		
		l = getWidgetFactory().createLabel(groupKml, Messages.GeoDispatchSection_6);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtDefaultPlacemarkZoneId = getWidgetFactory().createText(groupKml, ""); //$NON-NLS-1$
		txtDefaultPlacemarkZoneId.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.geoDispatch = (GeoDispatch) ((Node) ((NodePart) input).getModel()).getGatewayModel();
	}
	
	private void updateUi() {
		boolean onlyOneColumnGeoloc = btnOnlyOneColumn.getSelection();
		longitudeViewer.getCCombo().setEnabled(!onlyOneColumnGeoloc);
	}

	@Override
	public void refresh() {

		btnOnlyOneColumn.removeSelectionListener(sl);
		latitudeViewer.removeSelectionChangedListener(latitudeListener);
		longitudeViewer.removeSelectionChangedListener(longitudeListener);
		placemarkZoneIdViewer.removeSelectionChangedListener(placemarkListener);
		referenceLatitudeViewer.removeSelectionChangedListener(referenceLatitudeListener);
		referenceLongitudeViewer.removeSelectionChangedListener(referenceLongitudeListener);
		txtDefaultPlacemarkZoneId.removeModifyListener(defaultPlacemarkListener);

		GeoDispatch tr = geoDispatch;
		btnOnlyOneColumn.setSelection(tr.isOnlyOneColumnGeoloc());
		try {
			latitudeViewer.setInput(tr.getDescriptor(tr).getStreamElements());
			try {
				latitudeViewer.setSelection(new StructuredSelection(tr.getDescriptor(tr).getStreamElements().get(tr.getLatitudeIndex())));
			} catch (Exception ex) {
				latitudeViewer.setSelection(new StructuredSelection());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			latitudeViewer.setInput(new ArrayList<StreamElement>());
			latitudeViewer.setSelection(new StructuredSelection());
		}
		try {
			longitudeViewer.setInput(tr.getDescriptor(tr).getStreamElements());
			try {
				longitudeViewer.setSelection(new StructuredSelection(tr.getDescriptor(tr).getStreamElements().get(tr.getLongitudeIndex())));
			} catch (Exception ex) {
				longitudeViewer.setSelection(new StructuredSelection());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			longitudeViewer.setInput(new ArrayList<StreamElement>());
			longitudeViewer.setSelection(new StructuredSelection());
		}
		try {
			List<StreamElement> inputFields = new ArrayList<StreamElement>();
			for(Transformation t : tr.getInputs()) {
				if(t instanceof KMLInput || (t instanceof MdmContractFileInput && ((MdmContractFileInput)t).getFileTransfo() instanceof KMLInput)) {
					for(StreamElement e : t.getDescriptor(tr).getStreamElements()) {
						inputFields.add(e);
					}
					break;
				}
			}
			
			placemarkZoneIdViewer.setInput(inputFields);
			try {
				placemarkZoneIdViewer.setSelection(new StructuredSelection(inputFields.get(tr.getPlacemarkIdIndex())));
			} catch (Exception ex) {
				placemarkZoneIdViewer.setSelection(new StructuredSelection());
			}
			
			referenceLatitudeViewer.setInput(inputFields);
			try {
				referenceLatitudeViewer.setSelection(new StructuredSelection(inputFields.get(tr.getInputReferenceLatitudeIndex())));
			} catch (Exception ex) {
				referenceLatitudeViewer.setSelection(new StructuredSelection());
			}
			
			referenceLongitudeViewer.setInput(inputFields);
			try {
				referenceLongitudeViewer.setSelection(new StructuredSelection(inputFields.get(tr.getInputReferenceLongitudeIndex())));
			} catch (Exception ex) {
				referenceLongitudeViewer.setSelection(new StructuredSelection());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			placemarkZoneIdViewer.setInput(new ArrayList<StreamElement>());
			placemarkZoneIdViewer.setSelection(new StructuredSelection());
		}
		
		txtDefaultPlacemarkZoneId.setText(tr.getDefaultPlacemarkId() != null ? tr.getDefaultPlacemarkId() : ""); //$NON-NLS-1$
		
		updateUi();
		
		btnOnlyOneColumn.addSelectionListener(sl);
		latitudeViewer.addSelectionChangedListener(latitudeListener);
		longitudeViewer.addSelectionChangedListener(longitudeListener);
		placemarkZoneIdViewer.addSelectionChangedListener(placemarkListener);
		referenceLatitudeViewer.addSelectionChangedListener(referenceLatitudeListener);
		referenceLongitudeViewer.addSelectionChangedListener(referenceLongitudeListener);
		txtDefaultPlacemarkZoneId.addModifyListener(defaultPlacemarkListener);
	}

}
