package org.fasd.views.composites;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPDimension;
import org.fasd.security.SecurityDim;
import org.fasd.views.dialogs.DialogSelectDimension;
import org.freeolap.FreemetricsPlugin;

public class CompositeDimView {
	private Text name, desc, dimension;
	private Button ok, cancel;
	private SecurityDim secuDim;
	private OLAPDimension dim;

	public CompositeDimView(final Composite parent, SecurityDim dimView) {

		final Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3, false));

		Label lbl = new Label(container, SWT.NONE);
		lbl.setLayoutData(new GridData());
		lbl.setText(LanguageText.CompositeDimView_0);

		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

		Label lb2 = new Label(container, SWT.NONE);
		lb2.setLayoutData(new GridData());
		lb2.setText(LanguageText.CompositeDimView_1);

		desc = new Text(container, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

		Label lb3 = new Label(container, SWT.NONE);
		lb3.setLayoutData(new GridData());
		lb3.setText(LanguageText.CompositeDimView_2);

		dimension = new Text(container, SWT.BORDER);
		dimension.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));

		Button b = new Button(container, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.FILL, false, false, 1, 1));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogSelectDimension dial = new DialogSelectDimension(container.getShell(), null);
				if (dial.open() == Dialog.OK) {
					dim = dial.getDim();
					dimension.setText(dim.getName());
				}
			}

		});

		ok = new Button(container, SWT.PUSH);
		ok.setText(LanguageText.CompositeDimView_4);
		ok.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				secuDim.setName(name.getText());
				secuDim.setDesc(desc.getText());
				secuDim.setDim(dim);
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		});

		cancel = new Button(container, SWT.PUSH);
		cancel.setText(LanguageText.CompositeDimView_5);
		cancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}
		});

		this.secuDim = dimView;

		fillData();
	}

	public void fillData() {
		name.setText(secuDim.getName());
		desc.setText(secuDim.getDesc());
		if (secuDim.getDim() != null)
			dimension.setText(secuDim.getDim().getName());
	}
}
