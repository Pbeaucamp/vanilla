package org.fasd.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.datasource.DatasourceOda;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPDimensionGroup;
import org.fasd.olap.OLAPGroup;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPMeasureGroup;
import org.fasd.olap.OLAPRelation;
import org.fasd.olap.OlapDynamicMeasure;
import org.fasd.olap.SecurityProvider;
import org.fasd.olap.ServerConnection;
import org.fasd.olap.virtual.VirtualCube;
import org.fasd.security.SecurityDim;
import org.fasd.security.SecurityGroup;
import org.fasd.security.View;
import org.fasd.utils.trees.TreeColumn;
import org.fasd.utils.trees.TreeConnection;
import org.fasd.utils.trees.TreeCube;
import org.fasd.utils.trees.TreeDatabase;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeDimGroup;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeLevel;
import org.fasd.utils.trees.TreeMes;
import org.fasd.utils.trees.TreeMesGroup;
import org.fasd.utils.trees.TreeRelation;
import org.fasd.utils.trees.TreeTable;
import org.fasd.utils.trees.TreeVirtualCube;
import org.fasd.utils.trees.TreeVirtualDim;
import org.fasd.utils.trees.TreeVirtualMes;
import org.fasd.views.SecurityView.TreeDimView;
import org.fasd.views.SecurityView.TreeGroup;
import org.fasd.views.SecurityView.TreeView;
import org.fasd.views.composites.CompositeColumn;
import org.fasd.views.composites.CompositeCube;
import org.fasd.views.composites.CompositeDataSource;
import org.fasd.views.composites.CompositeDataSourceConnection2;
import org.fasd.views.composites.CompositeDetailRelation;
import org.fasd.views.composites.CompositeDimView;
import org.fasd.views.composites.CompositeDimension;
import org.fasd.views.composites.CompositeGroup;
import org.fasd.views.composites.CompositeHierarchy;
import org.fasd.views.composites.CompositeLevel;
import org.fasd.views.composites.CompositeSecurityProvider;
import org.fasd.views.composites.CompositeServerConnection;
import org.fasd.views.composites.CompositeTable;
import org.fasd.views.composites.CompositeView;
import org.fasd.views.composites.CompositeVirtualCube;
import org.freeolap.FreemetricsPlugin;

import bpm.fasd.ui.measure.definition.composite.BaseAggregationComposite;
import bpm.fasd.ui.measure.definition.composite.CalculatedMeasureComposite;
import bpm.fasd.ui.measure.definition.composite.ClassicMeasureComposite;
import bpm.fasd.ui.measure.definition.composite.DynamicMeasureComposite;
import bpm.fasd.ui.measure.definition.composite.IAggregationComposite;
import bpm.fasd.ui.measure.definition.composite.LastMeasureComposite;

public class DetailView extends ViewPart implements ISelectionListener {
	public static final String ID = "org.fasd.views.detailView"; //$NON-NLS-1$
	private Composite par;

	private Composite container = null;

	public DetailView() {
		super();

	}

