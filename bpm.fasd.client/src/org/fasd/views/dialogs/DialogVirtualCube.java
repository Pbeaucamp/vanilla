package org.fasd.views.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
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
import org.fasd.datasource.DataSource;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPElement;
import org.fasd.olap.OLAPGroup;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.virtual.VirtualCube;
import org.fasd.olap.virtual.VirtualDimension;
import org.fasd.olap.virtual.VirtualMeasure;
import org.fasd.security.SecurityGroup;
import org.fasd.utils.trees.TreeContentProvider;
import org.fasd.utils.trees.TreeCube;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeLabelProvider;
import org.fasd.utils.trees.TreeMes;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeVirtualCube;
import org.fasd.utils.trees.TreeVirtualDim;
import org.fasd.utils.trees.TreeVirtualMes;
import org.freeolap.FreemetricsPlugin;

public class DialogVirtualCube extends Dialog {

	private Text name;
	private TreeViewer cubeViewer, virtualViewer;
	private ListViewer secuList, secuViewer;
	private Combo dataSource;
	private VirtualCube virtual = new VirtualCube();
	private HashMap<String, DataSource> map = new HashMap<String, DataSource>();

	@Override
	protected void okPressed() {
		virtual.setName(name.getText());
		virtual.setDataSource(map.get(dataSource.getText()));
		if (virtual.getVirtualDimensions().size() < 2) {
			MessageDialog.openInformation(this.getShell(), LanguageText.DialogVirtualCube_Info, LanguageText.DialogVirtualCube_Dim_R_Needed);
		}

		super.okPressed();
	}

	@Override
	protected void initializeBounds() {
		this.getShell().setText(LanguageText.DialogVirtualCube_Create_Virtual_Cube);
		super.initializeBounds();
		this.getShell().setSize(800, 600);
	}

	public DialogVirtualCube(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite par) {
		Composite parent = new Composite(par, SWT.NONE);

		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		// TABFOLDER
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		TabItem itemGeneral = new TabItem(tabFolder, SWT.NONE, 0);
		itemGeneral.setText(LanguageText.DialogVirtualCube_General);
		itemGeneral.setControl(createGeneral(tabFolder));

		TabItem itemSecu = new TabItem(tabFolder, SWT.NONE, 1);
		itemSecu.setText(LanguageText.DialogVirtualCube_Secu);
		itemSecu.setControl(createSecurity(tabFolder));

		return parent;
	}

	private void createModel() {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		TreeParent rd = new TreeParent(LanguageText.DialogVirtualCube_Dimensions);
		root.addChild(rd);
		for (OLAPDimension d : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getDimensions()) {
			TreeDim td = new TreeDim(d);
			rd.addChild(td);
		}

		for (OLAPCube c : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getCubes()) {
			TreeCube tc = new TreeCube(c);
			for (OLAPMeasure m : c.getMes()) {
				TreeMes tm = new TreeMes(m);
				tc.addChild(tm);
			}
			root.addChild(tc);
		}

		cubeViewer.setInput(root);
	}

	private void createVirtual() {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		TreeVirtualCube cube = new TreeVirtualCube(virtual);

		for (VirtualDimension vd : virtual.getVirtualDimensions()) {
			TreeVirtualDim dim = new TreeVirtualDim(vd);
			cube.addChild(dim);
		}

		for (VirtualMeasure vm : virtual.getVirtualMeasure()) {
			TreeVirtualMes mes = new TreeVirtualMes(vm);
			cube.addChild(mes);
		}

		root.addChild(cube);
		virtualViewer.setInput(root);
	}

	public VirtualCube getVirtualCube() {
		return virtual;
	}

	private Control createGeneral(TabFolder folder) {
		Composite container = new Composite(folder, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		container.setLayout(new GridLayout(3, false));

		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false));
		l.setText(LanguageText.DialogVirtualCube_Name_);

		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

