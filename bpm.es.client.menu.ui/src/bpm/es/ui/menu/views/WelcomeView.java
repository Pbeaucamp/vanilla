package bpm.es.ui.menu.views;

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

import bpm.es.ui.menu.Messages;

import adminbirep.Activator;

public class WelcomeView extends ViewPart {
private static final Font descriptionFont = new Font(Display.getDefault(), "Arial", 8, SWT.ITALIC); //$NON-NLS-1$
private static final Font menuFont = new Font(Display.getDefault(), "Arial", 10, SWT.BOLD);; //$NON-NLS-1$
	private FormToolkit toolkit;
	private ScrolledForm form;

	private Section userManagement;
	
	private static final String[] users = new String[]{
		"adminBIRep.perspective", //$NON-NLS-1$
		"bpm.birep.admin.client.perspectives.PerspectiveRepositoryAdmin" //$NON-NLS-1$
	};
	private static final String[] settings = new String[]{
		"adminBIRep.perspective.setup", //$NON-NLS-1$
		"bpm.birep.admin.client.perspectives.CustomizeHome", //$NON-NLS-1$
		"bpm.entrepriseservices.fmdturl.perspectives.FmdtUrlPerspective", //$NON-NLS-1$
		"bpm.birep.admin.client.perspectives.VanillaServices", //$NON-NLS-1$
		"adminBIRep.perspective.variables" //$NON-NLS-1$
	};
	
	private static final String[] repository = new String[]{
		"adminBIRep.perspective.adressable", //$NON-NLS-1$
		"adminBIRep.perspective.content", //$NON-NLS-1$
		"bpm.birep.admin.client.perspectives.DeploymentsPerspective", //$NON-NLS-1$
		"adminBIRep.perspective.versionning" //$NON-NLS-1$
		
	};
	
	private static final String[] analysis = new String[]{
		"adminBIRep.perspective1", //$NON-NLS-1$
		"adminBIRep.perspective.crossing", //$NON-NLS-1$
		"bpm.es.datasource.analyzer.ui.perspective", //$NON-NLS-1$
		"bpm.es.datasource.audit.ui.AuditPerspectiveFactory" //$NON-NLS-1$
		
	};
	
	private Section repositoryManagement;
	private Section settingsSection;
	private Section analysisSection;
	private Section otherSection;
	
	public WelcomeView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText(Messages.WelcomeView_17);
		form.getBody().setLayout(new GridLayout(2, true));

		Composite pannelLeft = toolkit.createComposite(form.getBody(), SWT.NONE);
		pannelLeft.setLayout(new GridLayout());
		pannelLeft.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Composite pannelRight = toolkit.createComposite(form.getBody(), SWT.NONE);
		pannelRight.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		pannelRight.setLayout(new GridLayout());
		
		createUserManagementSection(pannelLeft);
		createSettingsSection(pannelRight);
		createRepositorySection(pannelLeft);
		createAnalysisSection(pannelRight);
		createOtherSection(pannelLeft);
	}
	private void createOtherSection(Composite parent){
		otherSection = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR| Section.TWISTIE);
		otherSection.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		otherSection.setText(Messages.WelcomeView_18);
		otherSection.setDescription(Messages.WelcomeView_19);
		
		Composite main = toolkit.createComposite(otherSection);
		main.setLayout(new GridLayout());
		
		
		for(IPerspectiveDescriptor pd : Activator.getDefault().getWorkbench().getPerspectiveRegistry().getPerspectives()){
			boolean found = false;
			for(String s : users){
				if (s.equals(pd.getId())){
					found = true;
					break;
				}
			}
			for(String s : settings){
				if (s.equals(pd.getId())){
					found = true;
					break;
				}
			}
			for(String s : repository){
				if (s.equals(pd.getId())){
					found = true;
					break;
				}
			}
			for(String s : analysis){
				if (s.equals(pd.getId())){
					found = true;
					break;
				}
			}
			
			if (!found && !pd.getId().equals("bpm.es.ui.menu.WelcomePerspective")){ //$NON-NLS-1$
				createButton(pd.getId(), main);
			}
		}
		
		
		
		otherSection.setClient(main);

	}
	private void createUserManagementSection(Composite parent){
		userManagement = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR| Section.TWISTIE|Section.EXPANDED);
		userManagement.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		userManagement.setText(Messages.WelcomeView_21);
		userManagement.setDescription(Messages.WelcomeView_22);
		
		Composite main = toolkit.createComposite(userManagement);
		main.setLayout(new GridLayout());
		
		for(String s : users){
			createButton(s, main);
		}
		
		
		userManagement.setClient(main);

	}
	
	private void createRepositorySection(Composite parent){
		repositoryManagement = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR| Section.TWISTIE|Section.EXPANDED);
		repositoryManagement.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		repositoryManagement.setText(Messages.WelcomeView_23);
		repositoryManagement.setDescription(Messages.WelcomeView_24);
		
		Composite main = toolkit.createComposite(repositoryManagement);
		main.setLayout(new GridLayout());
		
		for(String s : repository){
			createButton(s, main);
		}
		
		
		repositoryManagement.setClient(main);

	}
	
	private void createAnalysisSection(Composite parent){
		analysisSection = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR| Section.TWISTIE|Section.EXPANDED);
		analysisSection.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		analysisSection.setText(Messages.WelcomeView_25);
		analysisSection.setDescription(Messages.WelcomeView_26);
		
		Composite main = toolkit.createComposite(analysisSection);
		main.setLayout(new GridLayout());
		
		for(String s : analysis){
			createButton(s, main);
		}
		
		
		analysisSection.setClient(main);

	}
	
	private void createSettingsSection(Composite parent){
		settingsSection = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR| Section.TWISTIE|Section.EXPANDED);
		settingsSection.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		settingsSection.setText(Messages.WelcomeView_27);
		settingsSection.setDescription(Messages.WelcomeView_28);
		
		Composite main = toolkit.createComposite(settingsSection);
		main.setLayout(new GridLayout());
		
		for(String s : settings){
			createButton(s, main);
		}
		
		
		settingsSection.setClient(main);

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
//		if (persId.equals("bpm.birep.admin.client.perspectives.WorkplacePerspective")){
//			b.setEnabled(false);
//		}
		
		Label l = toolkit.createLabel(c, pd.getLabel(), SWT.NONE);
		l.setFont(menuFont);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		l = toolkit.createLabel(c, pd.getDescription(), SWT.WRAP);
		l.setFont(descriptionFont);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
	}
	
	
	private IPerspectiveDescriptor getPerspectiveDescriptor(String id){
		IPerspectiveRegistry reg = bpm.es.ui.menu.Activator.getDefault().getWorkbench().getPerspectiveRegistry();
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