	public void createPartControl(Composite parent) {
		par = parent;
		par.setLayout(new GridLayout());
		par.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void setFocus() {
		if (FreemetricsPlugin.getDefault().isRepositoryConnectionDefined()) {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.DetailView_1 + FreemetricsPlugin.getDefault().getRepositoryConnection().getContext().getRepository().getUrl());
		} else {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.DetailView_2);
		}
	}

	public void loadData() {
		if (container != null) {
			container.dispose();
		}
		container = new Composite(par, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void loadData(Object elem) {
		System.out.println("DetailView.loadData() on " + elem.getClass()); //$NON-NLS-1$

		try {
			par.getChildren()[0].dispose();
		} catch (Exception e) {

		}

		ScrolledComposite sc = new ScrolledComposite(par, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		container = new Composite(sc, SWT.NONE);

		sc.setContent(container);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData());
		if (elem instanceof OLAPDimension) {
			new CompositeDimension(container, (OLAPDimension) elem);
			sc.pack();
		} else if (elem instanceof OLAPHierarchy) {
			new CompositeHierarchy(container, (OLAPHierarchy) elem, this.getViewSite());
			sc.pack();
		} else if (elem instanceof OLAPLevel) {
			new CompositeLevel(container, (OLAPLevel) elem);
			sc.pack();
		} else if (elem instanceof OLAPCube) {
			new CompositeCube(container, (OLAPCube) elem, this.getViewSite());
			sc.pack();
			container.setBounds(0, 0, 420, 250);
		} else if (elem instanceof OLAPMeasureGroup) {
			new CompositeGroup(container, (OLAPMeasureGroup) elem, this.getViewSite());
			sc.pack();
		} else if (elem instanceof OLAPMeasure) {
			container.setLayout(new GridLayout());
			container.setLayoutData(new GridData());

			OLAPMeasure mes = (OLAPMeasure) elem;

			BaseAggregationComposite comp = null;

			if (mes instanceof OlapDynamicMeasure) {
				comp = new DynamicMeasureComposite(container, SWT.BEGINNING, null, FreemetricsPlugin.getDefault().getFAModel());
			}

			else if (mes.getType().equalsIgnoreCase("calculated")) { //$NON-NLS-1$
				comp = new CalculatedMeasureComposite(container, SWT.BEGINNING, null, FreemetricsPlugin.getDefault().getFAModel());
			}

			else {
				if (mes.getLastTimeDimensionName() != null && !mes.getLastTimeDimensionName().equals("")) { //$NON-NLS-1$
					comp = new LastMeasureComposite(container, SWT.BEGINNING, null, FreemetricsPlugin.getDefault().getFAModel());
				}

				else {
					comp = new ClassicMeasureComposite(container, SWT.BEGINNING, null, FreemetricsPlugin.getDefault().getFAModel());
				}
			}
			comp.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			comp.setEditedMeasureData(mes);

			Button btnApplyCh = new Button(comp, SWT.PUSH);
			btnApplyCh.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			btnApplyCh.setText(LanguageText.DetailView_6);

			MeasureChangeSelectionListener listener = new MeasureChangeSelectionListener(comp, mes);
			btnApplyCh.addSelectionListener(listener);
			sc.pack();
		} else if (elem instanceof DataSource && !(elem instanceof DatasourceOda)) {
			new CompositeDataSource(container, SWT.NONE, (DataSource) elem, false);
			sc.pack();
		} else if (elem instanceof DataObject) {// && !(elem instanceof
												// DataObjectOda)) {
			new CompositeTable(container, (DataObject) elem, this.getViewSite());
			sc.pack();
		} else if (elem instanceof DataObjectItem) {
			new CompositeColumn(container, (DataObjectItem) elem, this.getViewSite());
			sc.pack();
		} else if (elem instanceof OLAPMeasureGroup || elem instanceof OLAPDimensionGroup) {
			new CompositeGroup(container, (OLAPGroup) elem, this.getViewSite());
			sc.pack();
		} else if (elem instanceof SecurityGroup) {
			new CompositeGroup(container, (OLAPGroup) elem, this.getViewSite());
			sc.pack();
		} else if (elem instanceof SecurityDim) {
			new CompositeDimView(container, (SecurityDim) elem);
			sc.pack();
		} else if (elem instanceof View) {
			new CompositeView(container, (View) elem, this.getViewSite());
			sc.pack();
		} else if (elem instanceof ServerConnection) {
			new CompositeServerConnection(container, (ServerConnection) elem, this.getViewSite());
			sc.pack();
		} else if (elem instanceof DataSourceConnection && !(((DataSourceConnection) elem).getParent() instanceof DatasourceOda)) {
			new CompositeDataSourceConnection2(container, (DataSourceConnection) elem, this.getViewSite());
			sc.pack();
		} else if (elem instanceof SecurityProvider) {
			new CompositeSecurityProvider(container, (SecurityProvider) elem, this.getViewSite());
			sc.pack();
		} else if (elem instanceof OLAPRelation) {
			new CompositeDetailRelation(container, SWT.NONE, (OLAPRelation) elem);
			sc.pack();
		} else if (elem instanceof VirtualCube) {
			new CompositeVirtualCube(container, (VirtualCube) elem);
			sc.pack();
			container.setBounds(0, 0, 420, 290);
		}

		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		sc.pack();

	}

	public void refresh() {

	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		if (!(selection instanceof IStructuredSelection)) {
			return;
		}

		IStructuredSelection ss = (IStructuredSelection) selection;
		Object object = ss.getFirstElement();

		if (object instanceof TreeDim) {
			TreeDim dim = (TreeDim) object;
			loadData(dim.getOLAPDimension());
		} else if (object instanceof TreeHierarchy) {
			TreeHierarchy elem = (TreeHierarchy) object;
			loadData(elem.getOLAPHierarchy());
		} else if (object instanceof TreeLevel) {
			TreeLevel elem = (TreeLevel) object;
			loadData(elem.getOLAPLevel());
		} else if (object instanceof TreeCube) {
			TreeCube elem = (TreeCube) object;
			loadData(elem.getOLAPCube());
		} else if (object instanceof TreeDim) {
			TreeDim elem = (TreeDim) object;
			loadData(elem.getOLAPDimension());
		} else if (object instanceof TreeMes) {
			TreeMes elem = (TreeMes) object;
			loadData(elem.getOLAPMeasure());
		} else if (object instanceof TreeMes) {
			TreeMes elem = (TreeMes) object;
			loadData(elem.getOLAPMeasure());
		} else if (object instanceof TreeDatabase) {
			TreeDatabase elem = (TreeDatabase) object;
			loadData(elem.getDriver());
		} else if (object instanceof TreeTable) {
			TreeTable elem = (TreeTable) object;
			loadData(elem.getTable());
		} else if (object instanceof TreeConnection) {
			TreeConnection elem = (TreeConnection) object;
			loadData(elem.getDriver());
		} else if (object instanceof TreeColumn) {
			TreeColumn elem = (TreeColumn) object;
			loadData(elem.getColumn());
		} else if (object instanceof TreeMesGroup) {
			TreeMesGroup elem = (TreeMesGroup) object;
			loadData(elem.getOLAPMeasureGroup());
		} else if (object instanceof TreeDimGroup) {
			TreeDimGroup elem = (TreeDimGroup) object;
			loadData(elem.getOLAPDimensionGroup());
		} else if (object instanceof TreeGroup) {
			TreeGroup elem = (TreeGroup) object;
			loadData(elem.getSecurityGroup());
		} else if (object instanceof TreeView) {
			TreeView elem = (TreeView) object;
			loadData(elem.getView());
		} else if (object instanceof TreeDimView) {
			TreeDimView elem = (TreeDimView) object;
			loadData(elem.getSecuDim());
		} else if (object instanceof ServerConnection) {
			loadData((ServerConnection) object);
		} else if (object instanceof SecurityProvider) {
			loadData((SecurityProvider) object);
		} else if (object instanceof TreeRelation) {
			loadData(((TreeRelation) object).getOLAPRelation());
		} else if (object instanceof TreeVirtualCube) {
			loadData(((TreeVirtualCube) object).getVirtualCube());
		} else if (object instanceof TreeVirtualDim) {
			loadData(((TreeVirtualDim) object).getVirtualDimension().getDim());
		} else if (object instanceof TreeVirtualMes) {
			loadData(((TreeVirtualMes) object).getVirtualMeasure().getMes());
		}

	}

	private class MeasureChangeSelectionListener implements SelectionListener {
		private BaseAggregationComposite comp;
		private OLAPMeasure selectedMeasure;

		public MeasureChangeSelectionListener(BaseAggregationComposite comp, OLAPMeasure selectedMeasure) {
			this.comp = comp;
			this.selectedMeasure = selectedMeasure;
		}

		public void widgetSelected(SelectionEvent e) {

			if (comp instanceof IAggregationComposite) {
				OLAPMeasure mes = ((IAggregationComposite) comp).getMeasure();

				if (mes instanceof OlapDynamicMeasure) {
					((OlapDynamicMeasure) selectedMeasure).setAggregations(((OlapDynamicMeasure) mes).getAggregations());
				}

				selectedMeasure.setName(mes.getName());
				selectedMeasure.setLabel(mes.getLabel());
				selectedMeasure.setAggregator(mes.getAggregator());
				selectedMeasure.setDesc(mes.getDesc());
				selectedMeasure.setFormula(mes.getFormula());
				selectedMeasure.setLastTimeDimensionName(mes.getLastTimeDimensionName());
				selectedMeasure.setOrigin(mes.getOrigin());
				selectedMeasure.setType(mes.getType());

				FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMeasures().setChanged();
				for (OLAPCube c : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getCubes()) {
					if (c.getMes().contains(selectedMeasure)) {
						FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
						break;
					}
				}
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}

		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}

}