		Label l4 = new Label(container, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false));
		l4.setText(LanguageText.DialogVirtualCube_DS);

		dataSource = new Combo(container, SWT.NONE | SWT.READ_ONLY);
		dataSource.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

		for (DataSource d : FreemetricsPlugin.getDefault().getFAModel().getDataSources()) {
			map.put(d.getDSName(), d);
		}

		dataSource.setItems(map.keySet().toArray(new String[map.size()]));

		cubeViewer = new TreeViewer(container, SWT.BORDER | SWT.MULTI);
		cubeViewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		;
		cubeViewer.setLabelProvider(new TreeLabelProvider());
		cubeViewer.setContentProvider(new TreeContentProvider());
		cubeViewer.setAutoExpandLevel(2);

		Button add = new Button(container, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.FILL, GridData.END, false, true, 1, 1));
		add.setText(LanguageText.DialogVirtualCube_Add);
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection ss = (StructuredSelection) cubeViewer.getSelection();
				Iterator it = ss.iterator();

				while (it.hasNext()) {
					Object o = it.next();

					if (o instanceof TreeDim) {
						OLAPDimension d = ((TreeDim) o).getOLAPDimension();
						VirtualDimension vd = new VirtualDimension(d);
						virtual.addVirtualDimension(vd);
					} else if (o instanceof TreeMes) {
						OLAPMeasure m = ((TreeMes) o).getOLAPMeasure();
						OLAPCube c = ((TreeCube) ((TreeMes) o).getParent()).getOLAPCube();
						VirtualMeasure vm = new VirtualMeasure(c, m);
						virtual.addVirtualMeasure(vm);

					}
				}

				createVirtual();
			}

		});

		virtualViewer = new TreeViewer(container, SWT.BORDER);
		virtualViewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		;
		virtualViewer.setLabelProvider(new TreeLabelProvider());
		virtualViewer.setContentProvider(new TreeContentProvider());
		virtualViewer.setAutoExpandLevel(2);

		Button rem = new Button(container, SWT.PUSH);
		rem.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, true, 1, 1));
		rem.setText(LanguageText.DialogVirtualCube_Remove);
		rem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection ss = (StructuredSelection) virtualViewer.getSelection();
				Object o = ss.getFirstElement();
				if (o instanceof TreeVirtualDim) {
					virtual.removeVirtualDimension(((TreeVirtualDim) o).getVirtualDimension());
					createVirtual();
				} else if (o instanceof TreeVirtualMes) {
					virtual.removeVirtualMeasure(((TreeVirtualMes) o).getVirtualMeasure());
					createVirtual();
				}
			}

		});

		createModel();
		return container;
	}

	private Control createSecurity(TabFolder folder) {
		Composite secu = new Composite(folder, SWT.NONE);
		secu.setLayout(new GridLayout(3, false));

		secuViewer = new ListViewer(secu, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		secuViewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		secuViewer.setContentProvider(new DimContentProvider());
		secuViewer.setLabelProvider(new ViewerLabelProvider());

		Button addSecu = new Button(secu, SWT.PUSH);
		addSecu.setLayoutData(new GridData(GridData.FILL, GridData.END, false, true, 1, 1));
		addSecu.setText(LanguageText.DialogVirtualCube_Add);
		addSecu.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Object o : ((StructuredSelection) secuViewer.getSelection()).toList()) {
					if (o instanceof SecurityGroup) {
						virtual.addSecurityGroup((SecurityGroup) o);
						createSecuModel();
					}
				}

			}
		});

		secuList = new ListViewer(secu, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		secuList.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		secuList.setContentProvider(new DimContentProvider());
		secuList.setLabelProvider(new ViewerLabelProvider());

		Button delSecu = new Button(secu, SWT.PUSH);
		delSecu.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, true, 1, 1));
		delSecu.setText(LanguageText.DialogVirtualCube_Remove);
		delSecu.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Object o : ((StructuredSelection) secuList.getSelection()).toList()) {
					if (o instanceof SecurityGroup) {
						virtual.removeSecurityGroup((SecurityGroup) o);
						createSecuModel();
					}

				}
			}
		});

		Button allMes = new Button(secu, SWT.PUSH);
		allMes.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, true, 1, 1));
		allMes.setText(LanguageText.DialogVirtualCube_Add_All);
		allMes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Object o : (Object[]) secuViewer.getInput()) {
					if (o instanceof SecurityGroup) {
						virtual.addSecurityGroup((SecurityGroup) o);
						createSecuModel();
					}

				}
			}
		});

		createSecuModel();
		return secu;

	}

	private void createSecuModel() {
		if (virtual == null)
			virtual = new VirtualCube();

		for (SecurityGroup m : virtual.getSecurityGroups()) {
			secuList.remove(m);
		}

		List<SecurityGroup> l = new ArrayList<SecurityGroup>();
		for (SecurityGroup m : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getSecurityGroups()) {
			if (!virtual.getSecurityGroups().contains(m))
				l.add(m);
		}

		SecurityGroup[] t = l.toArray(new SecurityGroup[l.size()]);
		secuViewer.setInput(t);
		secuList.setInput(virtual.getSecurityGroups().toArray(new SecurityGroup[virtual.getSecurityGroups().size()]));

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
			Display disp = Display.getCurrent();
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
}
