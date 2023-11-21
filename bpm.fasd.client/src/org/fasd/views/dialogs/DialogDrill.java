package org.fasd.views.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.Drill;
import org.fasd.olap.OLAPCube;

public class DialogDrill extends Dialog {

	private OLAPCube cube;

	private Text url;
	private ListViewer lv;
	private Drill drill;

	protected void initializeBounds() {
		getShell().setText(LanguageText.DialogDrill_0);
		getShell().setSize(400, 300);
	}

	public DialogDrill(Shell parentShell, OLAPCube cube) {
		super(parentShell);
		this.cube = cube;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));

		Label l0 = new Label(c, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l0.setText(LanguageText.DialogDrill_1);

		url = new Text(c, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Button add = new Button(c, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		add.setText(LanguageText.DialogDrill_2);
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogDrillCube d = new DialogDrillCube(getShell(), cube);

			}

		});

		Button remove = new Button(c, SWT.PUSH);
		remove.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		remove.setText(LanguageText.DialogDrill_3);

		lv = new ListViewer(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		lv.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		lv.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<Drill> l = (List<Drill>) inputElement;

				return l.toArray(new Drill[l.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		lv.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				DrillElement d = (DrillElement) element;
				String s = d.name + "-->" + d.lvl.getName(); //$NON-NLS-1$
				return s;
			}

		});

		lv.setInput(new ArrayList<Drill>());

		return c;
	}

	@Override
	protected void okPressed() {

		super.okPressed();
	}

	public Drill getDrill() {
		return drill;
	}

}
