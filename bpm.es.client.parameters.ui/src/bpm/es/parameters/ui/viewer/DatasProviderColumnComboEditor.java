package bpm.es.parameters.ui.viewer;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import bpm.es.parameters.ui.Activator;
import bpm.es.parameters.ui.Messages;
import bpm.es.parameters.ui.views.DatasProviderHelper;

public class DatasProviderColumnComboEditor extends ComboBoxCellEditor{

	private DatasProviderHelper helper;
	
	
	
	public DatasProviderColumnComboEditor(Composite parent, DatasProviderHelper helper) {
		super(parent, new String[]{}, SWT.READ_ONLY);
		this.helper = helper;
	}

	public void setDataProvider(final int providerId){
		
		BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
			@Override
			public void run() {
				Exception error = null;
				try {
					loadQueryItems(helper.getQuery(providerId));
				} catch (Exception e) {
					error = e;
				}
				
				if (error != null){
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.DatasProviderColumnComboEditor_0, Messages.DatasProviderColumnComboEditor_1 + error.getMessage());
				}
				
			}
		});
		
//		IRunnableWithProgress r = new IRunnableWithProgress() {
//			
//			@Override
//			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
//				Exception error = null;
//				monitor.beginTask("Loading ODA Query Result Metadata", 1);
//				try {
//					loadQueryItems(helper.getQuery(providerId));
//				} catch (Exception e) {
//					error = e;
//				}
//				
//				if (error != null){
//					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", "Error when gathering ODa Query Result structure : \n" + error.getMessage());
//				}
//			}
//		};
//		
//		
//		IProgressService service = PlatformUI.getWorkbench().getProgressService();
//	     try {
//	    	 service.run(false, false, r);
//	        	 
//	     } catch (InvocationTargetException ex) {
//	        ex.printStackTrace();
//	     } catch (InterruptedException ex) {
//	        ex.printStackTrace();
//	     }
		
	}
	
	

	
	private void loadQueryItems(IQuery query){
		
		
		try{
			IResultSetMetaData rsmd = query.getMetaData();
			String[] items = new String[rsmd.getColumnCount()];
			
			for(int i = 0; i < items.length; i++){
				items[i] = rsmd.getColumnLabel(i + 1);
			}
			setItems(items);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
}
