package bpm.fd.design.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.wizard.pages.ProjectDefintionPage;

public abstract class NewFdWizard extends Wizard implements INewWizard {
	public static final String DEFINITION_PAGE_NAME = "bpm.fd.design.ui.wizard.NewFdWizard.ModelDefinitionPage"; //$NON-NLS-1$
	public static final String DEFINITION_PAGE_DESCRIPTION = Messages.NewFdWizard_1;
	public static final String DEFINITION_PAGE_TITLE = Messages.NewFdWizard_2;

	
	
	protected ProjectDefintionPage definitionPage;
	
	
	public NewFdWizard() {
		
	}

//	@Override
//	public abstract boolean performFinish() {
//		FdProjectDescriptor desc = definitionPage.getProjectDescriptor();
//		
//		FdProject project = new FdProject(desc);
//			
//		
//		IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
//		
//		IWorkspaceRoot r = workspace.getRoot();
//
//		IProject p = r.getProject(desc.getProjectName());
//	
//		try {
//			if (p.exists()){
//				if (MessageDialog.openQuestion(getShell(), Messages.NewFdWizard_3, Messages.NewFdWizard_4)){
//					p.delete(true, true, null);
//				}
//			}
//			p.create(null);
//			p.open(null);
//			
//			
//			IProjectDescription pD = p.getDescription();
//			pD.setNatureIds(new String[]{FdNature.ID});
//			p.setDescription(pD, null);
//
//
//			IFile f = p.getFile(desc.getModelName() + ".freedashboard"); //$NON-NLS-1$
//			try {
//				Document d = DocumentHelper.createDocument(project.getFdModel().getElement());
//				f.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null); //$NON-NLS-1$
//			} catch (Exception e) {
//				throw new Exception (Messages.NewFdWizard_7, e);
//			}
//			
//			/*
//			 * create the componentProperties file
//			 */
//			IFile propF = p.getFile("components.properties"); //$NON-NLS-1$
//			try {
//				propF.create(new ByteArrayInputStream("".getBytes()), true, null); //$NON-NLS-1$
//			}catch (Exception e) {
//				throw new Exception (Messages.NewFdWizard_10, e);
//			}
//			FileProperties propR = new FileProperties("components.properties", null, propF.getLocation().toFile()); //$NON-NLS-1$
//			project.addResource(propR);
//			
//			File dictionaryFile = definitionPage.getDictionaryFile();
//			
//			//comming from repository or new Dictionary
//			if (dictionaryFile == null){
//				
//				IFile dicoFile = p.getFile( desc.getDictionaryName() + ".dictionary"); //$NON-NLS-1$
////				dicoFile.create(new FileInputStream(Platform.getLocation().toOSString() + dicoFile.getFullPath().toOSString()), true, null);
//				try {
//					
//					
//					
//					Document d = null;
//					if (definitionPage.getXml() != null){
//						d = DocumentHelper.parseText(definitionPage.getXml());
//					}
//					else{
//						d = DocumentHelper.createDocument(project.getDictionary().getElement());
//						project.setDictionary(new DictionaryParser().parse(d));
//						project.getProjectDescriptor().setDictionaryName(project.getDictionary().getName());
//					}
//					
//					dicoFile.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null); //$NON-NLS-1$
//					XMLWriter w = null;
//					
//					try{
//						w = new XMLWriter(new FileOutputStream(Platform.getLocation().toOSString() + dicoFile.getFullPath().toOSString()), OutputFormat.createPrettyPrint());
//					}catch(Exception ex){
//						w = new XMLWriter(new FileOutputStream(Platform.getLocation().toOSString() + dicoFile.getFullPath().toOSString()), OutputFormat.createPrettyPrint());
//					}
//					w.write(d);
//					w.close();
//
//					Dictionary dic = new DictionaryParser().parse(d);
//					project.setDictionary(dic);
//					
////					dicoFile.createLink(new Path(p.getName() + "/" + dicoFile.getName()), IResource.REPLACE, null);
//
//				} catch (Exception e) {
//					throw new Exception (Messages.NewFdWizard_14, e);
//				}
//
//			}
//			else{
//				
//				try {
//					String xml = definitionPage.getXml();
//					Dictionary dic = null;
//					IFile dicoFile = null;
//					
//					if (xml != null){
//						 dic = new DictionaryParser().parse(IOUtils.toInputStream(xml));
//						 dicoFile = p.getFile( dic.getName());
//						 XMLWriter w = null;
//							
//						try{
//							Document d = DocumentHelper.parseText(xml);
//							w = new XMLWriter(new FileOutputStream(dicoFile.getLocation().toOSString()), OutputFormat.createPrettyPrint());
//							w.write(d);
//							w.close();
//						}catch(Exception ex){
//							w = new XMLWriter(new FileOutputStream(Platform.getLocation().toOSString() + dicoFile.getFullPath().toOSString()), OutputFormat.createPrettyPrint());
//						}
//					}
//					else{
//						 dic = new DictionaryParser().parse(new FileInputStream(dictionaryFile));
//						 dicoFile = p.getFile( dic.getName()+ ".dictionary"); //$NON-NLS-1$
////						 XMLWriter w = null;
////							
////							try{
////								Document d = DocumentHelper.parseText(xml);
////								w = new XMLWriter(new FileOutputStream(dicoFile.getLocation().toOSString()), OutputFormat.createPrettyPrint());
////								w.write(d);
////								w.close();
////							}catch(Exception ex){
////								w = new XMLWriter(new FileOutputStream(Platform.getLocation().toOSString() + dicoFile.getFullPath().toOSString()), OutputFormat.createPrettyPrint());
////							}
//					}
//					
//					//means that dictionary havent been uploaded from resitory
//					//and stored in the project location
//					//but coming from an existing filesystem file
//					if (xml == null){
//						dicoFile.createLink(dictionaryFile.toURI(), IResource.REPLACE, null);
//					}
//					else{
//						dicoFile.create(new FileInputStream(Platform.getLocation().toOSString() + dicoFile.getFullPath().toOSString()), true, null);
//					}
//					
//
//					project.setDictionary(dic);
//
//				} catch (Exception e) {
//					throw new Exception (Messages.NewFdWizard_16, e);
//				}
//			}
//			
//			String cssFileName = project.getProjectDescriptor().getProjectName() + ".css";
//			IFile cssFile = p.getFile(cssFileName);
//			try{
//				
//				File physicalFile = new File(cssFile.getLocation().toOSString() ); //$NON-NLS-1$
//				physicalFile.createNewFile();
//				PrintWriter pw = new PrintWriter(physicalFile, "UTF-8"); //$NON-NLS-1$
//				pw.write("._loading{background-color: #000;opacity: 0.5;filter: literal(\"alpha(opacity=50)\");z-index: 4;background:url(../../freedashboardRuntime/js/wait/loadingBig.gif) no-repeat center center;}\n");
//				pw.write(".cell{overflow:auto;}\n");
//				pw.close();
//				
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			
//			cssFile.createLink(Platform.getLocation().append(cssFile.getFullPath()), org.eclipse.core.resources.IResource.REPLACE, null);
//			
//			FileCSS cssF = new FileCSS(cssFileName, cssFile.getLocation().toFile());
//			project.addResource(cssF);
//	
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			ErrorDialog.openError(getShell(), Messages.NewFdWizard_17, e.getMessage(), 
//					new Status(IStatus.ERROR, Activator.PLUGIN_ID,e.getMessage(),e));
//			return false;
//		}
//		Activator.getDefault().openProject(project);	
//		return true;
//		
//	}

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
