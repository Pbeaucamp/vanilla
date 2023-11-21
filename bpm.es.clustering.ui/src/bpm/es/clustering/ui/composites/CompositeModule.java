package bpm.es.clustering.ui.composites;

import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.es.clustering.ui.Messages;
import bpm.es.clustering.ui.model.VanillaPlatformModule;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.ReportingComponent;

public class CompositeModule extends Composite {

	private Text url;
	private Text name;
	private Button connect;
	private TableViewer embededServices;

	private VanillaPlatformModule module;

	public CompositeModule(Composite parent, int style) {
		super(parent, style);

		buildContent();
	}

	private void buildContent() {
		setLayout(new GridLayout(2, false));

		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeModule_0);

		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeModule_1);

		url = new Text(this, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		url.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				connect.setEnabled(url.getText().trim().length() > 0);

			}

		});

		connect = new Button(this, SWT.PUSH);
		connect.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		connect.setText(Messages.CompositeModule_2);
		connect.setEnabled(false);
		connect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// module = new VanillaPlatformModule(url.getText(), 0);
				// module.load();
				embededServices.setInput(module.getRegisteredRuntimeServers());
			}
		});

		embededServices = new TableViewer(this, SWT.BORDER);
		embededServices.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		embededServices.getTable().setHeaderVisible(true);
		embededServices.getTable().setLinesVisible(true);
		embededServices.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List l = (List) inputElement;
				return l.toArray(new Object[l.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});

		TableViewerColumn runtimeType = new TableViewerColumn(embededServices, SWT.LEFT);
		runtimeType.getColumn().setText(Messages.CompositeModule_3);
		runtimeType.getColumn().setWidth(200);
		runtimeType.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if((IVanillaServerManager) element instanceof ReportingComponent) {
					return ServerType.REPORTING.name();
				}
				else if ((IVanillaServerManager) element instanceof GatewayComponent) {
					return ServerType.GATEWAY.name();
				}
				else {
					return ""; //$NON-NLS-1$
				}
			}
		});
	}

	public VanillaPlatformModule getModule() {
		if (module != null) {
			module.setName(name.getText());
		}
		return module;
	}

}
