package bpm.sqldesigner.ui.repositoryimport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IViewReference;

import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.xml.ReadData;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.view.TreeView;

public class ImportHelper {

	public static void importDwhView(String xmlModel) throws Exception{
		
		/*
		 * cluster Creation
		 */
		
		File tmpFolder = new File(Platform.getInstallLocation().getURL().getPath()+ "/temp"); //$NON-NLS-1$
		if (!tmpFolder.exists() || !tmpFolder.isDirectory()){
			tmpFolder.mkdirs();
		}
		File tempFile = new File(tmpFolder, "t_" + new Object().hashCode() + ".tmp"); //$NON-NLS-1$ //$NON-NLS-2$
		
		FileOutputStream fos = new FileOutputStream(tempFile);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(xmlModel.getBytes());
		
		byte[] BUF = new byte[1024];
		int sz = 0;
		while( (sz = bis.read(BUF)) != -1){
			fos.write(BUF, 0, sz);
		}
		
		bis.close();
		fos.close();
		
		DatabaseCluster cl = ReadData.readCatalogsList(tempFile.getAbsolutePath());
		cl.setName(cl.getProductName());
		DocumentSnapshot dwhView = cl.getDocumentSnapshots().get(0);
		
		
		DialogSnapshotImport d = new DialogSnapshotImport(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), dwhView);
		
		if (d.open() != DialogSnapshotImport.OK){
			return;
		}
		
		
		IViewReference ref = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findViewReference(TreeView.ID);
		
		HashMap<DatabaseCluster, DwhViewImportStrategie> startegies = d.getStrategies();
		
		if (startegies.isEmpty()){
			
//			((TreeView)ref.getView(false)).addDb(cl);
			Activator.getDefault().getWorkspace().addDatabaseCluster(cl);
			
		}
		else{
			for(DatabaseCluster cluster : startegies.keySet()){
				DwhViewImportStrategie strat = startegies.get(cluster);
				
				if (strat == DwhViewImportStrategie.OVERRIDE_MATCHING_DWH_VIEW){
					dwhView.setSchema(cluster.getCalalog(dwhView.getSchema().getCatalog().getName()).getSchema(dwhView.getSchema().getName()));
					dwhView.setDatabaseCluster(cluster);
					dwhView.rebuildLinks();
					
					try{
						cluster.addDocumentSnapshot(dwhView);
					}catch(Exception ex){
						for(DocumentSnapshot s : cluster.getDocumentSnapshots()){
							if (s.getName().equals(dwhView.getName())){
								cluster.removeDocumentSnapshot(s);
								break;
							}
						}
						cluster.addDocumentSnapshot(dwhView);
					}
				}
			}
		}
		
		tempFile.delete();
	}
}
