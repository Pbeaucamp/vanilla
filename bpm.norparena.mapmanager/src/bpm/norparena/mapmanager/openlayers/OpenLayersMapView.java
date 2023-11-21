package bpm.norparena.mapmanager.openlayers;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

public class OpenLayersMapView extends ViewPart {

	public static final String ID = "bpm.norparena.mapmanager.openlayers.OpenLayersMapView"; //$NON-NLS-1$
	
	private FormToolkit formToolkit;
	private ScrolledForm form;
	private OpenLayersObjectBlock block;
	
	@Override
	public void createPartControl(Composite parent) {
		formToolkit = new FormToolkit(parent.getDisplay());
		
		form = formToolkit.createScrolledForm(parent);
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.setLayout(new GridLayout());
		
		
		block = new OpenLayersObjectBlock();
		ManagedForm managedForm = new ManagedForm(formToolkit, form);
		
		block.createContent(managedForm);
	}

	@Override
	public void setFocus() {
		
		
	}

}
