package bpm.gateway.ui.views.resources.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.server.database.nosql.cassandra.CassandraConnection;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.resource.composite.connection.CassandraConnectionComposite;

public class CassandraConnectionSection extends AbstractPropertySection {

	private CassandraConnection dataBaseConnection;
	
	private CassandraConnectionComposite compositeConnection;
	private Button apply, cancel;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, true));
		
		compositeConnection = new CassandraConnectionComposite(composite, SWT.NONE, null);
		compositeConnection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		apply = new Button(composite, SWT.PUSH);
		apply.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		apply.setText(Messages.DataBaseSection_0);
		apply.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				dataBaseConnection = compositeConnection.getConnection();
			}
		});
		
		cancel = new Button(composite, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.setText(Messages.DataBaseSection_3);
		cancel.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});
		
	}
	
	@Override
	public void refresh() {
		compositeConnection.fillData(dataBaseConnection);
		super.refresh();
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof CassandraConnection);
        this.dataBaseConnection = ((CassandraConnection) input);
	}
	
}
