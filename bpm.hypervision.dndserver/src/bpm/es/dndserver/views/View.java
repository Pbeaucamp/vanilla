package bpm.es.dndserver.views;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;

import bpm.es.dndserver.Activator;
import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.DNDOProject;
import bpm.es.dndserver.api.Message;
import bpm.es.dndserver.api.migrator.Migrator;
import bpm.es.dndserver.icons.IconsName;
import bpm.es.dndserver.tools.OurLogger;
import bpm.es.dndserver.views.composites.DataMappingViewer;
import bpm.es.dndserver.views.composites.NavigationViewer;
import bpm.es.dndserver.views.composites.RepositoryViewer;
import bpm.es.dndserver.views.dialogs.DialogGroupPicker;
import bpm.vanilla.platform.core.IRepositoryApi;

public class View extends ViewPart {
	public static final String ID = "bpm.es.dndserver.view"; //$NON-NLS-1$
	
	public static final int STEP0 = 0;
	public static final int STEP1 = 1;
	public static final int STEP2 = 2;
	public static final int STEP3 = 3;
	
	private int currentStep = 0;
	
	private NavigationViewer navLeft, navRight;
	
	private FormToolkit toolkit;
	//private ScrolledForm form;
	private Form form;

	private PageBook book;
	
	private Control pageRepository;
	private DataMappingViewer pageDataMapping;
	
	private RepositoryViewer inputViewer;
	private RepositoryViewer outputViewer;
	
	private DNDOProject project;
	
//	private IManager manager;
	
