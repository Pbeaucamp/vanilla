package bpm.sqldesigner.ui.internal;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.IProgressMonitor;

import bpm.sqldesigner.api.database.ExtractData;
import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.document.SchemaView;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.xml.ReadData;
import bpm.sqldesigner.api.xml.SaveData;
import bpm.sqldesigner.ui.i18N.Messages;

public class WorkspaceSerializer {
	private static final String ROOT_TAG = "sqlDesignerWorkspace"; //$NON-NLS-1$
	private static final String CLUSTER_TAG = "databaseCluster"; //$NON-NLS-1$
	private static final String CLUSTER_FILE_NAME = "fileName"; //$NON-NLS-1$
	private static final String DWH_VIEW = "dwhView"; //$NON-NLS-1$
	private static final String SCHEMA_VIEW = "schemaView"; //$NON-NLS-1$
	private static final String NAME_ATT = "name"; //$NON-NLS-1$
	
	public static void save(OutputStream output, Workspace workspace, IProgressMonitor progressMonitor) throws Exception{
		
		progressMonitor.beginTask(Messages.WorkspaceSerializer_6, workspace.getOpenedClusters().size() + 1);
		
		int count = 0;
		for(DatabaseCluster cluster : workspace.getOpenedClusters()){
			
			try{
				if (cluster.isNotFullLoaded()){
					progressMonitor.subTask(Messages.WorkspaceSerializer_7 + cluster.getName());
					ExtractData.extractWhenNotLoaded(cluster);
					
				}
				progressMonitor.subTask(Messages.WorkspaceSerializer_8 + cluster.getName() + Messages.WorkspaceSerializer_9 + cluster.getFileName());
				SaveData.saveDatabaseCluster(cluster, cluster.getFileName(), SaveData.SAVE_VIEWS | SaveData.SAVE_LAYOUT | SaveData.SAVE_PROCEDURES);
				progressMonitor.internalWorked(count++);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		
		
		progressMonitor.subTask(Messages.WorkspaceSerializer_10 + workspace.getFileName());
		Document doc = DocumentHelper.createDocument();
		Element root = DocumentHelper.createElement(ROOT_TAG);
		doc.setRootElement(root);
		
		
		for(DatabaseCluster cluster : workspace.getOpenedClusters()){
			Element eC = root.addElement(CLUSTER_TAG);
			eC.addAttribute(CLUSTER_FILE_NAME, cluster.getFileName());
			
			for(DocumentSnapshot snap : workspace.getOpenedDocumentSnapshots()){
				if (snap.getDatabaseCluster() == cluster){
					eC.addElement(DWH_VIEW).addAttribute(NAME_ATT, snap.getName());
				}
			}
			
			for(SchemaView v : workspace.getOpenedSchemaViews()){
				if (v.getCluster() == cluster){
					eC.addElement(SCHEMA_VIEW).addAttribute(NAME_ATT, v.getName());
				}
			}
			
		}
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8"); //$NON-NLS-1$
		format.setTrimText(false);
		
		XMLWriter writer = null;
		
		Exception ex = null;
		try{
			writer = new XMLWriter(output, format);
			writer.write(doc);
			writer.close();
		}catch (Exception e) {
			ex = e;
		
		}finally {
			if (writer != null){
				writer.close();
			}
		}
		progressMonitor.internalWorked(count++);
		progressMonitor.done();
		if (ex != null){
			throw ex;
		}
		
	}


	public static Workspace load(InputStream input) throws Exception{
		SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(input);
		
		Element root = doc.getRootElement();
		
		Workspace wks = new Workspace();
		
		
		
		
		for(Element eC : (List<Element>)root.elements(CLUSTER_TAG)){
			
			try{
				DatabaseCluster cluster = ReadData.readCatalogsList(eC.attributeValue(CLUSTER_FILE_NAME));
				Date date = cluster.getDate();
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
				


				wks.addDatabaseCluster(cluster);
				
				for(Element e : (List<Element>)eC.elements(SCHEMA_VIEW)){
					for(SchemaView d : cluster.getSchemaViews()){
						if (d.getName().equals(e.attributeValue(NAME_ATT))){
							wks.addSchemaView(d);
											
							break;
						}
					}
					
				}
				
				
				for(Element e : (List<Element>)eC.elements(DWH_VIEW)){
					for(DocumentSnapshot d : cluster.getDocumentSnapshots()){
						if (d.getName().equals(e.attributeValue(NAME_ATT))){
							
							wks.addSnapshot(d);
							
							
							break;
						}
					}
					
				}
				
				
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			
			
			
		}
		
		return wks;
	}
}
