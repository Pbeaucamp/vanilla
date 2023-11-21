package bpm.fd.design.ui.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;

public class ActionGenerateLocalisationFiles {

	public void run(){
		List<String> locales  = Activator.getDefault().getProject().getLocale();
		Dictionary dic = Activator.getDefault().getProject().getDictionary();
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		FdProject fdProject = Activator.getDefault().getProject();
		IProject p = root.getProject(fdProject.getProjectDescriptor().getProjectName());

		

		IFile f = p.getFile("components.properties"); //$NON-NLS-1$
		if (f.exists()){
			try {
				update(f, dic);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			try {
				create(null, f, dic);
				f.createLink(Platform.getLocation().append(f.getFullPath()), IResource.REPLACE, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(String local : locales){
			//check if fileAlreayExists
			
			f = p.getFile(local + "_components.properties" ); //$NON-NLS-1$
			if (f.exists()){
				try {
					update(f, dic);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else{
				try {
					create(local, f, dic);
					f.createLink(Platform.getLocation().append(f.getFullPath()), IResource.REPLACE, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	
	}
	
	
	private void update(IFile f, Dictionary dictionary) throws Exception{
		Properties lang = new Properties();
		lang.load(new FileInputStream(f.getLocation().toOSString()));
		
		for(IComponentDefinition def : dictionary.getComponents()){
			for(IComponentOptions opt : def.getOptions()){
				for(String key : opt.getInternationalizationKeys()){
					if (lang.getProperty(def.getName() + "." + key) == null){ //$NON-NLS-1$
						lang.setProperty(def.getName() + "." + key, opt.getDefaultLabelValue(key)); //$NON-NLS-1$
					}
				}
			}
		}
		
		lang.store(new FileOutputStream(f.getLocation().toOSString()), Messages.ActionGenerateLocalisationFiles_4);
		
	}
	
	
	private void create(String locale, IFile f, Dictionary dictionary) throws Exception{
		Properties lang = new Properties();
		for(IComponentDefinition def : dictionary.getComponents()){
			for(IComponentOptions opt : def.getOptions()){
				for(String key : opt.getInternationalizationKeys()){
					String value  = opt.getDefaultLabelValue(key);
					if (value != null){
						lang.setProperty(def.getName() + "." + key, value); //$NON-NLS-1$
					}
					else{
						lang.setProperty(def.getName() + "." + key, ""); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		}
		
		
		
		lang.store(new FileOutputStream(Platform.getLocation().toOSString() + f.getFullPath().toOSString()), Messages.ActionGenerateLocalisationFiles_8 );
		FileProperties fp = new FileProperties(f.getName(), locale, new File(Platform.getLocation().toOSString() + f.getFullPath().toOSString()));
		Activator.getDefault().getProject().addResource(fp);

	}
}
