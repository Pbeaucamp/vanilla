package org.fasd.views.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataSource;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPDimensionGroup;
import org.fasd.olap.OLAPElement;
import org.fasd.olap.OLAPGroup;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPMeasureGroup;
import org.fasd.utils.trees.TreeMes;
import org.freeolap.FreemetricsPlugin;

public class DialogCube extends Dialog {
	private OLAPCube cube = null;;
	private Composite info, dims, mes, groups;
	private Text name, desc;
	private Combo fact;
	private ListViewer dimViewer, dimList, grpViewer;
	private ListViewer mesViewer, mesList, grpList;
	private List<OLAPMeasure> inputMes;
	private List<OLAPDimension> inputDim;
	private List<OLAPGroup> inputGrp;
	private Button addDim, addMes, addGrp;

	private HashMap<String, DataObject> mapFacts = new HashMap<String, DataObject>();
	private Button delGrp, delDim, delMes;

	public DialogCube(Shell parentShell) {
		super(parentShell);
	}

	public DialogCube(Shell sh, OLAPCube cube) {
		super(sh);
		this.cube = cube;
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		this.getShell().setText(LanguageText.DialogCube_New_Cube);
		this.getShell().setSize(400, 300);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, true));
		// TABFOLDER
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

		TabItem itemStart = new TabItem(tabFolder, SWT.NONE, 0);
		itemStart.setText(LanguageText.DialogCube_Informations);
		itemStart.setControl(createInfo(tabFolder));

		TabItem itemDim = new TabItem(tabFolder, SWT.NONE, 0);
		itemDim.setText(LanguageText.DialogCube_Dimensions);
		itemDim.setControl(createDims(tabFolder));

		TabItem itemMes = new TabItem(tabFolder, SWT.NONE, 0);
		itemMes.setText(LanguageText.DialogCube_Measures);
		itemMes.setControl(createMes(tabFolder));

		TabItem itemGrp = new TabItem(tabFolder, SWT.NONE, 0);
		itemGrp.setText(LanguageText.DialogCube_Groups);
		itemGrp.setControl(createGroups(tabFolder));

		fillData();

		return parent;
	}

	private Control createInfo(TabFolder folder) {
		FAModel model = FreemetricsPlugin.getDefault().getFAModel();

		info = new Composite(folder, SWT.NONE);
		info.setLayout(new GridLayout(3, false));

		Label l1 = new Label(info, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.DialogCube_Name_);

		name = new Text(info, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l2 = new Label(info, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.DialogCube_Descr_);

		desc = new Text(info, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l6 = new Label(info, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(LanguageText.DialogCube_Fact_DataObj);

		fact = new Combo(info, SWT.BORDER | SWT.READ_ONLY);
		fact.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		for (DataSource ds : model.getDataSources()) {
			for (DataObject o : ds.getDataObjects()) {
				if (o.getDataObjectType().equals("fact") || o.getDataObjectType().equals("aggregate")) { //$NON-NLS-1$ //$NON-NLS-2$
					mapFacts.put("[" + o.getDataSource().getDSName() + "].[" + o.getName() + "]", o); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
		}
		fact.setItems(mapFacts.keySet().toArray(new String[mapFacts.size()]));
		fact.select(0);

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
		addMes.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, true, 1, 1));
		addMes.setText(LanguageText.DialogCube_Add);
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
		delMes.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, true, 1, 1));
		delMes.setText(LanguageText.DialogCube_Remove);
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
		allMes.setText(LanguageText.DialogCube_Add_All);
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

	private Control createGroups(TabFolder folder) {
		groups = new Composite(folder, SWT.NONE);
		groups.setLayout(new GridLayout(3, false));

		grpViewer = new ListViewer(groups, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		grpViewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		grpViewer.setContentProvider(new DimContentProvider());
		grpViewer.setLabelProvider(new ViewerLabelProvider());

		addGrp = new Button(groups, SWT.PUSH);
		addGrp.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, true, 1, 1));
		addGrp.setText(LanguageText.DialogCube_Add);
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
		delGrp.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, true, 1, 1));
		delGrp.setText(LanguageText.DialogCube_Remove);
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
		all.setText(LanguageText.DialogCube_Add_All);
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

	private Control createDims(TabFolder folder) {
		dims = new Composite(folder, SWT.NONE);
		dims.setLayout(new GridLayout(4, false));

		dimViewer = new ListViewer(dims, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		dimViewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		dimViewer.setContentProvider(new DimContentProvider());
		dimViewer.setLabelProvider(new ViewerLabelProvider());

		addDim = new Button(dims, SWT.PUSH);
		addDim.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, true, 1, 1));
		addDim.setText(LanguageText.DialogCube_Add);
		addDim.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Object o : ((StructuredSelection) dimViewer.getSelection()).toList()) {
					if (o instanceof OLAPDimension) {
						inputDim.add((OLAPDimension) o);
						createDimModel();
					}
				}

			}

		});

		dimList = new ListViewer(dims, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		dimList.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		dimList.setContentProvider(new DimContentProvider());
		dimList.setLabelProvider(new ViewerLabelProvider());
		
		Button upDim = new Button(dims, SWT.PUSH);
		upDim.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		upDim.setText(LanguageText.DialogCube_0);
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
		delDim.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, true, 1, 1));
		delDim.setText(LanguageText.DialogCube_Remove);
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
		downDim.setText(LanguageText.DialogCube_2);
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
		all.setText(LanguageText.DialogCube_Add_All);
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
			for (OLAPMeasure m : cube.getMes()) {
				inputMes.add(m);
			}
		}

		List<OLAPMeasure> l = new ArrayList<OLAPMeasure>();
		for (OLAPMeasure m : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getMeasures()) {
			if (!inputMes.contains(m) && m.getGroup() == null)
				l.add(m);
		}
		mesViewer.setInput(l.toArray(new OLAPMeasure[l.size()]));
		mesList.setInput(inputMes.toArray(new OLAPMeasure[inputMes.size()]));

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

	private void fillData() {
		if (cube == null)
			return;

		name.setText(cube.getName());
		desc.setText(cube.getDescription());

		for (int i = 0; i < fact.getItemCount(); i++) {
			DataObject o = cube.getFactDataObject();
			if (o != null && o.getId().equals(mapFacts.get(fact.getItem(i)))) {
				fact.select(i);
				break;
			}

		}
	}

	@Override
	protected void okPressed() {
		if (fact.getText().trim().equals("")) { //$NON-NLS-1$
			MessageDialog.openInformation(this.getShell(), LanguageText.DialogCube_Warning, LanguageText.DialogCube_Select_A_DataObj);
			return;
		}

		if (name.getText() == null || name.getText().trim().equals("")) { //$NON-NLS-1$
			MessageDialog.openInformation(this.getShell(), LanguageText.DialogCube_Warning, LanguageText.DialogCube_3);
			return;
		}

		if (cube == null) {
			cube = new OLAPCube();
		}

		cube.setName(name.getText());
		cube.setDescription(desc.getText());
		cube.setFactDataObject(mapFacts.get(fact.getText()));

		for (OLAPDimension d : (OLAPDimension[]) dimList.getInput()) {
			cube.addDim(d);
		}

		for (OLAPMeasure d : (OLAPMeasure[]) mesList.getInput()) {
			cube.addMes(d);
		}

		for (OLAPElement e : (OLAPElement[]) grpList.getInput()) {
			if (e instanceof OLAPDimensionGroup) {
				cube.addDimGroup((OLAPDimensionGroup) e);
			}
			if (e instanceof OLAPMeasureGroup) {
				cube.addMesGroup((OLAPMeasureGroup) e);
			}
		}

		super.okPressed();
	}

	public OLAPCube getCube() {
		return cube;
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
			Display disp = DialogCube.this.getShell().getDisplay();
			if (element instanceof OLAPDimension) {
				try {
					return new Image(disp, Platform.getInstallLocation().getURL().getPath() + "icons/dimension.gif"); //$NON-NLS-1$
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
			if (element instanceof OLAPElement) {
				return ((OLAPElement) element).getName();
			}

			return null;
		}

	}

}
