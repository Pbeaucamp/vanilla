package bpm.gateway.ui.dwh.importer.wizard;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.core.runtime.Platform;

import bpm.gateway.ui.Activator;
import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.xml.ReadData;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ImporterHelper {

	
	public static DocumentSnapshot loadDwhView(RepositoryItem item) throws Exception{
		
		/*
		 * load xml
		 */
		String xml = null;
		try {
			xml = Activator.getDefault().getRepositoryConnection().getRepositoryService().loadModel(item);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		/*
		 * store Xml
		 */
		
		File tempFile = new File(Platform.getInstallLocation().getURL().getPath()+ "/temp/t_" + new Object().hashCode() + ".tmp");
		
		FileOutputStream fos = new FileOutputStream(tempFile);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
		
		byte[] BUF = new byte[1024];
		int sz = 0;
		while( (sz = bis.read(BUF)) != -1){
			fos.write(BUF, 0, sz);
		}
		
		bis.close();
		fos.close();
		
		/*
		 * rebuild DwhView
		 */
		DatabaseCluster cl = ReadData.readCatalogsList(tempFile.getAbsolutePath());
		cl.setName(cl.getProductName());
		DocumentSnapshot dwhView = cl.getDocumentSnapshots().get(0);
		
		
		return dwhView;
	}
}
