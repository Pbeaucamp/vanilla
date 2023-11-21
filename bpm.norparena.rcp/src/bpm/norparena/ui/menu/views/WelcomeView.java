package bpm.norparena.ui.menu.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import bpm.norparena.ui.menu.Activator;
import bpm.norparena.ui.menu.Messages;

public class WelcomeView extends ViewPart {
private static final Font descriptionFont = new Font(Display.getDefault(), "Arial", 8, SWT.ITALIC); //$NON-NLS-1$
private static final Font menuFont = new Font(Display.getDefault(), "Arial", 10, SWT.BOLD);; //$NON-NLS-1$
	private FormToolkit toolkit;
	private ScrolledForm form;

	private Section toolsSection;
	private static final String[] tools = new String[]{
		"bpm.es.gedmanager.perspective.gedmanager", //$NON-NLS-1$
		"bpm.es.gedmanager.perspective.historicmanager", //$NON-NLS-1$
		"bpm.es.external.disconnected.perspective.DisconnectedPerspective", //$NON-NLS-1$
		"bpm.norparena.alertmanager.perspectives.AlertManagerPerspective", //$NON-NLS-1$
		"bpm.norparena.mapmanager.perspectives.MapManagerPerspective" //$NON-NLS-1$
	};
	private Section otherSection;
	
	public WelcomeView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText(Messages.WelcomeView_7);
		form.getBody().setLayout(new GridLayout());

		Composite pannelLeft = toolkit.createComposite(form.getBody(), SWT.NONE);
		pannelLeft.setLayout(new GridLayout());
		pannelLeft.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
//		Composite pannelRight = toolkit.createComposite(form.getBody(), SWT.NONE);
//		pannelRight.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
//		pannelRight.setLayout(new GridLayout());
		
//		createToolsSection(pannelLeft);
		createOtherSection(pannelLeft);
	}
	
	private void createOtherSection(Composite parent){
		otherSection = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR| Section.TWISTIE|Section.EXPANDED);
		otherSection.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		otherSection.setText(Messages.WelcomeView_8);
		otherSection.setDescription(Messages.WelcomeView_9);
		
		Composite main = toolkit.createComposite(otherSection);
		main.setLayout(new GridLayout());
		
		
		for(IPerspectiveDescriptor pd : Activator.getDefault().getWorkbench().getPerspectiveRegistry().getPerspectives()){

			boolean found = false;
//			for(String s : tools){
//				if (s.equals(pd.getId())){
//					found = true;
//					break;
//				}
//			}
			
			if (!found && !pd.getId().equals("bpm.norparena.ui.menu.WelcomePerspective")){ //$NON-NLS-1$
				createButton(pd.getId(), main);
			}
		}
		
		
		
		otherSection.setClient(main);

	}
	
	private void createToolsSection(Composite parent){
		toolsSection = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR| Section.TWISTIE|Section.EXPANDED);
		toolsSection.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		toolsSection.setText(Messages.WelcomeView_11);
		toolsSection.setDescription(Messages.WelcomeView_12);
		
		Composite main = toolkit.createComposite(toolsSection);
		main.setLayout(new GridLayout());
		
		for(String s : tools){
			createButton(s, main);
		}
		
		
		toolsSection.setClient(main);

	}
	
	private void createButton(String persId, Composite parent){
		final IPerspectiveDescriptor pd = getPerspectiveDescriptor(persId);
		if (pd == null){
			return;
		}

		Composite c = toolkit.createComposite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		
		final Button b = toolkit.createButton(c, "", SWT.PUSH | SWT.FLAT); //$NON-NLS-1$
		b.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 2));
		b.setImage(pd.getImageDescriptor().createImage());
		b.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);
			}
		});
		
		Label l = toolkit.createLabel(c, pd.getLabel(), SWT.NONE);
		l.setFont(menuFont);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		l = toolkit.createLabel(c, pd.getDescription(), SWT.WRAP);
		l.setFont(descriptionFont);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
	}
	
	
	private IPerspectiveDescriptor getPerspectiveDescriptor(String id){
		IPerspectiveRegistry reg = Activator.getDefault().getWorkbench().getPerspectiveRegistry();
		for(IPerspectiveDescriptor pd : reg.getPerspectives()){
			if (pd.getId().equals(id)){
				return pd;
			}
		}
		return null;
	}

	@Override
	public void setFocus() {
		

	}

}
