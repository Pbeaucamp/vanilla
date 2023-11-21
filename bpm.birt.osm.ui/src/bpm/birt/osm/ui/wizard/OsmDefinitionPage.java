package bpm.birt.osm.ui.wizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.birt.report.model.api.ColumnHintHandle;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.birt.osm.core.model.OsmSerie;
import bpm.birt.osm.core.model.OsmSerieGeometry;
import bpm.birt.osm.core.model.OsmSerieMarker;
import bpm.birt.osm.core.reportitem.OsmReportItem;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;

public class OsmDefinitionPage extends WizardPage {

	private DataSetHandle dataSet;
	private Label lblNoDataSet;
	private Button setDataset;

	private OsmReportItem item;
	private ExtendedItemHandle handle;

	private boolean init = false;

	private Text txtVanillaUrl;
	private Text txtVanillaLogin;
	private Text txtVanillaPassword;

	private ComboViewer mapViewer;
	private Combo lstZone;
	private Combo lstValue;

	public OsmDefinitionPage(String pageName, OsmReportItem item, DataSetHandle dataSet, ExtendedItemHandle handle) {
		super(pageName);
		this.item = item;
		this.dataSet = dataSet;
		this.handle = handle;
	}

	private void setDataSetInfo() {
		if (dataSet == null) {
			lblNoDataSet.setText("No dataset is set to this item");
			setDataset.setText("Select Dataset");
		}
		else {
			lblNoDataSet.setText(dataSet.getName());
			setDataset.setText("Change DataBinding");
		}
	}

