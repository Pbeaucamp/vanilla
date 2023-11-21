package org.fasd.views.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.fasd.cubewizard.DialogSelectRepositoryItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.DrillCube;
import org.fasd.olap.OLAPCube;
import org.freeolap.FreemetricsPlugin;

import bpm.united.olap.api.BadFasdSchemaModelTypeException;
import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogDrillCube extends Dialog {

	private OLAPCube cube;
	private Text txtName;
	private Text txtSelectedFasd;

	private Button btnBrowseFasd;

	private ComboViewer cbCubes;
	private RepositoryItem selectedFasd;

	@Override
	protected void initializeBounds() {
		getShell().setText(LanguageText.DialogDrillElement_0);
	}

	public DialogDrillCube(Shell parentShell, OLAPCube cube) {
		super(parentShell);
		this.cube = cube;
	}

	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(3, false));
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblName = new Label(c, SWT.NONE);
		lblName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblName.setText(LanguageText.DialogDrillCube_0);

		txtName = new Text(c, SWT.BORDER);
		txtName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label lblFasd = new Label(c, SWT.NONE);
		lblFasd.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		lblFasd.setText(LanguageText.DialogDrillCube_1);

		txtSelectedFasd = new Text(c, SWT.BORDER);
		txtSelectedFasd.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtSelectedFasd.setEnabled(false);

		btnBrowseFasd = new Button(c, SWT.PUSH);
		btnBrowseFasd.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		btnBrowseFasd.setText("..."); //$NON-NLS-1$
		btnBrowseFasd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogSelectRepositoryItem dial = new DialogSelectRepositoryItem(getParentShell(), IRepositoryApi.FASD_TYPE, -1);
				if (dial.open() == Dialog.OK) {
					selectedFasd = dial.getItem();
					txtSelectedFasd.setText(selectedFasd.getItemName());

					// set cube input

					IRepositoryContext ctx = FreemetricsPlugin.getDefault().getRepositoryContext();
					IObjectIdentifier identifier = new ObjectIdentifier(ctx.getRepository().getId(), selectedFasd.getId());

					IRuntimeContext runtimeContext = new RuntimeContext(ctx.getVanillaContext().getLogin(), ctx.getVanillaContext().getPassword(), ctx.getGroup().getName(), ctx.getGroup().getId());

					try {
						String schemaId = FreemetricsPlugin.getDefault().getModelService().loadSchema(identifier, runtimeContext);
						Schema schema = FreemetricsPlugin.getDefault().getModelService().getSchema(schemaId);

						cbCubes.setInput(schema.getCubes());

					} catch (BadFasdSchemaModelTypeException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		Label lblCube = new Label(c, SWT.NONE);
		lblCube.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		lblCube.setText(LanguageText.DialogDrillCube_3);

		cbCubes = new ComboViewer(c, SWT.READ_ONLY);
		cbCubes.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		cbCubes.setContentProvider(new IStructuredContentProvider() {
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			@Override
			public void dispose() {

			}

			@Override
			public Object[] getElements(Object inputElement) {
				return ((List<Cube>) inputElement).toArray();
			}
		});
		cbCubes.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Cube) element).getName();
			}
		});

		return c;
	}

	@Override
	protected void okPressed() {
		DrillCube d = new DrillCube();
		d.setCubeName(((Cube) ((IStructuredSelection) cbCubes.getSelection()).getFirstElement()).getName());
		d.setFasdId(selectedFasd.getId());
		d.setDrillName(txtName.getText());
		cube.addDrill(d);

		super.okPressed();
	}

}
