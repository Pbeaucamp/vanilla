package org.fasd.views.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPHierarchy;
import org.freeolap.FreemetricsPlugin;

public class CompositeHierarchy {

	private Text name, allMember, desc;
	private OLAPHierarchy hierarchy;

	public CompositeHierarchy(final Composite parent, OLAPHierarchy hiera, IViewSite s) {
		parent.setLayout(new GridLayout(3, true));

		Label lb1 = new Label(parent, SWT.NONE);
		lb1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lb1.setText(LanguageText.CompositeHierarchy_0);

		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		name.setText(LanguageText.CompositeHierarchy_1);
		name.selectAll();

		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(LanguageText.CompositeHierarchy_2);

		desc = new Text(parent, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(LanguageText.CompositeHierarchy_3);

		allMember = new Text(parent, SWT.BORDER);
		allMember.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		allMember.setText(LanguageText.CompositeHierarchy_4);

		Button ok = new Button(parent, SWT.PUSH);
		ok.setText(LanguageText.CompositeHierarchy_5);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				hierarchy.setAllMember(allMember.getText());
				hierarchy.setDesc(desc.getText());
				if (!hierarchy.getName().equals(name.getText())) {
					hierarchy.setName(name.getText());
					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
				}
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		});

		Button cancel = new Button(parent, SWT.PUSH);
		cancel.setText(LanguageText.CompositeHierarchy_6);
		cancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}
		});

		this.hierarchy = hiera;

		fillData();

	}

	public void fillData() {
		name.setText(hierarchy.getName());
		desc.setText(hierarchy.getDesc());
		allMember.setText(hierarchy.getAllMember());
	}
}
