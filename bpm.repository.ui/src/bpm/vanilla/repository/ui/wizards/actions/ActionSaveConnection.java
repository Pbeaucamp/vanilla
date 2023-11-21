package bpm.vanilla.repository.ui.wizards.actions;


public class ActionSaveConnection{}
//extends Action {
//	private String path;
//	private Collection<IRepositoryApi> cons;
//	
//	public ActionSaveConnection(Collection<IRepositoryApi> con, String path){
//		this.cons = con;
//		this.path = path;
//		
//	}
//	
//	public void run(){
//		//save connections
//		try {
//			FileWriter fw;
//			fw = new FileWriter(path);
//			fw.write("<RepositoryConnections>"); //$NON-NLS-1$
//			
//			for(IRepositoryApi r : cons){
//				fw.write(r.getXML());
//			}
//			fw.write("</RepositoryConnections>"); //$NON-NLS-1$
//			fw.close();
//		} catch (IOException e) {
//			MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
//					Messages.ActionSaveConnection_2, Messages.ActionSaveConnection_3);
//			e.printStackTrace();
//		}
//		
//		
//	}
//
//}
