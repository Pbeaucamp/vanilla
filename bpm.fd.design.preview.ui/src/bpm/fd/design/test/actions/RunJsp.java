package bpm.fd.design.test.actions;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdVanillaFormModel;
import bpm.fd.design.test.Activator;
import bpm.fd.design.test.Messages;
import bpm.fd.design.test.preferences.PreferencesConstants;
import bpm.fd.design.test.servlets.ComponentServlet;
import bpm.fd.design.test.servlets.DrillServlet;
import bpm.fd.design.test.servlets.ExportServlet;
import bpm.fd.design.test.servlets.ListDirtyComponentServlet;
import bpm.fd.design.test.servlets.ParameterServlet;
import bpm.fd.design.test.servlets.PopupModelServlet;
import bpm.fd.design.test.servlets.SlicerServlet;
import bpm.fd.design.test.servlets.ZoomChartServlet;
import bpm.fd.runtime.engine.VanillaProfil;
import bpm.fd.runtime.engine.deployer.ProjectDeployer;
import bpm.fd.runtime.model.controler.Controler;
import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.components.FreeDashboardComponent;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;




public class RunJsp implements IEditorActionDelegate {

	private static final String webAppPath = Platform.getInstallLocation().getURL().getFile() + "/webapps/freedashboard.war"; //$NON-NLS-1$
	private IEditorPart editorPart;
	private static Server jettyServer;
	private String url;
	
