package bpm.metadata.birt.contribution.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ActionLoadWorkspace implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	
	public void dispose() {
		

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;


	}

	public void run(IAction action) {

		
//		DialogImport d = new DialogImport(window.getShell());
//		if (d.open() == DialogImport.OK){
//			try {
//				IRepositoryConnection sock = d.getSock();
//				IDirectoryItem item = d.getDirectoryItem();
//				String xml = sock.loadModel(IRepositoryConnection.CUST_TYPE, item);
//				String fileName = d.getFileName();
//				if (!fileName.endsWith(".rptdesign")) 
//					fileName += ".rptdesign";
//				
//				EclipseHelper.createFile(d.getPath(), fileName, xml);
//				
//				LinkedDocumentAdministrator linkedManager = new LinkedDocumentAdministrator(
//						sock, new AdminAccess(d.getVanillaUrl()));
//				//linkedManager.get
//				List<LinkedDocument> docs = linkedManager.getLinkedDocument((DirectoryItem)item);
//				
//				LinkedDocumentFileStreamerClient client = new LinkedDocumentFileStreamerClient(
//						d.getVanillaUrl(), d.getRepositoryUrl());
//				
//				for (LinkedDocument doc : docs) {
//					IPath path = d.getPath();
//					IFile file = EclipseHelper.createEmptyFile(path, doc.getName() + doc.getFormat());
//					InputStream stream = client.writeLinkedDocument(d.getUserName(), d.getUserPassword(), d.getGroupName(), 
//							doc.getItemId(), doc.getId());
//					EclipseHelper.writeFile(file, stream);
//					//doc.g
//					//doc.getId();
//					//sock.
//					//sock.getRepository().getItem(doc.getId());
////					IDirectoryItem itm = sock.getRepository().getItem(doc.getId());
////					String txt = sock.loadModel(IRepositoryConnection.LINKED_FILE, itm);
//				}
//				
//				
//				
////				PrintWriter pw = new PrintWriter(d.getFileName(), "UTF-8");
////				
////				pw.write(d.getXml());
////				pw.close();
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			
//		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		

	}

}
