package org.fasd.cubewizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
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

public class PageRelation extends WizardPage {
	private List<OLAPRelation> rels = new ArrayList<OLAPRelation>();
	private Combo leftObject, rightObject;
	private DataObject lO, rO;
	private DataObjectItem lI, rI;
	private HashMap<String, DataObject> map = new HashMap<String, DataObject>();
	private ListViewer leftList, rightList, viewer;
	private Button add;

	protected PageRelation(String pageName) {
		super(pageName);

	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl(mainComposite);
		setPageComplete(true);

	}

	private void createPageContent(Composite parent) {
		parent.setLayout(new GridLayout(2, false));

		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.PageRelation_LeftObj);

		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.PageRelation_RightObj);

		leftObject = new Combo(parent, SWT.BORDER);
		leftObject.setLayoutData(new GridData(GridData.FILL_BOTH));
		leftObject.setItems(map.keySet().toArray(new String[map.size()]));
		leftObject.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				lO = map.get(leftObject.getText());
				List<DataObjectItem> l = lO.getColumns();
				leftList.setInput(l.toArray(new DataObjectItem[l.size()]));
			}

		});

		rightObject = new Combo(parent, SWT.BORDER);
		rightObject.setLayoutData(new GridData(GridData.FILL_BOTH));
		rightObject.setItems(map.keySet().toArray(new String[map.size()]));
		rightObject.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				rO = map.get(rightObject.getText());
				List<DataObjectItem> l = rO.getColumns();
				rightList.setInput(l.toArray(new DataObjectItem[l.size()]));
			}

		});

		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.PageRelation_LeftObjItem);

		Label l4 = new Label(parent, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.PageRelation_RightObjItem);

		leftList = new ListViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		leftList.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		leftList.setLabelProvider(new ListLabelProvider());
		leftList.setContentProvider(new ListContentProvider());

		rightList = new ListViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		rightList.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		rightList.setLabelProvider(new ListLabelProvider());
		rightList.setContentProvider(new ListContentProvider());

		add = new Button(parent, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, true, false, 1, 1));
		add.setText(LanguageText.PageRelation_CreateRelation);
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
				((DataSourceWizard) getWizard()).getRelations().add(r);
				viewer.setInput(rels.toArray(new OLAPRelation[rels.size()]));

			}

		});

		Button del = new Button(parent, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, true, false, 1, 1));
		del.setText(LanguageText.PageRelation_RemoveRelation);
		del.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				if (ss.getFirstElement() instanceof OLAPRelation) {
					OLAPRelation r = (OLAPRelation) (ss.getFirstElement());
					rels.remove(r);
					((DataSourceWizard) getWizard()).getRelations().remove(r);
					viewer.setInput(rels.toArray(new OLAPRelation[rels.size()]));
				}

			}

		});

		viewer = new ListViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setLabelProvider(new ListLabelProvider());
		viewer.setContentProvider(new ListContentProvider());
		viewer.setInput(rels.toArray(new OLAPRelation[rels.size()]));
	}

	/**
	 * This method initializes this
	 * 
	 */
	protected void initialize() {
		DataSource ds = ((DataSourceWizard) getWizard()).getDataSource();
		map.clear();
		for (DataObject d : ds.getDataObjects()) {
			map.put(d.getName(), d);
		}
		leftObject.setItems(map.keySet().toArray(new String[map.size()]));
		rightObject.setItems(map.keySet().toArray(new String[map.size()]));

		List<OLAPRelation> l = ((DataSourceWizard) getWizard()).getRelations();
		rels.clear();
		for (OLAPRelation r : l) {
			rels.add(r);
		}
		viewer.setInput(rels.toArray(new OLAPRelation[rels.size()]));
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
