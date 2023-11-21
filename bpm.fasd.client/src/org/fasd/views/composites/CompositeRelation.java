package org.fasd.views.composites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPRelation;
import org.freeolap.FreemetricsPlugin;

public class CompositeRelation extends Composite {
	private List<OLAPRelation> rels = new ArrayList<OLAPRelation>();
	private Combo leftObject, rightObject, leftSource, rightSource;
	private DataSource lS, rS;
	private DataObject lO, rO;
	private DataObjectItem lI, rI;
	private HashMap<String, DataSource> map = new HashMap<String, DataSource>();
	private HashMap<String, DataObject> mapleft = new HashMap<String, DataObject>();
	private HashMap<String, DataObject> mapright = new HashMap<String, DataObject>();
	private ListViewer leftList, rightList, viewer;
	private Button add;

	public CompositeRelation(Composite parent, int style) {
		super(parent, style);
		initialize();

		this.setLayout(new GridLayout(2, false));

		Label lS = new Label(this, SWT.NONE);
		lS.setLayoutData(new GridData());
		lS.setText(LanguageText.CompositeRelation_0);

		Label rS = new Label(this, SWT.NONE);
		rS.setLayoutData(new GridData());
		rS.setText(LanguageText.CompositeRelation_1);

		leftSource = new Combo(this, SWT.BORDER);
		leftSource.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		leftSource.setItems(map.keySet().toArray(new String[map.size()]));
		leftSource.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				CompositeRelation.this.lS = map.get(leftSource.getText());
				
				mapleft.clear();
				
				for (DataObject o : CompositeRelation.this.lS.getDataObjects()) {
					mapleft.put(o.getPhysicalName(), o);
				}

				leftObject.setItems(mapleft.keySet().toArray(new String[mapleft.size()]));
			}

		});

		rightSource = new Combo(this, SWT.BORDER);
		rightSource.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		rightSource.setItems(map.keySet().toArray(new String[map.size()]));
		rightSource.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				CompositeRelation.this.rS = map.get(rightSource.getText());
				
				mapright.clear();
				
				for (DataObject o : CompositeRelation.this.rS.getDataObjects()) {
					mapright.put(o.getPhysicalName(), o);
				}

				rightObject.setItems(mapright.keySet().toArray(new String[mapright.size()]));

			}

		});

		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.CompositeRelation_2);

		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.CompositeRelation_3);

		leftObject = new Combo(this, SWT.BORDER);
		leftObject.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		leftObject.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				lO = mapleft.get(leftObject.getText());
				List<DataObjectItem> l = lO.getColumns();
				leftList.setInput(l.toArray(new DataObjectItem[l.size()]));
			}

		});

		rightObject = new Combo(this, SWT.BORDER);
		rightObject.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		rightObject.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				rO = mapright.get(rightObject.getText());
				List<DataObjectItem> l = rO.getColumns();
				rightList.setInput(l.toArray(new DataObjectItem[l.size()]));
			}

		});

		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.CompositeRelation_4);

		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.CompositeRelation_5);

		leftList = new ListViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		leftList.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		leftList.setLabelProvider(new ListLabelProvider());
		leftList.setContentProvider(new ListContentProvider());

		rightList = new ListViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		rightList.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		rightList.setLabelProvider(new ListLabelProvider());
		rightList.setContentProvider(new ListContentProvider());

		add = new Button(this, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.END, GridData.FILL, true, false, 1, 1));
		add.setText(LanguageText.CompositeRelation_6);
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (leftList.getSelection().isEmpty() || rightList.getSelection().isEmpty()) {
					return;
				}
				lI = (DataObjectItem) ((StructuredSelection) leftList.getSelection()).getFirstElement();
				rI = (DataObjectItem) ((StructuredSelection) rightList.getSelection()).getFirstElement();

				OLAPRelation r = new OLAPRelation();
				r.setLeftObjectItem(lI);
				r.setRightObjectItem(rI);

				rels.add(r);
				viewer.setInput(rels.toArray(new OLAPRelation[rels.size()]));
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}

		});

		Button del = new Button(this, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, true, false, 1, 1));
		del.setText(LanguageText.CompositeRelation_7);
		del.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				if (ss.getFirstElement() instanceof OLAPRelation) {
					OLAPRelation r = (OLAPRelation) (ss.getFirstElement());
					rels.remove(r);
					viewer.setInput(rels.toArray(new OLAPRelation[rels.size()]));
				}

			}

		});

		viewer = new ListViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setLabelProvider(new ListLabelProvider());
		viewer.setContentProvider(new ListContentProvider());
		viewer.setInput(rels.toArray(new OLAPRelation[rels.size()]));
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setLayout(new GridLayout());
		for (DataSource ds : FreemetricsPlugin.getDefault().getFAModel().getDataSources()) {
			map.put(ds.getDSName(), ds);

		}

		for (OLAPRelation r : FreemetricsPlugin.getDefault().getFAModel().getRelations()) {
			rels.add(r);
		}
	}

	public List<OLAPRelation> getRelations() {
		return rels;
	}

	public class ListLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			if (element instanceof DataObjectItem) {
				return ((DataObjectItem) element).getName();
			}
			if (element instanceof OLAPRelation) {
				return ((OLAPRelation) element).getName();
			}
			return null;
		}

	}

	public class ListContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof DataObjectItem[]) {
				return (DataObjectItem[]) inputElement;
			}
			if (inputElement instanceof OLAPRelation[]) {
				return (OLAPRelation[]) inputElement;
			}
			return null;
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

	}

}
