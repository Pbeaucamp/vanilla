package bpm.datasource.vanilla.oda.ui.impl;

import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DatasetWizardPage extends DataSetWizardPage {
	
	private static String DEFAULT_MESSAGE = "Define the query text for the data set";

	public DatasetWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setMessage(DEFAULT_MESSAGE);
	}

	public DatasetWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setMessage(DEFAULT_MESSAGE);
	}

	public void createPageCustomControl(Composite parent) {
		Composite inCompo = new Composite(parent, SWT.NONE);
		inCompo.setLayout(new GridLayout(1, false));
		setControl(createPageControl(inCompo));
	}

	private Control createPageControl(Composite parent) {
		setMessage("Nothing to do here.");
		setPageComplete(false);

		return parent;
	}

	protected DataSetDesign collectDataSetDesign(DataSetDesign design) {
		if(getControl() == null) // page control was never created
			return design; // no editing was done
		if(!hasValidData())
			return null; // to trigger a design session error status
		return design;
	}

	protected void collectResponseState() {
		super.collectResponseState();
	}

	protected boolean canLeave() {
		return isPageComplete();
	}

	private boolean hasValidData() {
		return canLeave();
	}
}
