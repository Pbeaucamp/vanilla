package bpm.gateway.ui.views.property.sections.nosql;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.nosql.SparkTransformation;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class SparkSection extends AbstractPropertySection {

	private Text txtClass;
	private Text txtName;
	private Text txtJar;
	private SparkTransformation transfo;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		Label l = getWidgetFactory().createLabel(composite, Messages.SparkSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtName.addModifyListener(listener);

		Label l3 = getWidgetFactory().createLabel(composite, Messages.SparkSection_1);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtJar = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtJar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtJar.addModifyListener(listener);

		Label l4 = getWidgetFactory().createLabel(composite, Messages.SparkSection_2);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtClass = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtClass.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtClass.addModifyListener(listener);
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.transfo = (SparkTransformation) ((Node) ((NodePart) input).getModel()).getGatewayModel();
	}

	@Override
	public void refresh() {
		txtName.removeModifyListener(listener);
		txtName.setText(transfo.getJobName() != null ? transfo.getJobName() : ""); //$NON-NLS-1$
		txtName.addModifyListener(listener);

		txtClass.removeModifyListener(listener);
		txtClass.setText(transfo.getClassName() != null ? transfo.getClassName() : ""); //$NON-NLS-1$
		txtClass.addModifyListener(listener);

		txtJar.removeModifyListener(listener);
		txtJar.setText(transfo.getJarPath() != null ? transfo.getJarPath() : ""); //$NON-NLS-1$
		txtJar.addModifyListener(listener);
	}
	
	private ModifyListener listener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			if(e.widget == txtClass) {
				transfo.setClassName(txtClass.getText());
			}
			else if(e.widget == txtJar) {
				transfo.setJarPath(txtJar.getText());
			}
			else if(e.widget == txtName) {
				transfo.setName(txtName.getText());
			}
		}
	};
	
}
