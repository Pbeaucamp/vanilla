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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.fd.api.core.model.FdAndroidModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.parsers.DictionaryParser;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.nature.FdNature;
import bpm.fd.design.ui.wizard.pages.ProjectDefintionPage;

public class NewAndroidDashboardWizard extends Wizard implements INewWizard {
	public static final String DEFINITION_PAGE_NAME = "bpm.fd.design.ui.wizard.NewFdWizard.ModelDefinitionPage"; //$NON-NLS-1$
	public static final String DEFINITION_PAGE_DESCRIPTION = Messages.NewAndroidDashboardWizard_1;
	public static final String DEFINITION_PAGE_TITLE = Messages.NewAndroidDashboardWizard_2;

	
	
	private ProjectDefintionPage definitionPage;
	
	
	public NewAndroidDashboardWizard() {
		
	}

	@Override
	public boolean performFinish() {
		FdProjectDescriptor desc = definitionPage.getProjectDescriptor();
		
		FdProject project = new MultiPageFdProject(desc, new FdAndroidModel(new FactoryStructure()));
			
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
		
		IWorkspaceRoot r = workspace.getRoot();

		IProject p = r.getProject(desc.getProjectName());
	
		try {
			if (p.exists()){
				if (MessageDialog.openQuestion(getShell(), Messages.NewAndroidDashboardWizard_3, Messages.NewAndroidDashboardWizard_4)){
					p.delete(true, true, null);
				}
			}
			p.create(null);
			p.open(null);
			
			
			IProjectDescription pD = p.getDescription();
			pD.setNatureIds(new String[]{FdNature.ID});
			p.setDescription(pD, null);


			IFile f = p.getFile(desc.getModelName() + ".freedashboard"); //$NON-NLS-1$
			try {
				Document d = DocumentHelper.createDocument(project.getFdModel().getElement());
				f.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null);				 //$NON-NLS-1$
			} catch (Exception e) {
				throw new Exception (Messages.NewAndroidDashboardWizard_7, e);
			}
			
			/*
			 * create the componentProperties file
			 */
			IFile propF = p.getFile("components.properties"); //$NON-NLS-1$
			try {
				propF.create(new ByteArrayInputStream("".getBytes()), true, null); //$NON-NLS-1$
			}catch (Exception e) {
				throw new Exception (Messages.NewAndroidDashboardWizard_10, e);
			}
			FileProperties propR = new FileProperties("components.properties", null, propF.getLocation().toFile()); //$NON-NLS-1$
			project.addResource(propR);
			
			File dictionaryFile = definitionPage.getDictionaryFile();
			
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
					dicoFile.createLink(Platform.getLocation().append(dicoFile.getFullPath()), IResource.REPLACE, null);

				} catch (Exception e) {
					throw new Exception (Messages.NewAndroidDashboardWizard_13, e);
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
						 dicoFile = p.getFile( dic.getName());
						
					}
					if (xml == null){
						dicoFile.createLink(dictionaryFile.toURI(), IResource.REPLACE, null);
					}
					else{
						dicoFile.create(new FileInputStream(Platform.getLocation().toOSString() + dicoFile.getFullPath().toOSString()), true, null);
					}
				

					project.setDictionary(dic);

				} catch (Exception e) {
					throw new Exception (Messages.NewAndroidDashboardWizard_14, e);
				}
			}
			
						
			
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.openError(getShell(), Messages.NewAndroidDashboardWizard_15, e.getMessage(), 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID,e.getMessage(),e));
			return false;
		}
		Activator.getDefault().openProject(project);	
		return true;
		
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		definitionPage = new ProjectDefintionPage(DEFINITION_PAGE_NAME);
		definitionPage.setDescription(DEFINITION_PAGE_DESCRIPTION);
		definitionPage.setTitle(DEFINITION_PAGE_TITLE);
		addPage(definitionPage);
	}

}