	public void createPartControl(Composite parent) {
		
		toolkit = new FormToolkit(parent.getDisplay());
		
		form = toolkit.createForm(parent);
		form.setText(Messages.View_1);
		form.setImage(Activator.getDefault().getImageRegistry().get(IconsName.DNDO48));
		form.getBody().setLayout(new GridLayout(1, false));
		
		toolkit.decorateFormHeading(form);
		
		Composite main = toolkit.createComposite(form.getBody(), SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		navLeft = new NavigationViewer(main, SWT.NONE, this);
		navLeft.setNavigation(NavigationViewer.TYPE_BACKWARD, 
				NavigationViewer.STYLE_DISABLED);
		
		book = new PageBook(main, SWT.NONE);
		book.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		pageRepository = createPageRepository(book);
		pageDataMapping = createPageDataMapping(book);
		
		book.showPage(pageRepository);
		
		navRight = new NavigationViewer(main, SWT.NONE, this);
		navRight.setNavigation(NavigationViewer.TYPE_FORWARD, 
				NavigationViewer.STYLE_DISABLED);
		
		try {
			loadData();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	private Control createPageRepository(Composite main) {
		Composite tables = toolkit.createComposite(main, SWT.NONE);
		tables.setLayout(new GridLayout(2, true));
		tables.setLayoutData(new GridData(GridData.FILL_BOTH));
		toolkit.paintBordersFor(tables);
		
		inputViewer = new RepositoryViewer(tables, SWT.NONE, 
				RepositoryViewer.TYPE_SOURCE, toolkit);
		toolkit.paintBordersFor(inputViewer);
		
		outputViewer = new RepositoryViewer(tables, SWT.NONE, 
				RepositoryViewer.TYPE_TARGET, toolkit);
		outputViewer.addListener(SWT.Modify, new Listener(){
			@Override
			public void handleEvent(Event event) {
				showStep(currentStep);
				
			}
		});
		toolkit.paintBordersFor(outputViewer);
		
		return tables;
	}
	
	private DataMappingViewer createPageDataMapping(Composite main) {
		DataMappingViewer viewer = new DataMappingViewer(main, SWT.NONE, toolkit, form);
		
		return viewer;
	}
	
	private void loadData() throws Exception {

		Activator.getDefault().createDefaultProject();
		project = Activator.getDefault().getDefaultProject();
		
		inputViewer.setInput(project);
		outputViewer.setInput(project);//should be null
	}

	public void setFocus() {
		
		if (bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaApi() != null){
			try {
				loadData();
//				manager = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getManager();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		else{
			inputViewer.setFocus();
		}
		
		
		
	}
	
	public void nextStep() {
		if (project.getOutputRepository().getFlatItems().isEmpty()){
			MessageDialog.openInformation(getSite().getShell(), Messages.View_2, Messages.View_3);
			return;
		}
		currentStep++;
		showStep(currentStep);
	}
	
	public void previousStep() {
		currentStep--;
		showStep(currentStep);
	}
	
	private void showStep(int currentStep) {
		//currentStep++;
		
		if (currentStep == STEP0) {
			book.showPage(pageRepository);
			navLeft.setNavigation(NavigationViewer.TYPE_BACKWARD, 
					NavigationViewer.STYLE_DISABLED);
			
			if (project.getOutputRepository() == null || project.getOutputRepository().getFlatItems().isEmpty()){
				navRight.setNavigation(NavigationViewer.TYPE_FORWARD, 
						NavigationViewer.STYLE_DISABLED);
			}
			else{
				navRight.setNavigation(NavigationViewer.TYPE_FORWARD, 
						NavigationViewer.STYLE_ENABLED);
			}
			
		}
		else if (currentStep == STEP1) {
			pageDataMapping.setProject(project);
			book.showPage(pageDataMapping);
			navLeft.setNavigation(NavigationViewer.TYPE_BACKWARD, 
					NavigationViewer.STYLE_ENABLED);
			navRight.setNavigation(NavigationViewer.TYPE_FORWARD, 
					NavigationViewer.STYLE_FINISHED);
		} else {
			OurLogger.info(Messages.View_4);
			//MessageDIa
		}
	}
	
	public void performFinish() throws Exception {
		System.out.println("performing finish"); //$NON-NLS-1$
		
		if (project.getMessenger().hasError()) {
			boolean res = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.View_6, 
					Messages.View_7);
			
			if (!res)
				return;
		}
		else if (project.getMessenger().hasWarnings()) {
			boolean res = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					Messages.View_8, 
					Messages.View_9);
			
			if (!res)
				return;
		}
		
		IRepositoryApi input = project.getInputRepository().getRepositoryClient();

		IRepositoryApi output = project.getOutputRepository().getRepositoryClient();
		
		int count = project.getOutputRepository().getFlatItems().size();
		Migrator m = new Migrator(project);
		
		int nmMess = project.getMessenger().getAllMessages().size();
		
		
		/*
		 * select to group performing the creations
		 */
				
		
		DialogGroupPicker d = new DialogGroupPicker(getSite().getShell());
		if (d.open() != DialogGroupPicker.OK){
			MessageDialog.openInformation(getSite().getShell(), Messages.View_10, Messages.View_11);
			return;
			
		}
		
		
		m.migrate(project.getOutputRepository().getFlatItems(), input, output, d.getGroups(), project.getMessenger());
		
		
		
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
	     try {
	    	 service.run(true, false, m);
	    	 
	    	 
	     } catch (InvocationTargetException e) {
	        e.printStackTrace();
	     } catch (InterruptedException e) {
	        e.printStackTrace();
	     }
		
		nmMess = project.getMessenger().getAllMessages().size() - nmMess;
		
		if (nmMess > 0){
			StringBuilder b = new StringBuilder();
			for(Message ms : project.getMessenger().getAllMessages()){
				b.append(ms.toString() + "\n"); //$NON-NLS-1$
			}
			
			MessageDialog.openWarning(Display.getDefault().getActiveShell(), Messages.View_13, Messages.View_14 + (count - nmMess) + Messages.View_15 + b.toString());
		}
		else{
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), Messages.View_16, Messages.View_17 + count + Messages.View_18);
		}
		
		
		
		
		//clear errors and objects
		project.getMessenger().clearAllMessages();
		project.getOutputRepository().clearMapObjects();
		
		//refresh views;
//		pageDataMapping.setProject(project);
//		outputViewer.setInput(project);
//		previousStep();
		pageDataMapping.setProject(project);
		outputViewer.setInput(project);
//		book.showPage(pageDataMapping);
		previousStep();
		
	}

}