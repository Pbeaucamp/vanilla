package bpm.es.gedmanager.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import bpm.es.gedmanager.Activator;
import bpm.es.gedmanager.Messages;
import bpm.es.gedmanager.api.GedModel;
import bpm.es.gedmanager.icons.IconsNames;
import bpm.es.gedmanager.views.composites.GedComposite;

public class GeneralView extends ViewPart {
	public static final String ID = "bpm.es.gedmanager.views.generalview"; //$NON-NLS-1$

	private FormToolkit toolkit;
	private ScrolledForm form;

	private Section sectionManager;

	private GedModel model;

	public GeneralView() {}

	public void createPartControl(Composite parent) {
		model = new GedModel();

		toolkit = new FormToolkit(parent.getDisplay());

		form = toolkit.createScrolledForm(parent);
		form.setText(Messages.GeneralView_1);
		form.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ged32));
		form.getBody().setLayout(new GridLayout());

		Composite panelTop = toolkit.createComposite(form.getBody(), SWT.NONE);
		panelTop.setLayout(new GridLayout());
		panelTop.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		createPanelManager(panelTop);
	}

	private void createPanelManager(Composite parent) {
		sectionManager = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		sectionManager.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		sectionManager.setText(Messages.GeneralView_2);

		Composite panel = toolkit.createComposite(sectionManager);
		panel.setLayout(new GridLayout());
		panel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Group docPanel = new Group(panel, SWT.NONE);
		docPanel.setText(Messages.GeneralView_3);
		docPanel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		docPanel.setLayout(new GridLayout());
		docPanel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		new GedComposite(docPanel, model);

		Composite splitPanel = toolkit.createComposite(panel);
		splitPanel.setLayout(new GridLayout(2, false));
		splitPanel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		sectionManager.setClient(panel);
	}

	@Override
	public void setFocus() {}

}
