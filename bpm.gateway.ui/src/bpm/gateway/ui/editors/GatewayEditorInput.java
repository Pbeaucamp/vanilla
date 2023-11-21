package bpm.gateway.ui.editors;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.xml.sax.SAXException;

import bpm.gateway.core.Activator;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.GatewayDigester;
import bpm.gateway.ui.gef.model.ContainerPanelModel;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class GatewayEditorInput implements IEditorInput {

	private String name;
	private DocumentGateway doc = new DocumentGateway();
	private RepositoryItem item;
	
	
	private ContainerPanelModel container;
	
	/**
	 * create an Editor from a gateway file
	 * @param fileName
	 */
	public GatewayEditorInput(File file){
		doc.setRepositoryContext(bpm.gateway.ui.Activator.getDefault().getRepositoryContext());
		this.name = file.getAbsolutePath();
		rebuildModel();
	}
	
	public ContainerPanelModel getContainer() {
		return container;
	}
	
	public void setContainer(ContainerPanelModel container) {
		this.container = container;
	}
	
	public void setFile(File f){
		this.name = f.getAbsolutePath();
	}
	
	public RepositoryItem getDirectoryItem(){
		return item;
	}
	
	
	/**
	 * create an Editor from xml
	 * @param fileName
	 */
	public GatewayEditorInput(InputStream stream, String modelName, RepositoryItem item){
		rebuildModel(stream);
		name = modelName;
		this.item = item;
		doc.setRepositoryContext(bpm.gateway.ui.Activator.getDefault().getRepositoryContext());
	}
	
	public GatewayEditorInput(InputStream stream, RepositoryItem item){
		rebuildModel(stream);
		this.item = item;
		doc.setRepositoryContext(bpm.gateway.ui.Activator.getDefault().getRepositoryContext());
		
	}
	
	/**
	 * create an Editor from xml
	 * @param fileName
	 */
	public GatewayEditorInput(String modelXml, RepositoryItem item) throws Exception{
		rebuildModel(IOUtils.toInputStream(modelXml, "UTF-8")); //$NON-NLS-1$
//		name = doc.getName();
		this.item = item;
		doc.setRepositoryContext(bpm.gateway.ui.Activator.getDefault().getRepositoryContext());
		
	}
	
	/**
	 * create a new GateWayModel
	 * @param fileName
	 */
	public GatewayEditorInput(String fileName, DocumentGateway doc){
		this.name = fileName;
		this.doc = doc;
		doc.setRepositoryContext(bpm.gateway.ui.Activator.getDefault().getRepositoryContext());
	}
	
	
	public DocumentGateway getDocumentGateway(){
		return doc;
	}
	
	private void rebuildModel(){
		File f = new File(name);
		try {
			
			
			GatewayDigester dig = new GatewayDigester(f, Activator.getAdditionalDigesters());
			doc = dig.getDocument(bpm.gateway.ui.Activator.getDefault().getRepositoryContext());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void rebuildModel(InputStream stream){

		try {

			GatewayDigester dig = new GatewayDigester(stream, Activator.getAdditionalDigesters());
			doc = dig.getDocument(bpm.gateway.ui.Activator.getDefault().getRepositoryContext());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean exists() {
		return name != null;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return name;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return name;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

}
