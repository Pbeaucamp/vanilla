package org.fasd.aggwizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.ICube;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPRelation;
import org.fasd.olap.aggregate.AggLevel;
import org.fasd.olap.aggregate.AggMeasure;
import org.fasd.olap.aggregate.AggregateTable;
import org.freeolap.FreemetricsPlugin;

public class PageCubeAttachment extends WizardPage {

	private ComboViewer viewer, factCol, aggCol;

	protected PageCubeAttachment(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		// create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl(mainComposite);
		setPageComplete(true);

	}

	private void createPageContent(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.PageCubeAttachment_0);

		viewer = new ComboViewer(container, SWT.READ_ONLY);
		viewer.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof ICube) {
					return ((ICube) element).getName();
				} else
					return ""; //$NON-NLS-1$
			}

		});
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<OLAPCube> list = (List<OLAPCube>) inputElement;
				return list.toArray(new OLAPCube[list.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty())
					return;

				DataObject fact = ((OLAPCube) ss.getFirstElement()).getFactDataObject();
				factCol.setInput(fact.getColumns());

			}

		});

		Group g = new Group(container, SWT.NONE);
		g.setLayout(new GridLayout(2, false));
		g.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		g.setText(LanguageText.PageCubeAttachment_2);

		Label l2 = new Label(g, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.PageCubeAttachment_3);

		Label l3 = new Label(g, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.PageCubeAttachment_4);

		factCol = new ComboViewer(g, SWT.READ_ONLY);
		factCol.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		LabelProvider labelProvider = new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((DataObjectItem) element).getName();
			}

		};

		IStructuredContentProvider contentProvider = new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<DataObjectItem> l = (List<DataObjectItem>) inputElement;

				return l.toArray(new DataObjectItem[l.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		};

		factCol.setLabelProvider(labelProvider);
		factCol.setContentProvider(contentProvider);

		aggCol = new ComboViewer(g, SWT.READ_ONLY);
		aggCol.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		aggCol.setLabelProvider(labelProvider);
		aggCol.setContentProvider(contentProvider);

	}

	protected void createAggStructure() {
		AggregateTable aggTable = new AggregateTable();

		PageDefinition defPage = (PageDefinition) ((WizardAggregate) getWizard()).getPage("Definition"); //$NON-NLS-1$
		OLAPCube cube = (OLAPCube) ((IStructuredSelection) viewer.getSelection()).getFirstElement();

		// create mesureAgg objects
		List<AggMeasure> aggMes = new ArrayList<AggMeasure>();
		List<OLAPMeasure> mesList = defPage.getSelectedMeasures();

		for (OLAPMeasure m : mesList) {
			if (cube.getMes().contains(m)) {
				AggMeasure aM = new AggMeasure();
				aM.setColumn(defPage.getAggTable().findItemNamed(m.getOrigin().getName()));
				aM.setMes(m);
				aggMes.add(aM);
			}
		}

		// create levelAgg objects
		List<OLAPLevel> lvlList = defPage.getSelectedLevels();
		List<AggLevel> aggLvl = new ArrayList<AggLevel>();

		for (OLAPLevel l : lvlList) {
			boolean cubeContainLevel = false;

			for (OLAPDimension d : cube.getDims()) {
				for (OLAPHierarchy h : d.getHierarchies()) {
					if (h.getLevels().contains(l)) {
						cubeContainLevel = true;
						break;
					}
				}
			}

			if (cubeContainLevel) {
				AggLevel aL = new AggLevel();
				aL.setColumn(defPage.getAggTable().findItemNamed(l.getItem().getName()));
				aL.setLvl(l);
				aggLvl.add(aL);
			}
		}
		// create factCountAgg object
		DataObjectItem rowCountCol = defPage.getRowCountCol();

		// create FKey agg object

		// add the aggMes
		for (AggMeasure m : aggMes) {
			aggTable.addAggMeasure(m);
		}

		// add the aggLvl
		for (AggLevel l : aggLvl) {
			aggTable.addAggLevel(l);
		}

		// set the rowcountcol
		aggTable.setFactCountColumn(rowCountCol);

		OLAPRelation relation = new OLAPRelation();

		IStructuredSelection ss = (IStructuredSelection) factCol.getSelection();
		if (ss.isEmpty()) {
			return;
		}

		relation.setLeftObjectItem((DataObjectItem) ss.getFirstElement());
		ss = (IStructuredSelection) aggCol.getSelection();
		if (ss.isEmpty()) {
			return;
		}
		relation.setRightObjectItem((DataObjectItem) ss.getFirstElement());
		((WizardAggregate) getWizard()).foreignKeys = relation;

		((WizardAggregate) getWizard()).aggTable = aggTable;
		aggTable.setTable(defPage.getAggTable());
		cube.addAggTable(aggTable);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().addAggregate(aggTable);
	}

	protected void fillCombos() {
		// fillCombos
		viewer.setInput(FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getCubes());
		PageDefinition defPage = (PageDefinition) ((WizardAggregate) getWizard()).getPage("Definition"); //$NON-NLS-1$
		DataObject table = defPage.getAggTable();
		aggCol.setInput(table.getColumns());
	}

}
