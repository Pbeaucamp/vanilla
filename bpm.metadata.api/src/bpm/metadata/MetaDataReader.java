package bpm.metadata;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bpm.metadata.digester.MetaDataDigester;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;

public class MetaDataReader {

	private static MetaData metaData;
	
	/**
	 * read the xml file containing the metadata deifnition
	 * return ths BusinessModel list
	 * @param path
	 * @throws Exception 
	 * @deprecated use read(String, String, IRepositoryConnection, boolean instead)
	 */

	public static List<IBusinessModel> read(String groupName, String path, IRepositoryApi sock) throws Exception{
		MetaDataDigester dig = new MetaDataDigester(path, new MetaDataBuilder(sock));
		metaData = dig.getModel(sock, groupName);
		
		List<IBusinessModel> l =  new ArrayList<IBusinessModel>();
		
		for(IBusinessModel m : metaData.getBusinessModels()){
			if (((BusinessModel)m).isGrantedFor(groupName)){
				l.add(m);
			}
		}
		
		return l;
	}
	
	/**
	 * 
	 * @param groupName
	 * @param path
	 * @param sock
	 * @param isLightWeightLoad : true if no connection to the Database is established (used only by FMDT api client)
	 * @return
	 * @throws Exception
	 */
	public static List<IBusinessModel> read(String groupName, String path, IRepositoryApi sock, boolean isLightWeightLoad) throws Exception{
		MetaDataDigester dig = null;
		
		if (isLightWeightLoad){
			
			dig = new MetaDataDigester(path, null);
		}
		else{
			dig = new MetaDataDigester(path,new MetaDataBuilder(sock));
		}
		metaData = dig.getModel(sock, groupName);
		
		List<IBusinessModel> l =  new ArrayList<IBusinessModel>();
		
		for(IBusinessModel m : metaData.getBusinessModels()){
			if (((BusinessModel)m).isGrantedFor(groupName)){
				l.add(m);
			}
		}
		
		return l;
	}
	

	/**
	 * read the xml file containing the metadata deifnition
	 * return ths BusinessModel list
	 * @param inpuststream
	 * @throws Exception 
	 * @deprecated use read(String, InputStream, IRepositoryConnection, boolean instead)
	 */
	public static List<IBusinessModel> read(String groupName, InputStream stream, IRepositoryApi sock) throws Exception{
		MetaDataDigester dig = new MetaDataDigester(stream, new MetaDataBuilder(sock));
		metaData = dig.getModel(sock, groupName);
		
		List<IBusinessModel> l =  new ArrayList<IBusinessModel>();
		
		for(IBusinessModel m : metaData.getBusinessModels()){
			if (((BusinessModel)m).isGrantedFor(groupName)){
				l.add(m);
			}
		}
		
		return l;
	}
	
	public static List<IBusinessModel> read(String groupName, InputStream stream, IRepositoryApi sock, boolean isLightWeightLoad) throws Exception{
		MetaDataDigester dig = new MetaDataDigester(stream, new MetaDataBuilder(sock));
		metaData = dig.getModel(sock, groupName);
		
		List<IBusinessModel> l =  new ArrayList<IBusinessModel>();
		
		for(IBusinessModel m : metaData.getBusinessModels()){
			if (((BusinessModel)m).isGrantedFor(groupName)){
				l.add(m);
			}
		}
		
		return l;
	}
	
	
	
	
	/**
	 * read the xml file containing the metadata deifnition
	 * return ths BusinessModel list
	 * @param inpuststream
	 * @throws Exception 
	 * @deprecated use read(InputStream, Collection<String>, IRepositoryConnection, boolean instead)
	 */
	public static List<IBusinessModel> read(InputStream stream, List<Group> groupName, IRepositoryApi sock) throws Exception{
		MetaDataDigester dig = new MetaDataDigester(stream, new MetaDataBuilder(sock));
		metaData = dig.getModel(sock, groupName.iterator().next().getName());
		
		List<IBusinessModel> l =  new ArrayList<IBusinessModel>();
		
		for(IBusinessModel m : metaData.getBusinessModels()){
			for(Group s : groupName){
				if (((BusinessModel)m).isGrantedFor(s.getName())){
					l.add(m);
					break;
				}
			}
			
		}
		
		return l;
	}


	
	public static List<IBusinessModel> read(InputStream stream, Collection<String> groupName, IRepositoryApi sock, boolean isLightWeightLoad) throws Exception{
		MetaDataDigester dig = new MetaDataDigester(stream, new MetaDataBuilder(sock));
		metaData = dig.getModel(sock, groupName.iterator().next());
		
		List<IBusinessModel> l =  new ArrayList<IBusinessModel>();
		
		for(IBusinessModel m : metaData.getBusinessModels()){
			for(String s : groupName){
				if (((BusinessModel)m).isGrantedFor(s)){
					l.add(m);
					break;
				}
			}
			
		}
		
		return l;
	}

}
