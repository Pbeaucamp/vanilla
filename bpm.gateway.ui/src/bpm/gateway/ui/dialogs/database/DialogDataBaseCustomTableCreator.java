package bpm.gateway.ui.dialogs.database;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import bpm.gateway.core.ITargetableStream;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.ScrolledComposite;

public class DialogDataBaseCustomTableCreator extends Dialog {

	private ITargetableStream transformation;
	private Text txtTableName;
	private Button btnAdd;
	private Composite row;
	private ScrolledComposite scrolledComposite;
	private DataBaseConnection dbc;
	private Display display;
	private Label lblColumnNames, lblType, lblPrecision, lblPK, lblNN, lblAI;

	public DialogDataBaseCustomTableCreator(Shell parentShell, ITargetableStream transformation) {
		super(parentShell);
		this.transformation = transformation;

		display = Display.getCurrent();
		dbc = (DataBaseConnection) transformation.getServer().getConnections().get(0);
		setShellStyle(SWT.RESIZE | SWT.CLOSE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lblNomTable = new Label(container, SWT.NONE);
		lblNomTable.setText(Messages.DBCreationDialog_0);

		txtTableName = new Text(container, SWT.BORDER);
		txtTableName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite compLabel = new Composite(container, SWT.NONE);
		compLabel.setLayout(new GridLayout(7, false));
		GridData gd_compLabel = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_compLabel.minimumHeight = 30;
		compLabel.setLayoutData(gd_compLabel);
		compLabel.setSize(500, 30);

		lblColumnNames = new Label(compLabel, SWT.NONE);
		GridData gd_lblColumnNames = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblColumnNames.widthHint = 155;
		lblColumnNames.setLayoutData(gd_lblColumnNames);
		lblColumnNames.setText(Messages.DBCreationDialog_1);

		lblType = new Label(compLabel, SWT.NONE);
		GridData gd_lblType = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblType.widthHint = 165;
		lblType.setLayoutData(gd_lblType);
		lblType.setText(Messages.DBCreationDialog_2);

		lblPrecision = new Label(compLabel, SWT.NONE);
		GridData gd_lblPrecision = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblPrecision.widthHint = 75;
		lblPrecision.setLayoutData(gd_lblPrecision);
		lblPrecision.setText(Messages.DBCreationDialog_3);

		lblPK = new Label(compLabel, SWT.NONE);
		GridData gd_lblPK = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblPK.widthHint = 20;
		lblPK.setLayoutData(gd_lblPK);
		lblPK.setText(Messages.DBCreationDialog_4);

		lblNN = new Label(compLabel, SWT.NONE);
		GridData gd_lblNN = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNN.widthHint = 20;
		lblNN.setLayoutData(gd_lblNN);
		lblNN.setText(Messages.DBCreationDialog_5);

		lblAI = new Label(compLabel, SWT.NONE);
		GridData gd_lblAI = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblAI.widthHint = 20;
		lblAI.setLayoutData(gd_lblAI);
		lblAI.setText(Messages.DBCreationDialog_6);

		Label lbl = new Label(compLabel, SWT.NONE);
		GridData gd_lbl = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl.widthHint = 30;
		lbl.setLayoutData(gd_lbl);

		scrolledComposite = new ScrolledComposite(container, SWT.V_SCROLL | SWT.BORDER);
		scrolledComposite.setLayout(new GridLayout());
		scrolledComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinSize(new Point(550, 500));

		row = new Composite(scrolledComposite, SWT.NONE);
		row.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		row.setLayout(new GridLayout(1, false));

		scrolledComposite.setContent(row);

		scrolledComposite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {

				scrolledComposite.layout();
			}
		});

		btnAdd = new Button(container, SWT.PUSH);
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		btnAdd.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.add_16));
		btnAdd.setText(Messages.DBCreationDialog_7);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);

				addColumn();

				scrolledComposite.setMinSize(row.computeSize(row.getSize().x, row.getSize().y + 45));
				scrolledComposite.layout();
			}
		});

		try {
			fillDatas();
		} catch(Exception e1) {
			e1.printStackTrace();
		}

		return parent;

	}

	private void fillDatas() throws Exception {
		if(!transformation.getInputs().isEmpty()) {
			StreamDescriptor desc = transformation.getInputs().get(0).getDescriptor(transformation);

			for(StreamElement e : desc.getStreamElements()) {
				new DataBaseColumns(row, SWT.NONE, e.name, e.className, dbc.getDriverName());
			}
		}

		scrolledComposite.setMinSize(row.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.layout();
	}
	
	public void addColumn() {
		new DataBaseColumns(row, SWT.NONE, "", "", dbc.getDriverName()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String editSQLArea(Composite row) {
		String sqlQuery = "CREATE TABLE " + txtTableName.getText() + " (\n"; //$NON-NLS-1$ //$NON-NLS-2$

		int colomnNbr = row.getChildren().length;
		int i = 1;
		for(Control c : row.getChildren()) {
			if(!c.isDisposed()) {
				if(i < colomnNbr) {
					sqlQuery = sqlQuery + ((DataBaseColumns) c).getSql() + ",\n"; //$NON-NLS-1$
				}
				else {
					sqlQuery = sqlQuery + ((DataBaseColumns) c).getSql();
				}
				i++;
			}
		}
		sqlQuery = sqlQuery + ");"; //$NON-NLS-1$
		return sqlQuery;
	}

	@Override
	protected void okPressed() {
		DialogSqlScript dial = new DialogSqlScript(getShell(), editSQLArea(row), this.transformation);
		dial.open();
	}

	public String getTableName() {
		return txtTableName.getText();

	}

	public String getSelectStatement() {
		return editSQLArea(row);

	}
}
