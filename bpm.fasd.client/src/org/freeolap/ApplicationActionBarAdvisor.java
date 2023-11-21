package org.freeolap;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.fasd.aggwizard.WizardAggregate;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.DocumentProperties;
import org.fasd.olap.FAModel;
import org.fasd.preferences.PreferenceConstants;
import org.fasd.utils.trees.TreeCube;
import org.fasd.views.actions.ActionCheckCube;
import org.fasd.views.actions.ActionCheckSchema;
import org.fasd.views.actions.ActionNewSchema;
import org.fasd.views.actions.ActionOpen;
import org.fasd.views.actions.ActionUnderImplementation;
import org.fasd.views.composites.PropertiesDialog;


/**
 * An action bar advisor is responsible for creating, adding, and disposing of the
 * actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
    private IWorkbenchAction exitAction;
    private IWorkbenchAction preferenceAction;
    
    private IWorkbenchAction aboutAction, importModel, exportModel;
    
    private Action newschema;
    private Action save;
    private Action saveas;
    private Action open;
 //   private Action launchFA;
    
    private Action propertiesSchema;
    private Action aggregateAction;
    private Action inportMondrian;
    
    private Action exportXMLFS;
    private Action exportXMLWEB;
    
    private Action securityConnection, securityParameters, designer;
//    private Action molapCreation,molapDefinition, molapDistribution, molapLoad, molapSupervision;
    
    private Action validateProject, validateCube, scheduleCube;
    private Action svg;
    
    private Action xmlaPerspectiveAction;
    
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }
    
    protected void makeActions(final IWorkbenchWindow window) {

        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);
        
        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
        
        preferenceAction = ActionFactory.PREFERENCES.create(window);
        register(preferenceAction);
	
        importModel = ActionFactory.IMPORT.create(window);
        register(importModel);
        exportModel= ActionFactory.EXPORT.create(window);
        register(exportModel);
        
        newschema = new Action(){
        	public void run(){
        		new ActionNewSchema(window.getShell(), true).run();
        	}
        };
        
        System.out.println(Platform.getInstallLocation().getURL().getPath());
        Image img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/new.png"); //$NON-NLS-1$
        //Image img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/new.png"); //$NON-NLS-1$
        newschema.setImageDescriptor(ImageDescriptor.createFromImage(img));
        newschema.setText(LanguageText.ApplicationActionBarAdvisor_New_Schema);
        newschema.setToolTipText(LanguageText.ApplicationActionBarAdvisor_New_Schema);
        
       propertiesSchema=new Action(LanguageText.ApplicationActionBarAdvisor_Properties){
    	   public void run() {
    		   DocumentProperties prop = FreemetricsPlugin.getDefault().getFAModel().getDocumentProperties();
    		   PropertiesDialog dialog = new PropertiesDialog(window.getShell(), prop);
    		   if (dialog.open() == Dialog.OK){
    			   System.out.println(dialog.getDocProperty().toString());
        		   
        		   FAModel model = FreemetricsPlugin.getDefault().getFAModel();
        			
        			if (model == null) {
        				return; // there is no data
        			}
        			
//        			DocumentProperties docProp=dialog.getDocProperty();
//        			model.setDocumentProperties(docProp);
    		   }
    	   }
       };

       img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_file.png"); //$NON-NLS-1$
       propertiesSchema.setImageDescriptor(ImageDescriptor.createFromImage(img)); 
       
        save = new Action(LanguageText.ApplicationActionBarAdvisor_Save) {
			public void run() {
				String path = FreemetricsPlugin.getDefault().getPath();
				if (path.equals("") || path.equals("*") || path == null){ //$NON-NLS-1$ //$NON-NLS-2$
					saveas.run();
					return;
				}
				if (FreemetricsPlugin.getDefault().isMondrianImport()){
					saveas.run();
				}
				
				if (path != null) {
					FAModel model = FreemetricsPlugin.getDefault().getFAModel();
					File file = new File(path);
					try {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
						Date today= new Date();
						model.getDocumentProperties().setModification(sdf.format(today));
						
						FileWriter fw = new FileWriter(file);
						fw.write(model.getFAXML());
						fw.close();
						//new ActionLoadViews(window.getShell()).run();
						
						//model.save();
						window.getShell().setText("Vanilla Analysis Designer - " + path); //$NON-NLS-1$
						
					} catch (Exception ex) {
						ex.printStackTrace();
						MessageDialog.openError(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Error, LanguageText.ApplicationActionBarAdvisor_Failed_Write_Data);
					}
				}
			}
        };
       
   
        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/Save.png"); //$NON-NLS-1$
        save.setImageDescriptor(ImageDescriptor.createFromImage(img));
        
        saveas = new Action(LanguageText.ApplicationActionBarAdvisor_SaveAs__) {
			public void run() {
				//d = new Display();
			    //Shell s = new Shell(window.getShell());
			    
				FileDialog dd = new FileDialog(window.getShell(), SWT.SAVE);
				dd.setFilterExtensions(new String[] {"*.fasd"}); //$NON-NLS-1$
				dd.setText(LanguageText.ApplicationActionBarAdvisor_Save_Project_As___);
				
				String path = dd.open();
				if (path != null) {
					FAModel model = FreemetricsPlugin.getDefault().getFAModel();
					
					if (!path.endsWith(".fasd")) //$NON-NLS-1$
						path += ".fasd";  //$NON-NLS-1$
					
					File file = new File(path);
					try {
						FileWriter fw = new FileWriter(file);
						fw.write(model.getFAXML());
						fw.close();
						//new ActionLoadViews(window.getShell()).run(); 
						//FreemetricsPlugin.getDefault().getFAModel().save();
						if (FreemetricsPlugin.getDefault().isMondrianImport())
							FreemetricsPlugin.getDefault().setMondrianImport(false);
						
						window.getShell().setText("Vanilla Analysis Designer - " + path); //$NON-NLS-1$
						FreemetricsPlugin.getDefault().setPath(path);
					} catch (Exception ex) {
						ex.printStackTrace();
						MessageDialog.openError(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Error, LanguageText.ApplicationActionBarAdvisor_Failed_Write_DataR+ex.getMessage());
					}
				}
			}
        };
        save.setAccelerator(SWT.CTRL | 's');
        
        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/Save_As.png"); //$NON-NLS-1$
        saveas.setImageDescriptor(ImageDescriptor.createFromImage(img));
//        
//        saveas = new ActionSaveAs(window.getShell());
        
        open = new ActionOpen();
        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/Open.png"); //$NON-NLS-1$
        open.setImageDescriptor(ImageDescriptor.createFromImage(img));
        
//        inportMondrian = new Action(LanguageText.ApplicationActionBarAdvisor_Import_Schema_Mondrian) {
//			public void run() {
//				new ActionNewSchema(window.getShell(), true).run();
//				FreemetricsPlugin.getDefault().setMondrianImport(true);
//				
//				FileDialog dd = new FileDialog(window.getShell());
//				dd.setFilterExtensions(new String[] {"*.xml"}); //$NON-NLS-1$
//				dd.setText(LanguageText.ApplicationActionBarAdvisor_Choose_Schema);
//				
//				String path = dd.open();
//				if (path != null) {
//					try {
//						FreemetricsPlugin.getDefault().importMondrian(path);
//						window.getShell().setText("Free Analysis Schema Designer - " + path); //$NON-NLS-1$
//						
//						//detect presence of calculated column inside dimension's levels
//						boolean calc = false;
//						StringBuffer buf = new StringBuffer();
//						for(OLAPDimension d : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getDimensions()){
//							for(OLAPHierarchy h : d.getHierarchies()){
//								for(OLAPLevel l : h.getLevels()){
//									if (l.getItem() ==  null && l.getKeyExpressions().size() > 0)
//										calc = true;
//								}
//							}
//						}
//						
//						if (calc){
//							buf.append("\n\n The Mondrian Schema contains some calculated Columns inside Dimension's Levels. Those columns are not created inside  the FASD DataSource, but you can create them choosing the add Column button when you select � Table in the Dummy's DataSource."); //$NON-NLS-1$
//						}
//						
//						MessageDialog.openInformation(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Warning, LanguageText.ApplicationActionBarAdvisor_Dummy + buf.toString());
//					} catch (FileNotFoundException ex) {
//						ex.printStackTrace();
//						MessageDialog.openError(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Error, LanguageText.ApplicationActionBarAdvisor_Opening_Failed_ + ex.getMessage());
//					}	catch (Exception ex) {
//						ex.printStackTrace();
//						MessageDialog.openError(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Error, LanguageText.ApplicationActionBarAdvisor_Parsing_Failed_ + ex.getMessage());
//					}
//				}
//			}
//        };
//        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/dataset_wizard16.png"); //$NON-NLS-1$
//        inportMondrian.setImageDescriptor(ImageDescriptor.createFromImage(img));
//        
//        exportXMLFS = new Action(LanguageText.ApplicationActionBarAdvisor_Export_Mondrian) {
//			public void run() {
//				ActionCheckSchema action = new ActionCheckSchema();
//				action.run();
//				if (!action.isValide()){
//					MessageDialog.openInformation(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Error, LanguageText.ApplicationActionBarAdvisor_Schema_Contains_Error);
//					return;
//				}
//				
//				
//				
//				FileDialog dd = new FileDialog(window.getShell(), SWT.SAVE);
//				dd.setFilterExtensions(new String[] {"*.xml"}); //$NON-NLS-1$
//				dd.setText(LanguageText.ApplicationActionBarAdvisor_Export_Mondrian_File);
//				
//				String path = dd.open();
//				if (path != null) {
//					//OLAPSchema schema = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema();
//					
//					if (!path.endsWith(".xml")) //$NON-NLS-1$
//						path += ".xml";  //$NON-NLS-1$
//					File file = new File(path);
//					try {
//						FileWriter fw = new FileWriter(file);
//						
//						fw.write(FreemetricsPlugin.getDefault().getFAModel().exportToMondrian());
//						fw.close();
//					} catch (Exception ex) {
//						ex.printStackTrace();
//						MessageDialog.openError(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Error, LanguageText.ApplicationActionBarAdvisor_Failed_To_Generate);
//					}
//				}
//			}
//        };
//        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/datasource_wizard16.png"); //$NON-NLS-1$
//        exportXMLFS.setImageDescriptor(ImageDescriptor.createFromImage(img));
//        
//        exportXMLWEB = new Action(LanguageText.ApplicationActionBarAdvisor_Export_To_FA_Server){
//        	public void run(){
//        		FAModel model = FreemetricsPlugin.getDefault().getFAModel();
//        		
//        		if (model.getDocumentProperties().getName().equals("")){ //$NON-NLS-1$
//        			boolean cancel = false;
//            		cancel = MessageDialog.openConfirm(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Warning, LanguageText.ApplicationActionBarAdvisor_Name_No_Set);
//           			if (cancel){
//           				PropertiesDialog pdd = new PropertiesDialog(window.getShell(), model.getDocumentProperties());
//           				pdd.open();
//
//           			}
//
//        		}
//        		if (model.getDocumentProperties().getName().equals("")) //$NON-NLS-1$
//        			return;
//        		
//        		try {
//						DigesterRepository dig = new DigesterRepository(Platform.getInstallLocation().getURL().getPath() + "\\datas\\repository.xml"); //$NON-NLS-1$ //$NON-NLS-2$
//						
//          				Repository r = (Repository) dig.getRepository();
//
//      					r.addProject(model.getDocumentProperties().getName(), FreemetricsPlugin.getDefault().getPath());
//      					r.save();
//					} catch (FileNotFoundException e) {
//						MessageDialog.openError(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Error, LanguageText.ApplicationActionBarAdvisor_Unable_Find_repository);
//						e.printStackTrace();
//					} catch (Exception e) {
//						MessageDialog.openError(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Error, LanguageText.ApplicationActionBarAdvisor_Already_In_rep);
//						e.printStackTrace();
//					}
//      				
//      				
//       			}
//        	
//        };
//        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/datasource_wizard16_2.png"); //$NON-NLS-1$
//        exportXMLWEB.setImageDescriptor(ImageDescriptor.createFromImage(img));

        
        designer = new Action(LanguageText.ApplicationActionBarAdvisor_Designer){
			@Override
			public void run() {
				IWorkbenchPage page = window.getActivePage();
				page.closePerspective(page.getPerspective(), true, false);
//				if (FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema() != null){
					IPerspectiveDescriptor perspective = window.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.freeolap.perspective");; //$NON-NLS-1$
					page.setPerspective(perspective);
					FreemetricsPlugin.getDefault().refreshSQLView();
//				}
//				else if (FreemetricsPlugin.getDefault().getFAModel().getXMLASchema() != null){
//					IPerspectiveDescriptor perspective = window.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.fasd.xmlaperspective");; //$NON-NLS-1$
//					page.setPerspective(perspective);
//				}
				
				
			}
        	
        };
        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/stock_designer-16.png"); //$NON-NLS-1$
        designer.setImageDescriptor(ImageDescriptor.createFromImage(img));
        designer.setToolTipText(LanguageText.ApplicationActionBarAdvisor_Designer_Mode);
        
        
        xmlaPerspectiveAction = new Action("XMLA connections") { //$NON-NLS-1$
        	@Override
        	public void run() {
				IWorkbenchPage page = window.getActivePage();
				page.closePerspective(page.getPerspective(), true, false);
				IPerspectiveDescriptor perspective = window.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.fasd.xmlaperspective");; //$NON-NLS-1$
				page.setPerspective(perspective);
        	}
		};
        
        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/dataset_wizard16.png"); //$NON-NLS-1$
        xmlaPerspectiveAction.setImageDescriptor(ImageDescriptor.createFromImage(img));
        xmlaPerspectiveAction.setToolTipText("XMLA connections"); //$NON-NLS-1$
//        
//        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/netid.png"); //$NON-NLS-1$
//        securityConnection = new ActionUnderImplementation(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Secu_Conn);
//        securityConnection.setImageDescriptor(ImageDescriptor.createFromImage(img));
//        //securityParameters = new ActionUnderImplementation(window.getShell(),"Security Parameters");
//        
//        securityParameters = new Action(LanguageText.ApplicationActionBarAdvisor_Admin){
//			@Override
//			public void run() {
//				IWorkbenchPage page = window.getActivePage();
//				IPerspectiveDescriptor perspective = window.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.freeolap.securityperspective");; //$NON-NLS-1$
//				page.setPerspective(perspective);
//			}
//        	
//        };
//        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/lock16.png"); //$NON-NLS-1$
//        securityParameters.setImageDescriptor(ImageDescriptor.createFromImage(img));
//        
//        molapCreation = new Action(LanguageText.ApplicationActionBarAdvisor_Create_Molap_DS){
//        	public void run(){
//        		ISelection s = window.getSelectionService().getSelection();
//        		
//        		if (s instanceof StructuredSelection){
//        			StructuredSelection ss = (StructuredSelection)s;
//            		Object o = ss.getFirstElement();
//            		
//            		if(o != null && o instanceof TreeCube){
//            			if (((TreeCube)o).getOLAPCube().getType().equals("Rolap")){ //$NON-NLS-1$
//            				MessageDialog.openInformation(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Information, LanguageText.ApplicationActionBarAdvisor_Not_Molap_Cube);
//            				return;
//            			}
//            			
//            			new ActionBuildMolap(((TreeCube)o).getOLAPCube()).run();
//            		}
//            		else{
//            			MessageDialog.openInformation(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Information, LanguageText.ApplicationActionBarAdvisor_No_cube_Sel);
//            		}
//        		}
//        		
//        	}
//        	
//        	
//        };
//        
//        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/database.ico"); //$NON-NLS-1$
//        molapCreation.setImageDescriptor(ImageDescriptor.createFromImage(img));
//        
//        molapDefinition = new Action(LanguageText.ApplicationActionBarAdvisor_Fill_Molap_DS){
//        	public void run(){
//        		ISelection s = window.getSelectionService().getSelection();
//        		
//        		if (s instanceof StructuredSelection){
//        			StructuredSelection ss = (StructuredSelection)s;
//            		Object o = ss.getFirstElement();
//            		
//            		if(o != null && o instanceof TreeCube){
//            			new ActionFillMolap(((TreeCube)o).getOLAPCube()).run();
//            		}
//            		else{
//            			MessageDialog.openInformation(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Information, LanguageText.ApplicationActionBarAdvisor_No_cube_Sel);
//            		}
//        		}
//        	}
//        };
//        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/database_edit.ico"); //$NON-NLS-1$
//        molapDefinition.setImageDescriptor(ImageDescriptor.createFromImage(img));
//
//        molapDistribution = new ActionUnderImplementation(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Zip_Molap_Cube);
//        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/database_close.ico"); //$NON-NLS-1$
//        molapDistribution.setImageDescriptor(ImageDescriptor.createFromImage(img));
//
//        molapLoad = new ActionUnderImplementation(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Load_Molap);
//        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/database_open.ico"); //$NON-NLS-1$
//        molapLoad.setImageDescriptor(ImageDescriptor.createFromImage(img));
//
//        
//        molapSupervision = new ActionUnderImplementation(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Super_Molap);
//        img = new Image(window.getWorkbench().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/database_import.ico"); //$NON-NLS-1$
//        molapSupervision.setImageDescriptor(ImageDescriptor.createFromImage(img));

        scheduleCube = new ActionUnderImplementation(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Scheduler_for_Molap_Cube);
        
        validateCube = new Action(LanguageText.ApplicationActionBarAdvisor_Validate_Cube){
        	public void run(){
        		//getting the selected cube
        		ISelection s = FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
        		StructuredSelection ss = (StructuredSelection)s;
        		Object o = ss.getFirstElement();
        		
        		if (o instanceof TreeCube){
        			ActionCheckCube action = new ActionCheckCube();
        			action.setCube(((TreeCube)o).getOLAPCube());
        			action.run();
        		}
        		else{
        			MessageDialog.openInformation(FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), 
        											LanguageText.ApplicationActionBarAdvisor_Information, LanguageText.ApplicationActionBarAdvisor_No_cube_Sel);
        		}
        	}
        };
        validateProject = new ActionUnderImplementation(window.getShell(), LanguageText.ApplicationActionBarAdvisor_Validate_FASD_Proj);
        validateProject = new Action(LanguageText.ApplicationActionBarAdvisor_Validate_Proj){
        	public void run(){
        			ActionCheckSchema action = new ActionCheckSchema();
        			action.run();
        		
        	}
        };
        
        aggregateAction = new Action(LanguageText.ApplicationActionBarAdvisor_0){
        	public void run(){
        		WizardAggregate wizard = new WizardAggregate();
        		wizard.init(window.getWorkbench(),
        					(IStructuredSelection)window.getSelectionService().getSelection());
        		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        		dialog.create();
        		dialog.getShell().setSize(600, 800);
        		dialog.getShell().setText(LanguageText.ApplicationActionBarAdvisor_1);
        		if (dialog.open() == Dialog.OK){   
        			
        		}
        	}
        };
        img = new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() +"icons/agg.png"); //$NON-NLS-1$
        aggregateAction.setImageDescriptor(ImageDescriptor.createFromImage(img));
        aggregateAction.setToolTipText(LanguageText.ApplicationActionBarAdvisor_3);


    }
    
    protected void fillMenuBar(IMenuManager menuBar) {
    	MenuManager fileMenu = new MenuManager(LanguageText.ApplicationActionBarAdvisor_File, IWorkbenchActionConstants.M_FILE);
        MenuManager helpMenu = new MenuManager(LanguageText.ApplicationActionBarAdvisor_Help, IWorkbenchActionConstants.M_HELP);
//        MenuManager securityMenu = new MenuManager(LanguageText.ApplicationActionBarAdvisor_Security, IWorkbenchActionConstants.M_WINDOW);
//        MenuManager molapMenu = new MenuManager(LanguageText.ApplicationActionBarAdvisor_Molap, IWorkbenchActionConstants.M_LAUNCH);
        MenuManager toolsMenu = new MenuManager(LanguageText.ApplicationActionBarAdvisor_Tools, "tools"); //$NON-NLS-1$
//        MenuManager editMenu = new MenuManager("&Edit", IWorkbenchActionConstants.M_EDIT);
        MenuManager recentMenu = new MenuManager(LanguageText.ApplicationActionBarAdvisor_Open_Recent___, IWorkbenchActionConstants.M_NAVIGATE);
        
        menuBar.add(fileMenu);
//        menuBar.add(editMenu);
        menuBar.add(toolsMenu);
//        menuBar.add(securityMenu);
//        menuBar.add(molapMenu);
        menuBar.add(helpMenu);
        
        fileMenu.add(newschema);
        fileMenu.add(open);
        fileMenu.add(recentMenu);
        fileMenu.add(new Separator());
        fileMenu.add(saveas);
        fileMenu.add(save);
        fileMenu.add(new Separator());
        fileMenu.add(importModel);
        fileMenu.add(exportModel);
        fileMenu.add(new Separator());
        fileMenu.add(preferenceAction);
        fileMenu.add(propertiesSchema);
        
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);
        
        
//        editMenu.add(propertiesSchema);
        
       
       
        
       
        
        
        
        
//        //securityMenu.add(securityConnection);
//        securityMenu.add(securityParameters);
//        
//        molapMenu.add(molapCreation);
//        molapMenu.add(molapDefinition);
//        molapMenu.add(molapDistribution);
//        molapMenu.add(molapLoad);
//        molapMenu.add(molapSupervision);
//        molapMenu.add(scheduleCube);
       
       
//        toolsMenu.add(inportMondrian);
//        toolsMenu.add(exportXMLFS);
//        toolsMenu.add(exportXMLWEB);
        toolsMenu.add(new Separator());
        toolsMenu.add(validateProject);
        toolsMenu.add(validateCube);
//        toolsMenu.add(new Separator());
//        toolsMenu.add(aggregateAction);
        toolsMenu.add(new Separator("add")); //$NON-NLS-1$
        helpMenu.add(aboutAction);
        
        IPreferenceStore store = FreemetricsPlugin.getDefault().getPreferenceStore();
    	
        String[] recent = new String[]{
        		store.getString(PreferenceConstants.P_RECENTFILE1),
        		store.getString(PreferenceConstants.P_RECENTFILE2),
            	store.getString(PreferenceConstants.P_RECENTFILE3),
            	store.getString(PreferenceConstants.P_RECENTFILE4),
            	store.getString(PreferenceConstants.P_RECENTFILE5)
        };
        
        for(String s : recent){
        	if (!s.trim().equals("")){ //$NON-NLS-1$
        		Action a = new ActionOpen(s);
        		a.setText(s);
        		recentMenu.add(a);
        	}
        }
    	
    }
    
    protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(SWT.RIGHT | SWT.FLAT);
		coolBar.add(new ToolBarContributionItem(toolbar, "main"));    //$NON-NLS-1$
		toolbar.add(new GroupMarker(IWorkbenchActionConstants.OPEN_EXT));
		
		toolbar.add(newschema);
		toolbar.add(save);
		toolbar.add(saveas);
		toolbar.add(open);
		toolbar.add(new Separator());
//		toolbar.add(importModel);
//		toolbar.add(exportModel);
//		toolbar.add(new Separator());
//		toolbar.add(inportMondrian);
//		toolbar.add(exportXMLFS);
//		toolbar.add(exportXMLWEB);
		
		
		//toolbar.add(launchFA);
		IToolBarManager toolbarPort = new ToolBarManager(SWT.FLAT| SWT.RIGHT|SWT.DROP_DOWN);
		coolBar.add(new ToolBarContributionItem(toolbarPort, "in/export"));	 //$NON-NLS-1$
		toolbarPort.add(propertiesSchema);
//		toolbarPort.add(aggregateAction);
		
		
		
		IToolBarManager toolbarSecurity = new ToolBarManager(SWT.FLAT| SWT.RIGHT|SWT.DROP_DOWN);
		coolBar.add(new ToolBarContributionItem(toolbarSecurity, "security")); //$NON-NLS-1$
		toolbarSecurity.add(designer);
		toolbarSecurity.add(xmlaPerspectiveAction);
//		//toolbarSecurity.add(securityConnection);
//		toolbarSecurity.add(securityParameters);
//		
//		IToolBarManager toolbarMolap = new ToolBarManager(SWT.FLAT| SWT.RIGHT|SWT.DROP_DOWN);
//		coolBar.add(new ToolBarContributionItem(toolbarMolap, "molap")); //$NON-NLS-1$
//		toolbarMolap.add(molapCreation);
//		toolbarMolap.add(molapDefinition);
//		toolbarMolap.add(molapDistribution);
//		toolbarMolap.add(molapLoad);
//		toolbarMolap.add(molapSupervision);
		
		IToolBarManager toolbarOlapExplorer = new ToolBarManager(SWT.FLAT| SWT.RIGHT|SWT.DROP_DOWN);
		coolBar.add(new ToolBarContributionItem(toolbarOlapExplorer, "plugin")); //$NON-NLS-1$
    }
     
}
