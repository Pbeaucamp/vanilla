package bpm.fd.design.ui.wizard.fmdt;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.filter.FilterData;
import bpm.fd.api.core.model.components.definition.filter.FilterRenderer;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.design.ui.Activator;
import bpm.metadata.birt.oda.runtime.ConnectionPool;
import bpm.metadata.digester.SqlQueryDigester;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.query.QuerySql;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;

public class FmdDataSetHelper {
	private static final String P_GROUP_NAME = "GROUP_NAME";
	private static final String P_PACKAGE= "BUSINESS_PACKAGE";
	private static final String P_MODEL= "BUSINESS_MODEL";
	
	
	private DataSource dataSource;
	String businessModelName ;
	String businessPackageName;
	String group ;
	
	public static class Options{
		boolean generateFilters;
		int filterRendererStyle = -1;
		public Options(boolean generateFilters, int filterRendererStyle) {
			super();
			this.generateFilters = generateFilters;
			this.filterRendererStyle = filterRendererStyle;
		}
		
	}
	
	public FmdDataSetHelper(DataSource dataSource){
		this.dataSource = dataSource;
		
		businessModelName = dataSource.getProperties().getProperty(P_MODEL);
		businessPackageName = dataSource.getProperties().getProperty(P_PACKAGE);
		group = dataSource.getProperties().getProperty(P_GROUP_NAME);
	}
	
	public IBusinessPackage getBusinessPackage() throws Exception{
		Collection<IBusinessModel> c = ConnectionPool.getConnection(dataSource.getProperties());
		for(IBusinessModel m : c){
			if (m.getName().equals(businessModelName)){
				for(IBusinessPackage p : m.getBusinessPackages(group)){
					if (p.getName().equals(businessPackageName)){
						return p;
					}
				}
			}
		}
		return null;
	}
	
	public Collection<IResource> getFmdtResources() throws Exception{
		return getBusinessPackage().getResources(group);
	}
	
	public IStatus generateParameterDataSet(DataSet dataSet, Options opts){
		
		IStatus result = null;

		IBusinessPackage fmdtPackage = null;
				
		
		SqlQueryDigester dig = null;
		QuerySql originalQuery = null;
		try{
			fmdtPackage = getBusinessPackage();
			dig =new SqlQueryDigester(IOUtils.toInputStream(dataSet.getQueryText(), "UTF-8"),
					group,
					fmdtPackage);
			originalQuery = dig.getModel();
		}catch(Exception ex){
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to parse FMDT query : " + ex.getMessage(), ex);
		}
		
		
		
		
		for(Prompt pt : originalQuery.getPrompts()){
			
			try{
				IDataStreamElement columnProvider = pt.getOrigin();
				
				QuerySql dsQuery = new QuerySql();
				dsQuery.getSelect().add(columnProvider);
				dsQuery.setDistinct(true);
				dsQuery.getOrderBy().add(columnProvider);
				
				
				
				DataSet ds = new DataSet(
						dataSet.getName() + "_" + pt.getName() + "_provider", 
						dataSet.getOdaExtensionDataSetId(), 
						dataSet.getOdaExtensionDataSourceId(), 
						new Properties(dataSet.getPublicProperties()), 
						new Properties(dataSet.getPrivateProperties()), 
						dsQuery.getXml(), 
						dataSource);
				

				
				ds.buildDescriptor(dataSource);
				Activator.getDefault().getProject().getDictionary().addDataSet(ds);
				Activator.getDefault().firePropertyChange(Activator.PROJECT_CONTENT, null, ds);
				
				if (opts.generateFilters){
					ComponentFilterDefinition c = new ComponentFilterDefinition(
							"Filter For " + pt.getName(),
							Activator.getDefault().getProject().getDictionary());
					
					
					c.setComment("Auto-generated filter to provide DataSet " + dataSet.getName() + "'s " + pt.getName() + " parameter values");
				
					c.setRenderer(FilterRenderer.getRenderer(opts.filterRendererStyle));
					
					FilterData datas = new FilterData();
					datas.setDataSet(ds);
					datas.setColumnLabelIndex(1);
					datas.setColumnOrderIndex(1);
					datas.setColumnValueIndex(1);
					
					c.setComponentDatas(datas);
					Activator.getDefault().getProject().getDictionary().addComponent(c);
					Activator.getDefault().firePropertyChange(Activator.PROJECT_CONTENT, null, c);
				}

			}catch(Exception ex){
				if (result == null){
					result = new MultiStatus(Activator.PLUGIN_ID, IStatus.WARNING, "Unable to create some object in the dictionary", null);
				}
				((MultiStatus)result).add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Problem on Prompt " + pt.getOutputName(Locale.getDefault()) + " : " + ex.getMessage(), ex));
			}
		}
		
		if (result == null){
			return Status.OK_STATUS;
		}
		else{
			return result;
		}
	}

	public IStatus convertResources(List<IResource> resources, Options opts) {
		IStatus result = null;
		
		for(IResource r : resources){
			try{
				QuerySql query = new QuerySql();
				
				
				if (r instanceof IFilter){
					query.getSelect().add(((IFilter)r).getOrigin());
					query.getFilters().add((IFilter)r);
					query.getOrderBy().add(((IFilter)r).getOrigin());
				}
				else if (r instanceof Prompt){
					query.getSelect().add(((Prompt)r).getOrigin());
					query.getOrderBy().add(((Prompt)r).getOrigin());
				}
				query.setDistinct(true);
				
				DataSet ds = new DataSet(
						dataSource.getName() + "_" + r.getName() + "_resource", 
						"bpm.metadata.birt.oda.runtime.dataSet", 
						"bpm.metadata.birt.oda.runtime", 
						new Properties(), 
						new Properties(), 
						query.getXml(), 
						dataSource);
				
				ds.buildDescriptor(dataSource);
				Activator.getDefault().getProject().getDictionary().addDataSet(ds);
				Activator.getDefault().firePropertyChange(Activator.PROJECT_CONTENT, null, ds);

				if (opts != null && opts.generateFilters){
					
					try{
						ComponentFilterDefinition c = new ComponentFilterDefinition(
								"Filter For Resource " + r.getName(),
								Activator.getDefault().getProject().getDictionary());
						
						
						c.setComment("Auto-generated filter from FMDT Resource  " + r.getName() );
					
						c.setRenderer(FilterRenderer.getRenderer(opts.filterRendererStyle));
						
						FilterData datas = new FilterData();
						datas.setDataSet(ds);
						datas.setColumnLabelIndex(1);
						datas.setColumnOrderIndex(1);
						datas.setColumnValueIndex(1);
						
						c.setComponentDatas(datas);
						Activator.getDefault().getProject().getDictionary().addComponent(c);
						Activator.getDefault().firePropertyChange(Activator.PROJECT_CONTENT, null, c);
					}catch(Exception ex){
						if (result == null){
							result = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, "Could not convert some FMDT resources ", null);
						}
						((MultiStatus)result).add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to create Filter component for FMDT Resource " + r.getOutputName(Locale.getDefault()) + " : " + ex.getMessage(), ex));
					}
					

				}
			}catch(Exception ex){
				if (result == null){
					result = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, "Could not convert some FMDT resources ", null);
				}
				((MultiStatus)result).add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to create Filter component for FMDT Resource " + r.getOutputName(Locale.getDefault()) + " : " + ex.getMessage(), ex));
			}
			
			
		}
		if (result == null){
			return Status.OK_STATUS;
		}
		return result;
	}
}
