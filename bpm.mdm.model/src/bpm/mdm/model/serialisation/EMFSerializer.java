package bpm.mdm.model.serialisation;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.datatools.connectivity.oda.design.DesignPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import bpm.mdm.model.MdmPackage;
import bpm.mdm.model.Model;

public class EMFSerializer {

	private ResourceSet resourceSet;
	private MdmConfiguration conf;
	
	
	public EMFSerializer(MdmConfiguration conf){
		this.conf = conf;
		MdmPackage.eINSTANCE.eClass();
		DesignPackage.eINSTANCE.eClass();
		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(MdmConfiguration.MDM_FILE_SUFFIX, 
				new XMLResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(MdmConfiguration.MDM_ENTITY_FILE_SUFFIX, 
				new XMLResourceFactoryImpl());
	}
	
	public void save(Model model)throws Exception{
		
		Resource resource = resourceSet.getResource(URI.createFileURI(conf.getMdmPersistanceFolderName() + "/mdm.xml"), false);
		if (resource == null){
			save(new FileOutputStream(conf.getMdmPersistanceFolderName() + "/mdm.xml"), model);
		}
		else{
			resource.getContents().clear();
			resource.getContents().add(model);
			resource.save(null);
		}
		
	}
	
	public void save(OutputStream os, Model model)throws Exception{
		Resource resource = new XMLResourceImpl(); 
		
//		Resource resource = resourceSet.getResource(URI.createFileURI(conf.getMdmPersistanceFolderName() + "/mdm.xml"), true);
		resource.getContents().add(model);
		resource.save(os, null);
	}
	
	public Model loadModel(InputStream is)throws Exception{
		URI u = URI.createGenericURI("mdm", "stream", "");
		System.setProperty("org.eclipse.emf.common.util.URI.archiveSchemes", "jar zip http ftp mdm");
//		resourceSet.getResource(u, true)
//		Resource resource = resourceSet.getResource(u, true);
		Resource resource = new XMLResourceImpl(); 
		resource.load(is, null);
		return (Model)resource.getContents().get(0);
	}
	
	public Model loadModel()throws Exception{
		Resource resource = resourceSet.getResource(URI.createFileURI(conf.getMdmPersistanceFolderName() + "/mdm.xml"), true);
		resource.load(null);
		return (Model)resource.getContents().get(0);
	}
	
	

}
