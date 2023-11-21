package bpm.es.parameters.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.dataprovider.odainput.OdaInputDigester;
import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.es.parameters.ui.Activator;
import bpm.es.parameters.ui.Messages;
import bpm.vanilla.platform.core.beans.data.OdaInput;
import bpm.vanilla.platform.core.repository.DatasProvider;
import bpm.vanilla.platform.core.repository.ILinkedParameter;
import bpm.vanilla.platform.core.repository.Parameter;


public class DatasProviderHelper {
	private  HashMap<DatasProvider, IQuery> loadedDatasProviders = new LinkedHashMap<DatasProvider, IQuery>();
	
	public  Collection<DatasProvider> getDatasProviders(){
		return loadedDatasProviders.keySet();
	}
	
	
	public void refresh() throws Exception{
		
		
		for(IQuery q : loadedDatasProviders.values()){
			if (q == null){
				continue;
			}
			try {
				q.close();
			} catch (OdaException e) {
				e.printStackTrace();
			}
		}
		loadedDatasProviders.clear();
		
		
		
		for(DatasProvider p : Activator.getDefault().getRepositorySocket().getDatasProviderService().getAll()){
			loadedDatasProviders.put(p, null);
		}
	}
	
	
	/**
	 * 
	 * @param dataProviderId
	 * @return
	 * @throws Exception is the dataProvider with given id doesnt exist
	 */
	public IQuery getQuery(int dataProviderId) throws Exception{
		for(DatasProvider p : loadedDatasProviders.keySet()){
			if (p.getId() == dataProviderId){
				IQuery q = loadedDatasProviders.get(p);
				
				if (q == null){
					return load(p);
				}
				return q;
			}
		}
		return null;
	}
	
	public DatasProvider getDatasProvider(int id){
		for(DatasProvider dp : loadedDatasProviders.keySet()){
			if (dp.getId() == id){
				return dp;
			}
		}
		
		return null;
	}
	
	
	private void refreshRequestedParameters(Parameter param, IQuery q) throws Exception{
		
		IParameterMetaData pmd = q.getParameterMetaData();
		
		if (pmd.getParameterCount() == param.getRequestecParameters().size()){
			return;
		}
		else if (pmd.getParameterCount() > param.getRequestecParameters().size()){
			for(int i = param.getRequestecParameters().size(); i < pmd.getParameterCount(); i++){
				ILinkedParameter n = new ILinkedParameter();
				if (pmd.getParameterName(i + 1) != null && ! "".equals(pmd.getParameterName(i + 1))){ //$NON-NLS-1$
					n.setInternalParameterName(pmd.getParameterName(i + 1));
				}
				else{
					n.setInternalParameterName("parameter_" + i); //$NON-NLS-1$
				}
				n.setInternalParameterPosition(i + 1);
				param.addRequestedParameter(n);
			}
		}
		else{
			List<ILinkedParameter> lp =  new ArrayList<ILinkedParameter>(param.getRequestecParameters());
			
			for(int i = pmd.getParameterCount(); i < lp.size(); i++){
				param.removeRequestedParameter(lp.get(i));
			}
		}
		
		
	}
	
	
	private IQuery load(DatasProvider d) throws Exception{
		OdaInputDigester dig = null;
		try {
			dig = new OdaInputDigester(d.getXmlDataSourceDefinition());
		} catch (Exception e1) {
			throw new Exception(Messages.DatasProviderHelper_2 + e1.getMessage(), e1);
		} 
		OdaInput input = dig.getOdaInput();
		
		try {
			
			IQuery query = QueryHelper.buildquery(input);
			loadedDatasProviders.put(d, query);
			return query;
		} catch (Exception e1) {
				throw new Exception(Messages.DatasProviderHelper_3 + e1.getMessage(), e1);
		} 
	}


	private void reInitColumnNames(Parameter p) throws Exception{
		if (p.getDataProviderId() > 0 ){
			IQuery q = getQuery(p.getDataProviderId());
			
			
			q.clearInParameters();
			IParameterMetaData m = q.getParameterMetaData();
			for(int i = 1; i <= m.getParameterCount(); i++){
				try{
					q.setString(i, ""); //$NON-NLS-1$
					Logger.getLogger(getClass().getName()).info("parameter " + i + " set"); //$NON-NLS-1$ //$NON-NLS-2$
				}catch(Exception ex){
					Logger.getLogger(getClass().getName()).warning("initColumnNames " + ex.getMessage()); //$NON-NLS-1$
				}
			}
			Logger.getLogger(getClass().getName()).info("parameters set"); //$NON-NLS-1$
			try{
				IResultSetMetaData rsmd = q.getMetaData();
				
				if (p.getValueColumnIndex() > 0 && (p.getValueColumnName() == null || p.getValueColumnName().equals(""))){ //$NON-NLS-1$
					String s = rsmd.getColumnLabel(p.getValueColumnIndex());
					
					if (s == null |  "".equals(s)){ //$NON-NLS-1$
						s = rsmd.getColumnName(p.getValueColumnIndex());
					}
					if (s == null |  "".equals(s)){ //$NON-NLS-1$
						s = "column_" + p.getValueColumnIndex(); //$NON-NLS-1$
					}
					
					p.setValueColumnName(s);
				}
				
				if (p.getLabelColumnIndex() > 0 && (p.getLabelColumnName() == null || p.getLabelColumnName().equals(""))){ //$NON-NLS-1$
					String s = rsmd.getColumnLabel(p.getLabelColumnIndex());
					
					if (s == null |  "".equals(s)){ //$NON-NLS-1$
						s = rsmd.getColumnName(p.getLabelColumnIndex());
					}
					if (s == null |  "".equals(s)){ //$NON-NLS-1$
						s = "column_" + p.getLabelColumnIndex(); //$NON-NLS-1$
					}
					
					p.setLabelColumnName(s);
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			
			
		}
		
	}


	private void reInitParameterName(ILinkedParameter p) throws Exception{
		if (p.getInternalParameterPosition() > 0){
			IQuery q = getQuery(p.getParent().getDataProviderId());
			
			
			
			try{
				IParameterMetaData pmd = q.getParameterMetaData();
				
				String s = pmd.getParameterName(p.getInternalParameterPosition());
				
				if (s == null || s.equals("")){ //$NON-NLS-1$
					s = "parameter_" + p.getInternalParameterPosition(); //$NON-NLS-1$
				}
				p.setInternalParameterName(s);
			}catch(Exception ex){
				
			}
		}
		
	}
}
