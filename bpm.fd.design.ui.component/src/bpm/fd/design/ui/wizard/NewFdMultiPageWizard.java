package bpm.fd.design.ui.wizard;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.parsers.DictionaryParser;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.nature.FdNature;
import bpm.fd.design.ui.wizard.pages.PagesPage;

public class NewFdMultiPageWizard extends NewFdWizard{

	private PagesPage modelsPage;
	
	
	protected MultiPageFdProject createFdProject(FdProjectDescriptor desc){
		return new MultiPageFdProject(desc);
	}
	
	@Override
	public boolean performFinish() {
		FdProjectDescriptor desc = definitionPage.getProjectDescriptor();
		
		MultiPageFdProject project = createFdProject(desc);
			
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
		
		IWorkspaceRoot r = workspace.getRoot();

		IProject p = r.getProject(desc.getProjectName());
	
		try {
			if (p.exists()){
				if (MessageDialog.openQuestion(getShell(), Messages.NewFdMultiPageWizard_0, Messages.NewFdMultiPageWizard_1)){
					p.delete(true, true, null);
				}
			}
			p.create(null);
			p.open(null);
			
			
			IProjectDescription pD = p.getDescription();
			pD.setNatureIds(new String[]{FdNature.ID});
			p.setDescription(pD, null);


			
			
			/*
			 * create the componentProperties file
			 */
			IFile propF = p.getFile("components.properties"); //$NON-NLS-1$
			try {
				propF.create(new ByteArrayInputStream("".getBytes()), true, null); //$NON-NLS-1$
			}catch (Exception e) {
				throw new Exception (Messages.NewFdMultiPageWizard_7, e);
			}
			FileProperties propR = new FileProperties("components.properties", null, propF.getLocation().toFile()); //$NON-NLS-1$
			project.addResource(propR);
			
			for(String s : modelsPage.getNames().keySet()){
				
				IFile _f = p.getFile(s + ".freedashboard"); //$NON-NLS-1$
				try {
					FdModel m = new FdModel(project.getFdModel().getStructureFactory(), project, s);
					Document d = DocumentHelper.createDocument(m.getElement());
					_f.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null); //$NON-NLS-1$
					project.addPageModel(m);
				} catch (Exception e) {
					throw new Exception (Messages.NewFdMultiPageWizard_11, e);
				}
			}
			
			File dictionaryFile = definitionPage.getDictionaryFile();
			
			//comming from repository or new Dictionary
			if (dictionaryFile == null){
				
				IFile dicoFile = p.getFile( desc.getDictionaryName() + ".dictionary"); //$NON-NLS-1$
			
				try {
					Document d = null;
					if (definitionPage.getXml() != null){
						d = DocumentHelper.parseText(definitionPage.getXml());
					}
					else{
						d = DocumentHelper.createDocument(project.getDictionary().getElement());
						project.setDictionary(new DictionaryParser().parse(d));
						project.getProjectDescriptor().setDictionaryName(project.getDictionary().getName());
					}
					dicoFile.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null); //$NON-NLS-1$
					
					XMLWriter w = null;
					
					try{
						w = new XMLWriter(new FileOutputStream(Platform.getLocation().toOSString() + dicoFile.getFullPath().toOSString()), OutputFormat.createPrettyPrint());
					}catch(Exception ex){
						w = new XMLWriter(new FileOutputStream(Platform.getLocation().toOSString() + dicoFile.getFullPath().toOSString()), OutputFormat.createPrettyPrint());
					}
					w.write(d);
					w.close();

					Dictionary dic = new DictionaryParser().parse(d);
					project.setDictionary(dic);
//					f.create(new FileInputStream(Platform.getLocation().toOSString() + dicoFile.getFullPath().toOSString()), true, null);
				} catch (Exception e) {
					throw new Exception (Messages.NewFdMultiPageWizard_14, e);
				}

			}
			else{
				
				try {
					String xml = definitionPage.getXml();
					Dictionary dic = null;
					IFile dicoFile = null;
					
					if (xml != null){
						 dic = new DictionaryParser().parse(IOUtils.toInputStream(xml));
						 dicoFile = p.getFile( dic.getName());
						 XMLWriter w = null;
							
						try{
							Document d = DocumentHelper.parseText(xml);
							w = new XMLWriter(new FileOutputStream(dicoFile.getLocation().toOSString()), OutputFormat.createPrettyPrint());
							w.write(d);
							w.close();
						}catch(Exception ex){
							w = new XMLWriter(new FileOutputStream(Platform.getLocation().toOSString() + dicoFile.getFullPath().toOSString()), OutputFormat.createPrettyPrint());
						}
					}
					else{
						 dic = new DictionaryParser().parse(new FileInputStream(dictionaryFile));
						 dicoFile = p.getFile( dic.getName()+ ".dictionary"); //$NON-NLS-1$
					}
					
					//means that dictionary havent been uploaded from resitory
					//and stored in the project location
					//but coming from an existing filesystem file
					if (xml == null){
						dicoFile.createLink(dictionaryFile.toURI(), IResource.REPLACE, null);
					}
					else{
						dicoFile.create(new FileInputStream(Platform.getLocation().toOSString() + dicoFile.getFullPath().toOSString()), true, null);
					}
					

					project.setDictionary(dic);

				} catch (Exception e) {
					throw new Exception (Messages.NewFdMultiPageWizard_16, e);
				}
			}
			String cssFileName = project.getProjectDescriptor().getProjectName() + ".css";
			IFile cssFile = p.getFile(cssFileName);
			FileInputStream fis = new FileInputStream(Platform.getInstallLocation().getURL().getPath() + "/resources/css/multi_page.css"); //$NON-NLS-1$
			
			cssFile.create(fis, true, null);
		
			FileCSS cssF = new FileCSS(cssFileName, cssFile.getLocation().toFile()); //$NON-NLS-1$
			project.addResource(cssF);
			
			
			//create the Folder
			Folder folder = null;
			
			int i = 1;
			for(FdModel mp : project.getPagesModels()){
				if (!modelsPage.getNames().get(mp.getName())){
					continue;
				}
				
				if (folder == null){
					folder = project.getFdModel().getStructureFactory().createFolder("folder");
					project.getFdModel().addToContent(folder);
				}
				
				FolderPage fp = project.getFdModel().getStructureFactory().createFolderPage(mp.getName());
				fp.setTitle(mp.getName());
				fp.addToContent(mp);
				folder.addToContent(fp);
				
				i++;
			}
			
			IFile f = p.getFile(desc.getModelName() + ".freedashboard"); //$NON-NLS-1$
			try {
				Document d = DocumentHelper.createDocument(project.getFdModel().getElement());
				f.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null); //$NON-NLS-1$
			} catch (Exception e) {
				throw new Exception (Messages.NewFdMultiPageWizard_4, e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.openError(getShell(), Messages.NewFdMultiPageWizard_20, e.getMessage(), 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID,e.getMessage(),e));
			return false;
		}
		Activator.getDefault().openProject(project);	
		return true;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		modelsPage = new PagesPage("modelPages"); //$NON-NLS-1$
		modelsPage.setTitle(Messages.NewFdMultiPageWizard_22);
		modelsPage.setDescription(Messages.NewFdMultiPageWizard_23);
		addPage(modelsPage);
	}
}
