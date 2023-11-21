package bpm.fd.design.ui.rcp.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectRepositoryDescriptor;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.parsers.AbstractParserException;
import bpm.fd.api.core.model.parsers.DictionaryParser;
import bpm.fd.api.core.model.parsers.FdModelParser;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.FileImage;
import bpm.fd.api.core.model.resources.FileJavaScript;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.rcp.Messages;
import bpm.vanilla.repository.ui.versionning.VersionningManager;

public class ActionOpenFdProject extends Action{

	private IProject project;
	
	public ActionOpenFdProject(IProject p)throws Exception {
		super(p.getDescription().getName() + " - " + p.getFullPath().toOSString()); //$NON-NLS-1$
		this.project = p;
	}
	
	public void run(){

		try{
			
			HashMap<Exception, String> errors  = new HashMap<Exception, String>();
			
			FdProject fdProject = null;
			Dictionary dictionary = null;
			
			if (!project.isOpen()){
				project.open(null);
			}
			//look for linkedFiles
			
			List<IFile> modelFile = new ArrayList<IFile>();
			IFile dictionaryFile = null;
			List<IFile> propertiesFile = new ArrayList<IFile>();
			List<IFile> cssFiles = new ArrayList<IFile>();
			List<IFile> imageFiles = new ArrayList<IFile>();
			List<IFile> javaScriptFiles = new ArrayList<IFile>();
			for(IResource r : project.members()){
				if (r == null){
					continue;
				}
				if (r instanceof IFile){
					IFile f = (IFile)r;
					
					if ("freedashboard".equals(f.getFileExtension().toLowerCase())){ //$NON-NLS-1$
						modelFile.add(f);
											}
					else if ("dictionary".equals(f.getFileExtension().toLowerCase())){ //$NON-NLS-1$
						dictionaryFile = f;
											}
					else if ("properties".contains(f.getFileExtension().toLowerCase())){ //$NON-NLS-1$
						propertiesFile.add(f);
					}
					else if ("css".equals(f.getFileExtension().toLowerCase())){ //$NON-NLS-1$
						cssFiles.add(f);
					}
					else if ("js".equals(f.getFileExtension().toLowerCase())){ //$NON-NLS-1$
						javaScriptFiles.add(f);
					}
					else{
						for(String s : FileImage.extensions){
							if (s.equals("*." + f.getFileExtension().toLowerCase())){ //$NON-NLS-1$
								imageFiles.add(f);
							}
						}
					}
					
				}
			}
			
			
			for(IFile f : propertiesFile){
				if (!f.getLocation().toFile().exists()){
					f.delete(true, null);
					errors.put(new Exception(Messages.ActionOpenFdProject_7 + f.getName() + Messages.ActionOpenFdProject_8), Messages.ActionOpenFdProject_9);
				}
			}
			
			for(IFile f : cssFiles){
				if (!f.getLocation().toFile().exists()){
					f.delete(true, null);
					errors.put(new Exception(Messages.ActionOpenFdProject_10 + f.getName() + Messages.ActionOpenFdProject_11), Messages.ActionOpenFdProject_12);
				}
			}
			for(IFile f : imageFiles){
				if (!f.getLocation().toFile().exists()){
					f.delete(true, null);
					errors.put(new Exception(Messages.ActionOpenFdProject_13 + f.getName() + Messages.ActionOpenFdProject_14), Messages.ActionOpenFdProject_15);
				}
			}
			for(IFile f : javaScriptFiles){
				if (!f.getLocation().toFile().exists()){
					f.delete(true, null);
					errors.put(new Exception(Messages.ActionOpenFdProject_16 + f.getName() + Messages.ActionOpenFdProject_17), Messages.ActionOpenFdProject_18);
				}
			}
			
			DictionaryParser parserDic = new DictionaryParser();
			try{
				dictionary = parserDic.parse(new FileInputStream(dictionaryFile.getLocation().toOSString()));
				for(AbstractParserException x : parserDic.getErrors()){
					errors.put(x, x.getMessage());
				}
			}catch(Exception e){
				e.printStackTrace();
				errors.put(e, Messages.ActionOpenFdProject_19);
				for(AbstractParserException x : parserDic.getErrors()){
					errors.put(x, x.getMessage());
				}
			}
			

			
			
			
			List<FdModel> fdModels = new ArrayList<FdModel>();
			FactoryStructure factoryStructure = new FactoryStructure();
			for(IFile mFile : modelFile){
				try{
					FdModelParser parser = new FdModelParser(factoryStructure, dictionary);
					parser.parse(new FileInputStream(mFile.getLocation().toOSString()));
					
					FdModel model = parser.getModel();
					
					if (model.getName().equals(parser.getDescriptor().getModelName())){
						fdProject = parser.getProject();
					}
					else{
						fdModels.add(model);
					}
					
					if (fdProject != null && !project.getName().equals(fdProject.getProjectDescriptor().getProjectName())){
						fdProject.getProjectDescriptor().setProjectName(project.getName());
						
						
						IFile f = project.getFile(fdProject.getFdModel().getName() + Messages.ActionOpenFdProject_20);
						XMLWriter writer = new XMLWriter(new FileOutputStream(f.getLocation().toOSString()), OutputFormat.createPrettyPrint());
						writer.write(fdProject.getFdModel().getElement());
						writer.close();
						mFile.delete(true, null);
					}
					
					
					
					for(Exception x : parser.getErrors().keySet()){
						errors.put(x, parser.getErrors().get(x));
					}
				}catch(Exception e){
					e.printStackTrace();
					errors.put(e, "Unable to parse structure"); //$NON-NLS-1$
					
					for(AbstractParserException x : parserDic.getErrors()){
						errors.put(x, x.getMessage());
					}
				}
			}
			
			for(FdModel m : fdModels){
				((MultiPageFdProject)fdProject).addPageModel(m);				
			}
			
			

			
			fdProject.setDictionary(dictionary);
			
			for(IFile f : propertiesFile){
				if (f.getName().contains("_") && f.getFileExtension().equals("properties")){
					String locale = f.getName().substring(0, f.getName().indexOf("_")); //$NON-NLS-1$
					fdProject.addResource(new FileProperties(f.getName(), locale, f.getLocation().toFile()));
					fdProject.addLocale(locale);
				}
				else if (f.getFileExtension().equals("properties")){
					fdProject.addResource(new FileProperties(f.getName(), null, f.getLocation().toFile()));
				}

				
			}
			
			for(IFile f : cssFiles){
				fdProject.addResource(new FileCSS(f.getName(), f.getLocation().toFile()));
				
			}
			
			for(IFile f : imageFiles){
				fdProject.addResource(new FileImage(f.getName(), f.getLocation().toFile()));
				
			}
			
			for(IFile f : javaScriptFiles){
				fdProject.addResource(new FileJavaScript(f.getName(), f.getLocation().toFile()));
				
			}
			
			if (!errors.isEmpty()){
				MultiStatus m = new MultiStatus(Activator.PLUGIN_ID, 0, Messages.ActionOpenFdProject_24, null);
				for(Exception e : errors.keySet()){
					m.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, errors.get(e), e));
				}
				ErrorDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), 
						Messages.ActionOpenFdProject_25, Messages.ActionOpenFdProject_26, m);
				
			}
			
			Properties props = VersionningManager.getInstance().getCheckoutInfos(new File(Platform.getInstallLocation().getURL().getPath() + "/" + fdProject.getProjectDescriptor().getProjectName()).getAbsolutePath());  //$NON-NLS-1$
			if (props != null){
				FdProject p = null;
				try{
					
					FdProjectRepositoryDescriptor pd = new FdProjectRepositoryDescriptor(fdProject.getProjectDescriptor());
					pd.setModelDirectoryItemId(Integer.parseInt(props.getProperty("directoryItemId"))); //$NON-NLS-1$
					p = new MultiPageFdProject(pd, fdProject.getFdModel(), fdProject.getDictionary());
					
				}catch(Exception ex){
					ex.printStackTrace();
					Activator.getDefault().openProject(fdProject);;
					return;
				}
				
				Activator.getDefault().openProject(p);
			}
			else{
				Activator.getDefault().openProject(fdProject);
			}
			
			
		
		
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		
	}
}
