package bpm.sqldesigner.ui.view.tree;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.document.SchemaView;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.procedure.SQLProcedure;
import bpm.sqldesigner.api.model.view.SQLView;
import bpm.sqldesigner.ui.Activator;

public class TreeLabelProvider implements ILabelProvider {


	public Image getImage(Object element) {
		if (element instanceof Catalog) {
			Catalog catalog = (Catalog) element;
			Schema schemaTest = catalog.getSchema("null"); //$NON-NLS-1$
			if (schemaTest != null) {
//				if (SavedEditors.get(schemaTest) != null)
					return Activator.getDefault().getImageRegistry().get("databaseSave"); //$NON-NLS-1$
			}
			return Activator.getDefault().getImageRegistry().get("catalog"); //$NON-NLS-1$
		} else if (element instanceof Table){
			return Activator.getDefault().getImageRegistry().get("table"); //$NON-NLS-1$
		}
			
		else if (element instanceof DatabaseCluster) {
			DatabaseCluster databaseCluster = (DatabaseCluster) element;
			if (databaseCluster.getDatabaseConnection() == null){
				return Activator.getDefault().getImageRegistry().get("databasesMP");			 //$NON-NLS-1$
			}
			else{
				return Activator.getDefault().getImageRegistry().get("databases"); //$NON-NLS-1$
			}
	
		} else if (element instanceof SQLView){
			return Activator.getDefault().getImageRegistry().get("view"); //$NON-NLS-1$
		}else if (element instanceof DocumentSnapshot){
			return Activator.getDefault().getImageRegistry().get("schemaMP"); //$NON-NLS-1$
		}
		
			
		else if (element instanceof Schema){
//			if (SavedEditors.get((Schema) element) != null)
			return Activator.getDefault().getImageRegistry().get(
					"schemaSave"); //$NON-NLS-1$
//		else
//			return Activator.getDefault().getImageRegistry().get("schema");
		}

		else if (element instanceof Column){
			if (((Column) element).isPrimaryKey()){
				return Activator.getDefault().getImageRegistry().get("key"); //$NON-NLS-1$
			}		
			else{
				return Activator.getDefault().getImageRegistry().get("column"); //$NON-NLS-1$
			}
				
		}
			
		else if (element instanceof SQLProcedure){
			return Activator.getDefault().getImageRegistry().get("procedure"); //$NON-NLS-1$
		}
			
		else if (element instanceof DocumentSnapshot){
			
		}
		else if (element instanceof SchemaView){
			
		}
		return null;
	}

	
	public String getText(Object element) {
		return ((Node) element).getName();
	}

	
	public void addListener(ILabelProviderListener listener) {
	}

	
	public void dispose() {
	}

	
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	
	public void removeListener(ILabelProviderListener listener) {
	}

}