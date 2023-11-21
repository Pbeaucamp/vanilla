package org.fasd.cubewizard;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataSource;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.FAModel;
import org.freeolap.FreemetricsPlugin;

public class PageDimension extends WizardPage {

	private Text txt;
	private Combo tables;

	protected PageDimension(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		// create main composite & set layout
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
		final Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		Label lbl = new Label(container, SWT.NONE);
		lbl.setText(LanguageText.PageDimension_DimensionName);

		txt = new Text(container, SWT.NONE);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button bt = new Button(container, SWT.PUSH);
		bt.setText(LanguageText.PageDimension_NewHierarchy);
		bt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Composite hierarchyComposite = new Composite(container, SWT.NONE);
				hierarchyComposite.setLayout(new GridLayout());
				Label lb = new Label(hierarchyComposite, SWT.NONE);
				lb.setText(LanguageText.PageDimension_HierarchyName);
				Text txt = new Text(container, SWT.FILL);
				txt.setLayoutData(new GridData(SWT.FILL));

				Label lb1 = new Label(hierarchyComposite, SWT.NONE);
				lb1.setText(LanguageText.PageDimension_SelectHierarchyTable);
				tables = new Combo(hierarchyComposite, SWT.FILL);
				tables.setLayoutData(new GridData(SWT.FILL));
				FAModel model = FreemetricsPlugin.getDefault().getFAModel();
				List<DataSource> dList = model.getDataSources();
				ArrayList<DataObject> tList = new ArrayList<DataObject>();

				for (DataSource ds : dList)
					tList.addAll(ds.getDataObjects());

				String[] items = new String[tList.size()];

				for (int i = 0; i < tList.size(); i++) {
					items[i] = tList.get(i).getName();
				}
				tables.setItems(items);

				container.pack();

			}

		});

	}

}
