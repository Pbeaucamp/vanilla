package org.fasd.views.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPDimensionGroup;
import org.fasd.olap.OLAPGroup;
import org.fasd.olap.OLAPMeasureGroup;
import org.freeolap.FreemetricsPlugin;

public class CompositeGroup {
	private Text name, desc, level, parent;
	private Button ok, cancel;
	private OLAPGroup gr;

	public CompositeGroup(Composite container, OLAPGroup g, IViewSite s) {

		Label lbl = new Label(container, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.NONE));
		lbl.setText(LanguageText.CompositeGroup_0);

		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lb2 = new Label(container, SWT.NONE);
		lb2.setLayoutData(new GridData(SWT.NONE));
		lb2.setText(LanguageText.CompositeGroup_1);

		desc = new Text(container, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lb3 = new Label(container, SWT.NONE);
		lb3.setLayoutData(new GridData(SWT.NONE));
		lb3.setText(LanguageText.CompositeGroup_2);

		parent = new Text(container, SWT.BORDER);
		parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		parent.setEnabled(false);

		Label lb4 = new Label(container, SWT.NONE);
		lb4.setLayoutData(new GridData(SWT.NONE));
		lb4.setText(LanguageText.CompositeGroup_3);

		level = new Text(container, SWT.BORDER);
		level.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		ok = new Button(container, SWT.PUSH);
		ok.setText(LanguageText.CompositeGroup_4);
		ok.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				gr.setName(name.getText());
				gr.setDesc(desc.getText());
				gr.setLevel(level.getText());
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
				if (gr instanceof OLAPDimensionGroup) {
					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
				} else if (gr instanceof OLAPMeasureGroup) {
					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMeasures().setChanged();
				}
			}
		});

		cancel = new Button(container, SWT.PUSH);
		cancel.setText(LanguageText.CompositeGroup_5);
		cancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}
		});

		this.gr = g;

		fillData();
	}

	public void fillData() {
		name.setText(gr.getName());
		desc.setText(gr.getDesc());
		level.setText(String.valueOf(gr.getLevel()));
		if (gr.getParent() == null)
			parent.setText(""); //$NON-NLS-1$
		else
			parent.setText(gr.getParent().getName());
	}
}