	private static void stop(){
		if (jettyServer != null){
			try {
				jettyServer.stop();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	static{
		//init Controler
		Controler.initGenerationFolder(webAppPath + "/generation");
	}
	
	private static void start(final Group group, final User user){
		
		int port = Activator.getDefault().getPreferenceStore().getInt(PreferencesConstants.P_JETTY_PORT);
	 	jettyServer = new Server(port);
	 	
		WebAppContext webapp = new WebAppContext();

        webapp.setContextPath("/"); //$NON-NLS-1$
        webapp.setWar(webAppPath);
        webapp.setCopyWebDir(true);
        
        try{
            ServletHolder old = webapp.addServlet(ParameterServlet.class.getName(), FreeDashboardComponent.SERVLET_PARAMETER);
            
            webapp.getServletHandler().addServlet(old);
            
            old = webapp.addServlet(ListDirtyComponentServlet.class.getName(), FreeDashboardComponent.SERVLET_LIST_DIRTY_COMPONENTS);
            webapp.getServletHandler().addServlet(old);
            
            old = webapp.addServlet(ComponentServlet.class.getName(), FreeDashboardComponent.SERVLET_COMPONENT);
            webapp.getServletHandler().addServlet(old);
            
            
            old = webapp.addServlet(PopupModelServlet.class.getName(), FreeDashboardComponent.SERVLET_POPUP_MODEL);
            webapp.getServletHandler().addServlet(old);
            
            old = webapp.addServlet(ZoomChartServlet.class.getName(), "/freedashboardRuntime/zoomComponent");
            webapp.getServletHandler().addServlet(old);
            
            old = webapp.addServlet(DrillServlet.class.getName(), FreeDashboardComponent.SERVLET_DRILL);
            webapp.getServletHandler().addServlet(old);
            
            old = webapp.addServlet(ExportServlet.class.getName(), FreeDashboardComponent.SERVLET_EXPORT);
            webapp.getServletHandler().addServlet(old);
            
            old = webapp.addServlet(SlicerServlet.class.getName(), FreeDashboardComponent.SERVLET_SLICER);
            webapp.getServletHandler().addServlet(old);

        }catch(Exception ex){
        	ex.printStackTrace();
        }

        Logger l = Logger.getLogger("runtimeFdLogger"); //$NON-NLS-1$
        
        l.setLevel(Level.ALL);
        jettyServer.setHandler(webapp);
       
       
        jettyServer.setStopAtShutdown(true);
        try {
			jettyServer.start();
		} catch (Exception e1) {
			e1.printStackTrace();
			Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			ErrorDialog.openError(sh, Messages.RunJsp_3,Messages.RunJsp_4, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.RunJsp_5, e1));

			
		}
        Thread t =  new Thread(){
        	public void run(){
        		try {
					jettyServer .join();
				} catch (InterruptedException e) {
					
				}
        	}
        };
        t.start();
	}
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		editorPart = targetEditor;

	}

	public void run(IAction action) {
		try{
			stop();
		}catch(Exception e){
			
		}
		Object adapter = editorPart.getEditorInput().getAdapter(this.getClass());
		
		Assert.isTrue(adapter instanceof FdModel);
		FdProject project = ((FdModel)adapter).getProject();
		VanillaProfil profil = null;
		
		IRepositoryContext repCtx = null;
		String jspName = project.getProjectDescriptor().getModelName().replace(" ", "_");
		String jspPath = project.getProjectDescriptor().getProjectName() + "/" + jspName + ".jsp"; //$NON-NLS-1$ //$NON-NLS-2$
		try{
			
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			
			
			
			try{
				Bundle bundle = Platform.getBundle("bpm.fd.repository.ui"); //$NON-NLS-1$
				String activator = (String)bundle.getHeaders().get(Constants.BUNDLE_ACTIVATOR);

				Class<?> activatorClass = bundle.loadClass(activator);
				Method method = activatorClass.getMethod("getDefault"); //$NON-NLS-1$
				IDesignerActivator<FdProject> designer = (IDesignerActivator<FdProject>)method.invoke(activatorClass);
				if (designer != null){
					repCtx = designer.getRepositoryContext();
				}
			}catch(Throwable t){
				t.printStackTrace();
			}
			
			
			
			if (repCtx == null){
//				MessageDialog.openWarning(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.RunJsp_10, Messages.RunJsp_11);
				
				BaseVanillaContext ctx = new BaseVanillaContext(store.getString(PreferencesConstants.P_DEFAULT_VANILLA_URL), store.getString(PreferencesConstants.P_DEFAULT_VANILLA_LOGIN), store.getString(PreferencesConstants.P_DEFAULT_VANILLA_PASSWORD));
				Group g = new Group();
				g.setId(1);
				g.setName(store.getString(PreferencesConstants.P_DEFAULT_VANILLA_GROUP));
				
				Repository repDef = new Repository();
				repDef.setId(1);
				repDef.setName(Messages.RunJsp_12);
				repDef.setSociete("http://localhost:8080/BIRepository"); //$NON-NLS-1$
				repCtx = new BaseRepositoryContext(ctx, g, repDef);
				
				
			}
			profil = new VanillaProfil(repCtx, null);
			
			
			// for the deployment we dont need the repositoryreferences here
			User user = null;
			
			
			IObjectIdentifier identifier = new ObjectIdentifier(repCtx.getRepository().getId(), 
					-1);
			IVanillaAPI api = new RemoteVanillaPlatform(repCtx.getVanillaContext());
			
			try{
				user = api.getVanillaSecurityManager().authentify("", 
						repCtx.getVanillaContext().getLogin(), 
						repCtx.getVanillaContext().getPassword(), false);
			}catch(Exception ex){
				user = new User();
				user.setId(1);
				user.setLogin(repCtx.getVanillaContext().getLogin());
				user.setLogin(repCtx.getVanillaContext().getPassword());
			}
			
			Controler.getInstance().destroyAll();
			if (project.getFdModel() instanceof FdVanillaFormModel){
				HashMap<String, String> dummy = new HashMap<String, String>();
				dummy.put("hiddenParam", "value"); //$NON-NLS-1$ //$NON-NLS-2$
				String relativePath = ProjectDeployer.deployForm(false,
						identifier,
						repCtx.getGroup(),
						user,
						project, Locale.getDefault().getLanguage(), true, dummy); //$NON-NLS-1$ //$NON-NLS-2$
				jspPath = relativePath;				
			}
			else{
				
				String relativePath = ProjectDeployer.deploy(
						identifier,
						repCtx,
						user,
						project, Locale.getDefault().getLanguage(), true, new HashMap<String, String>()); //$NON-NLS-1$ //$NON-NLS-2$
				jspPath = relativePath;				
			}
		}catch(Exception e){
			e.printStackTrace();
			ErrorDialog.openError(editorPart.getSite().getShell(), Messages.RunJsp_25,Messages.RunJsp_26, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.RunJsp_27, e));
			return;
		}

		try{
			User u = new User();
			u.setId(1);
			u.setLogin(repCtx.getVanillaContext().getLogin());
			u.setPassword(repCtx.getVanillaContext().getPassword());
			start(repCtx.getGroup(), u);
		}catch(Exception e){
			
		}
		int port = Activator.getDefault().getPreferenceStore().getInt(PreferencesConstants.P_JETTY_PORT);
		url = "http://localhost:" + port + "/generation/" + jspPath; //$NON-NLS-1$ //$NON-NLS-2$
		
	}
	
	public String getUrl(){
		return url;
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}

}
