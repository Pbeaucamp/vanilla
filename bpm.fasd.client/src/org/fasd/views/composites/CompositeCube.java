package org.fasd.views.composites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewSite;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataSource;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.Drill;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPDimensionGroup;
import org.fasd.olap.OLAPElement;
import org.fasd.olap.OLAPGroup;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPMeasureGroup;
import org.fasd.utils.trees.TreeMes;
import org.fasd.views.dialogs.DialogDrill;
import org.fasd.views.dialogs.DialogDrillCube;
import org.fasd.views.dialogs.DialogDrillReport;
import org.freeolap.FreemetricsPlugin;

public class CompositeCube {

	private Button ok, cancel;
	private OLAPCube cube = null;;
	private Composite info, dims, mes, groups;
	private Text name, desc, dataSource;
	private Combo fact;
	private ListViewer dimViewer, dimList, grpViewer;
	private ListViewer mesViewer, mesList, grpList, drillList;
	private List<OLAPMeasure> inputMes;
	private List<OLAPDimension> inputDim;
	private List<OLAPGroup> inputGrp;

	private Button addDim, addMes, addGrp;
	private Button delDim, delMes, delGrp;
	private HashMap<String, DataObject> mapFacts = new HashMap<String, DataObject>();

	public CompositeCube(Composite parent, OLAPCube c, IViewSite s) {
		cube = c;
		parent.setLayout(new GridLayout(2,false));
		
		//TABFOLDER
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		TabItem itemStart = new TabItem(tabFolder, SWT.NONE, 0);
    	itemStart.setText(LanguageText.CompositeCube_0);
    	itemStart.setControl(createInfo(tabFolder));
    	
    	TabItem itemDim = new TabItem(tabFolder, SWT.NONE, 0);
    	itemDim.setText(LanguageText.CompositeCube_1);
    	itemDim.setControl(createDims(tabFolder));
    	

    	TabItem itemMes = new TabItem(tabFolder, SWT.NONE, 0);
    	itemMes.setText(LanguageText.CompositeCube_2);
    	itemMes.setControl(createMes(tabFolder));
    	
    	TabItem itemGrp = new TabItem(tabFolder, SWT.NONE, 0);
    	itemGrp.setText(LanguageText.CompositeCube_3);
    	itemGrp.setControl(createGroups(tabFolder));
    	
    	
    	TabItem drillGrp = new TabItem(tabFolder, SWT.NONE, 0);
    	drillGrp.setText(LanguageText.CompositeCube_4);
    	drillGrp.setControl(createDrills(tabFolder));
    	fillData();
		
		ok = new Button(parent, SWT.PUSH);
		ok.setText(LanguageText.CompositeCube_5);
		ok.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent el) {
				if(fact.getText().trim().equals("")){ //$NON-NLS-1$
					MessageDialog.openInformation(CompositeCube.this.info.getShell(), LanguageText.CompositeCube_7, LanguageText.CompositeCube_8);
					return;
				}
				
				boolean needRefresh = false;
				
				if (cube == null)
					cube = new OLAPCube();
				
				if (!cube.getName().equals(name.getText())){
					cube.setName(name.getText());
					needRefresh = true;
				}
				
				
				cube.setDescription(desc.getText());
				
				
				cube.setFactDataObject(mapFacts.get(fact.getText()));
				
				List<OLAPDimension> _dims = new ArrayList<OLAPDimension>();
				for(OLAPDimension d : cube.getDims())
					_dims.add(d);
				
				List<OLAPMeasure> _mes = new ArrayList<OLAPMeasure>();
				for(OLAPMeasure d : cube.getMes()) {
					_mes.add(d);
				}
				Collection<OLAPMeasureGroup> _mesG = new ArrayList<OLAPMeasureGroup>();
				for(OLAPMeasureGroup d : cube.getMesGroups()) {
					_mesG.add(d);
				}
				
				Collection<OLAPDimensionGroup> _dimG = new ArrayList<OLAPDimensionGroup>();
				for(OLAPDimensionGroup d : cube.getDimGroups())
					_dimG.add(d);
				cube.clearDims();
				for(OLAPDimension d : (OLAPDimension[])dimList.getInput()){
//					if (!_dims.contains(d)){
						cube.addDim(d);
						needRefresh = true;
//					}
				}
				for(OLAPDimension d : _dims){
					if (!inputDim.contains(d)){
						cube.removeDim(d);
						needRefresh = true;
					}
				}
				
				for(OLAPMeasure d : (OLAPMeasure[])mesList.getInput()){
					if (!_mes.contains(d)){
						cube.addMes(d);
						needRefresh = true;
					}
				}
				for(OLAPMeasure d : _mes){
					if (!inputMes.contains(d)){
						cube.removeMes(d);
						needRefresh = true;
					}
				}
				
				for(OLAPGroup e : (OLAPGroup[])grpList.getInput()){
					if (e instanceof OLAPDimensionGroup){
						if (!_dimG.contains((OLAPDimensionGroup)e)){
							cube.addDimGroup((OLAPDimensionGroup)e);
							needRefresh = true;
						}
							
					}
					if(e instanceof OLAPMeasureGroup){
						if (!_mesG.contains((OLAPMeasureGroup)e)){
							cube.addMesGroup((OLAPMeasureGroup)e);
							needRefresh = true;
						}
					}
				}
				
				for(OLAPMeasureGroup g : _mesG){
					if (!inputGrp.contains(g)){
						cube.removeMesGroup(g);
						needRefresh = true;
					}
				}
				for(OLAPDimensionGroup g : _dimG){
					if (!inputGrp.contains(g)){
						cube.removeDimGroup(g);
						needRefresh = true;
					}
				}
				
				if (needRefresh)
					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
				
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
		}});
		
		cancel = new Button(parent, SWT.PUSH);
		cancel.setText(LanguageText.CompositeCube_9);
		cancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}
		});
		

		
		fillData();
		
	}

	private Control createInfo(TabFolder folder) {
		FAModel model = FreemetricsPlugin.getDefault().getFAModel();

		info = new Composite(folder, SWT.NONE);
		info.setLayout(new GridLayout(2, false));

		Label l1 = new Label(info, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.CompositeCube_10);

		name = new Text(info, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		Label l2 = new Label(info, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.CompositeCube_11);

		desc = new Text(info, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		Label ll = new Label(info, SWT.NONE);
		ll.setLayoutData(new GridData());
		ll.setText(LanguageText.CompositeCube_12);

		dataSource = new Text(info, SWT.BORDER | SWT.READ_ONLY);
		dataSource.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		dataSource.setEnabled(false);

		Label l6 = new Label(info, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(LanguageText.CompositeCube_13);

		fact = new Combo(info, SWT.BORDER | SWT.READ_ONLY);
		fact.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		for (DataSource ds : model.getDataSources()) {
			for (DataObject o : ds.getDataObjects()) {
				if (o.getDataObjectType().equals("fact") || o.getDataObjectType().equals("aggregate")) { //$NON-NLS-1$ //$NON-NLS-2$
					mapFacts.put("[" + o.getDataSource().getDSName() + "].[" + o.getName() + "]", o); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
		}
		fact.setItems(mapFacts.keySet().toArray(new String[mapFacts.size()]));

		return info;
	}

	private Control createMes(TabFolder folder) {
		mes = new Composite(folder, SWT.NONE);
		mes.setLayout(new GridLayout(3, false));

		mesViewer = new ListViewer(mes, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		mesViewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		mesViewer.setContentProvider(new DimContentProvider());
		mesViewer.setLabelProvider(new ViewerLabelProvider());

		addMes = new Button(mes, SWT.PUSH);
		addMes.setLayoutData(new GridData(GridData.FILL, GridData.END, false, true, 1, 1));
		addMes.setText(LanguageText.CompositeCube_19);
		addMes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Object o : ((StructuredSelection) mesViewer.getSelection()).toList()) {
					if (o instanceof OLAPMeasure) {
						inputMes.add((OLAPMeasure) o);
						createMesModel();
					}
				}

			}
		});

		mesList = new ListViewer(mes, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		mesList.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		mesList.setContentProvider(new DimContentProvider());
		mesList.setLabelProvider(new ViewerLabelProvider());

		delMes = new Button(mes, SWT.PUSH);
		delMes.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, true, 1, 1));
		delMes.setText(LanguageText.CompositeCube_20);
		delMes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Object o : ((StructuredSelection) mesList.getSelection()).toList()) {
					if (o instanceof OLAPMeasure) {
						inputMes.remove((OLAPMeasure) o);
						createMesModel();
					}

				}
			}
		});

		Button allMes = new Button(mes, SWT.PUSH);
		allMes.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, true, 1, 1));
		allMes.setText(LanguageText.CompositeCube_21);
		allMes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Object o : (Object[]) mesViewer.getInput()) {
					if (o instanceof OLAPMeasure) {
						inputMes.add((OLAPMeasure) o);
						createMesModel();
					}

				}
			}
		});

		createMesModel();
		return mes;

	}

	private Control createDims(TabFolder folder) {
		dims = new Composite(folder, SWT.FILL);
		dims.setLayout(new GridLayout(4, false));

		dimViewer = new ListViewer(dims, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		dimViewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		dimViewer.setContentProvider(new DimContentProvider());
		dimViewer.setLabelProvider(new ViewerLabelProvider());

		addDim = new Button(dims, SWT.PUSH);
		addDim.setLayoutData(new GridData(GridData.FILL, GridData.END, false, true, 1, 1));
		addDim.setText(LanguageText.CompositeCube_22);
		addDim.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object o = ((StructuredSelection) dimViewer.getSelection()).getFirstElement();
				if (o instanceof OLAPDimension) {
					inputDim.add((OLAPDimension) o);
					createDimModel();
				}
			}

		});

		dimList = new ListViewer(dims, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		dimList.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		dimList.setContentProvider(new DimContentProvider());
		dimList.setLabelProvider(new ViewerLabelProvider());
		
		Button upDim = new Button(dims, SWT.PUSH);
		upDim.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		upDim.setText(LanguageText.CompositeCube_6);
		upDim.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object o = ((StructuredSelection) dimList.getSelection()).getFirstElement();
				if (o instanceof OLAPDimension) {
					int i = inputDim.indexOf(o);
					if(i > 0) {
						inputDim.remove(o);
						inputDim.add(i - 1, (OLAPDimension) o);
						createDimModel();
					}
				}
			}
		});

		delDim = new Button(dims, SWT.PUSH);
		delDim.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, true, 1, 1));
		delDim.setText(LanguageText.CompositeCube_23);
		delDim.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Object o : ((StructuredSelection) dimList.getSelection()).toList()) {
					if (o instanceof OLAPDimension) {
						inputDim.remove((OLAPDimension) o);
						createDimModel();
					}
				}

			}
		});
		
		Button downDim = new Button(dims, SWT.PUSH);
		downDim.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 2));
		downDim.setText(LanguageText.CompositeCube_14);
		downDim.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object o = ((StructuredSelection) dimList.getSelection()).getFirstElement();
				if (o instanceof OLAPDimension) {
					int i = inputDim.indexOf(o);
					if(i < inputDim.size() - 1) {
						inputDim.remove(o);
						inputDim.add(i + 1, (OLAPDimension) o);
						createDimModel();
					}
				}
			}
		});

		Button all = new Button(dims, SWT.PUSH);
		all.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, true, 1, 1));
		all.setText(LanguageText.CompositeCube_24);
		all.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Object o : (Object[]) dimViewer.getInput()) {
					if (o instanceof OLAPDimension) {
						inputDim.add((OLAPDimension) o);
						
					}

				}
				createDimModel();
			}
		});

		createDimModel();
		return dims;
	}

	private Control createGroups(TabFolder folder) {
		groups = new Composite(folder, SWT.NONE);
		groups.setLayout(new GridLayout(3, false));

		grpViewer = new ListViewer(groups, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		grpViewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		grpViewer.setContentProvider(new DimContentProvider());
		grpViewer.setLabelProvider(new ViewerLabelProvider());

		addGrp = new Button(groups, SWT.PUSH);
		addGrp.setLayoutData(new GridData(GridData.FILL, GridData.END, false, true, 1, 1));
		addGrp.setText(LanguageText.CompositeCube_25);
		addGrp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				for (Object o : ((StructuredSelection) grpViewer.getSelection()).toList()) {
					if (o instanceof OLAPGroup) {
						inputGrp.add((OLAPGroup) o);
						createGroupModel();
					}

				}
			}
		});

		grpList = new ListViewer(groups, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		grpList.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		grpList.setContentProvider(new DimContentProvider());
		grpList.setLabelProvider(new ViewerLabelProvider());

		delGrp = new Button(groups, SWT.PUSH);
		delGrp.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, true, 1, 1));
		delGrp.setText(LanguageText.CompositeCube_26);
		delGrp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Object o : ((StructuredSelection) grpList.getSelection()).toList()) {
					if (o instanceof OLAPGroup) {
						inputGrp.remove((OLAPGroup) o);
						createGroupModel();
					}

				}
			}
		});

		Button all = new Button(groups, SWT.PUSH);
		all.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, true, 1, 1));
		all.setText(LanguageText.CompositeCube_27);
		all.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Object o : (Object[]) grpViewer.getInput()) {
					if (o instanceof OLAPGroup) {
						inputGrp.add((OLAPGroup) o);
						createGroupModel();
					}

				}
			}
		});

		createGroupModel();

		return groups;
	}

	private void createGroupModel() {
		if (cube == null)
			cube = new OLAPCube();

		if (inputGrp == null) {
			inputGrp = new ArrayList<OLAPGroup>();
			for (OLAPMeasureGroup m : cube.getMesGroups()) {
				inputGrp.add(m);
			}
			for (OLAPDimensionGroup m : cube.getDimGroups()) {
				inputGrp.add(m);
			}
		}

		List<OLAPGroup> l = new ArrayList<OLAPGroup>();
		for (OLAPMeasureGroup m : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getMeasureGroups()) {
			if (!inputGrp.contains(m) && m.getLevel() == 0)
				l.add(m);
		}
		for (OLAPDimensionGroup m : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getDimensionGroups()) {
			if (!inputGrp.contains(m) && m.getLevel() == 0)
				l.add(m);
		}
		OLAPGroup[] t = l.toArray(new OLAPGroup[l.size()]);
		grpViewer.setInput(t);
		grpList.setInput(inputGrp.toArray(new OLAPGroup[inputGrp.size()]));

	}

	private void createDimModel() {
		if (cube == null)
			cube = new OLAPCube();

		if (inputDim == null) {
			inputDim = new ArrayList<OLAPDimension>();
			for (OLAPDimension d : cube.getDims()) {
				inputDim.add(d);
			}
		}

		List<OLAPDimension> l = new ArrayList<OLAPDimension>();
		for (OLAPDimension d : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getDimensions()) {
			if (!inputDim.contains(d) && d.getGroup() == null)
				l.add(d);
		}
		dimViewer.setInput(l.toArray(new OLAPDimension[l.size()]));
		dimList.setInput(inputDim.toArray(new OLAPDimension[inputDim.size()]));
	}

	private void createMesModel() {
		if (cube == null)
			cube = new OLAPCube();

		if (inputMes == null) {
			inputMes = new ArrayList<OLAPMeasure>();
			for (OLAPMeasure d : cube.getMes()) {
				inputMes.add(d);
			}
		}

		List<OLAPMeasure> l = new ArrayList<OLAPMeasure>();
		for (OLAPMeasure d : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getMeasures()) {
			if (!inputMes.contains(d))
				l.add(d);
		}
		mesViewer.setInput(l.toArray(new OLAPMeasure[l.size()]));
		mesList.setInput(inputMes.toArray(new OLAPMeasure[inputMes.size()]));

	}

	private void fillData() {
		if (cube == null)
			return;
		name.setText(cube.getName());
		desc.setText(cube.getDescription());

		if (cube.getFactDataObject() == null) {
			return;
		}
		if (cube.getDataSource() != null)
			dataSource.setText(cube.getDataSource().getDSName());

		fact.setText("[" + cube.getFactDataObject().getDataSource().getDSName() + "].[" + cube.getFactDataObject().getName() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		drillList.setInput(cube.getDrills());

	}

	public class DimContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof OLAPDimension[])
				return (OLAPDimension[]) inputElement;
			if (inputElement instanceof OLAPMeasure[])
				return (OLAPMeasure[]) inputElement;
			if (inputElement instanceof OLAPGroup[])
				return (OLAPGroup[]) inputElement;

			return null;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	public class ViewerLabelProvider extends LabelProvider {

		@Override
		public Image getImage(Object element) {
			Display disp = CompositeCube.this.info.getShell().getDisplay();
			if (element instanceof OLAPDimension) {
				try {
					return new Image(disp, "icons/dimension.gif"); //$NON-NLS-1$
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (element instanceof OLAPMeasure) {
				try {
					if (((TreeMes) element).getOLAPMeasure().getType().equals("calculated")) //$NON-NLS-1$
						return new Image(disp, Platform.getInstallLocation().getURL().getPath() + "icons/obj_calc.png"); //$NON-NLS-1$
					if (((TreeMes) element).getOLAPMeasure().getType().equals("physical")) //$NON-NLS-1$
						return new Image(disp, Platform.getInstallLocation().getURL().getPath() + "icons/obj_measure.png"); //$NON-NLS-1$
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return null;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof OLAPDimension) {
				return ((OLAPDimension) element).getName();
			} else if (element instanceof OLAPMeasure) {
				return ((OLAPMeasure) element).getName();
			} else if (element instanceof OLAPElement) {
				return ((OLAPElement) element).getName();
			}
			return null;
		}
	}

	private Control createDrills(final TabFolder folder) {
		Composite c = new Composite(folder, SWT.FILL);
		c.setLayout(new GridLayout(2, true));

		ToolBar toolbar = new ToolBar(c, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		ToolItem addCube = new ToolItem(toolbar, SWT.PUSH);
		;
		addCube.setToolTipText(LanguageText.CompositeCube_36);
		addCube.setImage(new Image(FreemetricsPlugin.getDefault().getWorkbench().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/add.png")); //$NON-NLS-1$
		addCube.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogDrillCube d = new DialogDrillCube(folder.getShell(), cube);
				if (d.open() == DialogDrill.OK) {
					drillList.setInput(cube.getDrills());
				}
			}
		});

		ToolItem addReport = new ToolItem(toolbar, SWT.PUSH);
		;
		addReport.setToolTipText(LanguageText.CompositeCube_15);
		addReport.setImage(new Image(FreemetricsPlugin.getDefault().getWorkbench().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/FD-16.png")); //$NON-NLS-1$
		addReport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogDrillReport d = new DialogDrillReport(folder.getShell(), cube);
				if (d.open() == DialogDrill.OK) {
					drillList.setInput(cube.getDrills());
				}
			}
		});

		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		;
		del.setToolTipText(LanguageText.CompositeCube_38);
		del.setImage(new Image(FreemetricsPlugin.getDefault().getWorkbench().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/del.png")); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) drillList.getSelection();

				if (ss.isEmpty()) {
					return;
				} else {
					((List<Drill>) drillList.getInput()).remove((Drill) ss.getFirstElement());
					drillList.refresh();
				}

			}

		});

		drillList = new ListViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		drillList.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		drillList.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Drill) element).getDrillName();
			}

		});

		drillList.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<Drill> l = (List<Drill>) inputElement;
				return l.toArray(new Drill[l.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});
		drillList.setInput(cube.getDrills());

		return c;
	}
}