	private void createContentNoDataset(Composite main) {
		lblNoDataSet = new Label(main, SWT.NONE);
		lblNoDataSet.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

		setDataset = new Button(main, SWT.PUSH);
		setDataset.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		setDataset.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				BindingDatasetDialog dial = new BindingDatasetDialog(getShell(), handle);
				if (dial.open() == Dialog.OK) {
					try {
						if (init)
							refreshData();
						else
							initValues();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					setDataSetInfo();

				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		setDataSetInfo();
	}

	private void initValues() throws Exception {
		refreshData();
		init = true;
	}

	private void refreshData() throws Exception {
		dataSet = handle.getDataSet();
		if(dataSet != null){
			Iterator<ColumnHintHandle> it = dataSet.columnHintsIterator();
			List<String> nameColumn = new ArrayList<String>();
			while(it.hasNext()){
				nameColumn.add(it.next().getColumnName());
			}
			
			String[] nameCol = nameColumn.toArray(new String[nameColumn.size()]);
			
			lstZone.setItems(nameCol);
			lstValue.setItems(nameCol);
			if(item.getValueColumn() != null) {
				int index = lstValue.indexOf(item.getValueColumn());
				lstValue.select(index);
			}
			if(item.getZoneColumn() != null) {
				int index = lstZone.indexOf(item.getZoneColumn());
				lstZone.select(index);
			}
		}
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(1, false));
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Group groupMap = new Group(main, SWT.NONE);
		groupMap.setLayout(new GridLayout(2, false));
		groupMap.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		groupMap.setText("Map");

		Label lblVanillaUrl = new Label(groupMap, SWT.NONE);
		lblVanillaUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblVanillaUrl.setText("Vanilla Url");

		txtVanillaUrl = new Text(groupMap, SWT.BORDER);
		txtVanillaUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtVanillaUrl.setText(item.getVanillaUrl());

		Label lblVanillaLogin = new Label(groupMap, SWT.NONE);
		lblVanillaLogin.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblVanillaLogin.setText("Vanilla Login");

		txtVanillaLogin = new Text(groupMap, SWT.BORDER);
		txtVanillaLogin.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtVanillaLogin.setText(item.getVanillaLogin());
		
		Label lblVanillaPassword = new Label(groupMap, SWT.NONE);
		lblVanillaPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblVanillaPassword.setText("Vanilla Password");

		txtVanillaPassword = new Text(groupMap, SWT.BORDER | SWT.PASSWORD);
		txtVanillaPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtVanillaPassword.setText(item.getVanillaPassword());
		
		Button btnConnection = new Button(groupMap, SWT.PUSH);
		btnConnection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		btnConnection.setText("Connection");
		btnConnection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadMaps();
				
			}
		});

		Label lblMap = new Label(groupMap, SWT.NONE);
		lblMap.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblMap.setText("Map");

		mapViewer = new ComboViewer(groupMap, SWT.READ_ONLY);
		mapViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		mapViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MapVanilla) element).getName();
			}
		});
		mapViewer.setContentProvider(new ArrayContentProvider());
		mapViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				MapVanilla map = (MapVanilla) ((IStructuredSelection)mapViewer.getSelection()).getFirstElement();
				if(!(map.getId() == item.getMapId())) {
					List<OsmSerie> listSerie = new ArrayList<OsmSerie>();
					boolean first = true;
					for(MapDataSet data : map.getDataSetList()) {
						OsmSerie serie = null;
						data.setType("polygon");
						if(data.getType().equals("polygon") || data.getType().equals("line")) {
							OsmSerieGeometry geo = new OsmSerieGeometry();
							
							serie = geo;
						}
						else {
							OsmSerieMarker marker = new OsmSerieMarker();
							marker.setMinMarkerSize(data.getMarkerSizeMin());
							marker.setMaxMarkerSize(data.getMarkerSizeMax());
							
							serie = marker;
						}
						serie.setDatasetId(data.getId());
						if(first) {
							serie.setDisplay(true);
							first = false;
						}
						serie.setName(data.getName());
						serie.setType(data.getType());
						
						listSerie.add(serie);
					}
					try {
						item.setSerieList(listSerie);
					} catch (SemanticException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		Group groupData = new Group(main, SWT.NONE);
		groupData.setLayout(new GridLayout(2, false));
		groupData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		groupData.setText("Data");

		createContentNoDataset(groupData);
		
		Label lblZone = new Label(groupData, SWT.NONE);
		lblZone.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblZone.setText("Zone Column");
		
		lstZone = new Combo(groupData, SWT.READ_ONLY);
		lstZone.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label lblValue = new Label(groupData, SWT.NONE);
		lblValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblValue.setText("Value Column");
		
		lstValue = new Combo(groupData, SWT.READ_ONLY);
		lstValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		setControl(main);
		
		try {
			fillFields();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	protected void loadMaps() {
		RemoteMapDefinitionService service = new RemoteMapDefinitionService();
		service.configure(txtVanillaUrl.getText());
		try {
			List<MapVanilla> maps = service.getAllMapsVanilla();
			mapViewer.setInput(maps);
			if(item.getMapId() > 0) {
				for(MapVanilla map : maps) {
					if(map.getId() == item.getMapId()) {
						mapViewer.setSelection(new StructuredSelection(map), true);
						break;
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void fillFields() throws Exception {
		txtVanillaUrl.setText(item.getVanillaUrl());
		txtVanillaLogin.setText(item.getVanillaLogin());
		txtVanillaPassword.setText(item.getVanillaPassword());
		
		if(item.getMapId() > 0) {
			loadMaps();
		}
		
		initValues();
	}

	public MapVanilla getSelectedMap() {
		return (MapVanilla) ((IStructuredSelection)mapViewer.getSelection()).getFirstElement();
	}

	public void fillItem() throws SemanticException {
		item.setVanillaUrl(txtVanillaUrl.getText());
		item.setVanillaLogin(txtVanillaLogin.getText());
		item.setVanillaPassword(txtVanillaPassword.getText());
		MapVanilla map = (MapVanilla) ((IStructuredSelection)mapViewer.getSelection()).getFirstElement();
		item.setMapId(map.getId());
		item.setValueColumn(lstValue.getText());
		item.setZoneColumn(lstZone.getText());
	}

}
