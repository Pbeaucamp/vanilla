package bpm.metadata.client.security.viewers;

import metadata.client.trees.TreeJoin;
import metadata.client.trees.TreeLov;
import metadata.client.trees.TreeModel;
import metadata.client.trees.TreePackage;
import metadata.client.trees.TreeResource;
import metadataclient.Activator;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.metadata.resource.complex.FmdtDimension;

public class FMDTLabelProvider extends LabelProvider{
	@Override
	public String getText(Object element) {
		if (element instanceof IDataStream){
			return ((IDataStream)element).getName();
		}
		if (element instanceof IDataStreamElement){
			return ((IDataStreamElement)element).getName();
		}
		if (element instanceof IConnection){
			return((IConnection)element).getName();
		}
		
		if (element instanceof AbstractDataSource){
			return((AbstractDataSource)element).getName();
		}
		if (element instanceof IBusinessModel){
			return ((BusinessModel)element).getName();
		}
		if (element instanceof IBusinessPackage){
			return ((IBusinessPackage)element).getName();
		}
		if (element instanceof IBusinessTable){
			return ((IBusinessTable)element).getName();
		}
		if (element instanceof IResource){
			return ((IResource)element).getName();
		}
		
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object obj) {
		if (obj instanceof IBusinessTable){
			return Activator.getDefault().getImageRegistry().get("bus_table"); //$NON-NLS-1$
		}
		else if (obj instanceof IDataStream){
			return Activator.getDefault().getImageRegistry().get("log_table"); //$NON-NLS-1$
		}
		else if (obj instanceof IDataStreamElement){
			return Activator.getDefault().getImageRegistry().get("log_column"); //$NON-NLS-1$
		}


		else if (obj instanceof IDataSource){
			return Activator.getDefault().getImageRegistry().get("datasource"); //$NON-NLS-1$
		}
		else if (obj instanceof IBusinessPackage){
			return Activator.getDefault().getImageRegistry().get("package"); //$NON-NLS-1$
		}
		else if (obj instanceof IBusinessModel){
			return Activator.getDefault().getImageRegistry().get("model"); //$NON-NLS-1$
		}

		else if (obj instanceof ListOfValue){
			return Activator.getDefault().getImageRegistry().get("lov"); //$NON-NLS-1$
		}
		else if (obj instanceof IResource){
			if (obj instanceof Prompt){
				return Activator.getDefault().getImageRegistry().get("prompt"); //$NON-NLS-1$
			}
			if (obj instanceof ComplexFilter){
				return Activator.getDefault().getImageRegistry().get("filter_complex"); //$NON-NLS-1$	
			}
			if (obj instanceof ListOfValue){
				return Activator.getDefault().getImageRegistry().get("lov"); //$NON-NLS-1$
			}
			if (obj instanceof SqlQueryFilter){
				return Activator.getDefault().getImageRegistry().get("filter_sql"); //$NON-NLS-1$
			}
			if (obj instanceof Filter){
				return Activator.getDefault().getImageRegistry().get("filter"); //$NON-NLS-1$
			}
			
			
			
		}
		return Activator.getDefault().getImageRegistry().get("object"); //$NON-NLS-1$
	}
}
